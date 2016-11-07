package com.pay.core;

import com.utils.StringUtil;

public enum ServiceEnum {
	PAYMENT("支付网关", "pay.auth.pay.apply", "payment"),
	VERIFYCODE("验证核销码", "pay.auth.code.verify", "verifyCode"),
	REVERIFYCODE("重发核销码", "pay.auth.code.request", "reVerificationCode"),
	REFUND("退款", "pay.auth.refund.apply", "refund"),
	PRORATE("分帐", "pay.auth.prorate.apply", "prorate");

	private String name;

	private String mark;

	private String method;

	private ServiceEnum(String name, String mark, String method) {
		this.name = name;
		this.mark = mark;
		this.method = method;
	}

	public static String getName(String mark) {
		if (StringUtil.isEmpty(mark)) {
			return null;
		}

		for (ServiceEnum e : ServiceEnum.values()) {
			if (e.getMark().equals(mark)) {
				return e.getName();
			}
		}

		return null;
	}

	public static ServiceEnum getByServiceName(String mark) {
		if (StringUtil.isEmpty(mark)) {
			return null;
		}

		for (ServiceEnum e : ServiceEnum.values()) {
			if (e.getMark().equals(mark)) {
				return e;
			}
		}

		return null;
	}

	public String getName() {
		return name;
	}

	public String getMark() {
		return mark;
	}

	public String getMethod() {
		return method;
	}

	public boolean isPay() {
		if (StringUtil.isEmpty(mark)) {
			return false;
		}
		if (mark.equals(PAYMENT.getMark()) ||
				mark.equals(REFUND.getMark())
				|| mark.equals(PRORATE.getMark())) {
			return true;
		}

		return false;
	}
}
