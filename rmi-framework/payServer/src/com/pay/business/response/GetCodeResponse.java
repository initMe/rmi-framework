/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.response   
 * 文件名：GetCodeResponse.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:16:49
 */
package com.pay.business.response;

import com.pay.business.BaseResponse;

/**   
 * 类名：GetCodeResponse   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class GetCodeResponse extends BaseResponse {

	private String out_trade_no;

	private String verify_code;

	private Integer expire_time;

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public Integer getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(Integer expire_time) {
		this.expire_time = expire_time;
	}

}
