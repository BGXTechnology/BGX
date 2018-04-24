package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.Edge;

import java.awt.*;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

/**
 * Class CustomEdgePaintFunction
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CustomEdgePaintFunction implements EdgePaintFunction {
  public Paint getDrawPaint(Edge edge) {
    LinkType type = GraphNetworkUtil.getType(edge);
    return NotationModel.getInstance().getColor4Link(type);
  }

  public Paint getFillPaint(Edge edge) {
    return null;
  }
}
