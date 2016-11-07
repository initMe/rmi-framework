package com.bean.business;

import com.bean.db.ID;

public class LoginUser extends ID {
	private static final long serialVersionUID = 1L;
	/** 用户名 */
	private String uname;
	/** 用户密码 */
	private String upass;
	/** 权限码 */
	private Long auth;
	/** 角色id */
	private String role_id;
	/**手机号*/
	private String mobile;
	/**是否有效(1:有效,0:停用)*/
	private Long valid;
	/**真实姓名*/
	private String real_name;
	/**备注*/
	private String remark;
	/**创建者id*/
	private Long create_uid;
	/**创建者用户名*/
	private String create_uname;
	/**登录IP*/
	private String last_login_ip;
	/**登录时间*/
	private Long last_login_time;
	/**退出时间*/
	private Long last_out_time;

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUpass() {
		return upass;
	}

	public void setUpass(String upass) {
		this.upass = upass;
	}

	public Long getAuth() {
		return auth;
	}

	public void setAuth(Long auth) {
		this.auth = auth;
	}

	public String getRole_id() {
		return role_id;
	}

	public void setRole_id(String role_id) {
		this.role_id = role_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Long getValid() {
		return valid;
	}

	public void setValid(Long valid) {
		this.valid = valid;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreate_uname() {
		return create_uname;
	}

	public void setCreate_uname(String create_uname) {
		this.create_uname = create_uname;
	}

	public String getLast_login_ip() {
		return last_login_ip;
	}

	public void setLast_login_ip(String last_login_ip) {
		this.last_login_ip = last_login_ip;
	}

	public Long getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Long last_login_time) {
		this.last_login_time = last_login_time;
	}

	public Long getLast_out_time() {
		return last_out_time;
	}

	public void setLast_out_time(Long last_out_time) {
		this.last_out_time = last_out_time;
	}

	public Long getCreate_uid() {
		return create_uid;
	}

	public void setCreate_uid(Long create_uid) {
		this.create_uid = create_uid;
	}

}
