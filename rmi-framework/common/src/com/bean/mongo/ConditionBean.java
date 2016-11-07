/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.bean.mongo 
* 文件名：ConditionBean.java   
* 版本信息：   
* 创建日期：2015-4-24-下午04:51:26
*    
*/
package com.bean.mongo;

import java.util.Map;

import com.bean.BaseBean;
import com.bean.db.Page;

/**   
 *    
 * 类名：ConditionBean   
 * 类描述：查询分页
 * 创建人：Administrator    
 * 修改时间：2015-4-24 下午04:51:26   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public class ConditionBean extends BaseBean {

	private static final long serialVersionUID = -4559782016761097540L;

	/** 起始下标 */
	private int offset;
	/** 截止下标 */
	private int limit;
	/** 排序 */
	private Sort sort;
	
	public ConditionBean() {}
	
	public ConditionBean(Page page, Sort sort) {
		if(page != null) {
			int nowPage = page.getCurrentPage();
			int contentNum = page.getShowCount();
			offset = (nowPage-1) * contentNum;
			limit = nowPage * contentNum;
		}
		if(sort != null) {
			this.sort = sort;
		}
	}

	public ConditionBean offset(int offset) {
		this.offset = offset;
		return this;
	}

	public ConditionBean limit(int limit) {
		this.limit = limit;
		return this;
	}

	public int getSkip() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public Sort sort() {
		if (this.sort == null) {
			this.sort = new Sort();
		}
		return this.sort;
	}

	public Sort sort(String key, Order order) {
		if (this.sort == null) {
			this.sort = new Sort(key, order);
		}
		return this.sort;
	}

	public Map<String, Object> getSortObject() {
		if (this.sort == null) {
			return null;
		}
		return this.sort.getSortObject();
	}

}
