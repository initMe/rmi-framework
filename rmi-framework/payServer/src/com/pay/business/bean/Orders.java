package com.pay.business.bean;

import java.math.BigDecimal;

public class Orders {
	/**  */
	private Long id;

	/** 订单号 */
	private String outTradeNo;

	/** 请求流水号 */
	private String outRequestNo;

	/** 资金授权订单号 */
	private String authNo;

	/** 订单金额（元） */
	private BigDecimal totalFee;

	/** 退款金额（元） */
	private BigDecimal refundFee;

	private BigDecimal payFee;

	/** 支付方式 */
	private String limitPay;

	/** 订单标题 */
	private String subject;

	/** 商品描述 */
	private String body;

	/** 创建时间 */
	private Long createTime;

	/** 过期时间(分钟) */
	private Integer expireTime;

	/** 核销码 */
	private String verifyCode;

	/** 核销码创建时间 */
	private Long codeCreateTime;

	/** 订单状态 */
	private String state;

	/** 下单冻结资金异步通知地址 */
	private String freezeNotifyUrl;

	private String freezeReturnUrl;

	/** 退款异步通知地址 */
	private String refundNotifyUrl;

	/** 分账异步通知地址 */
	private String payNotifyUrl;

	/** 买家账号 */
	private String payerUserId;

	/** 卖家账户 */
	private String sellerUserId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo == null ? null : outTradeNo.trim();
	}

	public String getOutRequestNo() {
		return outRequestNo;
	}

	public void setOutRequestNo(String outRequestNo) {
		this.outRequestNo = outRequestNo == null ? null : outRequestNo.trim();
	}

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo == null ? null : authNo.trim();
	}

	public BigDecimal getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(BigDecimal totalFee) {
		this.totalFee = totalFee;
	}

	public String getLimitPay() {
		return limitPay;
	}

	public void setLimitPay(String limitPay) {
		this.limitPay = limitPay == null ? null : limitPay.trim();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject == null ? null : subject.trim();
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body == null ? null : body.trim();
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public Integer getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Integer expireTime) {
		this.expireTime = expireTime;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode == null ? null : verifyCode.trim();
	}

	public Long getCodeCreateTime() {
		return codeCreateTime;
	}

	public void setCodeCreateTime(Long codeCreateTime) {
		this.codeCreateTime = codeCreateTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state == null ? null : state.trim();
	}

	public String getFreezeNotifyUrl() {
		return freezeNotifyUrl;
	}

	public void setFreezeNotifyUrl(String freezeNotifyUrl) {
		this.freezeNotifyUrl = freezeNotifyUrl == null ? null : freezeNotifyUrl.trim();
	}

	public String getRefundNotifyUrl() {
		return refundNotifyUrl;
	}

	public void setRefundNotifyUrl(String refundNotifyUrl) {
		this.refundNotifyUrl = refundNotifyUrl == null ? null : refundNotifyUrl.trim();
	}

	public String getPayNotifyUrl() {
		return payNotifyUrl;
	}

	public void setPayNotifyUrl(String payNotifyUrl) {
		this.payNotifyUrl = payNotifyUrl == null ? null : payNotifyUrl.trim();
	}

	public String getPayerUserId() {
		return payerUserId;
	}

	public void setPayerUserId(String payerUserId) {
		this.payerUserId = payerUserId == null ? null : payerUserId.trim();
	}

	public String getSellerUserId() {
		return sellerUserId;
	}

	public void setSellerUserId(String sellerUserId) {
		this.sellerUserId = sellerUserId == null ? null : sellerUserId.trim();
	}

	public BigDecimal getRefundFee() {
		return refundFee;
	}

	public void setRefundFee(BigDecimal refundFee) {
		this.refundFee = refundFee;
	}

	public String getFreezeReturnUrl() {
		return freezeReturnUrl;
	}

	public void setFreezeReturnUrl(String freezeReturnUrl) {
		this.freezeReturnUrl = freezeReturnUrl;
	}

	public BigDecimal getPayFee() {
		return payFee;
	}

	public void setPayFee(BigDecimal payFee) {
		this.payFee = payFee;
	}
}