package com.service.rmi.control;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.bean.db.ID;
import com.manager.GroupParams;
import com.manager.MultiServerManager;
import com.manager.QueryParams;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.LogService;

public class LogControlImpl implements LogService {

	/** 聊天管理 */
	private MultiServerManager logManager = RmiProxy.getServer(LogService.class);
	
	/** 依据数据选择对应的服务 */
	private LogService chooseService(Object bean) {
		return (LogService) logManager.getRemoteService(bean);
	}
	
	@Override
	public void add(ID obj) throws RemoteException {
		chooseService(obj.getClass().getName()).add(obj);
	}

	@Override
	public void add(Class<?> clazz, Map<String, Object> map) throws RemoteException {
		chooseService(clazz.getName()).add(clazz, map);
	}

	@Override
	public void delete(ID bean) throws RemoteException {
		chooseService(bean.getClass().getName()).delete(bean);
	}

	@Override
	public void delete(Class<?> clazz, Map<String, Object> map) throws RemoteException {
		chooseService(clazz.getName()).delete(clazz, map);
	}

	@Override
	public void deleteAll(Class<?> clazz) throws RemoteException {
		chooseService(clazz.getName()).deleteAll(clazz);
	}

	@Override
	public void deleteForId(ID bean) throws RemoteException {
		chooseService(bean.getClass().getName()).delete(bean);
	}

	@Override
	public <T extends ID> List<T> find(T bean) throws RemoteException {
		return chooseService(bean.getClass().getName()).find(bean);
	}

	@Override
	public <T extends ID> List<T> find(QueryParams queryMap) throws RemoteException {
		return chooseService(queryMap.getClazz().getName()).find(queryMap);
	}

	@Override
	public <T extends ID> List<T> find(QueryParams queryMap, QueryParams writeMap) throws RemoteException {
		return chooseService(queryMap.getClazz().getName()).find(queryMap, writeMap);
	}

	@Override
	public <T> T findOne(T obj) throws RemoteException {
		return chooseService(obj.getClass().getName()).findOne(obj);
	}

	@Override
	public <T> T findOne(QueryParams query, QueryParams write) throws RemoteException {
		return chooseService(query.getClazz().getName()).findOne(query, write);
	}

	@Override
	public <T extends ID> List<T> findOneForId(T bean) throws RemoteException {
		return chooseService(bean.getClass().getName()).findOneForId(bean);
	}

	@Override
	public void modify(QueryParams query, ID bean) throws RemoteException {
		chooseService(query.getClazz().getName()).modify(query, bean);
	}

	@Override
	public void modify(QueryParams query, Map<String, Object> updateMap) throws RemoteException {
		chooseService(query.getClazz().getName()).modify(query, updateMap);
	}

	@Override
	public void modifyForId(ID bean) throws RemoteException {
		chooseService(bean.getClass().getName()).modifyForId(bean);
	}

	@Override
	public void heartbeat() throws RemoteException {
		chooseService(null).heartbeat();
	}

	@Override
	public void delete(QueryParams qp) throws RemoteException {
		chooseService(qp.getClazz()).delete(qp);
	}
	@Override
	public List<Map<String, Object>> findGroup(QueryParams queryParams, GroupParams groupParams) throws RemoteException {
		return chooseService(queryParams.getClazz()).findGroup(queryParams, groupParams);
	}

	@Override
	public Map<String, Object> findGroupOne(QueryParams queryParams,
			GroupParams groupParams) throws RemoteException {
		return chooseService(queryParams.getClazz()).findGroupOne(queryParams, groupParams);
	}
}
