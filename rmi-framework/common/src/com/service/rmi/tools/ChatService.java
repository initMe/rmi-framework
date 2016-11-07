package com.service.rmi.tools;

import java.rmi.RemoteException;

import com.bean.chat.MsgBean;
import com.service.rmi.control.ChatControlImpl;

public interface ChatService extends RemoteBaseService {
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public ChatService controlChatService = new ChatControlImpl();
	
	/**
	 * 发送消息
	 * @param selfId	自己的用户id
	 * @param userId	对方的用户id
	 * @param msg	要发送的信息
	 */
	public void sendMsg(Long selfId, Long userId, String msg) throws RemoteException;
	
	/**
	* sendMsg
	* (跨服务器发送消息)   
	* @param userId 接收人
	* @param msgBean 消息对象
	* @throws RemoteException    
	* @return void   
	* @exception    
	* @since  1.0.0
	*/
	public void sendMsg(Long userId,MsgBean msgBean) throws RemoteException;
}
