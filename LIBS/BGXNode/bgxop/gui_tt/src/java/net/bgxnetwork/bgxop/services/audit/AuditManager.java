package net.bgx.bgxnetwork.bgxop.services.audit;

import net.bgx.bgxnetwork.auditmanager.interfaces.AuditServiceRemote;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import com.bgx.client.net.AbstractManager;
import com.bgx.client.net.Connector;

/**
 * User: A.Borisenko Date: 06.10.2006 Time: 13:02:02
 */
public class AuditManager extends AbstractManager<AuditServiceRemote> {
    private static AuditManager instance;

    public static AuditManager getInstance() {
        if (instance == null) {
            instance = new AuditManager();                           
        }
        return instance;
    }

    protected Connector<AuditServiceRemote> getConnector() {
//        System.out.println("create new AuditManager");
        return new AuditManagerImpl();
    }

    protected AuditServiceRemote getService() throws QueryBusinesException {
        AuditServiceRemote obj = getServerObject();
        if (obj == null)
            throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_ACCESS_SERVICE);
        return obj;
    }

    public void addExportEvent(Long idQuery) throws Exception {
        Object out = getServerObject().addExportEvent(idQuery);
        if (out != null) {
            checkException(out);
        }
    }

    public void addPrintEvent(Long idQuery) throws Exception {
        Object out = getServerObject().addPrintEvent(idQuery);
        if (out != null) {
            checkException(out);
        }
    }

    public void addSaveImageEvent(Long idQuery) throws Exception {
        Object out = getServerObject().addSaveImageEvent(idQuery);
        if (out != null) {
            checkException(out);
        }
    }

    public void addAccesDeniedEvent(String note) throws Exception {
        Object out = getServerObject().addAccesDeniedEvent(note);
        if (out != null) {
            checkException(out);
        }
    }

    public void addExitEvent() throws Exception {
        Object out = getServerObject().addExitEvent();
        if (out != null) {
            checkException(out);
        }
    }


    public void addOpenTemporaryDiagramEvent(Long idQuery) throws Exception {
        Object out = getServerObject().addOpenTemporaryDiagramEvent(idQuery);
        if (out != null) {
            checkException(out);
        }
    }



    public void addOpenCardObjectEvent(Long idQuery, String nameObject) throws Exception {
        Object out = getServerObject().addOpenCardObjectEvent(idQuery, nameObject);
        if (out != null) {
            checkException(out);
        }
    }

    public void addOpenCardLinkEvent(Long idQuery, String nameObject) throws Exception {
        Object out = getServerObject().addOpenCardLinkEvent(idQuery, nameObject);
        if (out != null) {
            checkException(out);
        }
    }

    public void addOpenCardIntegrationLinkEvent(Long idQuery, String nameObject) throws Exception {
        Object out = getServerObject().addOpenCardIntegrationLinkEvent(idQuery, nameObject);
        if (out != null) {
            checkException(out);
        }
    }


    private void checkException(Object obj) throws Exception {
        if (obj instanceof Throwable) {
            throw (Exception) obj;
        }
    }
}
