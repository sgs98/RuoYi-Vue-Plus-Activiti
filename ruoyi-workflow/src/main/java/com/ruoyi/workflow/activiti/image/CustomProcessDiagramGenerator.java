/*
package com.ruoyi.workflow.activiti.image;

import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.history.HistoricActivityInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

public class CustomProcessDiagramGenerator {
    protected static final Logger logger = LoggerFactory.getLogger(CustomProcessDiagramGenerator.class);

    protected String ACTIVITY_FONT_NAME = "微软雅黑";
    protected String LABEL_FONT_NAME = "微软雅黑";
    protected String ANNOTATION_FONT_NAME = "微软雅黑";

    protected Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions = new HashMap<>();
    protected Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions = new HashMap<>();

    */
/**
     * The instructions on how to draw a certain construct is
     * created statically and stored in a map for performance.
     *//*

    public CustomProcessDiagramGenerator() {
        // start event
        activityDrawInstructions.put(StartEvent.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                StartEvent startEvent = (StartEvent) flowNode;
                if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty()) {
                    EventDefinition eventDefinition = startEvent.getEventDefinitions().get(0);
                    if (eventDefinition instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawTimerStartEvent(flowNode.getId(), graphicInfo);
                    } else if (eventDefinition instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawErrorStartEvent(flowNode.getId(), graphicInfo);
                    } else if (eventDefinition instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawSignalStartEvent(flowNode.getId(), graphicInfo);
                    } else if (eventDefinition instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawMessageStartEvent(flowNode.getId(), graphicInfo);
                    } else {
                        processDiagramCanvas.drawNoneStartEvent(flowNode.getId(), graphicInfo);
                    }
                } else {
                    processDiagramCanvas.drawNoneStartEvent(flowNode.getId(), graphicInfo);
                }
            }
        });

        // signal catch
        activityDrawInstructions.put(IntermediateCatchEvent.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                IntermediateCatchEvent intermediateCatchEvent = (IntermediateCatchEvent) flowNode;
                if (intermediateCatchEvent.getEventDefinitions() != null && !intermediateCatchEvent
                        .getEventDefinitions().isEmpty()) {
                    if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(), flowNode.getName(), graphicInfo, true);
                    } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(), flowNode.getName(), graphicInfo, true);
                    } else if (intermediateCatchEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(), flowNode.getName(), graphicInfo, true);
                    }
                }
            }
        });

        // signal throw
        activityDrawInstructions.put(ThrowEvent.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                ThrowEvent throwEvent = (ThrowEvent) flowNode;
                if (throwEvent.getEventDefinitions() != null && !throwEvent.getEventDefinitions().isEmpty()) {
                    if (throwEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawThrowingSignalEvent(flowNode.getId(), graphicInfo);
                    } else if (throwEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                        processDiagramCanvas.drawThrowingCompensateEvent(flowNode.getId(), graphicInfo);
                    } else {
                        processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(), graphicInfo);
                    }
                } else {
                    processDiagramCanvas.drawThrowingNoneEvent(flowNode.getId(), graphicInfo);
                }
            }
        });

        // end event
        activityDrawInstructions.put(EndEvent.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                EndEvent endEvent = (EndEvent) flowNode;
                if (endEvent.getEventDefinitions() != null && !endEvent.getEventDefinitions().isEmpty()) {
                    if (endEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawErrorEndEvent(flowNode.getId(), flowNode.getName(), graphicInfo);
                    } else {
                        processDiagramCanvas.drawNoneEndEvent(flowNode.getId(), flowNode.getName(), graphicInfo);
                    }
                } else {
                    processDiagramCanvas.drawNoneEndEvent(flowNode.getId(), flowNode.getName(), graphicInfo);
                }
            }
        });

        // task
        activityDrawInstructions.put(Task.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // user task
        activityDrawInstructions.put(UserTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawUserTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // script task
        activityDrawInstructions.put(ScriptTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawScriptTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // service task
        activityDrawInstructions.put(ServiceTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                ServiceTask serviceTask = (ServiceTask) flowNode;
                processDiagramCanvas.drawServiceTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // receive task
        activityDrawInstructions.put(ReceiveTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawReceiveTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // send task
        activityDrawInstructions.put(SendTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawSendTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // manual task
        activityDrawInstructions.put(ManualTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawManualTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // businessRuleTask task
        activityDrawInstructions.put(BusinessRuleTask.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawBusinessRuleTask(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // exclusive gateway
        activityDrawInstructions.put(ExclusiveGateway.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawExclusiveGateway(flowNode.getId(), graphicInfo);
            }
        });

        // inclusive gateway
        activityDrawInstructions.put(InclusiveGateway.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawInclusiveGateway(flowNode.getId(), graphicInfo);
            }
        });

        // parallel gateway
        activityDrawInstructions.put(ParallelGateway.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawParallelGateway(flowNode.getId(), graphicInfo);
            }
        });

        // event based gateway
        activityDrawInstructions.put(EventGateway.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawEventBasedGateway(flowNode.getId(), graphicInfo);
            }
        });

        // Boundary timer
        activityDrawInstructions.put(BoundaryEvent.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                BoundaryEvent boundaryEvent = (BoundaryEvent) flowNode;
                if (boundaryEvent.getEventDefinitions() != null && !boundaryEvent.getEventDefinitions().isEmpty()) {
                    if (boundaryEvent.getEventDefinitions().get(0) instanceof TimerEventDefinition) {
                        processDiagramCanvas.drawCatchingTimerEvent(flowNode.getId(), flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity());
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof ErrorEventDefinition) {
                        processDiagramCanvas.drawCatchingErrorEvent(flowNode.getId(), graphicInfo, boundaryEvent.isCancelActivity());
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof SignalEventDefinition) {
                        processDiagramCanvas.drawCatchingSignalEvent(flowNode.getId(), flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity());
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof MessageEventDefinition) {
                        processDiagramCanvas.drawCatchingMessageEvent(flowNode.getId(), flowNode.getName(), graphicInfo, boundaryEvent.isCancelActivity());
                    } else if (boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                        processDiagramCanvas.drawCatchingCompensateEvent(flowNode.getId(), graphicInfo, boundaryEvent.isCancelActivity());
                    }
                }

            }
        });

        // subprocess
        activityDrawInstructions.put(SubProcess.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                    processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(), flowNode.getName(), graphicInfo, false);
                } else {
                    processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(), flowNode.getName(), graphicInfo, SubProcess.class);
                }
            }
        });

        // Event subprocess
        activityDrawInstructions.put(EventSubProcess.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                if (graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
                    processDiagramCanvas.drawCollapsedSubProcess(flowNode.getId(), flowNode.getName(), graphicInfo, false);
                } else {
                    processDiagramCanvas.drawExpandedSubProcess(flowNode.getId(), flowNode.getName(), graphicInfo, Transaction.class);
                }
            }
        });

        // call activity
        activityDrawInstructions.put(CallActivity.class, new ActivityDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
                processDiagramCanvas.drawCollapsedCallActivity(flowNode.getId(), flowNode.getName(), graphicInfo);
            }
        });

        // text annotation
        artifactDrawInstructions.put(TextAnnotation.class, new ArtifactDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(artifact.getId());
                TextAnnotation textAnnotation = (TextAnnotation) artifact;
                processDiagramCanvas.drawTextAnnotation(textAnnotation.getId(), textAnnotation.getText(), graphicInfo);
            }
        });

        // association
        artifactDrawInstructions.put(Association.class, new ArtifactDrawInstruction() {

            public void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact) {
                Association association = (Association) artifact;
                String sourceRef = association.getSourceRef();
                String targetRef = association.getTargetRef();

                // source and target can be instance of FlowElement or Artifact
                BaseElement sourceElement = bpmnModel.getFlowElement(sourceRef);
                BaseElement targetElement = bpmnModel.getFlowElement(targetRef);
                if (sourceElement == null) {
                    sourceElement = bpmnModel.getArtifact(sourceRef);
                }
                if (targetElement == null) {
                    targetElement = bpmnModel.getArtifact(targetRef);
                }
                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];
                for (int i = 1; i < graphicInfoList.size(); i++) {
                    GraphicInfo graphicInfo = graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }
                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();
                }

                AssociationDirection associationDirection = association.getAssociationDirection();
                processDiagramCanvas.drawAssociation(xPoints, yPoints, associationDirection, false);
            }
        });
    }


    protected CustomProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel,
                                                                List<String> highLightedActivities, List<String> runningActivityIdList, List<String> highLightedFlows, List<String> runningActivityFlowsIds,
                                                                String activityFontName, String labelFontName, String annotationFontName) {

        CustomProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, activityFontName, labelFontName, annotationFontName);

        // Draw pool shape, if process is participant in collaboration
        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getId(), pool.getName(), graphicInfo);
        }

        // Draw lanes
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane lane : process.getLanes()) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getId(), lane.getName(), graphicInfo);
            }
        }

        // Draw activities and their sequence-flows
        */
