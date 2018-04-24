package net.bgx.bgxnetwork.bgxop.gui.table;
import java.util.Hashtable;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/*

 $Id$

 $DateTime$

 $Change$

 $Author $

 */
public class TableHelper {
	protected FlexibleTable table;
	private DefaultTableModel tableModel;
	private DefaultTableModel rowHeaderModel;
	private Object[] header;
	private TableSortHeader sorter;
	public TableHelper(FlexibleTable table, Object[] header, boolean isHeaderAction) {
		this.header = header;
		this.table = table;
		initTable();
	}
	public TableHelper(FlexibleTable table, Object[] header, DefaultTableModel model, boolean isHeaderAction) {
		this.header = header;
		this.table = table;
		tableModel = model;
		initTable();
	}
	private void initTable() {
		if (tableModel == null) {
			tableModel = new NoEditableTableModel(header, 0);
			rowHeaderModel = new RowHeaderTableModel();
			table.getRowHeader().setModel(rowHeaderModel);
			table.getRowHeader().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			if (sorter == null) {
				sorter = new TableSortHeader(tableModel);
			} else {
				sorter.setTableModel(tableModel);
			}
			table.setModel(sorter);
			sorter.setTableHeaderRenderer(table.getTableHeader());
			setMultylineColumn();
		} else {
			table.setModel(tableModel);
			setMultylineColumn();
		}
	}
	private void setMultylineColumn() {
		MultiLineCellRenderer rendererC = new MultiLineCellRenderer();
		TableColumnModel columnModel = table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn column = columnModel.getColumn(i);
			column.setCellRenderer(rendererC);
		}
	}
	public void setValueAt(int row, int column, Object value) {
		tableModel.setValueAt(value, row, column);
	}
	public void addRow(Object[] row) {
		tableModel.addRow(row);
		rowHeaderModel.addRow(new Object[] { "Line: " + 1 });
	}
	public void removeRow(int row) {
		tableModel.removeRow(row);
		rowHeaderModel.removeRow(row);
	}
	public Hashtable<Object, Object> getRow(int row) {
		Hashtable<Object, Object> out = new Hashtable<Object, Object>();
		int length = header.length;
		for (int i = 0; i < length; i++) {
			if (sorter.getValueAt(row, i) == null) {
				out.put(header[i], "");
			} else {
				out.put(header[i], sorter.getValueAt(row, i));
			}
		}
		return out;
	}
	public void clear() {
		initTable();
	}
	public DefaultTableModel getTableModel() {
		return tableModel;
	}
	public void setDefineSortingStatus(int column, int status) {
		sorter.setDefineSortingStatus(column, status);
	}
	public JTable getTable() {
		return table;
	}
	public void setColumnMinWidth(int column, int width) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		col.setMinWidth(width);
	}
	public void setColumnMaxWidth(int column, int width) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		col.setMaxWidth(width);
	}
	public void setHiddenColumn(int column) {
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn col = columnModel.getColumn(column);
		table.removeColumn(col);
	}
	public JScrollPane getScrollWithRowHeader() {
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(table);
	//	table.initRowHeader(scroll);
		return scroll;
	}
}
