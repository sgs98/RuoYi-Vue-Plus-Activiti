package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.collection.CollectionUtil;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeleteVariableCmd implements Command<Void> {

    /**
     * 执行id
     */
    private String executionId;

    /**
     * 是否是执行实例父级id
     */
    private Boolean isParent;

    /**
     * 是否删除流程实例
     */
    private Boolean isDelete;

    public DeleteVariableCmd(String executionId,Boolean isParent,Boolean isDelete) {
        this.executionId=executionId;
        this.isParent=isParent;
        this.isDelete=isDelete;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        //获得用到的Manager
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        VariableInstanceEntityManager variableInstanceEntityManager = commandContext.getVariableInstanceEntityManager();
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
            if(isDelete){
                if(isParent){
                    executionEntityManager.findChildExecutionsByParentExecutionId(executionId);
                }else{
                    executionEntityManager.delete(executionId);
                }
            }
        }
        return null;
    }
}
