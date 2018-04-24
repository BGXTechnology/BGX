package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

import java.awt.*;

/**
 * Interface IVertexContainer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IEdgeContainer {

    Edge getEdgeByPoint(Point p);

    Object[] getSelectedEdges();

    void selectEdgeByPoint(Point p);

    int getParallelIndex(Edge e);

    public Vertex getVertexByPoint(Point p);
}
