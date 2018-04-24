package net.bgx.bgxnetwork.auditmanager.ejb;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.bgx.bgxnetwork.auditmanager.interfaces.AuditServiceLocal;
import net.bgx.bgxnetwork.exception.audit.AuditEJBException;
import net.bgx.bgxnetwork.exception.audit.ErrorList;
import net.bgx.bgxnetwork.persistence.auditmanager.EventCode;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import net.bgx.bgxnetwork.persistence.auditmanager.EventType;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.audit.interfaces.LoggerServiceLocal;
import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import org.jboss.annotation.security.SecurityDomain;
import org.apache.log4j.Logger;

@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RunAs("LVSystem")
public class AuditServiceBean implements AuditServiceLocal {
    @Resource
    private EJBContext ctx;
    @PersistenceContext(unitName = "business_manager")
    private EntityManager manager;
    private static Logger logServer = Logger.getLogger(AuditServiceBean.class.getName());

    /*
     * @RolesAllowed( { "AuditReader" }) public Collection<EventLog>
     * getLogList(int startPosition) { Collection<EventLog> logs = null; try {
     * Query query = manager.createQuery("from EventLog l");
     * query.setFirstResult(startPosition); logs = query.getResultList(); }
     * catch (Exception e) { e.printStackTrace(); } return logs; }
     */
    @RolesAllowed({"LV_ROLE"})
    public Collection<EventCode> getCodes() {
        Collection<EventCode> evCodes = null;
        try {
            Query query = manager.createQuery("from EventCode ec");
            evCodes = query.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return evCodes;
    }

    @RolesAllowed({"LV_ROLE"})
    public Collection<EventType> getTypes() {
        Collection<EventType> evTypes = null;
        try {
            Query query = manager.createQuery("from EventType et");
            evTypes = query.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return evTypes;
    }

    public void setLog(String login, Integer code) {
        EventLog log = new EventLog();
        log.setLogin(login);
        log.setCode(code);
        log.setEventTime(System.currentTimeMillis());
        sendMessage(log);
    }

    public void setLog(EventLog log) {
        try {
            persistLog(log);
        }
        catch (Exception e) {
            System.out.println("Persist log exception" + e);
        }
    }

    @RolesAllowed({"LV_ROLE", "LVSystem"})
    public void setLog(String login, Integer code, String note) {
        EventLog log = new EventLog();
        log.setLogin(login);
        log.setCode(code);
        log.setArticle(note);
        log.setEventTime(System.currentTimeMillis());
        sendMessage(log);
    }

    @RolesAllowed({"LV_ROLE", "LVSystem"})
    public void setLog(String login, Integer code, String note, String params) {
        Principal principal = ctx.getCallerPrincipal();
        if (login.equals("unknown") && principal != null)
            login = principal.getName();
        EventLog log = new EventLog();
        log.setLogin(login);
        log.setCode(code);
        log.setArticle(note);
        log.setEventTime(System.currentTimeMillis());
        log.setParams(params);
        sendMessage(log);
    }

    @RolesAllowed({"LV_ROLE"})
    public Collection<EventLog> getLogList(int idCode, int idType, String login, Long time, int startPosition,
                                           int maxReult) {
        Collection<EventLog> logs = null;
        try {
            StringBuffer strQuery = new StringBuffer("select l from EventLog l");
            strQuery.append(" where 1=1");
            if (idCode != 0)
                strQuery.append(" and l.code=:idCode");
            if (idType != 0)
                strQuery.append(" and l.evCode.evType.id=:idType");
            if (login != null)
                strQuery.append(" and l.login=:login");
            if (time != 0)
                strQuery.append(" and l.eventTime>:time");
            strQuery.append(" order by l.eventTime desc");
            Query query = manager.createQuery(strQuery.toString());
            if (idCode != 0)
                query.setParameter("idCode", idCode);
            if (idType != 0)
                query.setParameter("idType", idType);
            if (login != null)
                query.setParameter("login", login);
            if (time != 0)
                query.setParameter("time", time);
            query.setFirstResult(startPosition);
            // -1 denotes all results
            if (Integer.signum(maxReult) > -1) {
                query.setMaxResults(maxReult);
            }
            logs = query.getResultList();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return logs;
    }

    @RolesAllowed({"LV_ROLE"})
    public int getCountLogs(int idCode, int idType, String login, Long time) {
        Integer count = 0;
        try {
            String queryStr = "select count(l) from EventLog l where 1=1 ";
            if (idCode != 0)
                queryStr += " and l.code=:idCode";
            if (idType != 0)
                queryStr += " and l.evCode.evType.id=:idType";
            if (login != null)
                queryStr += " and l.login=:login";
            if (time != 0)
                queryStr += " and l.eventTime>:time";
            Query query = manager.createQuery(queryStr);
            if (idCode != 0)
                query.setParameter("idCode", idCode);
            if (idType != 0)
                query.setParameter("idType", idType);
            if (login != null)
                query.setParameter("login", login);
            if (time != 0)
                query.setParameter("time", time);
            count = ((Long) query.getSingleResult()).intValue();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @RolesAllowed({"LV_ROLE"})
    public Collection<Long> deleteLogs(Collection<Long> ids) {
        Collection<Long> survivors = new LinkedList<Long>(ids);
        Query query = manager.createQuery("delete from EventLog e where e.id=:id");
        for (Long id : ids) {
            query.setParameter("id", id);
            try {
                query.executeUpdate();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            survivors.remove(id);
        }
        return survivors;
    }

    @RolesAllowed({"LV_ROLE"})
    public void deletAllLogs() {
        try {
            Query query = manager.createQuery("delete from EventLog");
            query.executeUpdate();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RolesAllowed({"LV_ROLE"})
    public void addExportEvent(Long idQuery) {
        addClientEvent(25, getFullQueryNameById(idQuery));
    }

    @RolesAllowed({"LV_ROLE"})
    public void addPrintEvent(Long idQuery) {
        addClientEvent(26, getFullQueryNameById(idQuery));
    }

    @RolesAllowed({"LV_ROLE"})
    public void addSaveImageEvent(Long idQuery) {
        addClientEvent(27, getFullQueryNameById(idQuery));
    }

    @PermitAll
    public void addAccesDeniedEvent(String note) {
        addClientEvent(28, note);
    }

    @PermitAll
    public void addExitEvent() {
        addClientEvent(2, null);
    }

    @RolesAllowed({"LV_ROLE"})
    public void addOpenTemporaryDiagramEvent(Long idQuery) {
        addClientEvent(31, getFullQueryNameById(idQuery));
    }

    @RolesAllowed({"LV_ROLE"})
    public void addOpenCardObjectEvent(Long idQuery, String nameObject) {
        addClientEvent(32, getFullQueryNameById(idQuery) + " " + nameObject);
    }

    @RolesAllowed({"LV_ROLE"})
    public void addOpenCardLinkEvent(Long idQuery, String nameObject) {
        addClientEvent(33, getFullQueryNameById(idQuery) + " " + nameObject);
    }

    @RolesAllowed({"LV_ROLE"})
    public void addOpenCardIntegrationLinkEvent(Long idQuery, String nameObject) {
        addClientEvent(34, getFullQueryNameById(idQuery) + " " + nameObject);
    }

    @PermitAll
    public void addNotAuthorizationLogin(String where, String login) {
        setLog(login, 7, where);
    }

    @PermitAll
    public void addAccessDeniedAudit() {
        addClientEvent(11, null);
    }

    @PermitAll
    public void addAccessDeniedGroup() {
        addClientEvent(10, null);
    }

    @PermitAll
    public void addAccessDeniedAccount() {
        addClientEvent(8, null);
    }

    @PermitAll
    public void addAccessDeniedPerson() {
        addClientEvent(9, null);
    }

    private void addClientEvent(Integer code, String note) {
        try {
            Principal principal = ctx.getCallerPrincipal();
            if (note != null) {
                setLog(principal.getName(), code, note.substring(0, note.length() > 127 ? 127 : note.length()));
            } else {
                setLog(principal.getName(), code);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persistLog(EventLog log) {
        try {
            if (log.getCode() != null) {
                logServer.info(buildLogMessage(log));
                manager.persist(log);
            }
        }
        catch (EntityExistsException e) {
            throw new AuditEJBException(ErrorList.EJB_ENTITY_IS_EXIST);
        }
        catch (IllegalArgumentException e) {
            throw new AuditEJBException(ErrorList.EJB_ILLEGAL_ARGUMENT);
        }
        catch (Exception e) {
            throw new AuditEJBException(ErrorList.EJB_CREATE_EXCEPTION, new Object[]{e.toString()});
        }
    }

    private void sendMessage(EventLog log) {
        try {
            InitialContext context = new InitialContext();
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("ConnectionFactory");
            QueueConnection conn = factory.createQueueConnection();
            QueueSession session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) context.lookup("queue/audit-mdb");
            QueueSender sender = session.createSender(queue);
            ObjectMessage msg = session.createObjectMessage(log);
            sender.send(msg);
            sender.close();
            session.close();
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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

    private String getFullQueryNameById(Long id) {
        String name = null;
        LoggerServiceLocal loggerService = (LoggerServiceLocal) ServiceLocator.findEjb3LocalByDefault("LoggerServiceBean");
        name = loggerService.getFullNameById(id);
        return name;
    }
}
