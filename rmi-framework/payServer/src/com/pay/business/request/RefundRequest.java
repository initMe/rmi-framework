/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.request   
 * 文件名：RefundRequest.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:14:38
 */
package com.pay.business.request;

import com.pay.business.BaseRequest;
import com.pay.core.util.RequestCheckUtils;

/**   
 * 类名：RefundRequest   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class RefundRequest extends BaseRequest {

	private String notify_url;

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	@Override
	public void check() {
		RequestCheckUtils.checkNotEmpty(service, "service");
		RequestCheckUtils.checkNotEmpty(out_trade_no, "out_trade_no");
		RequestCheckUtils.checkNotEmpty(notify_url, "notify_url");
	}

}
