package com.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.exception.Error;
import com.exception.SystemException;

public class DateUtil {

	/** 缺省格式 */
	private String pattern = "yyyy-MM-dd HH:mm:ss";
	
	/** 一天的毫秒数 */
	private final long oneDayTime = 24*3600*1000;
	
	/** 时间 */
	private Long time = 0L;
	
	public DateUtil() {}

	public DateUtil(Long time){
		this.time = time;
	}
	/**
	 * Date工具类
	 * @param pattern	格式(缺省：yyyy-MM-dd HH:mm:ss)
	 */
	public DateUtil(String pattern) {
		this.pattern = pattern;
	}
	/**
	 * Date工具类
	 * @param time	要处理的时间long值
	 * @param pattern	格式(缺省：yyyy-MM-dd HH:mm:ss)
	 */
	public DateUtil(Long time, String pattern) {
		this.pattern = pattern;
		this.time = time;
	}
	
	/**
	 * 将字符串转换为毫秒数
	 * @param dateStr	对应格式的时间字符串
	 * @return	毫秒数
	 */
	public Long dateStrToLong(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Long timeStart = 0l;
		try {
			timeStart = sdf.parse(dateStr).getTime();
		} catch (ParseException e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_error, e);
		}
		return timeStart;
	}
	
	/**
	 * 将毫秒数转换为可显示的字符串
	 * @param dateLong	毫秒数
	 * @return	对应格式的时间字符串
	 */
	public String dateLongToString(Long dateLong) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date = new Date(dateLong);
		return sdf.format(date);
	}
	
	/**
	 * 计算时间(计算传入时间前几天或者后几天的时间)
	 * @param nowTime	传入时间
	 * @param addNum	要增加的天数(为负数则是减少的天数)
	 * @return	计算后的时间
	 */
	public Long dateAddDay(Long nowTime, int addNum) {
		return nowTime + (oneDayTime*addNum);
	}
	
	/** 获取date缺省工具类(使用缺省的格式：yyyy-MM-dd HH:mm:ss) */
	public static DateUtil getDefaultUtil() {
		return new DateUtil();
	}
	
	/** 获取今天的零点时间 */
	public long getTodayZero(){
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_YEAR);
		
		calendar.set(Calendar.DAY_OF_YEAR, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
	}
	
	@Override
	public String toString() {
		long tempTime = System.currentTimeMillis();
		if(time > 0) {
			tempTime = time;
		}
		return dateLongToString(tempTime);
	}
}
