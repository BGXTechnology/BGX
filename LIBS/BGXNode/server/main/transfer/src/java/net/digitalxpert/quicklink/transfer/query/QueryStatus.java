package net.bgx.bgxnetwork.transfer.query;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: yerokhin
 * Date: 04.04.2006
 * Time: 13:25:34
 * To change this template use File | Settings | File Templates.
 */
public enum QueryStatus implements Serializable {
  /**
   * Query lifetime:
   * [not saved] -> [saved] -> [saved, execution in progress] -> [saved, results ready]
   *                                                          -> [execution complete with errors]
   *                                                          -> [results ready, data truncated]
   */
  NotSaved(1),
  Saved(2),
  Executing(3),
  Ready(4),
  Error(5),
  Limited(6);

  private int value;

  QueryStatus(int value) {
   this.value = value;
 }

  public int getValue() {
    return value;
  }

  public static QueryStatus getByValue(int val) {
    for (QueryStatus qs : QueryStatus.values())
      if (qs.getValue()==val) return qs;
    return null;
  }
}
