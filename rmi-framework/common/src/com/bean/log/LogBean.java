package com.bean.log;

import com.bean.db.ID;

/** 日志存储bean */
public class LogBean extends ID {
	private static final long serialVersionUID = -93401541682601078L;
	/** 日志类型 */
	private Integer type;
	/** 日志内容 */
	private String msg;
	/** 项目名称*/
	private String projectName;
	public LogBean() {}
	/** 带项目名参数的构造器 */
	public LogBean(String projectName) {
		this.projectName = projectName;
	}
	
	public LogBean(String projectName, LogType type, String msg) {
		setProjectName(projectName);
		setType(type);
		setMsg(msg);
	}
	
	public LogBean(String projectName, Integer type, String msg) {
		setProjectName(projectName);
		setType(type);
		setMsg(msg);
	}
	
	public Integer getType() {
		return type;
	}
	public LogType getTypeForEnum() {
		return LogType.getLogTypeByType(type);
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public void setType(LogType type) {
		this.type = type.getType();
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	/** 日志类型 */
	public static enum LogType {
		unknow(-1, "unknow未知类型"),
		debug(0, "debug测试日志"),
		info(1, "info普通日志"),
		error(2, "error错误日志");
		private Integer type;
		private String remark;
		private LogType (Integer type, String remark) {
			this.type = type;
			this.remark = remark;
		}
		/** 依据log类型值获取log类型枚举 */
		public static LogType getLogTypeByType(Integer type) {
			LogType[] lts = LogType.values();
			for(LogType lt : lts) {
				if(lt.getType().equals(type)) {
					return lt;
				}
			}
			return unknow;
		}
		public Integer getType() {
			return type;
		}
		public String getRemark() {
			return remark;
		}
	}
}
