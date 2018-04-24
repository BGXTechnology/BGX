/*

$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/TDTimelineLayout.java#1 $

$DateTime: 2007/08/06 17:28:33 $

$Change: 20537 $

$Author: a.borisenko $

 */

package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Layout;

class TDTimelineLayout implements Layout {
    private int width = 0;
    private int height = 0;
    private Graph graph = null;
    private Map<Vertex, Point2D> coordinates = null;
    private long minTimestamp = 0L;
    private long maxTimestamp = 0L;

    public TDTimelineLayout(Graph graph, long minTimestamp, long maxTimestamp) {
        this.graph = graph;
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
        this.height = TDConstants.TIMELINE_HEIGHT;
        TTStep step = new TTStep(maxTimestamp - minTimestamp);
        long www = (1 + ((maxTimestamp - minTimestamp) / step.getMinorStep())) * step.getMinorRule();
        width = new Long(www).intValue();
        this.width = new Long(maxTimestamp - minTimestamp).intValue() + TDConstants.INDENT*2;
        this.coordinates = new HashMap<Vertex, Point2D>();
    }

    public Point2D getLocation(ArchetypeVertex v) {
        return coordinates.get(v);
    }

    public Iterator<Vertex> getVertexIterator() {
        return graph.getVertices().iterator();
    }

    public Dimension getCurrentSize() {
        return new Dimension(width, height);
    }

    public void initialize(Dimension currentSize) {
        Vertex vertex = null;
        Long timestamp = null;
        Object scaleMainMarker = null;
        Iterator<Vertex> iterator = graph.getVertices().iterator();
        while (iterator.hasNext()) {
            vertex = iterator.next();
            timestamp = (Long) vertex.getUserDatum(TDConstants.KEY_TIMELINE_TIMESTAMP);
            if ( timestamp != null ) {
                scaleMainMarker = vertex.getUserDatum(TDConstants.KEY_TIMELINE_SCALEMAIN);
                if ( scaleMainMarker != null ) {
                    coordinates.put(vertex, new Point2D.Float(timestamp - minTimestamp + TDConstants.INDENT, TDConstants.TIMELINE_SCALEMARK1_Y_TOP));
                    vertex = (Vertex) vertex.getNeighbors().toArray()[0];
                    coordinates.put(vertex, new Point2D.Float(timestamp - minTimestamp + TDConstants.INDENT, TDConstants.TIMELINE_SCALEMARK1_Y_BOTTOM));
                }
                else {
                    coordinates.put(vertex, new Point2D.Float(timestamp - minTimestamp + TDConstants.INDENT, TDConstants.TIMELINE_SCALEMARK2_Y_TOP));
                    vertex = (Vertex) vertex.getNeighbors().toArray()[0];
                    coordinates.put(vertex, new Point2D.Float(timestamp - minTimestamp + TDConstants.INDENT, TDConstants.TIMELINE_SCALEMARK2_Y_BOTTOM));
                }
            }
        }
    }

    public void resize(Dimension d) {
    }

    public void restart() {
    }

    public void advancePositions() {
    }

    public void applyFilter(Graph subgraph) {
    }

    public void forceMove(Vertex picked, double x, double y) {
    }

    public Graph getGraph() {
        return graph;
    }

    public String getStatus() {
        return null;
    }

    public double getX(Vertex v) {
        return 0;
    }

    public double getY(Vertex v) {
        return 0;
    }

    public boolean incrementsAreDone() {
        return true;
    }

    public boolean isIncremental() {
        return false;
    }

    public boolean isLocked(Vertex v) {
        return true;
    }

    public void lockVertex(Vertex v) {
    }

    public void unlockVertex(Vertex v) {
    }

    @Deprecated
    public Vertex getVertex(double x, double y) {
        return null;
    }

    @Deprecated
    public Vertex getVertex(double x, double y, double maxDistance) {
        return null;
    }

    @Deprecated
    public Set getVisibleEdges() {
        return null;
    }

    @Deprecated
    public Set getVisibleVertices() {
        return null;
    }
}