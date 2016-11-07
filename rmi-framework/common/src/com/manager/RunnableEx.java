package com.manager;

/** 扩展Runnable的run */
public interface RunnableEx {
	
	/***
	 * 执行体
	 * @param execTime
	 * @return
	 */
	public Object run(Long execTime) throws Exception;
}
