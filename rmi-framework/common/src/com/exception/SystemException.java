package com.exception;

/**
 * 系统异常处理 系统异常必须处理
 * @version
 * 
 */
public class SystemException extends BaseException {
	private static final long serialVersionUID = -7093709261858466656L;
	
	public SystemException(Error errorCode, String... remark) {
		super(errorCode, remark);
	}
	
	public SystemException(Error errorCode, Throwable throwable, String... remark) {
		super(errorCode, throwable, remark);
	}

	
}
