/**
 * Created by IntelliJ IDEA.
 * User: Ramek
 * Date: 06.03.2007
 * Time: 14:53:09
 * To change this template use File | Settings | File Templates.
 */
package net.bgx.bgxnetwork.bgxop.gui.query.dictionary;

import net.bgx.bgxnetwork.transfer.data.Request;
import net.bgx.bgxnetwork.transfer.data.Quest;
import net.bgx.bgxnetwork.bgxop.services.MetaDataServiceDelegator;

import java.util.ArrayList;
import java.util.HashMap;

public class Loader {
    public static final int INN_MASK = 0;

    private ArrayList<Request> _requestList = new ArrayList<Request>();
    private HashMap<Long, ArrayList<Quest>> _quests = new HashMap<Long, ArrayList<Quest>>();

    private static Loader ourInstance = new Loader();

    public static Loader getInstance() {
        return ourInstance;
    }

    private Loader() {
    }

    synchronized public ArrayList<Request> getRequestsList() {
        if (_requestList.isEmpty()) {
            try {
                _requestList = (ArrayList<Request>) MetaDataServiceDelegator.getInstance().getRequestList();
            }
            catch (Exception e) {
                //todo
                e.printStackTrace();
            }
        }
        return _requestList;
    }

    synchronized public ArrayList<Quest> getQueryListByRequest(Long id) {
        if (_quests.get(id) == null){
            try {
                _quests.put(id, (ArrayList<Quest>) MetaDataServiceDelegator.getInstance().getQuestListByRequest(id));
            } catch (Exception e) {
                //todo
                e.printStackTrace();
            }
        }
        return _quests.get(id);
    }
//  synchronized public void set_requestList(ArrayList<Request> requests){
//    _requestList = requests;
//  }
}
