package com.ruoyi.workflow.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.workflow.activiti.cmd.DeleteExecutionCmd;
import com.ruoyi.workflow.activiti.cmd.DeleteTaskCmd;
import com.ruoyi.workflow.activiti.cmd.ExpressCmd;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.domain.ActFullClassParam;
import com.ruoyi.workflow.domain.vo.ActFullClassVo;
import com.ruoyi.workflow.domain.vo.MultiVo;
import com.ruoyi.workflow.domain.vo.ProcessNode;
import com.ruoyi.workflow.service.IActBusinessStatusService;
import lombok.SneakyThrows;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.activiti.engine.task.Task;
import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.workflow.common.constant.ActConstant.*;

/**
 * @program: ruoyi-vue-plus
 * @description: 工作流工具栏
 * @author: gssong
 * @created: 2021/10/03 19:31
 */
@Component
public class WorkFlowUtils {
    @Resource
    public ObjectMapper objectMapper;

    @Autowired
    private IActBusinessStatusService iActBusinessStatusService;

    @Autowired
    private TaskService taskService;

    @Resource
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;


    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private ManagementService managementService;

    @Autowired
    private  RepositoryService repositoryService;


    /**
     * @Description: bpmnModel转为xml
     * @param: jsonBytes
     * @return: byte[]
     * @Author: gssong
     * @Date: 2021/11/5
     */
    public byte[] bpmnJsonXmlBytes(byte[] jsonBytes) throws IOException {
        if (jsonBytes == null) {
            return null;
        }
        // 1. json字节码转成 BpmnModel 对象
        JsonNode jsonNode = objectMapper.readTree(jsonBytes);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(jsonNode);

        if (bpmnModel.getProcesses().size() == 0) {
            return null;
        }
        //2.将bpmnModel转为xml
        byte[] bytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        return bytes;
    }

    /**
     * 获取下一审批节点信息
     *
     * @param flowElement
     * @param nextNodes
     * @param tempNodes
     * @param taskId
     * @param gateway
     * @return: void
     * @author: gssong
     * @Date: 2022/4/11 10:31
     */
    public void getNextNodes(FlowElement flowElement, ExecutionEntityImpl executionEntity, List<ProcessNode> nextNodes, List<ProcessNode> tempNodes, String taskId, String gateway) {
        // 获取当前节点的连线信息
        List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getOutgoingFlows();
        // 当前节点的所有下一节点出口
        for (SequenceFlow sequenceFlow : outgoingFlows) {
            // 下一节点的目标元素
            ProcessNode processNode = new ProcessNode();
            ProcessNode tempNode = new ProcessNode();
            FlowElement outFlowElement = sequenceFlow.getTargetFlowElement();
            if (outFlowElement instanceof UserTask) {
                buildNode(executionEntity, nextNodes, tempNodes, taskId, gateway, sequenceFlow, processNode, tempNode, outFlowElement);
            }else if (outFlowElement instanceof ExclusiveGateway) { // 排他网关
                getNextNodes(outFlowElement, executionEntity, nextNodes, tempNodes, taskId, ActConstant.EXCLUSIVEGATEWAY);
            }else if (outFlowElement instanceof ParallelGateway) { //并行网关
                getNextNodes(outFlowElement,executionEntity, nextNodes, tempNodes, taskId, ActConstant.PARALLELGATEWAY);
            }else if(outFlowElement instanceof InclusiveGateway){ //包含网关
                getNextNodes(outFlowElement,executionEntity, nextNodes, tempNodes, taskId, ActConstant.INCLUSIVEGATEWAY);
            }else if (outFlowElement instanceof EndEvent) {
                continue;
            }else if(outFlowElement instanceof SubProcess) {
                Collection<FlowElement> flowElements = ((SubProcess) outFlowElement).getFlowElements();
                for (FlowElement element : flowElements) {
                    if (element instanceof UserTask) {
                        buildNode(executionEntity, nextNodes, tempNodes, taskId, gateway, sequenceFlow, processNode, tempNode, element);
                        break;
                    }
                }
            }else {
                throw new ServiceException("未识别出节点类型");
            }
        }
    }

