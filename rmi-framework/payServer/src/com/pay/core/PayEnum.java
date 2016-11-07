package com.pay.core;

import com.utils.StringUtil;

public enum PayEnum {
	ALIPAY("alipay", "支付宝支付", "com.pay.core.alipay.AlipayExcutor"),
	WECHAT("wechat", "微信支付", "com.pay.core.wechat.WechatExcutor");

	/** 属性值 */
	private String code;

	/** 备注值 */
	private String description;

	/** 包地址 */
	private String pkg;

	private PayEnum(String code, String description, String pkg) {
		this.code = code;
		this.description = description;
		this.pkg = pkg;
	}

	public static String getPkg(String code) {
		if (StringUtil.isEmpty(code)) {
			return null;
		}

		for (PayEnum e : PayEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getPkg();
			}
		}

		return null;
	}

	public static String getDescription(String code) {
		if (StringUtil.isEmpty(code)) {
			return null;
		}

		for (PayEnum e : PayEnum.values()) {
			if (e.getCode().equals(code)) {
				return e.getDescription();
			}
		}

		return null;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getPkg() {
		return pkg;
	}
}
