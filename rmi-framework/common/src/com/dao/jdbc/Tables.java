package com.dao.jdbc;

import com.bean.db.jdbc.TestTable1;
import com.exception.DataBaseException;
import com.exception.Error;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

/** 数据库中所有表对应java中class */
public enum Tables {
	TESTTABLE1(TestTable1.class, "table1");
	
	/** 表对应的实体类的class */
	private Class tableClass;
	/** 表名 */
	private String tableName;
	
	private Tables(Class tableClass, String tableName) {
		this.tableClass = tableClass;
		this.tableName = tableName;
	}
	
	/***
	 * 获取对应的class
	 * @param <T>	JdbcTable的子类
	 * @return	对应的class
	 */
	public Class getTableClass() {
		return tableClass;
	}
	
	/***
	 * 获取对应的class的空实体bean
	 * @param <T>	JdbcTable的子类
	 * @return	对应的class
	 */
	public Class getTableBean() {
		try {
			return ObjectUtil.initObject(tableClass, null);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
		}
		return null;
	}
	
	/**
	 * 依据class获取对应的对应枚举
	 * @param clazz	已知的class
	 * @return	返回对应的枚举
	 */
	public static Tables getTable(Class clazz) {
		Tables ts[] = Tables.values();
		for(Tables t : ts) {
			if(t.getTableClass().equals(clazz)) {
				return t;
			}
		}
		throw new DataBaseException(Error.db_jdbc_table_notfind);
	}
	
	/**
	 * 依据tableName获取对应的对应枚举
	 * @param clazz	已知的数据库表名(tableName)
	 * @return	返回对应的枚举
	 */
	public static Tables getTable(String tableName) {
		Tables ts[] = Tables.values();
		for(Tables t : ts) {
			if(t.getTableName().equals(tableName)) {
				return t;
			}
		}
		return TESTTABLE1;
	}
	
	/** 获取表名 */
	public String getTableName() {
		return tableName;
	}
}
