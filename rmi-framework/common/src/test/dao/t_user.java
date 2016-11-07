package test.dao;

import com.bean.BaseBean;

/** 用户实体类 */
public class t_user extends BaseBean {
	private static final long serialVersionUID = -1953896030045338650L;
	/** 用户id */
	private Integer id;
	/** 头像唯一标识 */
	private String head;
	/** 别名，昵称 */
	private String alias;
	/** 用户名 */
	private String uname;
	/** 密码 */
	private String upass;
	/** 性别 */
	private String sex;
	/** 电话 */
	private String phone;
	/** 邮箱 */
	private String email;
	/** 住址 */
	private String place;
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
}
