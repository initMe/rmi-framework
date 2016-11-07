/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.bean   
 * 文件名：CheckResultBean.java   
 * 版本信息：   
 * 创建日期：2016-7-13-上午10:43:44
 */
package com.bean.utils.webcode;

/**   
 * 检查分析结果bean
 * 类名：CheckResultBean   
 * 类描述：   
 * 创建人：F12_end   
 * 修改人：F12_end   
 * 修改时间：2016-7-13 上午10:43:44   
 * 修改备注：   
 * @version 1.0.0
 */
public class CheckResultBean {
	/** 覆盖率 */
	private double rate;
	/** 重叠量 */
	private int num;
	/** code值 */
	private String code;
	/** 颜色值 */
	private int rgb;
	private double num_rate;
	
	public double getNum_rate() {
		return num_rate;
	}
	public void setNum_rate(double numRate) {
		num_rate = numRate;
	}
	public int getRgb() {
		return rgb;
	}
	public void setRgb(int rgb) {
		this.rgb = rgb;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
