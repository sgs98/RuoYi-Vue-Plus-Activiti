/*
package com.ruoyi.workflow.activiti.image;

import org.activiti.bpmn.model.AssociationDirection;
import org.activiti.bpmn.model.EventSubProcess;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.bpmn.model.Transaction;
import org.activiti.engine.ActivitiException;
import org.activiti.image.exception.ActivitiImageException;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
import org.activiti.image.impl.ProcessDiagramSVGGraphics2D;
import org.activiti.image.impl.icon.*;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.*;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomProcessDiagramCanvas {

    */
/**
     * 矩形、菱形、椭圆形
     *//*

    public enum SHAPE_TYPE {
        Rectangle, Rhombus, Ellipse;

        SHAPE_TYPE() {
        }
    }

    protected static final int ARROW_WIDTH = 5;
    protected static final int CONDITIONAL_INDICATOR_WIDTH = 16;
    protected static final int DEFAULT_INDICATOR_WIDTH = 10;
    protected static final int MARKER_WIDTH = 12;
    protected static final int FONT_SIZE = 12;
    protected static final int FONT_SPACING = 2;
    protected static final int TEXT_PADDING = 3;
    protected static final int ANNOTATION_TEXT_PADDING = 7;
    protected static final int LINE_HEIGHT = FONT_SIZE + FONT_SPACING;

    */
/**
     * Colors
     *//*

    protected static Color TASK_BOX_COLOR = new Color(249, 249, 249);
    protected static Color SUBPROCESS_BOX_COLOR = new Color(255, 255, 255);
    protected static Color EVENT_COLOR = Color.WHITE;
    protected static Color CONNECTION_COLOR = new Color(128, 128, 128);
    protected static Color CONDITIONAL_INDICATOR_COLOR = new Color(255, 255, 255);
    protected static Color RUNNING_HIGHLIGHT_COLOR = Color.RED;
    protected static Color LAST_HIGHLIGHT_COLOR = new Color(252, 75, 52);
    protected static Color HIGHLIGHT_COLOR = Color.GREEN;
    protected static Color LABEL_COLOR = new Color(67, 150, 246);
    //	protected static Color			LABEL_COLOR						= Color.blue;
    protected static Color TASK_BORDER_COLOR = new Color(149, 148, 148);
    protected static Color EVENT_BORDER_COLOR = new Color(76, 76, 76);
    protected static Color SUBPROCESS_BORDER_COLOR = Color.BLACK;

    // Fonts
    protected static Font LABEL_FONT = new Font("微软雅黑", Font.ITALIC, 14);
    protected static Font ANNOTATION_FONT = new Font("微软雅黑", Font.PLAIN, FONT_SIZE);
    protected static Font TASK_FONT = new Font("微软雅黑", Font.PLAIN, FONT_SIZE);

    // Strokes笔画
    //TODO 边框宽度修改
    //protected static Stroke THICK_TASK_BORDER_STROKE = new BasicStroke(3.0f);
    protected static Stroke THICK_TASK_BORDER_STROKE = new BasicStroke(2.0f);
    protected static Stroke GATEWAY_TYPE_STROKE = new BasicStroke(3.0f);
    protected static Stroke END_EVENT_STROKE = new BasicStroke(3.0f);
    protected static Stroke MULTI_INSTANCE_STROKE = new BasicStroke(1.3f);
    protected static Stroke EVENT_SUBPROCESS_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{1.0f}, 0.0f);
    protected static Stroke NON_INTERRUPTING_EVENT_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{4.0f, 3.0f}, 0.0f);
    protected static Stroke HIGHLIGHT_FLOW_STROKE = new BasicStroke(1.3f);
    protected static Stroke ANNOTATION_STROKE = new BasicStroke(2.0f);
    protected static Stroke ASSOCIATION_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2.0f, 2.0f}, 0.0f);

    // icons
    protected static int ICON_PADDING = 5;
    protected static TaskIconType USERTASK_IMAGE;
    protected static TaskIconType SCRIPTTASK_IMAGE;
    protected static TaskIconType SERVICETASK_IMAGE;
    protected static TaskIconType RECEIVETASK_IMAGE;
    protected static TaskIconType SENDTASK_IMAGE;
    protected static TaskIconType MANUALTASK_IMAGE;
    protected static TaskIconType BUSINESS_RULE_TASK_IMAGE;

    protected static IconType TIMER_IMAGE;
    protected static IconType COMPENSATE_THROW_IMAGE;
    protected static IconType COMPENSATE_CATCH_IMAGE;
    protected static IconType ERROR_THROW_IMAGE;
    protected static IconType ERROR_CATCH_IMAGE;
    protected static IconType MESSAGE_CATCH_IMAGE;
    protected static IconType SIGNAL_CATCH_IMAGE;
    protected static IconType SIGNAL_THROW_IMAGE;

    protected int canvasWidth = -1;
    protected int canvasHeight = -1;
    protected int minX = -1;
    protected int minY = -1;
    protected ProcessDiagramSVGGraphics2D g;
    protected FontMetrics fontMetrics;
    protected boolean closed;
    protected String activityFontName = "微软雅黑";
    protected String labelFontName = "微软雅黑";
    protected String annotationFontName = "微软雅黑";

    */
/**
     * Creates an empty canvas with given width and height. Allows to specify minimal boundaries on the left and upper side of the canvas. This is useful for diagrams that have
     * white space there. Everything beneath these minimum values will be cropped. It's also possible to pass a specific font name and a class loader for the icon images.
     *//*

    public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String activityFontName, String labelFontName, String annotationFontName) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.minX = minX;
        this.minY = minY;
        if (activityFontName != null) {
            this.activityFontName = activityFontName;
        }
        if (labelFontName != null) {
            this.labelFontName = labelFontName;
        }
        if (annotationFontName != null) {
            this.annotationFontName = annotationFontName;
        }
        this.initialize();
    }

    */
