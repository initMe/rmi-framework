package com.utils;

import java.util.HashMap;
import java.util.Map;

import com.bean.BaseBean;
import com.bean.db.DefaultPageBean;
import com.bean.db.Page;
import com.exception.DataBaseException;
import com.exception.Error;

public class PageUtil {
	
	/**
	 * 设置分页大小(每页显示条数)
	 * @param dp	缺省分页对象
	 * @param num	每页总条数
	 * @return	如果缺省分页对象为null，则内部会重新实例化，返回实例
	 */
	public static DefaultPageBean setPageShowNum(DefaultPageBean dp, int num) {
		if(dp == null) {
			dp = new DefaultPageBean();
		}
		dp.setShowNum(num);
		return dp;
	}
	
	/**
	 * 设置分页大小(每页显示条数)
	 * @param param	参数对象
	 * @param clazz	参数对象的类型(如果为空则实例化一个)
	 * @param num	每页总条数
	 * @return	如果缺省分页对象为null，则内部会重新实例化，返回实例
	 * @throws Exception 
	 */
	public static <T extends BaseBean> T setPageShowNum(T param, Class<T> clazz, int num) throws Exception {
		if(num > 0) {
			if(param == null) {
				Map<String, Object> attrMap = new HashMap<String, Object>();
				attrMap.put("page", new Page());
				param = ObjectUtil.initObject(clazz, attrMap);
			}
			Object page = ObjectUtil.getAttributeValue(param, "page");
			if(page == null) {
				page = ObjectUtil.initObject(Page.class, null);
				ObjectUtil.setAttribute(param, "page", page);
			}
			ObjectUtil.setAttribute(page, "showCount", num);
			return param;
		}
		LoggerUtil.error(PageUtil.class, "设置数据库分页，每页显示条数太小");
		throw new DataBaseException(Error.database_page_min);
	}
}
