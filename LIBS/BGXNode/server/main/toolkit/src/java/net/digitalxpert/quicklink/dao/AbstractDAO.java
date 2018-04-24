package net.bgx.bgxnetwork.dao;

import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocator;
import net.bgx.bgxnetwork.toolkit.ejb.ServiceLocatorException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class AbstractDAO implements Cloneable {
    protected final Logger _log = Logger.getLogger(this.getClass());
    private Map<String, Query> _queries = new HashMap<String, Query>();
    private Map<String, String> _datasources = new HashMap<String, String>();
    private Connection connection;
    private Long oracleSessionId;

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected AbstractDAO() {
        String resourceName = this.getClass().getName().replace('.', '/') + "DAO.properties";
        InputStream stream = null;
        try {
            Properties rawMap = new Properties();
            stream = this.getClass().getClassLoader().getResourceAsStream(resourceName);
            if (stream == null) {
                _log.warn("can't found resource [" + resourceName + "]");
                return;
            }
            rawMap.load(stream);
            parseRawMap(rawMap);
        } catch (IOException e) {
            _log.warn("can't load resource [" + resourceName + "]", e);
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException ignore) {
                }
        }
        try {
            this.connection = ServiceLocator.findDataSource("java:/bgxnetworkDS_CORE").getConnection();
            setSessionId();
        } catch (ServiceLocatorException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void setSessionId() throws SQLException {
        PreparedStatement pstm = connection.prepareStatement(
                "SELECT SYS_CONTEXT('USERENV','SESSIONID') FROM DUAL");
        ResultSet rs = pstm.executeQuery();
        rs.next();
        oracleSessionId = rs.getLong(1);
    }

    private void parseRawMap(Properties aRawMap) {
        for (Iterator i = aRawMap.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            int dot = key.indexOf('.');
            String name = key.substring(0, dot);
            String spec = key.substring(dot + 1);
            if ("Datasource".equalsIgnoreCase(spec)) {
                _datasources.put(name, aRawMap.getProperty(key));
                _log.debug("added datasource [" + name + "]");
                continue;
            }
            Query query = getQuery(name);
            if (query == null) {
                query = new Query();
                _queries.put(name, query);
                _log.debug("added query [" + name + "]");
            }
            if ("Parameters".equalsIgnoreCase(spec)) {
                query.setParameters(parseParametersSpec(aRawMap.getProperty(key)));
                continue;
            }
            if ("Query".equalsIgnoreCase(spec)) {
                query.setStatement(aRawMap.getProperty(key));
            }
        }
    }

    private Map<String, Integer> parseParametersSpec(String aSpec) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        StringTokenizer st = new StringTokenizer(aSpec, ",");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            int colon = token.indexOf(':');
            String parameterName = token.substring(0, colon).trim();
            Integer parameterIndex = new Integer(token.substring(colon + 1));
            map.put(parameterName, parameterIndex);
        }
        return map;
    }

    protected Query getQuery(String aName) {
        return _queries.get(aName);
    }

    protected String getDatasourceName(String aName) {
        return _datasources.get(aName);
    }

    /**
     * «акрывает ресурсы соединени€, если они не null
     *
     * @param resultSet
     * @param statement
     */
    public void doClose(ResultSet resultSet, Statement statement) {
        if (resultSet != null)
            try {
                resultSet.close();
            } catch (SQLException ignore) {
            }
        if (statement != null)
            try {
                statement.close();
            } catch (SQLException ignore) {
            }
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ignore) {
            }
    }

    protected Connection getConnectionFromDataSource(String aDatasourceName) throws SQLException {
        return ServiceLocator.findDataSource(getDatasourceName(aDatasourceName)).getConnection();
    }

    protected Connection getConnection() throws SQLException {
        return connection;
    }

    public Long getSessionId() {
        return oracleSessionId;
    }

    protected Long getAsLong(ResultSet aSet, String aColumnName) throws SQLException {
        long value = aSet.getLong(aColumnName);
        if (aSet.wasNull()) return null;
        return value;
    }

    protected Double getAsDouble(ResultSet aSet, String aColumnName) throws SQLException {
        double value = aSet.getLong(aColumnName);
        if (aSet.wasNull()) return null;
        return value;
    }
}
