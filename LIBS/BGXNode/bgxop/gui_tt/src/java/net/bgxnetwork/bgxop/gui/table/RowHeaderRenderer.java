package net.bgx.bgxnetwork.bgxop.gui.table;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableCellRenderer;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class RowHeaderRenderer extends DefaultTableCellRenderer {
    private Border noFocusBorder;
    private Border focusBorder;
    private JTable tab;

    public RowHeaderRenderer(JTable table) {
        this.tab = table;
        setOpaque(true);
        setBorder(noFocusBorder);
        Border cell = UIManager.getBorder("TableHeader.cellBorder");
        Border focus = UIManager.getBorder("Table.focusCellHighlightBorder");
        focusBorder = new BorderUIResource.CompoundBorderUIResource(cell, focus);
        Insets i = focus.getBorderInsets(this);
        noFocusBorder = new BorderUIResource.CompoundBorderUIResource(cell, BorderFactory.createEmptyBorder(i.top, i.left, i.bottom,
                i.right));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        if (table != null) {
            if (selected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
                if (focused) {
                    tab.setColumnSelectionAllowed(false);
                    tab.setRowSelectionInterval(row, row);
                    tab.scrollRectToVisible(table.getCellRect(row, 0, false));
                    tab.requestFocus();
                }
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            setFont(table.getFont());
            setEnabled(table.isEnabled());
            if (tab.getRowHeight(row) != 0 && table.getRowHeight(row) != tab.getRowHeight(row)) {
                table.setRowHeight(row, tab.getRowHeight(row));
            }
        } else {
            setBackground(UIManager.getColor("TableHeader.background"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setEnabled(true);
        }
        if (focused) {
            setBorder(focusBorder);
        } else {
            setBorder(noFocusBorder);
        }
        setValue(value);
        return this;
    }
}
