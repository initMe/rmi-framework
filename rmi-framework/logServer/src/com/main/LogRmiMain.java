package com.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

import com.bean.service.RemoteBean;
import com.manager.MongoManager;
import com.manager.RmiRemoteExecutor;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

/** 搜索器启动 */
public class LogRmiMain {
	
	/** 设置为静态变量，防止jvm垃圾回收掉 */
	private static MongoManager mm = null;
	
	public LogRmiMain() throws Exception {
		ConfigUtil cu = ConfigUtil.getInstance();
		try {
			RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(cu.getLogServiceSocketPort()));
			Registry r = LocateRegistry.createRegistry(cu.getLogServicePort());
			mm = new MongoManager(cu.getLogServicePort());
			r.rebind(cu.getLogServiceName(), mm);
			// 向控制器注册服务
			Controllor controllor = RmiProxy.getControlService();
			RemoteBean serviceBean = new RemoteBean();
			serviceBean.setServiceIp(cu.getLogServiceIp());
			serviceBean.setServicePort(cu.getLogServicePort());
			serviceBean.setServiceName(cu.getLogServiceName());
			serviceBean.setServiceType(RemoteServiceType.log);
			controllor.registerService(serviceBean);
			
			RmiProxy.setThisRemoteBean(serviceBean);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "向控制器注册服务失败");
			throw e;
		}
		
		System.out.println("日志服务启动成功!");
	}
	
	public static void main(String[] args) {
		try {
			new LogRmiMain();
		} catch (Exception e) {
			LoggerUtil.error(LogRmiMain.class,e);
//			e.printStackTrace();
		}
	}
}
