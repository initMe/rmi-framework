/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api   
 * 文件名：BaseRequest.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:09:44
 */
package com.pay.business;

import com.pay.core.BaseBean;

/**   
 * 类名：BaseRequest   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public abstract class BaseRequest extends BaseBean {

	/** 接口名称 **/
	protected String service;

	protected final static String input_charset = "utf-8";

	protected final static String sign_type = "MD5";

	protected String sign;

	protected String out_trade_no;

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public static String getInputCharset() {
		return input_charset;
	}

	public static String getSignType() {
		return sign_type;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public abstract void check();
}
