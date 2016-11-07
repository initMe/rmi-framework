/**   
 * Copyright (c) 版权所有 2010-2015
 * 产品名：   
 * 包名：com.manager   
 * 文件名：MongoServer.java   
 * 版本信息：   
 * 创建日期：2015-4-23-上午11:42:57
 *    
 */
package com.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import com.bean.mongo.ConditionBean;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoOptions;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.util.MongoUtils;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;
import com.manager.GroupParams;

@SuppressWarnings("deprecation")
public class MongoServer {

	private static Mongo mongo = null;
	private static DB db = null;
	private static MongoClient mongoClient =null;
	private static MongoDatabase database=null;

	public MongoServer() {
		ConfigUtil cu = ConfigUtil.getInstance();
		String host = cu.getMongoDBHost(); // IP
		LoggerUtil.info(this.getClass(), "mongodb-ip : " + host);
		int port = cu.getMongoDBPort(); // 端口
		 String dbName = cu.getMongoDBName();
		int poolSize = cu.getMongoConnecPerHost(); // 线程池数量
		int blockSize = cu.getMongoConnectionMultiplier(); // 最大线程阻塞数量
		int connectTimeOut = cu.getMongoConnectTimeOut(); // 连接超时时间
		int socketTimeOut = cu.getMongoSocketTimeOut(); // socket超时时间设置
		try {
			mongo = new Mongo(host, port);
			mongoClient = new MongoClient(host, port);
			database = mongoClient.getDatabase(dbName);
			db = mongo.getDB(dbName);
			MongoOptions ops = mongo.getMongoOptions();
			ops.connectionsPerHost = poolSize;
			ops.threadsAllowedToBlockForConnectionMultiplier = blockSize;
			ops.connectTimeout = connectTimeOut;
			ops.socketTimeout = socketTimeOut;
		} catch (Exception e) {
			LoggerUtil.error(MongoServer.class, e);
		}
	}

	/**
	 * add (添加)
	 * 
	 * @param obj      实体对象
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static void add(Object obj) throws Exception {
		DBCollection docs = db.getCollection(obj.getClass().getName());
		DBObject dbObj = MongoUtils.objToDBObj(obj);
		add(docs, dbObj);
	}

	/**
	 * add (添加)
	 * 
	 * @param clazz
	 * @param map 封装了参数的map对象
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static void add(Class<?> clazz, Map<String, Object> map) throws Exception {
		DBCollection docs = db.getCollection(clazz.getName());
		BasicDBObject dbObj = new BasicDBObject();
		dbObj.putAll(map);
		add(docs, dbObj);
	}

	private static void add(DBCollection docs, DBObject obj) throws Exception {
		docs.save(obj);
	}

	/**
	 * delete (删除)
	 * 
	 * @param clazz
	 * @param map     封装了参数的map对象
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static void delete(Class<?> clazz, BasicDBObject param) throws Exception {
		DBCollection docs = db.getCollection(clazz.getName());
		docs.remove(param);
	}

	/**
	 * delete (删除)
	 * 
	 * @param clazz
	 * @param map   封装了参数的map对象
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static void delete(Class<?> clazz, Map<String, Object> map) throws Exception {
		DBCollection docs = db.getCollection(clazz.getName());
		BasicDBObject dbObj = new BasicDBObject(map);
		docs.remove(dbObj);
	}

	/**
	 * deleteAll (删除所有数据)
	 * 
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static void deleteAll(Class<?> clazz) throws Exception {
		DBCollection docs = db.getCollection(clazz.getName());
		BasicDBObject query = new BasicDBObject("_id", new BasicDBObject("$ne", 0));
		docs.remove(query);
	}

	/**
	 * modify (修改)
	 * 
	 * @param queryMap       查询参数
	 * @param updateMap      封装了参数的map对象，用于修改数据
	 * @since 1.0.0
	 */
	public static void modify(QueryParams queryMap, Map<String, Object> updateMap) throws Exception {
		DBCollection docs = db.getCollection(queryMap.getClazz().getName());
		BasicDBObject query = new BasicDBObject(queryMap);
		BasicDBObject update = new BasicDBObject();
		update.put("$set", updateMap);
		docs.update(query, update);

	}

	/**
	 * findOne (查询数据，适用于单条数据的查询)
	 * 
	 * @param obj 实体对象
	 * @return
	 * @since 1.0.0
	 */
	public static DBObject findOne(Object obj) throws Exception {
		DBCollection docs = db.getCollection(obj.getClass().getName());
		DBObject query = MongoUtils.objToDBObj(obj);
		return docs.findOne(query);
	}

	// public static MapReduceOutput mapReduce(QueryParams queryMap) throws Exception{
	// DBCollection docs = db.getCollection(queryMap.getClazz().getName());
	// String map ="function(){emit(this.name,{count:1});}";
	// String reduce = "";
	// MapReduceOutput output = docs.mapReduce(map, reduce, null, new BasicDBObject(queryMap));
	//
	// return null;
	// }

	/**
	 * findOne (查询数据，适用于单条数据的查询)
	 * 
	 * @param queryMap   查询参数
	 * @param writeMap 指定返回字段
	 * @return
	 * @throws Exception
	 * @since 1.0.0
	 */
	public static DBObject findOne(QueryParams queryMap, QueryParams writeMap) throws Exception {
		DBCollection docs = db.getCollection(queryMap.getClazz().getName());
		BasicDBObject query = new BasicDBObject(queryMap);
		DBObject retObj = null;
		if (writeMap == null) {
			retObj = docs.findOne(query);
		} else {
			BasicDBObject write = new BasicDBObject(writeMap);
			retObj = docs.findOne(query, write);
		}
		return retObj;
	}

