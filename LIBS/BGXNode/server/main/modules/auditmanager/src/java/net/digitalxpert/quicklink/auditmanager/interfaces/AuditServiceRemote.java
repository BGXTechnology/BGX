package net.bgx.bgxnetwork.auditmanager.interfaces;

import java.util.Collection;
import javax.ejb.Remote;
import net.bgx.bgxnetwork.persistence.auditmanager.EventCode;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import net.bgx.bgxnetwork.persistence.auditmanager.EventType;

@Remote
public interface AuditServiceRemote {
   // Collection<EventLog> getLogList(int startPosition);
    Object getTypes();
    Object getCodes();
    Object getLogList(int idCode, int idType, String login, Long time, int startPosition, int maxReult);
    Object deleteLogs(Collection<Long> ids);
    Object deletAllLogs();
    Object getCountLogs(int idCode, int idType, String login, Long time);
    Object setLog(String login, Integer code, String note);
    Object setLog(String login, Integer code, String note, String params);
    Object addExportEvent(Long idQuery);
    Object addPrintEvent(Long idQuery);
    Object addSaveImageEvent(Long idQuery);
    Object addAccesDeniedEvent(String note);
    Object addNotAuthorizationLogin(String where, String login);
    
    Object addExitEvent();

    Object addOpenTemporaryDiagramEvent(Long idQuery);

    Object addOpenCardObjectEvent(Long idQuery, String nameObject);

    Object addOpenCardLinkEvent(Long idQuery, String nameObject);

    Object addOpenCardIntegrationLinkEvent(Long idQuery, String nameObject);
}
