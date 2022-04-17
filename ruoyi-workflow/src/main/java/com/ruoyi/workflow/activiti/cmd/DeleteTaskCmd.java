package com.ruoyi.workflow.activiti.cmd;

import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntityManager;

/**
 * @program: ruoyi-vue-plus
 * @description: 删除运行中的流程
 * @author: gssong
 * @created: 2022/04/10 11:13
 */
public class DeleteTaskCmd extends NeedsActiveTaskCmd<String> {
    public DeleteTaskCmd(String taskId) {
        super(taskId);
    }

    @Override
    protected String execute(CommandContext commandContext, TaskEntity task) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        ExecutionEntity execution = task.getExecution();
        taskEntityManager.deleteTask(task,"删除任务",false,false);
        return execution.getId();
    }
}
