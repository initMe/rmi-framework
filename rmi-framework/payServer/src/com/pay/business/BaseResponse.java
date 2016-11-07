/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.request   
 * 文件名：BaseResponse.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:12:38
 */
package com.pay.business;

import com.pay.core.BaseBean;
import com.utils.DateUtil;

/**   
 * 类名：BaseResponse   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class BaseResponse extends BaseBean {

	protected String code;

	protected String err_msg;

	protected String notify_time = DateUtil.getDefaultUtil().dateLongToString(System.currentTimeMillis());

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String err_msg) {
		this.err_msg = err_msg;
	}

	public String getNotify_time() {
		return notify_time;
	}

	public void setNotify_time(String notify_time) {
		this.notify_time = notify_time;
	}

}
