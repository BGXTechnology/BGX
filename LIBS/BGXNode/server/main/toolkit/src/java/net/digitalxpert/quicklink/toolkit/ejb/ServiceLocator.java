/**
 *$Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/toolkit/ejb/ServiceLocator.java#2 $
 *$DateTime: 2006/04/13 23:45:12 $
 *$Change: 7588 $
 *$Author: A.Nikutov $
 */
package net.bgx.bgxnetwork.toolkit.ejb;
import net.bgx.bgxnetwork.toolkit.ErrorList;
import org.apache.log4j.Logger;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class ServiceLocator {
	private static final Logger _log = Logger.getLogger(ServiceLocator.class);
	private static String EAR_NAME = null;
	private InitialContext _initialContext;
	private InternalCache _internalCache;
	private static ServiceLocator _instance = null;
	private ServiceLocator() throws ServiceLocatorException {
		try {
			_initialContext = new InitialContext();
			_internalCache = new InternalCache();
			EAR_NAME = System.getProperty("net.bgx.bgxnetwork.ear.name");
		} catch (NamingException e) {
			throw new ServiceLocatorException(ErrorList.LOCATOR_CREATION_FAILED, e);
		} catch (Exception e) {
			throw new ServiceLocatorException(ErrorList.LOCATOR_CREATION_FAILED, e);
		}
	}
	private static synchronized ServiceLocator getInstance() {
		if (_instance == null)
			_instance = new ServiceLocator();
		return _instance;
	}
	public static QueueConnectionFactory findQueueConnectionFactory(String aJndiName) throws ServiceLocatorException {
		return (QueueConnectionFactory) getInstance().lookupTroughCache(aJndiName);
	}
	public static Queue findQueue(String aJndiName) throws ServiceLocatorException {
		return (Queue) getInstance().lookupTroughCache(aJndiName);
	}
	public static TopicConnectionFactory findTopicConnectionFactory(String aJndiName) throws ServiceLocatorException {
		return (TopicConnectionFactory) getInstance().lookupTroughCache(aJndiName);
	}
	public static Topic findTopic(String aJndiName) throws ServiceLocatorException {
		return (Topic) getInstance().lookupTroughCache(aJndiName);
	}
	public static DataSource findDataSource(String aJndiName) throws ServiceLocatorException {
		return (DataSource) getInstance().lookupTroughCache(aJndiName);
	}
	/**
	 * Wrapper arround {@link InitialContext#lookup(java.lang.String)}
	 */
	public static Object find(String aJndiName) {
		return getInstance().internalLookup(aJndiName);
	}
	public static Object findEjb3LocalByDefault(String ejb3BeanName) {
		return getInstance().internalLookup(EAR_NAME + "/" + ejb3BeanName + "/local");
	}
	public static Object findEjb3RemoteByDefault(String ejb3BeanName) {
		return getInstance().internalLookup(EAR_NAME + "/" + ejb3BeanName + "/remote");
	}
	public Object internalLookup(String aJndiName) {
		try {
			Object jndiObject = _initialContext.lookup(aJndiName);
			if (jndiObject == null)
				throw new ServiceLocatorException(ErrorList.GOT_NULL_BY_LOOKUP, new Object[] { aJndiName }, null);
			return jndiObject;
		} catch (NamingException e) {
			throw new ServiceLocatorException(ErrorList.LOOKUP_FAILED, new Object[] { aJndiName }, e);
		}
	}
	private Object lookupTroughCache(String aJndiName) {
		if (getInternalCache().containsKey(aJndiName)) {
			Object cachedObject = getInternalCache().get(aJndiName);
			if (cachedObject == null)
				throw new ServiceLocatorException(ErrorList.CACHED_OBJECT_IS_NULL, new Object[] { aJndiName }, null);
			return cachedObject;
		} else {
			Object object = internalLookup(aJndiName);
			getInternalCache().put(aJndiName, object);
			return object;
		}
	}
	private InternalCache getInternalCache() {
		return _internalCache;
	}
	public static void bind(String name, Object obj) {
		try {
			getInstance()._initialContext.bind(name, obj);
		} catch (NamingException e) {
			throw new ServiceLocatorException(ErrorList.LOOKUP_FAILED, e);
		}
	}
	private class InternalCache {
		private Map<String, Object> _delegateMap = null;
		private boolean _disabled;
		InternalCache() {
			_disabled = Boolean.getBoolean(getClass().getName() + ".DISABLED");
			if (_disabled)
				_log.warn("internal cache DISABLED");
			_delegateMap = _disabled ? new HashMap<String, Object>() : Collections.synchronizedMap(new HashMap<String, Object>());
		}
		void clear() {
			if (_disabled)
				return;
			_delegateMap.clear();
		}
		boolean containsKey(String aKey) {
			if (_disabled)
				return false;
			return _delegateMap.containsKey(aKey);
		}
		Object get(String aKey) {
			if (_disabled)
				return null;
			return _delegateMap.get(aKey);
		}
		void remove(String aKey) {
			if (_disabled)
				return;
			_delegateMap.remove(aKey);
		}
		void put(String aKey, Object anObject) {
			if (_disabled)
				return;
			_delegateMap.put(aKey, anObject);
		}
	}
}