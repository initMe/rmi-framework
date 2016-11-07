package com.service.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.bean.BaseBean;
import com.utils.LoggerUtil;

/** JMS的消息监听器 */
public abstract class MsgListener {
	
	private MsgListener ml = this;
	
	/** 缺省的原始监听器 */
	private MessageListener defaultListener = new MessageListener() {
		@Override
		public void onMessage(Message arg0) {
			ObjectMessage objMsg = (ObjectMessage) arg0;
			try {
				ml.onMessage((BaseBean)objMsg.getObject());
			} catch (JMSException e) {
				LoggerUtil.error(this.getClass(), e);
			}
		}
	};
	
	/***
	 * 用户重写监听器内容
	 * @param bb	接收到的对象
	 */
	public abstract void onMessage(BaseBean bb);
	
	/** 获取原始监听器 */
	public MessageListener getListener() {
		return defaultListener;
	}
}
