package com.bean.utils;

import com.bean.BaseBean;

/** 定时执行bean */
public class TimerBean extends BaseBean {
	private static final long serialVersionUID = 5075327467295969424L;
	
	/** 执行事件的时间 */
	private Long doTime;
	/** 要执行的事件 */
	private Runnable tasker;
	/** 对应的唯一索引 */
	private Object key;
	
	public TimerBean() {}
	
	public TimerBean(Long doTime, Runnable tasker) {
		this.doTime = doTime;
		this.tasker = tasker;
	}
	
	public TimerBean(Long doTime, Runnable tasker, Object key) {
		this(doTime, tasker);
		this.key = key;
	}
	
	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Long getDoTime() {
		return doTime;
	}
	public void setDoTime(Long doTime) {
		this.doTime = doTime;
	}
	public Runnable getTasker() {
		return tasker;
	}
	public void setTasker(Runnable tasker) {
		this.tasker = tasker;
	}
}
