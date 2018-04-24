package net.bgx.bgxnetwork.bgxop.gui.background;

import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;

import java.util.logging.Logger;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.ArrayList;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Class CardRequestThread
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class RemoveVerticesThread extends ServerThread {
  private static Logger log = Logger.getLogger(RemoveVerticesThread.class.getName());
  private Query q;
  private Vertex v;
  private Set<Edge> eset;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  public RemoveVerticesThread(WaitDialog dialog, Query q, Vertex vertex, Set<Edge> edges) {
    super(dialog);
    this.q = q;
    v = vertex;
    eset = edges;
  }

  public void run() {
    ArrayList<Integer> vertices = new ArrayList<Integer>();
    vertices.add(GraphNetworkUtil.getID(v));
    ArrayList<Integer> edges = new ArrayList<Integer>();
    for (Edge e : eset)
      edges.add(GraphNetworkUtil.getID(e));
    try {
      QueryServiceDelegator.getInstance().removeFromGraph(q.getId(), vertices, edges);
      ex = null;
    } catch (Exception e1) {
      ex = e1;
    }

    finish();
  }

  public Object getResult() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
