/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/TDTimeline.java#1 $
$DateTime: 2007/08/06 17:28:33 $
$Change: 20537 $
$Author: a.borisenko $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EdgeShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.transform.MutableAffineTransformer;
import edu.uci.ics.jung.visualization.transform.MutableTransformer;

class TDTimeline  {
    private PluggableRenderer renderer = null;
    private EdgeShapeFunction edgeShapeFunction = null;
    private VertexShapeFunction vertexShapeFunction = null;
    private VertexStringer vertexStringer = null;
    private TDLinePaintable paintable = null;
    private VisualizationViewer viewer = null;
    private VisualizationModel model = null;
    private Layout layout = null;
    private UserDataContainer.CopyAction copyAction = null;
    private Graph graph = null;
    private long minTimestamp = 0L;
    private long maxTimestamp = 0L;
    private MutableTransformer transformer = null;
    private double scrollPosition = 0;

    public TDTimeline() {
        copyAction = new UserDataContainer.CopyAction.Shared();
        transformer = new MutableAffineTransformer();
        //
        edgeShapeFunction = new EdgeShape.Line();
        vertexShapeFunction = new TDTimelineVertexShapeFunction();
        vertexStringer = new TDTimelineVertexStringer();
        //
        paintable = new TDLinePaintable();
        paintable.setCoordinates(0, TDConstants.TIMELINE_BASELINE_Y, 10000, TDConstants.TIMELINE_BASELINE_Y);
        //
        renderer = new PluggableRenderer();
        renderer.setEdgeShapeFunction(edgeShapeFunction);
        renderer.setVertexShapeFunction(vertexShapeFunction);
        renderer.setVertexStringer(vertexStringer);
    }

    public Component getComponent(long minTimestamp, long maxTimestamp, int screenWidth) {
        this.minTimestamp = minTimestamp;
        long interval = maxTimestamp - minTimestamp;
        if(interval < screenWidth) {
            this.maxTimestamp = minTimestamp + screenWidth;
        } else {
            this.maxTimestamp = maxTimestamp;
        }
        //
        produceGraph();
        //
        layout = new TDTimelineLayout(graph, minTimestamp, maxTimestamp);
        model = new TDVisualizationModel();
        model.setGraphLayout(layout);
        //
        viewer = new VisualizationViewer(model, renderer, layout.getCurrentSize());
        viewer.setDoubleBuffered(false);
        viewer.addPreRenderPaintable(paintable);
        viewer.setViewTransformer(transformer);
        viewer.setMinimumSize(new Dimension(100, TDConstants.TIMELINE_HEIGHT));
        return viewer;
    }

    public void setTransformer(MutableTransformer transformer) {
        this.transformer = transformer;
    }

    public double getScrollPosition() {
        return scrollPosition;
    }

    public void scroll(double offsetx, double offsety) {
        scrollPosition -= offsetx;
        transformer.translate(offsetx, offsety);
    }

    private Graph produceGraph() {
        Edge edge = null;
        Vertex vertexTop = null;
        Vertex vertexBottom = null;

        int scaleRate = 3;
        graph = new SparseGraph();

        TTStep step = new TTStep(maxTimestamp-minTimestamp);
        int majorCounter = 0;
        long pos = 0;
        for (long position=minTimestamp; position < maxTimestamp; position += step.getMajorStep()) {
            vertexTop = new SparseVertex();
            vertexBottom = new SparseVertex();

            Long majorRulePosition = minTimestamp+step.getMajorRule()*majorCounter;

            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIMESTAMP, majorRulePosition, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIME, position, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_SCALEMAIN, TDConstants.VALUE_TIMELINE_SCALEMAIN, copyAction);

            graph.addVertex(vertexTop);
            graph.addVertex(vertexBottom);

            edge = new UndirectedSparseEdge(vertexTop, vertexBottom);
            graph.addEdge(edge);

            scaleRate = step.getMajorRule()/step.getMinorRule();
            for (int i=1; i < scaleRate; i++) {
                vertexTop = new SparseVertex();
                vertexBottom = new SparseVertex();
                vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIMESTAMP, majorRulePosition + step.getMinorRule()*i, copyAction);
                vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIME, position + step.getMinorStep()*i, copyAction);

                graph.addVertex(vertexTop);
                graph.addVertex(vertexBottom);

                edge = new UndirectedSparseEdge(vertexTop, vertexBottom);
                graph.addEdge(edge);
            }
            majorCounter++;
            pos = position;
        }

        if (maxTimestamp != 0 && maxTimestamp - minTimestamp == 0){
            vertexTop = new SparseVertex();
            vertexBottom = new SparseVertex();
            Long majorRulePosition = minTimestamp+step.getMajorRule()*majorCounter;
            pos = majorRulePosition;

            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIMESTAMP, majorRulePosition, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIME, pos, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_SCALEMAIN, TDConstants.VALUE_TIMELINE_SCALEMAIN, copyAction);
            graph.addVertex(vertexTop);
            graph.addVertex(vertexBottom);

            edge = new UndirectedSparseEdge(vertexTop, vertexBottom);
            graph.addEdge(edge);
            majorCounter = 1;
        }

        if (majorCounter != 0 && pos != 0){
            vertexTop = new SparseVertex();
            vertexBottom = new SparseVertex();
            pos += step.getMajorStep();
            Long majorRulePosition = minTimestamp+step.getMajorRule()*majorCounter;

            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIMESTAMP, majorRulePosition, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_TIME, pos, copyAction);
            vertexTop.addUserDatum(TDConstants.KEY_TIMELINE_SCALEMAIN, TDConstants.VALUE_TIMELINE_SCALEMAIN, copyAction);

            graph.addVertex(vertexTop);
            graph.addVertex(vertexBottom);

            edge = new UndirectedSparseEdge(vertexTop, vertexBottom);
            graph.addEdge(edge);
        }
        return graph;
    }
}

class TDTimelineVertexShapeFunction implements VertexShapeFunction {
    private Shape rectangle = new Rectangle2D.Float(0, 0, 0, 0);
    public Shape getShape(Vertex vertex) {
        return rectangle;
    }
}

class TDTimelineVertexStringer implements VertexStringer {
    private SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public String getLabel(ArchetypeVertex vertex) {
        String label = null;
        Long timestamp = (Long) vertex.getUserDatum(TDConstants.KEY_TIMELINE_TIME);
        if (timestamp != null) {
            if (vertex.getUserDatum(TDConstants.KEY_TIMELINE_SCALEMAIN) != null) {
                label = formatter.format(new Date(timestamp));
            }
        }
        return label;
    }
}