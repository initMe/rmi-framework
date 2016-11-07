package com.manager;

import java.io.Serializable;

/** 任务接口 */
public interface Tasker extends Serializable {
	/** 执行一个任务 */
	public Object doTask(Object... objs) throws Exception;
}
