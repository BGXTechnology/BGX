package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.ChangeEventSupport;
import edu.uci.ics.jung.utils.DefaultChangeEventSupport;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.Iterator;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.query.SerialPoint2D;

import javax.swing.event.ChangeListener;

/**
 * Class CustomLayout
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomLayout implements Layout, ChangeEventSupport {
    private LayoutCoordinates coordinates;
    private Graph g;
    private ChangeEventSupport changeSupport = new DefaultChangeEventSupport(this);

    public CustomLayout(LayoutCoordinates coordinates, Graph g) {
        this.coordinates = coordinates;
        this.g = g;
    }

    public void initialize(Dimension dimension) {
    }

    public double getX(Vertex vertex) {
        SerialPoint2D p = coordinates.getCoordinate(GraphNetworkUtil.getID(vertex));
        return p != null ? p.getX() : 1;
    }

    public double getY(Vertex vertex) {
        SerialPoint2D p = coordinates.getCoordinate(GraphNetworkUtil.getID(vertex));

        return p != null ? p.getY() : 1;
    }

    public Point2D getLocation(ArchetypeVertex vertex) {
        SerialPoint2D p = coordinates.getCoordinate(GraphNetworkUtil.getID((Vertex) vertex));
        if (p == null) return null;
        return new Coordinates(p.getX(), p.getY());
    }

    public void applyFilter(Graph graph) {
    }

    public String getStatus() {
        return "";
    }

    public void restart() {
    }

    public Vertex getVertex(double v, double v1) {
        return null;
    }

    public Vertex getVertex(double v, double v1, double v2) {
        return null;
    }

    public Graph getGraph() {
        return g;
    }

    public void resize(Dimension dimension) {
    }

    public void advancePositions() {
    }

    public boolean isIncremental() {
        return false;
    }

    public boolean incrementsAreDone() {
        return true;
    }

    public void lockVertex(Vertex vertex) {
    }

    public void unlockVertex(Vertex vertex) {
    }

    public boolean isLocked(Vertex vertex) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void forceMove(Vertex vertex, double v, double v1) {
        coordinates.getCoordinates().put(GraphNetworkUtil.getID(vertex), new SerialPoint2D(v, v1));
        fireStateChanged();
    }

    public Set getVisibleEdges() {
        return g.getEdges();
    }

    public Set getVisibleVertices() {
        return g.getVertices();
    }

    public Dimension getCurrentSize() {
        return coordinates.getLayoutSize();
    }

    public Iterator getVertexIterator() {
        return g.getVertices().iterator();
    }

    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    public void fireStateChanged() {
        changeSupport.fireStateChanged();
    }

    public ChangeListener[] getChangeListeners() {
        return changeSupport.getChangeListeners();
    }

    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

}
