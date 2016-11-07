package com.pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.utils.LoggerUtil;

/** 线程池 */
public class ThreadPool {
	/** 线程池大小 */
	private int maxPoolNum = 0;
	/** 线程超时清理间隔 */
	private long keepAliveTime = 0;
	/** 每次开始执行附加任务 */
	private Runnable onceBefore;
	/** 每次执行完附加任务 */
	private Runnable onceEnd;
	
	/** 要执行的任务集合 */
	private LinkedList<Runnable> taskList = new LinkedList<Runnable>();
	/** 执行的线程集合 */
	private List<ThreadBean> threadList = Collections.synchronizedList(new ArrayList<ThreadBean>());
	
	/** 提供一个缺省的线程池(最大200线程，超时清理为30分钟) */
	public static ThreadPool defaultPool = new ThreadPool(200, 1000*60*30);
	
	/**
	 * 创建一个线程池
	 * @param maxPoolNum	线程池中最大线程数(0:表示不限最大值)
	 * @param keepAliveTime	空闲线程被清理的间隔时间(0:表示不清理)
	 * @param onceBefore	每次开始执行时附加的任务
	 * @param onceEnd	每次执行完后附加的任务
	 */
	public ThreadPool(int maxPoolNum, long keepAliveTime, Runnable onceBefore, Runnable onceEnd) {
		this.maxPoolNum = maxPoolNum;
		this.keepAliveTime = keepAliveTime;
		this.onceBefore = onceBefore;
		this.onceEnd = onceEnd;
	}
	/**
	 * 创建一个线程池
	 * @param maxPoolNum	线程池中最大线程数(0:表示不限最大值)
	 * @param keepAliveTime	空闲线程被清理的间隔时间(0:表示不清理)
	 */
	public ThreadPool(int maxPoolNum, long keepAliveTime) {
		this(maxPoolNum, keepAliveTime, null, null);
	}
	/**
	 * 创建一个无限容量、永不超时的线程池
	 * @param maxPoolNum	线程池中最大线程数(0:表示不限最大值)
	 * @param keepAliveTime	空闲线程被清理的间隔时间(0:表示不清理)
	 */
	public ThreadPool() {}
	
	/**
	 * 新增任务
	 * @param tasker	任务详情
	 */
	public void addTasker(Runnable tasker) {
		taskList.add(tasker);
		for(ThreadBean tb : threadList) {
			if(!tb.isUse()) {
				synchronized (tb) {
					tb.notify();
				}
				return;
			}
		}
		// 创建线程，为了防止多线程并发出现超出预订量，所以以同步限制
		synchronized (this) {
			if(maxPoolNum>0 && maxPoolNum>threadList.size()) {
				createThread();
			}
		}
	}
	/** 创建一个线程 */
	private void createThread() {
		final ThreadBean tb = new ThreadBean();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					// 从数据集合中获取元素并执行
					Runnable tasker = null;
					while((tasker=taskList.poll()) != null) {
						if(onceBefore != null) {
							onceBefore.run();
						}
						tasker.run();
						if(onceEnd != null) {
							onceEnd.run();
						}
						tb.setLastExec(System.currentTimeMillis());
					}
					// 执行结束后，标志位空闲并锁死自身
					synchronized (tb) {
						try {
							tb.setUse(false);
							tb.wait(keepAliveTime);
							// 重新设定为忙碌状态
							tb.setUse(true);
						} catch (Exception e) {
							LoggerUtil.error(this.getClass(), e);
						}
					}
					// 检查是否是因超时清理而解锁
					if(keepAliveTime>0 && (System.currentTimeMillis()-tb.getLastExec())>=keepAliveTime) {
						threadList.remove(tb);
						return;
					}
				}
			}
		});
		tb.setThread(thread);
		thread.start();
	}

	/** 执行线程的相关参数 */
	class ThreadBean {
		/** 最后一次执行时间 */
		private Long lastExec = System.currentTimeMillis();
		/** 是否正在被使用 */
		private boolean isUse = true;
		/** 执行的线程 */
		private Thread thread;
		public Thread getThread() {
			return thread;
		}
		public void setThread(Thread thread) {
			this.thread = thread;
		}
		public Long getLastExec() {
			return lastExec;
		}
		public void setLastExec(Long lastExec) {
			this.lastExec = lastExec;
		}
		public boolean isUse() {
			return isUse;
		}
		public void setUse(boolean isUse) {
			this.isUse = isUse;
		}
	}
}