package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.util.ObjectUtil;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;

import java.io.Serializable;
import java.util.List;

public class DeleteExecutionCommand implements Command<String>, Serializable {
    /**
     * 当前任务对应的 act_ru_execution 执行id
     */
    private String executionId;

    public DeleteExecutionCommand(String executionId) {
        this.executionId = executionId;
    }


    @Override
    public String execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        // 获取当前执行数据
        ExecutionEntity executionEntity = executionEntityManager.findById(executionId);
        // 通过当前执行数据的父执行，查询所有子执行数据
        if(ObjectUtil.isNotEmpty(executionEntity)){
            List<ExecutionEntity> allChildrenExecution =
                executionEntityManager.collectChildren(executionEntity.getParent());
            for (ExecutionEntity entity : allChildrenExecution) {
                if(!entity.isActive()) {
                    // 将is_active_=0的执行数据删除，不然重复流向并行任务后，重新审批并行任务，
                    // 只要有一个节点完成（就是当前有2个并行任务，驳回前已经完成了1个任务），则会并行网关就会汇聚往后走
                    executionEntityManager.delete(entity);
                }
            }
        }
        return null;
    }
}
