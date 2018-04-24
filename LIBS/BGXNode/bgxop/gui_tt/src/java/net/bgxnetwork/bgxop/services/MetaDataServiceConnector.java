package net.bgx.bgxnetwork.bgxop.services;

import net.bgx.bgxnetwork.query.interfaces.MetaDataServiceRemote;
import com.bgx.client.net.ConnectorImpl;

/**
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 18:13:07
 * To change this template use File | Settings | File Templates.
 */
public class MetaDataServiceConnector extends ConnectorImpl<MetaDataServiceRemote> {
      public String getInvokedClassName() {
    return (System.getProperty("EAR") + "/MetaDataServiceDelegate/remote");
  }
}
