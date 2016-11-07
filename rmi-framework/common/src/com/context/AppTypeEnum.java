package com.context;

/** app类型枚举 */
public enum AppTypeEnum {
	android(0, "安卓版"),
	ios(1, "IOS版");
	
	/** 编码值 */
	private Integer code;
	/** 备注 */
	private String remark;
	private AppTypeEnum(Integer code, String remark) {
		this.code = code;
		this.remark = remark;
	}
	
	/**
	 * 依据消息编码值获取消息枚举值
	 * @param code	消息编码值
	 * @return	对应的枚举值
	 */
	public static AppTypeEnum getForCode(Integer code) {
		AppTypeEnum[] ocArray = AppTypeEnum.values();
		for(AppTypeEnum oc : ocArray) {
			if(oc.getCode().equals(code)) {
				return oc;
			}
		}
		return android;
	}
	
	public Integer getCode() {
		return code;
	}
	public String getRemark() {
		return remark;
	}
}
