/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.bean   
* 文件名：Student.java   
* 版本信息：   
* 创建日期：2015-5-6-上午10:47:19
*    
*/
package com.bean;

import com.bean.db.ID;

/**   
 *    
 * 类名：Student   
 * 类描述：   
 * 创建人：Administrator    
 * 修改时间：2015-5-6 上午10:47:19   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public class Student extends ID{

	private String name;
	private String sex;
	private Integer language;
	private Integer math;
	private Integer history;
	private Integer plitics;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getLanguage() {
		return language;
	}
	public void setLanguage(Integer language) {
		this.language = language;
	}
	public Integer getMath() {
		return math;
	}
	public void setMath(Integer math) {
		this.math = math;
	}
	public Integer getHistory() {
		return history;
	}
	public void setHistory(Integer history) {
		this.history = history;
	}
	public Integer getPlitics() {
		return plitics;
	}
	public void setPlitics(Integer plitics) {
		this.plitics = plitics;
	}
	
	
}
