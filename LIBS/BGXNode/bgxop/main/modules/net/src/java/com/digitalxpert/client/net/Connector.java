package com.bgx.client.net;
import java.rmi.RemoteException;
/**
 * @author crazyart
 */
public interface Connector<T> {
	public T getServerObject() throws RemoteException;
}