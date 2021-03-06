package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.workflow.activiti.cmd.*;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.domain.ActHiTaskInst;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.*;
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
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
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
 * @description: ???????????????
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

    private final IActHiTaskInstService iActHiTaskInstService;

    private final ManagementService managementService;




    /**
     * @Description: ?????????????????????????????????
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskWaitingVo>
     * @author: gssong
     * @Date: 2021/10/17
     */
    @Override
    public TableDataInfo<TaskWaitingVo> getTaskWaitByPage(TaskREQ req) {
        //???????????????
        String username = LoginHelper.getLoginUser().getUserId().toString();
        TaskQuery query = taskService.createTaskQuery()
            .taskCandidateOrAssigned(String.valueOf(username)) // ????????????????????????
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
            taskWaitingVo.setProcessStatus(task.isSuspended() == true ? "??????" : "??????");
            //???????????????: ????????????????????????????????????????????????
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
            // ??????????????????
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            //???????????????
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
        if(CollectionUtil.isNotEmpty(list)){
            List<String> businessKeyList = list.stream().map(TaskWaitingVo::getBusinessKey).collect(Collectors.toList());
            List<ActBusinessStatus> infoList = iActBusinessStatusService.getListInfoByBusinessKey(businessKeyList);
            if(CollectionUtil.isNotEmpty(infoList)){
                list.forEach(e->{
                    ActBusinessStatus businessStatus = infoList.stream().filter(t -> t.getBusinessKey().equals(e.getBusinessKey())).findFirst().orElse(null);
                    if(ObjectUtil.isNotEmpty(businessStatus)){
                        e.setActBusinessStatus(businessStatus);
                    }
                });
            }
        }
        return new TableDataInfo(list, total);
    }


    /**
     * @Description: ????????????
     * @param: req
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2021/10/21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(TaskCompleteREQ req) {
        // 1.????????????
        Task task = taskService.createTaskQuery().taskId(req.getTaskId()).taskAssignee(getUserId().toString()).singleResult();

        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("??????????????????????????????????????????");
        }

        if (task.isSuspended()) {
            throw new ServiceException("????????????????????????");
        }
        //??????????????????
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
        //????????????????????????????????? ??????????????? ????????????????????????????????????
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
            //?????????????????????????????????????????????????????????
            if(actNodeAssignee.getMultiple()&&!actNodeAssignee.getIsShow()&&(StringUtils.isBlank(column) || StringUtils.isBlank(assigneeId))){
                throw new ServiceException("????????????"+processInstance.getProcessDefinitionKey()+"????????? ");
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
        // 3. ????????????????????????
        taskService.addComment(req.getTaskId(), task.getProcessInstanceId(), req.getMessage());
        // 4. ????????????
        taskService.setVariables(req.getTaskId(), req.getVariables());
        taskService.complete(req.getTaskId());
        // 5. ??????????????????????????????
        List<ActTaskNode> actTaskNodeList = iActTaskNodeService.getListByInstanceId(task.getProcessInstanceId());
        ActTaskNode actTaskNode = new ActTaskNode();
        actTaskNode.setNodeId(task.getTaskDefinitionKey());
        actTaskNode.setNodeName(task.getName());
        actTaskNode.setInstanceId(task.getProcessInstanceId());
        if (CollectionUtil.isEmpty(actTaskNodeList)) {
            actTaskNode.setOrderNo(0);
            actTaskNode.setIsBack(true);
            iActTaskNodeService.save(actTaskNode);
        } else {
            ActNodeAssignee actNodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(task.getTaskDefinitionKey())).findFirst().orElse(null);
            //??????????????????????????????????????? ????????????????????????
            if(ObjectUtil.isEmpty(actNodeAssignee)){
                actTaskNode.setIsBack(true);
            }else{
                actTaskNode.setIsBack(actNodeAssignee.getIsBack());
            }
            iActTaskNodeService.saveTaskNode(actTaskNode);
        }
        // ?????????????????????????????????, ???????????????id
        iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.WAITING, task.getProcessInstanceId());
        // 6. ?????????????????????
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        // 7. ???????????? ????????????
        if (CollectionUtil.isEmpty(taskList)) {
            HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            // ??????????????????????????? ????????????
            boolean b = iActBusinessStatusService.updateState(hpi.getBusinessKey(), BusinessStatusEnum.FINISH);
            return b;
        }

        // 8. ??????????????? ???????????????
        if (CollectionUtil.isNotEmpty(taskList)) {
            // 9. ???????????????
            for (Task t : taskList) {
                ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> t.getTaskDefinitionKey().equals(e.getNodeId())).findFirst().orElse(null);
                if(ObjectUtil.isNotNull(nodeAssignee)){
                    // ?????????????????????
                    if(!nodeAssignee.getIsShow()){
                        //??????????????????
                        if(ActConstant.WORKFLOW_RULE.equals(nodeAssignee.getChooseWay())){
                            ActFullClassVo actFullClassVo = iActFullClassService.queryById(nodeAssignee.getFullClassId());
                            //?????????????????????
                            Object assignee = workFlowUtils.assignList(actFullClassVo, t.getId());
                            List<Long> userIds = new ArrayList<>();
                            String[] splitUserIds = assignee.toString().split(",");
                            for (String userId : splitUserIds) {
                                userIds.add(Long.valueOf(userId));
                            }
                            List<SysUser> userList = iUserService.selectListUserByIds(userIds);
                            if (CollectionUtil.isEmpty(userList)) {
                                throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                            }
                            settingAssignee(t,userList.stream().map(SysUser::getUserId).collect(Collectors.toList()));
                        }else{
                            // ??????????????????
                            List<Long> assignees = workFlowUtils.assignees(nodeAssignee.getAssigneeId(), nodeAssignee.getChooseWay(), t.getName());
                            settingAssignee(t,assignees);
                        }
                    }else{
                        //???????????? ????????????????????????id???????????????
                        if(t.getTaskDefinitionKey().equals(nodeAssignee.getNodeId()) && nodeAssignee.getIsShow()){
                            List<Long> assignees = req.getAssignees(t.getTaskDefinitionKey());
                            //????????????
                            if (CollectionUtil.isNotEmpty(assignees)) {
                                settingAssignee(t,assignees);
                            } else if (StringUtils.isBlank(t.getAssignee())) {
                                if(taskList.size()==1){
                                    throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                                }
                            }
                        }else{
                            //??????????????????
                            ActNodeAssignee info = iActNodeAssigneeService.getInfo(t.getProcessDefinitionId(), t.getTaskDefinitionKey());
                            if(ObjectUtil.isNotNull(info)&&!info.getMultiple()){
                                throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                            }else{
                                List<ActNodeAssignee> list = iActNodeAssigneeService.getInfoByProcessDefinitionId(task.getProcessDefinitionId());
                                for (ActNodeAssignee actNodeAssignee : list) {
                                    if(actNodeAssignee.getMultiple()&&StringUtils.isNotBlank(actNodeAssignee.getMultipleColumn())){
                                        Object variable = runtimeService.getVariable(t.getExecutionId(), actNodeAssignee.getMultipleColumn());
                                        if(ObjectUtil.isEmpty(variable)){
                                            throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                                        }
                                    }
                                    if(actNodeAssignee.getMultiple()&&StringUtils.isBlank(actNodeAssignee.getMultipleColumn())){
                                        throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                                    }
                                }
                            }
                        }
                    }
                } else if (StringUtils.isBlank(t.getAssignee())) {
                    throw new ServiceException("???" + t.getName() + "?????????????????????????????????");
                }
            }
        }
        return true;
    }


    /**
     * @Description: ????????????????????????
     * @param: task ??????
     * @param: assignees ?????????
     * @return: void
     * @author: gssong
     * @Date: 2021/10/21
     */
    public void settingAssignee(Task task,List<Long> assignees){
        if (assignees.size() == 1) {
            taskService.setAssignee(task.getId(), assignees.get(0).toString());
        } else {
            // ?????????????????????
            for (Long assignee : assignees) {
                taskService.addCandidateUser(task.getId(), assignee.toString());
            }
        }
    }


    /**
     * @Description: ?????????????????????????????????
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskFinishVo>
     * @author: gssong
     * @Date: 2021/10/23
     */
    @Override
    public TableDataInfo<TaskFinishVo> getTaskFinishByPage(TaskREQ req) {
        //???????????????
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
     * @Description: ???????????????????????????????????????
     * @param: req
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     * @author: gssong
     * @Date: 2021/10/23
     */
    @Override
    public Map<String,Object> getNextNodeInfo(NextNodeREQ req) {
        Map<String, Object> map = new HashMap<>();
        //????????????
        TaskEntity task = (TaskEntity)taskService.createTaskQuery().taskId(req.getTaskId()).singleResult();
        //??????????????????
        List<ActTaskNode> taskNodeList = iActTaskNodeService.getListByInstanceId(task.getProcessInstanceId()).stream().filter(e->e.getIsBack()).collect(Collectors.toList());
        map.put("backNodeList",taskNodeList);
        //????????????
        if(ObjectUtil.isNotEmpty(task.getDelegationState())&&ActConstant.PENDING.equals(task.getDelegationState().name())){
            map.put("list",new ArrayList<>());
            map.put("isMultiInstance",false);
            return map;
        }
        //???????????????????????????
        MultiVo isMultiInstance = workFlowUtils.isMultiInstance(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if(ObjectUtil.isEmpty(isMultiInstance)){
            map.put("isMultiInstance",false);
        }else{
            map.put("isMultiInstance",true);
        }
        //????????????
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        //?????????????????????
        if(ObjectUtil.isNotEmpty(isMultiInstance)){
            if(isMultiInstance.getType() instanceof ParallelMultiInstanceBehavior){
                map.put("multiList",multiList(task, taskList,isMultiInstance.getType(),null));
            }else if(isMultiInstance.getType() instanceof SequentialMultiInstanceBehavior){
                List<Long> assigneeList = (List)runtimeService.getVariable(task.getExecutionId(), isMultiInstance.getAssigneeList());
                map.put("multiList",multiList(task, taskList,isMultiInstance.getType(),assigneeList));
            }
        }else{
            map.put("multiList",new ArrayList<>());
        }
        //?????????????????????????????????????????????
        if(CollectionUtil.isNotEmpty(taskList)&&taskList.size()>1){
            //return null;
        }
        taskService.setVariables(task.getId(),req.getVariables());
        //????????????
        String processDefinitionId = task.getProcessDefinitionId();
        //??????bpmn??????
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //??????????????????id??????????????????????????????
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        // ???????????????????????????????????????
        List<ProcessNode> nextNodes = new ArrayList<>();
        // ??????????????????????????????
        List<ProcessNode> tempNodes = new ArrayList<>();
        ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId()).singleResult();
        workFlowUtils.getNextNodes(flowElement,executionEntity, nextNodes, tempNodes, task.getId(), null);
        if(CollectionUtil.isEmpty(tempNodes)&&CollectionUtil.isNotEmpty(nextNodes)){
            Iterator<ProcessNode> iterator = nextNodes.iterator();
            while (iterator.hasNext()) {
                ProcessNode node = iterator.next();
                if (ActConstant.FALSE.equals(node.getExpression())){
                    iterator.remove();
                }
            }
        }
        //????????????  ??????????????????????????????????????????false  ?????????????????????????????????????????????
        List<String> exclusiveLists = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVE_GATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
            map(ProcessNode::getNodeType).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(nextNodes) && CollectionUtil.isEmpty(exclusiveLists)) {
            nextNodes.addAll(tempNodes);
        }
        // ????????????
        List<ProcessNode> nodeList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(nextNodes)) {
            //????????????  ??????????????????????????????????????????false  ?????????????????????????????????????????????
            List<String> exclusiveList = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVE_GATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
                map(ProcessNode::getNodeType).collect(Collectors.toList());
            if (!CollectionUtil.isEmpty(nextNodes) && CollectionUtil.isEmpty(exclusiveList)) {
                nextNodes.addAll(tempNodes);
            }
            //????????????
            List<String> exclusiveGatewayList = nextNodes.stream().filter(e -> e.getNodeType().equals(ActConstant.EXCLUSIVE_GATEWAY) && e.getExpression().equals(ActConstant.TRUE)).
                map(ProcessNode::getNodeType).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(exclusiveGatewayList)) {
                nextNodes.forEach(node -> {
                    if ((ActConstant.EXCLUSIVE_GATEWAY.equals(node.getNodeType()) && node.getExpression())) {
                        nodeList.add(node);
                    }
                });
                //????????????????????????
                List<ProcessNode> processNodeList = getProcessNodeAssigneeList(nodeList, task.getProcessDefinitionId());
                map.put("list",processNodeList);
                return map;
            } else {
                //????????????????????????
                List<ProcessNode> processNodeList = getProcessNodeAssigneeList(nextNodes, task.getProcessDefinitionId());
                map.put("list",processNodeList);
                return map;
            }
        }
        map.put("list",nextNodes);
        return map;
    }


    /**
     * @Description: ?????????????????????
     * @param: task  ????????????
     * @param: taskList  ????????????????????????
     * @param: type  ????????????
     * @param: assigneeList ??????????????????
     * @return: java.util.List<com.ruoyi.workflow.domain.vo.TaskVo>
     * @author: gssong
     * @Date: 2022/4/24 11:17
     */
    private List<TaskVo> multiList(TaskEntity task, List<Task> taskList,Object type,List<Long> assigneeList) {
        List<TaskVo> taskListVo = new ArrayList<>();
        if(type instanceof SequentialMultiInstanceBehavior){
            List<Long> userIds = assigneeList.stream().filter(userId -> !userId.toString().equals(task.getAssignee())).collect(Collectors.toList());
            List<SysUser> sysUsers = null;
            if(CollectionUtil.isNotEmpty(userIds)){
                sysUsers = iUserService.selectListUserByIds(userIds);
            }
            for (Long userId : userIds) {
                TaskVo taskVo = new TaskVo();
                taskVo.setId("????????????");
                taskVo.setExecutionId("????????????");
                taskVo.setProcessInstanceId(task.getProcessInstanceId());
                taskVo.setName(task.getName());
                taskVo.setAssigneeId(String.valueOf(userId));
                if(CollectionUtil.isNotEmpty(sysUsers)&&sysUsers.size()>0){
                    SysUser sysUser = sysUsers.stream().filter(u -> u.getUserId().toString().equals(userId.toString())).findFirst().orElse(null);
                    if(ObjectUtil.isNotEmpty(sysUser)){
                        taskVo.setAssignee(sysUser.getUserName());
                    }
                }
                taskListVo.add(taskVo);
            }
            return taskListVo;
        }else if(type instanceof ParallelMultiInstanceBehavior){
            List<Task> tasks = taskList.stream().filter(e -> !e.getExecutionId().equals(task.getExecutionId())
                &&e.getTaskDefinitionKey().equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(tasks)){
                List<Long> userIds = tasks.stream().map(e -> Long.valueOf(e.getAssignee())).collect(Collectors.toList());
                List<SysUser> sysUsers = null;
                if(CollectionUtil.isNotEmpty(userIds)){
                    sysUsers = iUserService.selectListUserByIds(userIds);
                }
                for (Task t : tasks) {
                    TaskVo taskVo = new TaskVo();
                    taskVo.setId(t.getId());
                    taskVo.setExecutionId(t.getExecutionId());
                    taskVo.setProcessInstanceId(t.getProcessInstanceId());
                    taskVo.setName(t.getName());
                    taskVo.setAssigneeId(t.getAssignee());
                    if(CollectionUtil.isNotEmpty(sysUsers)){
                        SysUser sysUser = sysUsers.stream().filter(u -> u.getUserId().toString().equals(t.getAssignee())).findFirst().orElse(null);
                        if(ObjectUtil.isNotEmpty(sysUser)){
                            taskVo.setAssignee(sysUser.getUserName());
                        }
                    }
                    taskListVo.add(taskVo);
                }
               return taskListVo;
            }
        }
        return new ArrayList<>();
    }

    /**
     * @Description: ????????????????????????
     * @param: nodeList ????????????
     * @param: definitionId ????????????id
     * @return: java.util.List<com.ruoyi.workflow.domain.vo.ProcessNode>
     * @author: gssong
     * @Date: 2021/10/23
     */
    private List<ProcessNode> getProcessNodeAssigneeList(List<ProcessNode> nodeList, String definitionId) {
        List<ActNodeAssignee> actNodeAssignees = iActNodeAssigneeService.getInfoByProcessDefinitionId(definitionId);
        if (CollectionUtil.isNotEmpty(actNodeAssignees)) {
            for (ProcessNode processNode : nodeList) {
                //??????????????????????????????????????????????????????
                if (StringUtils.isBlank(processNode.getAssignee())) {
                    if(CollectionUtil.isEmpty(actNodeAssignees)){
                        throw new ServiceException("????????????????????????????????????????????????");
                    }
                    ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(processNode.getNodeId())).findFirst().orElse(null);

                    //????????? ?????? ??????id ???????????????????????????
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
                        //??????????????????????????????????????????
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
                        throw new ServiceException(processNode.getNodeName() + "??????????????????????????????????????????");
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
                // ???????????????????????????????????????  ??????????????? ??????
                // ????????????????????????????????????
                if (ActConstant.WORKFLOW_ASSIGNEE.equals(node.getChooseWay())||!node.getIsShow()) {
                    iterator.remove();
                }
            }
        }
        return nodeList;
    }

    /**
     * @Description: ?????????????????????????????????
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskFinishVo>
     * @author: gssong
     * @Date: 2021/10/23
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
     * @Description: ?????????????????????????????????
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskWaitingVo>
     * @author: gssong
     * @Date: 2021/10/17
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
            taskWaitingVo.setProcessStatus(task.isSuspended() == true ? "??????" : "??????");
            //???????????????: ????????????????????????????????????????????????
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
            // ??????????????????
            ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId()).singleResult();
            //???????????????
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
     * @Description: ????????????
     * @param: backProcessVo
     * @return: java.lang.String
     * @author: gssong
     * @Date: 2021/11/6
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String backProcess(BackProcessVo backProcessVo) {

        Task task = taskService.createTaskQuery().taskId(backProcessVo.getTaskId()).taskAssignee(getUserId().toString()).singleResult();
        if (task.isSuspended()) {
            throw new ServiceException("????????????????????????");
        }
        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("????????????????????????????????????????????????");
        }
        //????????????id
        String processInstanceId = task.getProcessInstanceId();
        MultiVo currentMultiInstance = workFlowUtils.isMultiInstance(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        MultiVo targetMultiInstance = workFlowUtils.isMultiInstance(task.getProcessDefinitionId(), backProcessVo.getTargetActivityId());

        // 1. ???????????????????????? BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        // 2.??????????????????
        FlowNode curFlowNode = (FlowNode) bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        // 3.?????????????????????????????????
        List<SequenceFlow> sequenceFlowList = curFlowNode.getOutgoingFlows();
        // 4. ??????????????????????????????????????????
        List<SequenceFlow> oriSequenceFlows = new ArrayList<>();
        oriSequenceFlows.addAll(sequenceFlowList);
        // 5. ?????????????????????????????????
        sequenceFlowList.clear();
        // 6. ????????????????????????
        FlowNode targetFlowNode = (FlowNode) bpmnModel.getFlowElement(backProcessVo.getTargetActivityId());
        // 7. ?????????????????????????????????
        List<SequenceFlow> incomingFlows = targetFlowNode.getIncomingFlows();
        // 8. ????????????????????????
        List<SequenceFlow> targetSequenceFlow = new ArrayList<>();
        for (SequenceFlow incomingFlow : incomingFlows) {
            // ???????????????????????????????????????????????????????????????
            FlowNode source = (FlowNode) incomingFlow.getSourceFlowElement();
            List<SequenceFlow> sequenceFlows;
            if (source instanceof ParallelGateway) {
                // ????????????: ??????????????????????????????????????????????????????????????????
                sequenceFlows = source.getOutgoingFlows();
            } else {
                // ?????????????????????, ????????????????????????????????????
                sequenceFlows = targetFlowNode.getIncomingFlows();
            }
            targetSequenceFlow.addAll(sequenceFlows);
        }
        // 9. ??????????????????????????????????????????
        curFlowNode.setOutgoingFlows(targetSequenceFlow);
        // 10. ????????????????????????????????????????????????????????????????????????
        List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if(list.size() == 1){
            if(ObjectUtil.isEmpty(currentMultiInstance)){
                DeleteExecutionChildCmd deleteExecutionChildCmd = new DeleteExecutionChildCmd(task.getExecutionId());
                managementService.executeCommand(deleteExecutionChildCmd);
            }
            // ?????????????????????????????????
            taskService.addComment(task.getId(), processInstanceId, StringUtils.isNotBlank(backProcessVo.getComment()) ? backProcessVo.getComment() : "??????");
            Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            // ????????????????????????????????????????????????????????????????????????????????????
            taskService.complete(backProcessVo.getTaskId());
            if(ObjectUtil.isNotEmpty(currentMultiInstance)&&ObjectUtil.isEmpty(targetMultiInstance)){
                DeleteVariableCmd deleteVariableCmd = new DeleteVariableCmd(execution.getParentId(),true,true);
                managementService.executeCommand(deleteVariableCmd);
            }
        }else{
            for (Task t : list) {
                if (backProcessVo.getTaskId().equals(t.getId())) {
                    // ?????????????????????????????????
                    taskService.addComment(t.getId(), processInstanceId, StringUtils.isNotBlank(backProcessVo.getComment()) ? backProcessVo.getComment() : "??????");
                    // ????????????????????????????????????????????????????????????????????????????????????
                    taskService.complete(t.getId());
                } else {
                    Execution execution = runtimeService.createExecutionQuery().executionId(t.getExecutionId()).singleResult();
                    // ?????????????????????????????????
                    taskService.addComment(t.getId(), processInstanceId, StringUtils.isNotBlank(backProcessVo.getComment()) ? backProcessVo.getComment() : "??????");
                    // ????????????????????????????????????????????????????????????????????????????????????
                    taskService.complete(t.getId());
                    historyService.deleteHistoricTaskInstance(t.getId());
                    if(ObjectUtil.isEmpty(currentMultiInstance)){
                        DeleteVariableCmd deleteVariableCmd = new DeleteVariableCmd(execution.getId(),false,true);
                        managementService.executeCommand(deleteVariableCmd);
                        historyService.createNativeHistoricActivityInstanceQuery()
                            .sql("DELETE  FROM ACT_HI_ACTINST WHERE EXECUTION_ID_ = '" + t.getExecutionId() + "'").list();
                    }else if(ObjectUtil.isEmpty(targetMultiInstance)){
                        DeleteVariableCmd deleteVariableCmd = new DeleteVariableCmd(execution.getParentId(),true,true);
                        managementService.executeCommand(deleteVariableCmd);
                    }
                }
            }
        }
        // 11. ?????????????????????????????????????????????????????????????????????
        curFlowNode.setOutgoingFlows(oriSequenceFlows);
       // ??????????????????
        LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActNodeAssignee::getNodeId,backProcessVo.getTargetActivityId());
        wrapper.eq(ActNodeAssignee::getProcessDefinitionId,task.getProcessDefinitionId());
        ActNodeAssignee actNodeAssignee = iActNodeAssigneeService.getOne(wrapper);
        List<Task> newTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if(ObjectUtil.isNotEmpty(actNodeAssignee)&&!actNodeAssignee.getMultiple()){
            for (Task newTask : newTaskList) {
                // ???????????????????????????
                List<HistoricTaskInstance> oldTargerTaskList = historyService.createHistoricTaskInstanceQuery()
                    .taskDefinitionKey(newTask.getTaskDefinitionKey()) // ??????id
                    .processInstanceId(processInstanceId)
                    .finished() // ????????????????????????
                    .orderByTaskCreateTime().desc() // ???????????????????????????
                    .list();
                if(CollectionUtil.isNotEmpty(oldTargerTaskList)){
                    HistoricTaskInstance oldTargerTask = oldTargerTaskList.get(0);
                    taskService.setAssignee(newTask.getId(), oldTargerTask.getAssignee());
                }
            }
        }

        // 13. ??????????????????????????????
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
        return processInstanceId;
    }

    /**
     * @Description: ?????????????????????????????????????????????
     * @param: processDefinitionId ????????????id
     * @param: targetActivityId ???????????????id
     * @param: task ??????
     * @return: java.util.List<java.lang.String>
     * @author: gssong
     * @Date: 2022/4/10
     */
    public List<String>  getPrevUserNodeList(String processDefinitionId, String targetActivityId, Task task){
        List<String> nodeListId = new ArrayList<>();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        FlowElement flowElement = bpmnModel.getFlowElement(targetActivityId);
        List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getIncomingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement sourceFlowElement = outgoingFlow.getSourceFlowElement();
            //????????????
            if(sourceFlowElement instanceof ParallelGateway){
                List<SequenceFlow> parallelGatewayOutgoingFlow = ((ParallelGateway) sourceFlowElement).getOutgoingFlows();
                for (SequenceFlow sequenceFlow : parallelGatewayOutgoingFlow) {
                    FlowElement element = sequenceFlow.getTargetFlowElement();
                    if(element instanceof UserTask){
                        nodeListId.add(element.getId());
                    }
                }
             //????????????
            }/*else if(sourceFlowElement instanceof InclusiveGateway){
                List<SequenceFlow> inclusiveGatewayOutgoingFlow = ((InclusiveGateway) sourceFlowElement).getOutgoingFlows();
                for (SequenceFlow sequenceFlow : inclusiveGatewayOutgoingFlow) {
                    String conditionExpression = outgoingFlow.getConditionExpression();
                    FlowElement element = sequenceFlow.getTargetFlowElement();
                    if (element instanceof UserTask) {
                        if(StringUtils.isBlank(conditionExpression)){
                            nodeListId.add(element.getId());
                        }else{
                            ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
                                .executionId(task.getExecutionId()).singleResult();
                            ExpressCmd expressCmd = new ExpressCmd(outgoingFlow,executionEntity);
                            Boolean condition = managementService.executeCommand(expressCmd);
                            if(condition){
                                nodeListId.add(element.getId());
                            }
                        }
                    }
                }
            }*/
        }
        return nodeListId;
    }

    /**
     * @Description: ?????????????????????????????????????????????
     * @param: processInstId
     * @return: java.util.List<com.ruoyi.workflow.domain.ActTaskNode>
     * @author: gssong
     * @Date: 2021/11/6
     */
    @Override
    public List<ActTaskNode> getBackNodes(String processInstId) {
        List<ActTaskNode> list = iActTaskNodeService.getListByInstanceId(processInstId).stream().filter(e->e.getIsBack()).collect(Collectors.toList());
        return list;
    }

    /**
     * @Description: ????????????
     * @param: taskREQ
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2022/3/4
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delegateTask(TaskREQ taskREQ) {
        if(StringUtils.isBlank(taskREQ.getDelegateUserId())){
            throw new ServiceException("??????????????????");
        }
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskREQ.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            throw new ServiceException("????????????????????????????????????????????????");
        }
        try{
            TaskEntity subTask = this.createSubTask(task,new Date());
            taskService.addComment(subTask.getId(), task.getProcessInstanceId(),"???"+LoginHelper.getUsername()+"???????????????"+taskREQ.getDelegateUserName()+"???");
            //????????????
            taskService.delegateTask(taskREQ.getTaskId(), taskREQ.getDelegateUserId());
            //???????????????????????????
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

    /**
     * @Description: ????????????
     * @param: transmitREQ
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/3/13 13:18
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> transmitTask(TransmitREQ transmitREQ) {
        Task task = taskService.createTaskQuery().taskId(transmitREQ.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            return R.fail("????????????????????????????????????????????????");
        }
        try {
            TaskEntity subTask = createSubTask(task, new Date());
            taskService.addComment(subTask.getId(), task.getProcessInstanceId(),
                StringUtils.isNotBlank(transmitREQ.getComment())?transmitREQ.getComment():LoginHelper.getUsername()+"???????????????");
            taskService.complete(subTask.getId());
            taskService.setAssignee(task.getId(),transmitREQ.getTransmitUserId());
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }

    /**
     * @Description: ??????????????????
     * @param: parentTask
     * @param: createTime
     * @return: org.activiti.engine.impl.persistence.entity.TaskEntity
     * @author: gssong
     * @Date: 2022/3/13
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
                hiTaskInst.setStartTime(createTime);
                iActHiTaskInstService.updateById(hiTaskInst);
            }
        }
        return  task;
    }

    /**
     * @Description: ??????????????????
     * @param: addMultiREQ
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/4/15 13:06
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> addMultiInstanceExecution(AddMultiREQ addMultiREQ) {
        String taskId = addMultiREQ.getTaskId();
        Task task = taskService.createTaskQuery().taskId(taskId)
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            throw new ServiceException("????????????????????????????????????????????????");
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processInstanceId = task.getProcessInstanceId();
        String processDefinitionId = task.getProcessDefinitionId();
        MultiVo multiVo = workFlowUtils.isMultiInstance(processDefinitionId, taskDefinitionKey);
        if(ObjectUtil.isEmpty(multiVo)){
            throw new ServiceException("??????????????????????????????");
        }
        try {
            if(multiVo.getType() instanceof ParallelMultiInstanceBehavior){
                for (Long assignee : addMultiREQ.getAssignees()) {
                    AddMultiInstanceExecutionCmd addMultiInstanceExecutionCmd = new AddMultiInstanceExecutionCmd(taskDefinitionKey, processInstanceId, Collections.singletonMap(multiVo.getAssignee(), assignee));
                    managementService.executeCommand(addMultiInstanceExecutionCmd);
                }
            }else if(multiVo.getType() instanceof SequentialMultiInstanceBehavior){
                AddSequenceMultiInstanceCmd addSequenceMultiInstanceCmd = new AddSequenceMultiInstanceCmd(task.getExecutionId(),multiVo.getAssigneeList(),addMultiREQ.getAssignees());
                managementService.executeCommand(addSequenceMultiInstanceCmd);
            }
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }

    /**
     * @Description: ??????????????????
     * @param: deleteMultiREQ
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/4/16 10:59
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> deleteMultiInstanceExecution(DeleteMultiREQ deleteMultiREQ) {
        Task task = taskService.createTaskQuery().taskId(deleteMultiREQ.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if(ObjectUtil.isEmpty(task)){
            throw new ServiceException("????????????????????????????????????????????????");
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processDefinitionId = task.getProcessDefinitionId();
        MultiVo multiVo = workFlowUtils.isMultiInstance(processDefinitionId, taskDefinitionKey);
        if(ObjectUtil.isEmpty(multiVo)){
            throw new ServiceException("??????????????????????????????");
        }
        try {
            if(multiVo.getType() instanceof ParallelMultiInstanceBehavior){
                for (String executionId : deleteMultiREQ.getExecutionIds()) {
                    DeleteMultiInstanceExecutionCmd deleteMultiInstanceExecutionCmd = new DeleteMultiInstanceExecutionCmd(executionId,false);
                    managementService.executeCommand(deleteMultiInstanceExecutionCmd);
                }
                for (String taskId : deleteMultiREQ.getTaskIds()) {
                    historyService.deleteHistoricTaskInstance(taskId);
                }
            }else if(multiVo.getType() instanceof SequentialMultiInstanceBehavior){
                DeleteSequenceMultiInstanceCmd deleteSequenceMultiInstanceCmd = new DeleteSequenceMultiInstanceCmd(task.getAssignee(),task.getExecutionId(),multiVo.getAssigneeList(),deleteMultiREQ.getAssigneeIds());
                managementService.executeCommand(deleteSequenceMultiInstanceCmd);
            }
            return R.ok();
        }catch (Exception e){
            e.printStackTrace();
            return R.fail();
        }
    }
}
