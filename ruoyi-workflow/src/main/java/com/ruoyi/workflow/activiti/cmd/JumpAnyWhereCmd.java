package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.history.HistoryManager;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

public class JumpAnyWhereCmd implements Command {
    private String taskId;

    private String targetNodeId;

    private RepositoryService repositoryService;

    public JumpAnyWhereCmd(String taskId, String targetNodeId,RepositoryService repositoryService) {
        this.taskId = taskId;
        this.targetNodeId = targetNodeId;
        this.repositoryService = repositoryService;
    }

    public Object execute(CommandContext commandContext) {
        //获取任务实例管理类
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        //获取当前任务实例
        TaskEntity currentTask = taskEntityManager.findById(taskId);

        //获取当前节点的执行实例
        ExecutionEntity execution = currentTask.getExecution();
        String executionId = execution.getId();

        //获取流程定义id
        String processDefinitionId = execution.getProcessDefinitionId();
        //获取目标节点
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        //通过任务节点id，来获取当前节点信息
        FlowElement flowElement = bpmnModel.getFlowElement(targetNodeId);

        //获取历史管理
        HistoryManager historyManager = commandContext.getHistoryManager();

        //通知当前活动结束(更新act_hi_actinst)
        historyManager.recordActivityEnd(execution,"jump to userTask1");
        //通知任务节点结束(更新act_hi_taskinst)
        historyManager.recordTaskEnd(taskId,"jump to userTask1");

        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        identityLinkEntityManager.deleteIdentityLinksByTaskId(taskId);
        //删除正在执行的当前任务
        taskEntityManager.delete(taskId);

        //此时设置执行实例的当前活动节点为目标节点
        execution.setCurrentFlowElement(flowElement);

        //向operations中压入继续流程的操作类
        //commandContext.getAgenda().planContinueProcessOperation(execution);
        commandContext.getAgenda().planContinueMultiInstanceOperation(execution);

        return null;
    }
}

