package com.service.proxy;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.bean.service.RemoteBean;
import com.context.Context;
import com.exception.Error;
import com.exception.SystemException;
import com.manager.MultiServerManager;
import com.manager.ServerStopListener;
import com.service.RemoteServiceType;
import com.service.rmi.tools.Controllor;
import com.service.rmi.tools.RemoteBaseService;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

/** RMI类型的接口的代理类 */
public class RmiProxy {
	
    /** 服务群池[接口class名, 储存的实例] */
    private static Map<Class<? extends RemoteBaseService>, MultiServerManager> serverPool = new HashMap<Class<? extends RemoteBaseService>, MultiServerManager>();
    
	/** 控制器服务 */
	private static Controllor controlService = null;
	
	private static boolean init = false;
	/** 是否产生过心跳 */
	private static boolean haveHearbeat = false;
	/** 等待心跳锁 */
	private static Object hearbeatLock = new Object();
	/** 当前系统是否是控制中心系统 */
	public static boolean isControlService = false;
	
	/** 当前系统bean */
	public static RemoteBean thisRemoteBean;
	// 执行初始化以及心跳
	static {
		if(!init) {
			init();
		}
	}
	
	/** 初始化 */
	private static void init() {
		// 开启服务关闭监听器
		ServerStopListener.getInstance();
		
		// 控制中心不启动心跳
		if(isControlService) {
			return;
		}
		
		if(init) {
			LoggerUtil.info(RmiProxy.class, "rmi代理类已初始化过，不允许重复初始化!");
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				if(isControlService) {
					return;
				}
				controlService = getControlService();
				while(true) {
					try {
						doHeartbeat();
						if(!haveHearbeat) {
							synchronized (hearbeatLock) {
								haveHearbeat = true;
								hearbeatLock.notifyAll();
							}
						}
//						LoggerUtil.info(RmiProxy.class, "执行控制器心跳，进行服务同步");
						
						
						if(!init) {
//							System.out.println("释放锁");
//							initLock.notifyAll();
							init = true;
//							if(initLock.tryLock()) {
//								initLock.unlock();
//							}
						}
						Thread.sleep(Context.remoteService_heartbeat);
					} catch (Exception e) {
						LoggerUtil.error(this.getClass(), e);
						try {
							Thread.sleep(Context.remoteService_heartbeat);
						} catch (InterruptedException e1) {
							LoggerUtil.error(this.getClass(), e);
						}
					}
				}
			}
			/** 执行心跳 */
			private void doHeartbeat() {
				try {
					controlService.heartbeat();
					// 同步服务群
					Map<Class<? extends RemoteBaseService>, MultiServerManager> allServer = controlService.getAllServer();
					for(Class<? extends RemoteBaseService> clazz : serverPool.keySet()) {
						MultiServerManager server = serverPool.get(clazz);
						if(server == null) {
							serverPool.put(clazz, new MultiServerManager());
						}
						server.intoSelf(allServer.get(clazz));
					}
				} catch (RemoteException e) {
					LoggerUtil.error(this.getClass(), e);
				}
			}
		}).start();
		
		LoggerUtil.info(RmiProxy.class, "rmi代理类初始化");
	}
	
	/***
	 * 获取控制器的远程接口的映射
	 * (获取出来后，直接调用Controllor里的方法)
	 * @return	返回映射
	 * @throws Exception	映射的获取出现异常,可能由于地址、端口或接口名发生了变更
	 */
	public static Controllor getControlService() throws SystemException {
		if(controlService == null) {
			ConfigUtil cu = ConfigUtil.getInstance();
			try {
				Controllor control = (Controllor) Naming.lookup(
						"rmi://"+cu.getControlServiceIp()+
						":"+cu.getControlServicePort()+
						"/"+cu.getControlServiceName()
				);
				controlService = control;
			} catch (Exception e) {
				LoggerUtil.error(RmiProxy.class, "控制服务连接失败", e);
				throw new SystemException(Error.system_remote_control_error, e);
			}
		}
		return controlService;
	}
	
	/** 检查并锁定心跳锁 */
	private static void chenckHearbeatLock() {
		if(!haveHearbeat) {
			synchronized (hearbeatLock) {
				if(!haveHearbeat) {
					try {
						hearbeatLock.wait();
					} catch (InterruptedException e) {
						LoggerUtil.error(RmiProxy.class, e);
					}
				}
			}
		}
	}
	
	/**
	 * 依据类型获取对应的远程接口
	 * @param <T>	远程接口RemoteBaseService的子类
	 * @param type	接口类型
	 * @return	可使用的服务
	 */
	@SuppressWarnings("unchecked")
	public static <T extends RemoteBaseService> T getRemoteService(RemoteServiceType type) {
		try {
			chenckHearbeatLock();
			// 如果有集群控制器，则返回集群控制器
			if(type.getServiceControl() != null) {
				return type.getServiceControl();
			}
			// 随机挑选一个作为返回远程接口实例
			MultiServerManager server = serverPool.get(type.getServiceClass());
			if(server.getServerMap().size() > 0) {
				return (T) server.getRemoteServerByRandom().getService();
			}
			throw new SystemException(Error.system_remoteService_notfind_error, "远程"+type.getRemark()+"未获取到");
		} catch (Exception e) {
			throw new SystemException(Error.system_remoteService_connect_error, e);
		}
	}
	
	/** 获取对应的服务集合 */
	public static MultiServerManager getServer(RemoteServiceType type) {
		return serverPool.get(type.getServiceClass());
	}
	
	/** 获取对应的服务集合 */
	public static MultiServerManager getServer(Class<? extends RemoteBaseService> clazz) {
		return serverPool.get(clazz);
	}
	
	/** 获取整个服务池 */
	public static Map<Class<? extends RemoteBaseService>, MultiServerManager> getServerPool() {
		return serverPool;
	}
	
	/** 当前系统是否是控制中心系统 */
	public static boolean isControlService() {
		return isControlService;
	}

	/** 获取当前系统的bean */
	public static RemoteBean getThisRemoteBean() {
		return thisRemoteBean;
	}

	/** 设定当前系统的bean */
	public static void setThisRemoteBean(RemoteBean thisRemoteBean) {
		RmiProxy.thisRemoteBean = thisRemoteBean;
	}
}
