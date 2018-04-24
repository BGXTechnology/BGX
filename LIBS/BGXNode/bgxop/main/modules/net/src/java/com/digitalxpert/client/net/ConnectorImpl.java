package com.bgx.client.net;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
public abstract class ConnectorImpl<T> implements Connector {
	private T serverObject;
	private static Properties prop;
	private static final String PATH = "jndi.properties";
	public T getServerObject() throws RemoteException {
		InitialContext ctx = null;
		try {
			if (serverObject == null) {
				loadProperties();
				ctx = new InitialContext(prop);
				serverObject = (T) ctx.lookup(getInvokedClassName());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("EXCEPTION SEREVER OBJECT=" + ex.getClass());
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
		return serverObject;
	}
	private void loadProperties() {
		try {
			if (prop == null) {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream is = classLoader.getResourceAsStream(PATH);
				prop = new Properties();
				prop.load(is);
				is.close();
				Enumeration keys = prop.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
					String value = (String) prop.get(key);
					System.setProperty(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public abstract String getInvokedClassName();
}
