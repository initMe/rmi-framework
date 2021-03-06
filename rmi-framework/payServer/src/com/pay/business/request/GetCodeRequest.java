/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.request   
 * 文件名：GetCodeRequest.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:12:06
 */
package com.pay.business.request;

import com.pay.business.BaseRequest;
import com.pay.core.util.RequestCheckUtils;

/**   
 * 类名：GetCodeRequest   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class GetCodeRequest extends BaseRequest {

	@Override
	public void check() {
		RequestCheckUtils.checkNotEmpty(service, "service");
		RequestCheckUtils.checkNotEmpty(out_trade_no, "out_trade_no");
	}

}
