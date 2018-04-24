/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package com.bgx.client.net;

import net.bgx.bgxnetwork.security.auth.server.inerfaces.SecurityLoginServiceRemote;
import net.bgx.bgxnetwork.transfer.types.LoginInfo;

/**
 * User: A.Borisenko
 * Date: 07.06.2007
 * Time: 14:53:54
 */
public class LoginManager extends AbstractManager<SecurityLoginServiceRemote>{
    protected Connector<SecurityLoginServiceRemote> getConnector() {
        return new LoginManagerImpl();
    }

    public LoginInfo auth(){
        try{
            SecurityLoginServiceRemote serv = getServerObject();
            Object obj = serv.getCallerLoginInfo();
            checkException(obj);
            return new LoginInfo();
        }
        catch(Exception e){
            return null;
        }
    }

    private void checkException(Object obj) throws Exception{
        if(obj instanceof Throwable){
            throw (Exception) obj;
        }
    }
}
