package test.dao;

import com.bean.BaseBean;

/** 好友实体 */
public class t_friend extends BaseBean {
	private static final long serialVersionUID = -4408451310369819557L;
	/** 唯一标识 */
	private Integer id;
	/** 自己id */
	private Integer selfId;
	/** 好友id */
	private Integer friendId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSelfId() {
		return selfId;
	}
	public void setSelfId(Integer selfId) {
		this.selfId = selfId;
	}
	public Integer getFriendId() {
		return friendId;
	}
	public void setFriendId(Integer friendId) {
		this.friendId = friendId;
	}
}
