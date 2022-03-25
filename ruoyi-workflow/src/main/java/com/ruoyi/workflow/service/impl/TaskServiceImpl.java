package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.workflow.activiti.cmd.JumpAnyWhereCmd;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActHiTaskInst;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.ActRuExecution;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.NextNodeREQ;
import com.ruoyi.workflow.domain.bo.TaskCompleteREQ;
import com.ruoyi.workflow.domain.bo.TaskREQ;
import com.ruoyi.workflow.domain.vo.*;
import com.ruoyi.workflow.factory.WorkflowService;
import com.ruoyi.workflow.service.*;
import com.ruoyi.workflow.utils.WorkFlowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.ruoyi.common.helper.LoginHelper.getUserId;

/**
 * @program: ruoyi-vue-plus
 * @description: 任务业务层
 * @author: gssong
 * @created: 2021/10/17 14:57
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl extends WorkflowService implements ITaskService {

    private final IUserService iUserService;

    private final IActBusinessStatusService iActBusinessStatusService;

    private final WorkFlowUtils workFlowUtils;

    private final IActTaskNodeService iActTaskNodeService;

    private final IActNodeAssigneeService iActNodeAssigneeService;

    private final IActFullClassService iActFullClassService;

    private final IActHiActInstService iActHiActInstService;

    private final ManagementService managementService;

    private final IActRuExecutionService iActRuExecutionService;

    private final IActHiTaskInstService iActHiTaskInstService;



    /**
     * 查询当前用户的待办任务
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<TaskWaitingVo> getTaskWaitByPage(TaskREQ req) {
        //当前登录人
        String username = LoginHelper.getLoginUser().getUserId().toString();
        TaskQuery query = taskService.createTaskQuery()
            .taskCandidateOrAssigned(String.valueOf(username)) // 候选人或者办理人
            .orderByTaskCreateTime().asc();
        if (StringUtils.isNotEmpty(req.getTaskName())) {
            query.taskNameLikeIgnoreCase("%" + req.getTaskName() + "%");
        }
        List<Task> taskList = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        List<TaskWaitingVo> list = new ArrayList<>();
        for (Task task : taskList) {
            TaskWaitingVo taskWaitingVo = new TaskWaitingVo();
            BeanUtils.copyProperties(task, taskWaitingVo);
            taskWaitingVo.setProcessStatus(task.isSuspended() == true ? "挂起" : "激活");
            //任务办理人: 如果是候选人则没有值，办理人才有
            if (StringUtils.isNotBlank(taskWaitingVo.getAssignee())) {
                String[] split = taskWaitingVo.getAssignee().split(",");
                List<Long> userIds = new ArrayList<>();
                for (String userId : split) {
                    userIds.add(Long.valueOf(userId));
                }
                List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                if (CollectionUtil.isNotEmpty(userList)) {
                    List<String> nickNames = userList.stream().map(SysUser::getNickName).collect(Collectors.toList());
                    taskWaitingVo.setAssignee(StringUtils.join(nickNames, ","));
                    taskWaitingVo.setAssigneeId(StringUtils.join(userIds, ","));
                }

            }
            // 查询流程实例
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            //流程发起人
            String startUserId = pi.getStartUserId();
            if (StringUtils.isNotBlank(startUserId)) {
                SysUser sysUser = iUserService.selectUserById(Long.valueOf(startUserId));
                if (ObjectUtil.isNotNull(sysUser)) {
                    taskWaitingVo.setStartUserNickName(sysUser.getNickName());
                }
            }
            taskWaitingVo.setProcessDefinitionVersion(pi.getProcessDefinitionVersion());
            taskWaitingVo.setProcessDefinitionName(pi.getProcessDefinitionName());
            taskWaitingVo.setBusinessKey(pi.getBusinessKey());
            list.add(taskWaitingVo);
        }
        return new TableDataInfo(list, total);
    }

    /**
     * 完成任务
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(TaskCompleteREQ req) {
        // 1.查询任务
        Task task = taskService.createTaskQuery().taskId(req.getTaskId()).taskAssignee(getUserId().toString()).singleResult();

        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("任务不存在或您不是当前审批人");
        }

        if (task.isSuspended()) {
            throw new ServiceException("当前任务已被挂起");
        }
        //办理委托任务
        if(ObjectUtil.isNotEmpty(task.getDelegationState())&&ActConstant.PENDING.equals(task.getDelegationState().name())){
            taskService.addComment(task.getId(),task.getProcessInstanceId(),req.getMessage());
            taskService.resolveTask(req.getTaskId());
            ActHiTaskInst hiTaskInst = iActHiTaskInstService.getById(task.getId());
            TaskEntity subTask = createSubTask(task, hiTaskInst.getStartTime());
            taskService.addComment(subTask.getId(), task.getProcessInstanceId(), req.getMessage());
            taskService.complete(subTask.getId());
            ActHiTaskInst actHiTaskInst = new ActHiTaskInst();
            actHiTaskInst.setId(task.getId());
            actHiTaskInst.setStartTime(new Date());
            iActHiTaskInstService.updateById(actHiTaskInst);
            return true;
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        //判断下一节点是否是会签 如果是会签 将选择的人员放到会签变量
        List<ActNodeAssignee> actNodeAssignees = iActNodeAssigneeService.getInfoByProcessDefinitionId(task.getProcessDefinitionId());
        for (ActNodeAssignee actNodeAssignee : actNodeAssignees) {
            String column = actNodeAssignee.getMultipleColumn();
            String assigneeId = actNodeAssignee.getAssigneeId();
            if(actNodeAssignee.getMultiple()&&actNodeAssignee.getIsShow()){
                List<Long> userIdList = req.getAssignees(actNodeAssignee.getMultipleColumn());
                if(CollectionUtil.isNotEmpty(userIdList)){
                    taskService.setVariable(task.getId(),column,userIdList);
                }
            }
            //判断是否有会签并且不需要弹窗选人的节点
            if(actNodeAssignee.getMultiple()&&!actNodeAssignee.getIsShow()&&(StringUtils.isBlank(column) || StringUtils.isBlank(assigneeId))){
                throw new ServiceException("请检查【"+processInstance.getProcessDefinitionKey()+"】配置 ");
            }
            if(actNodeAssignee.getMultiple()&&!actNodeAssignee.getIsShow()){
                List<Long> userIds = new ArrayList<>();
                String[] split = assigneeId.split(",");
                for (String userId : split) {
                    userIds.add(Long.valueOf(userId));
                }
                taskService.setVariable(task.getId(),actNodeAssignee.getMultipleColumn(),userIds);
            }
        }
        // 3. 指定任务审批意见
        taskService.addComment(req.getTaskId(), task.getProcessInstanceId(), req.getMessage());
        // 4. 完成任务
        taskService.setVariables(req.getTaskId(), req.getVariables());
        taskService.complete(req.getTaskId());
        // 5. 记录执行过的流程任务
        List<ActTaskNode> actTaskNodeList = iActTaskNodeService.getListByInstanceId(task.getProcessInstanceId());
        List<String> nodeIdList = actTaskNodeList.stream().map(ActTaskNode::getNodeId).collect(Collectors.toList());
        if (!nodeIdList.contains(task.getTaskDefinitionKey())) {
            ActTaskNode actTaskNode = new ActTaskNode();
            actTaskNode.setNodeId(task.getTaskDefinitionKey());
            actTaskNode.setNodeName(task.getName());
            actTaskNode.setInstanceId(task.getProcessInstanceId());
            if (CollectionUtil.isEmpty(actTaskNodeList)) {
                actTaskNode.setOrderNo(0);
                actTaskNode.setIsBack(true);
            } else {
                ActNodeAssignee actNodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(task.getTaskDefinitionKey())).findFirst().orElse(null);
                //如果为设置流程定义配置默认 当前环节可以回退
                if(ObjectUtil.isEmpty(actNodeAssignee)){
                    actTaskNode.setIsBack(true);
                    actTaskNode.setOrderNo(actTaskNodeList.get(0).getOrderNo() + 1);
                }else{
                    actTaskNode.setIsBack(actNodeAssignee.getIsBack());
                    actTaskNode.setOrderNo(actTaskNodeList.get(0).getOrderNo() + 1);
                }
            }
            iActTaskNodeService.save(actTaskNode);
        }
        // 更新业务状态为：办理中, 和流程实例id
        iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.WAITING, task.getProcessInstanceId());
        // 6. 查询下一个任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        // 7. 如果为空 办结任务
        if (CollectionUtil.isEmpty(taskList)) {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            // 更新业务状态已完成 办结流程
            boolean b = iActBusinessStatusService.updateState(hpi.getBusinessKey(), BusinessStatusEnum.FINISH);
            return b;
        }

        // 8. 如果不为空 指定办理人
        if (CollectionUtil.isNotEmpty(taskList)) {
            // 9. 指定办理人
            for (Task t : taskList) {
                ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> t.getTaskDefinitionKey().equals(e.getNodeId())).findFirst().orElse(null);
                if(ObjectUtil.isNotNull(nodeAssignee)){
                    // 不需要弹窗选人
                    if(!nodeAssignee.getIsShow()){
                        //业务规则选人
                        if(ActConstant.WORKFLOW_RULE.equals(nodeAssignee.getChooseWay())){
                            ActFullClassVo actFullClassVo = iActFullClassService.queryById(nodeAssignee.getFullClassId());
                            //按业务规则选人
                            Object assignee = workFlowUtils.assignList(actFullClassVo, t.getId());
                            List<Long> userIds = new ArrayList<>();
                            String[] splitUserIds = assignee.toString().split(",");
                            for (String userId : splitUserIds) {
                                userIds.add(Long.valueOf(userId));
                            }
                            List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                            if (CollectionUtil.isEmpty(userList)) {
                                throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                            }
                            settingAssignee(t,userList.stream().map(SysUser::getUserId).collect(Collectors.toList()));
                        }else{
                            // 设置审批人员
                            List<Long> assignees = workFlowUtils.assignees(nodeAssignee.getAssigneeId(), nodeAssignee.getChooseWay(), t.getName());
                            settingAssignee(t,assignees);
                        }
                    }else{
                        //弹窗选人 根据当前任务节点id获取办理人
                        if(t.getTaskDefinitionKey().equals(nodeAssignee.getNodeId()) && nodeAssignee.getIsShow()){
                            List<Long> assignees = req.getAssignees(t.getTaskDefinitionKey());
                            //设置选人
                            if (CollectionUtil.isNotEmpty(assignees)) {
                                settingAssignee(t,assignees);
                            } else if (StringUtils.isBlank(t.getAssignee())) {
                                if(taskList.size()==1){
                                    throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                                }
                            }
                        }else{
                            //校验会签环节
                            ActNodeAssignee info = iActNodeAssigneeService.getInfo(t.getProcessDefinitionId(), t.getTaskDefinitionKey());
                            if(ObjectUtil.isNotNull(info)&&!info.getMultiple()){
                                throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                            }else{
                                List<ActNodeAssignee> list = iActNodeAssigneeService.getInfoByProcessDefinitionId(task.getProcessDefinitionId());
                                for (ActNodeAssignee actNodeAssignee : list) {
                                    if(actNodeAssignee.getMultiple()&&StringUtils.isNotBlank(actNodeAssignee.getMultipleColumn())){
                                        Object variable = runtimeService.getVariable(t.getExecutionId(), actNodeAssignee.getMultipleColumn());
                                        if(ObjectUtil.isEmpty(variable)){
                                            throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                                        }
                                    }
                                    if(actNodeAssignee.getMultiple()&&StringUtils.isBlank(actNodeAssignee.getMultipleColumn())){
                                        throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                                    }
                                }
                            }
                        }
                    }
                } else if (StringUtils.isBlank(t.getAssignee())) {
                    throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                }
            }
        }
        return true;
    }

    /**
     * 设置下一环节人员
     * @param task
     * @param assignees
     */
    public void settingAssignee(Task task,List<Long> assignees){
        if (assignees.size() == 1) {
            taskService.setAssignee(task.getId(), assignees.get(0).toString());
        } else {
            // 多个作为候选人
            for (Long assignee : assignees) {
                taskService.addCandidateUser(task.getId(), assignee.toString());
            }
        }
    }

    /**
     * 查询当前用户的已办任务
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<TaskFinishVo> getTaskFinishByPage(TaskREQ req) {
        //当前登录人
        String username = LoginHelper.getUserId().toString();
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
            .taskAssignee(username).finished().orderByHistoricTaskInstanceStartTime().asc();
        if (StringUtils.isNotBlank(req.getTaskName())) {
            query.taskNameLike(req.getTaskName());
        }
        List<HistoricTaskInstance> list = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        List<TaskFinishVo> taskFinishVoList = new ArrayList<>();
        for (HistoricTaskInstance hti : list) {
            TaskFinishVo taskFinishVo = new TaskFinishVo();
            BeanUtils.copyProperties(hti, taskFinishVo);
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(hti.getProcessDefinitionId()).singleResult();
            taskFinishVo.setProcessDefinitionName(processDefinition.getName());
            taskFinishVo.setProcessDefinitionKey(processDefinition.getKey());
            taskFinishVo.setVersion(processDefinition.getVersion());
            if (StringUtils.isNotBlank(hti.getAssignee())) {
                String[] split = hti.getAssignee().split(",");
                List<Long> userIds = new ArrayList<>();
                for (String userId : split) {
                    userIds.add(Long.valueOf(userId));
                }
                List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                if (CollectionUtil.isNotEmpty(userList)) {
                    List<String> nickNames = userList.stream().map(SysUser::getNickName).collect(Collectors.toList());
                    taskFinishVo.setAssignee(StringUtils.join(nickNames, ","));
                    taskFinishVo.setAssigneeId(StringUtils.join(userIds, ","));
                }

            }
            taskFinishVoList.add(taskFinishVo);
        }
        return new TableDataInfo(taskFinishVoList, total);
    }

    /**
     * 获取目标节点（下一个节点）
     * @param req
     * @return
     */
    @Override
    public List<ProcessNode> getNextNodeInfo(NextNodeREQ req) {
        //设置变量
        TaskEntity task = (TaskEntity)taskService.createTaskQuery().taskId(req.getTaskId()).singleResult();
        //委托流程
        if(ObjectUtil.isNotEmpty(task.getDelegationState())&&ActConstant.PENDING.equals(task.getDelegationState().name())){
            return null;
        }
        //查询任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        //如果是会签最后一个人员审批选人
        if(CollectionUtil.isNotEmpty(taskList)&&taskList.size()>1){
            //return null;
        }
        taskService.setVariables(task.getId(),req.getVariables());
        //流程定义
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        //业务id
        String businessKey = processInstance.getBusinessKey();
        //查询bpmn信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //通过任务节点id，来获取当前节点信息
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        // 封装下一个用户任务节点信息
        List<ProcessNode> nextNodes = new ArrayList<>();
        //  保存没有表达式的节点
        List<ProcessNode> tempNodes = new ArrayList<>();
        ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId()).singleResult();
        workFlowUtils.getNextNodes(flowElement,executionEntity, nextNodes, tempNodes, task.getId(), businessKey, null);

        //排它网关  如果下已审批节点变量判断都为false  将保存的临时的节点赋予下一节点
        List<String> exclusiveLists = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVEGATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
            map(ProcessNode::getNodeType).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(nextNodes) && CollectionUtil.isEmpty(exclusiveLists)) {
            nextNodes.addAll(tempNodes);
        }
        // 返回前端
        List<ProcessNode> nodeList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(nextNodes)) {
            //排它网关  如果下已审批节点变量判断都为false  将保存的临时的节点赋予下一节点
            List<String> exclusiveList = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVEGATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
                map(ProcessNode::getNodeType).collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(nextNodes) && CollectionUtil.isEmpty(exclusiveList)) {
                nextNodes.addAll(tempNodes);
            }
            //排它网关
            List<String> exclusiveGatewayList = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVEGATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
                map(ProcessNode::getNodeType).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(exclusiveGatewayList)) {
                nextNodes.forEach(node -> {
                    if ((ActConstant.EXCLUSIVEGATEWAY.equals(node.getNodeType()) && node.getExpression())) {
                        nodeList.add(node);
                    }
                });
                //设置节点审批人员
                List<ProcessNode> processNodeList = getProcessNodeAssigneeList(nodeList, task.getProcessDefinitionId());
                return processNodeList;
            } else {
                //设置节点审批人员
                List<ProcessNode> processNodeList = getProcessNodeAssigneeList(nextNodes, task.getProcessDefinitionId());
                return processNodeList;
            }
        }
        return nextNodes;
    }

    /**
     * 设置节点审批人员
     * @param nodeList
     * @param definitionId
     * @return
     */
    private List<ProcessNode> getProcessNodeAssigneeList(List<ProcessNode> nodeList, String definitionId) {
        List<ActNodeAssignee> actNodeAssignees = iActNodeAssigneeService.getInfoByProcessDefinitionId(definitionId);
        if (CollectionUtil.isNotEmpty(actNodeAssignees)) {
            for (ProcessNode processNode : nodeList) {
                //校验画流程模型该环节是否放置审批人员
                if (StringUtils.isBlank(processNode.getAssignee())) {
                    if(CollectionUtil.isEmpty(actNodeAssignees)){
                        throw new ServiceException("该流程定义未配置，请联系管理员！");
                    }
                    ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(processNode.getNodeId())).findFirst().orElse(null);

                    //按角色 部门 人员id 等设置查询人员信息
                    if (ObjectUtil.isNotNull(nodeAssignee) && StringUtils.isNotBlank(nodeAssignee.getAssigneeId())
                        && nodeAssignee.getFullClassId() == null && StringUtils.isNotBlank(nodeAssignee.getAssignee())) {
                        processNode.setChooseWay(nodeAssignee.getChooseWay());
                        processNode.setAssignee(nodeAssignee.getAssignee());
                        processNode.setAssigneeId(nodeAssignee.getAssigneeId());
                        processNode.setIsShow(nodeAssignee.getIsShow());
                        if(nodeAssignee.getMultiple()){
                            processNode.setNodeId(nodeAssignee.getMultipleColumn());
                        }
                        processNode.setMultiple(nodeAssignee.getMultiple());
                        processNode.setMultipleColumn(nodeAssignee.getMultipleColumn());
                        //按照业务规则设置查询人员信息
                    } else if (ObjectUtil.isNotNull(nodeAssignee) && nodeAssignee.getFullClassId() != null) {
                        ActFullClassVo actFullClassVo = iActFullClassService.queryById(nodeAssignee.getFullClassId());
                        Object assignee = workFlowUtils.assignList(actFullClassVo, processNode.getTaskId());
                        processNode.setChooseWay(nodeAssignee.getChooseWay());
                        processNode.setAssignee("");
                        processNode.setAssigneeId(assignee.toString());
                        processNode.setIsShow(nodeAssignee.getIsShow());
                        if(nodeAssignee.getMultiple()){
                            processNode.setNodeId(nodeAssignee.getMultipleColumn());
                        }
                        processNode.setMultiple(nodeAssignee.getMultiple());
                        processNode.setMultipleColumn(nodeAssignee.getMultipleColumn());
                    }else{
                        throw new ServiceException(processNode.getNodeName() + "未配置审批人，请联系管理员！");
                    }
                } else {
                    ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(processNode.getNodeId())).findFirst().orElse(null);
                    if(ObjectUtil.isNotEmpty(nodeAssignee)){
                        processNode.setChooseWay(nodeAssignee.getChooseWay());
                        processNode.setAssignee(nodeAssignee.getAssignee());
                        processNode.setAssigneeId(nodeAssignee.getAssigneeId());
                        processNode.setIsShow(nodeAssignee.getIsShow());
                        if(nodeAssignee.getMultiple()){
                            processNode.setNodeId(nodeAssignee.getMultipleColumn());
                        }
                        processNode.setMultiple(nodeAssignee.getMultiple());
                        processNode.setMultipleColumn(nodeAssignee.getMultipleColumn());
                    }else{
                        processNode.setChooseWay(ActConstant.WORKFLOW_ASSIGNEE);
                    }
                }
            }
        }
        if (CollectionUtil.isNotEmpty(nodeList)) {
            Iterator<ProcessNode> iterator = nodeList.iterator();
            while (iterator.hasNext()) {
                ProcessNode node = iterator.next();
                // 去除画流程时设置的选人节点  不需要弹窗 选人
                // 去除不需要弹窗选人的节点
                if (ActConstant.WORKFLOW_ASSIGNEE.equals(node.getChooseWay())||!node.getIsShow()) {
                    iterator.remove();
                }
            }
        }
        return nodeList;
    }

    /**
     * 查询所有用户的已办任务
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<TaskFinishVo> getAllTaskFinishByPage(TaskREQ req) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
            .finished().orderByHistoricTaskInstanceStartTime().asc();
        if (StringUtils.isNotBlank(req.getTaskName())) {
            query.taskNameLike(req.getTaskName());
        }
        List<HistoricTaskInstance> list = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        List<TaskFinishVo> taskFinishVoList = new ArrayList<>();
        for (HistoricTaskInstance hti : list) {
            TaskFinishVo taskFinishVo = new TaskFinishVo();
            BeanUtils.copyProperties(hti, taskFinishVo);
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(hti.getProcessDefinitionId()).singleResult();
            taskFinishVo.setProcessDefinitionName(processDefinition.getName());
            taskFinishVo.setProcessDefinitionKey(processDefinition.getKey());
            taskFinishVo.setVersion(processDefinition.getVersion());
            if (StringUtils.isNotBlank(hti.getAssignee())) {
                String[] split = hti.getAssignee().split(",");
                List<Long> userIds = new ArrayList<>();
                for (String userId : split) {
                    userIds.add(Long.valueOf(userId));
                }
                List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                if (CollectionUtil.isNotEmpty(userList)) {
                    List<String> nickNames = userList.stream().map(SysUser::getNickName).collect(Collectors.toList());
                    taskFinishVo.setAssignee(StringUtils.join(nickNames, ","));
                    taskFinishVo.setAssigneeId(StringUtils.join(userIds, ","));
                }

            }
            taskFinishVoList.add(taskFinishVo);
        }
        return new TableDataInfo(taskFinishVoList, total);
    }

    /**
     * 查询所有用户的待办任务
     * @param req
     * @return
     */
    @Override
    public TableDataInfo<TaskWaitingVo> getAllTaskWaitByPage(TaskREQ req) {
        TaskQuery query = taskService.createTaskQuery()
            .orderByTaskCreateTime().asc();
        if (StringUtils.isNotEmpty(req.getTaskName())) {
            query.taskNameLikeIgnoreCase("%" + req.getTaskName() + "%");
        }
        List<Task> taskList = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        List<TaskWaitingVo> list = new ArrayList<>();
        for (Task task : taskList) {
            TaskWaitingVo taskWaitingVo = new TaskWaitingVo();
            BeanUtils.copyProperties(task, taskWaitingVo);
            taskWaitingVo.setProcessStatus(task.isSuspended() == true ? "挂起" : "激活");
            //任务办理人: 如果是候选人则没有值，办理人才有
            if (StringUtils.isNotBlank(taskWaitingVo.getAssignee())) {
                String[] split = taskWaitingVo.getAssignee().split(",");
                List<Long> userIds = new ArrayList<>();
                for (String userId : split) {
                    userIds.add(Long.valueOf(userId));
                }
                List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                if (CollectionUtil.isNotEmpty(userList)) {
                    List<String> nickNames = userList.stream().map(SysUser::getNickName).collect(Collectors.toList());
                    taskWaitingVo.setAssignee(StringUtils.join(nickNames, ","));
                    taskWaitingVo.setAssigneeId(StringUtils.join(userIds, ","));
                }

            }
            // 查询流程实例
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            //流程发起人
            String startUserId = pi.getStartUserId();
            if (StringUtils.isNotBlank(startUserId)) {
                SysUser sysUser = iUserService.selectUserById(Long.valueOf(startUserId));
                if (ObjectUtil.isNotNull(sysUser)) {
                    taskWaitingVo.setStartUserNickName(sysUser.getNickName());
                }
            }
            taskWaitingVo.setProcessDefinitionVersion(pi.getProcessDefinitionVersion());
            taskWaitingVo.setProcessDefinitionName(pi.getProcessDefinitionName());
            taskWaitingVo.setBusinessKey(pi.getBusinessKey());
            list.add(taskWaitingVo);
        }
        return new TableDataInfo(list, total);
    }

    /**
     * 驳回审批
     * @param backProcessVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String backProcess(BackProcessVo backProcessVo) {

        Task task = taskService.createTaskQuery().taskId(backProcessVo.getTaskId()).taskAssignee(getUserId().toString()).singleResult();
        if (task.isSuspended()) {
            throw new ServiceException("当前任务已被挂起");
        }
        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("当前任务不存在或你不是任务办理人");
        }
        //流程实例id
        String processInstanceId = task.getProcessInstanceId();
        // 1. 获取流程模型实例 BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 2.当前节点信息
        FlowNode curFlowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        // 3.获取当前节点原出口连线
        List<SequenceFlow> sequenceFlowList = curFlowNode.getOutgoingFlows();
        // 4. 临时存储当前节点的原出口连线
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
        oriSequenceFlows.addAll(sequenceFlowList);
        // 5. 将当前节点的原出口清空
        sequenceFlowList.clear();
        // 6. 获取目标节点信息
        FlowNode targetFlowNode = (FlowNode) bpmnModel.getFlowElement(backProcessVo.getTargetActivityId());
        // 7. 获取目标节点的入口连线
        List<SequenceFlow> incomingFlows = targetFlowNode.getIncomingFlows();
        // 8. 存储所有目标出口
        List<SequenceFlow> targetSequenceFlow = new ArrayList<>();
        for (SequenceFlow incomingFlow : incomingFlows) {
            // 找到入口连线的源头（获取目标节点的父节点）
            FlowNode source = (FlowNode) incomingFlow.getSourceFlowElement();
            List<SequenceFlow> sequenceFlows;
            if (source instanceof ParallelGateway) {
                // 并行网关: 获取目标节点的父节点（并行网关）的所有出口，
                sequenceFlows = source.getOutgoingFlows();
            } else {
                // 其他类型父节点, 则获取目标节点的入口连续
                sequenceFlows = targetFlowNode.getIncomingFlows();
            }
            targetSequenceFlow.addAll(sequenceFlows);
        }
        // 9. 将当前节点的出口设置为新节点
        curFlowNode.setOutgoingFlows(targetSequenceFlow);
        // 10. 完成当前任务，流程就会流向目标节点创建新目标任务
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for (Task t : list) {
            if (backProcessVo.getTaskId().equals(t.getId())) {
                // 当前任务，完成当前任务
                taskService.addComment(t.getId(), processInstanceId, StringUtils.isNotBlank(backProcessVo.getComment()) ? backProcessVo.getComment() : "驳回");
                // 完成任务，就会进行驳回到目标节点，产生目标节点的任务数据
                taskService.complete(backProcessVo.getTaskId());
            } else {
                taskService.complete(t.getId());
                historyService.deleteHistoricTaskInstance(t.getId());
                iActHiActInstService.deleteActHiActInstByActId(t.getTaskDefinitionKey());
            }
        }
        // 11. 完成驳回功能后，将当前节点的原出口方向进行恢复
        curFlowNode.setOutgoingFlows(oriSequenceFlows);
       // 判断是否会签
        LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActNodeAssignee::getNodeId,backProcessVo.getTargetActivityId());
        wrapper.eq(ActNodeAssignee::getProcessDefinitionId,task.getProcessDefinitionId());
        ActNodeAssignee actNodeAssignee = iActNodeAssigneeService.getOne(wrapper);
        List<Task> newTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if(ObjectUtil.isNotEmpty(actNodeAssignee)&&!actNodeAssignee.getMultiple()){
            for (Task newTask : newTaskList) {
                // 取之前的历史办理人
                HistoricTaskInstance oldTargerTask = historyService.createHistoricTaskInstanceQuery()
                    .taskDefinitionKey(newTask.getTaskDefinitionKey()) // 节点id
                    .processInstanceId(processInstanceId)
                    .finished() // 已经完成才是历史
                    .orderByTaskCreateTime().desc() // 最新办理的在最前面
                    .list().get(0);
                taskService.setAssignee(newTask.getId(), oldTargerTask.getAssignee());
            }
        }

        // 13. 删除驳回后的流程节点
        ActTaskNode actTaskNode = iActTaskNodeService.getListByInstanceIdAndNodeId(task.getProcessInstanceId(), backProcessVo.getTargetActivityId());
        if (ObjectUtil.isNotNull(actTaskNode) && actTaskNode.getOrderNo() == 0) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            List<Task> newList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            for (Task ta : newList) {
                Map<String, Object> variables = new HashMap<>();
                variables.put("status", BusinessStatusEnum.BACK.getStatus());
                taskService.setVariables(ta.getId(), variables);
            }
            iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.BACK);
        }
        iActTaskNodeService.deleteBackTaskNode(processInstanceId, backProcessVo.getTargetActivityId());
        //删除未执行的流程执行实例
        if(ObjectUtil.isNotEmpty(actNodeAssignee)&&!actNodeAssignee.getMultiple()){
            List<ActRuExecution> actRuExecutions = iActRuExecutionService.selectRuExecutionByProcInstId(processInstanceId);
            for (ActRuExecution actRuExecution : actRuExecutions) {
                if(StringUtils.isNotBlank(actRuExecution.getActId())&&actRuExecution.getIsActive()==0){
                    iActRuExecutionService.deleteWithValidByIds(Arrays.asList(actRuExecution.getId()),false);
                }
            }
        }
        return processInstanceId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean backProcess2(BackProcessVo backProcessVo) {
        String taskId = backProcessVo.getTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        JumpAnyWhereCmd jumpAnyWhereCmd = new JumpAnyWhereCmd
            (backProcessVo.getTaskId(),backProcessVo.getTargetActivityId(),repositoryService);
        managementService.executeCommand(jumpAnyWhereCmd);
        /*String processInstanceId = task.getProcessInstanceId();
        //当前节点id
        String currentActivityId = task.getTaskDefinitionKey();
        //驳回的目标节点id
        String targetActivityId = backProcessVo.getTargetActivityId();
        //获取模型实体
        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //获取当前节点
        FlowElement currentFlowElement = bpmnModel.getFlowElement(currentActivityId);

        //获取当前节点
        FlowElement targetFlowElement = bpmnModel.getFlowElement(targetActivityId);
        //创建连线
        SequenceFlow newSequenceFlow  = new SequenceFlow();
        String id = IdUtil.getSnowflake().nextIdStr();
        newSequenceFlow .setId(id);
        newSequenceFlow.setSourceFlowElement(currentFlowElement);
        newSequenceFlow.setTargetFlowElement(targetFlowElement);
        //设置条件
        newSequenceFlow.setConditionExpression("${\"+id+\"==\"" + id + "\"}");
        bpmnModel.getMainProcess().addFlowElement(newSequenceFlow);
        //提交
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回申请");
        //完成任务
        taskService.complete(task.getId());
        //删除连线
        bpmnModel.getMainProcess().removeFlowElement(id);

        List<Task> newTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        for (Task newTask : newTaskList) {
            HistoricTaskInstance singleResult = historyService.createHistoricTaskInstanceQuery().taskId(backProcessVo.getTaskId()).singleResult();
            taskService.setAssignee(newTask.getId(), singleResult.getAssignee());
        }*/
        // 13. 删除驳回后的流程节点
        ActTaskNode actTaskNode = iActTaskNodeService.getListByInstanceIdAndNodeId(task.getProcessInstanceId(), backProcessVo.getTargetActivityId());
        if(ObjectUtil.isNotNull(actTaskNode)&&actTaskNode.getOrderNo()==0){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            List<Task> newList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            for (Task t : newList) {
                Map<String, Object> variables =new HashMap<>();
                variables.put("status",BusinessStatusEnum.BACK.getStatus());
                taskService.setVariables(t.getId(),variables);
            }
            iActBusinessStatusService.updateState(processInstance.getBusinessKey(),BusinessStatusEnum.BACK);
        }
        Boolean taskNode = iActTaskNodeService.deleteBackTaskNode(processInstanceId, backProcessVo.getTargetActivityId());
        return taskNode;
    }

    /**
     * 获取历史任务节点，用于驳回功能
     * @param processInstId
     * @return
     */
    @Override
    public List<ActTaskNode> getBackNodes(String processInstId) {
        List<ActTaskNode> list = iActTaskNodeService.getListByInstanceId(processInstId);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delegateTask(TaskREQ taskREQ) {
        if(StringUtils.isBlank(taskREQ.getDelegateUserId())){
            throw new ServiceException("请选择委托人");
        }
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskREQ.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            throw new ServiceException("当前任务不存在或你不是任务办理人");
        }
        try{
            TaskEntity subTask = this.createSubTask(task,new Date());
            taskService.addComment(subTask.getId(), task.getProcessInstanceId(),"【"+LoginHelper.getUsername()+"】委派给【"+taskREQ.getDelegateUserName()+"】");
            //委托任务
            taskService.delegateTask(taskREQ.getTaskId(), taskREQ.getDelegateUserId());
            //办理生成的任务记录
            taskService.complete(subTask.getId());
            ActHiTaskInst actHiTaskInst = new ActHiTaskInst();
            actHiTaskInst.setId(task.getId());
            actHiTaskInst.setStartTime(new Date());
            iActHiTaskInstService.updateById(actHiTaskInst);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> transmitTask(TaskREQ taskREQ) {
        Task task = taskService.createTaskQuery().taskId(taskREQ.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            return R.fail("当前任务不存在或你不是任务办理人");
        }
        try {
            TaskEntity subTask = createSubTask(task, new Date());
            taskService.addComment(subTask.getId(), task.getProcessInstanceId(),
                StringUtils.isNotBlank(taskREQ.getComment())?taskREQ.getComment():LoginHelper.getUsername()+"转办了任务");
            taskService.complete(subTask.getId());
            taskService.setAssignee(task.getId(),taskREQ.getTransmitUserId());
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }

    /**
     * 创建流程任务
     * @param parentTask
     * @param createTime
     * @return
     */
    private TaskEntity createSubTask(Task parentTask,Date createTime){
        TaskEntity task = null;
        if(ObjectUtil.isNotEmpty(parentTask)){
            task = (TaskEntity) taskService.newTask();
            task.setCategory(parentTask.getCategory());
            task.setDescription(parentTask.getDescription());
            task.setTenantId(parentTask.getTenantId());
            task.setAssignee(parentTask.getAssignee());
            task.setName(parentTask.getName());
            task.setProcessDefinitionId(parentTask.getProcessDefinitionId());
            task.setProcessInstanceId(parentTask.getProcessInstanceId());
            task.setTaskDefinitionKey(parentTask.getTaskDefinitionKey());
            task.setPriority(parentTask.getPriority());
            task.setCreateTime(createTime);
            taskService.saveTask(task);
        }
        if(ObjectUtil.isNotNull(task)){
            ActHiTaskInst hiTaskInst = iActHiTaskInstService.getById(task.getId());
            if(ObjectUtil.isNotEmpty(hiTaskInst)){
                hiTaskInst.setProcDefId(task.getProcessDefinitionId());
                hiTaskInst.setProcInstId(task.getProcessInstanceId());
                hiTaskInst.setTaskDefKey(task.getTaskDefinitionKey());
                iActHiTaskInstService.updateById(hiTaskInst);
            }
        }
        return  task;
    }

}
