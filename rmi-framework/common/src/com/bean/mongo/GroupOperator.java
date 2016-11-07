package com.bean.mongo;

public enum GroupOperator {
	/**求和*/
	sum("$sum", "求和"),
	/**平均值*/
	avg("$avg", "平均值"),
	/**每组第一个文档的值*/
	first("$first", "每组第一个文档的值"),
	/**每组最后一个文档的值*/
	last("$last", "每组最后一个文档的值"),
	/**最大值*/
	max("$max", "最大值"),
	/**最小值*/
	min("$min", "最小值");
	
	private String mongoCode;
	private String sqlCode;
	
	GroupOperator(String mongoCode, String sqlCode) {
		this.mongoCode = mongoCode;
		this.sqlCode = sqlCode;
	}

	/** 获取mongodb编码值 */
	public String getMongoCode() {
		return mongoCode;
	}

	/** 获取类sql的阅读值 */
	public String getSqlCode() {
		return sqlCode;
	}

}
