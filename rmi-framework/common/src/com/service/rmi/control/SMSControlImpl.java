package com.service.rmi.control;

import java.rmi.RemoteException;

import com.bean.sms.SMSBaseResponse;
import com.bean.sms.SMSCheckNoResponse;
import com.manager.MultiServerManager;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.SMSClient;

public class SMSControlImpl implements SMSClient {

	/** 聊天管理 */
	private MultiServerManager smsManager = RmiProxy.getServer(SMSClient.class);
	
	/** 依据数据选择对应的服务 */
	private SMSClient chooseService(Object bean) {
		return (SMSClient) smsManager.getRemoteService(bean);
	}
	
	@Override
	public SMSCheckNoResponse sendCheckMsg(String mobile)
			throws RemoteException {
		return chooseService(mobile).sendCheckMsg(mobile);
	}

	@Override
	public SMSBaseResponse sendMsg(String mobile, String context)
			throws RemoteException {
		return chooseService(mobile).sendMsg(mobile, context);
	}

	@Override
	public void heartbeat() throws RemoteException {
		chooseService(null).heartbeat();
	}

	@Override
	public SMSCheckNoResponse sendPayPassMsg(String mobile)
			throws RemoteException {
		return chooseService(mobile).sendPayPassMsg(mobile);
	}

}
