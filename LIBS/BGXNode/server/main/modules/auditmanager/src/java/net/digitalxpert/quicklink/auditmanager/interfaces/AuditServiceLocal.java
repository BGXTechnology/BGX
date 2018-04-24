package net.bgx.bgxnetwork.auditmanager.interfaces;



import java.util.Collection;
import javax.ejb.Local;
import net.bgx.bgxnetwork.persistence.auditmanager.EventCode;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import net.bgx.bgxnetwork.persistence.auditmanager.EventType;

@Local
public interface AuditServiceLocal {
    void setLog(String login, Integer code);
    void setLog(EventLog log);
    Collection<EventType> getTypes();
    Collection<EventCode> getCodes();
    Collection<EventLog> getLogList(int idCode, int idType, String login, Long time, int startPosition, int maxReult);
    void deletAllLogs();
    Collection<Long> deleteLogs(Collection<Long> ids);
    int getCountLogs(int idCode, int idType, String login, Long time);
    void setLog(String login, Integer code, String note);
    void setLog(String login, Integer code, String note, String params);
    void addExportEvent(Long idQuery);
    void addPrintEvent(Long idQuery);
    void addSaveImageEvent(Long idQuery);
    void addAccesDeniedEvent(String note);
    void addNotAuthorizationLogin(String where, String login);
    void addAccessDeniedAudit();
    void addAccessDeniedGroup();
    void addAccessDeniedAccount();
    void addAccessDeniedPerson();
    void addExitEvent();

    void addOpenTemporaryDiagramEvent(Long idQuery);

    void addOpenCardObjectEvent(Long idQuery, String nameObject);

    void addOpenCardLinkEvent(Long idQuery, String nameObject);

    void addOpenCardIntegrationLinkEvent(Long idQuery, String nameObject);
}
