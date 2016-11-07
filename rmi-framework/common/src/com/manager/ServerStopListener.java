package com.manager;

import com.bean.service.RemoteBean;
import com.bean.sms.SMSBaseResponse;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.SMSClient;
import com.utils.ConfigUtil;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.SocketUtil;

/** 服务器关闭监听器 */
public class ServerStopListener {
	private static ServerStopListener ssl;
	private static Object lock = new Object();
	private ServerStopListener() {
		doListen();
	}

	/** 获取服务器关闭监听器的实例 */
	public static ServerStopListener getInstance() {
		if(ssl == null) {
			synchronized (lock) {
				if(ssl == null) {
					ssl = new ServerStopListener();
				}
			}
		}
		return ssl;
	}
	
	/** 开启监听 */
	private void doListen() {
		ConfigUtil cu = ConfigUtil.getInstance();
		String controlIp = cu.getControlServiceIp().trim();
		// 如果是测试服务，则取消监听
		if(!controlIp.equals("202.91.241.85") || !controlIp.equals("10.10.10.5")) {
			return;
		}
		
		// 添加系统关闭的钩子
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				notifyServerStop(RmiProxy.getThisRemoteBean()!=null?RmiProxy.getThisRemoteBean():LoggerUtil.getProjectName());
			}
		}));

		// 只在控制中心上添加服务器(非子系统)监控
		if(RmiProxy.isControlService) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 每次检查间隔为10分钟
					long checkTime = 1000*60*10;
					while(true) {
						// 休眠，检查间隔
						try {
							Thread.sleep(checkTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						listenServer();
					}
				}
			}).start();
		}
	}
	
	/** 监控各个服务(非子系统) */
	private void listenServer() {
		// 监控数据库
		if (SocketUtil.checkSocket("202.91.241.87", 3306)) {
			notifyServerStop("正式数据库");
		}
		// 监控Mongodb
		if (SocketUtil.checkSocket("202.91.241.87", 27017)) {
			notifyServerStop("正式Mongodb");
		}
		// 监控redis
		if (SocketUtil.checkSocket("202.91.241.85", 6321)) {
			notifyServerStop("正式redis");
		}
		// 监控tomcat-redis
		if (SocketUtil.checkSocket("202.91.241.83", 6379)) {
			notifyServerStop("正式tomcat-redis");
		}
	}
	
	/**
	 * 服务停止的通知
	 * (向管理员预警)
	 * @param serverName	服务信息
	 */
	public static void notifyServerStop(Object serverInfo) {
		String serverName = serverInfo.toString();
		try {
			RemoteBean bean = null;
			if(serverInfo instanceof RemoteBean) {
				bean = (RemoteBean) serverInfo;
				serverName = bean.getServiceName();
			}
			SMSClient sms = RmiProxy.getRemoteService(RemoteServiceType.sms);
			SMSBaseResponse smsResponse = sms.sendMsg("15110009282", "警报! ["+serverName+"] 服务已关闭，" +
					"该系统的控制中心ip为："+ConfigUtil.getInstance().getControlServiceIp() + 
					bean!=null?"，该系统相关参数："+JsonUtil.objToJson(bean):"");
			if(!smsResponse.getCode().trim().equals("1")) {
				LoggerUtil.error(ServerStopListener.class, "系统["+serverName+"]停止，向管理员短信通知时发生失败");
			}
		} catch (Exception e) {
			LoggerUtil.error(ServerStopListener.class, "系统["+serverName+"]停止，向管理员短信通知时发生错误", e);
		}
	}
}
