package com.exception;

/**
 * 业务异常处理 业务异常必须处理
 * 
 * @version
 * 
 */
public class BusinessException extends BaseException {
	private static final long serialVersionUID = -7093709261858466656L;
	public BusinessException(Error errorCode, String... remark) {
		super(errorCode, remark);
	}
	
	public BusinessException(Error errorCode, Throwable throwable, String... remark) {
		super(errorCode, throwable, remark);
	}
}
