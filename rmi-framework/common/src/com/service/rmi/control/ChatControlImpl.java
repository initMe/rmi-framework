package com.service.rmi.control;

import java.rmi.RemoteException;

import com.bean.chat.MsgBean;
import com.manager.MultiServerManager;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.ChatService;

public class ChatControlImpl implements ChatService {

	/** 聊天管理 */
	private MultiServerManager chatManager = RmiProxy.getServer(ChatService.class);
	
	/** 依据数据选择对应的服务 */
	private ChatService chooseService(Object bean) {
		return (ChatService) chatManager.getRemoteService(bean);
	}
	
	@Override
	public void sendMsg(Long selfId, Long userId, String msg) throws RemoteException {
		chooseService(userId).sendMsg(selfId, userId, msg);
	}

	@Override
	public void sendMsg(Long userId, MsgBean msgBean) throws RemoteException {
		chooseService(userId).sendMsg(userId, msgBean);
	}

	@Override
	public void heartbeat() throws RemoteException {
		chooseService(null).heartbeat();
	}
	
}
