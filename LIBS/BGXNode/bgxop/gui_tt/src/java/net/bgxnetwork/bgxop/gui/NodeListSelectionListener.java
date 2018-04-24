package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Set;
import java.util.HashSet;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;

/**
 * Class TableSelectionListener
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class NodeListSelectionListener implements ListSelectionListener, ISelectable {

  private NodeListTable target;
  private GraphDataModel model;
  private boolean lockRecursion = false;

  public NodeListSelectionListener(NodeListTable target, GraphDataModel model) {
    this.target = target;
    this.model = model;
    model.addSelectable(this);
  }

  public void selectVertices(Set<Vertex> vertices) {
    if (lockRecursion) return;
    lockRecursion = true;

    ListSelectionModel selection = target.getSelectionModel();
    selection.setValueIsAdjusting(true);
    selection.clearSelection();

    int idx;

    for (Vertex o : vertices) {
      idx = target.getNodeListModel().indexOf(GraphNetworkUtil.getNode(o));
      if (idx>=0) selection.addSelectionInterval(idx,idx);
    }
    selection.setValueIsAdjusting(false);

    lockRecursion = false;
  }

  public void selectEdges(Set<Edge> edges) {
 /*    if (lockRecursion) return;
    lockRecursion = true;

    ListSelectionModel selection = target.getSelectionModel();
    selection.setValueIsAdjusting(true);
    selection.clearSelection();

    int idx;

    for (Edge o : edges) {
      idx = target.getNodeListModel().indexOf(GraphNetworkUtil.getLinkObject(o));
      if (idx>=0) selection.addSelectionInterval(idx,idx);
    }
    selection.setValueIsAdjusting(false);

    lockRecursion = false;
 */ }

  public void valueChanged(ListSelectionEvent e) {
    if (lockRecursion) return;
    if (e.getValueIsAdjusting()) return;
    lockRecursion = true;

    HashSet<Vertex> set = new HashSet<Vertex>();
    for (int i : target.getSelectedRows()) {
      set.add(GraphNetworkUtil.getVertex(target.getNodeListModel().getObjectAt(i)));
    }
    model.selectVertices(set);

    lockRecursion = false;
  }
}
