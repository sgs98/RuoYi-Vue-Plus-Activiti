package com.ruoyi.workflow.activiti.cmd;

import org.activiti.bpmn.model.Activity;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.io.Serializable;

import static com.ruoyi.workflow.common.constant.ActConstant.NUMBER_OF_COMPLETED_INSTANCES;
import static com.ruoyi.workflow.common.constant.ActConstant.NUMBER_OF_INSTANCES;
/**
 * @author Tijs Rademakers
 */
public class DeleteMultiInstanceExecutionCmd implements Command<Void>, Serializable {
    private static final long serialVersionUID = 1L;

    protected String executionId;
    protected boolean executionIsCompleted;

    public DeleteMultiInstanceExecutionCmd(String executionId, boolean executionIsCompleted) {
        this.executionId = executionId;
        this.executionIsCompleted = executionIsCompleted;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        ExecutionEntity execution = executionEntityManager.findById(executionId);

        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(execution.getProcessDefinitionId());
        Activity miActivityElement = (Activity) bpmnModel.getFlowElement(execution.getActivityId());
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = miActivityElement.getLoopCharacteristics();

        if (miActivityElement.getLoopCharacteristics() == null) {
            throw new RuntimeException("No multi instance execution found for execution id " + executionId);
        }

        if (!(miActivityElement.getBehavior() instanceof MultiInstanceActivityBehavior)) {
            throw new RuntimeException("No multi instance behavior found for execution id " + executionId);
        }

        ExecutionEntity miExecution = getMultiInstanceRootExecution(execution);
        executionEntityManager.deleteChildExecutions(execution, "Delete MI execution", false);
        executionEntityManager.deleteExecutionAndRelatedData(execution, "Delete MI execution", false);

        int loopCounter = 0;
        if (multiInstanceLoopCharacteristics.isSequential()) {
            SequentialMultiInstanceBehavior miBehavior = (SequentialMultiInstanceBehavior) miActivityElement.getBehavior();
            loopCounter = getLoopVariable(execution, miBehavior.getCollectionElementIndexVariable());
        }

        if (executionIsCompleted) {
            Integer numberOfCompletedInstances = (Integer) miExecution.getVariable(NUMBER_OF_COMPLETED_INSTANCES);
            miExecution.setVariableLocal(NUMBER_OF_COMPLETED_INSTANCES, numberOfCompletedInstances + 1);
            loopCounter++;

        } else {
            Integer currentNumberOfInstances = (Integer) miExecution.getVariable(NUMBER_OF_INSTANCES);
            miExecution.setVariableLocal(NUMBER_OF_INSTANCES, currentNumberOfInstances - 1);
        }

        ExecutionEntity childExecution = executionEntityManager.createChildExecution(miExecution);
        childExecution.setCurrentFlowElement(miExecution.getCurrentFlowElement());

        if (multiInstanceLoopCharacteristics.isSequential()) {
            continueSequentialMultiInstance(commandContext, childExecution, loopCounter, miExecution, miActivityElement);
        }

        return null;
    }

    protected ExecutionEntity getMultiInstanceRootExecution(ExecutionEntity executionEntity) {
        ExecutionEntity multiInstanceRootExecution = null;
        ExecutionEntity currentExecution = executionEntity;
        while (currentExecution != null && multiInstanceRootExecution == null && currentExecution.getParent() != null) {
            if (currentExecution.isMultiInstanceRoot()) {
                multiInstanceRootExecution = currentExecution;
            } else {
                currentExecution = currentExecution.getParent();
            }
        }
        return multiInstanceRootExecution;
    }

    protected Integer getLoopVariable(DelegateExecution execution, String variableName) {
        Object value = execution.getVariableLocal(variableName);

        for(DelegateExecution parent = execution.getParent(); value == null && parent != null; parent = parent.getParent()) {
            value = parent.getVariableLocal(variableName);
        }

        return (Integer)((Integer)(value != null ? value : 0));
    }

    public void continueSequentialMultiInstance(CommandContext commandContext, DelegateExecution execution, int loopCounter, ExecutionEntity multiInstanceRootExecution, Activity miActivityElement) {
        try {

            if (execution.getCurrentFlowElement() instanceof SubProcess) {
                //TODO 子流程的本地参数赋值
                execution.setScope(true);
                execution.setCurrentFlowElement(miActivityElement);
            }
            commandContext.getAgenda().planOperation(new CustomizedContinueMultiInstanceOperation(commandContext, (ExecutionEntity) execution, multiInstanceRootExecution, loopCounter));

        } catch (BpmnError error) {
            // re-throw business fault so that it can be caught by an Error
            // Intermediate Event or Error Event Sub-Process in the process
            throw error;
        } catch (Exception e) {
            throw new RuntimeException("Could not execute inner activity behavior of multi instance behavior", e);
        }
    }
}
