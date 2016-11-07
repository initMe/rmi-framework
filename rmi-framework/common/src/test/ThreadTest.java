package test;

public class ThreadTest {
	private Thread parant = null;
	private Thread t = new Thread(new Runnable() {
		public void run() {
			System.out.println("支线程启动!");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("支线程退出!");
		}
	});
	
	public ThreadTest() {
		t.start();
		parant = new Thread(new Runnable() {
			public void run() {
				try {
					t.join();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("父线程启动!");
				System.out.println("父线程执行完毕!");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("父线程退出!");
				parant.start();
			}
		});
		parant.start();
	}
	public static void main(String[] args) {
		new ThreadTest();
	}
}
