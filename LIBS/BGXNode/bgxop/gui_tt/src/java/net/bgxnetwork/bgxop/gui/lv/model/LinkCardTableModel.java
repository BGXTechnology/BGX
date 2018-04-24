package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import net.bgx.bgxnetwork.persistence.metadata.LinkValue;
import net.bgx.bgxnetwork.transfer.data.FieldObject;

public class LinkCardTableModel extends DefaultTableModel{
    private List<LinkValue> list;
    private String[] headers;
    private ArrayList<FieldObject> fields;

    public LinkCardTableModel(String[] headers,List<LinkValue> list, ArrayList<FieldObject> fields){
        this.fields=fields;
        this.list=list;
        this.headers=headers;
        insertData();
    }

    private void insertData() {
        for(String h:headers){
            addColumn(h);
        }
        for (FieldObject field : fields){
            boolean isFound = false;
            String val = "";
            for(LinkValue v:list){
                if (v == null) continue;
                if (v.getPropertyType().getCodePropertyType()!= null &&
                        Long.parseLong(v.getPropertyType().getCodePropertyType()) == (long)field.getCode()){
                    isFound = true;
                    val = v.getValueObject();
                    addRow(new Object[]{field.getCaption(),val});
                }
                if (isFound) break;
            }
            if (!isFound){
                addRow(new Object[]{field.getCaption(),val});
            }
        }
    }

    public boolean isCellEditable(int row, int column){
        return false;
    }

}
