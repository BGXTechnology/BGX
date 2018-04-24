package net.bgx.bgxnetwork.query.dao.interfaces;

import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.data.Relation;
import net.bgx.bgxnetwork.exception.query.QueryDataException;

import java.util.ArrayList;

/**
 * User: A.Borisenko
 * Date: 11.06.2007
 * Time: 18:34:48
 */
public interface LVExecutorDAO {
    public QueryData readQueryData(Long qId, int limit) throws QueryDataException;
    public QueryData getNodeList(Long qId, String netName, int vxId) throws QueryDataException;
    public boolean writeQueryData(long qid, String netName, QueryData data) throws QueryDataException;
    public ArrayList<Relation> getRelationList(ArrayList<String> qids, String userName, String pwd);
    public Long createObject(String name, Long nodeId, Long queryId, String userName, String pwd);
    public Long createLink(Long startId, Long endId, Long linkId, Long queryId);
    public void setAttributesToLink(Long linkId, Long queryId, Relation relation);
    public void copyDataFromQuery(Long oldId, Long newId);
    public Long getUIDbyQuery(Long qId);
}
