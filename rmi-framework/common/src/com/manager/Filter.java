package com.manager;

import java.io.Serializable;

/** 过滤器 */
public interface Filter<T> extends Serializable {
	/**
	 * 执行过滤
	 * @param objs	业务参数
	 * @return	验证是否可以通过(true:通过，false:不通过)
	 */
	public boolean doFilter(T obj);
}
