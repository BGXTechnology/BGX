package net.bgx.bgxnetwork.persistence.query;

import java.util.HashMap;
import java.io.Serializable;

/**
 * Class GraphAnnotation
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class GraphAnnotation implements Serializable {
  private static final long serialVersionUID = 1L;
  private String networkName;
  private String keyName;
  private HashMap<Integer, String> annotations;

  public GraphAnnotation(String networkName, String keyName, HashMap<Integer, String> annotations) {
    this.networkName = networkName;
    this.keyName = keyName;
    this.annotations = annotations;
  }

  public GraphAnnotation(String networkName, String keyName) {
    this.networkName = networkName;
    this.keyName = keyName;
    this.annotations = new HashMap<Integer, String>();
  }

  public String getNetworkName() {
    return networkName;
  }

  public String getKeyName() {
    return keyName;
  }

  public HashMap<Integer, String> getAnnotations() {
    return annotations;
  }

  public void setNetworkName(String networkName) {
    this.networkName = networkName;
  }

  public void setKeyName(String keyName) {
    this.keyName = keyName;
  }

  public void setAnnotations(HashMap<Integer, String> annotations) {
    this.annotations = annotations;
  }

  public void addAnnotation(int vertexId, String annotation) {
    annotations.put(vertexId, annotation);
  }

  public String getAnnotation(int vertexId) {
    return annotations.get(vertexId);
  }
}
