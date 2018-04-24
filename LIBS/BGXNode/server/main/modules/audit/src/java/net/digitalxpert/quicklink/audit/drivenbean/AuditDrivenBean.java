package net.bgx.bgxnetwork.audit.drivenbean;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import net.bgx.bgxnetwork.audit.interfaces.LoggerServiceLocal;
import net.bgx.bgxnetwork.persistence.auditmanager.EventLog;
import org.apache.log4j.Logger;

@MessageDriven(activationConfig = { @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/audit-mdb") })
public class AuditDrivenBean implements MessageListener{
    private static Logger log = Logger.getLogger(AuditDrivenBean.class.getName());
    @EJB
    private LoggerServiceLocal loggerService;
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void onMessage(Message msg){
        try{
            ObjectMessage om = (ObjectMessage) msg;
            EventLog log = (EventLog) om.getObject();
            loggerService.setLog(log);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.toString());
        }
    }
}
