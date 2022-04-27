package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: ruoyi-vue-plus
 * @description: 删除执行实例
 * @author: gssong
 * @created: 2022/04/26 21:58
 */
public class DeleteExecutionCmd implements Command<Void> {
    /**
     * 执行id
     */
    private String executionId;

    public DeleteExecutionCmd(String executionId) {
        this.executionId = executionId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        VariableInstanceEntityManager variableInstanceEntityManager = commandContext.getVariableInstanceEntityManager();
        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        // 获取当前执行数据
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        // 通过当前执行数据的父执行，查询所有子执行数据
        if(ObjectUtil.isNotEmpty(executionEntity)){
            List<ExecutionEntity> allChildrenExecution = executionEntityManager.collectChildren(executionEntity.getParent());
            List<ExecutionEntity> childrenCollect = allChildrenExecution.stream().filter(e -> !e.getId().equals(executionId)).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(childrenCollect)){
                childrenCollect.forEach(e->{
                    executionEntityManager.delete(e.getId());
                    //删除相关的办理人
                    identityLinkEntityManager.deleteIdentityLink(e, null, null, null);
                });
                childrenCollect.stream().map(ExecutionEntity::getId).collect(Collectors.toSet())
                //删除相关的参数
                List<VariableInstanceEntity> variableInstanceEntities = variableInstanceEntityManager.findVariableInstancesByExecutionIds(executionIds);
                for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
                    variableInstanceEntityManager.delete(variableInstanceEntity, true);
                }
            }
        }

        return null;
    }
}
