package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.List;

/**
 * @program: ruoyi-vue-plus
 * @description: 单节点驳回到并行网关
 * @author: gssong
 * @created: 2022/4/29 14:42:35
 */
public class MoveActivityIdToActivityIdsCmd implements Command<Void> {
    /**
     * 当前任务id
     */
    private String currentTaskId;

    /**
     * 驳回节点id
     */
    private List<String> targetNodeIds;

    public MoveActivityIdToActivityIdsCmd(String currentTaskId, List<String> targetNodeIds) {
        this.currentTaskId = currentTaskId;
        this.targetNodeIds = targetNodeIds;
    }
    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        TaskEntity taskEntity = taskEntityManager.findById(currentTaskId);
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        Process process = ProcessDefinitionUtil.getProcess(taskEntity.getProcessDefinitionId());
        for (String targetNodeId : targetNodeIds) {
            FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
            taskEntityManager.deleteTask(taskEntity,"删除任务",true,true);
            executionEntity.setCurrentFlowElement(targetFlowElement);
            commandContext.getAgenda().planContinueProcessOperation(executionEntity);
        }
        return null;
    }
}
