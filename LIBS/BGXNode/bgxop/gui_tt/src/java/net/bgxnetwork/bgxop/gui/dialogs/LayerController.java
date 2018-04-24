package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import net.bgx.bgxnetwork.transfer.query.LinkType;

import java.util.ArrayList;
import java.util.Set;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.utils.UserDataContainer;
import ru.zsoft.jung.viewer.BufferedViewer;

/**
 * Class LayerController
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class LayerController {
  private static final String key_layers = "key_layers";
  private static final String key_show_disconnected = "key_show_disconnected";

  private BufferedViewer viewer;
  private LayerDialog view = null;

  public LayerController(BufferedViewer viewer) {
    this.viewer = viewer;
  }

  public void setView(LayerDialog view) {
    this.view = view;
  }

  public LayerDialog getView() {
    return view;
  }

  protected void loadData() {
    ArrayList<LinkType> layers = (ArrayList<LinkType>) viewer.getGraphLayout().
        getGraph().getUserDatum(key_layers);
    boolean show = true;
    Boolean b = (Boolean) viewer.getGraphLayout().getGraph().getUserDatum(key_show_disconnected);
    if (b!=null) show = b.booleanValue();
    view.loadData(layers, show);
  }

  protected void setVisibleLayers(ArrayList<LinkType> types, boolean showDiscon) {
    Graph g = viewer.getGraphLayout().getGraph();
    Set edges = g.getEdges();
    Edge e;
    for (Object o : edges) {
      e = (Edge) o;
      GraphDataUtil.setVisible(e, types.contains(GraphNetworkUtil.getType(e)));
    }
    Set vertices = g.getVertices();
    Vertex v;
    boolean visible;
    for (Object o : vertices) {
      v = (Vertex) o;
      visible = showDiscon;
      if (!visible) {
        edges = v.getIncidentEdges();
        for (Object o1 : edges)
          if (GraphDataUtil.getVisible((Edge)o1)) {
            visible = true;
            break;
          }
      }
      GraphDataUtil.setVisible(v, visible);
    }
    g.setUserDatum(key_layers, types, new UserDataContainer.CopyAction.Shared());
    g.setUserDatum(key_show_disconnected, showDiscon, new UserDataContainer.CopyAction.Shared());
  }

  protected void apply(ArrayList<LinkType> types, boolean showDesc) {
    setVisibleLayers(types, showDesc);
    viewer.unlock();
    viewer.repaint();
    view.close();
  }

  protected void revert() {
    ArrayList<LinkType> typesArr = new ArrayList<LinkType>();
    for (LinkType type : LinkType.values())
      typesArr.add(type);
    setVisibleLayers(typesArr, true);
    viewer.unlock();
    viewer.repaint();
    view.close();
  }
}
