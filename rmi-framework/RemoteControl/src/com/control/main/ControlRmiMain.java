package com.control.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

import com.bean.service.RemoteBean;
import com.control.manager.rmi.RmiControl;
import com.manager.RmiRemoteExecutor;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

public class ControlRmiMain {

	/** 设置为静态变量，防止jvm垃圾回收掉 */
	private static RmiControl rc;
	
	public ControlRmiMain() throws Exception {
		RmiProxy.isControlService = true;
		ConfigUtil cu = ConfigUtil.getInstance();
		RemoteBean crb = new RemoteBean();
		crb.setServiceIp(cu.getControlServiceIp());
		crb.setServiceName(cu.getControlServiceName());
		crb.setServicePort(cu.getControlServicePort());
		crb.setServiceType(RemoteServiceType.control.getType());
		RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(cu.getControlServiceSocketPort()));
		Registry r = LocateRegistry.createRegistry(crb.getServicePort());
		rc = new RmiControl(crb.getServicePort());
		r.rebind(crb.getServiceName(), rc);
		RmiProxy.setThisRemoteBean(crb);
		System.out.println("服务控制器启动成功，端口号："+crb.getServicePort());
	}
	public static void main(String[] args) {
		try {
			new ControlRmiMain();
		} catch (Exception e) {
			LoggerUtil.error(ControlRmiMain.class, e);
			e.printStackTrace();
		}
	}
}
