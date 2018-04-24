package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.table.TableModel;

/**
 * Class IFormatTableModel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IFormatTableModel extends TableModel {
  int getRowHeight();

  int getColumnMinWidth(int columnIndex);

  int getColumnMaxWidth(int columnIndex);

  int getDefaultResizableColumn();

  int sortByColumn(int column);

  Object getObjectAt(int row);

  int indexOf(Object o);
}
