package com.utils;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.context.Context;

/** 地图工具类 */
public class WorldMapUtil {
	/** 经纬度转换常量 */
	private static final double EARTH_RADIUS = 6378137.0;
	
	/**
	 * 计算AB两点距离(米)
	 * @param lat_a	A点纬度
	 * @param lng_a	A点经度
	 * @param lat_b	B点纬度
	 * @param lng_b	B点经度
	 * @return	AB两点距离多少米
	 */
	public static Double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
		double radLat1 = (lat_a * Math.PI / 180.0);
		double radLat2 = (lat_b * Math.PI / 180.0);
		double a = radLat1 - radLat2;
		double b = (lng_a - lng_b) * Math.PI / 180.0;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	/**
	 * 计算角方位
	 * @param lat_a	A点纬度
	 * @param lng_a	A点经度
	 * @param lat_b	B点纬度
	 * @param lng_b	B点经度
	 * @return	返回角度
	 */
	public static double gps2d(double lat_a, double lng_a, double lat_b, double lng_b) {
		double d = 0;
		lat_a=lat_a*Math.PI/180;
		lng_a=lng_a*Math.PI/180;
		lat_b=lat_b*Math.PI/180;
		lng_b=lng_b*Math.PI/180;
		
		d=Math.sin(lat_a)*Math.sin(lat_b)+Math.cos(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
		d=Math.sqrt(1-d*d);
		d=Math.cos(lat_b)*Math.sin(lng_b-lng_a)/d;
		d=Math.asin(d)*180/Math.PI;
		
//		d = (d*10000);
		return d;
	}
	
	/**
	 * 合并经纬度为一个单一数字
	 * @param lat	维度
	 * @param lng	经度
	 * @return	合并后的单一数据
	 */
	public static Long mergerPoint(Double lat, Double lng) {
		String latStr = lat.toString();
		String lngStr = lng.toString();
		int lat_d = latStr.indexOf(".");
		int lng_d = lngStr.indexOf(".");
		latStr = latStr.replace(".", "");
		lngStr = lngStr.replace(".", "");
		// 处理掉过长经纬度
		latStr = (latStr.length()-lat_d)>Context.point_length ? latStr.substring(0, lat_d+Context.point_length) : latStr;
		lngStr = (lngStr.length()-lng_d)>Context.point_length ? lngStr.substring(0, lng_d+Context.point_length) : lngStr;
		// 处理过短的经纬度
		while(latStr.length()-lat_d < Context.point_length) {
			latStr += "0";
		}
		while(lngStr.length()-lng_d < Context.point_length) {
			lngStr += "0";
		}
		
		// 转换成数字数据并获取数据的二进制下的长度(准备做二进制对插)
		Long tempLat = Long.parseLong(latStr);
		Long tempLng = Long.parseLong(lngStr);
		int lengthLat = Long.toBinaryString(tempLat).length();
		int lengthLng= Long.toBinaryString(tempLng).length();
		
		// 获取经纬度中最长为位数
		int maxLength = lengthLat>lengthLng?lengthLat:lengthLng;
		
		// 进行二进制对插处理
		Long newPoint = 0L;
		for(int i=0; i<maxLength; i++) {
			newPoint += ((tempLat>>i)%2)*(new Double(Math.pow(2, i*2)).longValue());
			newPoint += ((tempLng>>i)%2)*(new Double(Math.pow(2, i*2+1)).longValue());
		}
		return newPoint;
	}
	
	/**
	 * 将合并后的数值重新转换为经纬度
	 * @param num	合并后的数值
	 * @return	借用 Point.Double 来表示经纬度(x:纬度lat，y:经度lng)
	 */
	public static Point.Double getPointForMerger(Long num) {
		int binaryLength = Long.toBinaryString(num).length();
		Long latNg = 0L;
		Long lngNg = 0L;
		for(int i=0; i<binaryLength; i++) {
			if(i%2 == 0) {
				latNg += ((num>>i)%2) * (new Double(Math.pow(2, i/2)).longValue());
			} else {
				lngNg += ((num>>i)%2) * (new Double(Math.pow(2, i/2)).longValue());
			}
		}
		
		Double pow = Math.pow(10, Context.point_length);
		Double lat = latNg / pow;
		Double lng = lngNg / pow;
		return new Point2D.Double(lat, lng);
	}
	
	/**
	 * 校验经纬度是否是异常值
	 * (处理经纬度有获取不到的情况)
	 * @param lng	经度
	 * @param lat	纬度
	 * @return	是否正常(true:非正常)
	 */
	public static boolean isErrorLoction(Double lng, Double lat) {
		if(lng==null || lat==null) {
			return true;
		}
		if(lng.equals(Double.MAX_VALUE) || lat.equals(Double.MAX_VALUE)) {
			return true;
		}
		if(lng.equals(0.0d) || lat.equals(0.0d)) {
			return true;
		}
		// 特殊值(未获取到经纬度时的值)
		if(lng.equals(4.9E-324) || lat.equals(4.9E-324)) {
			return true;
		}
		return false;
	}
	
//	/**
//	 * 将合并后的数值重新转换为经纬度
//	 * @param num	合并后的数值
//	 * @return	借用 Point.Double 来表示经纬度(x:纬度lat，y:经度lng)
//	 */
//	public static Point.Double getPointForMerger(Long num) {
//		// 拆分
//		String binaryNum = Long.toBinaryString(num);
//		String latStr = "";
//		String lngStr = "";
//		for(int i=binaryNum.length()-1; i>=0; i--) {
//			if((binaryNum.length()-i-1)%2 == 0) {
//				latStr = binaryNum.charAt(i) + latStr;
//			} else {
//				lngStr = binaryNum.charAt(i) + lngStr;
//			}
//		}
//
//		// 进制转换
//		latStr = new Long(Long.parseLong(latStr, 2)).toString();
//		lngStr = new Long(Long.parseLong(lngStr, 2)).toString();
//		
//		// 补位
//		while(latStr.length() < Context.point_length+1) {
//			if(latStr.length() == Context.point_length) {
//				latStr = "." + latStr;
//			}
//			latStr = "0" + latStr;
//		}
//		while(lngStr.length() < Context.point_length+1) {
//			if(lngStr.length() == Context.point_length) {
//				lngStr = "." + lngStr;
//			}
//			lngStr = "0" + lngStr;
//		}
//		
//		// 补点
//		if(latStr.indexOf(".") == -1) {
//			latStr = latStr.substring(0, latStr.length()-Context.point_length) + "." + latStr.substring(latStr.length()-Context.point_length, latStr.length());
//		}
//		if(lngStr.indexOf(".") == -1) {
//			lngStr = lngStr.substring(0, lngStr.length()-Context.point_length) + "." + lngStr.substring(lngStr.length()-Context.point_length, lngStr.length());
//		}
//		
//		// 转换
//		Double lat = Double.parseDouble(latStr);
//		Double lng = Double.parseDouble(lngStr);
//		return new Point2D.Double(lat, lng);
//	}
	
	/**
	 * 计算合并后数值间的距离
	 * @param num1	数值1
	 * @param num2	数值2
	 * @return	距离(米)
	 */
	public static double getDistance(Long num1, Long num2) {
		Point.Double pd1 = getPointForMerger(num1);
		Point.Double pd2 = getPointForMerger(num2);
		return getDistance(pd1.x, pd1.y, pd2.x, pd2.y);
	}
	
	/**
	 * 计算合并后数值间的角方位
	 * @param num1	数值1
	 * @param num2	数值2
	 * @return	角度
	 */
	public static double gps2d(Long num1, Long num2) {
		Point.Double pd1 = getPointForMerger(num1);
		Point.Double pd2 = getPointForMerger(num2);
		return gps2d(pd1.x, pd1.y, pd2.x, pd2.y);
	}
	
	public static void main(String[] args) {
//		Long ng1 = WorldMapUtil.mergerPoint(1.541581, 12.23311);
//		Long ng2 = WorldMapUtil.mergerPoint(1.541511, 12.23311);
//		Long ng3 = WorldMapUtil.mergerPoint(1.341511, 12.2331111);
//		Long ng4 = WorldMapUtil.mergerPoint(132.5400131231, 211.23002112312);
//		Long ng5 = WorldMapUtil.mergerPoint(999.54001313333, 999.23002113333);
//		System.out.println(ng1);
//		System.out.println(ng2);
//		System.out.println(ng3);
//		System.out.println(ng4);
//		System.out.println(ng5);
//		
//		System.out.println(getDistance(1.541581, 12.23311, 1.541511, 12.23311));
//		System.out.println(getDistance(ng1, ng2));
//		System.out.println(gps2d(1.541581, 12.23311, 85.541511, 72.23311));
//		System.out.println(mergerPoint(1.541581, 12.23311));
//		
//		System.out.println("--------------------");
//		Long num = mergerPoint(85.541511, 72.23311);
//		System.out.println(getPointForMerger(num));
		System.out.println(isErrorLoction(new Double(1D), new Double(1D)));
		System.out.println(isErrorLoction(new Double("4.9E-324"), 1D));
		System.out.println(new Double("49E-32") == 4.9E-31);
	}
}
