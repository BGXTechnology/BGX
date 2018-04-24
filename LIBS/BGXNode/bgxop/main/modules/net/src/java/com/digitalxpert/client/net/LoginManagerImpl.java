package com.bgx.client.net;
import net.bgx.bgxnetwork.admin.interfaces.AdminServiceRemote;
public class LoginManagerImpl extends ConnectorImpl<AdminServiceRemote> {
	public String getInvokedClassName() {
		return (System.getProperty("EAR") + "/SecurityLoginServiceDelegate/remote");
	}
}