/**
     * Creates an empty canvas with given width and height. Allows to specify minimal boundaries on the left and upper side of the canvas. This is useful for diagrams that have
     * white space there (eg Signavio). Everything beneath these minimum values will be cropped.
     *
     * @param minX Hint that will be used when generating the image. Parts that fall below minX on the horizontal scale will be cropped.
     * @param minY Hint that will be used when generating the image. Parts that fall below minX on the horizontal scale will be cropped.
     *//*

    public CustomProcessDiagramCanvas(int width, int height, int minX, int minY) {
        this.canvasWidth = width;
        this.canvasHeight = height;
        this.minX = minX;
        this.minY = minY;
        this.initialize();
    }

    public void initialize() {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", (DocumentType) null);
        this.g = new ProcessDiagramSVGGraphics2D(document);
        this.g.setSVGCanvasSize(new Dimension(this.canvasWidth, this.canvasHeight));
        this.g.setBackground(new Color(255, 255, 255, 0));
        this.g.clearRect(0, 0, this.canvasWidth, this.canvasHeight);
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g.setPaint(Color.black);
        Font font = new Font(this.activityFontName, 1, 11);
        this.g.setFont(font);
        this.fontMetrics = this.g.getFontMetrics();
        LABEL_FONT = new Font(this.labelFontName, 2, 10);
        ANNOTATION_FONT = new Font(this.annotationFontName, 0, 11);

        USERTASK_IMAGE = new UserTaskIconType();
        SCRIPTTASK_IMAGE = new ScriptTaskIconType();
        SERVICETASK_IMAGE = new ServiceTaskIconType();
        RECEIVETASK_IMAGE = new ReceiveTaskIconType();
        SENDTASK_IMAGE = new SendTaskIconType();
        MANUALTASK_IMAGE = new ManualTaskIconType();
        BUSINESS_RULE_TASK_IMAGE = new BusinessRuleTaskIconType();
        TIMER_IMAGE = new TimerIconType();
        COMPENSATE_THROW_IMAGE = new CompensateThrowIconType();
        COMPENSATE_CATCH_IMAGE = new CompensateIconType();
        ERROR_THROW_IMAGE = new ErrorThrowIconType();
        ERROR_CATCH_IMAGE = new ErrorIconType();
        MESSAGE_CATCH_IMAGE = new MessageIconType();
        SIGNAL_THROW_IMAGE = new SignalThrowIconType();
        SIGNAL_CATCH_IMAGE = new SignalIconType();

    }

    */
