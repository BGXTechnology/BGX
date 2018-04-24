package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.ArchetypeEdge;

/**
 * Class CustomEdgeStringer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomEdgeStringer implements EdgeStringer {

  protected String label;

  public CustomEdgeStringer(String label) {
    this.label = label;
  }

  public String getLabel(ArchetypeEdge archetypeEdge) {
    return label;
  }
}
