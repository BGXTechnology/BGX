package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

/**
 * Class IObjectSelectionListener
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IObjectSelectionListener {

  void objectSelected(Vertex v);
  void linkSelected(Edge e);  

}
