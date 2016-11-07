/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.business   
 * 文件名：EnumState.java   
 * 版本信息：   
 * 创建日期：2016年10月13日-下午7:25:37
 */
package com.pay.business;

/**   
 * 类名：EnumState   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月13日 下午7:25:37   
 * 修改备注：   
 * @version 1.0.0
 */
public enum EnumState {

	WAIT_PAY("wait_pay", "待支付"),
	Freezed("freezed", "已冻结"),
	VERIFYED("verifyed", "已核销"),
	REFUNDED("refunded", "已退款"),
	PAYED("payed", "已支付"), ;

	private String code;

	private String mark;

	private EnumState(String code, String mark) {
		this.code = code;
		this.mark = mark;
	}

	public String getCode() {
		return code;
	}

	public String getMark() {
		return mark;
	}

}
