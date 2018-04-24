package net.bgx.bgxnetwork.bgxop.gui.table;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.table.JTableHeader;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class FlexibleTable extends JTable {
    private JTable rowHeader = null;
    private int HEADER_WIDTH = 25;

    public FlexibleTable() {
        super();
        rowHeader = new JTable();
    }

    public void initRowHeader(JScrollPane scroll) {
        LookAndFeel.installColorsAndFont(rowHeader, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");
        rowHeader.setIntercellSpacing(new Dimension(0, 0));
        Dimension d = rowHeader.getPreferredScrollableViewportSize();
        d.width = HEADER_WIDTH;
        rowHeader.setPreferredScrollableViewportSize(d);
        rowHeader.setDefaultRenderer(Object.class, new RowHeaderRenderer(this));
        scroll.setRowHeaderView(rowHeader);
        JTableHeader corner = rowHeader.getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);
        scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);
    }

    public void repaint() {
        if (rowHeader != null) {
            rowHeader.revalidate();
            rowHeader.repaint();
        }
        super.repaint();
    }

    public JTable getRowHeader() {
        return rowHeader;
    }
}
