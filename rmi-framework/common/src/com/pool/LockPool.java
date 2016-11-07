package com.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.manager.RunnableEx;
import com.utils.LoggerUtil;

/***
 * 同步锁池
 * (针对要同步的对象，同时发生时，不能保证两对象相等)
 * (或常规synchronized或wait不能有效锁定，如：从缓存中拿出的数据)
 * @author F12_end
 */
public class LockPool {
	/** 锁定池 */
	private Map<String, LockObject> lockPool = Collections.synchronizedMap(new HashMap<String, LockObject>());
	
	/** 单例 */
	private static LockPool lp = null;
	private static Object instanceLock = new Object();
	private LockPool() {}
	/** 获取单例 */
	public static LockPool getInstance() {
		if(lp == null) {
			synchronized (instanceLock) {
				if(lp == null) {
					lp = new LockPool();
				}
			}
		}
		return lp;
	}
	
	/**
	 * 新增一个同步任务
	 * @param flag	同步的标记
	 * @param runBody	执行体
	 */
	public void doSynchronized(String flag, Runnable runBody) {
		LockObject lock = null;
		synchronized (this) {
			lock = lockPool.get(flag);
			if(lock == null) {
				lock = new LockObject(flag);
				lockPool.put(flag, lock);
			} else {
				lock.setLastTime(System.currentTimeMillis());
			}
		}
		synchronized (lock) {
			runBody.run();
		}
	}
	
	/***
	 * 新增一个同步任务
	 * @param flag	同步的标记
	 * @param runBody	执行体
	 * @return 执行体的返回值
	 * @throws Exception 执行体的异常
	 */
	public Object doSynchronized(String flag, RunnableEx runBody) throws Exception {
		LockObject lock = null;
		synchronized (this) {
			lock = lockPool.get(flag);
			if(lock == null) {
				lock = new LockObject(flag);
				lockPool.put(flag, lock);
			} else {
				lock.setLastTime(System.currentTimeMillis());
			}
		}
		synchronized (lock) {
			return runBody.run(System.currentTimeMillis());
		}
	}
	/***
	 * 新增一个锁定
	 * @param flag	锁对应的标记
	 */
	public void doLock(String flag) {
		LockObject lock = lockPool.get(flag);
		if(lock == null) {
			lock = new LockObject(flag);
		} else {
			lock.setLastTime(System.currentTimeMillis());
		}
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				LoggerUtil.error(this.getClass(), e);
			}
		}
	}
	
	/***
	 * 解锁一个锁定
	 * @param flag	锁对应的标记
	 */
	public void unLock(String flag) {
		LockObject lock = lockPool.get(flag);
		if(lock == null) {
			lock = new LockObject(flag);
		} else {
			lock.setLastTime(System.currentTimeMillis());
		}
		synchronized (lock) {
			lock.notify();
		}
	}
	
	/***
	 * 解锁标记上的所有锁定
	 * @param flag	锁对应的标记
	 */
	public void unLockAll(String flag) {
		LockObject lock = lockPool.get(flag);
		if(lock == null) {
			lock = new LockObject(flag);
		} else {
			lock.setLastTime(System.currentTimeMillis());
		}
		synchronized (lock) {
			lock.notifyAll();
			lockPool.remove(flag);
		}
	}
	/** 获取锁个数 */
	public int getLockSize() {
		return lockPool.size();
	}
	
	/** 锁对象 */
	class LockObject {
		private String flag;
		private Long lastTime = System.currentTimeMillis();
		public LockObject(String flag) {
			this.flag = flag;
		}
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public Long getLastTime() {
			return lastTime;
		}
		public void setLastTime(Long lastTime) {
			this.lastTime = lastTime;
		}
	}
}
