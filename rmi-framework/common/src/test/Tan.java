package test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tan<T> {
	// 圆心
	int x = 100, y = 100;
	// 触摸点
	int x1 = 60, y1 = 50;
	
	public Tan() {
		double vx = x1-x;
		double vy = y1-y;
		System.out.println(Math.atan(vy/vx)*180/Math.PI);
	}
	
	public void test() {
		T a ;
	}
	public static void main(String[] args) {
		new Tan();
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str3 = "1927-12-31 23:54:07";
			String str4 = "1927-12-31 23:54:08";
			Date sDt3 = sf.parse(str3);
			Date sDt4 = sf.parse(str4);
			System.out.println("str3:"+sDt3.toLocaleString());
			System.out.println("str4:"+sDt4.toLocaleString());
			long ld3 = sDt3.getTime() /1000;
			long ld4 = sDt4.getTime() /1000;
			System.out.println(ld3);
			System.out.println(ld4);
			System.out.println(ld4-ld3);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
