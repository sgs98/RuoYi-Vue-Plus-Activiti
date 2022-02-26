package com.ruoyi.workflow.activiti.cmd;

import com.ruoyi.common.helper.LoginHelper;
import org.activiti.engine.impl.cmd.NeedsActiveTaskCmd;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.*;

public class DeleteTaskCommand extends NeedsActiveTaskCmd<String> {

    /**
     * 当前任务对应的 act_ru_task 任务id
     */
    public DeleteTaskCommand(String taskId) {
        super(taskId);
    }

    @Override
    protected String execute(CommandContext commandContext, TaskEntity task) {
        TaskEntityManager taskEntityManager = commandContext.getTaskEntityManager();
        // 删除当前任务，不会把执行表中的 is_active_更新为0，会将任务数据更新到历史任务实例表中
        taskEntityManager.deleteTask(task,
            "【"+task.getName()+"】任务被" + LoginHelper.getUsername() + "】驳回",
            false, false);
        return null;
    }
}
