package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: ruoyi-vue-plus
 * @description: 会签节点驳回单个普通节点
 * @author: gssong
 * @created: 2022/04/29 13:13
 */
public class MoveMultiInstanceSingleCmd implements Command<Void> {
    /**
     * 当前任务id
     */
    private String currentTaskId;

    /**
     * 驳回节点id
     */
    private String targetNodeId;

    public MoveMultiInstanceSingleCmd(String currentTaskId, String targetNodeId) {
        this.currentTaskId = currentTaskId;
        this.targetNodeId = targetNodeId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        //获得用到的Manager
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        VariableInstanceEntityManager variableInstanceEntityManager = commandContext.getVariableInstanceEntityManager();
        //获得当前流程处于的Task信息
        TaskEntity taskEntity = taskEntityManager.findById(currentTaskId);
        //获得流程实例信息
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        ExecutionEntity parentExecutionEntity = executionEntityManager.findById(executionEntity.getParentId());
        List<ExecutionEntity> childExecutionEntities = executionEntityManager.findChildExecutionsByParentExecutionId(parentExecutionEntity.getId());
        //设置需要删除参数的流程实例
        Set<String> executionIds = new HashSet<>();
        executionIds.add(parentExecutionEntity.getId());
        for (ExecutionEntity childExecutionEntity : childExecutionEntities) {
            executionIds.add(childExecutionEntity.getId());
        }
        //获得流程定义信息
        Process process = ProcessDefinitionUtil.getProcess(executionEntity.getProcessDefinitionId());
        //删除相关的办理人
        identityLinkEntityManager.deleteIdentityLink(executionEntity, null, null, null);
        identityLinkEntityManager.deleteIdentityLink(parentExecutionEntity, null, null, null);
        //删除相关的参数
        List<VariableInstanceEntity> variableInstanceEntities = variableInstanceEntityManager.findVariableInstancesByExecutionIds(executionIds);
        for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
            variableInstanceEntityManager.delete(variableInstanceEntity, true);
        }
        //删除Task
        taskEntityManager.deleteTasksByProcessInstanceId(taskEntity.getProcessInstanceId(), "删除子节点", true);
        //删除子流程的流程实例
        executionEntityManager.deleteChildExecutions(parentExecutionEntity, "", true);
        //移动节点
        FlowElement targetFlowElement = process.getFlowElement(targetNodeId);
        parentExecutionEntity.setCurrentFlowElement(targetFlowElement);
        commandContext.getAgenda().planContinueProcessInCompensation(parentExecutionEntity);

        return null;
    }
}