/**
         * 绘制流程图上的所有节点和流程线，对高亮显示的节点和流程线进行特殊处理
         *//*

        for (FlowNode flowNode : bpmnModel.getProcesses().get(0).findFlowElementsOfType(FlowNode.class)) {
            drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, runningActivityIdList,
                    highLightedFlows, runningActivityFlowsIds);
        }

        // Draw artifacts
        for (Process process : bpmnModel.getProcesses()) {
            for (Artifact artifact : process.getArtifacts()) {
                drawArtifact(processDiagramCanvas, bpmnModel, artifact);
            }
        }

        return processDiagramCanvas;
    }

    */
/**
     * Desc: 绘制流程图上的所有节点和流程线，对高亮显示的节点和流程线进行特殊处理
     *
     * @param processDiagramCanvas
     * @param bpmnModel
     * @param flowNode
     * @param highLightedActivities
     * @param highLightedFlows
     * @author Fuxs
     *//*

    protected void drawActivity(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode,
                                List<String> highLightedActivities, List<String> runningActivityIdList,
                                List<String> highLightedFlows, List<String> runningActivityFlowsIds) {

        ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
        boolean highLighted;
        if (drawInstruction != null) {

            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);

            // Gather info on the multi instance marker
            boolean multiInstanceSequential = false;
            boolean multiInstanceParallel = false;
            highLighted = false;
            if (flowNode instanceof Activity) {
                Activity activity = (Activity) flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if (multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            // Gather info on the highLighted marker
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (!(flowNode instanceof SubProcess)) {
                if (flowNode instanceof CallActivity) {
                    highLighted = true;
                }
            } else {
                highLighted = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
            }

            processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), multiInstanceSequential, multiInstanceParallel, highLighted);


            // Draw highlighted activities
            if (highLightedActivities.contains(flowNode.getId())) {
                */
