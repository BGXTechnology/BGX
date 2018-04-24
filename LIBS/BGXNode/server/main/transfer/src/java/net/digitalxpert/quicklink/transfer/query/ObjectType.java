package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * User: yerokhin
 * Date: 04.04.2006
 * Time: 13:34:53
 */
public enum ObjectType implements Serializable {
  SIMPLE_OBJECT(1),
  LINK_OBJECT(2);  

  private static final long serialVersionUID = 1L;
  private int value;

  ObjectType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static ObjectType getByValue(int value) {
    for (ObjectType ot : ObjectType.values())
      if (ot.getValue()==value) return ot;
    return null;
  }
}
