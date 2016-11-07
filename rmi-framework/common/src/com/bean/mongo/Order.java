/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.bean   
* 文件名：Order.java   
* 版本信息：   
* 创建日期：2015-4-24-下午04:54:31
*    
*/
package com.bean.mongo;

/**   
 *    
 * 类名：Order   
 * 类描述：排序状态  
 * 创建人：Administrator    
 * 修改时间：2015-4-24 下午04:54:31   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public enum Order {
	/** 正序 */
    ASC(1, "正序"), 
    /** 倒序 */
    DESC(-1, "倒序");
    
    /** 编码值 */
    private Integer code;
    /** 备注 */
    private String remark;
    
    private Order(int code, String remark) {
    	this.code = code;
    	this.remark = remark;
    }
    
    public static Order getOrderByCode(int code) {
    	for(Order o : Order.values()) {
    		if(o.getCode().equals(code)) {
    			return o;
    		}
    	}
    	return ASC;
    }

	public Integer getCode() {
		return code;
	}

	public String getRemark() {
		return remark;
	}
}
