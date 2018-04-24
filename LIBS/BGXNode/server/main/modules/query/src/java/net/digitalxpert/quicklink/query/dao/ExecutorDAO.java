package net.bgx.bgxnetwork.query.dao;

import net.bgx.bgxnetwork.dao.AbstractDAO;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.metadata.MetaDataBusinessException;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceLocal;
import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import net.bgx.bgxnetwork.persistence.metadata.PropertyVal;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;

import java.sql.*;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import oracle.spatial.network.NetworkFactory;
import oracle.spatial.network.Node;
import oracle.spatial.network.Link;
import oracle.spatial.network.NetworkDataException;

/**
 * User: A.Borisenko
 * Date: 14.11.2006
 * Time: 11:47:28
 */
public class ExecutorDAO extends AbstractDAO {
    private Logger log = Logger.getLogger(ExecutorDAO.class.getName());
    protected PresentationServiceLocal presentationService;

    public ExecutorDAO() {
        presentationService =
                (PresentationServiceLocal) ServiceLocator.findEjb3LocalByDefault("PresentationService");
    }

    public QueryData readQueryData(Long qId, int limit) throws QueryDataException {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rset = null;
        String queryN = "select node_id from pln_node$ where query_id=" + qId;
        String queryL = "select link_id, start_node_id, end_node_id from pln_link$ where query_id=" + qId;

        QueryData data = new QueryData();
        ArrayList<Long> nodeKeys = new ArrayList<Long>();
        ArrayList<Long> linkKeys = new ArrayList<Long>();

        data.setNetwork(NetworkFactory.createLogicalNetwork("pln", 1, true));
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            rset = stmt.executeQuery(queryN);
            while (rset.next()) {
                int id = rset.getInt(1);
                Node n = NetworkFactory.createNode(id, "");
                data.getNetwork().addNode(n);
                nodeKeys.add((long) id);
            }
            rset.close();

            int id1, id2;
            rset = stmt.executeQuery(queryL);
            while (rset.next()) {
                int id = rset.getInt(1);

                id1 = rset.getInt(2);
                id2 = rset.getInt(3);
                Node n = null;
                Node n2 = null;
                try {
                    n = data.getNetwork().getNode(id1);
                    n2 = data.getNetwork().getNode(id2);
                }
                catch (Exception e) {
                    log.error("Unable get node from network: " + e);
                }

                if (n == null) {
                    log.warn("Fetched node id not found in node list: " + id1);
                    continue;
                } else if (n2 == null) {
                    log.warn("Fetched node id not found in node list: " + id1);
                    continue;
                }

                Link l = NetworkFactory.createLink(id, "", n, n2, 1);
                data.getNetwork().addLink(l);
                linkKeys.add((long)id);
            }
            rset.close();
            data.setObjectsLimit(limit);
        }
        catch (SQLException e) {
            throw new QueryDataException(ErrorList.DATA_CONNECTION_ERROR);
        }
        catch (NetworkDataException e) {
            throw new QueryDataException(ErrorList.DATA_NETWORK_ORACLE_EXCEPTION);
        }
        finally {
            try {
                stmt.close();
            } catch (SQLException e) {
                throw new QueryDataException(ErrorList.DATA_CLOSE_OPERATION_EXCEPTION);
            }
        }

        PresentationServiceLocal pService = (PresentationServiceLocal) ServiceLocator.findEjb3LocalByDefault("PresentationService");
        try{
            data.setObjects(pService.getControlObjects(nodeKeys, qId));
            data.setLinkObjects(pService.getLinkObjects(linkKeys, qId));
        }
        catch(Exception e){
            log.warn("Couldn't get parameter value for "+e);
            data.setObjects(new HashMap<Integer, ControlObject>());
            data.setLinkObjects(new HashMap<Integer, LinkObject>());
        }

        return data;
    }

    public void removeQueryData(Long qId) throws QueryDataException {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate("delete from LINK_VALUE where query_id=" + qId);
            stmt.executeUpdate("delete from PLN_LINK$ where query_id=" + qId);
            stmt.executeUpdate("delete from PROP_VALUE where query_id=" + qId);
            stmt.executeUpdate("delete from PLN_NODE$ where query_id=" + qId);
            stmt.close();
        }
        catch (SQLException e) {
            throw new QueryDataException(ErrorList.DATA_CONNECTION_ERROR);
        }
    }

    public int removeLinksFromGraph(Long qId, String netName, String[] exp) throws QueryDataException {
        String query = "delete from LINK_VALUE WHERE QUERY_ID="+qId+" and LINK_ID in ";
        removeFromGraph(query, exp);
        query = "delete from pln_link$ where query_id=" + qId + " and link_id in ";
        return removeFromGraph(query, exp);
    }

    public int removeNodesFromGraph(Long qId, String netName, String[] exp) throws QueryDataException {
        String query = "delete from PROP_VALUE where query_id=" + qId + " and node_id in ";
        removeFromGraph(query, exp);
        query = "delete from pln_node$ where query_id=" + qId + " and node_id in ";
        return removeFromGraph(query, exp);
    }

    private int removeFromGraph(String query, String[] exp) throws QueryDataException {
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = getConnection();
            stmt = connection.createStatement();
            int res = 0;
            for (String s : exp) {
                res += stmt.executeUpdate(query + "(" + s + ")");
            }
            stmt.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new QueryDataException(ErrorList.DATA_CANNOT_EXECUTE_UPDATE);
        }
    }

}
