package net.bgx.bgxnetwork.audit.aspect;
import java.security.Principal;
import java.util.ArrayList;
import javax.annotation.Resource;
import javax.ejb.EJBAccessException;
import javax.ejb.EJBContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;
import net.bgx.bgxnetwork.audit.annotation.Audit;
import net.bgx.bgxnetwork.audit.annotation.AuditWarning;
import net.bgx.bgxnetwork.audit.strategy.AbstractLog;
import net.bgx.bgxnetwork.audit.strategy.StandartLog;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;

public class AuditInterceptor{
    @Resource
    private EJBContext ctx;
    @AroundInvoke
    public Object audit(InvocationContext invocation) throws Exception{
        Principal principal = ctx.getCallerPrincipal();
        Audit audit = invocation.getMethod().getAnnotation(Audit.class);
        AuditWarning warn = invocation.getMethod().getDeclaringClass().getAnnotation(AuditWarning.class);
        Object obj = null;
        try{
            obj = invocation.proceed();
            return obj;
        }catch(Exception ex){
            obj = ex;
            throw ex;
        }finally{
            AbstractLog strategy = null;
            if(((obj instanceof EJBAccessException) || (obj instanceof LoginException)) && warn != null){
                strategy = new StandartLog();
                strategy.process(invocation, warn.value().getCode(), principal);
                sendMessage(strategy.getLogs());
            }else if(audit != null && !(obj instanceof Exception)){
                strategy = audit.strategy().getStrategy();
                strategy.process(invocation, audit.code().getCode(), principal);
                sendMessage(strategy.getLogs());
            }
        }
    }
    private void sendMessage(ArrayList<EventLog> l){
        ArrayList<EventLog> logs = l;
        try{
            if(logs != null){
                InitialContext context = new InitialContext();
                QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("ConnectionFactory");
                QueueConnection conn = factory.createQueueConnection();
                QueueSession session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
                Queue queue = (Queue) context.lookup("queue/audit-mdb");
                QueueSender sender = session.createSender(queue);
                for(EventLog log : logs){
                    ObjectMessage msg = session.createObjectMessage(log);
                    sender.send(msg);
                }
                sender.close();
                session.close();
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
