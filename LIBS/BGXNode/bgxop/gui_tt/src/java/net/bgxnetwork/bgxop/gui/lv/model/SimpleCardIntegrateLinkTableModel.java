/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: A.Borisenko
 * Date: 16.07.2007
 * Time: 10:18:01
 */
public class SimpleCardIntegrateLinkTableModel extends CardIntegrateLinkTableModel{
    public SimpleCardIntegrateLinkTableModel(ArrayList<FieldObject> fields, List<LinkObject> list, JTable table) {
        super(fields, list, table);
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public Class getColumnClass(int column) {
        return String.class;
    }

    public Object getValueAt(int row, int column) {
        LinkObject linkObject = list.get(row);
        LinkWorker linkWorker = new LinkWorker(linkObject);
        FieldObject fo = fields.get(column);
        return linkWorker.getValueByPropertyCode(fo.getCode());
    }
}
