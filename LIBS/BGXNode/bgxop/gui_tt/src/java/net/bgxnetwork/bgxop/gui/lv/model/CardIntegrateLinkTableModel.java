package net.bgx.bgxnetwork.bgxop.gui.lv.model;
import java.util.*;
import javax.swing.JTable;
import javax.swing.table.*;

import net.bgx.bgxnetwork.bgxop.gui.table.MultiLineCellRenderer;
import net.bgx.bgxnetwork.bgxop.gui.AbstractFormatTableModel;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkValue;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.LinkPK;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class CardIntegrateLinkTableModel extends /*DefaultTableModel*/ AbstractTableModel {
    ArrayList<FieldObject> fields;
    protected List<LinkObject> list;
    private HashMap<LinkPK, Boolean> changes = new HashMap<LinkPK, Boolean>();
    private JTable table;

    public CardIntegrateLinkTableModel(ArrayList<FieldObject> fields, List<LinkObject> list, JTable table){
        this.fields = fields;
        this.list=list;
        this.table=table;
        insertData();
    }
    private void insertData() {
        setMultylineColumn();
    }

    public boolean isCellEditable(int row, int column) {
        return (column == 0);
    }

    public List<LinkObject> getCheckedObjects(){
        for (LinkObject lo : list){
            if (changes.get(lo.getPk()) != null){
                LinkWorker worker = new LinkWorker(lo);
                worker.setVisibleInIntegratedLink(changes.get(lo.getPk()));
            }
        }
        return list;
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return fields.size();
    }

    public Class getColumnClass(int column) {
        if (column == 0) return Boolean.class;
        return String.class;
    }

    private void setMultylineColumn() {
        MultiLineCellRenderer rendererC = new MultiLineCellRenderer();
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setCellRenderer(rendererC);
        }
    }

    public Object getValueAt(int row, int column) {
        LinkObject linkObject = list.get(row);
        LinkWorker linkWorker = new LinkWorker(linkObject);
        if (column == 0) {
            if (changes.get(linkObject.getPk())!=null)
                return changes.get(linkObject.getPk());
            return linkWorker.isVisibleInIntegratedLink();
        }
        FieldObject fo = fields.get(column);
        return linkWorker.getValueByPropertyCode(fo.getCode());
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
       if (columnIndex == 0){
            boolean flag = (Boolean)aValue;
            LinkObject linkObject = list.get(rowIndex);
            changes.put(linkObject.getPk(), flag);
        }
    }

    public String getColumnName(int column) {
        return fields.get(column).getCaption();
    }
}
