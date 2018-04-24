package net.bgx.bgxnetwork.query.ejb;

import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import net.bgx.bgxnetwork.query.mdb.MessagePropertyName;
import net.bgx.bgxnetwork.query.mdb.MessageObject;

import javax.jms.*;
import javax.ejb.EJBException;

import org.apache.log4j.Logger;

/**
 * User: A.Borisenko
 * Date: 23.10.2006
 * Time: 18:02:15
 * To change this template use File | Settings | File Templates.
 */
public class MessageSender {
    private static Logger log = Logger.getLogger(MessageSender.class.getName());
    private javax.jms.Queue _queue = ServiceLocator.findQueue("queue/query-mdb");
    private QueueConnectionFactory _factory = ServiceLocator.findQueueConnectionFactory("ConnectionFactory");

    private static MessageSender ourInstance = new MessageSender();

    public static MessageSender getInstance() {
        return ourInstance;
    }

    private MessageSender() {
    }

    void sendMessage(MessageObject obj){
        QueueConnection cnn = null;
        QueueSession session = null;
        QueueSender sender = null;
        try {
            cnn = _factory.createQueueConnection();
            session = cnn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            sender = session.createSender(_queue);
            sender.setTimeToLive(10000L);

            ObjectMessage msg = session.createObjectMessage();
            //Set mandatory property
            msg.setStringProperty("RECIPIENT", "QueryService");

            msg.setJMSRedelivered(false);
            msg.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
            msg.setObject(obj);
            sender.send(msg);
        }
        catch (JMSException e) {
            throw new EJBException(e);
        }
        finally{
            if (sender != null) try {
                sender.close();
            }
            catch (JMSException e) {
                //todo
                e.printStackTrace();
            }
            if (session != null)
                try {
                    session.close();
                }
                catch (JMSException e) {
                    //todo
                    e.printStackTrace();
                }
            if (cnn!= null)
                try {
                    cnn.close();
                }
                catch (JMSException e)
                {
                    //todo
                    e.printStackTrace();
                }
        }

    }

    private void setMessageParameters(ObjectMessage out, MessageObject in) throws JMSException {
        out.setObjectProperty(MessagePropertyName.MSG_CODE, in.getCode());
        out.setObjectProperty(MessagePropertyName.QUERY_ID, in.getQueryID());
    }
}
