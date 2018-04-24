/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.model;

import javax.swing.DefaultListModel;
import net.bgx.bgxnetwork.transfer.data.Quest;

/**
 * User: A.Borisenko
 * Date: 09.06.2007
 * Time: 21:57:23
 */
public class QuestListModel extends DefaultListModel {
    public int getSize() {
        return super.getSize();
    }

    public Object getElementAt(int index) {
        return (Quest)super.getElementAt(index);
    }

    public void addElement(Quest q){
        super.addElement(q);
    }

    public void removeElement(int index){
        super.removeElementAt(index);
    }

    public void removeElement(Quest q){
        super.removeElement(q);
    }
}
