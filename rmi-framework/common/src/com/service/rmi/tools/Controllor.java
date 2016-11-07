package com.service.rmi.tools;

import java.rmi.RemoteException;
import java.util.Map;

import com.bean.service.RemoteBean;
import com.manager.MultiServerManager;

/** 远程服务控制器 */
public interface Controllor extends RemoteBaseService {
	
	/**
	 * 停止远程服务
	 * @param info	服务信息的详情(包含种类，ip，端口)
	 * @throws RemoteException
	 */
	public void stopService(RemoteBean info) throws RemoteException ;
	
	/**
	 * 上报异常的远程服务
	 * @param info	服务信息的详情(包含种类，ip，端口)
	 * @throws RemoteException
	 */
	public void badService(RemoteBean info) throws RemoteException ;
	
	/**
	 * 注册远程服务(启用新的服务)
	 * @param info	服务信息的详情(包含种类，ip，端口)
	 * @throws RemoteException
	 */
	public void registerService(RemoteBean info) throws RemoteException ;
	
	/**
	 * 依据接口类型获取远程服务信息
	 * @param type	接口类型(RMI接口)
	 * @return	返回type类型的接口信息
	 * @throws RemoteException
	 */
	public MultiServerManager getServiceByType(Class<? extends RemoteBaseService> type) throws RemoteException;
	
	/**
	 * 获取所有服务信息
	 * @return	服务信息
	 * @throws RemoteException
	 */
	public Map<Class<? extends RemoteBaseService>, MultiServerManager> getAllServer() throws RemoteException;
}
