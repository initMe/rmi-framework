package com.exception;

import com.utils.LoggerUtil;

/**
 * 基础异常处理类
 * @version
 * 
 */
public class BaseException extends RuntimeException {
	private static final long serialVersionUID = -7093709261858466656L;

	private Throwable throwable;

	private Error errorCode;

	private String remark;

	/** 如果是web请求，此值代表数据 */
	private String requestText;

	public BaseException(Error errorCode, String... remark) {
		super(remark == null || remark.length == 0 ? errorCode.getRemark() : errorCode.getRemark() + ", " + remark[0]);
		this.errorCode = errorCode;
		this.remark = remark == null || remark.length == 0 ? errorCode.getRemark() : errorCode.getRemark() + ", " + remark[0];
		LoggerUtil.error(this.getClass(), "异常编号:[" + errorCode.getCode() + "]" + this.remark, throwable);
	}

	public BaseException(Error errorCode, Throwable throwable, String... remark) {
		super(remark == null || remark.length == 0 ? errorCode.getRemark() : errorCode.getRemark() + ", " + remark[0], throwable);
		this.errorCode = errorCode;
		this.remark = remark == null || remark.length == 0 ? errorCode.getRemark() : errorCode.getRemark() + ", " + remark[0];
		this.throwable = throwable;
		LoggerUtil.error(this.getClass(), "异常编号:[" + errorCode.getCode() + "]" + this.remark + "," + throwable.getMessage(), throwable);
	}

	@Override
	public String getMessage() {
		//		String message = super.getMessage();
		//		if (throwable != null) {
		//			message += ";nested Exception is" + throwable;
		//		}
		return remark;
	}

	/** 打印异常，包括记录异常日志 */
	@Override
	public void printStackTrace() {
		if (throwable == null) {
			LoggerUtil.error(this.getClass(), requestText + ", 异常码：" + errorCode.getCode() + ", 异常说明:" + this.remark);
			//			log.error(requestText+", 异常码："+errorCode.getCode()+", 异常说明:"+this.remark);
		}
		else {
			LoggerUtil.error(this.getClass(), requestText + ", 异常码：" + errorCode.getCode() + ", 异常说明:" + this.remark, throwable);
			//			log.error(requestText+", 异常码："+errorCode.getCode()+", 异常说明:"+this.remark, throwable);
		}
	}

	public Error getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Error errorCode) {
		this.errorCode = errorCode;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public String getRequestText() {
		return requestText;
	}

	public void setRequestText(String requestText) {
		this.requestText = requestText;
	}
}
