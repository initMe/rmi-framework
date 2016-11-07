package com.pay.system.util;

import java.util.Observable;

/**
 * 用户信息观察者
 * @ClassName: UserObserver
 * @author: channel
 * @date: 2015-12-22 下午3:40:31
 */
public class ObjectObserver extends Observable {
	static ObjectObserver ob;

	private ObjectObserver() {}
	public static ObjectObserver getObserver() {
		if (null == ob) {
			synchronized (ObjectObserver.class) {
				if (null == ob) {
					ob = new ObjectObserver();
				}
			}
		}
		return ob;
	}
	/**
	 * 修改事件
	 * @param	被修改后的对象
	 */
	public void updateUserEvent(Object obj){
		setChanged();//设置信息有更新
		notifyObservers(obj);//通知监听者
	}
}