/**
     * Generates an image of what currently is drawn on the canvas. Throws an {@link ActivitiException} when {@link #close()} is already called.
     *//*

    public InputStream generateImage() {
        if (closed) {
            throw new ActivitiImageException("ProcessDiagramGenerator already closed");
        } else {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Writer out = new OutputStreamWriter(stream, "UTF-8");
                this.g.stream(out, true);
                return new ByteArrayInputStream(stream.toByteArray());
            } catch (SVGGraphics2DIOException | UnsupportedEncodingException var3) {
                throw new ActivitiImageException("Error while generating process image", var3);
            }
        }
    }

    public void close() {
        g.dispose();
        closed = true;
    }


    public void drawNoneStartEvent(String id, GraphicInfo graphicInfo) {
        this.drawStartEvent(id, graphicInfo, (IconType) null);
    }

    public void drawTimerStartEvent(String id, GraphicInfo graphicInfo) {
        this.drawStartEvent(id, graphicInfo, TIMER_IMAGE);
    }

    public void drawSignalStartEvent(String id, GraphicInfo graphicInfo) {
        this.drawStartEvent(id, graphicInfo, SIGNAL_CATCH_IMAGE);
    }

    public void drawMessageStartEvent(String id, GraphicInfo graphicInfo) {
        this.drawStartEvent(id, graphicInfo, MESSAGE_CATCH_IMAGE);
    }

    public void drawStartEvent(String id, GraphicInfo graphicInfo, IconType icon) {
        Paint originalPaint = this.g.getPaint();
        this.g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        this.g.fill(circle);
        this.g.setPaint(EVENT_BORDER_COLOR);
        this.g.draw(circle);
        this.g.setPaint(originalPaint);
        if (icon != null) {
            // calculate coordinates to center image
            int imageX = (int) Math.round(graphicInfo.getX() + graphicInfo.getWidth() / 2.0D - (double) (icon.getWidth() / 2));
            int imageY = (int) Math.round(graphicInfo.getY() + graphicInfo.getHeight() / 2.0D - (double) (icon.getHeight() / 2));
            icon.drawIcon(imageX, imageY, ICON_PADDING, this.g);
        }
        this.g.setCurrentGroupId(id);
    }


    public void drawNoneEndEvent(String id, String name, GraphicInfo graphicInfo) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(EVENT_COLOR);
        Ellipse2D circle = new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        this.g.fill(circle);
        this.g.setPaint(EVENT_BORDER_COLOR);
        this.g.setStroke(END_EVENT_STROKE);
        this.g.draw(circle);
        this.g.setStroke(originalStroke);
        this.g.setPaint(originalPaint);
        this.g.setCurrentGroupId(id);
        this.drawLabel(name, graphicInfo);
    }

    public void drawErrorEndEvent(String id, String name, GraphicInfo graphicInfo) {
        this.drawNoneEndEvent(id, name, graphicInfo);
        int imageX = (int) (graphicInfo.getX() + graphicInfo.getWidth() / 4.0D);
        int imageY = (int) (graphicInfo.getY() + graphicInfo.getHeight() / 4.0D);
        ERROR_THROW_IMAGE.drawIcon(imageX, imageY, ICON_PADDING, this.g);
    }

    public void drawErrorStartEvent(String id, GraphicInfo graphicInfo) {
        this.drawNoneStartEvent(id, graphicInfo);
        int imageX = (int) (graphicInfo.getX() + graphicInfo.getWidth() / 4.0D);
        int imageY = (int) (graphicInfo.getY() + graphicInfo.getHeight() / 4.0D);
        ERROR_THROW_IMAGE.drawIcon(imageX, imageY, ICON_PADDING, this.g);
    }

    public void drawCatchingEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting, IconType icon, String eventType) {

        // event circles
        Ellipse2D outerCircle = new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        int innerCircleSize = 4;
        int innerCircleX = (int) graphicInfo.getX() + innerCircleSize;
        int innerCircleY = (int) graphicInfo.getY() + innerCircleSize;
        int innerCircleWidth = (int) graphicInfo.getWidth() - 2 * innerCircleSize;
        int innerCircleHeight = (int) graphicInfo.getHeight() - 2 * innerCircleSize;
        Ellipse2D innerCircle = new Ellipse2D.Double((double) innerCircleX, (double) innerCircleY, (double) innerCircleWidth, (double) innerCircleHeight);

        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(EVENT_COLOR);
        this.g.fill(outerCircle);

        this.g.setPaint(EVENT_BORDER_COLOR);
        if (!isInterrupting) {
            this.g.setStroke(NON_INTERRUPTING_EVENT_STROKE);
        }
        this.g.draw(outerCircle);
        this.g.setStroke(originalStroke);
        this.g.setPaint(originalPaint);
        this.g.draw(innerCircle);

        if (icon != null) {
            // calculate coordinates to center image
            int imageX = (int) (graphicInfo.getX() + graphicInfo.getWidth() / 2.0D - (double) (icon.getWidth() / 2));
            int imageY = (int) (graphicInfo.getY() + graphicInfo.getHeight() / 2.0D - (double) (icon.getHeight() / 2));
            if ("timer".equals(eventType)) {
                imageX++;
                imageY++;
            }
            icon.drawIcon(imageX, imageY, ICON_PADDING, this.g);
        }
        this.g.setCurrentGroupId(id);
    }

    public void drawCatchingCompensateEvent(String id, String name, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingCompensateEvent(id, graphicInfo, isInterrupting);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingCompensateEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, COMPENSATE_CATCH_IMAGE, "compensate");
    }

    public void drawCatchingTimerEvent(String id, String name, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingTimerEvent(id, graphicInfo, isInterrupting);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingTimerEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, TIMER_IMAGE, "timer");
    }

    public void drawCatchingErrorEvent(String id, String name, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingErrorEvent(id, graphicInfo, isInterrupting);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingErrorEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, ERROR_CATCH_IMAGE, "error");
    }

    public void drawCatchingSignalEvent(String id, String name, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingSignalEvent(id, graphicInfo, isInterrupting);
        this.drawLabel(name, graphicInfo);
    }

    public void drawCatchingSignalEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, SIGNAL_CATCH_IMAGE, "signal");
    }

    public void drawCatchingMessageEvent(String id, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, MESSAGE_CATCH_IMAGE, "message");
    }

    public void drawCatchingMessageEvent(String id, String name, GraphicInfo graphicInfo, boolean isInterrupting) {
        this.drawCatchingEvent(id, graphicInfo, isInterrupting, MESSAGE_CATCH_IMAGE, "message");
        this.drawLabel(name, graphicInfo);
    }

    public void drawThrowingCompensateEvent(String id, GraphicInfo graphicInfo) {
        this.drawCatchingEvent(id, graphicInfo, true, COMPENSATE_THROW_IMAGE, "compensate");
    }

    public void drawThrowingSignalEvent(String id, GraphicInfo graphicInfo) {
        this.drawCatchingEvent(id, graphicInfo, true, SIGNAL_THROW_IMAGE, "signal");
    }

    public void drawThrowingNoneEvent(String id, GraphicInfo graphicInfo) {
        this.drawCatchingEvent(id, graphicInfo, true, (IconType) null, "none");
    }

    public void drawSequenceflow(int srcX, int srcY, int targetX, int targetY, boolean conditional) {
        this.drawSequenceflow(srcX, srcY, targetX, targetY, conditional, false);
    }

    public void drawSequenceflow(int srcX, int srcY, int targetX, int targetY, boolean conditional, boolean highLighted) {
        Paint originalPaint = this.g.getPaint();
        if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
        }

        Line2D.Double line = new Line2D.Double((double) srcX, (double) srcY, (double) targetX, (double) targetY);
        this.g.draw(line);
        this.drawArrowHead(line);
        if (conditional) {
            this.drawConditionalSequenceFlowIndicator(line);
        }

        if (highLighted) {
            this.g.setPaint(originalPaint);
        }

    }

    public void drawAssociation(int[] xPoints, int[] yPoints, AssociationDirection associationDirection, boolean highLighted) {
        boolean conditional = false;
        boolean isDefault = false;
        this.drawConnection(xPoints, yPoints, conditional, isDefault, "association", associationDirection, highLighted, HIGHLIGHT_COLOR);
    }

    public void drawSequenceflow(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, boolean highLighted) {
        this.drawConnection(xPoints, yPoints, conditional, isDefault, "sequenceFlow", AssociationDirection.ONE, highLighted, HIGHLIGHT_COLOR);
    }

    public void drawLastSequenceflow(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, boolean highLighted) {
        this.drawConnection(xPoints, yPoints, conditional, isDefault, "sequenceFlow", AssociationDirection.ONE, highLighted, LAST_HIGHLIGHT_COLOR);
    }

    public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault, String connectionType, AssociationDirection associationDirection, boolean highLighted, Color HIGHLIGHT_COLOR) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(CONNECTION_COLOR);
        if ("association".equals(connectionType)) {
            this.g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
            this.g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; ++i) {
            Integer sourceX = xPoints[i - 1];
            Integer sourceY = yPoints[i - 1];
            Integer targetX = xPoints[i];
            Integer targetY = yPoints[i];
            Line2D.Double line = new Line2D.Double((double) sourceX, (double) sourceY, (double) targetX, (double) targetY);
            this.g.draw(line);
        }

        Line2D.Double line;
        if (isDefault) {
            line = new Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawDefaultSequenceFlowIndicator(line);
        }

        if (conditional) {
            line = new Line2D.Double((double) xPoints[0], (double) yPoints[0], (double) xPoints[1], (double) yPoints[1]);
            this.drawConditionalSequenceFlowIndicator(line);
        }

        if (associationDirection.equals(AssociationDirection.ONE) || associationDirection.equals(AssociationDirection.BOTH)) {
            line = new Line2D.Double((double) xPoints[xPoints.length - 2], (double) yPoints[xPoints.length - 2], (double) xPoints[xPoints.length - 1], (double) yPoints[xPoints.length - 1]);
            this.drawArrowHead(line);
        }

        if (associationDirection.equals(AssociationDirection.BOTH)) {
            line = new Line2D.Double((double) xPoints[1], (double) yPoints[1], (double) xPoints[0], (double) yPoints[0]);
            this.drawArrowHead(line);
        }

        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }

    public void drawSequenceflowWithoutArrow(int srcX, int srcY, int targetX, int targetY, boolean conditional) {
        this.drawSequenceflowWithoutArrow(srcX, srcY, targetX, targetY, conditional, false);
    }

    public void drawSequenceflowWithoutArrow(int srcX, int srcY, int targetX, int targetY, boolean conditional, boolean highLighted) {
        Paint originalPaint = this.g.getPaint();
        if (highLighted) {
            this.g.setPaint(HIGHLIGHT_COLOR);
        }

        Line2D.Double line = new Line2D.Double((double) srcX, (double) srcY, (double) targetX, (double) targetY);
        this.g.draw(line);
        if (conditional) {
            this.drawConditionalSequenceFlowIndicator(line);
        }

        if (highLighted) {
            this.g.setPaint(originalPaint);
        }

    }

    public void drawArrowHead(Line2D.Double line) {
        int doubleArrowWidth = 10;
        if (doubleArrowWidth == 0) {
            doubleArrowWidth = 2;
        }

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);

        int arrowHeadPoint = -5;
        if (arrowHeadPoint == 0) {
            arrowHeadPoint = -1;
        }

        arrowHead.addPoint(arrowHeadPoint, -doubleArrowWidth);
        arrowHeadPoint = 5;
        if (arrowHeadPoint == 0) {
            arrowHeadPoint = 1;
        }

        arrowHead.addPoint(arrowHeadPoint, -doubleArrowWidth);
        AffineTransform transformation = new AffineTransform();
        transformation.setToIdentity();
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        transformation.translate(line.x2, line.y2);
        transformation.rotate(angle - 1.5707963267948966D);
        AffineTransform originalTransformation = this.g.getTransform();
        this.g.setTransform(transformation);
        this.g.fill(arrowHead);
        this.g.setTransform(originalTransformation);
    }

    public void drawDefaultSequenceFlowIndicator(Line2D.Double line) {
        double length = 10.0D;
        double halfOfLength = length / 2.0D;
        double f = 8.0D;
        Line2D.Double defaultIndicator = new Line2D.Double(-halfOfLength, 0.0D, halfOfLength, 0.0D);
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        double dx = f * Math.cos(angle);
        double dy = f * Math.sin(angle);
        double x1 = line.x1 + dx;
        double y1 = line.y1 + dy;
        AffineTransform transformation = new AffineTransform();
        transformation.setToIdentity();
        transformation.translate(x1, y1);
        transformation.rotate(angle - 2.356194490192345D);
        AffineTransform originalTransformation = this.g.getTransform();
        this.g.setTransform(transformation);
        this.g.draw(defaultIndicator);
        this.g.setTransform(originalTransformation);
    }

    public void drawConditionalSequenceFlowIndicator(Line2D.Double line) {
        int horizontal = 11;
        int halfOfHorizontal = horizontal / 2;
        int halfOfVertical = 8;
        Polygon conditionalIndicator = new Polygon();
        conditionalIndicator.addPoint(0, 0);
        conditionalIndicator.addPoint(-halfOfHorizontal, halfOfVertical);
        conditionalIndicator.addPoint(0, 16);
        conditionalIndicator.addPoint(halfOfHorizontal, halfOfVertical);
        AffineTransform transformation = new AffineTransform();
        transformation.setToIdentity();
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        transformation.translate(line.x1, line.y1);
        transformation.rotate(angle - 1.5707963267948966D);
        AffineTransform originalTransformation = this.g.getTransform();
        this.g.setTransform(transformation);
        this.g.draw(conditionalIndicator);
        Paint originalPaint = this.g.getPaint();
        this.g.setPaint(CONDITIONAL_INDICATOR_COLOR);
        this.g.fill(conditionalIndicator);
        this.g.setPaint(originalPaint);
        this.g.setTransform(originalTransformation);
    }

    public void drawTask(TaskIconType icon, String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(id, name, graphicInfo);
        icon.drawIcon((int) graphicInfo.getX(), (int) graphicInfo.getY(), ICON_PADDING, this.g);
    }

    public void drawTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(id, name, graphicInfo, false);
    }


    public void drawPoolOrLane(String id, String name, GraphicInfo graphicInfo) {
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        this.g.drawRect(x, y, width, height);
        if (name != null && name.length() > 0) {
            int availableTextSpace = height - 6;
            AffineTransform transformation = new AffineTransform();
            transformation.setToIdentity();
            transformation.rotate(4.71238898038469D);
            Font currentFont = this.g.getFont();
            Font theDerivedFont = currentFont.deriveFont(transformation);
            this.g.setFont(theDerivedFont);
            String truncated = this.fitTextToWidth(name, availableTextSpace);
            int realWidth = this.fontMetrics.stringWidth(truncated);
            this.g.drawString(truncated, x + 2 + this.fontMetrics.getHeight(), 3 + y + availableTextSpace - (availableTextSpace - realWidth) / 2);
            this.g.setFont(currentFont);
        }

        this.g.setCurrentGroupId(id);
    }

    protected void drawTask(String id, String name, GraphicInfo graphicInfo, boolean thickBorder) {
        Paint originalPaint = this.g.getPaint();
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        this.g.setPaint(TASK_BOX_COLOR);
        int arcR = 6;
        if (thickBorder) {
            arcR = 3;
        }

        RoundRectangle2D rect = new RoundRectangle2D.Double((double) x, (double) y, (double) width, (double) height, (double) arcR, (double) arcR);
        this.g.fill(rect);
        this.g.setPaint(TASK_BORDER_COLOR);
        if (thickBorder) {
            Stroke originalStroke = this.g.getStroke();
            this.g.setStroke(THICK_TASK_BORDER_STROKE);
            this.g.draw(rect);
            this.g.setStroke(originalStroke);
        } else {
            this.g.draw(rect);
        }

        this.g.setPaint(originalPaint);
        if (name != null && name.length() > 0) {
            int boxWidth = width - 6;
            int boxHeight = height - 16 - ICON_PADDING - ICON_PADDING - 12 - 2 - 2;
            int boxX = x + width / 2 - boxWidth / 2;
            int boxY = y + height / 2 - boxHeight / 2 + ICON_PADDING + ICON_PADDING - 2 - 2;
            this.drawMultilineCentredText(name, boxX, boxY, boxWidth, boxHeight);
        }

        this.g.setCurrentGroupId(id);
    }

    protected void drawMultilineCentredText(String text, int x, int y, int boxWidth, int boxHeight) {
        this.drawMultilineText(text, x, y, boxWidth, boxHeight, true);
    }

    protected void drawMultilineAnnotationText(String text, int x, int y, int boxWidth, int boxHeight) {
        this.drawMultilineText(text, x, y, boxWidth, boxHeight, false);
    }

    protected void drawMultilineText(String text, int x, int y, int boxWidth, int boxHeight, boolean centered) {
        AttributedString attributedString = new AttributedString(text);
        attributedString.addAttribute(TextAttribute.FONT, this.g.getFont());
        attributedString.addAttribute(TextAttribute.FOREGROUND, Color.black);
        AttributedCharacterIterator characterIterator = attributedString.getIterator();
        int currentHeight = 0;
        List<TextLayout> layouts = new ArrayList();
        String lastLine = null;
        LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator, this.g.getFontRenderContext());

        int currentY;
        int height;
        for (TextLayout layout = null; measurer.getPosition() < characterIterator.getEndIndex() && currentHeight <= boxHeight; currentHeight += height) {
            currentY = measurer.getPosition();
            layout = measurer.nextLayout((float) boxWidth);
            height = Float.valueOf(layout.getDescent() + layout.getAscent() + layout.getLeading()).intValue();
            if (currentHeight + height > boxHeight) {
                if (!layouts.isEmpty()) {
                    layouts.remove(layouts.size() - 1);
                    if (lastLine.length() >= 4) {
                        lastLine = lastLine.substring(0, lastLine.length() - 4) + "...";
                    }

                    layouts.add(new TextLayout(lastLine, this.g.getFont(), this.g.getFontRenderContext()));
                } else {
                    layouts.add(layout);
                    currentHeight += height;
                }
                break;
            }

            layouts.add(layout);
            lastLine = text.substring(currentY, measurer.getPosition());
        }

        currentY = y + (centered ? (boxHeight - currentHeight) / 2 : 0);


        TextLayout textLayout;
        for (Iterator var16 = layouts.iterator(); var16.hasNext(); currentY = (int) ((float) currentY + textLayout.getDescent() + textLayout.getLeading())) {
            textLayout = (TextLayout) var16.next();
            currentY = (int) ((float) currentY + textLayout.getAscent());
            height = x + (centered ? (boxWidth - Double.valueOf(textLayout.getBounds().getWidth()).intValue()) / 2 : 0);
            textLayout.draw(this.g, (float) height, (float) currentY);
        }

    }

    protected String fitTextToWidth(String original, int width) {
        String text = original;

        for (int maxWidth = width - 10; this.fontMetrics.stringWidth(text + "...") > maxWidth && text.length() > 0; text = text.substring(0, text.length() - 1)) {
        }

        if (!text.equals(original)) {
            text = text + "...";
        }

        return text;
    }

    public void drawUserTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(USERTASK_IMAGE, id, name, graphicInfo);
    }

    public void drawScriptTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(SCRIPTTASK_IMAGE, id, name, graphicInfo);
    }

    public void drawServiceTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(SERVICETASK_IMAGE, id, name, graphicInfo);
    }

    public void drawReceiveTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(RECEIVETASK_IMAGE, id, name, graphicInfo);
    }

    public void drawSendTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(SENDTASK_IMAGE, id, name, graphicInfo);
    }

    public void drawManualTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(MANUALTASK_IMAGE, id, name, graphicInfo);
    }

    public void drawBusinessRuleTask(String id, String name, GraphicInfo graphicInfo) {
        this.drawTask(BUSINESS_RULE_TASK_IMAGE, id, name, graphicInfo);
    }

    public void drawExpandedSubProcess(String id, String name, GraphicInfo graphicInfo, Class<?> type) {
        RoundRectangle2D rect = new RoundRectangle2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight(), 8.0D, 8.0D);
        if (type.equals(EventSubProcess.class)) {
            Stroke originalStroke = this.g.getStroke();
            this.g.setStroke(EVENT_SUBPROCESS_STROKE);
            this.g.draw(rect);
            this.g.setStroke(originalStroke);
        } else if (type.equals(Transaction.class)) {
            RoundRectangle2D outerRect = new RoundRectangle2D.Double(graphicInfo.getX() - 3.0D, graphicInfo.getY() - 3.0D, graphicInfo.getWidth() + 6.0D, graphicInfo.getHeight() + 6.0D, 8.0D, 8.0D);
            Paint originalPaint = this.g.getPaint();
            this.g.setPaint(SUBPROCESS_BOX_COLOR);
            this.g.fill(outerRect);
            this.g.setPaint(SUBPROCESS_BORDER_COLOR);
            this.g.draw(outerRect);
            this.g.setPaint(SUBPROCESS_BOX_COLOR);
            this.g.fill(rect);
            this.g.setPaint(SUBPROCESS_BORDER_COLOR);
            this.g.draw(rect);
            this.g.setPaint(originalPaint);
        } else {
            Paint originalPaint = this.g.getPaint();
            this.g.setPaint(SUBPROCESS_BOX_COLOR);
            this.g.fill(rect);
            this.g.setPaint(SUBPROCESS_BORDER_COLOR);
            this.g.draw(rect);
            this.g.setPaint(originalPaint);
        }

        if (name != null && !name.isEmpty()) {
            String text = this.fitTextToWidth(name, (int) graphicInfo.getWidth());
            this.g.drawString(text, (int) graphicInfo.getX() + 10, (int) graphicInfo.getY() + 15);
        }

        this.g.setCurrentGroupId(id);
    }

    public void drawCollapsedSubProcess(String id, String name, GraphicInfo graphicInfo, Boolean isTriggeredByEvent) {
        this.drawCollapsedTask(id, name, graphicInfo, false);
    }

    public void drawCollapsedCallActivity(String id, String name, GraphicInfo graphicInfo) {
        this.drawCollapsedTask(id, name, graphicInfo, true);
    }

    protected void drawCollapsedTask(String id, String name, GraphicInfo graphicInfo, boolean thickBorder) {
        this.drawTask(id, name, graphicInfo, thickBorder);
    }

    public void drawCollapsedMarker(int x, int y, int width, int height) {
        int rectangleWidth = 12;
        int rectangleHeight = 12;
        Rectangle rect = new Rectangle(x + (width - rectangleWidth) / 2, y + height - rectangleHeight - 3, rectangleWidth, rectangleHeight);
        this.g.draw(rect);
        Line2D.Double line = new Line2D.Double(rect.getCenterX(), rect.getY() + 2.0D, rect.getCenterX(), rect.getMaxY() - 2.0D);
        this.g.draw(line);
        line = new Line2D.Double(rect.getMinX() + 2.0D, rect.getCenterY(), rect.getMaxX() - 2.0D, rect.getCenterY());
        this.g.draw(line);
    }

    public void drawActivityMarkers(int x, int y, int width, int height, boolean multiInstanceSequential, boolean multiInstanceParallel, boolean collapsed) {
        if (collapsed) {
            if (!multiInstanceSequential && !multiInstanceParallel) {
                this.drawCollapsedMarker(x, y, width, height);
            } else {
                this.drawCollapsedMarker(x - 6 - 2, y, width, height);
                if (multiInstanceSequential) {
                    this.drawMultiInstanceMarker(true, x + 6 + 2, y, width, height);
                } else {
                    this.drawMultiInstanceMarker(false, x + 6 + 2, y, width, height);
                }
            }
        } else if (multiInstanceSequential) {
            this.drawMultiInstanceMarker(true, x, y, width, height);
        } else if (multiInstanceParallel) {
            this.drawMultiInstanceMarker(false, x, y, width, height);
        }

    }

    public void drawGateway(GraphicInfo graphicInfo) {
        Polygon rhombus = new Polygon();
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        rhombus.addPoint(x, y + height / 2);
        rhombus.addPoint(x + width / 2, y + height);
        rhombus.addPoint(x + width, y + height / 2);
        rhombus.addPoint(x + width / 2, y);
        this.g.draw(rhombus);
    }

    public void drawParallelGateway(String id, GraphicInfo graphicInfo) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        Stroke orginalStroke = this.g.getStroke();
        this.g.setStroke(GATEWAY_TYPE_STROKE);
        Line2D.Double line = new Line2D.Double((double) (x + 10), (double) (y + height / 2), (double) (x + width - 10), (double) (y + height / 2));
        this.g.draw(line);
        line = new Line2D.Double((double) (x + width / 2), (double) (y + height - 10), (double) (x + width / 2), (double) (y + 10));
        this.g.draw(line);
        this.g.setStroke(orginalStroke);
        this.g.setCurrentGroupId(id);
    }

    public void drawExclusiveGateway(String id, GraphicInfo graphicInfo) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        int quarterWidth = width / 4;
        int quarterHeight = height / 4;
        Stroke orginalStroke = this.g.getStroke();
        this.g.setStroke(GATEWAY_TYPE_STROKE);
        Line2D.Double line = new Line2D.Double((double) (x + quarterWidth + 3), (double) (y + quarterHeight + 3), (double) (x + 3 * quarterWidth - 3), (double) (y + 3 * quarterHeight - 3));
        this.g.draw(line);
        line = new Line2D.Double((double) (x + quarterWidth + 3), (double) (y + 3 * quarterHeight - 3), (double) (x + 3 * quarterWidth - 3), (double) (y + quarterHeight + 3));
        this.g.draw(line);
        this.g.setStroke(orginalStroke);
        this.g.setCurrentGroupId(id);
    }

    public void drawInclusiveGateway(String id, GraphicInfo graphicInfo) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        int diameter = width / 2;
        Stroke orginalStroke = this.g.getStroke();
        this.g.setStroke(GATEWAY_TYPE_STROKE);
        Ellipse2D.Double circle = new Ellipse2D.Double((double) ((width - diameter) / 2 + x), (double) ((height - diameter) / 2 + y), (double) diameter, (double) diameter);
        this.g.draw(circle);
        this.g.setStroke(orginalStroke);
        this.g.setCurrentGroupId(id);
    }

    public void drawEventBasedGateway(String id, GraphicInfo graphicInfo) {
        this.drawGateway(graphicInfo);
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        double scale = 0.6D;
        GraphicInfo eventInfo = new GraphicInfo();
        eventInfo.setX((double) x + (double) width * (1.0D - scale) / 2.0D);
        eventInfo.setY((double) y + (double) height * (1.0D - scale) / 2.0D);
        eventInfo.setWidth((double) width * scale);
        eventInfo.setHeight((double) height * scale);
        this.drawCatchingEvent((String) null, eventInfo, true, (IconType) null, "eventGateway");
        double r = (double) width / 6.0D;
        int topX = (int) (0.95D * r);
        int topY = (int) (-0.31D * r);
        int bottomX = (int) (0.59D * r);
        int bottomY = (int) (0.81D * r);
        int[] xPoints = new int[]{0, topX, bottomX, -bottomX, -topX};
        int[] yPoints = new int[]{-((int) r), topY, bottomY, bottomY, topY};
        Polygon pentagon = new Polygon(xPoints, yPoints, 5);
        pentagon.translate(x + width / 2, y + width / 2);
        this.g.drawPolygon(pentagon);
        this.g.setCurrentGroupId(id);
    }

    public void drawMultiInstanceMarker(boolean sequential, int x, int y, int width, int height) {
        int rectangleWidth = 12;
        int rectangleHeight = 12;
        int lineX = x + (width - rectangleWidth) / 2;
        int lineY = y + height - rectangleHeight - 3;
        Stroke orginalStroke = this.g.getStroke();
        this.g.setStroke(MULTI_INSTANCE_STROKE);
        if (sequential) {
            this.g.draw(new Line2D.Double((double) lineX, (double) lineY, (double) (lineX + rectangleWidth), (double) lineY));
            this.g.draw(new Line2D.Double((double) lineX, (double) (lineY + rectangleHeight / 2), (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight / 2)));
            this.g.draw(new Line2D.Double((double) lineX, (double) (lineY + rectangleHeight), (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight)));
        } else {
            this.g.draw(new Line2D.Double((double) lineX, (double) lineY, (double) lineX, (double) (lineY + rectangleHeight)));
            this.g.draw(new Line2D.Double((double) (lineX + rectangleWidth / 2), (double) lineY, (double) (lineX + rectangleWidth / 2), (double) (lineY + rectangleHeight)));
            this.g.draw(new Line2D.Double((double) (lineX + rectangleWidth), (double) lineY, (double) (lineX + rectangleWidth), (double) (lineY + rectangleHeight)));
        }

        this.g.setStroke(orginalStroke);
    }

    public void drawHighLight(int x, int y, int width, int height) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();
        this.g.setPaint(HIGHLIGHT_COLOR);
        this.g.setStroke(THICK_TASK_BORDER_STROKE);
        RoundRectangle2D rect = new RoundRectangle2D.Double((double) x, (double) y, (double) width, (double) height, 5.0D, 5.0D);
        this.g.draw(rect);
        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }


    */
