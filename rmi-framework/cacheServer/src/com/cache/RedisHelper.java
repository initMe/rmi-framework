package com.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

import redis.clients.jedis.Jedis;

public class RedisHelper {
	private static Jedis jedis = null;
	static {
		init();
	}
	
	private static void init() {
		ConfigUtil config = ConfigUtil.getInstance();
		// 连接redis服务   
        jedis = new Jedis(config.getCacheIp(), config.getCacheProt(), 0);  
        // 密码验证-如果你没有设置redis密码可不验证即可使用相关命令   
//    	jedis.auth(SystemInfo.RADIS_PASS); 
	}
	
	private RedisHelper(){}
	
	public static void main(String[] args) {
//		new RedisHelper().delSetValue(SystemInfo.ALLSERVER, "192.168.1.93");
//		Set a = new RedisHelper().getSet(SystemContext.ALLSERVER);
//		RedisHelper.set(SystemContext.SERVERCLIENTNUM+"10.132.30.82", "90000");
//		System.out.println(a);
	}
	
	/** 测试连接是否断开，如果断开，则重新连接 */
	private static void testConnect() {
		try {
			jedis.get("testConnect");
		} catch (Exception e) {
			LoggerUtil.error(RedisHelper.class,"连接断开，重新连接", e);
			init();
		}
	}
	
	/** 存储键值 */
	public static void set(String key, String value) {
		testConnect();
		jedis.set(key, value);
	}
	
	/** 对键内已有值后追加值 */
	public static void append(String key, String value) {
		testConnect();
		jedis.append(key, value);
	}
	
	/** 删除键值 */
	public static void del(String... keys) {
		testConnect();
		jedis.del(keys);
	}
	
	/** 获取单个值 */
	public static String get(String key) {
		testConnect();
		return jedis.get(key);
	}
	
	/** 返回多个key对应的值，以list存储 */
	public static List<String> get(String... keys) {
		if(keys != null) {
			testConnect();
			return jedis.mget(keys);
		}
		return new ArrayList<String>();
	}
	
	/** 存储map */
	public static void setMap(String key, Map<String, String> valueMap) {
		testConnect();
		jedis.hmset(key, valueMap);
	}
	
	/** 返回map */
	public static Map<String, String> getMap(String key) {
		testConnect();
		return jedis.hgetAll(key);
	}
	
	/** 删除map值内的其中一个键 */
	public static void delMapKey(String mapKey, String... fieldKey) {
		testConnect();
		jedis.hdel(mapKey, fieldKey);
	}
	
	/** 删除map的所有值 */
	public static void delMap(String mapKey) {
		testConnect();
		Map<String, String> infoMap = getMap(mapKey);
		if(infoMap!=null && infoMap.size()>0) {
			Set<String> keys = infoMap.keySet();
			for(String key : keys) {
				RedisHelper.delMapKey(mapKey, key);
			}
		}
	}
	
	/** key对应的list中添加值 */
	public static void addList(String key, String... values) {
		testConnect();
		jedis.lpush(key, values);
	}
	
	/** 获取key对应的list */
	public static List<String> getList(String key) {
		testConnect();
		return jedis.lrange(key, 0, -1);
	}
	
	/** 删除List所有值 */
	public static void delList(String key) {
		testConnect();
		jedis.ltrim(key, 0, 0);
	}
	
	/** key对应的Set中添加值 */
	public static void addSet(String key, String... values) {
		testConnect();
		jedis.sadd(key, values);
	}
	
	/** 删除set的其中一个值 */
	public static void delSetValue(String key, String... values) {
		testConnect();
		jedis.srem(key, values);
	}
	
	/** 获取key对应的set */
	public static Set<String> getSet(String key) {
		testConnect();
		return jedis.smembers(key);
	}
	
	/** 删除set中所有的值 */
	public static void delSet(String key) {
		testConnect();
		Set<String> set = getSet(key);
		if(set!=null && set.size()>0) {
			for(String value : set) {
				delSetValue(key, value);
			}
		}
	}
	
	/** 获取Redis操作对象 */
	public static Jedis getJedisObject() {
		testConnect();
		return jedis;
	}
}
