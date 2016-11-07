package com.service.rmi.tools;

import java.rmi.RemoteException;

import com.bean.sms.SMSBaseResponse;
import com.bean.sms.SMSCheckNoResponse;
import com.service.rmi.control.SMSControlImpl;

public interface SMSClient extends RemoteBaseService {
	
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public SMSClient controlSMS = new SMSControlImpl();
	
	/***
	 * 向手机发送验证码
	 * (注册帐号)
	 * @param mobile	手机号码
	 * @return	返回数据(包含发送的验证码)
	 */
	public SMSCheckNoResponse sendCheckMsg(String mobile) throws RemoteException;
	
	/**
	 * 向手机发送验证码
	 * (修改支付密码)
	 * @param mobile	手机号码
	 * @return	返回数据(包含发送的验证码)
	 * @throws RemoteException
	 */
	public SMSCheckNoResponse sendPayPassMsg(String mobile) throws RemoteException;
	
	/***
	 * 向手机发送短信
	 * @param mobile	手机号码
	 * @param context	信息内容
	 * @return	返回数据
	 */
	public SMSBaseResponse sendMsg(String mobile, String context) throws RemoteException;
}
