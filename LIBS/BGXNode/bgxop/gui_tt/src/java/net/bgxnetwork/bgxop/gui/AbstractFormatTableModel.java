package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.table.AbstractTableModel;

/**
 * Class AbstractFormatTableModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public abstract class AbstractFormatTableModel extends AbstractTableModel implements IFormatTableModel {
  protected int lastSortColumn = -1;
  protected boolean asc = true;

  public static int compareStrings(String s1, String s2) {
    if (s1==null || s1.length()==0) {
      if (s2==null || s2.length()==0) return 0;
      else return -1;
    }
    else if (s2 == null || s2.length() == 0){
        if (s1 == null || s1.length() == 0) return 0;
        else return 1;
    }
    else
      return s1.compareTo(s2);
  }

  public static int compareStringsAsLongs(String s1, String s2) {
    if (s1==null || s1.length()==0) {
      if (s2==null || s2.length()==0) return 0;
      else return -1;
    } else if (s2==null || s2.length()==0) return 1;
    long l1 = Long.MAX_VALUE, l2 = Long.MAX_VALUE;
    try {
      l1 = Long.parseLong(s1);
    } catch (Exception e) {}
    try {
      l2 = Long.parseLong(s2);
    } catch (Exception e) {}
    if (l1<l2) return -1;
    else if (l1>l2) return 1;
    return 0;
  }

  protected void prepareSort(int column) {
    if (lastSortColumn==column) asc = !asc;
    else asc = true;
    lastSortColumn = column;
  }

}
