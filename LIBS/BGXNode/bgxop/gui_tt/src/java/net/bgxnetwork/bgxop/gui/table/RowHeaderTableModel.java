package net.bgx.bgxnetwork.bgxop.gui.table;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class RowHeaderTableModel extends DefaultTableModel {
    private final String HEADER_SYMBOL = "#";

    public RowHeaderTableModel() {
        super(0, 1);
        Vector<String> v = new Vector<String>(1);
        v.addElement(HEADER_SYMBOL);
        setDataVector(new Vector(0), v);
    }

    public void addRow(Object[] row) {
        Object[] r = { getRowCount() + 1 };
        super.addRow(r);
    }

    public void removeRow(int row) {
    }
}
