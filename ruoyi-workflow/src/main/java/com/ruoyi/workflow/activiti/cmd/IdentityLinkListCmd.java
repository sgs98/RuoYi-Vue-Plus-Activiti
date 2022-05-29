package com.ruoyi.workflow.activiti.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntity;
import org.activiti.engine.impl.persistence.entity.IdentityLinkEntityManager;

import java.util.List;


/**
 * @program: ruoyi-vue-plus
 * @description: 查询人员信息
 * @author: gssong
 * @created: 2022/5/28 16:26
 */
public class IdentityLinkListCmd implements Command<List<IdentityLinkEntity>> {

    private String taskId;

    public IdentityLinkListCmd(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public List<IdentityLinkEntity> execute(CommandContext commandContext) {
        IdentityLinkEntityManager identityLinkEntityManager = commandContext.getIdentityLinkEntityManager();
        return identityLinkEntityManager.findIdentityLinksByTaskId(taskId);
    }
}