/**
     * Desc: 绘制正在执行中的节点红色高亮显示
     *
     * @param x      x
     * @param y      y
     * @param width  width
     * @param height height
     * @author Fuxs
     *//*

    public void drawRunningActivityHighLight(int x, int y, int width, int height) {
        Paint originalPaint = this.g.getPaint();
        Stroke originalStroke = this.g.getStroke();

        this.g.setPaint(RUNNING_HIGHLIGHT_COLOR);
        this.g.setStroke(THICK_TASK_BORDER_STROKE);
        //todo 修改长方形弧度
        //RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 20, 20);
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
        this.g.draw(rect);

        this.g.setPaint(originalPaint);
        this.g.setStroke(originalStroke);
    }

    public void drawTextAnnotation(String id, String text, GraphicInfo graphicInfo) {
        int x = (int) graphicInfo.getX();
        int y = (int) graphicInfo.getY();
        int width = (int) graphicInfo.getWidth();
        int height = (int) graphicInfo.getHeight();
        Font originalFont = this.g.getFont();
        Stroke originalStroke = this.g.getStroke();
        this.g.setFont(ANNOTATION_FONT);
        Path2D path = new Path2D.Double();
        x = (int) ((double) x + 0.5D);
        int lineLength = 18;
        path.moveTo((double) (x + lineLength), (double) y);
        path.lineTo((double) x, (double) y);
        path.lineTo((double) x, (double) (y + height));
        path.lineTo((double) (x + lineLength), (double) (y + height));
        path.lineTo((double) (x + lineLength), (double) (y + height - 1));
        path.lineTo((double) (x + 1), (double) (y + height - 1));
        path.lineTo((double) (x + 1), (double) (y + 1));
        path.lineTo((double) (x + lineLength), (double) (y + 1));
        path.closePath();
        this.g.draw(path);
        int boxWidth = width - 14;
        int boxHeight = height - 14;
        int boxX = x + width / 2 - boxWidth / 2;
        int boxY = y + height / 2 - boxHeight / 2;
        if (text != null && !text.isEmpty()) {
            this.drawMultilineAnnotationText(text, boxX, boxY, boxWidth, boxHeight);
        }

        this.g.setFont(originalFont);
        this.g.setStroke(originalStroke);
        this.g.setCurrentGroupId(id);
    }

    public void drawLabel(String text, GraphicInfo graphicInfo) {
        this.drawLabel(text, graphicInfo, true);
    }

    */
