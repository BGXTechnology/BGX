package net.bgx.bgxnetwork.bgxop.gui;

import java.awt.Point;
import java.util.Set;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

/**
 * Interface IVertexContainer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IVertexContainer {

    public Vertex getVertexByPoint(Point p);

    public Object[] getSelectedVertices();

    public void selectVertexByPoint(Point p);

}