/*
                 * 如果节点为当前正在处理中的节点，则红色高亮显示
                 *//*

                if (runningActivityIdList.contains(flowNode.getId())) {
                    logger.debug("[绘制]-当前正在处理中的节点-红色高亮显示节点[{}-{}]", flowNode.getId(), flowNode.getName());
                    drawRunningActivityHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                } else {
                    logger.debug("[绘制]-高亮显示节点[{}-{}]", flowNode.getId(), flowNode.getName());
                    drawHighLight(processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()));
                }
            }
        }

        */
/*
         * 绘制当前节点的流程线
         *//*

        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            highLighted = (highLightedFlows.contains(sequenceFlow.getId()));
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if (defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
                isDefault = true;
            }

            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null
                    && !(flowNode instanceof Gateway);

            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
            if (graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement,
                        targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); i++) {
                    GraphicInfo graphicInfo = graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }

                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();

                }

                //if (highLightedFlows.contains(sequenceFlow.getId()) && runningActivityFlowsIds.contains(sequenceFlow.getId())) {
                //    processDiagramCanvas.drawLastSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted);
                //}else {
                //    processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted);
                //}

                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted);

                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
                if(labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
                }
                */
/*
                 * 绘制流程线名称
                 *//*

                GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, true);
            }
        }

        // Nested elements
        if (flowNode instanceof FlowElementsContainer) {
            for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
                if (nestedFlowElement instanceof FlowNode) {
                    drawActivity(processDiagramCanvas, bpmnModel, (FlowNode) nestedFlowElement, highLightedActivities,
                            runningActivityIdList, highLightedFlows, runningActivityFlowsIds);
                }
            }
        }
    }

    */
