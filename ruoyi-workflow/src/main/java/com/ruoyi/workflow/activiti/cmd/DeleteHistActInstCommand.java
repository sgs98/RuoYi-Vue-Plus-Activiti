package com.ruoyi.workflow.activiti.cmd;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.activiti.engine.impl.persistence.entity.HistoricActivityInstanceEntityManager;

import java.io.Serializable;
import java.util.List;

public class DeleteHistActInstCommand implements Command<String>, Serializable {

    private String executionId;

    private List<HistoricActivityInstance> list;

    public DeleteHistActInstCommand(String executionId, List<HistoricActivityInstance> list) {
        this.executionId = executionId;
        this.list = list;
    }
    @Override
    public String execute(CommandContext commandContext) {
        HistoricActivityInstanceEntityManager manager = commandContext.getHistoricActivityInstanceEntityManager();
        List<HistoricActivityInstance> list = this.list;
        for (HistoricActivityInstance instance : list) {
            if(executionId.equals(instance.getExecutionId())){
                manager.delete((HistoricActivityInstanceEntity) instance);
            }
        }
        return null;
    }
}
