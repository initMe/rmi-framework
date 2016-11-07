package com.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bean.mongo.GroupOperator;

/** 分组查询组装类 */
public class GroupParams extends HashMap<String, Object> {
	private static final long serialVersionUID = 1622316409364972089L;
	
	/**
	 * 创建分组查询
	 * @param groupByFieldList	要分组的字段
	 */
	public GroupParams(List<String> groupByFieldList) {
		Map<String, String> fieldMap = new HashMap<String, String>();
		for (String str : groupByFieldList) {
			fieldMap.put(str, "$" + str);
		}
		// 用来分组的字段
		put("_id", fieldMap);
		// count属性用来计算每个分组的总条数
		Map<String, Object> count = new HashMap<String, Object>();
		count.put("$sum", 1);
		put("count", count);
	}
	
	/**
	 * 创建分组查询
	 * @param groupByFields	要分组的字段
	 */
	public GroupParams(String... groupByFields) {
		this(Arrays.asList(groupByFields));
	}
	
	/**
	 * 添加操作
	 * (查询返回的结果集的key为 operatorField+"_"+groupOperator.getMongoCode())
	 * @param groupOperator 需要的操作($sum,$avg,$max,$min,$last,$first)
	 * @param operatorField 指定操作对应的数据字段
	 * @return	组装后的查询条件(目前是this自己)
	 */
	public GroupParams appendGroupOperator(GroupOperator oper, String field){
		Map<String, String> mapOperator = new HashMap<String, String>();
		mapOperator.put(oper.getMongoCode(), "$" + field);
		put(field+"_"+oper, mapOperator);
		return this;
	}
	
	/**
	 * 获取返回对象里的聚合字段的key
	 * @param oper	操作符
	 * @param field	字段
	 * @return
	 */
	public static String getResultKey(GroupOperator oper, String field) {
		return field+"_"+oper.getMongoCode().substring(1);
	}

//	public static void main(String[] args) {
//		List<String> groupByFiledList=new ArrayList<String>();
//		groupByFiledList.add("a");
//		groupByFiledList.add("b");
//		groupByFiledList.add("c");
//		groupByFiledList.add("d");
//		GroupParams groupParams=getInstance(groupByFiledList);
//		groupParams.appendGroupOperator(GroupOperator.min,"id");
//		groupParams.appendGroupOperator(GroupOperator.min,"history");
//		System.out.println(JsonUtil.objToJson(groupParams));
//	}
}
