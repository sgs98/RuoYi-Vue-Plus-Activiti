package com.ruoyi.workflow.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.helper.LoginHelper;
import com.ruoyi.common.utils.JsonUtils;
import com.ruoyi.workflow.activiti.cmd.*;
import com.ruoyi.workflow.common.constant.ActConstant;
import com.ruoyi.workflow.common.enums.BusinessStatusEnum;
import com.ruoyi.workflow.domain.ActBusinessStatus;
import com.ruoyi.workflow.domain.ActHiTaskInst;
import com.ruoyi.workflow.domain.ActNodeAssignee;
import com.ruoyi.workflow.domain.ActTaskNode;
import com.ruoyi.workflow.domain.bo.*;
import com.ruoyi.workflow.domain.vo.*;
import com.ruoyi.workflow.activiti.factory.WorkflowService;
import com.ruoyi.workflow.mapper.TaskMapper;
import com.ruoyi.workflow.service.*;
import com.ruoyi.workflow.utils.WorkFlowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.*;
import org.activiti.engine.ManagementService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final IUserService iUserService;

    private final IActBusinessStatusService iActBusinessStatusService;

    private final IActTaskNodeService iActTaskNodeService;

    private final IActNodeAssigneeService iActNodeAssigneeService;

    private final IActBusinessRuleService iActBusinessRuleService;

    private final IActHiTaskInstService iActHiTaskInstService;

    private final ManagementService managementService;

    private final TaskMapper taskMapper;

    private final IActProcessDefSetting iActProcessDefSetting;


    /**
     * @Description: 查询当前用户的待办任务
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskWaitingVo>
     * @author: gssong
     * @Date: 2021/10/17
     */
    @Override
    public TableDataInfo<TaskWaitingVo> getTaskWaitByPage(TaskBo req) {
        //当前登录人
        String currentUserId = LoginHelper.getLoginUser().getUserId().toString();
        TaskQuery query = taskService.createTaskQuery()
            // 候选人或者办理人
            .taskCandidateOrAssigned(currentUserId)
            .orderByTaskCreateTime().asc();
        if (StringUtils.isNotEmpty(req.getTaskName())) {
            query.taskNameLikeIgnoreCase("%" + req.getTaskName() + "%");
        }
        List<Task> taskList = query.listPage(req.getFirstResult(), req.getPageSize());
        long total = query.count();
        if (CollectionUtil.isEmpty(taskList)) {
            return new TableDataInfo<>();
        }
        List<TaskWaitingVo> list = new ArrayList<>();
        //流程实例id
        Set<String> processInstanceIds = taskList.stream().map(Task::getProcessInstanceId).collect(Collectors.toSet());
        //流程定义id
        List<String> processDefinitionIds = taskList.stream().map(Task::getProcessDefinitionId).collect(Collectors.toList());
        // 查询流程实例
        List<ProcessInstance> processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceIds(processInstanceIds).list();
        // 查询流程定义设置
        List<ActProcessDefSettingVo> processDefSettingLists = iActProcessDefSetting.getProcessDefSettingByDefIds(processDefinitionIds);

        for (Task task : taskList) {
            TaskWaitingVo taskWaitingVo = new TaskWaitingVo();
            BeanUtils.copyProperties(task, taskWaitingVo);
            taskWaitingVo.setAssigneeId(StringUtils.isNotBlank(task.getAssignee()) ? Long.valueOf(task.getAssignee()) : null);
            taskWaitingVo.setProcessStatus(!task.isSuspended() ? "激活" : "挂起");
            // 查询流程实例
            processInstanceList.stream().filter(e -> e.getProcessInstanceId().equals(task.getProcessInstanceId())).findFirst()
                .ifPresent(e -> {
                    //流程发起人
                    String startUserId = e.getStartUserId();
                    if (StringUtils.isNotBlank(startUserId)) {
                        SysUser sysUser = iUserService.selectUserById(Long.valueOf(startUserId));
                        if (ObjectUtil.isNotNull(sysUser)) {
                            taskWaitingVo.setStartUserNickName(sysUser.getNickName());
                        }
                    }
                    taskWaitingVo.setProcessDefinitionVersion(e.getProcessDefinitionVersion());
                    taskWaitingVo.setProcessDefinitionName(e.getProcessDefinitionName());
                    taskWaitingVo.setBusinessKey(e.getBusinessKey());
                });
            // 查询流程定义
            processDefSettingLists.stream().filter(e -> e.getProcessDefinitionId().equals(task.getProcessDefinitionId())).findFirst()
                .ifPresent(taskWaitingVo::setActProcessDefSetting);
            list.add(taskWaitingVo);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            //认领与归还标识
            list.forEach(e -> {
                List<IdentityLink> identityLinkList = WorkFlowUtils.getCandidateUser(e.getId());
                if (CollectionUtil.isNotEmpty(identityLinkList)) {
                    List<String> collectType = identityLinkList.stream().map(IdentityLink::getType).collect(Collectors.toList());
                    if (StringUtils.isBlank(e.getAssignee()) && collectType.size() > 1 && collectType.contains(ActConstant.CANDIDATE)) {
                        e.setIsClaim(false);
                    } else if (StringUtils.isNotBlank(e.getAssignee()) && collectType.size() > 1 && collectType.contains(ActConstant.CANDIDATE)) {
                        e.setIsClaim(true);
                    }
                }
            });
            //办理人集合
            List<Long> assigneeList = list.stream().map(TaskWaitingVo::getAssigneeId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(assigneeList)) {
                List<SysUser> userList = iUserService.selectListUserByIds(assigneeList);
                if (CollectionUtil.isNotEmpty(userList)) {
                    list.forEach(e -> {
                        SysUser sysUser = userList.stream().filter(t -> StringUtils.isNotBlank(e.getAssignee()) && t.getUserId().compareTo(e.getAssigneeId()) == 0).findFirst().orElse(null);
                        if (ObjectUtil.isNotEmpty(sysUser)) {
                            e.setAssignee(sysUser.getNickName());
                            e.setAssigneeId(sysUser.getUserId());
                        }
                    });
                }
            }
            //业务id集合
            List<String> businessKeyList = list.stream().map(TaskWaitingVo::getBusinessKey).collect(Collectors.toList());
            List<ActBusinessStatus> infoList = iActBusinessStatusService.getListInfoByBusinessKey(businessKeyList);
            if (CollectionUtil.isNotEmpty(infoList)) {
                list.forEach(e -> {
                    ActBusinessStatus businessStatus = infoList.stream().filter(t -> t.getBusinessKey().equals(e.getBusinessKey())).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(businessStatus)) {
                        e.setActBusinessStatus(businessStatus);
                    }
                });
            }
        }
        return new TableDataInfo<>(list, total);
    }


    /**
     * @Description: 办理任务
     * @param: req
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2021/10/21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean completeTask(TaskCompleteBo req) {
        // 1.查询任务
        Task task = taskService.createTaskQuery().taskId(req.getTaskId()).taskAssignee(getUserId().toString()).singleResult();

        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("任务不存在或您不是当前审批人");
        }

        if (task.isSuspended()) {
            throw new ServiceException(ActConstant.MESSAGE_SUSPENDED);
        }
        try {
            //办理委托任务
            if (ObjectUtil.isNotEmpty(task.getDelegationState()) && ActConstant.PENDING.equals(task.getDelegationState().name())) {
                taskService.resolveTask(req.getTaskId());
                ActHiTaskInst hiTaskInst = iActHiTaskInstService.getById(task.getId());
                TaskEntity newTask = WorkFlowUtils.createNewTask(task, hiTaskInst.getStartTime());
                taskService.addComment(newTask.getId(), task.getProcessInstanceId(), req.getMessage());
                taskService.complete(newTask.getId());
                ActHiTaskInst actHiTaskInst = new ActHiTaskInst();
                actHiTaskInst.setId(task.getId());
                actHiTaskInst.setStartTime(new Date());
                iActHiTaskInstService.updateById(actHiTaskInst);
                return true;
            }

            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            // 2. 判断下一节点是否是会签 如果是会签 将选择的人员放到会签变量
            List<ActNodeAssignee> actNodeAssignees = iActNodeAssigneeService.getInfoByProcessDefinitionId(task.getProcessDefinitionId());
            for (ActNodeAssignee actNodeAssignee : actNodeAssignees) {
                String column = actNodeAssignee.getMultipleColumn();
                String assigneeId = actNodeAssignee.getAssigneeId();
                if (actNodeAssignee.getMultiple() && actNodeAssignee.getIsShow()) {
                    List<Long> userIdList = req.getAssignees(actNodeAssignee.getMultipleColumn());
                    if (CollectionUtil.isNotEmpty(userIdList)) {
                        taskService.setVariable(task.getId(), column, userIdList);
                    }
                }
                //判断是否有会签并且不需要弹窗选人的节点
                if (actNodeAssignee.getMultiple() && !actNodeAssignee.getIsShow() && (StringUtils.isBlank(column) || StringUtils.isBlank(assigneeId))) {
                    throw new ServiceException("请检查【" + processInstance.getProcessDefinitionKey() + "】配置 ");
                }
                if (actNodeAssignee.getMultiple() && !actNodeAssignee.getIsShow()) {
                    WorkFlowUtils.settingAssignee(task, actNodeAssignee, actNodeAssignee.getMultiple());
                }
            }
            // 3. 指定任务审批意见
            taskService.addComment(req.getTaskId(), task.getProcessInstanceId(), req.getMessage());
            // 设置变量
            taskService.setVariables(req.getTaskId(), req.getVariables());
            // 任务前执行集合
            List<TaskListenerVo> handleBeforeList = null;
            // 任务后执行集合
            List<TaskListenerVo> handleAfterList = null;
            ActNodeAssignee nodeEvent = actNodeAssignees.stream().filter(e -> task.getTaskDefinitionKey().equals(e.getNodeId())).findFirst().orElse(null);
            if (ObjectUtil.isNotEmpty(nodeEvent) && StringUtils.isNotBlank(nodeEvent.getTaskListener())) {
                List<TaskListenerVo> taskListenerVos = JsonUtils.parseArray(nodeEvent.getTaskListener(), TaskListenerVo.class);
                handleBeforeList = taskListenerVos.stream().filter(e -> ActConstant.HANDLE_BEFORE.equals(e.getEventType())).collect(Collectors.toList());
                handleAfterList = taskListenerVos.stream().filter(e -> ActConstant.HANDLE_AFTER.equals(e.getEventType())).collect(Collectors.toList());
            }
            // 任务前执行
            if (CollectionUtil.isNotEmpty(handleBeforeList)) {
                for (TaskListenerVo taskListenerVo : handleBeforeList) {
                    WorkFlowUtils.springInvokeMethod(taskListenerVo.getBeanName(), ActConstant.HANDLE_PROCESS
                        , task.getProcessInstanceId(), task.getId());
                }
            }
            // 4. 完成任务
            taskService.complete(req.getTaskId());
            // 任务后执行
            if (CollectionUtil.isNotEmpty(handleAfterList)) {
                for (TaskListenerVo taskListenerVo : handleAfterList) {
                    WorkFlowUtils.springInvokeMethod(taskListenerVo.getBeanName(), ActConstant.HANDLE_PROCESS
                        , task.getProcessInstanceId());
                }
            }
            // 5. 记录执行过的流程任务节点
            WorkFlowUtils.recordExecuteNode(task, actNodeAssignees);
            // 更新业务状态为：办理中
            iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.WAITING, task.getProcessInstanceId());
            // 6. 查询下一个任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            // 7. 如果为空 办结任务
            if (CollectionUtil.isEmpty(taskList)) {
                // 更新业务状态已完成 办结流程
                return iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.FINISH);
            }

            // 抄送
            if (req.getIsCopy()) {
                if (StringUtils.isBlank(req.getAssigneeIds())) {
                    throw new ServiceException("抄送人不能为空 ");
                }
                TaskEntity newTask = WorkFlowUtils.createNewTask(task, new Date());
                taskService.addComment(newTask.getId(), task.getProcessInstanceId(),
                    LoginHelper.getUsername() + "【抄送】给" + req.getAssigneeNames());
                taskService.complete(newTask.getId());
                WorkFlowUtils.createSubTask(taskList, req.getAssigneeIds());
            }
            // 自动办理
            Boolean autoComplete = WorkFlowUtils.autoComplete(processInstance.getProcessInstanceId(), processInstance.getBusinessKey(), actNodeAssignees, req);
            if (autoComplete) {
                List<Task> nextTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
                if (!CollectionUtil.isEmpty(nextTaskList)) {
                    for (Task t : nextTaskList) {
                        ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> t.getTaskDefinitionKey().equals(e.getNodeId())).findFirst().orElse(null);
                        if (ObjectUtil.isNull(nodeAssignee)) {
                            throw new ServiceException("请检查【" + t.getName() + "】节点配置");
                        }
                        WorkFlowUtils.settingAssignee(t, nodeAssignee, nodeAssignee.getMultiple());
                    }
                } else {
                    // 更新业务状态已完成 办结流程
                    return iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.FINISH);
                }
                // 发送站内信
                WorkFlowUtils.sendMessage(req.getSendMessage(), processInstance.getProcessInstanceId());
                return true;
            }
            // 8. 如果不为空 指定办理人
            List<Task> nextTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            if (CollectionUtil.isEmpty(nextTaskList)) {
                // 更新业务状态已完成 办结流程
                return iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.FINISH);
            }
            for (Task t : nextTaskList) {
                ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> t.getTaskDefinitionKey().equals(e.getNodeId())).findFirst().orElse(null);
                if (ObjectUtil.isNull(nodeAssignee)) {
                    throw new ServiceException("请检查【" + t.getName() + "】节点配置");
                }
                // 不需要弹窗选人
                if (!nodeAssignee.getIsShow() && StringUtils.isBlank(t.getAssignee()) && !nodeAssignee.getMultiple()) {
                    // 设置人员
                    WorkFlowUtils.settingAssignee(t, nodeAssignee, false);
                } else if (nodeAssignee.getIsShow() && StringUtils.isBlank(t.getAssignee()) && !nodeAssignee.getMultiple()) {
                    // 弹窗选人 根据当前任务节点id获取办理人
                    List<Long> assignees = req.getAssignees(t.getTaskDefinitionKey());
                    if (CollectionUtil.isEmpty(assignees)) {
                        throw new ServiceException("【" + t.getName() + "】任务环节未配置审批人");
                    }
                    // 设置选人
                    WorkFlowUtils.setAssignee(t, assignees);
                }
            }
            // 发送站内信
            WorkFlowUtils.sendMessage(req.getSendMessage(), processInstance.getProcessInstanceId());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("办理失败:" + e.getMessage());
            throw new ServiceException("办理失败:" + e.getMessage());
        }
    }

    /**
     * @Description: 查询当前用户的已办任务
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskFinishVo>
     * @author: gssong
     * @Date: 2021/10/23
     */
    @Override
    public TableDataInfo<TaskFinishVo> getTaskFinishByPage(TaskBo req) {
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
            taskFinishVo.setAssigneeId(StringUtils.isNotBlank(hti.getAssignee()) ? Long.valueOf(hti.getAssignee()) : null);
            taskFinishVoList.add(taskFinishVo);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            //办理人集合
            List<Long> assigneeList = taskFinishVoList.stream().map(TaskFinishVo::getAssigneeId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(assigneeList)) {
                List<SysUser> userList = iUserService.selectListUserByIds(assigneeList);
                if (CollectionUtil.isNotEmpty(userList)) {
                    taskFinishVoList.forEach(e -> {
                        SysUser sysUser = userList.stream().filter(t -> t.getUserId().compareTo(e.getAssigneeId()) == 0).findFirst().orElse(null);
                        if (ObjectUtil.isNotEmpty(sysUser)) {
                            e.setAssignee(sysUser.getNickName());
                            e.setAssigneeId(sysUser.getUserId());
                        }
                    });
                }
            }
        }
        return new TableDataInfo<>(taskFinishVoList, total);
    }


    /**
     * @Description: 获取目标节点（下一个节点）
     * @param: req
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author: gssong
     * @Date: 2021/10/23
     */
    @Override
    public Map<String, Object> getNextNodeInfo(NextNodeBo req) {
        Map<String, Object> map = new HashMap<>(16);
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(req.getTaskId()).singleResult();
        if (task.isSuspended()) {
            throw new ServiceException(ActConstant.MESSAGE_SUSPENDED);
        }
        ActNodeAssignee nodeAssignee = iActNodeAssigneeService.getInfo(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        //可驳回的节点
        List<ActTaskNode> taskNodeList = iActTaskNodeService.getListByInstanceId(task.getProcessInstanceId()).stream().filter(ActTaskNode::getIsBack).collect(Collectors.toList());
        map.put("backNodeList", taskNodeList);
        //当前流程实例状态
        ActBusinessStatus actBusinessStatus = iActBusinessStatusService.getInfoByProcessInstId(task.getProcessInstanceId());
        if (ObjectUtil.isEmpty(actBusinessStatus)) {
            throw new ServiceException("当前流程异常，未生成act_business_status对象");
        } else {
            map.put("businessStatus", actBusinessStatus);
        }
        //委托流程
        if (ObjectUtil.isNotEmpty(task.getDelegationState()) && ActConstant.PENDING.equals(task.getDelegationState().name())) {
            ActNodeAssignee actNodeAssignee = new ActNodeAssignee();
            actNodeAssignee.setIsDelegate(false);
            actNodeAssignee.setIsTransmit(false);
            actNodeAssignee.setIsCopy(false);
            actNodeAssignee.setAddMultiInstance(false);
            actNodeAssignee.setDeleteMultiInstance(false);
            map.put("setting", actNodeAssignee);
            map.put("list", new ArrayList<>());
            map.put("isMultiInstance", false);
            return map;
        }
        //流程定义设置
        if (ObjectUtil.isNotEmpty(nodeAssignee)) {
            map.put("setting", nodeAssignee);
        } else {
            ActNodeAssignee actNodeAssignee = new ActNodeAssignee();
            actNodeAssignee.setIsDelegate(false);
            actNodeAssignee.setIsTransmit(false);
            actNodeAssignee.setIsCopy(false);
            actNodeAssignee.setAddMultiInstance(false);
            actNodeAssignee.setDeleteMultiInstance(false);
            map.put("setting", actNodeAssignee);
        }

        //判断当前是否为会签
        MultiVo isMultiInstance = WorkFlowUtils.isMultiInstance(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        map.put("isMultiInstance", ObjectUtil.isNotEmpty(isMultiInstance));
        //查询任务
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        //可以减签的人员
        if (ObjectUtil.isNotEmpty(isMultiInstance)) {
            if (isMultiInstance.getType() instanceof ParallelMultiInstanceBehavior) {
                map.put("multiList", multiList(task, taskList, isMultiInstance.getType(), null));
            } else if (isMultiInstance.getType() instanceof SequentialMultiInstanceBehavior) {
                List<Long> assigneeList = (List<Long>) runtimeService.getVariable(task.getExecutionId(), isMultiInstance.getAssigneeList());
                map.put("multiList", multiList(task, taskList, isMultiInstance.getType(), assigneeList));
            }
        } else {
            map.put("multiList", new ArrayList<>());
        }
        //如果是会签最后一个人员审批选人
        if (CollectionUtil.isNotEmpty(taskList) && taskList.size() > 1) {
            //return null;
        }
        taskService.setVariables(task.getId(), req.getVariables());
        //流程定义
        String processDefinitionId = task.getProcessDefinitionId();
        //查询bpmn信息
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //通过任务节点id，来获取当前节点信息
        FlowElement flowElement = bpmnModel.getFlowElement(task.getTaskDefinitionKey());
        //全部节点
        Collection<FlowElement> flowElements = bpmnModel.getProcesses().get(0).getFlowElements();
        //封装下一个用户任务节点信息
        List<ProcessNode> nextNodeList = new ArrayList<>();
        //保存没有表达式的节点
        List<ProcessNode> tempNodeList = new ArrayList<>();
        ExecutionEntityImpl executionEntity = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId()).singleResult();
        WorkFlowUtils.getNextNodeList(flowElements, flowElement, executionEntity, nextNodeList, tempNodeList, task.getId(), null);
        if (CollectionUtil.isNotEmpty(nextNodeList) && CollectionUtil.isNotEmpty(nextNodeList.stream().filter(e -> e.getExpression() != null && e.getExpression()).collect(Collectors.toList()))) {
            List<ProcessNode> nodeList = nextNodeList.stream().filter(e -> e.getExpression() != null && e.getExpression()).collect(Collectors.toList());
            List<ProcessNode> processNodeList = getProcessNodeAssigneeList(nodeList, task.getProcessDefinitionId());
            map.put("list", processNodeList);
        } else if (CollectionUtil.isNotEmpty(tempNodeList)) {
            List<ProcessNode> processNodeList = getProcessNodeAssigneeList(tempNodeList, task.getProcessDefinitionId());
            map.put("list", processNodeList);
        } else {
            map.put("list", nextNodeList);
        }
        return map;
    }


    /**
     * @Description: 可减签人员集合
     * @param: task  当前任务
     * @param: taskList  当前实例所有任务
     * @param: type  会签类型
     * @param: assigneeList 串行会签人员
     * @return: java.util.List<com.ruoyi.workflow.domain.vo.TaskVo>
     * @author: gssong
     * @Date: 2022/4/24 11:17
     */
    private List<TaskVo> multiList(TaskEntity task, List<Task> taskList, Object type, List<Long> assigneeList) {
        List<TaskVo> taskListVo = new ArrayList<>();
        if (type instanceof SequentialMultiInstanceBehavior) {
            List<Long> userIds = assigneeList.stream().filter(userId -> !userId.toString().equals(task.getAssignee())).collect(Collectors.toList());
            List<SysUser> sysUsers = null;
            if (CollectionUtil.isNotEmpty(userIds)) {
                sysUsers = iUserService.selectListUserByIds(userIds);
            }
            for (Long userId : userIds) {
                TaskVo taskVo = new TaskVo();
                taskVo.setId("串行会签");
                taskVo.setExecutionId("串行会签");
                taskVo.setProcessInstanceId(task.getProcessInstanceId());
                taskVo.setName(task.getName());
                taskVo.setAssigneeId(String.valueOf(userId));
                if (CollectionUtil.isNotEmpty(sysUsers) && sysUsers != null) {
                    sysUsers.stream().filter(u -> u.getUserId().toString().equals(userId.toString())).findFirst().ifPresent(user -> taskVo.setAssignee(user.getNickName()));
                }
                taskListVo.add(taskVo);
            }
            return taskListVo;
        } else if (type instanceof ParallelMultiInstanceBehavior) {
            List<Task> tasks = taskList.stream().filter(e -> StringUtils.isBlank(e.getParentTaskId()) && !e.getExecutionId().equals(task.getExecutionId())
                && e.getTaskDefinitionKey().equals(task.getTaskDefinitionKey())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(tasks)) {
                List<Long> userIds = tasks.stream().map(e -> Long.valueOf(e.getAssignee())).collect(Collectors.toList());
                List<SysUser> sysUsers = null;
                if (CollectionUtil.isNotEmpty(userIds)) {
                    sysUsers = iUserService.selectListUserByIds(userIds);
                }
                for (Task t : tasks) {
                    TaskVo taskVo = new TaskVo();
                    taskVo.setId(t.getId());
                    taskVo.setExecutionId(t.getExecutionId());
                    taskVo.setProcessInstanceId(t.getProcessInstanceId());
                    taskVo.setName(t.getName());
                    taskVo.setAssigneeId(t.getAssignee());
                    if (CollectionUtil.isNotEmpty(sysUsers)) {
                        SysUser sysUser = sysUsers.stream().filter(u -> u.getUserId().toString().equals(t.getAssignee())).findFirst().orElse(null);
                        if (ObjectUtil.isNotEmpty(sysUser)) {
                            taskVo.setAssignee(sysUser.getNickName());
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
     * @Description: 设置节点审批人员
     * @param: nodeList节点列表
     * @param: definitionId 流程定义id
     * @return: java.util.List<com.ruoyi.workflow.domain.vo.ProcessNode>
     * @author: gssong
     * @Date: 2021/10/23
     */
    private List<ProcessNode> getProcessNodeAssigneeList(List<ProcessNode> nodeList, String definitionId) {
        List<ActNodeAssignee> actNodeAssignees = iActNodeAssigneeService.getInfoByProcessDefinitionId(definitionId);
        if (CollectionUtil.isNotEmpty(actNodeAssignees)) {
            for (ProcessNode processNode : nodeList) {
                if (CollectionUtil.isEmpty(actNodeAssignees)) {
                    throw new ServiceException("该流程定义未配置，请联系管理员！");
                }
                ActNodeAssignee nodeAssignee = actNodeAssignees.stream().filter(e -> e.getNodeId().equals(processNode.getNodeId())).findFirst().orElse(null);

                //按角色 部门 人员id 等设置查询人员信息
                if (ObjectUtil.isNotNull(nodeAssignee) && StringUtils.isNotBlank(nodeAssignee.getAssigneeId())
                    && nodeAssignee.getBusinessRuleId() == null && StringUtils.isNotBlank(nodeAssignee.getAssignee())) {
                    processNode.setChooseWay(nodeAssignee.getChooseWay());
                    processNode.setAssignee(nodeAssignee.getAssignee());
                    processNode.setAssigneeId(nodeAssignee.getAssigneeId());
                    processNode.setIsShow(nodeAssignee.getIsShow());
                    if (nodeAssignee.getMultiple()) {
                        processNode.setNodeId(nodeAssignee.getMultipleColumn());
                    }
                    processNode.setMultiple(nodeAssignee.getMultiple());
                    processNode.setMultipleColumn(nodeAssignee.getMultipleColumn());
                    //按照业务规则设置查询人员信息
                } else if (ObjectUtil.isNotNull(nodeAssignee) && nodeAssignee.getBusinessRuleId() != null) {
                    ActBusinessRuleVo actBusinessRuleVo = iActBusinessRuleService.queryById(nodeAssignee.getBusinessRuleId());
                    List<String> ruleAssignList = WorkFlowUtils.ruleAssignList(actBusinessRuleVo, processNode.getTaskId(), processNode.getNodeName());
                    processNode.setChooseWay(nodeAssignee.getChooseWay());
                    processNode.setAssignee("");
                    processNode.setAssigneeId(String.join(",", ruleAssignList));
                    processNode.setIsShow(nodeAssignee.getIsShow());
                    processNode.setBusinessRuleId(nodeAssignee.getBusinessRuleId());
                    if (nodeAssignee.getMultiple()) {
                        processNode.setNodeId(nodeAssignee.getMultipleColumn());
                    }
                    processNode.setMultiple(nodeAssignee.getMultiple());
                    processNode.setMultipleColumn(nodeAssignee.getMultipleColumn());
                } else {
                    throw new ServiceException(processNode.getNodeName() + "未配置审批人，请联系管理员！");
                }
            }
        }
        if (CollectionUtil.isNotEmpty(nodeList)) {
            // 去除不需要弹窗选人的节点
            nodeList.removeIf(node -> !node.getIsShow());
        }
        return nodeList;
    }

    /**
     * @Description: 查询所有用户的已办任务
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskFinishVo>
     * @author: gssong
     * @Date: 2021/10/23
     */
    @Override
    public TableDataInfo<TaskFinishVo> getAllTaskFinishByPage(TaskBo req) {
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
            taskFinishVo.setAssigneeId(StringUtils.isNotBlank(hti.getAssignee()) ? Long.valueOf(hti.getAssignee()) : null);
            taskFinishVoList.add(taskFinishVo);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            //办理人集合
            List<Long> assigneeList = taskFinishVoList.stream().map(TaskFinishVo::getAssigneeId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(assigneeList)) {
                List<SysUser> userList = iUserService.selectListUserByIds(assigneeList);
                if (CollectionUtil.isNotEmpty(userList)) {
                    taskFinishVoList.forEach(e -> {
                        SysUser sysUser = userList.stream().filter(t -> t.getUserId().compareTo(e.getAssigneeId()) == 0).findFirst().orElse(null);
                        if (ObjectUtil.isNotEmpty(sysUser)) {
                            e.setAssignee(sysUser.getNickName());
                            e.setAssigneeId(sysUser.getUserId());
                        }
                    });
                }
            }
        }
        return new TableDataInfo<>(taskFinishVoList, total);
    }

    /**
     * @Description: 查询所有用户的待办任务
     * @param: req
     * @return: com.ruoyi.common.core.page.TableDataInfo<com.ruoyi.workflow.domain.vo.TaskWaitingVo>
     * @author: gssong
     * @Date: 2021/10/17
     */
    @Override
    public TableDataInfo<TaskWaitingVo> getAllTaskWaitByPage(TaskBo req) {
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
            taskWaitingVo.setAssigneeId(StringUtils.isNotBlank(task.getAssignee()) ? Long.valueOf(task.getAssignee()) : null);
            taskWaitingVo.setProcessStatus(!task.isSuspended() ? "激活" : "挂起");
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
            //是否会签
            MultiVo multiInstance = WorkFlowUtils.isMultiInstance(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
            taskWaitingVo.setMultiInstance(ObjectUtil.isNotEmpty(multiInstance));
            //查询任务
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
            //可以减签的人员
            if (ObjectUtil.isNotEmpty(multiInstance)) {
                if (multiInstance.getType() instanceof ParallelMultiInstanceBehavior) {
                    taskWaitingVo.setTaskVoList(multiList((TaskEntity) task, tasks, multiInstance.getType(), null));
                } else if (multiInstance.getType() instanceof SequentialMultiInstanceBehavior && StringUtils.isNotBlank(task.getExecutionId())) {
                    List<Long> assigneeList = (List<Long>) runtimeService.getVariable(task.getExecutionId(), multiInstance.getAssigneeList());
                    taskWaitingVo.setTaskVoList(multiList((TaskEntity) task, tasks, multiInstance.getType(), assigneeList));
                }
            }
            list.add(taskWaitingVo);
        }
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> businessKeyList = list.stream().map(TaskWaitingVo::getBusinessKey).collect(Collectors.toList());
            List<ActBusinessStatus> infoList = iActBusinessStatusService.getListInfoByBusinessKey(businessKeyList);
            for (TaskWaitingVo e : list) {
                //认领与归还标识
                List<IdentityLink> identityLinkList = WorkFlowUtils.getCandidateUser(e.getId());
                if (CollectionUtil.isNotEmpty(identityLinkList)) {
                    List<String> collectType = identityLinkList.stream().map(IdentityLink::getType).collect(Collectors.toList());
                    if (StringUtils.isBlank(e.getAssignee()) && collectType.size() > 1 && collectType.contains(ActConstant.CANDIDATE)) {
                        e.setIsClaim(false);
                    } else if (StringUtils.isNotBlank(e.getAssignee()) && collectType.size() > 1 && collectType.contains(ActConstant.CANDIDATE)) {
                        e.setIsClaim(true);
                    }
                }
                //办理人集合
                List<Long> assigneeList = list.stream().map(TaskWaitingVo::getAssigneeId).filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(assigneeList)) {
                    List<SysUser> userList = iUserService.selectListUserByIds(assigneeList);
                    if (CollectionUtil.isNotEmpty(userList)) {
                        SysUser sysUser = userList.stream().filter(t -> StringUtils.isNotBlank(e.getAssignee()) && t.getUserId().compareTo(e.getAssigneeId()) == 0).findFirst().orElse(null);
                        if (ObjectUtil.isNotEmpty(sysUser)) {
                            e.setAssignee(sysUser.getNickName());
                            e.setAssigneeId(sysUser.getUserId());
                        }

                    }
                }
                //业务状态
                if (CollectionUtil.isNotEmpty(infoList)) {
                    ActBusinessStatus businessStatus = infoList.stream().filter(t -> t.getBusinessKey().equals(e.getBusinessKey())).findFirst().orElse(null);
                    if (ObjectUtil.isNotEmpty(businessStatus)) {
                        e.setActBusinessStatus(businessStatus);
                    }
                }
            }
        }
        return new TableDataInfo<>(list, total);
    }

    /**
     * @Description: 驳回审批
     * @param: backProcessBo
     * @return: java.lang.String
     * @author: gssong
     * @Date: 2021/11/6
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String backProcess(BackProcessBo backProcessBo) {

        Task task = taskService.createTaskQuery().taskId(backProcessBo.getTaskId()).taskAssignee(getUserId().toString()).singleResult();
        if (task.isSuspended()) {
            throw new ServiceException("当前任务已被挂起");
        }
        if (ObjectUtil.isNull(task)) {
            throw new ServiceException("当前任务不存在或你不是任务办理人");
        }
        try {
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
            FlowNode targetFlowNode = (FlowNode) bpmnModel.getFlowElement(backProcessBo.getTargetActivityId());
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
            List<SequenceFlow> targetSequenceList = targetSequenceFlow.stream().collect(Collectors
                .collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(e -> e.getTargetFlowElement().getId()))),
                    ArrayList::new));

            curFlowNode.setOutgoingFlows(targetSequenceList);
            // 10. 完成任务
            List<Task> list = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            for (Task t : list) {
                if (backProcessBo.getTaskId().equals(t.getId())) {
                    taskService.addComment(t.getId(), processInstanceId, StringUtils.isNotBlank(backProcessBo.getComment()) ? backProcessBo.getComment() : "驳回");
                    taskService.complete(backProcessBo.getTaskId());
                } else {
                    if(StringUtils.isBlank(t.getParentTaskId())){
                        taskService.complete(t.getId());
                        historyService.deleteHistoricTaskInstance(t.getId());
                        taskMapper.deleteActHiActInstByTaskId(t.getId());
                    }
                }
            }

            // 11. 完成驳回功能后，将当前节点的原出口方向进行恢复
            curFlowNode.setOutgoingFlows(oriSequenceFlows);
            // 判断是否会签
            LambdaQueryWrapper<ActNodeAssignee> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActNodeAssignee::getNodeId,backProcessBo.getTargetActivityId());
            wrapper.eq(ActNodeAssignee::getProcessDefinitionId,task.getProcessDefinitionId());
            ActNodeAssignee actNodeAssignee = iActNodeAssigneeService.getOne(wrapper);
            List<Task> newTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if(ObjectUtil.isNotEmpty(actNodeAssignee)&&!actNodeAssignee.getMultiple()){
                for (Task newTask : newTaskList) {

                    DeleteExecutionCmd executionCmd = new DeleteExecutionCmd(newTask.getExecutionId());
                    managementService.executeCommand(executionCmd);
                    // 取之前的历史办理人
                    List<HistoricTaskInstance> oldTargerTaskList = historyService.createHistoricTaskInstanceQuery()
                        .taskDefinitionKey(newTask.getTaskDefinitionKey()) // 节点id
                        .processInstanceId(processInstanceId)
                        .finished() // 已经完成才是历史
                        .orderByTaskCreateTime().desc() // 最新办理的在最前面
                        .list();
                    if(CollectionUtil.isNotEmpty(oldTargerTaskList)){
                        HistoricTaskInstance oldTargerTask = oldTargerTaskList.get(0);
                        taskService.setAssignee(newTask.getId(), oldTargerTask.getAssignee());
                    }
                }
            }

            // 13. 删除驳回后的流程节点
            ActTaskNode actTaskNode = iActTaskNodeService.getListByInstanceIdAndNodeId(task.getProcessInstanceId(), backProcessBo.getTargetActivityId());
            if (ObjectUtil.isNotNull(actTaskNode) && actTaskNode.getOrderNo() == 0) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                List<Task> newList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                for (Task ta : newList) {
                    Map<String, Object> variables = new HashMap<>();
                    taskService.setVariables(ta.getId(), variables);
                }
                iActBusinessStatusService.updateState(processInstance.getBusinessKey(), BusinessStatusEnum.BACK);
            }
            iActTaskNodeService.deleteBackTaskNode(processInstanceId, backProcessBo.getTargetActivityId());
            //发送站内信
            WorkFlowUtils.sendMessage(backProcessBo.getSendMessage(), processInstanceId);
            return processInstanceId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("驳回失败:" + e.getMessage());
        }
    }

    /**
     * @Description: 获取并行网关下一步审批节点信息
     * @param: processDefinitionId 流程定义id
     * @param: targetActivityId 驳回的节点id
     * @param: task 任务
     * @return: java.util.List<java.lang.String>
     * @author: gssong
     * @Date: 2022/4/10
     */
    public List<String> getPrevUserNodeList(String processDefinitionId, String targetActivityId, Task task) {
        List<String> nodeListId = new ArrayList<>();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        FlowElement flowElement = bpmnModel.getFlowElement(targetActivityId);
        List<SequenceFlow> outgoingFlows = ((FlowNode) flowElement).getIncomingFlows();
        for (SequenceFlow outgoingFlow : outgoingFlows) {
            FlowElement sourceFlowElement = outgoingFlow.getSourceFlowElement();
            //并行网关
            if (sourceFlowElement instanceof ParallelGateway) {
                List<SequenceFlow> parallelGatewayOutgoingFlow = ((ParallelGateway) sourceFlowElement).getOutgoingFlows();
                for (SequenceFlow sequenceFlow : parallelGatewayOutgoingFlow) {
                    FlowElement element = sequenceFlow.getTargetFlowElement();
                    if (element instanceof UserTask) {
                        nodeListId.add(element.getId());
                    }
                }
                //包容网关
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
     * @Description: 获取历史任务节点，用于驳回功能
     * @param: processInstId
     * @return: java.util.List<com.ruoyi.workflow.domain.ActTaskNode>
     * @author: gssong
     * @Date: 2021/11/6
     */
    @Override
    public List<ActTaskNode> getBackNodes(String processInstId) {
        return iActTaskNodeService.getListByInstanceId(processInstId).stream().filter(ActTaskNode::getIsBack).collect(Collectors.toList());
    }

    /**
     * @Description: 委派任务
     * @param: taskREQ
     * @return: java.lang.Boolean
     * @author: gssong
     * @Date: 2022/3/4
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delegateTask(DelegateBo delegateBo) {
        if (StringUtils.isBlank(delegateBo.getDelegateUserId())) {
            throw new ServiceException("请选择委托人");
        }
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(delegateBo.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            throw new ServiceException(ActConstant.MESSAGE_CURRENT_TASK_IS_NULL);
        }
        try {
            TaskEntity newTask = WorkFlowUtils.createNewTask(task, new Date());
            taskService.addComment(newTask.getId(), task.getProcessInstanceId(), "【" + LoginHelper.getUsername() + "】委派给【" + delegateBo.getDelegateUserName() + "】");
            //委托任务
            taskService.delegateTask(delegateBo.getTaskId(), delegateBo.getDelegateUserId());
            //办理生成的任务记录
            taskService.complete(newTask.getId());
            ActHiTaskInst actHiTaskInst = new ActHiTaskInst();
            actHiTaskInst.setId(task.getId());
            actHiTaskInst.setStartTime(new Date());
            iActHiTaskInstService.updateById(actHiTaskInst);
            //发送站内信
            WorkFlowUtils.sendMessage(delegateBo.getSendMessage(), task.getProcessInstanceId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @Description: 转办任务
     * @param: transmitBo
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/3/13 13:18
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> transmitTask(TransmitBo transmitBo) {
        Task task = taskService.createTaskQuery().taskId(transmitBo.getTaskId())
            .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        if (ObjectUtil.isEmpty(task)) {
            return R.fail(ActConstant.MESSAGE_CURRENT_TASK_IS_NULL);
        }
        try {
            TaskEntity newTask = WorkFlowUtils.createNewTask(task, new Date());
            taskService.addComment(newTask.getId(), task.getProcessInstanceId(),
                StringUtils.isNotBlank(transmitBo.getComment()) ? transmitBo.getComment() : LoginHelper.getUsername() + "转办了任务");
            taskService.complete(newTask.getId());
            taskService.setAssignee(task.getId(), transmitBo.getTransmitUserId());
            //发送站内信
            WorkFlowUtils.sendMessage(transmitBo.getSendMessage(), task.getProcessInstanceId());
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }

    /**
     * @Description: 会签任务加签
     * @param: addMultiBo
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/4/15 13:06
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> addMultiInstanceExecution(AddMultiBo addMultiBo) {
        Task task;
        if (LoginHelper.isAdmin()) {
            task = taskService.createTaskQuery().taskId(addMultiBo.getTaskId()).singleResult();
        } else {
            task = taskService.createTaskQuery().taskId(addMultiBo.getTaskId())
                .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        }
        if (ObjectUtil.isEmpty(task) && !LoginHelper.isAdmin()) {
            throw new ServiceException(ActConstant.MESSAGE_CURRENT_TASK_IS_NULL);
        }
        if (task.isSuspended()) {
            throw new ServiceException(ActConstant.MESSAGE_SUSPENDED);
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processInstanceId = task.getProcessInstanceId();
        String processDefinitionId = task.getProcessDefinitionId();
        MultiVo multiVo = WorkFlowUtils.isMultiInstance(processDefinitionId, taskDefinitionKey);
        if (ObjectUtil.isEmpty(multiVo)) {
            throw new ServiceException("当前环节不是会签节点");
        }
        try {
            if (multiVo.getType() instanceof ParallelMultiInstanceBehavior) {
                for (Long assignee : addMultiBo.getAssignees()) {
                    AddMultiInstanceExecutionCmd addMultiInstanceExecutionCmd = new AddMultiInstanceExecutionCmd(taskDefinitionKey, processInstanceId, Collections.singletonMap(multiVo.getAssignee(), assignee));
                    managementService.executeCommand(addMultiInstanceExecutionCmd);
                }
            } else if (multiVo.getType() instanceof SequentialMultiInstanceBehavior) {
                AddSequenceMultiInstanceCmd addSequenceMultiInstanceCmd = new AddSequenceMultiInstanceCmd(task.getExecutionId(), multiVo.getAssigneeList(), addMultiBo.getAssignees());
                managementService.executeCommand(addSequenceMultiInstanceCmd);
            }
            List<String> assigneeNames = addMultiBo.getAssigneeNames();
            String username = LoginHelper.getUsername();
            TaskEntity newTask = WorkFlowUtils.createNewTask(task, new Date());
            taskService.addComment(newTask.getId(), processInstanceId, username + "加签【" + String.join(",", assigneeNames) + "】");
            taskService.complete(newTask.getId());
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail(e.getMessage());
        }
    }

    /**
     * @Description: 会签任务减签
     * @param: deleteMultiBo
     * @return: com.ruoyi.common.core.domain.R<java.lang.Boolean>
     * @author: gssong
     * @Date: 2022/4/16 10:59
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> deleteMultiInstanceExecution(DeleteMultiBo deleteMultiBo) {
        Task task;
        if (LoginHelper.isAdmin()) {
            task = taskService.createTaskQuery().taskId(deleteMultiBo.getTaskId()).singleResult();
        } else {
            task = taskService.createTaskQuery().taskId(deleteMultiBo.getTaskId())
                .taskCandidateOrAssigned(LoginHelper.getUserId().toString()).singleResult();
        }
        if (ObjectUtil.isEmpty(task) && !LoginHelper.isAdmin()) {
            return R.fail(ActConstant.MESSAGE_CURRENT_TASK_IS_NULL);
        }
        if (task.isSuspended()) {
            return R.fail(ActConstant.MESSAGE_SUSPENDED);
        }
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String processInstanceId = task.getProcessInstanceId();
        String processDefinitionId = task.getProcessDefinitionId();
        MultiVo multiVo = WorkFlowUtils.isMultiInstance(processDefinitionId, taskDefinitionKey);
        if (ObjectUtil.isEmpty(multiVo)) {
            return R.fail("当前环节不是会签节点");
        }
        try {
            if (multiVo.getType() instanceof ParallelMultiInstanceBehavior) {
                for (String executionId : deleteMultiBo.getExecutionIds()) {
                    DeleteMultiInstanceExecutionCmd deleteMultiInstanceExecutionCmd = new DeleteMultiInstanceExecutionCmd(executionId,false);
                    managementService.executeCommand(deleteMultiInstanceExecutionCmd);
                }
                for (String taskId : deleteMultiBo.getTaskIds()) {
                    historyService.deleteHistoricTaskInstance(taskId);
                }
            } else if (multiVo.getType() instanceof SequentialMultiInstanceBehavior) {
                DeleteSequenceMultiInstanceCmd deleteSequenceMultiInstanceCmd = new DeleteSequenceMultiInstanceCmd(task.getAssignee(), task.getExecutionId(), multiVo.getAssigneeList(), deleteMultiBo.getAssigneeIds());
                managementService.executeCommand(deleteSequenceMultiInstanceCmd);
            }
            List<String> assigneeNames = deleteMultiBo.getAssigneeNames();
            String username = LoginHelper.getUsername();
            TaskEntity newTask = WorkFlowUtils.createNewTask(task, new Date());
            taskService.addComment(newTask.getId(), processInstanceId, username + "减签【" + String.join(",", assigneeNames) + "】");
            taskService.complete(newTask.getId());
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @Description: 修改办理人
     * @param: updateAssigneeBo
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/7/17 13:35
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateAssignee(UpdateAssigneeBo updateAssigneeBo) {
        List<Task> list = taskService.createNativeTaskQuery().sql("select * from act_ru_task where id_ in " + getInParam(updateAssigneeBo.getTaskIdList())).list();
        if (CollectionUtil.isEmpty(list)) {
            return R.fail("办理失败，任务不存在");
        }
        try {
            for (Task task : list) {
                taskService.setAssignee(task.getId(), updateAssigneeBo.getUserId());
            }
            return R.ok();
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * @Description: 拼接单引号, 到数据库后台用in查询.
     * @param: param
     * @return: java.lang.String
     * @author: gssong
     * @Date: 2022/7/22 12:17
     */
    private String getInParam(List<String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < param.size(); i++) {
            sb.append("'").append(param.get(i)).append("'");
            if (i != param.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * @Description: 查询流程变量
     * @param: taskId
     * @return: com.ruoyi.common.core.domain.R<java.util.List < com.ruoyi.workflow.domain.vo.VariableVo>>
     * @author: gssong
     * @Date: 2022/7/23 14:33
     */
    @Override
    public R<List<VariableVo>> getProcessInstVariable(String taskId) {
        List<VariableVo> variableVoList = new ArrayList<>();
        Map<String, VariableInstance> variableInstances = taskService.getVariableInstances(taskId);
        if (CollectionUtil.isNotEmpty(variableInstances)) {
            for (Map.Entry<String, VariableInstance> entry : variableInstances.entrySet()) {
                VariableVo variableVo = new VariableVo();
                variableVo.setKey(entry.getKey());
                variableVo.setValue(entry.getValue().getValue().toString());
                variableVoList.add(variableVo);
            }
        }
        return R.ok(variableVoList);
    }

    /**
     * @Description: 修改审批意见
     * @param: commentId
     * @param: comment
     * @return: com.ruoyi.common.core.domain.R<java.lang.Void>
     * @author: gssong
     * @Date: 2022/7/24 13:28
     */
    @Override
    public R<Void> editComment(String commentId, String comment) {
        try {
            taskMapper.editComment(commentId, comment);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail();
        }
    }
}
