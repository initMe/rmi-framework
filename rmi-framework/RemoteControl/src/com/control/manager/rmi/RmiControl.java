package com.control.manager.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

import com.bean.service.RemoteBean;
import com.exception.Error;
import com.exception.SystemException;
import com.exception.BaseException;
import com.manager.MultiServerManager;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.service.rmi.tools.RemoteBaseService;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;

/** 所有rmi服务的管理控制器 */
public class RmiControl extends UnicastRemoteObject implements Controllor {

	private static final long serialVersionUID = 6013034435155865638L;
	public RmiControl() throws RemoteException {
		super();
	}
	
	public RmiControl(int port) throws RemoteException {
		super(port);
	}
	
	/***
	 * 依据注册的服务获取对应的存储对象
	 * @param rb	注册的服务
	 * @return	存储对象
	 */
	private MultiServerManager getServiceMap(RemoteBean rb) {
		RemoteServiceType type = RemoteServiceType.getServiceType(rb.getServiceType());
		synchronized (this) {
			MultiServerManager ms = RmiProxy.getServer(type);
			if(ms == null) {
				ms = new MultiServerManager();
				RmiProxy.getServerPool().put(type.getServiceClass(), ms);
			}
			return ms;
		}
	}
	
	/** 处理损坏的服务 */
	public void badService(RemoteBean rs, boolean needCheck) {
		synchronized (this) {
			MultiServerManager serviceMap = getServiceMap(rs);
			if(serviceMap != null) {
				Map<Integer, RemoteBean> rbMap = serviceMap.getServerMap();
				List<Integer> keys = serviceMap.getSortKey();
				// 遍历查找对应的服务
				for(int i=keys.size()-1; i>=0; i--) {
					RemoteBean service = rbMap.get(keys.get(i));
					if(service.equals(rs)) {
						// 再测试一次连接
						if(needCheck) {
							try {
								service.getService().heartbeat();
							} catch (Exception e) {
//								serviceMap.getServerMap().remove(keys.get(i));
								service.setBroken(true);
							}
						} else {
//							serviceMap.getServerMap().remove(keys.get(i));
							service.setBroken(true);
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public void registerService(RemoteBean info) throws RemoteException {
		try {
			getServiceMap(info).addServer(info);
			try {
				LoggerUtil.info(this.getClass(), "有服务注册:" + JsonUtil.objToJson(info));
			} catch (Exception e) {
				LoggerUtil.error(this.getClass(), e);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			if(e instanceof BaseException) {
				LoggerUtil.error(this.getClass(),e);
				throw (BaseException)e;
			}
			LoggerUtil.error(this.getClass(),e);
			throw new SystemException(Error.system_error, e);
		}
	}


	@Override
	public void stopService(RemoteBean info) throws RemoteException {
		LoggerUtil.info(this.getClass(), "有服务停止:" + JsonUtil.objToJson(info));
		badService(info, false);
	}

	@Override
	public void heartbeat() throws RemoteException {}

	@Override
	public void badService(RemoteBean info) throws RemoteException {
		badService(info, true);
		LoggerUtil.info(this.getClass(), "有服务异常退出:["+"rmi://"+info.getServiceIp()+":"+info.getServicePort()+"/"+info.getServiceName()+"(序列号:"+info.getServiceType()+")]");
	}

	@Override
	public Map<Class<? extends RemoteBaseService>, MultiServerManager> getAllServer() throws RemoteException {
		return RmiProxy.getServerPool();
	}

	@Override
	public MultiServerManager getServiceByType(Class<? extends RemoteBaseService> type) throws RemoteException {
		return RmiProxy.getServer(RemoteServiceType.getServiceTypeByService(type));
	}
	
}
