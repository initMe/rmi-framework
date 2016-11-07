package com.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.bean.Student;
import com.bean.mongo.GroupOperator;
import com.manager.MongoServer;
import com.manager.QueryParams;
import com.manager.GroupParams;

public class MongoTest {
	public static void main(String[] args) throws Exception {
		Student student=new Student();
//		student.setSex("男");
//		student.setId(2L);
		QueryParams queryParams=new QueryParams(student);
		List<String> list=new ArrayList<String>();
		list.add("sex");
		list.add("id");
		GroupParams groupParams = new GroupParams(list);
		groupParams.appendGroupOperator(GroupOperator.sum,"id");
		groupParams.appendGroupOperator(GroupOperator.avg,"id");
		groupParams.appendGroupOperator(GroupOperator.avg,"history");
		groupParams.appendGroupOperator(GroupOperator.sum,"history");
		List<Map<String, Object>> documentList = new MongoServer().findGroup3_2(queryParams, groupParams);
		for(Map<String, Object> doc:documentList){
			System.out.println(doc);
			
		}
	}
}
