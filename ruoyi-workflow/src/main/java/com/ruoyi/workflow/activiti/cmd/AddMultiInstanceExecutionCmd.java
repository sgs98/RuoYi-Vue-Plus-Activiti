package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;
import org.activiti.engine.runtime.Execution;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.ruoyi.workflow.common.constant.ActConstant.NUMBER_OF_INSTANCES;

public class AddMultiInstanceExecutionCmd  implements Command<Execution>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String activityId;
    protected String parentExecutionId;
    protected Map<String, Object> executionVariables;

    public AddMultiInstanceExecutionCmd(String activityId, String parentExecutionId, Map<String, Object> executionVariables) {
        this.activityId = activityId;
        this.parentExecutionId = parentExecutionId;
        this.executionVariables = executionVariables;
    }


    @Override
    public Execution execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();

        ExecutionEntity miExecution = searchForMultiInstanceActivity(activityId, parentExecutionId, executionEntityManager);

        if (miExecution == null) {
            throw new RuntimeException("No multi instance execution found for activity id " + activityId);
        }

        ExecutionEntity childExecution = executionEntityManager.createChildExecution(miExecution);
        childExecution.setCurrentFlowElement(miExecution.getCurrentFlowElement());

        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(miExecution.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(miExecution.getActivityId());
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = miActivityElement.getLoopCharacteristics();

        Integer currentNumberOfInstances = (Integer) miExecution.getVariable(NUMBER_OF_INSTANCES);
        miExecution.setVariableLocal(NUMBER_OF_INSTANCES, currentNumberOfInstances + 1);

        if (executionVariables != null) {
            childExecution.setVariablesLocal(executionVariables);
        }

        if (!multiInstanceLoopCharacteristics.isSequential()) {
            miExecution.setActive(true);
            miExecution.setScope(false);

            childExecution.setCurrentFlowElement(miActivityElement);
            commandContext.getAgenda().planOperation(new CustomizedContinueMultiInstanceOperation(commandContext, childExecution, miExecution, currentNumberOfInstances));
        }

        return childExecution;
    }

    protected ExecutionEntity searchForMultiInstanceActivity(String activityId, String parentExecutionId, ExecutionEntityManager executionEntityManager) {
        List<ExecutionEntity> childExecutions = executionEntityManager.findChildExecutionsByParentExecutionId(parentExecutionId);

        ExecutionEntity miExecution = null;
        for (ExecutionEntity childExecution : childExecutions) {
            if (activityId.equals(childExecution.getActivityId()) && childExecution.isMultiInstanceRoot()) {
                if (miExecution != null) {
                    throw new ActivitiException("Multiple multi instance executions found for activity id " + activityId);
                }
                miExecution = childExecution;
            }

            ExecutionEntity childMiExecution = searchForMultiInstanceActivity(activityId, childExecution.getId(), executionEntityManager);
            if (childMiExecution != null) {
                if (miExecution != null) {
                    throw new ActivitiException("Multiple multi instance executions found for activity id " + activityId);
                }
                miExecution = childMiExecution;
            }
        }

        return miExecution;
    }
}
