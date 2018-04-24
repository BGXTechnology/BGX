package net.bgx.bgxnetwork.transfer.query.card;

import net.bgx.bgxnetwork.transfer.query.ObjectType;

import java.util.List;
import java.io.Serializable;

/**
 * Class Card
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class Card implements Serializable {
  private long queryId;
  private int nodeId;
  private ObjectType objectType;
  private List<CardTab> tabs;

  public Card(long queryId, int nodeId, ObjectType objectType, List<CardTab> tabs) {
    this.queryId = queryId;
    this.nodeId = nodeId;
    this.objectType = objectType;
    this.tabs = tabs;
  }

  public long getQueryId() {
    return queryId;
  }

  public void setQueryId(long queryId) {
    this.queryId = queryId;
  }

  public int getNodeId() {
    return nodeId;
  }

  public void setNodeId(int nodeId) {
    this.nodeId = nodeId;
  }

  public ObjectType getObjectType() {
    return objectType;
  }

  public void setObjectType(ObjectType objectType) {
    this.objectType = objectType;
  }

  public List<CardTab> getTabs() {
    return tabs;
  }

  public void setTabs(List<CardTab> tabs) {
    this.tabs = tabs;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Card card = (Card) o;

    if (nodeId != card.nodeId) return false;
    if (queryId != card.queryId) return false;
    if (objectType != card.objectType) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (int) (queryId ^ (queryId >>> 32));
    result = 29 * result + nodeId;
    result = 29 * result + (objectType != null ? objectType.hashCode() : 0);
    return result;
  }
}
