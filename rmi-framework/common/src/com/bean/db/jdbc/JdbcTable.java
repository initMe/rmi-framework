package com.bean.db.jdbc;

import com.bean.BaseBean;
import com.dao.jdbc.Tables;

/** JDBC中数据库表对应的javaBean */
public abstract class JdbcTable extends BaseBean {
	private static final long serialVersionUID = -8133832981206395645L;
	/** 获取表类型 */
	public abstract Tables getTableType();
}
