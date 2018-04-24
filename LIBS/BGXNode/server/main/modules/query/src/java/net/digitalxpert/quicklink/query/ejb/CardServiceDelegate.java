package net.bgx.bgxnetwork.query.ejb;
import java.util.Locale;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import net.bgx.bgxnetwork.query.interfaces.CardServiceLocal;
import net.bgx.bgxnetwork.query.interfaces.CardServiceRemote;
import net.bgx.bgxnetwork.system.SystemSetting;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import org.jboss.annotation.ejb.RemoteBinding;
import org.jboss.annotation.security.SecurityDomain;

@Stateless
@SecurityDomain(SystemSetting.SECURITY_DOMAIN)
@PermitAll
@RemoteBinding(clientBindUrl = SystemSetting.QL_CLIENT_BIND_URL)
public class CardServiceDelegate implements CardServiceRemote{
    @EJB
    private CardServiceLocal cardService;

    public Object getObjectCard(long objId, int srcId, String inn, ObjectType objectType, Locale clientLocale){
        try{
            return cardService.getObjectCard(objId, srcId, inn, objectType, clientLocale);
        }catch (Exception ex){
            return ex;
        }
    }
    public Long getServerTime(){
    	return System.currentTimeMillis();
    }
}
