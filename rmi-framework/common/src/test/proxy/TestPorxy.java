package test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestPorxy {
	
	public TestPorxy() {
		final ContextObj obj = new ContextObj();
		Context co = (Context) Proxy.newProxyInstance(
				ContextObj.class.getClassLoader(), 
				ContextObj.class.getInterfaces(), 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method,
							Object[] args) throws Throwable {
						System.out.println("代理：" + method);
						return method.invoke(obj, args);
					}
				}
		);
		co.a();
		co.b();
		co.c(2);
	}
	
	public static void main(String[] args) {
		new TestPorxy();
	}
	
	public interface Context {
		public void a();
		public int b();
		public void c(int i);
	}

	public class ContextObj implements Context {
		public void a() {
			System.out.println("a");
		}
		public int b() {
			System.out.println("b");
			return 1;
		}
		public void c(int i) {
			System.out.println("a:"+i);
		}
	}
}