/**
     * 绘制流程线名称
     * Desc:
     *
     * @param text
     * @param graphicInfo
     * @param centered
     * @author Fuxs
     *//*

    public void drawLabel(String text, GraphicInfo graphicInfo, boolean centered) {
        float interline = 1.0f;
        // text
        if (text != null && text.length() > 0) {
            Paint originalPaint = this.g.getPaint();
            Font originalFont = this.g.getFont();

            this.g.setPaint(LABEL_COLOR);
            this.g.setFont(LABEL_FONT);

            int wrapWidth = 100;
            int textY = (int) (graphicInfo.getY() + graphicInfo.getHeight());

            // TODO: use drawMultilineText()
            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FOREGROUND, this.g.getPaint());
            as.addAttribute(TextAttribute.FONT, this.g.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = new FontRenderContext(null, true, false);

            TextLayout tl;
            for (LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
                 lbm.getPosition() < text.length();
                 textY = (int) ((float) textY + tl.getDescent() + tl.getLeading() + (interline - 1.0F) * tl.getAscent())
            ) {
                tl = lbm.nextLayout((float) wrapWidth);
                textY = (int) ((float) textY + tl.getAscent());
                Rectangle2D bb = tl.getBounds();
                double tX = graphicInfo.getX();
                if (centered) {
                    tX += (double) ((int) (graphicInfo.getWidth() / 2.0D - bb.getWidth() / 2.0D));
                }

                tl.draw(this.g, (float) tX, (float) textY);
            }

            // restore originals
            this.g.setFont(originalFont);
            this.g.setPaint(originalPaint);
        }
    }

    */
