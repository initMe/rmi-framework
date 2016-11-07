package com.main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThreadTest {
	Object lock1 = new Object();
	Object lock2 = new Object();
	public ThreadTest() {
//		thread();
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		map.put(1, 1);
		map.put(2, 2);
		Set<Integer> set = map.keySet();
		for(Integer key : set) {
			map.remove(key);
		}
		
	}
	@SuppressWarnings("unused")
	private void thread() {
		new Thread(new Runnable() {
			public void run() {
				synchronized (lock1) {
					try {
						System.out.println("lock2_begin");
						Thread.sleep(5000);
						System.out.println("lock2_end");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				synchronized (lock1) {
					int i=0;
					while(true) {
//						try {
//							System.out.println("lock1_begin");
//							lock1.wait(5000);
//							System.out.println("lock1_end");
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						System.out.println(++i);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
	public static void main(String[] args) {
		new ThreadTest();
	}
}