    /**
     * 构建下一审批节点
     * @param executionEntity
     * @param nextNodes 下一节点信息
     * @param tempNodes 保存没有表达式的节点信息
     * @param taskId 任务id
     * @param gateway 网关
     * @param sequenceFlow  节点
     * @param processNode 下一节点的目标元素
     * @param tempNode  保存没有表达式的节点
     * @param outFlowElement 目标节点
     * @return: void
     * @author: gssong
     * @Date: 2022/4/11 10:31
     */
    private void buildNode(ExecutionEntityImpl executionEntity, List<ProcessNode> nextNodes, List<ProcessNode> tempNodes, String taskId, String gateway, SequenceFlow sequenceFlow, ProcessNode processNode, ProcessNode tempNode, FlowElement outFlowElement) {
        // 用户任务，则获取响应给前端设置办理人或者候选人
        // 判断是否为排它网关
        if (ActConstant.EXCLUSIVEGATEWAY.equals(gateway)) {
            String conditionExpression = sequenceFlow.getConditionExpression();
            //判断是否有条件
            if (StringUtils.isNotBlank(conditionExpression)) {
                ExpressCmd expressCmd = new ExpressCmd(sequenceFlow,executionEntity,conditionExpression);
                Boolean condition  = managementService.executeCommand(expressCmd);
                if (condition ) {
                    processNode.setNodeId(outFlowElement.getId());
                    processNode.setNodeName(outFlowElement.getName());
                    processNode.setNodeType(ActConstant.EXCLUSIVEGATEWAY);
                    processNode.setTaskId(taskId);
                    processNode.setExpression(true);
                    processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                    processNode.setAssignee(((UserTask) outFlowElement).getAssignee());
                    processNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
                    nextNodes.add(processNode);
                } else {
                    processNode.setNodeId(outFlowElement.getId());
                    processNode.setNodeName(outFlowElement.getName());
                    processNode.setNodeType(ActConstant.EXCLUSIVEGATEWAY);
                    processNode.setTaskId(taskId);
                    processNode.setExpression(false);
                    processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                    processNode.setAssignee(((UserTask) outFlowElement).getAssignee());
                    processNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
                    nextNodes.add(processNode);
                }
            } else {
                tempNode.setNodeId(outFlowElement.getId());
                tempNode.setNodeName(outFlowElement.getName());
                tempNode.setNodeType(ActConstant.EXCLUSIVEGATEWAY);
                tempNode.setTaskId(taskId);
                tempNode.setExpression(true);
                tempNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                tempNode.setAssignee(((UserTask) outFlowElement).getAssignee());
                tempNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
                tempNodes.add(tempNode);
            }
        // 判断是否为包含网关
        } else if(ActConstant.INCLUSIVEGATEWAY.equals(gateway)){
            String conditionExpression = sequenceFlow.getConditionExpression();
            if(StringUtils.isBlank(conditionExpression)){
                processNode.setNodeId(outFlowElement.getId());
                processNode.setNodeName(outFlowElement.getName());
                processNode.setNodeType(ActConstant.EXCLUSIVEGATEWAY);
                processNode.setTaskId(taskId);
                processNode.setExpression(true);
                processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                processNode.setAssignee(((UserTask) outFlowElement).getAssignee());
                processNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
                nextNodes.add(processNode);
            } else{
                ExpressCmd expressCmd = new ExpressCmd(sequenceFlow,executionEntity,conditionExpression);
                Boolean condition  = managementService.executeCommand(expressCmd);
                if (condition) {
                    processNode.setNodeId(outFlowElement.getId());
                    processNode.setNodeName(outFlowElement.getName());
                    processNode.setNodeType(ActConstant.EXCLUSIVEGATEWAY);
                    processNode.setTaskId(taskId);
                    processNode.setExpression(true);
                    processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                    processNode.setAssignee(((UserTask) outFlowElement).getAssignee());
                    processNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
                    nextNodes.add(processNode);
                }
            }
        } else {
            processNode.setNodeId(outFlowElement.getId());
            processNode.setNodeName(outFlowElement.getName());
            processNode.setNodeType(ActConstant.USER_TASK);
            processNode.setTaskId(taskId);
            processNode.setExpression(true);
            processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
            processNode.setAssignee(((UserTask) outFlowElement).getAssignee());
            processNode.setAssigneeId(((UserTask) outFlowElement).getAssignee());
            nextNodes.add(processNode);
        }
    }