/**
     * This method makes coordinates of connection flow better.
     *
     * @param sourceShapeType
     * @param targetShapeType
     * @param sourceGraphicInfo
     * @param targetGraphicInfo
     * @param graphicInfoList
     *//*

    public List<GraphicInfo> connectionPerfectionizer(SHAPE_TYPE sourceShapeType,
                                                      SHAPE_TYPE targetShapeType,
                                                      GraphicInfo sourceGraphicInfo,
                                                      GraphicInfo targetGraphicInfo,
                                                      List<GraphicInfo> graphicInfoList) {
        Shape shapeFirst = createShape(sourceShapeType, sourceGraphicInfo);
        Shape shapeLast = createShape(targetShapeType, targetGraphicInfo);

        if (graphicInfoList != null && graphicInfoList.size() > 0) {
            GraphicInfo graphicInfoFirst = graphicInfoList.get(0);
            GraphicInfo graphicInfoLast = graphicInfoList.get(graphicInfoList.size() - 1);
            if (shapeFirst != null) {
                graphicInfoFirst.setX(shapeFirst.getBounds2D().getCenterX());
                graphicInfoFirst.setY(shapeFirst.getBounds2D().getCenterY());
            }
            if (shapeLast != null) {
                graphicInfoLast.setX(shapeLast.getBounds2D().getCenterX());
                graphicInfoLast.setY(shapeLast.getBounds2D().getCenterY());
            }

            Point p = null;

            if (shapeFirst != null) {
                Line2D.Double lineFirst = new Line2D.Double(graphicInfoFirst.getX(), graphicInfoFirst.getY(), graphicInfoList.get(1).getX(), graphicInfoList.get(1).getY());
                p = getIntersection(shapeFirst, lineFirst);
                if (p != null) {
                    graphicInfoFirst.setX(p.getX());
                    graphicInfoFirst.setY(p.getY());
                }
            }

            if (shapeLast != null) {
                Line2D.Double lineLast = new Line2D.Double(graphicInfoLast.getX(), graphicInfoLast.getY(), graphicInfoList.get(graphicInfoList.size() - 2).getX(),
                        graphicInfoList.get(graphicInfoList.size() - 2).getY());
                p = getIntersection(shapeLast, lineLast);
                if (p != null) {
                    graphicInfoLast.setX(p.getX());
                    graphicInfoLast.setY(p.getY());
                }
            }
        }

        return graphicInfoList;
    }

    */
