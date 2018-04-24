package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

import java.util.Set;

/**
 * Class ISelectable
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface ISelectable {

  void selectVertices(Set<Vertex> vertices);

  void selectEdges(Set<Edge> edges);

}
