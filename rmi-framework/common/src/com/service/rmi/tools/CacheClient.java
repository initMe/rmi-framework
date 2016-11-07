package com.service.rmi.tools;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.bean.db.Dictionary;
import com.service.rmi.control.CacheControlImpl;

/** 缓存服务接口 */
public interface CacheClient extends RemoteBaseService {
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public CacheClient controlCacheClient = new CacheControlImpl();

	/** 缓存一个键值对 */
	public void set(String key,Object value) throws RemoteException ;
	/** 依据键获取对应的值 */
	public Object get(String key) throws RemoteException ;
	/** 依据键删除对应缓存 */
	public void remove(String key) throws RemoteException ;
	/** 关闭对应的缓存 */
	public abstract void close() throws RemoteException ;
	/** 异常处理对应的缓存 */
	public abstract void bad() throws RemoteException ;
	/** 清空缓存(会清空所有数据) */
	public void cleanAllCache() throws RemoteException ;
	
	/**
	 * 向缓存的Map中添加一个键值
	 * @param key	共有key(通过key指定map)
	 * @param privateKey	私有key(通过key来对应值)(Map中的key)
	 * @param value	私有key对应的值(与私有key对应)(Map中的value)
	 */
	public void setMapValue(String key, String privateKey, Object value) throws RemoteException ;
	
	/**
	 * 校验该值是否在缓存中已经存在
	 * @param key	共有key(通过key指定map)
	 * @param privateKey	私有key(通过key来对应值)(Map中的key)
	 * @param value	是否被包含的值
	 * @return	该值对应的私有key
	 */
	public String containsMapValue(String key, Object value) throws RemoteException ;
	
	/**
	 * 移除Map缓存的中一个键值
	 * @param key	共有key(通过key指定map)
	 * @param privateKey	私有key(通过key来对应值)(Map中的key)
	 */
	public void removeMapValue(String key, String privateKey) throws RemoteException ;
	
	/**
	 * 获取缓的Map中的一个值
	 * @param key	共有key
	 * @param privateKey	私有key(Map中的key)
	 * @return	与私有key对应的值(Map中key对应的值)
	 */
	public <T extends Object> T getMapValue(String key, String privateKey) throws RemoteException ;
	
	/**
	 * 获取缓存的Map中的所有key
	 * @param key	共有key
	 * @return	所有私有key(即缓存的这个Map内的所有key)
	 */
	public List<String> getMapAllKey(String key) throws RemoteException ;
	
	/**
	 * 获取缓存的Map中的所有值
	 * @param key	共有key
	 * @return	Map中所有的值(即缓存的这个Map内的所有value)
	 */
	public List<?> getMapAllValue(String key) throws RemoteException ;
	

	/**
	 * 获取缓存的Map
	 * @param key	共有key
	 * @return	缓存中的Map
	 */
	public Map<String, ?> getMap(String key) throws RemoteException ;
	
	/**
	 * 刷新字典缓存
	 * @param dictList	字典集合
	 */
	public void refreshDictionary(List<Dictionary> dictList) throws RemoteException ;
	
	/**
	 * 获取字典
	 * (获取对应父节点下的子节点)
	 * @param type	字典类型
	 * @param parentCode	字典类型的父code
	 * @return	对应的字典
	 */
	public List<Dictionary> getDictionary(String type, String parentCode) throws RemoteException;
	
	/**
	 * 获取单个字典
	 * (获取对应父节点下的字节点，并且子节点的code是传入值s)
	 * @param type	字典类型
	 * @param parentCode	字典类型的父code
	 * @param code	字典类型的code
	 * @return	对应的字典
	 */
	public Dictionary getDictionaryLayer(String type, String parentCode, String code) throws RemoteException;
	
	/**
	 * 获取单个字典
	 * (获取字典中code值是传入值的，parentCode值为0，所对应的字典s)
	 * @param type	字典类型
	 * @param code	字典类型的code
	 * @return	对应的字典
	 */
	public Dictionary getDictionaryLayer(String type, String code) throws RemoteException;
	
	/**
	 * 获取字典
	 * (获取字典中parentCode值为0的字典)
	 * @param type	字典类型
	 * @param code	字典类型的code
	 * @return	对应的字典
	 */
	public List<Dictionary> getDictionary(String type) throws RemoteException;
	
	/**
	 * 获取字典
	 * (获取该字典类型下的所有字典项，包含所有父节点和子节点)
	 * @param type	字典类型
	 * @return	该类型下的所有字典
	 */
	public List<Dictionary> getAllDictionary(String type) throws RemoteException;
	
	/**
	 * 获取字典
	 * (获取全部类型的字典，返回Map的key代表字典类型值)
	 * @return	获取所有字典
	 */
	public Map<String, List<Dictionary>> getAllDictionary() throws RemoteException;
	
	/**
	 * 依据类型获取缓存中的类型对应的当前序列<br/>
	 * (不推荐使用，性能影响高)
	 * @param indexType	要获取的序列的类型({@link CacheIndexType})
	 * @return	 该类型的序列值
	 */
	public Long getIndex(int indexType) throws RemoteException;
	
	/**
	 * 依据类型获取增长后的序列值<br/>
	 * (不推荐使用，性能影响高)
	 * @param indexType	要获取的序列的类型({@link CacheIndexType})
	 * @return	 该类型的序列值
	 */
	public Long getIndexAndAdd(int indexType) throws RemoteException;
	
	/**
	 * 添加数据到队列
	 * @Title: add
	 * @param name 队列名称
	 * @param value 数据
	 * @return: void
	 */
	public void offer(String name,String... value) throws RemoteException;
	
	/**
	 * 从队列中获取数据，如果没有数据则阻塞
	 * @Title: take
	 * @param name 队列名称
	 * @return: String
	 */
	public List<String> take(String... name) throws RemoteException;
}
