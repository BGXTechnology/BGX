package net.bgx.bgxnetwork.bgxop.services;

import com.bgx.client.net.ConnectorImpl;
import net.bgx.bgxnetwork.query.interfaces.CardServiceRemote;

/**
 * Class CardServiceConnector
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class CardServiceConnector extends ConnectorImpl<CardServiceRemote> {
  public String getInvokedClassName() {
    return (System.getProperty("EAR") + "/CardServiceDelegate/remote");
  }
}
