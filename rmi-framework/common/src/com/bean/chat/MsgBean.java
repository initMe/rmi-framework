package com.bean.chat;

public class MsgBean extends RequestBean {
	
	private static final long serialVersionUID = -6084593292825197647L;
	//接收消息的人/群/群主/讨论组/聊天室
	private Long otherId;
	/** 接收消息的人的类型(-3:通知该群的所有人; -2：通知该群的群主,群管理(otherId为群号); -1:通知单个人; 0:人的id; 1:群的id; 2:讨论组的id; 3:聊天室的id) */
	private Integer otherType = 0;
	//消息内容
	private String content;
	//消息发送时间
	private Long time = System.currentTimeMillis();
	//群号
	private Long groupNo;
	
	public Long getOtherId() {
		return otherId;
	}
	public void setOtherId(Long otherId) {
		this.otherId = otherId;
	}
	public Integer getOtherType() {
		return otherType;
	}
	public void setOtherType(Integer otherType) {
		this.otherType = otherType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(Long groupNo) {
		this.groupNo = groupNo;
	}
	
}
