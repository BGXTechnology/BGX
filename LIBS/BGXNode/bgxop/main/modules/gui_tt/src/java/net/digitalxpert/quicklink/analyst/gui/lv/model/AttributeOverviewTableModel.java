package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import java.util.Hashtable;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class AttributeOverviewTableModel extends DefaultTableModel{
	private Hashtable<Integer,FieldObject> data=new Hashtable<Integer, FieldObject>();
	private int row=0;
	private List<FieldObject> list;
	private String[] headers;
	public AttributeOverviewTableModel(String[] headers,List<FieldObject> list){
		this.list=list;
		this.headers=headers;
		insertData();
	}
	private void insertData() {
		for(String h:headers){
			addColumn(h);
		}
		for(FieldObject o:list){
			addRow(new Object[]{o.getCaption(),o.isVisible()});
			data.put(Integer.valueOf(row), o);
			row++;
		}
	}
	public boolean isCellEditable(int row, int column){
		return (column==1);
	}
	public FieldObject getRowData(int column) throws Exception{
		return data.get(Integer.valueOf(column));
	}
	public Class getColumnClass(int column){
		return (getValueAt(0,column).getClass());
	}
}
