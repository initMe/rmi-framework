/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.response   
 * 文件名：RefundResponse.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:18:16
 */
package com.pay.business.response;

import java.math.BigDecimal;

import com.pay.business.BaseResponse;

/**   
 * 类名：RefundResponse   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class RefundResponse extends BaseResponse {

	private String out_trade_no;

	private BigDecimal total_fee;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public BigDecimal getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(BigDecimal total_fee) {
		this.total_fee = total_fee;
	}

}
