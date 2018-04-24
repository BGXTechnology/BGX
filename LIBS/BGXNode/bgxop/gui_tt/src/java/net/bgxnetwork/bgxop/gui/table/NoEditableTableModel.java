package net.bgx.bgxnetwork.bgxop.gui.table;

import javax.swing.table.DefaultTableModel;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class NoEditableTableModel extends DefaultTableModel {
    public NoEditableTableModel() {
        super();
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public NoEditableTableModel(Object[] header, int row) {
        super(header, row);
    }

    public Class getColumnClass(int column) {
        return (getValueAt(0, column) == null ? "".getClass() : getValueAt(0, column).getClass());
    }
}
