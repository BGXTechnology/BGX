package net.bgx.bgxnetwork.dbmanager.ejb;

import net.bgx.bgxnetwork.dbmanager.dao.KillRequestDAO;
import net.bgx.bgxnetwork.dbmanager.dao.oracle.KillRequestDAOImpl;
import net.bgx.bgxnetwork.dbmanager.interfaces.DatabaseServiceLocal;
import net.bgx.bgxnetwork.transfer.query.DataAccessException;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;
import java.sql.SQLException;

import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
public class DatabaseServiceBean implements DatabaseServiceLocal {
    @Resource(mappedName = "java:/bgxnetworkDS_QLDS")
    private javax.sql.DataSource ds;

    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;

    @RolesAllowed({"LVSystem"})
    public void killSession(Long queryId) throws DataAccessException {
        try {
            QueryEntity queryEntity = manager.find(QueryEntity.class, queryId);
            Long sessionId = queryEntity.getSessionId();
            if (queryEntity.getQueryStatus().equals(QueryStatus.Executing.getValue()) && sessionId != null) {
                KillRequestDAO dao = new KillRequestDAOImpl(ds);
                dao.killStatement(sessionId);
            }
        } catch (SQLException e) {
            throw new EJBException(e);
        } catch (Exception other) {
            throw new EJBException(other);
        }
    }
}
