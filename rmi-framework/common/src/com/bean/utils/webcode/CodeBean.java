/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 文件名：CodeBean.java   
 * 版本信息：   
 * 创建日期：2016-7-13-上午11:14:28
 */
package com.bean.utils.webcode;

import java.awt.Point;
import java.util.List;

/**   
 * 校验码扩展类
 * 类名：CodeBean   
 * 类描述：   
 * 创建人：F12_end   
 * 修改人：F12_end   
 * 修改时间：2016-7-13 上午11:14:28   
 * 修改备注：   
 * @version 1.0.0
 */
public class CodeBean {
	private String code;
	private List<Point> drawList;
	
	public CodeBean() {}
	public CodeBean(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<Point> getDrawList() {
		return drawList;
	}
	public void setDrawList(List<Point> drawList) {
		this.drawList = drawList;
	}
}
