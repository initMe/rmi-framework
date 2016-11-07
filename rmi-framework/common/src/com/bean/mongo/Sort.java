/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.bean   
* 文件名：SortBean.java   
* 版本信息：   
* 创建日期：2015-4-24-下午04:55:22
*    
*/
package com.bean.mongo;

import java.util.LinkedHashMap;
import java.util.Map;

import com.bean.BaseBean;

/**   
 *    
 * 类名：SortBean   
 * 类描述：排序对象   
 * 创建人：Administrator    
 * 修改时间：2015-4-24 下午04:55:22   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public class Sort extends BaseBean {
	private static final long serialVersionUID = -3505879565588527800L;
	private Map<String, Object> field = new LinkedHashMap<String, Object>();

	public Sort() {}
	
    public Sort(String key, Order order) {
    	on(key, order);
    }

    public Sort on(String key, Order order) {
        field.put(key, order.getCode());
        return this;
    }

    public Map<String, Object> getSortObject() {
    	return field.size()==0?null:field;
    }
}
