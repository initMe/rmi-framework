package com.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.context.Context;
import com.utils.LoggerUtil;

/** 对象池 */
public class ObjectPool {
	/** 对象池容器 [sessionId, 存储对象] */
	private final Map<String, Object> pool = Collections.synchronizedMap(new HashMap<String, Object>());
	/** 池的超时监听 */
	private final Map<String, Long> poolListener = Collections.synchronizedMap(new HashMap<String, Long>());
	/** 单例 */
	private static ObjectPool op = null;
	private static Object lock = new Object();
	private ObjectPool() {
		listene();
	}
	/** 监听对象池的线程 */
	private void listene() {
		new Thread(new Runnable() {
			public void run() {
				List<String> removeList = new ArrayList<String>();
				while(true) {
					Long nowTime = System.currentTimeMillis();
					synchronized (poolListener) {
						Set<String> keys = poolListener.keySet();
						// 检测超时对象
						for(String key : keys) {
							Long updateTime = poolListener.get(key);
							if(nowTime-updateTime >= Context.pool_object_timeout) {
								removeList.add(key);
							}
						}
						// 开始移除掉超时的对象
						for(String key : removeList) {
							synchronized (pool) {
								pool.remove(key);
							}
							synchronized (poolListener) {
								poolListener.remove(key);
							}
						}
					}
					removeList.clear();
					try {
						// 计算精确的时间
						Long sleepTime = Context.pool_object_checkTime-(System.currentTimeMillis()-nowTime);
						Thread.sleep(sleepTime>0?sleepTime:0);
					} catch (InterruptedException e) {
						LoggerUtil.error(this.getClass(), e);
//						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	/** 获取连接池对象 */
	public static ObjectPool getInstance() {
		if(op == null) {
			synchronized (lock) {
				if(op == null) {
					op = new ObjectPool();
				}
			}
		}
		return op;
	}
	
	/**
	 * 向对象池中新增对象
	 * @param obj	对象
	 * @return	该对象对应的sessionId
	 */
	public String addObject(Object obj) {
		String sessionId = UUID.randomUUID().toString();
		updateObject(sessionId, obj);
		return sessionId;
	}
	
	/**
	 * 从对象池中取对象
	 * @param sessionId	与此sessionId对应的对象
	 * @return 存储的对象
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> T getObject(String sessionId) {
		if(!pool.containsKey(sessionId)) {
			return null;
		}
		Object obj = pool.get(sessionId);
		refresh(sessionId);
		return (T) obj;
	}
	
	/**
	 * 更新对象池
	 * (适用于对象被重新实例化)
	 * @param sessionId	对象sessionId
	 * @param obj	存储的对象
	 */
	public synchronized void updateObject(String sessionId, Object obj) {
		pool.put(sessionId, obj);
		refresh(sessionId);
	}
	
	/** 刷新对象池 */
	public synchronized void refresh(String sessionId) {
		poolListener.put(sessionId, System.currentTimeMillis());
	}
}
