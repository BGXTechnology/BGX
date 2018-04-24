/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.transfer.data;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 15:57:45
 */
public class LVObject implements Serializable {
    private ArrayList<FieldObject> fields = new ArrayList<FieldObject>();

    public void addField(FieldObject fo){
        fields.add(fo);
    }

    public void addFields(ArrayList<FieldObject> fos){
        fields.addAll(fos);
    }

    public ArrayList<FieldObject> getFields() {
        return fields;
    }
}
