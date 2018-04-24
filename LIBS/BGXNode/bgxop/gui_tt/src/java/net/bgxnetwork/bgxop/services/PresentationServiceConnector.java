package net.bgx.bgxnetwork.bgxop.services;

import com.bgx.client.net.ConnectorImpl;
import net.bgx.bgxnetwork.query.interfaces.PresentationServiceRemote;

/**
 * Created by IntelliJ IDEA.
 * User: O.Gerasimenko
 * Date: 13.02.2007
 * Time: 18:18:16
 * To change this template use File | Settings | File Templates.
 */
public class PresentationServiceConnector extends ConnectorImpl<PresentationServiceRemote> {
      public String getInvokedClassName() {
    return (System.getProperty("EAR") + "/PresentationServiceDelegate/remote");
  }
}