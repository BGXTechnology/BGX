package net.bgx.bgxnetwork.query.ejb;
import java.util.*;
import javax.annotation.Resource;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import net.bgx.bgxnetwork.query.interfaces.CardServiceLocal;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.query.card.Card;
import net.bgx.bgxnetwork.transfer.query.card.CardField;
import net.bgx.bgxnetwork.transfer.query.card.CardTab;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import org.jboss.annotation.security.SecurityDomain;
import org.jboss.resource.adapter.jdbc.WrappedCallableStatement;
import org.apache.log4j.Logger;

/**
 * Class CardServiceBean
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@RunAs("LVSystem")
public class CardServiceBean implements CardServiceLocal{
    private Logger log = Logger.getLogger(CardServiceBean.class.getName());

    @Resource(mappedName = "java:/bgxnetworkDS_CORE")
    private javax.sql.DataSource ds;
    protected ResourceBundle rb;
    protected ResourceBundle trb;
    private String packetNameSpObjCard = "";

    @PostConstruct
    public void myInit(){
        packetNameSpObjCard = System.getProperty("QLDS.PACKET.OBJ_CARD");
    }

    @RolesAllowed( { "LV_ROLE" })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Card getObjectCard(long objId, int srcId, String inn, ObjectType objectType, Locale clientLocale) throws QueryDataException {
        rb = PropertyResourceBundle.getBundle("query", clientLocale);
        trb = PropertyResourceBundle.getBundle("transfer", clientLocale);
        if(objId == 0L || srcId == -1)
            return getEmptyCard(objectType);
        return null;
    }

    /**
     * Create Empty Card
     *
     * @param ot
     * @return Card
     */
    protected Card getEmptyCard(ObjectType ot){
        ArrayList<CardTab> tabs = new ArrayList<CardTab>();
        ArrayList<CardField> fields = new ArrayList<CardField>();
        CardTab tab = new CardTab(rb.getString("Card.general.title"), fields);
        tabs.add(tab);
        return new Card(0L, 0, ot, tabs);
    }
}
