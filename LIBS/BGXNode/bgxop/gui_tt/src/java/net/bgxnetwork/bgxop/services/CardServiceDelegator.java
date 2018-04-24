package net.bgx.bgxnetwork.bgxop.services;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.uitools.UITools;
import net.bgx.bgxnetwork.query.interfaces.CardServiceRemote;
import net.bgx.bgxnetwork.transfer.query.LinkType;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.card.Card;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import oracle.spatial.network.Node;
import com.bgx.client.net.AbstractManager;
import com.bgx.client.net.Connector;

/**
 * Class CardServiceDelegator
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class CardServiceDelegator extends AbstractManager<CardServiceRemote>{
    private static Logger log = Logger.getLogger(QueryServiceDelegator.class.getName());
    private static CardServiceDelegator instance = null;
    public static CardServiceDelegator getInstance(){
        if(instance == null)
            instance = new CardServiceDelegator();
        return instance;
    }
    protected Connector<CardServiceRemote> getConnector(){
        return new CardServiceConnector();
    }
    protected CardServiceRemote getService() throws QueryBusinesException{
        CardServiceRemote obj = getServerObject();
        if(obj == null)
            throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_ACCESS_SERVICE);
        return obj;
    }
    public Card getObjectCard(long objId, int srcId, String inn, ObjectType objectType) throws Exception{
        Object out = getServerObject().getObjectCard(objId, srcId, inn, objectType, Locale.getDefault());
        if(out != null){
            checkException(out);
            return (Card) out;
        }
        return null;
    }
    public Long getServerTime(){
    	return getServerObject().getServerTime();
    }

    private void checkException(Object obj) throws Exception{
        if(obj instanceof Throwable){
            throw (Exception) obj;
        }
    }
}
