/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.request   
 * 文件名：OrderPayRequest.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:11:35
 */
package com.pay.business.request;

import java.math.BigDecimal;

import com.pay.api.PayEnum;
import com.pay.business.BaseRequest;
import com.pay.core.util.RequestCheckUtils;

/**   
 * 类名：OrderPayRequest   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class OrderPayRequest extends BaseRequest {

	private BigDecimal total_fee;

	private String notify_url;

	private String return_url;

	private String limit_pay;

	private String subject;

	private String body;

	private String extra;

	private String seller_user_id;

	public BigDecimal getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_ur) {
		this.notify_url = notify_ur;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	@Override
	public void check() {
		RequestCheckUtils.checkNotEmpty(service, "service");
		RequestCheckUtils.checkNotEmpty(out_trade_no, "out_trade_no");
		RequestCheckUtils.checkNotEmpty(total_fee, "total_fee");
		RequestCheckUtils.checkNotEmpty(notify_url, "notify_url");
		RequestCheckUtils.checkNotEmpty(limit_pay, "limit_pay");
		RequestCheckUtils.checkNotEmpty(subject, "subject");
		if (limit_pay.equals(PayEnum.ALIPAY.getCode())) {
			RequestCheckUtils.checkNotEmpty(seller_user_id, "seller_user_id");
		}
	}

	public String getOut_request_no() {
		return out_trade_no + String.valueOf(System.currentTimeMillis());
	}

	public String getSeller_user_id() {
		return seller_user_id;
	}

	public void setSeller_user_id(String seller_user_id) {
		this.seller_user_id = seller_user_id;
	}

}
