package com.manager;

/** 回调器 */
public interface Callback {
	
	/***
	 * 回调方法
	 * @param params	参数集合(可为null)
	 * @return	返回(可为null)
	 */
	public Object doTask(Object... params) throws Exception;
}
