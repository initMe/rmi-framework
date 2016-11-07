package com.pay.core;

import javax.servlet.http.HttpServletResponse;

/**
 * 类名：PayExcutor   
 * 类描述：   支付网关接口
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月15日 下午10:04:23   
 * 修改备注：   
 * @version 1.0.0
 */
public interface PayExcutor {

	public void payment(BaseBean bean, HttpServletResponse response) throws Exception;

	public void refund(BaseBean bean, HttpServletResponse response) throws Exception;

	public void prorate(BaseBean bean, HttpServletResponse response) throws Exception;
}
