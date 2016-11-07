package com.service.rmi.control;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bean.db.Dictionary;
import com.bean.service.RemoteBean;
import com.manager.MultiServerManager;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.CacheClient;

/** 缓存服务接口 */
public class CacheControlImpl implements CacheClient {

	/** 缓存管理 */
	private MultiServerManager cacheManager = RmiProxy.getServer(CacheClient.class);
	
	/** 依据数据选择对应的服务 */
	private CacheClient chooseService(Object bean) {
		return (CacheClient) cacheManager.getRemoteService(bean);
	}
	
	@Override
	public void bad() throws RemoteException {
		chooseService(null).bad();
	}

	@Override
	public void cleanAllCache() throws RemoteException {
		chooseService(null).cleanAllCache();
	}

	@Override
	public void close() throws RemoteException {
		chooseService(null).close();
	}

	@Override
	public String containsMapValue(String key, Object value) throws RemoteException {
		return chooseService(value).containsMapValue(key, value);
	}

	@Override
	public Object get(String key) throws RemoteException {
		return chooseService(key).get(key);
	}

	@Override
	public List<Dictionary> getAllDictionary(String type)
			throws RemoteException {
		return chooseService(null).getAllDictionary(type);
	}

	@Override
	public Map<String, List<Dictionary>> getAllDictionary()
			throws RemoteException {
		return chooseService(null).getAllDictionary();
	}

	@Override
	public List<Dictionary> getDictionary(String type, String parentCode)
			throws RemoteException {
		return chooseService(null).getDictionary(type, parentCode);
	}

	@Override
	public List<Dictionary> getDictionary(String type) throws RemoteException {
		return chooseService(null).getAllDictionary(type);
	}

	@Override
	public Dictionary getDictionaryLayer(String type, String parentCode,
			String code) throws RemoteException {
		return chooseService(null).getDictionaryLayer(type, parentCode, code);
	}

	@Override
	public Dictionary getDictionaryLayer(String type, String code)
			throws RemoteException {
		return chooseService(null).getDictionaryLayer(type, code);
	}

	@Override
	public Long getIndex(int indexType) throws RemoteException {
		return chooseService(null).getIndex(indexType);
	}

	@Override
	public Long getIndexAndAdd(int indexType) throws RemoteException {
		return chooseService(null).getIndexAndAdd(indexType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> getMap(String key) throws RemoteException {
		Map<Integer, RemoteBean> rbMap = cacheManager.getServerMap();
		Map map = new HashMap();
		for(RemoteBean rb : rbMap.values()) {
			CacheClient cc = (CacheClient) rb.getService();
			map.putAll(cc.getMap(key));
		}
		return map;
	}

	@Override
	public List<String> getMapAllKey(String key) throws RemoteException {
		Map<Integer, RemoteBean> rbMap = cacheManager.getServerMap();
		List<String> list = new ArrayList<String>();
		for(RemoteBean rb : rbMap.values()) {
			CacheClient cc = (CacheClient) rb.getService();
			list.addAll(cc.getMapAllKey(key));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<?> getMapAllValue(String key) throws RemoteException {
		Map<Integer, RemoteBean> rbMap = cacheManager.getServerMap();
		List list = new ArrayList();
		for(RemoteBean rb : rbMap.values()) {
			CacheClient cc = (CacheClient) rb.getService();
			list.addAll(cc.getMapAllValue(key));
		}
		return list;
	}

	@Override
	public <T> T getMapValue(String key, String privateKey)
			throws RemoteException {
		return chooseService(privateKey).getMapValue(key, privateKey);
	}


	@Override
	public void refreshDictionary(List<Dictionary> dictList)
			throws RemoteException {
		chooseService(null).refreshDictionary(dictList);
	}

	@Override
	public void remove(String key) throws RemoteException {
		chooseService(key).remove(key);
	}

	@Override
	public void removeMapValue(String key, String privateKey)
			throws RemoteException {
		chooseService(privateKey).removeMapValue(key, privateKey);
	}

	@Override
	public void set(String key, Object value) throws RemoteException {
		chooseService(key).set(key, value);
	}

	@Override
	public void setMapValue(String key, String privateKey, Object value)
			throws RemoteException {
		chooseService(privateKey).setMapValue(key, privateKey, value);
	}

	@Override
	public void heartbeat() throws RemoteException {
		chooseService(null).heartbeat();
	}

	@Override
	public void offer(String name, String... obj) throws RemoteException{
		chooseService(name).offer(name, obj);
	}

	@Override
	public List<String> take(String... name) throws RemoteException{
		return chooseService(name).take(name);
	}
}
