package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: yerokhin
 * Date: 06.04.2006
 * Time: 12:08:54
 * To change this template use File | Settings | File Templates.
 */
public enum QueryParameterDataType implements Serializable {
  //java.lang.String
  String(0),
  //java.util.List<java.lang.String>
  Array(1);

  private int value;

  QueryParameterDataType(int value) {
   this.value = value;
 }

  public int getValue() {
    return value;
  }

  public static QueryParameterDataType getByValue(int val) {
    switch (val) {
      case 0: return String;
      case 1: return Array;
    }
    return null;
  }
}
