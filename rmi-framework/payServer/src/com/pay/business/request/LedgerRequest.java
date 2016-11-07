/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.request   
 * 文件名：LedgerRequest.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:16:02
 */
package com.pay.business.request;

import com.pay.business.BaseRequest;
import com.pay.core.util.RequestCheckUtils;

/**   
 * 类名：LedgerRequest   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class LedgerRequest extends BaseRequest {

	private String notify_url;

	private String ledger_params;

	@Override
	public void check() {
		RequestCheckUtils.checkNotEmpty(service, "service");
		RequestCheckUtils.checkNotEmpty(out_trade_no, "out_trade_no");
		RequestCheckUtils.checkNotEmpty(notify_url, "notify_url");
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getLedger_params() {
		return ledger_params;
	}

	public void setLedger_params(String ledger_params) {
		this.ledger_params = ledger_params;
	}

}
