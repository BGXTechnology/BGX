package net.bgx.bgxnetwork.bgxop.services;

import com.bgx.client.net.AbstractManager;
import com.bgx.client.net.Connector;
import net.bgx.bgxnetwork.exception.metadata.PresentationBusinessException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceRemote;
import net.bgx.bgxnetwork.persistence.metadata.*;

import java.util.logging.Logger;
import java.util.Set;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 17:54:50
 * To change this template use File | Settings | File Templates.
 */
public class PresentationServiceDelegator extends AbstractManager<PresentationServiceRemote> {
    private static Logger log = Logger.getLogger(PresentationServiceDelegator.class.getName());
    private static PresentationServiceDelegator instance = null;

    public static PresentationServiceDelegator getInstance() {
        if (instance == null)
            instance = new PresentationServiceDelegator();
        return instance;
    }

    protected Connector<PresentationServiceRemote> getConnector() {
        return new PresentationServiceConnector();
    }

    protected PresentationServiceRemote getService() throws PresentationBusinessException {
        PresentationServiceRemote obj = getServerObject();
        if (obj == null)
            throw new PresentationBusinessException(ErrorList.BUSINES_CANNOT_ACCESS_SERVICE);
        return obj;
    }

    public ControlObject getControlObject(NodePK nodePK) throws Exception {
        Object out = getService().getControlObject(nodePK);
        if (out != null) {
            checkException(out);
            return (ControlObject) out;
        }
        return null;
    }

    public LinkObject getLinkObject(LinkPK linkPK) throws Exception {
        Object out = getService().getLinkObject(linkPK);
        if (out != null) {
            checkException(out);
            return (LinkObject) out;
        }
        return null;
    }

    public Object updateControlObject (ControlObject controlObject ) throws Exception {
        Object out = getService().updateControlObject(controlObject);
        if (out != null) {
            checkException(out);
            return (ControlObject) out;
        }
        return null;
    }

    private void checkException(Object obj) throws Exception {
        if (obj instanceof Throwable) {
            throw (Exception) obj;
        }
    }


    public List<PropertyVal> getPropertyValueByGroupPropertyType(NodePK pk, Integer idGroup) throws Exception {

        Object out = getService().getPropertyValueByGroupPropertyType(pk, idGroup);
        if (out != null) {
            checkException(out);
            return (List<PropertyVal>) out;
        }
        return null;
    }
}
