package com.bean.sms;

import com.bean.BaseBean;

/** 短信发送的返回数据 */
public class SMSBaseResponse extends BaseBean {
	private static final long serialVersionUID = -218976082374754215L;
	/** 返回编号(1:提交成功;小于1：失败) */
	private String code;
	private String msg;
	private String smsid;
	private String mobile;
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	/** 是否发送成功 */
	public boolean isSendSuccess() {
		return code!=null && code.equals("1");
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSmsid() {
		return smsid;
	}
	public void setSmsid(String smsid) {
		this.smsid = smsid;
	}
}
