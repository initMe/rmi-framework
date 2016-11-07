package com.main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

import com.bean.service.RemoteBean;
import com.cache.JedisCacheManager;
import com.manager.RmiRemoteExecutor;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

/** 对外接口 */
public class CacheRmiMain {
	
	/** 设置为静态变量，防止jvm垃圾回收掉 */
	private static JedisCacheManager jcm = null;
	
	public CacheRmiMain() throws RemoteException, Exception {
		ConfigUtil cu = ConfigUtil.getInstance();
		try {
			RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(cu.getCacheServiceSocketPort()));
			Registry r = LocateRegistry.createRegistry(cu.getCacheServicePort());
			jcm = new JedisCacheManager(cu.getCacheServicePort());
			r.rebind(cu.getCacheServiceName(), jcm);
			// 向控制器注册服务
			Controllor controllor = RmiProxy.getControlService();
			RemoteBean crs = new RemoteBean();
			crs.setServiceIp(cu.getCacheServiceIp());
			crs.setServicePort(cu.getCacheServicePort());
			crs.setServiceName(cu.getCacheServiceName());
			crs.setServiceType(RemoteServiceType.cache);
			controllor.registerService(crs);
			RmiProxy.setThisRemoteBean(crs);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "向控制器注册服务失败", e);
			throw e;
		}
		LoggerUtil.info(this.getClass(),"缓存服务启动成功!"); 
	}
	public static void main(String[] args) {
		try {
			new CacheRmiMain();
		} catch (Exception e) {
			LoggerUtil.error(CacheRmiMain.class, "启动失败", e);
		}
	}
}
