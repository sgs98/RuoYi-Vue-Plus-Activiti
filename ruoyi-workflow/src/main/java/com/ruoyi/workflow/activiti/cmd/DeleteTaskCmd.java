package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.collection.CollectionUtil;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DeleteTaskCmd extends NeedsActiveTaskCmd<Void> {

    public DeleteTaskCmd(String taskId) {
        super(taskId);
    }

    @Override
    protected Void execute(CommandContext commandContext, TaskEntity task) {
        //获得用到的Manager
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        VariableInstanceEntityManager variableInstanceEntityManager = commandContext.getVariableInstanceEntityManager();
        //获得当前流程处于的Task信息
        TaskEntity taskEntity = taskEntityManager.findById(task.getId());
        //获得流程实例信息
        ExecutionEntity executionEntity = executionEntityManager.findById(taskEntity.getExecutionId());
        ExecutionEntity parentExecutionEntity = executionEntityManager.findById(executionEntity.getParentId());
        //所有子级实例
        List<ExecutionEntity> childExecutionList = executionEntityManager.findChildExecutionsByParentExecutionId(parentExecutionEntity.getId());
        List<ExecutionEntity> childExecutions = childExecutionList.stream().filter(e -> e.getId().equals(taskEntity.getExecutionId())).collect(Collectors.toList());
        //设置需要删除参数的流程实例
        Set<String> executionIds = new HashSet<>();
        for (ExecutionEntity childExecutionEntity : childExecutions) {
            executionIds.add(childExecutionEntity.getId());
        }
        //删除相关的参数
        if(CollectionUtil.isNotEmpty(executionIds)){
            List<VariableInstanceEntity> variableInstanceEntities = variableInstanceEntityManager.findVariableInstancesByExecutionIds(executionIds);
            for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
                variableInstanceEntityManager.delete(variableInstanceEntity, true);
            }
        }
        taskEntityManager.deleteTasksByProcessInstanceId(taskEntity.getProcessInstanceId(), "删除任务", true);
        for (ExecutionEntity child : childExecutions) {
            //删除相关的办理人
            identityLinkEntityManager.deleteIdentityLink(executionEntity, null, null, null);
            //删除流程实例
            executionEntityManager.delete(child);
        }
        return null;
    }
}
