package com.service.jms;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.bean.BaseBean;
import com.bean.db.ID;
import com.exception.Error;
import com.exception.SystemException;

public class JmsClient {
	/** JMS连接 */
	private Connection connection = null;
	/** JMS消息生产器 */
	private MessageProducer producer = null;
	/** JMS用户会话 */
	private Session session = null;
	/** 消息接收器 */
	private MessageConsumer consumer = null;
	
	JmsClient(){}
	
	/***
	 * 注册消息监听 
	 * @param listener	用户自定义的消息监听处理
	 */
	public void registerListener(MsgListener listener) {
		if(listener != null) {
			try {
				consumer.setMessageListener(listener.getListener());
			} catch (JMSException e) {
				throw new SystemException(Error.system_error, e);
			}
		}
	}
	
	
	/***
	 * 发送消息
	 * @param bb	消息对象
	 * @throws JMSException
	 */
	public void sendMessage(BaseBean bb) {
		try {
			ObjectMessage objMsg = session.createObjectMessage(bb);
			producer.send(objMsg);
		} catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	Connection getConnection() {
		return connection;
	}

	void setConnection(Connection connection) {
		this.connection = connection;
	}

	MessageProducer getProducer() {
		return producer;
	}

	void setProducer(MessageProducer producer) {
		this.producer = producer;
	}

	Session getSession() {
		return session;
	}

	void setSession(Session session) {
		this.session = session;
	}
	
	MessageConsumer getConsumer() {
		return consumer;
	}

	void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}

	public static void main(String[] args) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				JmsClient jms = JmsFactory.createQueue("aa");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ID bb = new ID();
//				bb.setId(1L);
//				jms.sendMessage(bb);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				bb.setId(2L);
				jms.sendMessage(bb);
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				JmsClient jms = JmsFactory.createQueue("aa");
				jms.registerListener(new MsgListener() {
					@Override
					public void onMessage(BaseBean bb) {
						System.out.println("线程2：" + ((ID)bb).getId());
					}
				});
			}
		}).start();
	}
}
