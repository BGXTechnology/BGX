/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.query.dao;

import net.bgx.bgxnetwork.query.dao.interfaces.LVExecutorDAO;
import net.bgx.bgxnetwork.transfer.query.QueryData;
import net.bgx.bgxnetwork.transfer.data.Relation;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.exception.query.QueryDataException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.persistence.metadata.LinkPK;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.PropertyType;
import net.bgx.lanv.meta.util.MetaTableHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.driver.OracleCallableStatement;
import org.jboss.resource.adapter.jdbc.WrappedCallableStatement;
import org.apache.log4j.Logger;


/**
 * User: A.Borisenko
 * Date: 11.06.2007
 * Time: 18:32:35
 */
public class LVExecutorData extends ExecutorDAO implements LVExecutorDAO {
    private Logger log = Logger.getLogger(LVExecutorData.class.getName());
    private String SCHEMA_NAME = System.getProperty("db.name");

    public QueryData getNodeList(Long qId, String netName, int vxId) throws QueryDataException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean writeQueryData(long qid, String netName, QueryData data) throws QueryDataException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void copyDataFromQuery(Long oldId, Long newId){
        Connection connection = null;
        PreparedStatement pstm  = null;
        try {
            connection = getConnection();
            Statement stmt = connection.createStatement();
            String sql = "INSERT INTO PLN_NODE$(NODE_ID, QUERY_ID, OBJECT_TYPE) SELECT NODE_ID, "+newId+",OBJECT_TYPE FROM PLN_NODE$ WHERE QUERY_ID = "+oldId;
            stmt.executeUpdate(sql);
            sql = "INSERT INTO PLN_LINK$(QUERY_ID, LINK_ID,END_NODE_ID, RELATION_TYPE, START_NODE_ID) SELECT "+newId+", LINK_ID,END_NODE_ID, RELATION_TYPE, START_NODE_ID FROM PLN_LINK$ WHERE QUERY_ID = "+oldId;
            stmt.executeUpdate(sql);
            sql = "INSERT INTO PROP_VALUE(PROP_TYPE_ID, NODE_ID, QUERY_ID, VALUE_OBJECT) SELECT PROP_TYPE_ID, NODE_ID, "+newId+", VALUE_OBJECT FROM PROP_VALUE WHERE QUERY_ID="+oldId;
            stmt.executeUpdate(sql);
            sql = "INSERT INTO LINK_VALUE(PROP_TYPE_ID, QUERY_ID, LINK_ID, VALUE_OBJECT) SELECT PROP_TYPE_ID, "+newId+", LINK_ID, VALUE_OBJECT FROM LINK_VALUE WHERE QUERY_ID="+oldId;
            stmt.executeUpdate(sql);
            try{stmt.close();}
            catch(Exception eee){log.error(eee);}
/*
            pstm =
                connection.prepareStatement("INSERT INTO PLN_NODE$(NODE_ID, QUERY_ID, OBJECT_TYPE, ) SELECT NODE_ID, "+newId+",OBJECT_TYPE FROM PLN_NODE$ WHERE QUERY_ID = "+oldId);
            pstm.executeUpdate();
            try{pstm.close();}
            catch(Exception eee){log.error(eee);}

            pstm =
                connection.prepareStatement("INSERT INTO PLN_LINK$(QUERY_ID, LINK_ID,END_NODE_ID, RELATION_TYPE, START_NODE_ID) SELECT "+newId+", LINK_ID,END_NODE_ID, RELATION_TYPE, START_NODE_ID FROM PLN_LINK$ WHERE QUERY_ID = "+oldId);
            pstm.executeUpdate();
            try{pstm.close();}
            catch(Exception eee){log.error(eee);}

            pstm =
                connection.prepareStatement("INSERT INTO PROP_VALUE(PROP_TYPE_ID, NODE_ID, QUERY_ID, VALUE_OBJECT) SELECT PROP_TYPE_ID, NODE_ID, "+newId+", VALUE_OBJECT FROM PROP_VALUE WHERE QUERY_ID="+oldId);
            pstm.executeUpdate();
            try{pstm.close();}
            catch(Exception eee){log.error(eee);}

            pstm =
                connection.prepareStatement("INSERT INTO LINK_VALUE(PROP_TYPE_ID, QUERY_ID, LINK_ID, VALUE_OBJECT) SELECT PROP_TYPE_ID, "+newId+", LINK_ID, VALUE_OBJECT FROM LINK_VALUE WHERE QUERY_ID="+oldId);
            pstm.executeUpdate();
            try{pstm.close();}
            catch(Exception eee){log.error(eee);}
*/

        }
        catch (SQLException e) {
            log.error(e);
        }
    }

