/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core   
 * 文件名：CodeEnum.java   
 * 版本信息：   
 * 创建日期：2016年10月15日-下午9:52:25
 */
package com.pay.core;

/**   
 * 类名：CodeEnum   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月15日 下午9:52:25   
 * 修改备注：   
 * @version 1.0.0
 */
public enum CodeEnum {
	SUCCESS("200", "success", "操作成功"),
	FAIL("300", "fail", "操作失败"),
	ILLEGAL_PARAM("400", "illegal parameters", "参数不正确"),
	ILLEGAL_SIGN("401", "illegal sign", "签名不正确"),
	ILLEGAL_SERVICE("402", "illegal service", "接口名称不正确");

	private String code;

	private String remark;

	private String desc;

	private CodeEnum(String code, String remark, String desc) {
		this.code = code;
		this.remark = remark;
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public String getRemark() {
		return remark;
	}

	public String getDesc() {
		return desc;
	}

}
