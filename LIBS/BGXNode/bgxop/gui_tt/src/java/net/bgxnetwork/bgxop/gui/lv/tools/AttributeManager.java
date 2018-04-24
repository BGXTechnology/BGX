/**
 * User: A.Borisenko
 * Date: 23.06.2007
 * Time: 16:18:49
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.tools;

import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.transfer.query.Query;

public class AttributeManager {
    private MainFrame owner;
    private static AttributeManager ourInstance = new AttributeManager();

    public static AttributeManager getInstance() {
        return ourInstance;
    }

    private AttributeManager() {
    }

    public Query getCurrentVisibleQuery(){
        return owner.getQueryListController().getCurrentQuery();
    }

    public void setOwner(MainFrame owner) {
        this.owner = owner;
    }
}
