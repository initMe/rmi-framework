package com.bean.service;

import java.rmi.Naming;
import java.rmi.RemoteException;

import com.bean.BaseBean;
import com.exception.Error;
import com.exception.SystemException;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.RemoteBaseService;
import com.utils.LoggerUtil;

/** 远程服务注册的信息 */
public class RemoteBean extends BaseBean {
	private static final long serialVersionUID = -8285315857885350982L;
	/** 服务名 */
	protected String serviceName;
	/** 服务链接IP */
	protected String serviceIp;
	/** 服务端口 */
	protected int servicePort;
	/**
	 * 该服务的类型
	 * @see RemoteServiceType
	 */
	protected Integer serviceType;
	/** 接口实例(不进行序列化) */
	protected transient RemoteBaseService service;
	
	/** 是否是坏的 */
	protected boolean isBroken = false;
	
	public RemoteBean() {}
	
	public RemoteBean(String rmiUrl) {
		intoByUrl(rmiUrl);
	}
	
	public void intoByUrl(String rmiUrl) {
		rmiUrl = rmiUrl.substring(rmiUrl.indexOf("rmi://")+6, rmiUrl.length());
		serviceIp = rmiUrl.substring(0, rmiUrl.indexOf(":"));
		String portStr = rmiUrl.substring(rmiUrl.indexOf(":")+1, rmiUrl.indexOf("/"));
		servicePort = Integer.parseInt(portStr);
		serviceName = rmiUrl.substring(rmiUrl.indexOf("/")+1, rmiUrl.length());
	}
	
	/** 检验两个服务是否相同 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RemoteBean) {
			RemoteBean rs = (RemoteBean) obj;
			if(!super.equals(rs)) {
				String objFlag = getFlag(rs);
				String thisFlag = getFlag();
				return thisFlag.equals(objFlag);
			}
			return true;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "rmi://"+serviceIp+":"+servicePort+"/"+serviceName;
	}
	
	/** 获取唯一标识 */
	private String getFlag() {
		return getFlag(this);
	}
	
	/** 获取唯一标识 */
	private String getFlag(RemoteBean obj) {
		StringBuffer sb = new StringBuffer();
		sb.append(obj.getServiceName());
		sb.append(":");
		sb.append(obj.getServiceIp());
		sb.append(":");
		sb.append(obj.getServicePort());
		sb.append(":");
		sb.append(obj.getServiceType());
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return getFlag().hashCode();
	}
	
	/** 获取服务，如果为未创建服务，则重新创建，如果服务已创建，则检测 */
	public RemoteBaseService getService() {
		boolean oldBroken = isBroken;
		// 先检查之前的服务是否是通的
		if(service != null) {
			try {
				service.heartbeat();
			} catch (RemoteException e) {
				isBroken = true;
			}
		} else {
			isBroken = true;
		}
		// 判断最终是否需要重连
		if(isBroken) {
			try {
				LoggerUtil.info(this.getClass(), "建立远程RMI服务连接: ["+toString()+"]");
				service = (RemoteBaseService) Naming.lookup(toString());
				service.heartbeat();
				isBroken = false;
			} catch (Exception e) {
				LoggerUtil.error(this.getClass(), "远程RMI服务连接失败 ["+toString()+"]");
				// 如果原本就是坏的，那么则不再进行上报
				if(!oldBroken) {
					SystemException se = new SystemException(Error.system_remoteService_connect_error, e, "["+toString()+"]");
					isBroken = true;
					service = null;
					// 非控制中心的连接异常，才上报异常
					if(!RmiProxy.isControlService) {
						try {
							RmiProxy.getControlService().badService(this);
						} catch (Exception e1) {
							LoggerUtil.error(this.getClass(), "控制中心连接失败");
							throw new SystemException(Error.system_remoteService_connect_error, e, "["+toString()+"]");
						}
					}
					throw se;
				}
			}
		}
		return service;
	}
	
	public void setService(RemoteBaseService service) {
		this.service = service;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceIp() {
		return serviceIp;
	}
	public void setServiceIp(String serviceIp) {
		this.serviceIp = serviceIp;
	}
	public int getServicePort() {
		return servicePort;
	}
	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public boolean isBroken() {
		return isBroken;
	}
	
	public Integer getServiceType() {
		return serviceType;
	}

	public void setServiceType(Integer serviceType) {
		this.serviceType = serviceType;
	}
	
	public void setServiceType(RemoteServiceType serviceType) {
		this.serviceType = serviceType.getType();
	}

	public void setBroken(boolean isBroken) {
		this.isBroken = isBroken;
	}
	
}
