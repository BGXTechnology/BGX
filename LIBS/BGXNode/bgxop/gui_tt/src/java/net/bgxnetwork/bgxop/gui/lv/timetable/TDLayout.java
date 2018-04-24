/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDLayout.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.*;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.UserDataContainer;
import edu.uci.ics.jung.visualization.Layout;

import net.bgx.bgxnetwork.transfer.tt.*;

class TDLayout implements Layout {
    private int width;
    private int height;
    private int y_top;
    private int y_center;
    private int y_bottom;
    private Graph graph = null;
    private Map<Vertex, Point2D> coordinates;
//    private long offset = 10L;
    private long minTimestamp = 0L;
    private long maxTimestamp = 0L;

    public TDLayout(Graph graph, long minTimestamp, long maxTimestamp, int height, int y_top, int y_center, int y_bottom) {
        this.graph = graph;
        this.minTimestamp = minTimestamp;
        this.maxTimestamp = maxTimestamp;
        this.height = height;
        this.y_top = y_top;
        this.y_center = y_center;
        this.y_bottom = y_bottom;
        this.coordinates = new HashMap<Vertex, Point2D>();
    }

    public Point2D getLocation(ArchetypeVertex v) {
        return coordinates.get(v);
    }
    public Iterator getVertexIterator() {
        return graph.getVertices().iterator();
    }
    public Dimension getCurrentSize() {
        return new Dimension(width, height);
    }
    public void initialize(Dimension currentSize) {
        TTStep step = new TTStep(maxTimestamp-minTimestamp);

        Point2D.Float point = null;
        Vertex vertex = null;
        TDLink link = null;
        float y_coordinate = 0;
        long timestamp = 0;
//        long timestampMax = ((TDLink) graph.getUserDatum(TDConstants.KEY_GRAPH_LINKOBJECT_MAX)).getTimestamp(); 
//        long timestampMin = ((TDLink) graph.getUserDatum(TDConstants.KEY_GRAPH_LINKOBJECT_MIN)).getTimestamp();
        //width = new Long(maxTimestamp - minTimestamp).intValue();
        long www = (1+((maxTimestamp-minTimestamp)/step.getMinorStep()))*step.getMinorRule();
        width = new Long(www).intValue() + TDConstants.INDENT*2;

        Iterator<Vertex> iterator = graph.getVertices().iterator();

        while (iterator.hasNext()) {
            
            vertex = iterator.next();
            link = (TDLink) vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT);
            
            if (link == null) {
                y_coordinate = y_center;
                for(Vertex neighbor : (Set<Vertex>) vertex.getNeighbors()) {
                    link = (TDLink) neighbor.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT);
                    if (link != null) {
                        timestamp = link.getTimestamp();
                        break;
                    }
                }
            } else if (link.getInitiator().equals(link.getPair().getObjectOne())) {
                y_coordinate = y_top;
                timestamp = link.getTimestamp();
            } else if (link.getInitiator().equals(link.getPair().getObjectTwo())) {
                y_coordinate = y_bottom;
                timestamp = link.getTimestamp();
            }
            point = new Point2D.Float();
            //delta точка времени относительно начала отсчета
            long delta = timestamp - minTimestamp;
            long loopMajor = 0;
            Long majorX = 0L;
            for (long i = step.getMajorStep(); i<delta; i += step.getMajorStep()){
                majorX += step.getMajorRule();
                loopMajor ++;
            }
            /*
            X = целая часть деления = delta/step.getMajorStep()
            smallDelta = вычитаем целую часть от деления *step.getMajorStep() от delta
            x = целая часть от деления smallDelta/step.getMinorStep()

                координата = X * getMajorRule() + x * step.getMinorRule();
             */
            long smallDelta = delta - step.getMajorStep()*loopMajor;
            long loopMinor =0;
            Long minorX = 0L;
            for (long i =step.getMinorStep(); i<smallDelta; i += step.getMinorStep()){
                minorX += step.getMinorRule();
                loopMinor ++;
            }

            long alpha = smallDelta - step.getMinorStep()*loopMinor;
            int alphaCorrect = 0;
            if (alpha < (step.getMinorStep()/3)){
                //do nothing
            }
            else if(alpha > 2*(step.getMinorStep()/3)){
                alphaCorrect = (step.getMinorRule()*2)/3;
            }
            else{
                alpha = step.getMinorRule()/3;
            }

            //2 px просто сдвиг чтобы событие было справа от шкалы
            //point.x = majorX+minorX+2L;
            point.x = majorX+minorX+alphaCorrect+TDConstants.INDENT;
            point.y = y_coordinate;
            coordinates.put(vertex, point);
        }
        integrateLinks();
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

    public ArrayList<Vertex> getVertexesByPoint(Point2D p){
        ArrayList<Vertex> vertexes = new ArrayList<Vertex>();
        for(Vertex v: coordinates.keySet()){
            Point2D vertexPoint = (Point2D)coordinates.get(v);
            if (vertexPoint == null) continue;
            if (vertexPoint.equals(p))
                vertexes.add(v);
        }
        return vertexes;
    }
    private void integrateLinks() {
        Point2D point = null;
        ArrayList<Vertex> vertexes = null;
        for(Vertex v: coordinates.keySet()) {
            point = coordinates.get(v);
            vertexes = getVertexesByPoint(point);
            if (vertexes.size() > 1)
                for(Vertex vertex : vertexes) {
                    vertex.setUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT_INTEGRATED, new Object(), new UserDataContainer.CopyAction.Shared());
                }
        }
    }
}