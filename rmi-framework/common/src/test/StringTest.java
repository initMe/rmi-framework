package test;

import java.util.Random;

public class StringTest {
	
	public StringTest() throws Exception {
//		System.out.println("Long:\t" + Long.MAX_VALUE);
//		System.out.println("int:\t" + Integer.MAX_VALUE);
//		System.out.println("float:\t" + new BigDecimal(Float.MAX_VALUE).toString());
//		System.out.println("double:\t" + new BigDecimal(Double.MAX_VALUE).toString());
//		System.out.println("double:\t" + new BigDecimal(Double.MAX_VALUE).toString().length());

		System.out.println("double:\t" + String.format("%.2f", 2.12345d));
		System.out.println(new Double(0).equals(0));
		System.out.println(new Random().nextInt(2));
	}
	
	public static void main(String[] args) {
		try {
			new StringTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
