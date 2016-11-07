package com.dao.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

/** 数据库结果集处理器,返回结果处理 */
public interface QueryResultExecutor<T extends Object> {
	/**
	 * 查询结果集处理 
	 * @param returnClass	返回值的类型(返回的javaBean类型)
	 * @param rs	结果集
	 * @return	返回值
	 */
	public Object execute(Class<T> returnClass, ResultSet rs) throws Exception;
	
	/** 缺省的结果集处理器 */
	@SuppressWarnings("unchecked")
	public final QueryResultExecutor defaultExecutor = new QueryResultExecutor() {

		@Override
		public Object execute(Class returnClass, ResultSet rs) throws Exception {
			List<Object> rsList = new ArrayList<Object>();
			// 数据集的列
			ResultSetMetaData meta = rs.getMetaData();
			// 列长度
			int count = meta.getColumnCount();
			// 每列的列名
			List<String> countNameList = new ArrayList<String>();
			// 遍历列
			for(int i=1; i<=count; i++) {
				countNameList.add(meta.getColumnName(i));
			}
			Map<String, Class> map = ObjectUtil.getFieldNames(returnClass);
			// 遍历结果集
			while(rs.next()) {
				Object obj = ObjectUtil.initObject(returnClass, null);
				for(String countName : countNameList) {
					Object arrtValue = rs.getObject(countName);
//					LoggerUtil.info(this.getClass(), countName +"="+ arrtValue+"");
					ObjectUtil.setAttribute(obj, countName, ObjectUtil.parseToObject(arrtValue, map.get(countName)));
				}
				rsList.add(obj);
			}
			return rsList;
		}
	};
}
