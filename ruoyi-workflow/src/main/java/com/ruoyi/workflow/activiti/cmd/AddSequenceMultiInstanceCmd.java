package com.ruoyi.workflow.activiti.cmd;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruoyi.workflow.common.constant.ActConstant.NUMBER_OF_INSTANCES;

/**
 * @program: ruoyi-vue-plus
 * @description: 串行加签
 * @author: gssong
 * @created: 2022/4/20 20:57
 */
public class AddSequenceMultiInstanceCmd implements Command<Void> {

    /**
     * 执行id
     */
    private final String executionId;

    /**
     * 会签人员集合KEY
     */
    private final String assigneeList;

    /**
     * 加签人员
     */
    private final List<Long> assignees;

    public AddSequenceMultiInstanceCmd(String executionId, String assigneeList, List<Long> assignees) {
        this.executionId = executionId;
        this.assigneeList = assigneeList;
        this.assignees = assignees;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        ExecutionEntity entity = executionEntityManager.findById(executionId);
        //多实例任务总数加assignees.size()
        Integer nrOfInstances = (Integer) entity.getVariable(NUMBER_OF_INSTANCES);
        entity.setVariable(NUMBER_OF_INSTANCES, nrOfInstances + assignees.size());
        // 设置流程变量
        List<Long> userIds = (List) entity.getVariable(assigneeList);
        userIds.addAll(assignees);
        Map<String, Object> variables = new HashMap<>();
        variables.put(assigneeList, userIds);
        entity.setVariables(variables);
        return null;
    }
}
