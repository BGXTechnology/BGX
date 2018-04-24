package net.bgx.bgxnetwork.bgxop.gui.table;

import java.awt.Component;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
    private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();
    private final Map<Integer, Map<Integer, Integer>> cellSizes = new HashMap<Integer, Map<Integer, Integer>>();

    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
        adaptee.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().getHeight();
        addSize(row, column, height_wanted);
        height_wanted = findTotalMaximumRowSize(table, row);
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        if (obj instanceof Icon) {
            JLabel label = new JLabel();
            label.setIcon((Icon) obj);
            label.setForeground(adaptee.getForeground());
            label.setBackground(adaptee.getBackground());
            label.setBorder(adaptee.getBorder());
            label.setOpaque(true);
            return label;
        } else {
            return this;
        }
    }

    private void addSize(int row, int column, int height) {
        Map<Integer, Integer> rowheights = cellSizes.get(row);
        if (rowheights == null) {
            rowheights = new HashMap<Integer, Integer>();
            cellSizes.put(row, rowheights);
        }
        rowheights.put(column, height);
    }

    private int findTotalMaximumRowSize(JTable table, int row) {
        int maximum_height = 0;
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = (TableColumn) columns.nextElement();
            TableCellRenderer cellRenderer = tc.getCellRenderer();
            if (cellRenderer instanceof MultiLineCellRenderer) {
                MultiLineCellRenderer tar = (MultiLineCellRenderer) cellRenderer;
                maximum_height = Math.max(maximum_height, tar.findMaximumRowSize(row));
            }
        }
        return maximum_height;
    }

    public int findMaximumRowSize(int row) {
        Map<Integer, Integer> rowheights = cellSizes.get(row);
        if (rowheights == null)
            return 0;
        int maximum_height = 0;
        for (Iterator<Entry<Integer, Integer>> it = rowheights.entrySet().iterator(); it.hasNext();) {
            Entry<Integer, Integer> entry = it.next();
            int cellHeight = entry.getValue();
            maximum_height = Math.max(maximum_height, cellHeight);
        }
        return maximum_height;
    }
}
