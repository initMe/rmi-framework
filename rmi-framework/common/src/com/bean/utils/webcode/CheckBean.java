/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.bean   
 * 文件名：SearchBean.java   
 * 版本信息：   
 * 创建日期：2016-7-13-上午10:38:28
 */
package com.bean.utils.webcode;

import java.awt.Color;

import com.manager.WebCodeExecutor;

/**   
 * 检查过程bean
 * 类名：SearchBean   
 * 类描述：   
 * 创建人：F12_end   
 * 修改人：F12_end   
 * 修改时间：2016-7-13 上午10:38:28   
 * 修改备注：   
 * @version 1.0.0
 */
public class CheckBean {
	/** 匹配率 */
	private int num;
	/** 模糊值的rgb标准 */
	private int rgb;
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getRgb() {
		return rgb;
	}
	public void setRgb(int rgb) {
		this.rgb = rgb;
	}
	
	/** 重写equals，增加相似度校验 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CheckBean) {
			return super.equals(obj) || WebCodeExecutor.like(rgb, ((CheckBean)obj).rgb);
		} else if(obj instanceof Integer) {
			return ((Integer)obj)==rgb || WebCodeExecutor.like(rgb, (Integer)obj);
		}
		return super.equals(obj);
	}
	
	/** 获取相似标记 */
	public static int getLikeFlag(int rgb) {
		Color base = new Color(rgb);
		int r = base.getRed();
		int g = base.getGreen();
		int b = base.getBlue();
		Integer max = Math.max(Math.max(r, g), b);
		int length = Integer.toBinaryString(max).length();
		int newValue = 0;
		for(int i=0,offset=0; i<length; i++,offset+=3) {
			newValue += (r>>i)%2<<offset;
			newValue += (g>>i)%2<<(offset+1);
			newValue += (b>>i)%2<<(offset+2);
		}
		return newValue;
	}
}