    public Long getUIDbyQuery(Long qId){
        Connection connection = null;
        Statement stmt  = null;
        Long uid = null;
        try {
            connection = getConnection();
            stmt = connection.createStatement();
            String query = "SELECT ID_OWNER FROM QUERY_HEAD WHERE ID_QUERY = "+qId;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                uid = rs.getLong(1);
            }
            rs.close();
        }
        catch (SQLException e) {
            log.error(e);
        }
        return uid;
    }

    public ArrayList<Relation> getRelationList(ArrayList<String> qids, String userName, String pwd) {
        Connection connection = null;
        Statement stmt = null;
        ArrayList<Relation> relations = new ArrayList<Relation>();

        if (!qids.isEmpty())
        try {
            connection = getRemoteConnection(userName, pwd);

            stmt = connection.createStatement();
            String query = "SELECT * FROM "+SCHEMA_NAME+".V_CONN WHERE QID in (";
            for (String arg : qids)
                query += arg + ",";
            query = query.substring(0, query.length()-1)+")";


            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                LVObject dataObject = MetaTableHelper.createLVObject(rs, "V_CONN", userName, pwd);
                Relation relation = new Relation();
                relation.addFields(dataObject.getFields());
                relation.setqID(rs.getLong(1));
                relation.setSourceObject(rs.getString(2));
                relation.setTargetObject(rs.getString(3));
                relations.add(relation);
            }
            try{
                rs.close();
            }
            catch(Exception e){
                log.warn(e);
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        finally{
            try{
                if (stmt != null)
                    stmt.close();
            }
            catch(Exception e){
                log.warn(e);
            }
            //todo только для этой версии закрываем Connection т.к. мы его получаем не из пула
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                log.error(e);
            }
        }
        return relations;
    }

    protected Connection getRemoteConnection(String userName, String pwd){
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            return DriverManager.getConnection(jdbcDriverURL, userName, pwd);
        }
        catch (SQLException e) {
            log.error("Couldn't get connection:"+ e);
            return null;
        }
        catch (ClassNotFoundException e) {
            log.error("Couldn't get connection:"+ e);
            return null;
        }
    }

    public Long createObject(String name, Long nodeId, Long queryId, String userName, String pwd){
        NodePK pk = new NodePK(nodeId, queryId);
        presentationService.createControlObject(pk);
        setAttributesToObject(pk, name, userName, pwd);
        return pk.getNodeId();
    }

    public Long createLink(Long startId, Long endId, Long linkId, Long queryId) {
        LinkPK pk = new LinkPK(linkId, queryId);
        presentationService.createLinkObject(pk, startId, endId);
        presentationService.addValueToLink(pk, 16L, "1");
        return pk.getLinkId();
    }

    public void setAttributesToLink(Long linkId, Long queryId, Relation relation) {
        LinkPK linkPk = new LinkPK(linkId, queryId);
        LinkObject lo = presentationService.getLinkObject(linkPk);
        List<PropertyType> propertyTypes = lo.getLinkType().getPropertyTypes();
        for (PropertyType pt : propertyTypes){
            if (pt != null){
                String propertyName = pt.getNamePropertyType();
                for(FieldObject fo : relation.getFields()){
                    if (fo.getDbFieldName().equals(propertyName)){
                        String strValue = (String)FieldObject.convertToString(fo.getValue(), fo.getDataTypeCode());
                        presentationService.addValueToLink(linkPk, (long)pt.getPropertyTypeId(), strValue);
                        break;
                    }
                }
            }
        }
    }

    private void setAttributesToObject(NodePK pk, String name, String userName, String pwd){
        //set name to Object
        presentationService.addValueToObject(pk, 1L, name);
        //set visible flag
        presentationService.addValueToObject(pk, 16L, "1");
        //set group flag
        presentationService.addValueToObject(pk, 17L, "-1");
        //set attributes from DB
        setAttributesFromSource(pk, name, userName, pwd);
    }

    private void setAttributesFromSource(NodePK pk, String name, String userName, String pwd){
        Connection connection = null;
        PreparedStatement pstm = null;
        try {
            connection = getRemoteConnection(userName, pwd);
            pstm = connection.prepareStatement("SELECT ATR1 FROM "+SCHEMA_NAME+".V_OBJ WHERE OBJ = ?");
            pstm.setString(1, name);
            ResultSet rs = pstm.executeQuery();
            boolean isFound = false;
            while (rs.next()){
                presentationService.addValueToObject(pk, 2L, rs.getString(1));
                isFound = true;
            }
            rs.close();
            if(!isFound)
                presentationService.addValueToObject(pk, 2L, name);
        }
        catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        finally{
            //todo только для этой версии закрываем Connection т.к. мы его получаем не из пула
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                log.error(e);
            }
        }
    }
}
