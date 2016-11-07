/**   
 * Copyright (c) 版权所有 2010-2015
 * 产品名：   
 * 包名：com.manager   
 * 文件名：MongoManager.java   
 * 版本信息：   
 * 创建日期：2015-4-27-上午10:52:23
 *    
 */
package com.manager;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bean.db.ID;
import com.exception.Error;
import com.exception.SystemException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.service.rmi.tools.LogService;
import com.util.MongoUtils;
import com.utils.ObjectUtil;

public class MongoManager extends UnicastRemoteObject implements LogService {
	private static final long serialVersionUID = 3720807871250072982L;

	public MongoManager() throws RemoteException {
		super();
		new MongoServer();
	}

	public MongoManager(int port) throws RemoteException {
		super(port);
		new MongoServer();
	}

	public void heartbeat() throws RemoteException { }

	public void add(ID obj) throws RemoteException {
		try {
			MongoServer.add(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
	}

	public void add(Class<?> clazz, Map<String, Object> map)
			throws RemoteException {
		try {
			MongoServer.add(clazz, map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
	}

	public void delete(Class<?> clazz, Map<String, Object> map)
			throws RemoteException {
		try {
			MongoServer.delete(clazz, map);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
	}

	public void deleteAll(Class<?> clazz) throws RemoteException {
		try {
			MongoServer.deleteAll(clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
	}

	public void modify(QueryParams query, Map<String, Object> updateMap)
			throws RemoteException {
		try {
			MongoServer.modify(query, updateMap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T findOne(T obj) throws RemoteException {
		DBObject dbObj = null;
		Object retObj = null;
		try {
			dbObj = MongoServer.findOne(obj);
			if (dbObj != null) {
				retObj = MongoUtils.dbObjToBean(dbObj, obj.getClass());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);

		}
		return (T) retObj;
	}

	public <T> T findOne(QueryParams query, QueryParams write)
			throws RemoteException {

		DBObject dbObj = null;
		Object retObj = null;
		try {
			dbObj = MongoServer.findOne(query, write);
			if (dbObj != null) {
				retObj = MongoUtils.dbObjToBean(dbObj, query.getClazz());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);

		}
		return (T) retObj;
	}

	@SuppressWarnings("unchecked")
	public <T extends ID> List<T> find(QueryParams queryMap)
			throws RemoteException {
		DBCursor cursor = null;
		List<T> list = new ArrayList<T>();
		try {
			cursor = MongoServer.find(queryMap);
			while (cursor.hasNext()) {
				BasicDBObject dbObj = new BasicDBObject(cursor.next().toMap());
				Object obj = MongoUtils.dbObjToBean(dbObj, queryMap.getClazz());
				list.add((T) obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends ID> List<T> find(QueryParams queryMap,
			QueryParams writeMap) throws RemoteException {
		DBCursor cursor = null;
		List<T> list = new ArrayList<T>();
		try {
			cursor = MongoServer.find(queryMap, writeMap);
			while (cursor.hasNext()) {
				BasicDBObject dbObj = new BasicDBObject(cursor.next().toMap());
				Object obj = MongoUtils.dbObjToBean(dbObj, queryMap.getClazz());
				list.add((T) obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException(Error.system_error, e);
		}
		return list;
	}

	public void delete(ID bean) throws RemoteException {
		bean.cleanInitValue();
		bean.setPage(null);
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		delete(bean.getClass(), map);
	}

	public void deleteForId(ID bean) throws RemoteException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", bean.getId());
		delete(bean.getClass(), map);
	}
	
	public void delete(QueryParams qp) throws RemoteException {
		delete(qp.getClazz(), qp);
	}

	public <T extends ID> List<T> find(T bean) throws RemoteException {
		bean.cleanInitValue();
		bean.setPage(null);
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		return find(new QueryParams(null, map, bean.getClass()));
	}

	public <T extends ID> List<T> findOneForId(T bean) throws RemoteException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", bean.getId());
		return find(new QueryParams(null, map, bean.getClass()));
	}

	public void modify(QueryParams query, ID bean) throws RemoteException {
		bean.cleanInitValue();
		bean.setPage(null);
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		modify(query, map);
	}

	public void modifyForId(ID bean) throws RemoteException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", bean.getId());
		modify(new QueryParams(null, map, bean.getClass()), map);
	}
	
	public List<Map<String, Object>> findGroup(QueryParams queryParams, GroupParams groupParams) throws RemoteException {
		return MongoServer.findGroup3_2(queryParams, groupParams);
	}
	
	public Map<String, Object> findGroupOne(QueryParams queryParams, GroupParams groupParams) throws RemoteException {
		List<Map<String, Object>> list =  MongoServer.findGroup3_2(queryParams, groupParams);
		return list!=null&&list.size()>0?list.get(0):null;
	}
}
