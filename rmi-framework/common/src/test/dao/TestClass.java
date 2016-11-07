package test.dao;

import java.util.ArrayList;
import java.util.List;

public class TestClass<T extends List> {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List obj = new TestClass().test(new ArrayList());
	}
	
	public  T test(T bean){
		return bean;
	}

}
