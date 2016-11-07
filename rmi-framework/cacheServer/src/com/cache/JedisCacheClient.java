package com.cache;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.context.CacheContext;
import com.exception.Error;
import com.exception.SystemException;
import com.service.rmi.tools.CacheClient;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;
import com.bean.db.Dictionary;

import redis.clients.jedis.Jedis;

public class JedisCacheClient implements CacheClient {
	private Jedis jedis;

	JedisCacheClient(Jedis jedis) {
		this.jedis = jedis;
	}
	@Override
	public void set(String key, Object value) {
		try {
			jedis.set(key.getBytes(), ObjectUtil.parseObjForByte(value));
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "异常在bad()中处理");
			bad();
		}
	}
	@Override
	public Object get(String key) {
		try {
			byte[] bt = jedis.get(key.getBytes());
			if (bt == null) {
				return null;
			}
			return ObjectUtil.parseByteForObj(bt);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "异常在bad()中处理");
			bad();
			return null;
		}
	}
	@Override
	public void remove(String key) {
		jedis.del(key);
	}
	public void bad() {
		if (jedis == null) {
			return;
		}
		CacheClientFactory.getJedisFacede().badConnect(jedis);
		jedis = null;
	}
	@Override
	public void close() {
		if (jedis == null) {
			return;
		}
		CacheClientFactory.getJedisFacede().close(jedis);
		jedis = null;
	}
	@Override
	public void cleanAllCache() {
		jedis.flushAll();
		jedis.flushDB();
	}
	@Override
	public <T extends Object> T getMapValue(String key, String privateKey) {
		List<byte[]> objList = jedis.hmget(key.getBytes(), privateKey.getBytes());
		if (objList != null && objList.size() > 0) {
			for (byte[] bts : objList) {
				if (bts == null) {
					try {
						removeMapValue(key, privateKey);
					} catch (RemoteException e) {
						LoggerUtil.error(this.getClass(), e);
						e.printStackTrace();
					}
				} else {
					return ObjectUtil.parseByteForObj(bts);
				}
			}
		}
		return null;
	}
	@Override
	public String containsMapValue(String key, Object value) throws RemoteException {
		Map<String, ?> map = getMap(key);
		for (String mapKey : map.keySet()) {
			Object mapValue = map.get(mapKey);
			if (mapValue.equals(value) || mapValue.toString().equals(value.toString())
					|| JsonUtil.objToJson(mapValue).equals(JsonUtil.objToJson(value))) {
				return mapKey;
			}
		}
		return null;
	}
	@Override
	public List<String> getMapAllKey(String key) {
		Set<byte[]> keys = jedis.hkeys(key.getBytes());
		if (keys == null || keys.size() == 0) {
			return null;
		}
		List<String> privatekeyList = new ArrayList<String>();
		for (byte[] privatekey : keys) {
			try {
				privatekeyList.add(new String(privatekey, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LoggerUtil.error(this.getClass(), e);
			}
		}
		return privatekeyList;
	}
	@Override
	public List<?> getMapAllValue(String key) {
		List<byte[]> vals = jedis.hvals(key.getBytes());
		if (vals == null || vals.size() == 0) {
			return null;
		}
		List<Object> valList = new ArrayList<Object>();
		for (byte[] val : vals) {
			valList.add(ObjectUtil.parseByteForObj(val));
		}
		return valList;
	}
	@Override
	public void setMapValue(String key, String privateKey, Object value) {
		jedis.hset(key.getBytes(), privateKey.getBytes(), ObjectUtil.parseObjForByte(value));
	}
	@Override
	public void removeMapValue(String key, String privateKey) throws RemoteException {
		jedis.hdel(key.getBytes(), privateKey.getBytes());
	}
	@Override
	public Map<String, ?> getMap(String key) {
		Map<byte[], byte[]> tempMap = jedis.hgetAll(key.getBytes());
		Map<String, Object> map = new HashMap<String, Object>();
		if (tempMap != null) {
			Set<byte[]> keys = tempMap.keySet();
			for (byte[] k : keys) {
				try {
					map.put(new String(k, "UTF-8"), ObjectUtil.parseByteForObj(tempMap.get(k)));
				} catch (UnsupportedEncodingException e) {
					map.put(new String(k), ObjectUtil.parseByteForObj(tempMap.get(k)));
					LoggerUtil.error(this.getClass(), e);
				}
			}
		}
		return map;
	}
	@Override
	public void heartbeat() throws RemoteException {
	}
	@Override
	public Dictionary getDictionaryLayer(String type, String code) throws RemoteException {
		List<Dictionary> dictList = getMapValue(CacheContext.cache_dictionary, type);
		if (dictList == null) {
			return null;
		}
		for (Dictionary dict : dictList) {
			if (dict.getCode().equalsIgnoreCase(code)) {
				return dict;
			}
		}
		return null;
	}
	@Override
	public List<Dictionary> getDictionary(String type, String parentCode) throws RemoteException {
		List<Dictionary> dictList = getMapValue(CacheContext.cache_dictionary, type);
		if (dictList == null) {
			return null;
		}
		List<Dictionary> parentDictList = new ArrayList<Dictionary>();
		for (Dictionary dict : dictList) {
			if (dict.getParentCode().equals(parentCode)) {
				parentDictList.add(dict);
			}
		}
		return parentDictList;
	}
	@Override
	public Dictionary getDictionaryLayer(String type, String parentCode, String code) throws RemoteException {
		List<Dictionary> dictList = getMapValue(CacheContext.cache_dictionary, type);
		if (dictList == null) {
			return null;
		}
		for (Dictionary dict : dictList) {
			if (dict.getParentCode().equals(parentCode) && dict.getCode().equals(code)) {
				return dict;
			}
		}
		return null;
	}
	@Override
	public List<Dictionary> getDictionary(String type) throws RemoteException {
		return getDictionary(type, "0");
	}
	@Override
	public List<Dictionary> getAllDictionary(String type) throws RemoteException {
		return getMapValue(CacheContext.cache_dictionary, type);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<Dictionary>> getAllDictionary() throws RemoteException {
		return (Map<String, List<Dictionary>>) getMap(CacheContext.cache_dictionary);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void refreshDictionary(List<Dictionary> dictList) throws RemoteException {
		if (dictList == null || dictList.size() == 0) {
			return;
		}
		LoggerUtil.info(this.getClass(), "开始加载字典...");
		Map<String, List<Dictionary>> tempMap = (Map<String, List<Dictionary>>) getMap(CacheContext.cache_dictionary);
		if (tempMap == null) {
			tempMap = new HashMap<String, List<Dictionary>>();
		}
		// 如果缓存中已有字典数据，则执行替换操作
		boolean oldIsNull = tempMap.size() == 0;
		for (Dictionary dict : dictList) {
			// 获取到缓存中的对应类型字典，进行替换
			List<Dictionary> typeDict = tempMap.get(dict.getType());
			// 没有则新增
			if (typeDict == null) {
				typeDict = new ArrayList<Dictionary>();
				tempMap.put(dict.getType(), typeDict);
			}
			// 有则移除
			else if (!oldIsNull) {
				for (int i = typeDict.size() - 1; i >= 0; i--) {
					Dictionary oldDict = typeDict.get(i);
					if (oldDict.equalsDictionary(dict)) {
						typeDict.remove(oldDict);
					}
				}
			}
			// 加入缓存
			typeDict.add(dict);
		}
		// 重置字典对象，补充是否有子节点
		for (String key : tempMap.keySet()) {
			List<Dictionary> typeDictList = tempMap.get(key);
			// 遍历子节点，进行是否有子节点补充
			for (Dictionary dict : typeDictList) {
				dict.setHasChildren(0);
				// 查找该节点是否有子节点
				for (Dictionary sonDict : typeDictList) {
					// 校验子节点的父节点是否是自己
					if (sonDict.getId() != dict.getId() && sonDict.getParentCode().equals("0")
							&& sonDict.getParentCode().equals(dict.getCode())) {
						dict.setHasChildren(1);
						break;
					}
				}
			}
		}
		// 覆盖更新到缓存中
		for (String key : tempMap.keySet()) {
			setMapValue(CacheContext.cache_dictionary, key, tempMap.get(key));
		}
		LoggerUtil.info(this.getClass(), "加载字典完成!");
	}
	@Override
	public Long getIndex(int indexType) throws RemoteException {
		String indexStr = getMapValue(CacheContext.cache_index, String.valueOf(indexType));
		// 如果缓存没有该index，则初始化
		if (indexStr == null) {
			indexStr = "1L";
			setMapValue(CacheContext.cache_index, String.valueOf(indexType), "1");
		}
		return Long.parseLong(indexStr);
	}
	@Override
	public Long getIndexAndAdd(int indexType) throws RemoteException {
		Object lock = getMapValue(CacheContext.cache_index_lock, String.valueOf(indexType));
		int i = 0;
		while (lock != null && i < 3) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lock = getMapValue(CacheContext.cache_index_lock, String.valueOf(indexType));
			if (lock == null) {
				break;
			}
		}
		Long nowIndex = -1L;
		if (lock == null) {
			setMapValue(CacheContext.cache_index_lock, String.valueOf(indexType), "1");
			String indexStr = getMapValue(CacheContext.cache_index, String.valueOf(indexType));
			if (indexStr == null) {
				nowIndex = 0L;
			}
			nowIndex = Long.parseLong(indexStr) + 1;
			setMapValue(CacheContext.cache_index, String.valueOf(indexType), String.valueOf(nowIndex));
			removeMapValue(CacheContext.cache_index_lock, String.valueOf(indexType));
		}
		throw new SystemException(Error.cache_lock);
	}
	@Override
	public void offer(String name, String... str) {
		try {
			jedis.rpush(name, str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CacheClientFactory.getJedisFacede().badConnect(jedis);
		}
	}
	@Override
	public List<String> take(String... name) {
		try {
			List<String> data = jedis.brpop(0, name);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CacheClientFactory.getJedisFacede().badConnect(jedis);
		}
		return null;
	}
}