/**
     * This method creates shape by type and coordinates.
     *
     * @param shapeType
     * @param graphicInfo
     * @return Shape
     *//*

    private static Shape createShape(SHAPE_TYPE shapeType, GraphicInfo graphicInfo) {
        if (SHAPE_TYPE.Rectangle.equals(shapeType)) {
            // source is rectangle
            return new Rectangle2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight());
        } else if (SHAPE_TYPE.Rhombus.equals(shapeType)) {
            // source is rhombus
            Path2D.Double rhombus = new Path2D.Double();
            rhombus.moveTo(graphicInfo.getX(), graphicInfo.getY() + graphicInfo.getHeight() / 2);
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth() / 2, graphicInfo.getY() + graphicInfo.getHeight());
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth(), graphicInfo.getY() + graphicInfo.getHeight() / 2);
            rhombus.lineTo(graphicInfo.getX() + graphicInfo.getWidth() / 2, graphicInfo.getY());
            rhombus.lineTo(graphicInfo.getX(), graphicInfo.getY() + graphicInfo.getHeight() / 2);
            rhombus.closePath();
            return rhombus;
        } else {
            return DefaultProcessDiagramCanvas.SHAPE_TYPE.Ellipse.equals(shapeType) ? new Ellipse2D.Double(graphicInfo.getX(), graphicInfo.getY(), graphicInfo.getWidth(), graphicInfo.getHeight()) : null;
        }
    }

    */
