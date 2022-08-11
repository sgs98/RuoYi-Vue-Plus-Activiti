package com.ruoyi.workflow.activiti.cmd;

import cn.hutool.core.collection.CollectionUtil;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.agenda.AbstractOperation;
import org.activiti.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.activiti.engine.impl.bpmn.helper.ErrorPropagation;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.delegate.ActivityBehavior;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.logging.LogMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Special operation when executing an instance of a multi-instance. It's similar to the {@link ContinueProcessOperation}, but simpler, as it doesn't need to cater for as many use cases.
 *
 * @author Joram Barrez
 * @author Tijs Rademakers
 */
public class CustomizedContinueMultiInstanceOperation extends AbstractOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedContinueMultiInstanceOperation.class);

    private final ExecutionEntity multiInstanceRootExecution;
    private final int loopCounter;

    public CustomizedContinueMultiInstanceOperation(CommandContext commandContext, ExecutionEntity execution, ExecutionEntity multiInstanceRootExecution, int loopCounter) {
        super(commandContext, execution);
        this.multiInstanceRootExecution = multiInstanceRootExecution;
        this.loopCounter = loopCounter;
    }

    @Override
    public void run() {
        FlowElement currentFlowElement = getCurrentFlowElement(execution);
        if (currentFlowElement instanceof FlowNode) {
            continueThroughMultiInstanceFlowNode((FlowNode) currentFlowElement);
        } else {
            throw new RuntimeException("Programmatic error: no valid multi instance flow node, type: " + currentFlowElement + ". Halting.");
        }
    }

    protected void continueThroughMultiInstanceFlowNode(FlowNode flowNode) {
        setLoopCounterVariable(flowNode);
        if (!flowNode.isAsynchronous()) {
            executeSynchronous(flowNode);
        } else {
            executeAsynchronous(flowNode);
        }
    }

    protected void executeSynchronous(FlowNode flowNode) {
        if (CollectionUtil.isNotEmpty(flowNode.getExecutionListeners())) {
            this.executeExecutionListeners(flowNode, "start");
        }

        this.commandContext.getHistoryManager().recordActivityStart(this.execution);
        ActivityBehavior activityBehavior = (ActivityBehavior)flowNode.getBehavior();
        if (activityBehavior != null) {
            LOGGER.debug("Executing activityBehavior {} on activity '{}' with execution {}", new Object[]{activityBehavior.getClass(), flowNode.getId(), this.execution.getId()});
            if (Context.getProcessEngineConfiguration() != null && Context.getProcessEngineConfiguration().getEventDispatcher().isEnabled()) {
                Context.getProcessEngineConfiguration().getEventDispatcher().dispatchEvent(ActivitiEventBuilder.createActivityEvent(ActivitiEventType.ACTIVITY_STARTED, flowNode.getId(), flowNode.getName(), this.execution.getId(), this.execution.getProcessInstanceId(), this.execution.getProcessDefinitionId(), flowNode));
            }

            try {
                activityBehavior.execute(this.execution);
            } catch (BpmnError var4) {
                ErrorPropagation.propagateError(var4, this.execution);
            } catch (RuntimeException var5) {
                if (LogMDC.isMDCEnabled()) {
                    LogMDC.putMDCExecution(this.execution);
                }

                throw var5;
            }
        } else {
            LOGGER.debug("No activityBehavior on activity '{}' with execution {}", flowNode.getId(), this.execution.getId());
        }
    }

    protected void executeAsynchronous(FlowNode flowNode) {
        JobEntity job = this.commandContext.getJobManager().createAsyncJob(this.execution, flowNode.isExclusive());
        this.commandContext.getJobManager().scheduleAsyncJob(job);
    }

    protected ActivityBehavior setLoopCounterVariable(FlowNode flowNode) {
        ActivityBehavior activityBehavior = (ActivityBehavior) flowNode.getBehavior();
        if (!(activityBehavior instanceof MultiInstanceActivityBehavior)) {
            throw new RuntimeException("Programmatic error: expected multi instance activity behavior, but got " + activityBehavior.getClass());
        }
        MultiInstanceActivityBehavior multiInstanceActivityBehavior = (MultiInstanceActivityBehavior) activityBehavior;
        String elementIndexVariable = multiInstanceActivityBehavior.getCollectionElementIndexVariable();
        if (!flowNode.isAsynchronous()) {
            execution.setVariableLocal(elementIndexVariable, loopCounter);
        } else {
            multiInstanceRootExecution.setVariableLocal(elementIndexVariable, loopCounter);
        }
        return activityBehavior;
    }
}

