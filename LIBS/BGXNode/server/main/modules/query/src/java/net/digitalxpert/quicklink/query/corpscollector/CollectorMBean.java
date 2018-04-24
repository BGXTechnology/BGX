package net.bgx.bgxnetwork.query.corpscollector;

import javax.naming.NamingException;

/**
 * User: Ramek
 * Date: 22.08.2006
 * Time: 15:15:39
 */
public interface CollectorMBean extends org.jboss.system.ServiceMBean {
    public String getDataSource();

    public void setDataSource(String aDataSource) throws NamingException;

    public String getUpdateQuery();

    public void setUpdateQuery(String aQuery);
}
