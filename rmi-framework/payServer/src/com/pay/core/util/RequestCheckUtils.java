/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api.util   
 * 文件名：RequestCheckUtils.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:41:49
 */
package com.pay.core.util;

import com.pay.business.ApiRuleException;

/**   
 * 类名：RequestCheckUtils   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class RequestCheckUtils {

	public static void checkNotEmpty(Object value, String fieldName) throws ApiRuleException {
		if (value == null) {
			throw new ApiRuleException("client-error:Missing required arguments:" + fieldName + "");
		}
		if (value instanceof String) {
			if (((String) value).trim().length() == 0) {
				throw new ApiRuleException("client-error:Missing required arguments:" + fieldName + "");
			}
		}
	}

}
