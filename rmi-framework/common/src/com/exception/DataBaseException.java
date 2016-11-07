package com.exception;

import java.io.PrintStream;

/**
 * 数据库异常处理 数据库异常必须处理
 * 
 * @version
 * 
 */
public class DataBaseException extends RuntimeException {
	private static final long serialVersionUID = -7093709261858466656L;
	private Throwable throwable;
	private Error errorCode;

	public DataBaseException(Error errorCode, String... remark) {
		super(remark==null||remark.length==0?errorCode.getRemark():remark[0]);
		this.errorCode = errorCode;
	}

	public DataBaseException(Error errorCode, Throwable throwable, String... remark) {
		this(errorCode, remark);
		this.throwable = throwable;
	}

	@Override
	public String getMessage() {
		String message = super.getMessage();
		if (throwable != null) {
			message += ";nested Exception is" + throwable;
		}
		return message;
	}

	@Override
	public void printStackTrace() {
		printStackTrace(System.err);
	}

	@Override
	public void printStackTrace(PrintStream s) {
		if (throwable == null) {
			super.printStackTrace(s);
		}
		s.println(this);
		throwable.printStackTrace(s);
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
}
