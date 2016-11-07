package com.bean.sms;

import com.exception.Error;
import com.exception.SystemException;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

/** 发送短信验证码的返回数据 */
public class SMSCheckNoResponse extends SMSBaseResponse {
	private static final long serialVersionUID = -8757751393832352737L;

	/** 验证码 */
	private String checkText;
	
	public SMSCheckNoResponse() {}
	
	public SMSCheckNoResponse(SMSBaseResponse sbr) {
		try {
			ObjectUtil.insertObj(this, sbr);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_error, e);
		}
	}

	public String getCheckText() {
		return checkText;
	}
	public void setCheckText(String checkText) {
		this.checkText = checkText;
	}
}
