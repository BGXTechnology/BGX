package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;

import javax.swing.*;

import net.bgx.bgxnetwork.bgxop.graph.NotationModel;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;

import java.util.*;

import oracle.spatial.network.Node;

/**
 * Class VertexListTableModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class NodeListTableModel extends AbstractFormatTableModel {
  protected List<Node> vlist = new ArrayList<Node>();
  protected List<FieldObject> hlist = new ArrayList<FieldObject>();
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
  protected ResourceBundle trb = PropertyResourceBundle.getBundle("transfer");

  public NodeListTableModel(List<Node> vlist, List<FieldObject> headers) {
    this.vlist = vlist;
      this.hlist = headers;
  }

  public int getRowCount() {
    return vlist.size();
  }

  public int getColumnCount() {
    return hlist.size();
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
      ControlObject conrolObject = GraphNetworkUtil.getControlObject(getObjectAt(rowIndex));
      ObjectWorker worker = new ObjectWorker(conrolObject);
      FieldObject fieldObject = hlist.get(columnIndex);
      return worker.getValueByPropertyCode(fieldObject.getCode());
  }

  public Node getObjectAt(int rowIndex) {
    return vlist.get(rowIndex);
  }

  public int indexOf(Object o) {
    return vlist.indexOf(o);
  }

  public int getRowHeight() {
    //Icon icon = NotationModel.getInstance().getIcon4Object(ObjectType.FL);
    return 27;//icon.getIconHeight()+2;
  }

  public int getColumnMinWidth(int columnIndex) {
    return 75;
  }

    public int getColumnMaxWidth(int columnIndex) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getDefaultResizableColumn() {
      return 3;
    }

  public String getColumnName(int columnIndex) {
    return hlist.get(columnIndex).getCaption();
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
  }

  public int sortByColumn(int column) {
    //if (column<=0 || column>=getColumnCount()) return 0;
    prepareSort(column);
      Collections.sort(vlist, new StringComparator(column, asc));
    fireTableDataChanged();
    return asc ? 1 : -1;
  }


  protected class StringComparator implements Comparator<Node> {
    private int column = 0;
    private boolean asc;

    public StringComparator(int column, boolean asc) {
      this.column = column;
      this.asc = asc;
    }

    public int compare(Node o1, Node o2) {
      int n = asc ? 1 : -1;
      String s1 = (String) getValueAt(indexOf(o1), column);
      String s2 = (String) getValueAt(indexOf(o2), column);
      if (s1 == null) s1 = "";
      if (s2 == null) s2 = "";  
      int res = compareStrings(s1, s2);
      if (res!=0) return n*res;
      return n*compareStrings(GraphNetworkUtil.getName(o1),
          GraphNetworkUtil.getName(o2));
    }
  }

  protected class String2LongComparator implements Comparator<Node> {
    private int column = 0;
    private boolean asc;

    public String2LongComparator(int column, boolean asc) {
      this.column = column;
      this.asc = asc;
    }

    public int compare(Node o1, Node o2) {
      int n = asc ? 1 : -1;
      String s1 = (String) getValueAt(indexOf(o1), column);
      String s2 = (String) getValueAt(indexOf(o2), column);
      int res = compareStringsAsLongs(s1, s2);
      if (res!=0) return n*res;
      else  return n*compareStrings(GraphNetworkUtil.getName(o1),
          GraphNetworkUtil.getName(o2));
    }
  }

}
