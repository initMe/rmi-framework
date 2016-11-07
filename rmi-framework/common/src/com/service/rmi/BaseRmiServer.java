package com.service.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import com.bean.service.RemoteBean;
import com.exception.Error;
import com.exception.SystemException;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.service.rmi.tools.RemoteBaseService;
import com.utils.LoggerUtil;

/** RMI基础类(所有需要发布的RMI服务类继承于该类) */
public abstract class BaseRmiServer extends UnicastRemoteObject implements RemoteBaseService {
	private static final long serialVersionUID = 1L;
	/** 端口号 */
	private int port;
	/** 是否已启动过 */
	private boolean isRun = false;
	/** 该服务相关参数 */
	private RemoteBean bean;
	/** RMI服务的外部访问url */
	private String rmiUrl;
	public BaseRmiServer(int port) throws RemoteException {
		super(port);
		this.port = port;
	}
	
	public BaseRmiServer(String rmiUrl) throws RemoteException {
		super(new RemoteBean(rmiUrl).getServicePort());
		bean = new RemoteBean(rmiUrl);
	}
	
	public BaseRmiServer(RemoteBean bean) throws RemoteException {
		super(bean.getServicePort());
		this.bean = bean;
	}

	/** 获取开启的socket通讯端口号 */
	public int getPort() {
		return port;
	}
	
	/**
	 * 获取当前服务所对应的type
	 * (依据反射)
	 */
	@SuppressWarnings("unchecked")
	private RemoteServiceType getType() {
		Class<?> clazzs[] = this.getClass().getInterfaces();
		for(Class<?> clazz : clazzs) {
			try {
				RemoteServiceType type = RemoteServiceType.getServiceTypeByService((Class<? extends RemoteBaseService>) clazz);
				if(type != RemoteServiceType.not_know) {
					return type;
				}
			} catch (Exception e) {}
		}
		return RemoteServiceType.not_know;
	}
	
	/** 启动RMI服务 */
	public void start() throws RemoteException {
		// 注入RMI启动参数
		if(bean==null && rmiUrl!=null) {
			try {
				bean = new RemoteBean(rmiUrl);
			} catch (Exception e) {
				throw new SystemException(Error.system_error, e, "不符合RMI路径规范 ："+rmiUrl);
			}
		}
		// 无参数校验
		if(bean == null) {
//			LoggerUtil.error(this.getClass(), "未指定RMI相关参数，无法启动RMI服务："+this.getClass().getSimpleName()+"启动失败");
//			return;
			bean = new RemoteBean();
			bean.setServiceIp("127.0.0.1");
			bean.setServiceName(this.getClass().getInterfaces()[0].getSimpleName());
			bean.setServicePort(port);
		}
		// 是否已经启动
		try {
			if(isRun && bean.getService()!=null) {
				return;
			}
		} catch (SystemException e) {}
		// 补充type字段
		bean.setServiceType(getType().getType());
		// 启动
//		RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(bean.getControlServiceSocketPort()));
		Registry r = LocateRegistry.createRegistry(bean.getServicePort());
		r.rebind(bean.getServiceName(), this);
		LoggerUtil.info(this.getClass(), "RMI服务启动："+bean.toString()+" --- 启动成功!");
		// 向控制器注册服务
		Controllor controllor = RmiProxy.getControlService();
		controllor.registerService(bean);
		LoggerUtil.info(this.getClass(), "向控制器注册："+bean.toString()+" --- 注册成功!");
		isRun = true;
	}
	@Override
	public void heartbeat() throws RemoteException {
		
	}
}
