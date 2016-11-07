package com.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

import com.bean.service.RemoteBean;
import com.manager.RmiRemoteExecutor;
import com.search.SearchManager;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

/** 搜索器启动 */
public class SearchRmiMain {
	
	/** 设置为静态变量，防止jvm垃圾回收掉 */
	private static SearchManager sm = null;
	
	public SearchRmiMain() throws Exception {
		ConfigUtil cu = ConfigUtil.getInstance();
		try {
			RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(cu.getSearchServiceSocketPort()));
			Registry r = LocateRegistry.createRegistry(cu.getSearchServicePort());
			sm = new SearchManager(cu.getSearchServicePort());
			r.rebind(cu.getSearchServiceName(), sm);
			// 向控制器注册服务
			Controllor controllor = RmiProxy.getControlService();
			RemoteBean serviceBean = new RemoteBean();
			serviceBean.setServiceIp(cu.getSearchServiceIp());
			serviceBean.setServicePort(cu.getSearchServicePort());
			serviceBean.setServiceName(cu.getSearchServiceName());
			serviceBean.setServiceType(RemoteServiceType.searcher);
			controllor.registerService(serviceBean);
			
			RmiProxy.setThisRemoteBean(serviceBean);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "向控制器注册服务失败");
			throw e;
		}
		
		System.out.println("搜索器启动成功!");
	}
	
	public static void main(String[] args) {
		try {
			new SearchRmiMain();
		} catch (Exception e) {
			LoggerUtil.error(SearchRmiMain.class,e);
//			e.printStackTrace();
		}
	}
}
