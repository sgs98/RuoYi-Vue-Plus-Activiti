package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DeleteExecutionCmd implements Command<Void> {

    /**
     * 执行id
     */
    private String executionId;

    public DeleteExecutionCmd(String executionId) {
        this.executionId=executionId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        //获得用到的Manager
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        VariableInstanceEntityManager variableInstanceEntityManager = commandContext.getVariableInstanceEntityManager();
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        if(ObjectUtil.isNotEmpty(executionEntity)){
            //设置需要删除参数的流程实例
            Set<String> executionIds = new HashSet<>();
            executionIds.add(executionId);
            //删除相关的参数
            if(CollectionUtil.isNotEmpty(executionIds)){
                List<VariableInstanceEntity> variableInstanceEntities = variableInstanceEntityManager.findVariableInstancesByExecutionIds(executionIds);
                for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
                    variableInstanceEntityManager.delete(variableInstanceEntity, true);
                }
                //删除流程实例
                executionEntityManager.delete(executionId);
            }
        }
        return null;
    }
}
