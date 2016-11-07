package test;

import java.sql.Date;

import com.utils.DateUtil;

public class TimeTest {
	public TimeTest() {
		long time = DateUtil.getDefaultUtil().dateStrToLong("2016-2-20 17:30:21");
		System.out.println(time);
		System.out.println(new Date(time).toLocaleString());
	}
	
	public static void main(String[] args) {
		new TimeTest();
	}
}
