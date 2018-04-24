package net.bgx.bgxnetwork.bgxop.services;

import com.bgx.client.net.ConnectorImpl;
import net.bgx.bgxnetwork.query.interfaces.QueryServiceRemote;

/**
 * Class QueryServiceConnector
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class QueryServiceConnector extends ConnectorImpl<QueryServiceRemote> {

  public String getInvokedClassName() {
    return (System.getProperty("EAR") + "/QueryServiceDelegate/remote");
  }

}
