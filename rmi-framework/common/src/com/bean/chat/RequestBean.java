package com.bean.chat;

import java.io.Serializable;

public class RequestBean implements Serializable {
	private static final long serialVersionUID = 3552521479859497265L;
	
	//用户编号
	private Long senderUserId;
	//消息类型
	private Integer functionNo;
	//消息体类型(0:文字)
	private Integer type;
	public Long getSenderUserId() {
		return senderUserId;
	}
	public void setSenderUserId(Long senderUserId) {
		this.senderUserId = senderUserId;
	}
	public Integer getFunctionNo() {
		return functionNo;
	}
	public void setFunctionNo(Integer functionNo) {
		this.functionNo = functionNo;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
