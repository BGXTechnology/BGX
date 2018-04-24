package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * Class ComplexId
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ComplexId implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long objectId;
  private Integer srcId;

  public ComplexId() {
  }

  public ComplexId(Long objectId, Integer srcId) {
    this.objectId = objectId;
    this.srcId = srcId;
  }

  public Long getObjectId() {
    return objectId;
  }

  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  public Integer getSrcId() {
    return srcId;
  }

  public void setSrcId(Integer srcId) {
    this.srcId = srcId;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ComplexId complexId = (ComplexId) o;

    if (objectId != null ? !objectId.equals(complexId.objectId) : complexId.objectId != null) return false;
    if (srcId != null ? !srcId.equals(complexId.srcId) : complexId.srcId != null) return false;

    return true;
  }

  public int hashCode() {
    int result;
    result = (objectId != null ? objectId.hashCode() : 0);
    result = 29 * result + (srcId != null ? srcId.hashCode() : 0);
    return result;
  }

  public String toString() {
    return "("+srcId+","+objectId+")";
  }
}
