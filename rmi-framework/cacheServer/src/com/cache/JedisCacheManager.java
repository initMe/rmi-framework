package com.cache;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.service.rmi.tools.CacheClient;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;
import com.bean.db.Dictionary;

public class JedisCacheManager extends UnicastRemoteObject implements CacheClient {
	private static final long serialVersionUID = 7974881367043134420L;
	
	
	/** 缓存的前缀标记 */
	private static String cacheFlag = ConfigUtil.getInstance().getCacheFlag();
	
	/** 本地缓存 */
	private static Map<String, Object> localCacheMap = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public JedisCacheManager() throws RemoteException {
		super();
	}
	
	public JedisCacheManager(int port) throws RemoteException {
		super(port);
	}
	
	/** 获取缓存客户端 */
	private CacheClient getCache() {
		return CacheClientFactory.getCacheClient();
	}
	
	@Override
	public void close() {
		// 不应该使用本类的关闭处理,所以给予异常信息返回
		LoggerUtil.error(this.getClass(), "调用到无效的关闭接口!");
		throw new RuntimeException("调用到无效的关闭接口!");
	}
	
	@Override
	public void bad() throws RemoteException {
		// 不应该使用本类的异常处理,所以给予异常信息返回
		LoggerUtil.error(this.getClass(), "调用到无效的异常处理接口!");
		throw new RuntimeException("调用到无效的异常处理接口!");
	}

	@Override
	public Object get(String key) throws RemoteException {
		CacheClient cc = getCache();
		try {
			Object value = localCacheMap.get(cacheFlag + key);
			if(value == null) {
				value = cc.get(cacheFlag + key);
				if(value != null) {
					localCacheMap.put(cacheFlag + key, null);
				}
			}
			return value;
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "get缓存数据时异常(key="+key+")!",e);
			e.printStackTrace();
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public void remove(String key) throws RemoteException {
		CacheClient cc = getCache();
		try {
			localCacheMap.remove(cacheFlag + key);
			cc.remove(cacheFlag + key);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "remove缓存数据时异常(key="+key+")!", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}
	
	@Override
	public void set(String key, Object value) throws RemoteException {
		CacheClient cc = getCache();
		try {
			localCacheMap.put(cacheFlag + key, value);
			cc.set(cacheFlag + key, value);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"set缓存数据时异常(key="+key+", value="+value+")!", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}
	
	@Override
	public void cleanAllCache() throws RemoteException {
		localCacheMap.clear();
		CacheClient cc = getCache();
		cc.cleanAllCache();
	}
	
	public static void main(String[] args) {
		try {
			new JedisCacheManager(ConfigUtil.getInstance().getCacheServicePort()).cleanAllCache();
			System.out.println("缓存已清空");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
//		JedisCacheManager mamager = new JedisCacheManager();
//		mamager.setAutoClose(false);
//		List<String> a = (List<String>)mamager.get(SystemInfo.ALLSERVER);
//		mamager.set(SystemInfo.SERVERCLIENTNUM+"10.132.30.82", "90000");
//		mamager.close();
//		System.out.println(a);
	}

	@Override
	public List<String> getMapAllKey(String key) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getMapAllKey(cacheFlag + key);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理",e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public List<?> getMapAllValue(String key) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getMapAllValue(cacheFlag + key);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T getMapValue(String key, String privateKey) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return (T) cc.getMapValue(cacheFlag+key, privateKey);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public void setMapValue(String key, String privateKey, Object value) throws RemoteException {
		CacheClient cc = getCache();
		try {
			cc.setMapValue(cacheFlag+key, privateKey, value);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}

	@Override
	public void removeMapValue(String key, String privateKey)
			throws RemoteException {
		CacheClient cc = getCache();
		try {
			cc.removeMapValue(cacheFlag+key, privateKey);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}

	@Override
	public String containsMapValue(String key, Object value)
			throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.containsMapValue(cacheFlag+key, value);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}
	
	@Override
	public Map<String, ?> getMap(String key) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getMap(cacheFlag+key);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public void heartbeat() throws RemoteException {
		CacheClient cc = getCache();
		try {
			cc.heartbeat();
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}



	@Override
	public Dictionary getDictionaryLayer(String type, String code)
			throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getDictionaryLayer(type, code);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public List<Dictionary> getAllDictionary(String type) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getAllDictionary(type);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public Map<String, List<Dictionary>> getAllDictionary() throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getAllDictionary();
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public List<Dictionary> getDictionary(String type) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getDictionary(type);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}
	
	@Override
	public List<Dictionary> getDictionary(String type, String parentCode) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getDictionary(type, parentCode);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public Dictionary getDictionaryLayer(String type, String parentCode, String code) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getDictionaryLayer(type, parentCode, code);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public void refreshDictionary(List<Dictionary> dictList) throws RemoteException {
		CacheClient cc = getCache();
		try {
			cc.refreshDictionary(dictList);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}

	@Override
	public Long getIndex(int indexType) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getIndex(indexType);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public Long getIndexAndAdd(int indexType) throws RemoteException {
		CacheClient cc = getCache();
		try {
			return cc.getIndexAndAdd(indexType);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}

	@Override
	public void offer(String name, String... obj) throws RemoteException{
		CacheClient cc = getCache();
		try {
			cc.offer(name, obj);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
		} finally {
			cc.close();
		}
	}

	@Override
	public List<String> take(String...  name) throws RemoteException{
		CacheClient cc = getCache();
		try {
			return cc.take(name);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"异常在bad()中处理", e);
			cc.bad();
			return null;
		} finally {
			cc.close();
		}
	}
}
