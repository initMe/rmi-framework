package com.bean;


public class Version extends BaseBean {
	private static final long serialVersionUID = -5323486414239626459L;
	/** 版本号 */
	private Long version = -1L;
	/** 请求者自己的用户id */
	private Long selfId;
	/** 请求者请求数据对应的人的用户id */
	private Long userId;
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public Long getSelfId() {
		return selfId;
	}
	public void setSelfId(Long selfId) {
		this.selfId = selfId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
}
