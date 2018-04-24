package net.bgx.bgxnetwork.bgxop.services;

import com.bgx.client.net.AbstractManager;
import com.bgx.client.net.Connector;

import java.util.logging.Logger;
import java.util.List;
import java.util.ArrayList;

import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.metadata.MetaDataBusinessException;
import net.bgx.bgxnetwork.persistence.metadata.ObjectType;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceRemote;
import net.bgx.bgxnetwork.transfer.data.Request;
import net.bgx.bgxnetwork.transfer.data.Quest;
import net.bgx.bgxnetwork.transfer.data.LVObject;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:29:50
 * To change this template use File | Settings | File Templates.
 */
public class MetaDataServiceDelegator extends AbstractManager<MetaDataServiceRemote> {
    private static Logger log = Logger.getLogger(MetaDataServiceDelegator.class.getName());
    private static MetaDataServiceDelegator instance = null;

    public static MetaDataServiceDelegator getInstance() {
        if (instance == null)
            instance = new MetaDataServiceDelegator();
        return instance;
    }

    protected Connector<MetaDataServiceRemote> getConnector() {
        return new MetaDataServiceConnector();
    }

    protected MetaDataServiceRemote getService() throws MetaDataBusinessException {
        MetaDataServiceRemote obj = getServerObject();
        if (obj == null)
            throw new MetaDataBusinessException(ErrorList.BUSINES_CANNOT_ACCESS_SERVICE);
        return obj;
    }

    public List<ObjectType> getObjectTypeList() throws Exception {
        Object out = getService().getObjectTypeList();
        if (out != null) {
            checkException(out);
            return (List<ObjectType>) out;
        }
        return null;
    }

    private void checkException(Object obj) throws Exception {
        if (obj instanceof Throwable) {
            throw (Exception) obj;
        }
    }

    public List<Request> getRequestList() throws Exception {
        Object out = getService().getRequestListByUser();
        if (out != null){
            checkException(out);
            return (List<Request>)out;
        }
        return null;
    }

    public List<Quest> getQuestListByRequest(Long id) throws Exception {
        Object out = getService().getQuestListByRequest(id);
        if (out != null){
            checkException(out);
            return (List<Quest>)out;
        }
        return null;
    }

    public Object getVisiblePropertyByObjectType(Long id) throws Exception {
        Object out = getService().getVisiblePropertyByObjectType(id);
        if (out != null){
            checkException(out);
            return (ArrayList<PropertyType>)out;
        }
        return new ArrayList<PropertyType>();
    }

    public Object getObjectAttributeOverview() throws Exception {
        Object out = getService().getObjectAttributeOverview();
        if (out != null){
            checkException(out);
            return (LVObject)out;
        }
        return new LVObject();
    }

    public Object getLinkAttributeOverview() throws Exception {
        Object out = getService().getLinkAttributeOverview();
        if (out != null){
            checkException(out);
            return (LVObject)out;
        }
        return new LVObject();
    }

}
