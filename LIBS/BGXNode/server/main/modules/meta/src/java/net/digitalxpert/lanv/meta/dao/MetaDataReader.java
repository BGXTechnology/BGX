/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.lanv.meta.dao;

import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.data.Request;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.Quest;
import net.bgx.bgxnetwork.security.auth.server.LanUsers;
import net.bgx.lanv.meta.util.MetaTableHelper;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * User: A.Borisenko
 * Date: 08.06.2007
 * Time: 13:19:28
 */
public class MetaDataReader /*extends AbstractDAO*/ {
    private Logger log = Logger.getLogger(MetaDataReader.class.getName());
//    private DAOFactory _daoFactory = DAOFactory.getInstance(MetaDataReader.class.getPackage().getName(),
//            getClass().getClassLoader());
    private String SCHEMA_NAME = "";

    public MetaDataReader() {
        //AbstractDAO dao = (AbstractDAO) _daoFactory.getDAO(MetaDataReader.class);
        SCHEMA_NAME = System.getProperty("db.name");
    }

    public ArrayList<Request> readRequestsBy(String principalName){
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet res = null;
        ArrayList<Request> requests = new ArrayList<Request>();

        try {
            String userName = principalName;
            Long userID = LanUsers.getUserId(principalName);
            String pwd = LanUsers.getUserPassword(principalName);

            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            connection = DriverManager.getConnection(jdbcDriverURL, userName, pwd);

            pstm = connection.prepareStatement("SELECT * FROM "+SCHEMA_NAME+".V_ZAJAVKI where USERID = ? order by z_id desc");
            pstm.setLong(1, userID);
            res = pstm.executeQuery();

            ResultSetMetaData resMeta = res.getMetaData();
            int columnCount = resMeta.getColumnCount();

            while(res.next()){
                LVObject dataObject = MetaTableHelper.createLVObject(res, "V_ZAJAVKI", userName, pwd);
                Request request = new Request();
                request.addFields(dataObject.getFields());
                request.setId(res.getLong(1));
                request.setUserId(userID);
                request.setName(res.getString(columnCount));
                requests.add(request);
            }
        }
        catch (SQLException e) {
            log.error(e);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            try {
                res.close();
            }
            catch (SQLException e) {;}

            try {
                pstm.close();
            }
            catch (SQLException e) {;}

            try {
                connection.close();
            }
            catch (SQLException e) {
                log.warn("Couldn't close connection "+e);
            }
        }
        return requests;
    }

    public ArrayList<Quest> readQuestsBy(String principalName, Long requestID){
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet res = null;
        ArrayList<Quest> queries = new ArrayList<Quest>();

        try {
            String userName = principalName;
            Long userID = LanUsers.getUserId(principalName);
            String pwd = LanUsers.getUserPassword(principalName);

            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            connection = DriverManager.getConnection(jdbcDriverURL, userName, pwd);

            pstm = connection.prepareStatement("SELECT vq.Z_ID, vq.QID, vq.ATR1, vq.ATR2, vq.ATR3, vq.ATR4, vq.NAME FROM "+SCHEMA_NAME+".V_QUERY vq, "+SCHEMA_NAME+".V_ZAJAVKI vz where vz.Z_ID = ? and vq.Z_ID = vz.Z_ID and vz.USERID = ? order by vq.QID asc");
            pstm.setLong(1, requestID);
            pstm.setLong(2, userID);
            res = pstm.executeQuery();

            ResultSetMetaData resMeta = res.getMetaData();
            int columnCount = resMeta.getColumnCount();

            while(res.next()){
                LVObject dataObject = MetaTableHelper.createLVObject(res, "V_QUERY", userName, pwd);
                Quest lvQuery  = new Quest();
                lvQuery.addFields(dataObject.getFields());
                lvQuery.setRequestId(res.getLong(1));
                lvQuery.setId(res.getLong(2));
                lvQuery.setName(res.getString(columnCount));
                queries.add(lvQuery);
            }
        }
        catch (SQLException e) {
            log.error(e);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            try {
                res.close();
            }
            catch (SQLException e) {;}

            try {
                pstm.close();
            }
            catch (SQLException e) {;}

            try {
                connection.close();
            }
            catch (SQLException e) {
                log.warn("Couldn't close connection "+e);
            }
        }
        return queries;
    }



    public HashMap<String, String> getCaptionsByTable(String tableName, String userName, String pwd){
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HashMap<String, String> captions = new HashMap<String, String>();
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            connection = DriverManager.getConnection(jdbcDriverURL, userName, pwd);

            //connection = getConnection();
            pstm = connection.prepareStatement("SELECT table_name, field_name, value, id FROM "+SCHEMA_NAME+".VIEW_META_DATA WHERE TABLE_NAME = ?");
            pstm.setString(1, tableName);
            rs = pstm.executeQuery();
            while(rs.next()){
                String columnName = rs.getString(2);
                String columnCaption = rs.getString(3);
                captions.put(columnName, columnCaption);
            }
        }
        catch (SQLException e) {
            log.error(e);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            try{
                rs.close();
            }
            catch(Exception e){;}

            try{
                pstm.close();
            }
            catch(Exception e){;}

            try {
                connection.close();
            }
            catch (SQLException e) {
                log.warn("Couldn't close connection by getCAptionByTable, caused : "+e);
            }

        }
        return captions;
    }

    public HashMap<String, Long> getCodeFieldsByTable(String tableName, String userName, String pwd){
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        HashMap<String, Long> codes = new HashMap<String, Long>();
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            connection = DriverManager.getConnection(jdbcDriverURL, userName, pwd);

            //connection = getConnection();
            pstm = connection.prepareStatement("SELECT table_name, field_name, value, id FROM "+SCHEMA_NAME+".VIEW_META_DATA WHERE TABLE_NAME = ?");
            pstm.setString(1, tableName);
            rs = pstm.executeQuery();
            while(rs.next()){
                String columnName = rs.getString(2);
                Long fieldCode = rs.getLong(4);
                codes.put(columnName, fieldCode);
            }
        }
        catch (SQLException e) {
            log.error(e);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally{
            try{
                rs.close();
            }
            catch(Exception e){;}

            try{
                pstm.close();
            }
            catch(Exception e){;}

            try {
                connection.close();
            }
            catch (SQLException e) {
                log.warn("Couldn't close connection by getCAptionByTable, caused : "+e);
            }

        }
        return codes;
    }

    public LVObject getAttributesOverview(String principalName, String tableName){
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet res = null;
        LVObject dataObject = null;

        try {
            String userName = principalName;
            String pwd = LanUsers.getUserPassword(principalName);

            Class.forName("oracle.jdbc.OracleDriver");
            String jdbcDriverURL = System.getProperty("net.bgx.lanv.database.URL");
            connection = DriverManager.getConnection(jdbcDriverURL, userName, pwd);

            pstm = connection.prepareStatement("SELECT * FROM "+SCHEMA_NAME+"."+tableName+" where rownum =1");
            res = pstm.executeQuery();

            while(res.next()){
                dataObject = MetaTableHelper.createLVObject(res, tableName, userName, pwd);
                break;
            }

        }
        catch (SQLException e) {
            log.error(e);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace(); 
        }
        finally{
            try {
                res.close();
            }
            catch (SQLException e) {;}

            try {
                pstm.close();
            }
            catch (SQLException e) {;}

            try {
                connection.close();
            }
            catch (SQLException e) {
                log.warn("Couldn't close connection "+e);
            }
        }
        return dataObject;
    }
}
