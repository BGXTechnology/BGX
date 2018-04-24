package net.bgx.bgxnetwork.bgxop.gui;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.Edge;

import java.util.*;
import java.util.regex.Pattern;

import net.bgx.bgxnetwork.bgxop.engine.GraphNetworkMapper;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;

/**
 * Class GraphDataModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class GraphDataModel implements ISelectable {

  private ArrayList<ISelectable> selectables = new ArrayList<ISelectable>();
  private ArrayList<Vertex> orderedList = new ArrayList<Vertex>();
  private Comparator<Vertex> comparator = new ABCVertexComparator();
  private String latestSearchString = "";
  private int numOfLatestSearchResults = 0;
  private boolean wasLatestSearchByINN = false;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  public GraphDataModel(GraphNetworkMapper data) {
    Vertex v;
    for (Object o : data.getGraph().getVertices()) {
      v = (Vertex) o;
      orderedList.add(v);
    }
    Collections.sort(orderedList, comparator);
  }

  public void sortABC() {
    comparator = new ABCVertexComparator();
    Collections.sort(orderedList, comparator);
  }

  public Comparator<Vertex> getComparator() {
    return comparator;
  }

  public ArrayList<Vertex> getVertices() {
    return orderedList;
  }

  public void addSelectable(ISelectable selectable) {
    selectables.add(selectable);
  }

  public void removeSelectable(ISelectable selectable) {
    selectables.remove(selectable);
  }

  public void clearSelectables() {
    selectables.clear();
  }

  public String getLatestSearchString() {
      return latestSearchString;
  }

  public int getNumOfLatestSearchResults() {
      return numOfLatestSearchResults;
  }

  public boolean wasLatestSearchByINN() {
      return wasLatestSearchByINN;
  }

  public Set<Vertex> findByName(String pattern) {
        Set<Vertex> results = find(pattern, false);
        wasLatestSearchByINN = false;
        latestSearchString = pattern;
        numOfLatestSearchResults = results.size();
        return results;
  }

  public void clearLatestSearchInfo() {
      wasLatestSearchByINN = false;
      latestSearchString = "";
      numOfLatestSearchResults = 0;
  }

  private Set<Vertex> find(String pattern, boolean byInn) {
    HashSet<Vertex> res = new HashSet<Vertex>();
    String ptr = "", val = pattern.trim();
    if (val.startsWith("%")) {
      ptr = ".*";
      val = val.substring(1);
    }
    if (val.endsWith("%")) {
      val = val.substring(0,val.length()-1);
      ptr = ptr + val + ".*";
    } else ptr = ptr+val;

    boolean correctExpression;
    if (byInn) correctExpression = Pattern.matches(rb.getString("Search.pattern.byINN"), val);
    else correctExpression = Pattern.matches(rb.getString("Search.pattern"), val);
    if (!correctExpression) return res;

    String n;
    Pattern p = Pattern.compile(ptr, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    for (Vertex v : orderedList) {
      n = GraphNetworkUtil.getName(v);
      if (n==null) continue;
      if (p.matcher(n).matches()) res.add(v);
    }
    return res;
  }


  public void selectVertices(Set<Vertex> vertices) {
    for (ISelectable s : selectables)
      s.selectVertices(vertices);
  }

  public void selectEdges(Set<Edge> edges) {
    for (ISelectable s : selectables)
      s.selectEdges(edges);
  }

  //****************** comparators *****************************

  static class ABCVertexComparator implements Comparator<Vertex> {
    public int compare(Vertex o1, Vertex o2) {
      if (GraphNetworkUtil.getName(o1)==null) return -1;
      else if (GraphNetworkUtil.getName(o2)==null) return 1;
      return GraphNetworkUtil.getName(o1).compareTo(GraphNetworkUtil.getName(o2));
    }
  }
}
