package com.service.jms;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.exception.Error;
import com.exception.SystemException;
import com.utils.LoggerUtil;

public class JmsFactory {
	/** JMS上下文 */
	private static InitialContext ctx = null;
	/** 初始化 
	 * @throws NamingException */
	private static void init() throws NamingException {
		if(ctx != null) {
			return;
		}
		Properties env=new Properties();
		env.put("connectionFactoryNames", "TopicCF,QueueCF");
//		env.put("connectionFactoryNames", "");
//		env.put("topic.topic1", "jms.topic1");
//		env.put("queue.topic1", "jms.queue1");
		env.put(Context.SECURITY_PRINCIPAL, "system");
		env.put(Context.SECURITY_CREDENTIALS, "manager");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		// 使用jndi.properties文件获得一个JNDI连接
		ctx = new InitialContext(env);
		
		// 程序关闭时关掉JMS上下文
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ctx.close();
				} catch (NamingException e) {
					LoggerUtil.error(this.getClass(), e);
				}
			}
		}));
	}
	
	/**
	 * 创建topic消息，同时建立消息接收监听<br/>
	 * (广播消息)
	 * @param actionName	消息事件名
	 * @param listener	收到消息的监听器
	 */
	public static JmsClient createTopic(String actionName) {
		JmsClient jmsClient = new JmsClient();
		try {
			init();
			// 查找一个jms连接工厂并创建连接
			TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup("TopicCF");
			Connection connection = conFactory.createTopicConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic topic = session.createTopic(actionName);
			MessageProducer producer = session.createProducer(topic);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			MessageConsumer consumer = session.createConsumer(topic);
			
			jmsClient.setConnection(connection);
			jmsClient.setSession(session);
			jmsClient.setProducer(producer);
			jmsClient.setConsumer(consumer);
			connection.start();
			return jmsClient;
		} catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}
	
	/**
	 * 创建queue消息，同时建立消息接收监听<br/>
	 * (一对一消息)
	 * @param actionName	消息事件名
	 * @param listener	收到消息的监听器
	 */
	public static JmsClient createQueue(String actionName) {
		JmsClient jmsClient = new JmsClient();
		try {
			init();
			// 查找一个jms连接工厂并创建连接
			TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup("QueueCF");
			Connection connection = conFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(actionName);
			MessageProducer producer = session.createProducer(queue);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			MessageConsumer consumer = session.createConsumer(queue);
			
			jmsClient.setConnection(connection);
			jmsClient.setSession(session);
			jmsClient.setProducer(producer);
			jmsClient.setConsumer(consumer);
			connection.start();
			return jmsClient;
		} catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}
	
	/**
	 * 关闭指定JMS客户端连接
	 * @param jmsClient	JMS客户端
	 */
	public static void closeJms(JmsClient jmsClient) {
		try {
			jmsClient.getProducer().close();
		} catch (JMSException e) {
			throw new SystemException(Error.system_error, e);
		}
		try {
			jmsClient.getSession().close();
		} catch (JMSException e) {
			throw new SystemException(Error.system_error, e);
		}
		try {
			jmsClient.getConnection().close();
		} catch (JMSException e) {
			throw new SystemException(Error.system_error, e);
		}
	}
}
