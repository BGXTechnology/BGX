package net.bgx.bgxnetwork.bgxop.engine;

import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.VertexLocationFunction;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

import javax.swing.event.ChangeListener;
import java.util.Iterator;
import java.util.Set;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Class OffscreenLayout
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class OffscreenLayout extends AbstractLayout {
  private AbstractLayout layout;
  private LayoutType layoutType;
  private boolean layoutDone = false;

  public OffscreenLayout(AbstractLayout layout, LayoutType type) {
    super(new SparseGraph());
    this.layout = layout;
    this.layoutType = type;
  }

  public LayoutType getLayoutType() {
    return layoutType;
  }

  public void doLayout(int iterations) {
    if (!layout.isIncremental()) return;
    int i=0;
    while (i<iterations && !layout.incrementsAreDone()) {
      layout.advancePositions();
      i++;
    }
    layoutDone = true;
    fireStateChanged();
  }

  public boolean incrementsAreDone() {
    if (layoutDone) return true;
    return layout.incrementsAreDone();
  }

  protected void initialize_local() {
  }

  protected void postInitialize() {
  }

  protected void initialize_local_vertex(Vertex vertex) {
  }

  protected void initializeLocations() {
  }

  protected void initializeLocation(Vertex vertex, Coordinates coordinates, Dimension dimension) {
  }

  protected Graph getVisibleGraph() {
    return null;
  }

  protected Vertex getAVertex(Edge edge) {
    return null;
  }

  protected void offsetVertex(Vertex vertex, double v, double v1) {
  }

  public boolean dontMove(Vertex vertex) {return layout.dontMove(vertex);}

  public Iterator getVertexIterator() {return layout.getVertexIterator();}

  public void initialize(Dimension dimension) {layout.initialize(dimension);}

  public void initialize(Dimension dimension, VertexLocationFunction vertexLocationFunction) {layout.initialize(dimension, vertexLocationFunction);}

  public Object getBaseKey() {return layout.getBaseKey();}

  public String getStatus() {return layout.getStatus();}

  public void advancePositions() {layout.advancePositions();}

  public Dimension getCurrentSize() {return layout.getCurrentSize();}

  public Coordinates getCoordinates(ArchetypeVertex archetypeVertex) {return layout.getCoordinates(archetypeVertex);}

  public double getX(Vertex vertex) {return layout.getX(vertex);}

  public double getY(Vertex vertex) {return layout.getY(vertex);}

  public Point2D getLocation(ArchetypeVertex archetypeVertex) {return layout.getLocation(archetypeVertex);}

  public void resize(Dimension dimension) {layout.resize(dimension);}

  public void restart() {layout.restart();}

  public Vertex getVertex(double v, double v1) {return layout.getVertex(v, v1);}

  public Vertex getVertex(double v, double v1, double v2) {return layout.getVertex(v, v1, v2);}

  public Edge getEdge(double v, double v1) {return layout.getEdge(v, v1);}

  public Edge getEdge(double v, double v1, double v2) {return layout.getEdge(v, v1, v2);}

  public Graph getGraph() {return layout.getGraph();}

  public Set getVisibleEdges() {return layout.getVisibleEdges();}

  public Set getVisibleVertices() {return layout.getVisibleVertices();}

  public void forceMove(Vertex vertex, double v, double v1) {layout.forceMove(vertex, v, v1);}

  public void lockVertex(Vertex vertex) {layout.lockVertex(vertex);}

  public void unlockVertex(Vertex vertex) {layout.unlockVertex(vertex);}

  public void applyFilter(Graph graph) {layout.applyFilter(graph);}

  public void addChangeListener(ChangeListener changeListener) {layout.addChangeListener(changeListener);}

  public void removeChangeListener(ChangeListener changeListener) {layout.removeChangeListener(changeListener);}

  public ChangeListener[] getChangeListeners() {return layout.getChangeListeners();}

  public void fireStateChanged() {layout.fireStateChanged();}

  public boolean isIncremental() {return layout.isIncremental();}

}
