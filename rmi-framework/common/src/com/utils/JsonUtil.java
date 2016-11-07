package com.utils;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/** JSON帮助类 */
public class JsonUtil {
	
	/** 将JSON字符串转换为map */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jsonToMap(String json) {
		Map<String, Object> map = (Map<String, Object>) JSONObject.parse(json);
		return map;
	}
	/** 将obj转换成map */
	public static Map<String, Object> objToMap(Object obj) {
		return jsonToMap(objToJson(obj));
	}
	
	/** 将map转换成obj */
	public static <T>T mapToBean(Map<String, Object> map, Class<T> beanClass) {
//		return jsonToMap(objToJson(obj));
		return jsonToBean(objToJson(map), beanClass);
	}
	
	/** 字符串转json */
	public static String objToJson(Object obj) {
		return JSONObject.toJSONString(obj);
	}
	
	/** 将json字符串转换为对象 */
	public static <T> T jsonToBean(String jsonStr, Class<T> beanClass) {
		return (T) JSONObject.parseObject(jsonStr, beanClass);
	}
	
	/** 测试JSON格式是否正常 */
	public static boolean isJson(String json) {
    	try {
    		jsonToMap(json);
			return true;
		} catch (Exception e) {
			return false;
		}
    }
	
	public static void main(String[] args) {
		System.out.println(JsonUtil.isJson("{\"content\":\"eyJtc2ciOiLlj5F25LiN5LyaIiwicmVjZWl2ZVVzZXIiOnsiaWQiOjY4MCwiaXNUcnVlIjowLCJtb2JpbGUiOiIxMzExMTExMTExMSIsIm1vbmV5IjowLjAsIm5hbWUiOiIxMyoxMTExIiwib3JkZXJfc3VjY2Vzc19udW0iOjEsInB1c2hfc3RhdHVzIjowLCJzY29yZSI6Mywic2NvcmVfbnVtIjoxLCJ1c2VyU3RhdHVzQ29kZSI6Mn0sInNlbmRVc2VyIjp7ImlkIjo2NjYsImlzVHJ1ZSI6MCwibW9iaWxlIjoiMTMyMjIyMjIyMjIiLCJtb25leSI6MC4wLCJuYW1lIjoiMjIyMiIsInB1c2hfc3RhdHVzIjowLCJzY29yZSI6NSwic2NvcmVfbnVtIjoxLCJzZXJ2ZXJfdGFnIjoi5Yek6aOe6aOeLCDkv67nlLXohJEiLCJ1c2VyU3RhdHVzQ29kZSI6MX19\",\"countUnRead\":0,\"functionNo\":101,\"otherId\":680,\"otherType\":0,\"senderUserId\":666,\"time\":1446626598795,\"type\":0,\"unRead\":0}"));
	}
}
