package com.service;

import com.service.rmi.business.UserRmiService;
import com.service.rmi.tools.CacheClient;
import com.service.rmi.tools.ChatService;
import com.service.rmi.tools.Controllor;
import com.service.rmi.tools.FileService;
import com.service.rmi.tools.LogService;
import com.service.rmi.tools.RemoteBaseService;
import com.service.rmi.tools.SMSClient;
import com.service.rmi.tools.Searcher;

public enum RemoteServiceType {
	not_know(0, "未知服务", RemoteBaseService.class, null),
	cache(1,"缓存服务", CacheClient.class, CacheClient.controlCacheClient),
	searcher(2,"搜索服务", Searcher.class, Searcher.controlSearcher),
	sms(3,"短信服务", SMSClient.class, SMSClient.controlSMS),
	chat(4,"聊天服务", ChatService.class, ChatService.controlChatService),
	control(5,"控制中心", Controllor.class, null),
	log(6,"日志服务", LogService.class, LogService.controlChatService),
	file(7, "文件服务", FileService.class, FileService.controlFileService),
	user(8, "用户服务", UserRmiService.class, null);
	
	/** 类型 */
	private Integer type;
	/** 备注 名称 */
	private String remark;
	/** 接口类 */
	private Class<? extends RemoteBaseService> serviceClass;
	/** 集群选择控制器 */
	private RemoteBaseService serviceControl;
	
	private RemoteServiceType(int type, String remark, Class<? extends RemoteBaseService> serviceClass, RemoteBaseService serviceControl) {
		this.remark = remark;
		this.type = type;
		this.serviceClass = serviceClass;
		this.serviceControl = serviceControl;
	}

	/**
	 * 依据类型值获取对应的服务枚举类型
	 * @param type	类型值
	 * @return	枚举类型
	 */
	public static RemoteServiceType getServiceType(Integer type) {
		for(RemoteServiceType st : RemoteServiceType.values()) {
			if(st.getType().equals(type)) {
				return st;
			}
		}
		return not_know;
	}
	
	/***
	 * 依据具体服务，获取该服务对应的类型
	 * @param rs	具体服务
	 * @return	返回服务对应的类型
	 */
	public static RemoteServiceType getServiceTypeByService(Class<? extends RemoteBaseService> clazz) {
		for(RemoteServiceType st : RemoteServiceType.values()) {
			if(st!=RemoteServiceType.not_know && st.serviceClass.getName().equals(clazz.getName())) {
				return st;
			}
		}
		return not_know;
	}
	
	public Integer getType() {
		return type;
	}

	public String getRemark() {
		return remark;
	}

	/** 获取接口类 */
	public Class<? extends RemoteBaseService> getServiceClass() {
		return serviceClass;
	}

	/** 获取接口集群控制器 */
	@SuppressWarnings("unchecked")
	public <T extends RemoteBaseService> T getServiceControl() {
		return (T) serviceControl;
	}
}
