/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.query.ejb;

import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.transfer.data.Relation;
import net.bgx.bgxnetwork.persistence.query.QueryEntity;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.dao.DAOFactory;
import net.bgx.bgxnetwork.dao.AbstractDAO;
import net.bgx.bgxnetwork.query.dao.interfaces.LVExecutorDAO;
import net.bgx.bgxnetwork.security.auth.server.LanUsers;

import java.sql.SQLException;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * User: A.Borisenko
 * Date: 11.06.2007
 * Time: 18:30:55
 */
public class  LVExecutor extends AbstractExecutor {
    private static Logger log = Logger.getLogger(LVExecutor.class.getName());
    private Locale clientLocale = Locale.getDefault();
    private HashMap<String, Long> objects = new HashMap<String, Long>();
    Long nodeID = 0L;
    Long linkID = 0L;
    Long queryId = null;

    private DAOFactory _daoFactory = DAOFactory.getInstance(LVExecutorDAO.class.getPackage().getName(),
            getClass().getClassLoader());


    public LVExecutor() {
        setDAO((AbstractDAO) _daoFactory.getDAO(LVExecutorDAO.class));
    }

    public void setClientLocale(Locale clientLocale) {
        this.clientLocale = clientLocale;
    }

    public QueryData execute(Query q) throws QueryBusinesException, QueryDataException, SQLException {
        //test:
//        log.info("execute "+q.getId()+" started");
//        for (int i =0; i<30; i++){
//            try {
//                Thread.sleep(30000);
//                log.info("   Thread: "+Thread.currentThread().getName()+"  in loop : "+i);
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        return new QueryData();

        queryId = q.getId();
        try{
            Long uid = ((LVExecutorDAO) _dao).getUIDbyQuery(q.getId());
            String principalName = LanUsers.getNameById(uid);
            String userName = principalName;
            String pwd = LanUsers.getUserPassword(principalName);
        ArrayList<String> queryIdList = (ArrayList<String>) q.getParameter(QueryParameterType.QUEST_ID);
        ArrayList<Relation> relationList = ((LVExecutorDAO) _dao).getRelationList(queryIdList, userName, pwd);
        for (Relation relation : relationList){
            String firstObject = relation.getSourceObject();
            String secondObject = relation.getTargetObject();

            Long firstObjectId = getObjectId(firstObject, userName, pwd);
            Long secondObjectId = getObjectId(secondObject, userName, pwd);
            createNewLink(firstObjectId, secondObjectId, relation);
        }
        }
        catch(Exception e){
            log.error("exceptino while execute query "+queryId+" "+e);
        }
        return new QueryData();
    }

    public QueryData copyDataFromQuery(Long oldQID, Long newQID) throws QueryBusinesException, QueryDataException, SQLException {
        ((LVExecutorDAO) _dao).copyDataFromQuery(oldQID, newQID);
        return new QueryData();
    }

    private Long getObjectId(String objectName, String userName, String pwd){
        Long id = objects.get(objectName);
        if (id == null){
            id = createNewObject(objectName, userName, pwd);
            objects.put(objectName, id);
        }
        return id;
    }

    private Long createNewObject(String objectName, String userName, String pwd){
        return ((LVExecutorDAO) _dao).createObject(objectName, nodeID++, queryId, userName, pwd);
    }

    private void createNewLink(Long startObjectId, Long endObjectId, Relation relation){
        Long linkId = ((LVExecutorDAO) _dao).createLink(startObjectId, endObjectId, linkID++, queryId);
        ((LVExecutorDAO) _dao).setAttributesToLink(linkId, queryId, relation);
    }

    public void commitQueryData(Long qId, String netName, QueryData data) throws QueryDataException {
        writeQueryData(qId, netName, data);
    }

    private void writeQueryData(long qid, String netName, QueryData data) throws QueryDataException {
        ((LVExecutorDAO) _dao).writeQueryData(qid, netName, data);
    }

    public QueryData getNodeList(QueryEntity q, int vxId) throws QueryDataException {
        QueryData data = ((LVExecutorDAO) _dao).getNodeList(q.getId(), q.getNetworkName(), vxId);
        return data;
    }

    public QueryData readQueryData(QueryEntity q) throws QueryDataException {
        return ((LVExecutorDAO) _dao).readQueryData(q.getId(), q.getLimited());
    }

    public void doClose() {
        ((AbstractDAO) _dao).doClose(null, null);
    }

}
