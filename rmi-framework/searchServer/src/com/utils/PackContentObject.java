package com.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import com.bean.BaseBean;

/**
 * 不同对象之间转换
 * 
 * @author JLC From liutime.com
 * @version 1.0
 * @date 2014-7-26
 */
public class PackContentObject {

	/**
	 * 转换内容对象为Document对象
	 */
	public static Document convertContentToDoc(Object obj) {
		Document doc = new Document();
		Map<String, Object> attrMap = null;
		try {
			attrMap = ObjectUtil.getNotNullFields(obj, false);
			attrMap.remove("serialVersionUID");
		} catch (Exception e) {
			LoggerUtil.error(PackContentObject.class,e);
		}
		Set<String> keys = attrMap.keySet();
		if (keys != null && keys.size() > 0) {
			for (String key : keys) {
				Object val = attrMap.get(key);
				if (StringUtil.isEmpty(val)) {
					continue;
				}
				// 增加数据做索引字段
				if (val instanceof String) {
					// Field.Store.YES 该字段不仅分词而且要存储
					doc.add(new TextField(key, val.toString(), Field.Store.YES));
				} else if(!(val instanceof BaseBean)) {
					doc.add(new TextField(key, StringUtil.toString(val), Field.Store.YES));
				} 
				// else if(val instanceof Integer) {
				// doc.add(new IntField(key, (Integer)val, Field.Store.YES));
				// } else if(val instanceof Long) {
				// doc.add(new LongField(key, (Long)val, Field.Store.YES));
				// } else if(val instanceof Double) {
				// doc.add(new DoubleField(key, (Double)val, Field.Store.YES));
				// } else if(val instanceof Float) {
				// doc.add(new FloatField(key, (Float)val, Field.Store.YES));
				// }
			}
		}
		doc.add(new TextField("class", obj.getClass().getName(), Field.Store.YES));
		return doc;
	}

	/**
	 * 转换Documnet对象为内容对象
	 * @param doc 索引对应的内容
	 * @param score	相似度
	 * @return 转换后的对象
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Object convertDocToContent(Document doc, Float score) throws Exception {
		String className = doc.get("class");
		Map<String, Class> attrMap = ObjectUtil.getFieldNames(ObjectUtil
				.getClassByName(className));
		attrMap.remove("serialVersionUID");
		Set<String> attrNames = attrMap.keySet();
		Map<String, Object> objContext = new HashMap<String, Object>();
		if (attrNames != null && attrNames.size() > 0) {
			for (String attrName : attrNames) {
				Class type = attrMap.get(attrName);
				String value = doc.get(attrName);
				if(value != null) {
					objContext.put(attrName, ObjectUtil.parseToObject(value, type));
				}
			}
		}
		Object obj = ObjectUtil.initObject(ObjectUtil.getClassByName(doc.get("class")), objContext);
		ObjectUtil.setAttribute(obj, "score", score);
		return obj;
	}
}
