package com.bean.business;

import java.util.ArrayList;
import java.util.List;

import com.bean.db.ID;

public class User extends ID {
	private static final long serialVersionUID = 78749998549347551L;

	/** 手机号 */
	private String mobile;
	/** 设备编号 */
	private String device;
	/** 昵称 */
	private String name;
	/** 头像url */
	private String head;
	/** 余额 */
	private Double money;
	/** 余额支付密码 */
	private String money_pass;
	/** 是否推送(0:推送，1：不推送 ) */
	private Integer push_status;
	/** 逗号分隔的用户标签 */
	private String server_tag;
	/** 个人展示说明 */
	private String show_contend;
	/** 个人展示中的图片(逗号分隔) */
	private String show_img;
	/** 总评分 */
	private Integer score;
	/** 被评分次数 */
	private Integer score_num;
	/** 接单并且成功交易次数 */
	private Integer order_success_num;
	/** 是否实名认证了(0:未实名认证，1:认证中，2:已经实名认证，-1:认证失败) */
	private Integer isTrue;

	/** 性别 */
	private String sex;
	/** 脸部特征码 */
	private String face_code;
	/** QQ号 */
	private String qq;
	/** 微信号 */
	private String weixin;
	/** 微博号 */
	private String weibo;
	/** 最后登录时间 */
	private Long last_login_time;
	/**是否有效(0:是,1:否)*/
	private Integer valid;
	
	/** 还未评价的订单(为null或为0，代表用户没有为评价的订单 ) */
	private List<Long> discussOrderId = new ArrayList<Long>();
	
	/** 用户当前登录设备(0:安卓用户， 1:IOS用户) */
	private Integer phone_type;
	
	/** 第三方登陆标记 */
	private transient String accessToken;
	/** 第三方登录openId */
	private transient String openId;
	/** 第三登录appId */
	private transient String appId;

	/** 短信验证码(不做实际存储) */
	private transient String smsCode;
	/** 临时存储userId(一般用来接收参数) */
	private transient Long user_id;
	
	public void cleanPower() {
		device = null;
		money_pass = null;
		money = null;
		face_code = null;
	}

	public List<Long> getDiscussOrderId() {
		return discussOrderId;
	}

	public void setDiscussOrderId(List<Long> discussOrderId) {
		this.discussOrderId = discussOrderId;
	}

	public Integer getPhone_type() {
		return phone_type;
	}

	public void setPhone_type(Integer phoneType) {
		phone_type = phoneType;
	}

	public String getShow_contend() {
		return show_contend;
	}

	public void setShow_contend(String showContend) {
		show_contend = showContend;
	}

	public Integer getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(Integer isTrue) {
		this.isTrue = isTrue;
	}

	public Integer getOrder_success_num() {
		return order_success_num;
	}

	public void setOrder_success_num(Integer orderSuccessNum) {
		order_success_num = orderSuccessNum;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	public String getShow_img() {
		return show_img;
	}

	public void setShow_img(String showImg) {
		show_img = showImg;
	}

	public Integer getScore_num() {
		return score_num;
	}

	public void setScore_num(Integer scoreNum) {
		score_num = scoreNum;
	}

	public String getServer_tag() {
		return server_tag;
	}

	public void setServer_tag(String serverTag) {
		server_tag = serverTag;
	}

	public Integer getPush_status() {
		return push_status;
	}

	public void setPush_status(Integer pushStatus) {
		push_status = pushStatus;
	}

	public Long getUser_id() {
		return user_id == null ? id : user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
		this.id = user_id;
	}

	@Override
	public Long getId() {
		return id == null ? user_id : id;
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
		this.user_id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getMoney_pass() {
		return money_pass;
	}

	public void setMoney_pass(String moneyPass) {
		money_pass = moneyPass;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getFace_code() {
		return face_code;
	}

	public void setFace_code(String faceCode) {
		face_code = faceCode;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public Long getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Long last_login_time) {
		this.last_login_time = last_login_time;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}
	
}
