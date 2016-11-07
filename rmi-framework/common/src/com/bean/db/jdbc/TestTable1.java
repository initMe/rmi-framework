package com.bean.db.jdbc;

import com.dao.jdbc.Tables;

/** 测试类 */
public class TestTable1 extends JdbcTable {
	private static final long serialVersionUID = -2755636716145142023L;

	@Override
	public Tables getTableType() {
		return Tables.TESTTABLE1;
	}
}
