package com.bean;

import com.bean.business.User;
import com.bean.db.ID;

/** 心跳请求 */
public class Heartbeat extends ID {
	private static final long serialVersionUID = 3628673744792028515L;
	/** 用户id */
	private Long user_id;
	/** 当前经度 */
	private Double lng;
	/** 当前纬度 */
	private Double lat;
	/** 经纬度合并值 */
	private Long mergerLoction;
	/** 发生时间 */
	private Long time = System.currentTimeMillis();
	
	/** 以下不作为实际存储字段 */
	/** 用户对象详情 */
	private User user;
	/** 距离原点多远(米) */
	private Double distance;
	
	/** 用户的标签 */
	private String keyword;
	
	public Long getMergerLoction() {
		return mergerLoction;
	}
	public void setMergerLoction(Long mergerLoction) {
		this.mergerLoction = mergerLoction;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	public Long getUser_id() {
		return user_id;
	}
	public void setUser_id(Long userId) {
		user_id = userId;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) || user_id.equals(((Heartbeat)obj).getUser_id());
	}
	
	@Override
	public int hashCode() {
		return user_id!=null?user_id.hashCode():super.hashCode();
	}
}
