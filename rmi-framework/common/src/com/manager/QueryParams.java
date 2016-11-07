/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.manager   
* 文件名：MongoQueryManager.java   
* 版本信息：   
* 创建日期：2015-4-27-下午05:44:46
*    
*/
package com.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bean.db.ID;
import com.bean.db.Page;
import com.bean.mongo.ConditionBean;
import com.bean.mongo.Operator; //import com.mongodb.BasicDBList;
//import com.mongodb.BasicDBObject;
import com.utils.ObjectUtil;

/**   
 *    
 * 类名：MongoQueryManager   
 * 类描述：   
 * 创建人：Administrator    
 * 修改时间：2015-4-27 下午05:44:46   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public class QueryParams extends HashMap<String, Object> {
	private static final long serialVersionUID = 4613511013632157449L;

	/** 分页信息,排序信息 */
	private ConditionBean condition;
	/** 要操作的表 */
	private Class<?> clazz;

	/**
	 * Mongodb查询器(将对象转换为查询条件)
	 * @param bean 查询条件(可为null)
	 */
	public QueryParams(ID bean) {
		if(bean != null) {
			clazz = bean.getClass();
			Page page = bean.getPage();
			condition = new ConditionBean(page, null);
			bean.cleanInitValue();
			bean.setPage(null);
			putAll(ObjectUtil.getNotNullFields(bean));
		}
	}

	/**
	 * Mongodb查询器
	 * @param condition 分页信息,排序信息(可为null)
	 * @param params    初始查询条件(可为null)
	 */
	public QueryParams(ConditionBean condition, Map<String, Object> params, Class<?> clazz) {
		this.condition = condition;
		this.clazz = clazz;
		if (params != null) {
			putAll(params);
		}
	}

	/**
	 * Mongodb查询器
	 */
	public QueryParams(Class<?> clazz) {
		this(null, new HashMap<String, Object>(), clazz);
	}

	/**
	 * 拼接查询条件
	* @param key    条件字段
	* @param value  条件值
	* @param oper   匹配运算符
	* @since  1.0.0
	 */
	@SuppressWarnings("unchecked")
	public QueryParams append(String key, Object value, Operator oper) {
		if (oper == null || oper == Operator.eq) {
			put(key, value);
		} else {
			Object obj = get(key);
			Map<String, Object> sonMap = null;
			if (obj == null) {
				sonMap = new HashMap<String, Object>();
				put(key, sonMap);
			} else if (!(obj instanceof Map)) {
				sonMap = new HashMap<String, Object>();
				put(key, sonMap);
				sonMap.put(key, obj);
			} else {
				sonMap = (Map<String, Object>) obj;
			}
			sonMap.put(oper.getMongoCode(), value);
			put(key, sonMap);
		}

		return this;
	}
	
	/**
	 * 或匹配两个参数集合
	* @param params 参数集合   
	* @since  1.0.0
	 */
	@SuppressWarnings("unchecked")
	public QueryParams or(List<QueryParams> params) {
		//        BasicDBObject bdbMap = new BasicDBObject();
		//        BasicDBList bdbList = null;
		//        Object obj = bdbMap.get("$or");
		//        if(obj == null) {
		//            bdbList = new BasicDBList();
		//        } else if(!(obj instanceof BasicDBList)) {
		//            bdbList = new BasicDBList();
		//            bdbList.add(obj);
		//        }
		Map<String, Object> bdbMap = new HashMap<String, Object>();
		List<Object> bdbList = null;
		Object obj = bdbMap.get("$or");
		bdbList = new ArrayList<Object>();
		if (obj != null && !(obj instanceof List)) {
			bdbList.add(obj);
		}

		bdbList.addAll(params);
		bdbList.add(this.clone());
		bdbMap.put("$or", bdbList);
		this.clear();
		this.putAll(bdbMap);

		return this;
	}

	/** 将本条件与此条件进行参数匹配 */
	public QueryParams or(QueryParams param) {
		List<QueryParams> params = new ArrayList<QueryParams>();
		params.add(param);
		or(params);

		return this;
	}

	/** 获取表名 */
	public Class<?> getClazz() {
		return clazz;
	}

	/** 获取分页信息,排序信息 */
	public ConditionBean getCondition() {
		return condition;
	}
}
