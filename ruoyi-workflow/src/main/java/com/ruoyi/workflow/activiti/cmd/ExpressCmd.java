package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.Condition;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.el.UelExpressionCondition;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.impl.util.condition.ConditionUtil;

/**
 * @program: ruoyi-vue-plus
 * @description: 校验分支网关
 * @author: gssong
 * @created: 2021/10/10 11:13
 */
public class ExpressCmd implements Command<Boolean> {

    private final SequenceFlow sequenceFlow;
    private final ExecutionEntityImpl executionEntity;

    public ExpressCmd(SequenceFlow sequenceFlow, ExecutionEntityImpl executionEntity) {
        this.sequenceFlow = sequenceFlow;
        this.executionEntity = executionEntity;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        return ConditionUtil.hasTrueCondition(sequenceFlow, executionEntity);
    }
}
