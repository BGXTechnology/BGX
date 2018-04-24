package net.bgx.bgxnetwork.bgxop.gui.background;

import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;

import java.util.logging.Logger;
import java.util.Set;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

/**
 * Class CardRequestThread
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class RemoveEdgesThread extends ServerThread {
  private static Logger log = Logger.getLogger(RemoveEdgesThread.class.getName());
  private Query q;
  private Set<Edge> edges;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  public RemoveEdgesThread(WaitDialog dialog, Query q, Set<Edge> edges) {
    super(dialog);
    this.q = q;
    this.edges=edges;
  }

  public void run() {
    ArrayList<Integer> idEdges = new ArrayList<Integer>();
      for (Edge edge : edges)
      idEdges.add(GraphNetworkUtil.getID(edge));
    try {
      QueryServiceDelegator.getInstance().removeFromGraph(q.getId(), null, idEdges);
      ex = null;
    } catch (Exception e1) {
      ex = e1;
    }

    finish();
  }

  public Object getResult() {
    return null;  
  }
}
