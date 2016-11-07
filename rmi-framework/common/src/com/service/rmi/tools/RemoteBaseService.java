package com.service.rmi.tools;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** rmi远程服务基础接口 */
public interface RemoteBaseService extends Remote {
	
	/** 心跳接口 */
	public void heartbeat() throws RemoteException;
}