/**
     * This method returns intersection point of shape border and line.
     *
     * @param shape
     * @param line
     * @return Point
     *//*

    private static Point getIntersection(Shape shape, Line2D.Double line) {
        if (shape instanceof Ellipse2D) {
            return getEllipseIntersection(shape, line);
        } else {
            return !(shape instanceof Rectangle2D) && !(shape instanceof Path2D) ? null : getShapeIntersection(shape, line);
        }
    }

    */
/**
     * This method calculates ellipse intersection with line
     *
     * @param shape Bounds of this shape used to calculate parameters of inscribed into this bounds ellipse.
     * @param line
     * @return Intersection point
     *//*

    private static Point getEllipseIntersection(Shape shape, Line2D.Double line) {
        double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);
        double x = shape.getBounds2D().getWidth() / 2.0D * Math.cos(angle) + shape.getBounds2D().getCenterX();
        double y = shape.getBounds2D().getHeight() / 2.0D * Math.sin(angle) + shape.getBounds2D().getCenterY();
        Point p = new Point();
        p.setLocation(x, y);
        return p;
    }

    */
/**
     * This method calculates shape intersection with line.
     *
     * @param shape
     * @param line
     * @return Intersection point
     *//*

    private static Point getShapeIntersection(Shape shape, Line2D.Double line) {
        PathIterator it = shape.getPathIterator(null);
        double[] coords = new double[6];
        double[] pos = new double[2];
        Line2D.Double l = new Line2D.Double();
        while (!it.isDone()) {
            int type = it.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                    pos[0] = coords[0];
                    pos[1] = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    l = new Line2D.Double(pos[0], pos[1], coords[0], coords[1]);
                    if (line.intersectsLine(l)) {
                        return getLinesIntersection(line, l);
                    }
                    pos[0] = coords[0];
                    pos[1] = coords[1];
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    // whatever
            }
            it.next();
        }
        return null;
    }

    */
/**
     * This method calculates intersections of two lines.
     *
     * @param a Line 1
     * @param b Line 2
     * @return Intersection point
     *//*

    private static Point getLinesIntersection(Line2D a, Line2D b) {
        double d = (a.getX1() - a.getX2()) * (b.getY2() - b.getY1()) - (a.getY1() - a.getY2()) * (b.getX2() - b.getX1());
        double da = (a.getX1() - b.getX1()) * (b.getY2() - b.getY1()) - (a.getY1() - b.getY1()) * (b.getX2() - b.getX1());
        // double db = (a.getX1()-a.getX2())*(a.getY1()-b.getY1()) - (a.getY1()-a.getY2())*(a.getX1()-b.getX1());
        double ta = da / d;
        // double tb = db/d;
        Point p = new Point();
        p.setLocation(a.getX1() + ta * (a.getX2() - a.getX1()), a.getY1() + ta * (a.getY2() - a.getY1()));
        return p;
    }

}
*/
