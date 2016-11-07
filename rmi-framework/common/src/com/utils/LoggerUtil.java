package com.utils;

import java.io.File;
import java.net.URL;

import org.slf4j.LoggerFactory;

import com.bean.log.LogBean.LogType;

/** 日志 */
public class LoggerUtil {
	/** 项目名 */
	private static String projectName;
     
	static {
		URL url = ConfigUtil.class.getResource("/");
		String str = url.getFile();
		File f = new File(str);
		if (str.endsWith("bin/")) {
			projectName = f.getParentFile().getName();
		}
		else if (str.endsWith("classes/")) {
			projectName = f.getParentFile().getParentFile().getName();
		} else {
			projectName = f.getName();
		}
		//		System.out.println(projectName);
	}

	/** 获取当前项目的名称 */
	public static String getProjectName() {
		return projectName;
	}

	/** 保存日志 */
	private static void saveLog(LogType type, String msg) {
		//		LogBean lb = new LogBean(projectName, type, msg);
		//		try {
		//			RmiProxy.getLogService().add(lb);
		//		} catch (Exception e) {
		//			LoggerFactory.getLogger(LoggerUtil.class).error("项目名["+projectName+"]日志写入异常", e.getMessage());
		//		}
	}

	public static void debug(Class<?> clazz, String str) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).debug("项目名：[" + projectName + "]" + str);
		saveLog(LogType.debug, str);
	}

	public static void debug(Class<?> clazz, String str, Object obj) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).debug("项目名：[" + projectName + "]" + str, obj);
		saveLog(LogType.debug, str);
	}

	public static void debug(Class<?> clazz, String str, Object[] objs) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).debug("项目名：[" + projectName + "]" + str, objs);
		saveLog(LogType.debug, str);
	}

	public static void debug(Class<?> clazz, Throwable e) {
		LoggerFactory.getLogger(clazz).debug("项目名：[" + projectName + "]", e);
	}

	public static void info(Class<?> clazz, String str) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).info("项目名：[" + projectName + "]" + str);
		saveLog(LogType.info, str);
	}

	public static void info(Class<?> clazz, String str, Object obj) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).info("项目名：[" + projectName + "]" + str, obj);
		saveLog(LogType.info, str);
	}

	public static void info(Class<?> clazz, String str, Object[] objs) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).info("项目名：[" + projectName + "]" + str, objs);
		saveLog(LogType.info, str);
	}

	public static void error(Class<?> clazz, String str) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).error("项目名：[" + projectName + "]" + str);
		saveLog(LogType.error, str);
	}

	public static void error(Class<?> clazz, String str, Object obj) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).error("项目名：[" + projectName + "]" + str, obj);
		saveLog(LogType.error, str);
	}

	public static void error(Class<?> clazz, String str, Object[] objs) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).error("项目名：[" + projectName + "]" + str, objs);
		saveLog(LogType.error, str);
	}

	public static void error(Class<?> clazz, String str, Throwable e) {
		str = str == null ? "" : str;
		LoggerFactory.getLogger(clazz).error("项目名：[" + projectName + "]" + str, e);
		saveLog(LogType.error, e == null ? "null指针" : e.toString());
	}

	public static void error(Class<?> clazz, Throwable e) {
		error(clazz, "", e);
	}

	public static void main(String[] args) {

	}
}