/**
     * This method makes coordinates of connection flow better.
     *
     * @param processDiagramCanvas
     * @param bpmnModel
     * @param sourceElement
     * @param targetElement
     * @param graphicInfoList
     * @return
     *//*

    protected static List<GraphicInfo> connectionPerfectionizer(CustomProcessDiagramCanvas processDiagramCanvas,
                                                                BpmnModel bpmnModel, BaseElement sourceElement, BaseElement targetElement,
                                                                List<GraphicInfo> graphicInfoList) {
        GraphicInfo sourceGraphicInfo = bpmnModel.getGraphicInfo(sourceElement.getId());
        GraphicInfo targetGraphicInfo = bpmnModel.getGraphicInfo(targetElement.getId());

        CustomProcessDiagramCanvas.SHAPE_TYPE sourceShapeType = getShapeType(sourceElement);
        CustomProcessDiagramCanvas.SHAPE_TYPE targetShapeType = getShapeType(targetElement);

        return processDiagramCanvas.connectionPerfectionizer(sourceShapeType, targetShapeType, sourceGraphicInfo,
                targetGraphicInfo, graphicInfoList);
    }

    */
/**
     * This method returns shape type of base element.<br>
     * Each element can be presented as rectangle, rhombus, or ellipse.
     *
     * @param baseElement
     * @return CustomProcessDiagramCanvas.SHAPE_TYPE
     *//*

    protected static CustomProcessDiagramCanvas.SHAPE_TYPE getShapeType(BaseElement baseElement) {
        if (baseElement instanceof Task || baseElement instanceof Activity || baseElement instanceof TextAnnotation) {
            return CustomProcessDiagramCanvas.SHAPE_TYPE.Rectangle;
        } else if (baseElement instanceof Gateway) {
            return CustomProcessDiagramCanvas.SHAPE_TYPE.Rhombus;
        } else if (baseElement instanceof Event) {
            return CustomProcessDiagramCanvas.SHAPE_TYPE.Ellipse;
        } else {
            // unknown source element, just do not correct coordinates
        }
        return null;
    }

    protected static GraphicInfo getLineCenter(List<GraphicInfo> graphicInfoList) {
        GraphicInfo gi = new GraphicInfo();

        int[] xPoints = new int[graphicInfoList.size()];
        int[] yPoints = new int[graphicInfoList.size()];

        double length = 0;
        double[] lengths = new double[graphicInfoList.size()];
        lengths[0] = 0;
        double m;
        for (int i = 1; i < graphicInfoList.size(); i++) {
            GraphicInfo graphicInfo = graphicInfoList.get(i);
            GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

            if (i == 1) {
                xPoints[0] = (int) previousGraphicInfo.getX();
                yPoints[0] = (int) previousGraphicInfo.getY();
            }
            xPoints[i] = (int) graphicInfo.getX();
            yPoints[i] = (int) graphicInfo.getY();

            length += Math.sqrt(Math.pow((int) graphicInfo.getX() - (int) previousGraphicInfo.getX(), 2) + Math.pow(
                    (int) graphicInfo.getY() - (int) previousGraphicInfo.getY(), 2));
            lengths[i] = length;
        }
        m = length / 2;
        int p1 = 0, p2 = 1;
        for (int i = 1; i < lengths.length; i++) {
            double len = lengths[i];
            p1 = i - 1;
            p2 = i;
            if (len > m) {
                break;
            }
        }

        GraphicInfo graphicInfo1 = graphicInfoList.get(p1);
        GraphicInfo graphicInfo2 = graphicInfoList.get(p2);

        double AB = (int) graphicInfo2.getX() - (int) graphicInfo1.getX();
        double OA = (int) graphicInfo2.getY() - (int) graphicInfo1.getY();
        double OB = lengths[p2] - lengths[p1];
        double ob = m - lengths[p1];
        double ab = AB * ob / OB;
        double oa = OA * ob / OB;

        double mx = graphicInfo1.getX() + ab;
        double my = graphicInfo1.getY() + oa;

        gi.setX(mx);
        gi.setY(my);
        return gi;
    }

    protected void drawArtifact(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel,
                                Artifact artifact) {

        ArtifactDrawInstruction drawInstruction = artifactDrawInstructions.get(artifact.getClass());
        if (drawInstruction != null) {
            drawInstruction.draw(processDiagramCanvas, bpmnModel, artifact);
        }
    }

    private static void drawHighLight(CustomProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight());

    }

    */
