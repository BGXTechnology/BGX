package net.bgx.bgxnetwork.bgxop.services;

import java.util.*;
import java.util.logging.Logger;

import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.query.GraphAnnotation;
import net.bgx.bgxnetwork.persistence.query.LayoutCoordinates;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceRemote;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import net.bgx.bgxnetwork.transfer.tt.TimedDiagrammDataSnapshot;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import oracle.spatial.network.Node;
import com.bgx.client.net.AbstractManager;
import com.bgx.client.net.Connector;

/**
 * Class QueryServiceDelegator
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryServiceDelegator extends AbstractManager<QueryServiceRemote> {
    private static Logger log = Logger.getLogger(QueryServiceDelegator.class.getName());
    private static QueryServiceDelegator instance = null;

    public static QueryServiceDelegator getInstance() {
        if (instance == null)
            instance = new QueryServiceDelegator();
        return instance;
    }

    protected Connector<QueryServiceRemote> getConnector() {
        return new QueryServiceConnector();
    }

    protected QueryServiceRemote getService() throws QueryBusinesException {
        QueryServiceRemote obj = getServerObject();
        if (obj == null)
            throw new QueryBusinesException(ErrorList.BUSINES_CANNOT_ACCESS_SERVICE);
        return obj;
    }

    public List<QueryType> getQueryTypeList() throws Exception {
        Object out = getService().getQueryTypeList();
        if (out != null) {
            checkException(out);
            return (List<QueryType>) out;
        }
        return null;
    }

    public List<Query> getQueryList() throws Exception {
        Object out = getService().getQueryList();
        if (out != null) {
            checkException(out);
            return (List<Query>) out;
        }
        return null;
    }

    public Long create(Query q) throws Exception {
        Object out = getService().create(q);
        if (out != null) {
            checkException(out);
            return (Long) out;
        }
        return null;
    }

    public void update(Query q) throws Exception {
        Object out = getService().update(q);
        if (out != null) {
            checkException(out);
        }
    }

    public void remove(long queryId) throws Exception {
        Object out = getService().remove(queryId);
        if (out != null) {
            checkException(out);
        }
    }

    public QueryStatus execute(long queryId) throws Exception {
        Object out = getService().execute(queryId, Locale.getDefault());
        if (out != null) {
            checkException(out);
            return (QueryStatus) out;
        }
        return null;
    }

    public Long createAndExecute(Query q) throws Exception {
        Object out = getService().createAndExecute(q, Locale.getDefault());
        if (out != null) {
            checkException(out);
            return (Long) out;
        }
        return null;
    }

    public QueryStatus updateAndExecute(Query q) throws Exception {
        Object out = getService().updateAndExecute(q, Locale.getDefault());
        if (out != null) {
            checkException(out);
            return (QueryStatus) out;
        }
        return null;
    }

    public Network getQueryData(long queryId) throws Exception {
        Object out = getService().getQueryData(queryId, Locale.getDefault());
        if (out != null) {
            checkException(out);
            return transfer2network((QueryData) out);
        }
        return null;
    }

    public void removeFromGraph(long queryId, List<Integer> vertices, List<Integer> edges) throws Exception {
        Object out = getService().removeFromGraph(queryId, vertices, edges);
        if (out != null) {
            checkException(out);
        }
    }

    public Boolean saveLayout(long queryId, LayoutCoordinates layout) throws Exception {
        Object out = getService().saveLayout(queryId, layout);
        if (out != null) {
            checkException(out);
            return (Boolean) out;
        }
        return null;
    }

    public LayoutCoordinates getLayout(long queryId) throws Exception {
        Object out = getService().getLayout(queryId);
        if (out != null) {
            checkException(out);
            return (LayoutCoordinates) out;
        }
        return null;
    }

    public Boolean saveAnnotation(long queryId, GraphAnnotation annotation) throws Exception {
        Object out = getService().saveAnnotation(queryId, annotation);
        if (out != null) {
            checkException(out);
            return (Boolean) out;
        }
        return null;
    }

    public GraphAnnotation getAnnotation(long queryId) throws Exception {
        Object out = getService().getAnnotation(queryId);
        if (out != null) {
            checkException(out);
            return (GraphAnnotation) out;
        }
        return null;
    }

    private static void setNodeAttributes(Node n, QueryData data) {
        GraphNetworkUtil.setControlObject(n, data.getObjects().get(n.getID()));
    }

    private static void setLinkAttributes(Link link, QueryData data) {
        GraphNetworkUtil.setLinkObject(link, data.getLinkObjects().get(link.getID()));
    }

    public static Network transfer2network(QueryData data) {
        if (data == null)
            return null;
        Network res = data.getNetwork();
        Node n;
        for (Iterator it = res.getNodes(); it.hasNext();) {
            n = (Node) it.next();
            setNodeAttributes(n, data);
        }
        Link l;
        for (Iterator it = res.getLinks(); it.hasNext();) {
            l = (Link) it.next();
            setLinkAttributes(l, data);
        }
        
        GraphNetworkUtil.setObjectsLimit(res, data.getObjectsLimit());
        return res;
    }

    private void checkException(Object obj) throws Exception {
        if (obj instanceof Throwable) {
            throw (Exception) obj;
        }
    }

    public ArrayList<String> getInnForDictionary() throws Exception {
        Object obj = getService().getInnDictionary();
        checkException(obj);
        if (obj == null)
            return new ArrayList<String>();
        return (ArrayList<String>)obj;
    }

    public Long copyQueryFromExist(Query query) throws Exception {
        Object out = getService().copyQueryFromExist(query, Locale.getDefault());
        if (out != null) {
            checkException(out);
            return (Long) out;
        }
        return null;
    }

    public void updateName(Query q) throws Exception {
        Object out = getService().updateName(q);
        if (out != null) {
            checkException(out);
        }
    }

    public void updateTTParams(Long qId, String data) throws Exception {
        Object out = getService().updateTTParams(qId, data);
        if (out != null) {
            checkException(out);
        }
    }

    public HashMap<Long, List<PropertyType>> setVisibleAttributes(Long qId, List<Long> codes) throws Exception {
        Object out = getService().setVisibleAttributes(qId, codes);
        if (out != null) {
            checkException(out);
            return (HashMap<Long, List<PropertyType>>)out;
        }
        return new HashMap<Long, List<PropertyType>>();
    }

    public void updateControlObjects(ArrayList<ControlObject> objects) throws Exception {
        Object out = getService().updateControlObjects(objects);
        if (out != null) {
            checkException(out);
        }
    }

    public void updateLinkObjects(ArrayList<LinkObject> objects) throws Exception {
        Object out = getService().updateLinkObjects(objects);
        if (out != null) {
            checkException(out);
        }
    }

    public Boolean saveTTPairs(long queryId, LinkedList<TDPair> pairs) throws Exception {
        Object out = getService().saveTTPairs(queryId, pairs);
        if (out != null) {
            checkException(out);
            return (Boolean) out;
        }
        return null;
    }

    public LinkedList<TDPair> getTTPairs(long queryId) throws Exception {
        Object out = getService().getTTPairs(queryId);
        if (out != null) {
            checkException(out);
            return (LinkedList<TDPair>) out;
        }
        return null;
    }
    public Boolean saveTTParameters(long queryId, TimedDiagrammDataSnapshot data) throws Exception {
        Object out = getService().saveTTParameters(queryId, data);
        if (out != null) {
            checkException(out);
            return (Boolean) out;
        }
        return null;
    }

    public TimedDiagrammDataSnapshot getTTParameters(long queryId) throws Exception {
        Object out = getService().getTTParameters(queryId);
        if (out != null) {
            checkException(out);
            return (TimedDiagrammDataSnapshot) out;
        }
        return null;
    }
}
