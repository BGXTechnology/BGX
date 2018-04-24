package com.bgx.client.net;
import java.rmi.RemoteException;
public abstract class AbstractManager<T> {
	protected ClientLogin cl;
	protected T remoteObject;
	protected Connector<T> connector;
	protected T getServerObject() {
		if (remoteObject == null) {
			try {
				connector = getConnector();
				remoteObject = connector.getServerObject();
				if (cl != null) {
					cl.login();
				}
			} catch (Exception e) {
				remoteObject = null;
				e.printStackTrace();
			}
		}
		return remoteObject;
	}
	protected abstract Connector<T> getConnector();
}