	/**
	 * find (数据查询，支持分页和排序，查询所有字段)
	 * 
	 * @param queryMap查询map
	 * @param condition
	 * @return
	 * @since 1.0.0
	 */
	public static DBCursor find(QueryParams queryMap) throws Exception {
		DBCollection docs = db.getCollection(queryMap.getClazz().getName());
		BasicDBObject query = new BasicDBObject(queryMap);
		return cursorPreparer(docs, query, null, queryMap.getCondition());
	}

	/**
	 * find (数据查询，支持分页和排序,指定了返回字段)
	 * 
	 * @param queryMap	查询map
	 * @param writeMap	指定返回字段
	 * @param condition
	 * @return
	 * @since 1.0.0
	 */
	public static <T> DBCursor find(QueryParams queryMap, QueryParams writeMap) throws Exception {
		DBCollection docs = db.getCollection(queryMap.getClazz().getName());
		BasicDBObject query = new BasicDBObject(queryMap);
		BasicDBObject write = new BasicDBObject(writeMap);
		return cursorPreparer(docs, query, write, queryMap.getCondition());
	}
	/**
	 * 聚合查询(mongodb-driver3.2)
	 * @param queryParams	查询对象
	 * @param groupParams	分组对象
	 * @return	返回结果集合(分组数据，聚合数据等) 
	 * @see GroupParams getResultKey()
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> findGroup3_2(QueryParams queryParams, GroupParams groupParams) {
		// 聚合查询对象list
		List<BasicDBObject> dbObjectList = new ArrayList<BasicDBObject>();
		// 查询对象
		BasicDBObject matchObject = new BasicDBObject();
		//聚合对象
		BasicDBObject groupObject = new BasicDBObject();
		matchObject.put("$match", queryParams);
		groupObject.put("$group",groupParams);
		dbObjectList.add(matchObject);
		dbObjectList.add(groupObject);
		MongoCollection<Document> coll = database.getCollection(queryParams.getClazz().getName());
		AggregateIterable<Document> iterable = coll.aggregate(dbObjectList);
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		// 遍历结果集
		MongoCursor<Document> array = iterable.iterator();
		while(array.hasNext()) {
			Document doc = array.next();
			Map<String, Object> _id = (Map<String, Object>) doc.remove("_id");
			doc.putAll(_id);
			resultList.add(new HashMap<String, Object>(doc));
		}
		return resultList;
	}
	/**
	 * 分组查询
	 * @param keyMap指定返回字段（返回字段需要初始值mongodb-diver3.0）
	 * @param condMap查询条件
	 * @return 符合条件的文档集合
	 */
	public static BasicDBList findGroup(QueryParams keyMap, QueryParams condMap) {
		DBCollection dbCollection = db.getCollection(keyMap.getClazz().getName());
		BasicDBObject key = new BasicDBObject(keyMap);
		BasicDBObject cond = new BasicDBObject(condMap);
		BasicDBObject initial = new BasicDBObject();
		//count为每组计算结果,会追加到结果集中
		initial.put("count",0);
		//执行函数count叠加
		String reduce = "function(doc,pre){pre.count++}";
		BasicDBList dbList = (BasicDBList) dbCollection.group(key, cond, initial, reduce);
		return dbList;
	}

	/**
	 * count (查询总数)
	 * 
	 * @param clazz
	 * @param map 封装了查询参数的map
	 * @return
	 * @since 1.0.0
	 */
	public static long count(Class<?> clazz, Map<String, Object> map) {
		DBCollection docs = db.getCollection(clazz.getName());
		BasicDBObject query = new BasicDBObject(map);
		return docs.count(query);
	}

	/**
	 * cursorPreparer (查询结果处理)
	 * 
	 * @param query
	 * @param condition
	 * @return
	 * @since 1.0.0
	 */
	private static DBCursor cursorPreparer(DBCollection docs, DBObject query, DBObject write, final ConditionBean condition) {
		CursorPreparer cursorPreparer = condition == null ? null : new CursorPreparer() {
			public DBCursor prepare(DBCursor cursor) {
				if (condition == null) {
					return cursor;
				}
				if (condition.getLimit() <= 0 && condition.getSkip() <= 0 && condition.getSortObject() == null) {
					return cursor;
				}
				DBCursor cursorToUse = cursor;
				if (condition.getSkip() > 0) {
					cursorToUse = cursorToUse.skip(condition.getSkip());
				}
				if (condition.getLimit() > 0) {
					cursorToUse = cursorToUse.limit(condition.getLimit());
				}
				if (condition.getSortObject() != null) {
					cursorToUse = cursorToUse.sort(new BasicDBObject(condition.getSortObject()));
				}
				return cursorToUse;
			}
		};
		DBCursor cursor = cursorPreparer(docs, query, write, cursorPreparer);
		return cursor;
	}

	/**
	 * cursorPreparer (查询结果处理)
	 * 
	 * @param query 查询参数
	 * @param write  返回列
	 * @param cursorPreparer
	 * @return
	 * @since 1.0.0
	 */
	private static DBCursor cursorPreparer(DBCollection docs, DBObject query, DBObject write, CursorPreparer cursorPreparer) {
		DBCursor dbCursor = null;
		if (write != null) {
			dbCursor = docs.find(query, write);
		} else {
			dbCursor = docs.find(query);
		}
		if (cursorPreparer != null) {
			dbCursor = cursorPreparer.prepare(dbCursor);
		}
		return dbCursor;
	}
}
