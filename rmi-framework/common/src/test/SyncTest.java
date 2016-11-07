package test;

import com.pool.LockPool;

public class SyncTest {
	private int value = 0;
	public SyncTest() {
		for(int i=0; i<100000; i++) {
			new Thread(new Runnable() {
				public void run() {
					LockPool.getInstance().doSynchronized("a", new Runnable() {
						@Override
						public void run() {
							value ++;
						}
					});
				}
			}).start();
		}
		try {
			Thread.sleep(1000);
			System.out.println(value);
			System.out.println(LockPool.getInstance().getLockSize());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		new SyncTest();
	}
}
