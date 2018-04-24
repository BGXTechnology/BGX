package net.bgx.bgxnetwork.bgxop.services.audit;

import net.bgx.bgxnetwork.auditmanager.interfaces.AuditServiceRemote;
import com.bgx.client.net.ConnectorImpl;

/**
 * Date: 10.10.2006
 * Time: 11:36:55
 * To change this template use File | Settings | File Templates.
 */
public class AuditManagerImpl extends ConnectorImpl<AuditServiceRemote> {
    public String getInvokedClassName() {
        return (System.getProperty("EAR") + "/AuditServiceDelegate/remote");
    }
}
