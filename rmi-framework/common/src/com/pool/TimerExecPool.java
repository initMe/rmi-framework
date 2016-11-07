package com.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bean.utils.TimerBean;
import com.utils.BeanSort;
import com.utils.LoggerUtil;

/** 定时任务管理器 */
public class TimerExecPool {
	/** 要处理的定时任务集合 */ 
	protected List<TimerBean> list = Collections.synchronizedList(new ArrayList<TimerBean>());
	/** key对应任务 */
	protected Map<Object, TimerBean> keyMap = new HashMap<Object, TimerBean>();
	/** 新增定时器是否是有序的 */
	protected boolean isSort = true;
	/** 设定对象锁 */
	private Object lock = new Object();

	/** 提供一个缺省的定时任务管理器(无序的) */
	public static TimerExecPool defaultNotSortPool = new TimerExecPool(false);
	
	/** 创建一个有序的定时任务管理器 */
	public TimerExecPool() {
		this(true);
	}
	
	/**
	 * 生成一个定时任务管理器
	 * @param isSort	向管理器中新增定时任务是否按执行时间有序的(将决定最终线程执行的方式与效率)(true:有序)
	 * @return	新的定时任务管理器
	 */
	public TimerExecPool(boolean isSort) {
		this.isSort = isSort;
		runThread();
	}
	
	/**
	 * 新增定时任务
	 * @param execTime	预定执行的时间(不是间隔时间，是时间点的毫秒数)
	 * @param tasker	任务
	 * @param key	任务对应的唯一key(用于防止设定重复任务，覆盖曾经的同key任务)
	 */
	public void addTimer(long execTime, Runnable tasker, Object key) {
		if(containsKey(key)) {
			LoggerUtil.error(this.getClass(), "发现添加重复的定时器，key："+key);
		} else {
			TimerBean tb = new TimerBean(execTime, tasker, key);
			addTimer(tb);
		}
	}
	
	/**
	 * 新增定时任务
	 * @param tb	定时任务
	 */
	public void addTimer(TimerBean tb) {
		list.add(tb);
		if(tb.getKey() != null) {
			keyMap.put(tb.getKey(), tb);
		}
		// 如果设置为无序的话，则需进行排序
		if(!isSort) {
			BeanSort<TimerBean> bs = new BeanSort<TimerBean>("doTime");
			bs.sortList(list);
		}
		// 如果新增对象为更早的时间相应，则告知线程重新处理首个元素
		synchronized (lock) {
			if(list.indexOf(tb) == 0) {
				lock.notify();
			}
		}
	}
	
	/** 检查key是否已存在 */
	public boolean containsKey(Object key) {
		return keyMap.containsKey(key);
	}
	
	public void removeTimer(Object key) {
		TimerBean tb = keyMap.remove(key);
		// 如果新增对象为更早的时间相应，则告知线程重新处理首个元素
		synchronized (lock) {
			list.remove(tb);
		}
	}
	
	/** 执行处理线程 */
	private void runThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					synchronized (lock) {
						while(list.size() > 0) {
							try {
								long nowTime = System.currentTimeMillis();
								TimerBean tb = null;
								// 定时
								long time = list.get(0).getDoTime()-nowTime;
								if(time > 0) {
									lock.wait(time);
								}
								if(list.size() == 0) {
									continue;
								}
								synchronized (list) {
									tb = list.get(0);
									// 如果时间未到则重新定时处理(防止add方法新增一个更早时间任务的notify掉之前的定时)
									if(tb.getDoTime() > System.currentTimeMillis()) {
										continue;
									}
									tb = list.remove(0);
									keyMap.remove(tb.getKey());
									// 执行任务
									tb.getTasker().run();
									LoggerUtil.info(this.getClass(), "执行定时任务");
								}
							} catch (Exception e) {
								LoggerUtil.error(this.getClass(), e);
							}
						}
						try {
							lock.wait();
						} catch (InterruptedException e) {
							LoggerUtil.error(this.getClass(), e);
						}
					}
				}
			}
		}).start();
	}
	
	public void test() {
		final Object lock = new Object();
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						System.out.println(1);
						Thread.sleep(5000);
						System.out.println("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					try {
						Thread.sleep(1000);
						System.out.println(2);
						Thread.sleep(5000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	public static void main(String[] args) {
		new TimerExecPool(true).test();
	}
}