/**
     * Desc:绘制正在执行中的节点红色高亮显示
     *
     * @param processDiagramCanvas CustomProcessDiagramCanvas
     * @param graphicInfo          GraphicInfo
     * @author Fuxs
     *//*

    private static void drawRunningActivityHighLight(CustomProcessDiagramCanvas processDiagramCanvas, GraphicInfo graphicInfo) {
        processDiagramCanvas.drawRunningActivityHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo
                .getWidth(), (int) graphicInfo.getHeight());

    }

    protected static CustomProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String activityFontName,
                                                                         String labelFontName, String annotationFontName) {

        // We need to calculate maximum values to know how big the image will be in its entirety
        double minX = 1.7976931348623157E308D;
        double maxX = 0.0D;
        double minY = 1.7976931348623157E308D;
        double maxY = 0.0D;


        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
            maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        for (FlowNode flowNode : flowNodes) {

            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());

            // width
            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }
            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }
            // height
            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }
            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                if (graphicInfoList != null) {
                    for (GraphicInfo graphicInfo : graphicInfoList) {
                        // width
                        if (graphicInfo.getX() > maxX) {
                            maxX = graphicInfo.getX();
                        }
                        if (graphicInfo.getX() < minX) {
                            minX = graphicInfo.getX();
                        }
                        // height
                        if (graphicInfo.getY() > maxY) {
                            maxY = graphicInfo.getY();
                        }
                        if (graphicInfo.getY() < minY) {
                            minY = graphicInfo.getY();
                        }
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        for (Artifact artifact : artifacts) {

            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());

            if (artifactGraphicInfo != null) {
                // width
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }
                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }
                // height
                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }
                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }
                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }
                    // height
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }
                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {

                nrOfLanes++;

                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                // // width
                if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }
                if (graphicInfo.getX() < minX) {
                    minX = graphicInfo.getX();
                }
                // height
                if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                }
                if (graphicInfo.getY() < minY) {
                    minY = graphicInfo.getY();
                }
            }
        }

        // Special case, see http://jira.codehaus.org/browse/ACT-1431
        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            // Nothing to show
            minX = 0.0D;
            minY = 0.0D;
        }
        //width和height代表最大的长宽
        return new CustomProcessDiagramCanvas((int) maxX + 30, (int) maxY + 50, (int) minX, (int) minY, activityFontName, labelFontName, annotationFontName);
    }

    protected static List<Artifact> gatherAllArtifacts(BpmnModel bpmnModel) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        for (Process process : bpmnModel.getProcesses()) {
            artifacts.addAll(process.getArtifacts());
        }
        return artifacts;
    }

    protected static List<FlowNode> gatherAllFlowNodes(BpmnModel bpmnModel) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();
        for (Process process : bpmnModel.getProcesses()) {
            flowNodes.addAll(gatherAllFlowNodes(process));
        }
        return flowNodes;
    }

    protected static List<FlowNode> gatherAllFlowNodes(FlowElementsContainer flowElementsContainer) {
        List<FlowNode> flowNodes = new ArrayList<FlowNode>();
        for (FlowElement flowElement : flowElementsContainer.getFlowElements()) {
            if (flowElement instanceof FlowNode) {
                flowNodes.add((FlowNode) flowElement);
            }
            if (flowElement instanceof FlowElementsContainer) {
                flowNodes.addAll(gatherAllFlowNodes((FlowElementsContainer) flowElement));
            }
        }
        return flowNodes;
    }

    public Map<Class<? extends BaseElement>, ActivityDrawInstruction> getActivityDrawInstructions() {
        return activityDrawInstructions;
    }

    public void setActivityDrawInstructions(Map<Class<? extends BaseElement>, ActivityDrawInstruction> activityDrawInstructions) {
        this.activityDrawInstructions = activityDrawInstructions;
    }

    public Map<Class<? extends BaseElement>, ArtifactDrawInstruction> getArtifactDrawInstructions() {
        return artifactDrawInstructions;
    }

    public void setArtifactDrawInstructions(Map<Class<? extends BaseElement>, ArtifactDrawInstruction> artifactDrawInstructions) {
        this.artifactDrawInstructions = artifactDrawInstructions;
    }

    protected interface ActivityDrawInstruction {
        void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, FlowNode flowNode);
    }

    protected interface ArtifactDrawInstruction {
        void draw(CustomProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel, Artifact artifact);
    }

    */
