package net.bgx.bgxnetwork.audit.ejb;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import net.bgx.bgxnetwork.audit.interfaces.LoggerServiceLocal;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import net.bgx.bgxnetwork.persistence.auditmanager.EventCode;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.persistence.query.QueryTypeEntity;
import net.bgx.bgxnetwork.system.SystemSetting;
import org.jboss.annotation.security.SecurityDomain;
import org.apache.log4j.Logger;

@Stateless
//@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
//@RunAs("LVSystem")
public class LoggerServiceBean implements LoggerServiceLocal {
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;
    private static Logger logServer = Logger.getLogger(LoggerServiceBean.class.getName());

    @PermitAll
    public void setLog(EventLog log) {
        try {
            persistLog(log);
        } catch (Exception e) {
            System.out.println("Audit create log exception" + e);
        }
    }

    private void persistLog(EventLog log) {
        try {
            if (log.getCode() != null) {
                logServer.info(buildLogMessage(log));
                manager.persist(log);
            }

        } catch (Exception e) {
            System.out.println("Audit create log exception" + e);
        }
    }

    @PermitAll
    public String getQueryById(Long id) {
        String out = null;
        try {
            QueryEntity query = manager.find(QueryEntity.class, id);
            if (query != null) {
                out = query.getName();
            }
        } catch (Exception e) {
            System.out.println("Audit create log exception" + e);
        }
        return out;
    }

    public String getFullNameById(Long id) {
        String out = "";
        try {
            QueryEntity query;
            do {
                query = manager.find(QueryEntity.class, id);
                if (query != null) {
                    out = query.getQueryType().getName() + " " + query.getName() + " : " + out;
                    if (query.getParent() != null)
                        id = query.getParent().getId();
                    else
                        query = null;
                }

            } while (query != null);
        } catch (Exception e) {
            System.out.println("Audit create log exception" + e);
        }
        return out;
    }

    private String buildLogMessage(EventLog log) {
        EventCode eventCode = manager.find(EventCode.class, log.getCode());

        StringBuffer sb = new StringBuffer();
        sb.append("\n ****************************** \n");
        if (log.getLogin() != null) {
            sb.append(log.getLogin());
            sb.append(" ");
        }
        if (eventCode != null) {
            sb.append(eventCode.getDescription());
            sb.append(" ");
        }
        if (log.getArticle() != null) {
            sb.append(log.getArticle());
            sb.append(" ");
        }
        if (log.getParams() != null) {
            sb.append(log.getParams());
            sb.append(" ");
        }
        sb.append("\n ****************************** \n");
        return sb.toString();
    }
}
