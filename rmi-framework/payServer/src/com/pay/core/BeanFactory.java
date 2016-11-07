/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core   
 * 文件名：BeanFactory.java   
 * 版本信息：   
 * 创建日期：2016年10月12日-上午10:50:49
 */
package com.pay.core;

import java.util.HashMap;
import java.util.Map;

import com.pay.business.request.GetCodeRequest;
import com.pay.business.request.LedgerRequest;
import com.pay.business.request.OrderPayRequest;
import com.pay.business.request.RefundRequest;
import com.pay.business.request.VerifyCodeRequest;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;
import com.utils.StringUtil;

/**   
 * 类名：BeanFactory   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月12日 上午10:50:49   
 * 修改备注：   
 * @version 1.0.0
 */
public class BeanFactory {

	private static BeanFactory factory;

	private BeanFactory() {}

	public static BeanFactory getInstance() {
		if (factory == null) {
			factory = new BeanFactory();
		}

		return factory;
	}

	public BaseBean getBeanByParams(Map<String, String> paraMap) {
		String serviceName = paraMap.get("service");
		if (StringUtil.isEmpty(serviceName)) {
			LoggerUtil.error(this.getClass(), "service is null");
			throw new NullPointerException("service is null");
		}
		//获取方法名
		ServiceEnum serviceEnum = ServiceEnum.getByServiceName(serviceName);
		String method = serviceEnum.getMethod();

		if (StringUtil.isEmpty(method) || paraMap == null || paraMap.size() == 0) {
			return null;
		}

		Map<String, Object> parasMap = new HashMap<String, Object>();
		for (String s : paraMap.keySet()) {
			Object value = (Object) paraMap.get(s);
			parasMap.put(s, value);
		}

		if (method.equals(ServiceEnum.PAYMENT.getMethod())) {
			OrderPayRequest paramBean = ObjectUtil.initObject(OrderPayRequest.class, parasMap);
			return paramBean;
		}
		if (method.equals(ServiceEnum.PRORATE.getMethod())) {
			LedgerRequest paramBean = ObjectUtil.initObject(LedgerRequest.class, parasMap);
			return paramBean;
		}
		if (method.equals(ServiceEnum.REFUND.getMethod())) {
			RefundRequest paramBean = ObjectUtil.initObject(RefundRequest.class, parasMap);
			return paramBean;
		}
		if (method.equals(ServiceEnum.REVERIFYCODE.getMethod())) {
			GetCodeRequest paramBean = ObjectUtil.initObject(GetCodeRequest.class, parasMap);
			return paramBean;
		}
		if (method.equals(ServiceEnum.VERIFYCODE.getMethod())) {
			VerifyCodeRequest paramBean = ObjectUtil.initObject(VerifyCodeRequest.class, parasMap);
			return paramBean;
		}

		return null;
	}
}