/**
     * Desc: 输入所有的参数，以获取图像
     *
     * @param bpmnModel
     * @param highLightedActivities
     * @param runningActivityIdList
     * @param highLightedFlows
     * @param activityFontName
     * @param labelFontName
     * @param annotationFontName
     * @return
     *//*


    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> runningActivityIdList,
                                             List<String> highLightedFlows,
                                             List<String> runningActivityFlowsIds,
                                             String activityFontName,
                                             String labelFontName,
                                             String annotationFontName) {
        // TODO
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                runningActivityIdList,
                highLightedFlows,
                runningActivityFlowsIds,
                activityFontName,
                labelFontName,
                annotationFontName).generateImage();
    }


    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> runningActivityIdList,
                                             List<String> highLightedFlows,
                                             String activityFontName,
                                             String labelFontName,
                                             String annotationFontName) {
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                runningActivityIdList,
                highLightedFlows,
                Collections.emptyList(),
                activityFontName,
                labelFontName,
                annotationFontName).generateImage();
    }


    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> highLightedFlows,
                                             String activityFontName,
                                             String labelFontName,
                                             String annotationFontName){
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                Collections.emptyList(),
                highLightedFlows,
                Collections.emptyList(),
                activityFontName,
                labelFontName,
                annotationFontName).generateImage();
    }


    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> runningActivityIdList,
                                             List<String> highLightedFlows,
                                             List<String> runningActivityFlowsIds){
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                runningActivityIdList,
                highLightedFlows,
                runningActivityFlowsIds,
                ACTIVITY_FONT_NAME,
                LABEL_FONT_NAME,
                ANNOTATION_FONT_NAME).generateImage();
    }


    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> runningActivityIdList,
                                             List<String> highLightedFlows){
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                runningActivityIdList,
                highLightedFlows,
                Collections.emptyList(),
                ACTIVITY_FONT_NAME,
                LABEL_FONT_NAME,
                ANNOTATION_FONT_NAME).generateImage();
    }

    public InputStream generateDiagramCustom(BpmnModel bpmnModel,
                                             List<String> highLightedActivities,
                                             List<String> highLightedFlows){
        return generateProcessDiagram(bpmnModel,
                highLightedActivities,
                Collections.emptyList(),
                highLightedFlows,
                Collections.emptyList(),
                ACTIVITY_FONT_NAME,
                LABEL_FONT_NAME,
                ANNOTATION_FONT_NAME).generateImage();
    }


    public List<String> getHighLightedFlows(BpmnModel bpmnModel,
                                                   List<HistoricActivityInstance> historicActivityInstances) {
        // 高亮流程已发生流转的线id集合
        List<String> highLightedFlowIds = new ArrayList<>();
        // 全部活动节点
        List<FlowNode> historicActivityNodes = new ArrayList<>();
        // 已完成的历史活动节点
        List<HistoricActivityInstance> finishedActivityInstances = new ArrayList<>();

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
            FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstance.getActivityId(), true);
            historicActivityNodes.add(flowNode);
            if (historicActivityInstance.getEndTime() != null) {
                finishedActivityInstances.add(historicActivityInstance);
            }
        }

        FlowNode currentFlowNode = null;
        FlowNode targetFlowNode = null;

        // 遍历已完成的活动实例，从每个实例的outgoingFlows中找到已执行的
        for (HistoricActivityInstance currentActivityInstance : finishedActivityInstances) {
            // 获得当前活动对应的节点信息及outgoingFlows信息
            currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(currentActivityInstance.getActivityId(), true);
            List<SequenceFlow> sequenceFlows = currentFlowNode.getOutgoingFlows();
            // 遍历历史活动节点，找到匹配流程目标节点的
            for (SequenceFlow sequenceFlow : sequenceFlows) {
                targetFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(sequenceFlow.getTargetRef(), true);
                if (historicActivityNodes.contains(targetFlowNode)) {
                    highLightedFlowIds.add(sequenceFlow.getId());
                }
            }
        }

        return highLightedFlowIds;
    }
}
*/
