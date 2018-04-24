package net.bgx.bgxnetwork.auditmanager.ejb;
import java.util.Collection;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import net.bgx.bgxnetwork.audit.annotation.Audit;
import net.bgx.bgxnetwork.audit.annotation.AuditWarning;
import net.bgx.bgxnetwork.audit.aspect.AuditInterceptor;
import net.bgx.bgxnetwork.audit.strategy.AuditCode;
import net.bgx.bgxnetwork.audit.strategy.AuditLogs;
import net.bgx.bgxnetwork.auditmanager.interfaces.AuditServiceLocal;
import net.bgx.bgxnetwork.auditmanager.interfaces.AuditServiceRemote;
import net.bgx.bgxnetwork.system.SystemSetting;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@PermitAll
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
@AuditWarning(AuditCode.ERROR_AUDIT)
@Interceptors( { AuditInterceptor.class })
public class AuditServiceDelegate implements AuditServiceRemote{
    @EJB
    private AuditServiceLocal auditService;
    public Object addAccesDeniedEvent(String note){
        try{
            auditService.addAccesDeniedEvent(note);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object addExitEvent(){
        try{
            auditService.addExitEvent();
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }

    public Object addOpenTemporaryDiagramEvent(Long idQuery) {
        try{
            auditService.addOpenTemporaryDiagramEvent(idQuery);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }

    public Object addOpenCardObjectEvent(Long idQuery, String nameObject) {
        try{
            auditService.addOpenCardObjectEvent(idQuery, nameObject) ;
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }

    public Object addOpenCardLinkEvent(Long idQuery, String nameObject) {
        try{
            auditService.addOpenCardLinkEvent(idQuery, nameObject);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }

    public Object addOpenCardIntegrationLinkEvent(Long idQuery, String nameObject) {
        try{
            auditService.addOpenCardIntegrationLinkEvent(idQuery, nameObject);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }

    public Object addExportEvent(Long idQuery){
        try{
            auditService.addExportEvent(idQuery);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object addNotAuthorizationLogin(String where, String login){
        try{
            auditService.addNotAuthorizationLogin(where, login);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object addPrintEvent(Long idQuery){
        try{
            auditService.addPrintEvent(idQuery);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object addSaveImageEvent(Long idQuery){
        try{
            auditService.addSaveImageEvent(idQuery);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object deleteLogs(Collection<Long> ids){
        try{
            auditService.deleteLogs(ids);
        }catch (Exception ex){
            return ex;
        }
        return null;
    }
    public Object deletAllLogs(){
        try{
            auditService.deletAllLogs();
        }catch (Exception ex){
            return ex;
        }
        return null;
    }
    public Object getCodes(){
        try{
            return auditService.getCodes();
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getCountLogs(int idCode, int idType, String login, Long time){
        try{
            return auditService.getCountLogs(idCode, idType, login, time);
        }catch (Exception ex){
            return ex;
        }
    }
    public Object getLogList(int idCode, int idType, String login, Long time, int startPosition, int maxReult){
        try{
            return auditService.getLogList(idCode, idType, login, time, startPosition, maxReult);
        }catch (Exception ex){
            return ex;
        }
    }
    @Audit(code = AuditCode.VIEW_AUDIT, strategy = AuditLogs.STANDART_LOG)
    public Object getTypes(){
        try{
            return auditService.getTypes();
        }catch (Exception ex){
            return ex;
        }
    }
    public Object setLog(String login, Integer code, String note){
        try{
            auditService.setLog(login, code, note);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
    public Object setLog(String login, Integer code, String note, String params){
        try{
            auditService.setLog(login, code, note, params);
        }catch (Exception ex){
            System.out.println("Audit exception" + ex);
        }
        return null;
    }
}
