package com.ruoyi.workflow.activiti.cmd;

import com.ruoyi.common.utils.spring.SpringUtils;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;

import java.io.Serializable;
import java.util.Map;

/**
 * @program: ruoyi-vue-plus
 * @description: 校验流程变量
 * @author: gssong
 * @created: 2022/8/22 18:26
 */
public class ExpressCheckCmd implements Command<Boolean>, Serializable {


    private final String processInstanceId;

    private final String conditionExpression;

    private final Map<String, Object> variableMap;

    public ExpressCheckCmd(String processInstanceId, String conditionExpression, Map<String, Object> variableMap) {
        this.processInstanceId = processInstanceId;
        this.conditionExpression = conditionExpression;
        this.variableMap = variableMap;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = SpringUtils.getBean(ProcessEngineConfigurationImpl.class);
        RuntimeService runtimeService = SpringUtils.getBean(RuntimeService.class);
        Expression expression = processEngineConfiguration.getExpressionManager().createExpression(this.conditionExpression);
        ExecutionEntity executionEntity = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(this.processInstanceId).includeProcessVariables().singleResult();
        executionEntity.setVariables(variableMap);
        Object value = expression.getValue(executionEntity);
        return value != null && "true".equals(value.toString());
    }

}
