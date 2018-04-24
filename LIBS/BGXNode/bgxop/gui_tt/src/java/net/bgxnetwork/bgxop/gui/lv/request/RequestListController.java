/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.request;

import net.bgx.bgxnetwork.transfer.query.Query;

import java.util.HashMap;

/**
 * User: A.Borisenko
 * Date: 19.06.2007
 * Time: 9:51:26
 */
public class RequestListController {
    private HashMap<Long, RequestPanel> requestPanels = new HashMap<Long, RequestPanel>();

    public void addRequestPanel(Long qId, RequestPanel requestPanel){
        requestPanels.put(qId, requestPanel);
    }

    public RequestPanel getPanelByQuestId(Long id){
        return requestPanels.get(id);
    }

    public void removeRequestPanel(Long rId){
        requestPanels.remove(rId);
    }
}