    /**
     *
     * @param actFullClass 业务规则对象
     * @param taskId 任务id
     * @return 查询业务规则
     * @return: java.lang.Object
     * @author: gssong
     * @Date: 2022/4/11 10:31
     */
    @SneakyThrows
    public Object assignList(ActFullClassVo actFullClass, String taskId) {
        //方法名称
        String methodName = actFullClass.getMethod();
        //全类名
        String fullClass = actFullClass.getFullClass();
        List<Object> params = new ArrayList<>();

        List<ActFullClassParam> fullClassParam = actFullClass.getFullClassParam();
        for (ActFullClassParam param : fullClassParam) {
            Map<String, VariableInstance> variables = taskService.getVariableInstances(taskId);
            if (variables.containsKey(param.getParam())) {
                VariableInstance v = variables.get(param.getParam());
                String variable = v.getTextValue();
                if (param.getParamType().equals(ActConstant.PARAM_BYTE)) {
                    params.add(String.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_SHORT)) {
                    params.add(Short.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_INTEGER)) {
                    params.add(Integer.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_LONG)) {
                    params.add(Long.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_FLOAT)) {
                    params.add(Float.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_DOUBLE)) {
                    params.add(Double.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_BOOLEAN)) {
                    params.add(Boolean.valueOf(variable));
                } else if (param.getParamType().equals(ActConstant.PARAM_CHARACTER)) {
                    params.add(variable.charAt(variable.length()));
                }
            }
        }
        Class<?> aClass = Class.forName(fullClass);
        Method[] declaredMethods = aClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (methodName.equals(declaredMethod.getName())) {
                Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                Method method = aClass.getDeclaredMethod(declaredMethod.getName(), parameterTypes);
                Object newInstance = method.getDeclaringClass().newInstance();
                Object invoke = method.invoke(newInstance, params.toArray());
                return invoke;
            }
        }
        throw new ServiceException("未找到审批人员");
    }

    /**
     * @Description: 设置业务流程参数
     * @param o 对象
     * @param idList 主键集合
     * @param id 主键id
     * @Author: gssong
     * @Date: 2022/1/16
     */
    public void setStatusFileValue(Object o, List<String> idList, String id){
        Class<?> aClass = o.getClass();
        Field businessStatus = null;
        try {
            businessStatus = aClass.getDeclaredField(ACT_BUSINESSS_TATUS);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new ServiceException("未找到"+ACT_BUSINESSS_TATUS+"属性");
        }
        businessStatus.setAccessible(true);
        List<ActBusinessStatus> infoByBusinessKey = iActBusinessStatusService.getListInfoByBusinessKey(idList);
        try {
            if(CollectionUtil.isNotEmpty(infoByBusinessKey)){
                ActBusinessStatus actBusinessStatus = infoByBusinessKey.stream().filter(e -> e.getBusinessKey().equals(id)).findFirst().orElse(null);
                if(ObjectUtil.isNotEmpty(actBusinessStatus)){
                    businessStatus.set(o,actBusinessStatus);
                }else{
                    businessStatus.set(o,new ActBusinessStatus().setStatus(BusinessStatusEnum.DRAFT.getStatus()));
                }
            }else{
                businessStatus.set(o,new ActBusinessStatus().setStatus(BusinessStatusEnum.DRAFT.getStatus()));
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException("设置流程状态失败");
        }
    }

    /**
     * @Description: 设置流程实例id
     * @param o 对象
     * @param idList 主键集合
     * @param id 主键id
     * @Author: gssong
     * @Date: 2022/1/16
     */
    public void setProcessInstIdFileValue(Object o, List<String> idList, String id){
        Class<?> aClass = o.getClass();
        Field processInstanceId = null;
        try {
            processInstanceId = aClass.getDeclaredField(PROCESS_INSTANCE_ID);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new ServiceException("未找到"+PROCESS_INSTANCE_ID+"属性");
        }
        processInstanceId.setAccessible(true);
        List<ActBusinessStatus> infoByBusinessKey = iActBusinessStatusService.getListInfoByBusinessKey(idList);
        try {
            if(CollectionUtil.isNotEmpty(infoByBusinessKey)){
                ActBusinessStatus actBusinessStatus = infoByBusinessKey.stream().filter(e -> e.getBusinessKey().equals(id)).findFirst().orElse(null);
                if(ObjectUtil.isNotEmpty(actBusinessStatus)){
                    processInstanceId.set(o,actBusinessStatus.getProcessInstanceId());
                }else{
                    processInstanceId.set(o,"");
                }
            }else{
                processInstanceId.set(o,"");
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException("设置流程状态失败");
        }
    }

    /**
     * 查询审批人
     *
     * @param params
     * @param chooseWay
     * @param nodeName
     * @return: java.util.List<java.lang.Long>
     * @author: gssong
     * @Date: 2022/4/11 10:32
     */
    public List<Long> assignees(String params, String chooseWay, String nodeName) {
        List<Long> paramList = new ArrayList<>();
        String[] split = params.split(",");
        for (String userId : split) {
            paramList.add(Long.valueOf(userId));
        }
        List<SysUser> list = null;
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        // 按用户id查询
        if (WORKFLOW_PERSON.equals(chooseWay)) {
            queryWrapper.in(SysUser::getUserId, paramList);
            list = sysUserMapper.selectList(queryWrapper);
            //按角色id查询用户
        }else if (WORKFLOW_ROLE.equals(chooseWay)) {
            List<SysRole> sysRoles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleId, paramList));
            if (CollectionUtil.isNotEmpty(sysRoles)) {
                List<Long> collectRoleId = sysRoles.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
                List<SysUserRole> sysUserRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, collectRoleId));
                queryWrapper.in(SysUser::getUserId, sysUserRoles.stream().map(e -> e.getUserId()).collect(Collectors.toList()));
                list = sysUserMapper.selectList(queryWrapper);
            }
            //按部门id查询用户
        } else if (WORKFLOW_DEPT.equals(chooseWay)) {
            queryWrapper.in(SysUser::getDeptId, paramList);
            list = sysUserMapper.selectList(queryWrapper);
        }
        if (CollectionUtil.isEmpty(list)) {
            throw new ServiceException(nodeName + "环节未设置审批人");
        }
        return list.stream().map(e -> e.getUserId()).collect(Collectors.toList());
    }

    /**
     * @Description: 删除正在执行的任务
     * @param: task
     * @return: void
     * @author: gssong
     * @Date: 2022/4/11 13:36
     */

    public void deleteRuntimeTask(Task task){
        DeleteTaskCmd deleteTaskCmd = new DeleteTaskCmd(task.getId());
        managementService.executeCommand(deleteTaskCmd);
        DeleteExecutionCmd deleteExecutionCmd = new DeleteExecutionCmd(task.getExecutionId());
        managementService.executeCommand(deleteExecutionCmd);
        historyService.deleteHistoricTaskInstance(task.getId());
       /* historyService.createNativeHistoricActivityInstanceQuery()
            .sql("DELETE  FROM ACT_HI_ACTINST WHERE EXECUTION_ID_ = '" + task.getExecutionId() + "'").list();*/
    }
    /**
     * @Description: 判断当前节点是否为会签节点
     * @param: processDefinitionId 流程定义id
     * @param: taskDefinitionKey 当前节点id
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2022/4/16 13:31
     */
    public MultiVo isMultiInstance(String processDefinitionId, String taskDefinitionKey) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        FlowNode flowNode = (FlowNode)bpmnModel.getFlowElement(taskDefinitionKey);
        MultiVo multiVo = new MultiVo();
        //判断是否为并行会签节点
        if(flowNode.getBehavior()  instanceof ParallelMultiInstanceBehavior){
            ParallelMultiInstanceBehavior behavior = (ParallelMultiInstanceBehavior) flowNode.getBehavior();
            if (behavior != null && behavior.getCollectionExpression() != null) {
                Expression collectionExpression = behavior.getCollectionExpression();
                String assigneeList = collectionExpression.getExpressionText();
                String assignee = behavior.getCollectionElementVariable();
                multiVo.setType(behavior);
                multiVo.setAssignee(assignee);
                multiVo.setAssigneeList(assigneeList);
                return multiVo;
            }
            //判断是否为串行会签节点
        }else if(flowNode.getBehavior()  instanceof SequentialMultiInstanceBehavior){
            SequentialMultiInstanceBehavior behavior = (SequentialMultiInstanceBehavior) flowNode.getBehavior();
            if (behavior != null && behavior.getCollectionExpression() != null) {
                Expression collectionExpression = behavior.getCollectionExpression();
                String assigneeList = collectionExpression.getExpressionText();
                String assignee = behavior.getCollectionElementVariable();
                multiVo.setType(behavior);
                multiVo.setAssignee(assignee);
                multiVo.setAssigneeList(assigneeList);
                return multiVo;
            }
        }
        return null;
    }
}
