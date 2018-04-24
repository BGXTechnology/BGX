package net.bgx.bgxnetwork.bgxop.gui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class MultiLineHeaderRenderer extends JPanel implements TableCellRenderer {
    private JTextArea area = new JTextArea();
    private TableSortHeader sorter;

    public MultiLineHeaderRenderer(TableSortHeader sorter) {
        this.sorter = sorter;
        setLayout(new BorderLayout());
        area.setForeground(UIManager.getColor("TableHeader.foreground"));
        area.setBackground(UIManager.getColor("TableHeader.background"));
        area.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        removeAll();
        int modelColumn = table.convertColumnIndexToModel(column);
        JLabel label = new JLabel(sorter.getHeaderRendererIcon(modelColumn, area.getFont().getSize()));
        setLookAndFeel(label);
        label.setOpaque(true);
        add(label, BorderLayout.EAST);
        area.setFont(table.getFont());
        String str = (value == null) ? "" : value.toString();
        area.setText(str);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setSize(table.getColumnModel().getColumn(column).getWidth(), 10);
        add(area, BorderLayout.CENTER);
        return this;
    }

    public void setLookAndFeel(Component component) {
        component.setForeground(UIManager.getColor("TableHeader.foreground"));
        component.setBackground(UIManager.getColor("TableHeader.background"));
    }
}
