package net.bgx.bgxnetwork.query.corpscollector;

import org.jboss.naming.NonSerializableFactory;
import org.jboss.system.ServiceMBeanSupport;
import javax.sql.DataSource;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * User: Ramek
 * Date: 22.08.2006
 * Time: 15:16:25
 */
public class Collector extends org.jboss.system.ServiceMBeanSupport implements CollectorMBean {
    private String _datasource = null;
    private String _updateQuery = null;

    public void startService() throws Exception {
        CollectorService serv = new CollectorService(getUpdateQuery());
        serv.execute(getConnection());
    }


    public void stopService() {
    }
    public void setUpdateQuery(String aQuery) {
        if (super.getState() == STARTED)
            throw new IllegalStateException("service has to be stopped before");
        _updateQuery = aQuery;
    }

    public String getUpdateQuery() {
        return _updateQuery;
    }

    private Connection getConnection() throws NamingException, SQLException {
        InitialContext context = new InitialContext();
        DataSource datasource = (DataSource) context.lookup(_datasource);
        if (datasource == null)
              throw new NamingException("failed to find datasource");
        return datasource.getConnection();
    }

    public String getDataSource() {
        return _datasource;
    }

    public void setDataSource(String aDataSource) throws NamingException {
        if (super.getState() == STARTED)
            throw new IllegalStateException("service has to be stopped before");
        _datasource = aDataSource;
    }
}
