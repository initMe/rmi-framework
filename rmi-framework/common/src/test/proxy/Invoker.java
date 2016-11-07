package test.proxy;

import java.lang.reflect.Field;

public class Invoker {
	public Invoker() {
		TestBean1 tb = new TestBean1(){
			public String testStr = "123";
		};
		try {
			Field f = tb.getClass().getDeclaredField("testStr");
			System.out.println(f.get(tb));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Invoker();
	}
}
