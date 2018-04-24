package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import java.util.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.AbstractTableModel;

import net.bgx.bgxnetwork.bgxop.gui.table.MultiLineCellRenderer;
import net.bgx.bgxnetwork.bgxop.tools.lv.ObjectWorker;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyVal;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class HiddenObjectTableModel extends /*DefaultTableModel*/AbstractTableModel {
    private Object[] headers;
    private List<ControlObject> list;
    private HashMap<NodePK, Boolean> changes = new HashMap<NodePK, Boolean>();
//    private Hashtable<Integer,ControlObject> data=new Hashtable<Integer, ControlObject>();
//    private int rowCount=0;
    private JTable table;

    public HiddenObjectTableModel(Object[] headers, List<ControlObject> list, JTable table) {
        this.headers=headers;
        this.list=list;
        this.table=table;
        insertData();
    }

    private void insertData() {
        setMultylineColumn();
//        for(Object h:headers){
//            addColumn(((FieldObject)h).getCaption());
//        }

//        for (ControlObject l : list) {
//            List<PropertyVal> vl = getList(l);
//            Object[] row = new Object[vl.size() + 1];
//            row[0] = new ObjectWorker(l).isVisible();
//            int count = 1;
//            for (PropertyVal propertyVal : vl) {
//                if (propertyVal == null) continue;
//                row[count++] = propertyVal.getValueObject();
//            }
//            addRow(row);
//
//            data.put(rowCount++, l);
//        }
    }
    private List<PropertyVal> getList(ControlObject object) {
        List<PropertyType> listType = object.getTypeObject().getPropertyTypes();
        List<PropertyVal> out = new ArrayList<PropertyVal>();
        for (PropertyType t : listType) {
            if (t != null) {
                int id = t.getPropertyTypeId().intValue();
                out.add(getValue(id, object));
            }
        }
        return out;
    }

    private PropertyVal getValue(int id, ControlObject object) {
        Set<PropertyVal> values = object.getPropertyVals();
        for (PropertyVal v : values) {
            if (v.getPropertyType().getPropertyTypeId().intValue() == id) {
                return v;
            }
        }
        return null;
    }


    public boolean isCellEditable(int row, int column) {
        return (column == 0);
    }

    public Object getValueAt(int row, int column) {
        ControlObject conrolObject = list.get(row);
        ObjectWorker worker = new ObjectWorker(conrolObject);
        if (column == 0){
            Boolean visibleFlag = changes.get(conrolObject.getPk());
            if (visibleFlag == null)
                return worker.isVisible();
            else
                return visibleFlag;
        }

        FieldObject fo = (FieldObject)headers[column];
        return worker.getValueByPropertyCode(fo.getCode());
    }

    public void setValueAt(Object aValue, int row, int column) {
        if (column == 0){
            boolean flag = (Boolean)aValue;
            ControlObject controlObject = list.get(row);
            changes.put(controlObject.getPk(), flag);
        }
    }

//    public List<ControlObject> getCheckedObjects(){
//        List<ControlObject> out=new ArrayList<ControlObject>();
//
//        Set<Integer>keys=data.keySet();
//        for(Integer i:keys){
//            if(getValueAt(i.intValue(), 0).toString().equalsIgnoreCase("true")){
//                out.add(getControlObject(i.intValue()));
//            }
//        }
//        return out;
//    }

    public List<ControlObject> getAllObjects(){
//        for(Integer i:keys){
//            ControlObject co = getControlObject(i.intValue());
//            ObjectWorker worker = new ObjectWorker(co);
//            worker.setVisible(getValueAt(i.intValue(), 0).toString().equalsIgnoreCase("true"));
//        }
        for (ControlObject co : list){
            if (changes.get(co.getPk()) != null){
                ObjectWorker worker = new ObjectWorker(co);
                worker.setVisible(changes.get(co.getPk()));
            }
        }
        return list;
    }

//    public ControlObject getControlObject(int r){
//        return data.get(Integer.valueOf(r));
//    }

    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    private void setMultylineColumn() {
        MultiLineCellRenderer rendererC = new MultiLineCellRenderer();
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            column.setCellRenderer(rendererC);
        }
    }

    public int getRowCount() {
        return list.size();
    }

    public int getColumnCount() {
        return headers.length;
    }

    public String getColumnName(int column) {
        return ((FieldObject)headers[column]).getCaption();
    }
}

