/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.api   
 * 文件名：ApiRuleException.java   
 * 版本信息：   
 * 创建日期：2016年10月10日-下午5:35:56
 */
package com.pay.business;

/**   
 * 类名：ApiRuleException   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改备注：   
 * @version 1.0.0
 */
public class ApiRuleException extends RuntimeException {

	private static final long serialVersionUID = 739385746576313275L;

	public ApiRuleException() {
		super();
	}

	public ApiRuleException(String errMsg) {
		super(errMsg);
	}

}
