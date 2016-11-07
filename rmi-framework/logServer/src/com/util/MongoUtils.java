/**   
 * Copyright (c) 版权所有 2010-2015
 * 产品名：   
 * 包名：com.util   
 * 文件名：MongoUtils.java   
 * 版本信息：   
 * 创建日期：2015-4-24-下午03:01:45
 *    
 */
package com.util;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.utils.ObjectUtil;

public class MongoUtils {

	/**
	 * objToDBObj (实体对象转mongo对象)
	 * 
	 * @param obj
	 * @return DBObject
	 * @since 1.0.0
	 */
	public static DBObject objToDBObj(Object obj) {
		Map<String, Object> map = null;
		BasicDBObject dbObj = null;
		try {
			map = ObjectUtil.getNotNullFieldsForStructure(obj);
			//map = JsonUtil.objToMap(map);
			dbObj = new BasicDBObject(map);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbObj;
	}

	public static <T> T dbObjToBean(DBObject dbObj, Class<T> beanClass) {
//		String json = JsonUtil.objToJson(dbObj.toMap());
//		return JsonUtil.jsonToBean(json, beanClass);
		return (T) ObjectUtil.initObject(beanClass, dbObj.toMap());
	}

}
