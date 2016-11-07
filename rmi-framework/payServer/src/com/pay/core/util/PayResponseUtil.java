/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.util   
 * 文件名：PayResponseUtil.java   
 * 版本信息：   
 * 创建日期：2016年10月15日-下午10:18:50
 */
package com.pay.core.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.pay.business.BaseResponse;
import com.pay.core.PayEnum;
import com.pay.core.alipay.util.AlipayNotify;
import com.pay.core.alipay.util.AlipaySubmit;
import com.pay.core.config.MerchantConfig;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;
import com.utils.StringUtil;

/**   
 * 类名：PayResponseUtil   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月15日 下午10:18:50   
 * 修改备注：   
 * @version 1.0.0
 */
public class PayResponseUtil {

	/**
	 * 校验商户签名
	 * @param params
	 * @return    
	 * @return boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public static boolean verifyMechant(Map<String, String> params) {
		String sign = "";
		if (params.get("sign") != null) {
			sign = params.get("sign");
		}

		return AlipayNotify.getSignVeryfy(params, sign, MerchantConfig.key, MerchantConfig.sign_type);
	}

	/**
	 * 校验第三方支付平台签名：支付宝或微信
	 * @param params
	 * @return    
	 * @return boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public static boolean verifyThird(Map<String, String> params, String limtPay) {

		if (limtPay.equals(PayEnum.ALIPAY.getCode())) {
			return AlipayNotify.verify(params);
		}
		else if (limtPay.equals(PayEnum.WECHAT.getCode())) {
			return true;
		}

		return false;
	}

	public static String getResponseJson(BaseResponse resBean) throws Exception {

		Map<String, Object> attrMap = ObjectUtil.getFields(resBean);

		Map<String, String> parms = AlipaySubmit.buildRequestPara(parseMap(attrMap));

		Map<String, String> returnMap = new HashMap<String, String>();
		for (String key : parms.keySet()) {
			if (key.equals("sign_type") || key.equals("input_charset")) {
				continue;
			}
			String value = parms.get(key);
			returnMap.put(key, value);
		}

		String jsonData = JsonUtil.objToJson(returnMap);
		LoggerUtil.info(PayResponseUtil.class, "返回json字段：" + jsonData);

		//进行base64加密，防止乱码
		jsonData = StringUtil.toBase64(jsonData, "utf-8");
		return jsonData;
	}

	public static String getResponseQuery(BaseResponse resBean) throws Exception {
		Map<String, Object> attrMap = ObjectUtil.getFields(resBean);

		Map<String, String> parms = AlipaySubmit.buildRequestPara(parseMap(attrMap));

		String result = "";
		for (String key : parms.keySet()) {
			String value = URLEncoder.encode(parms.get(key), MerchantConfig.input_charset);
			result = result + key + "=" + value + "&";
		}

		result = result.substring(0, result.length() - 1);

		LoggerUtil.info(PayResponseUtil.class, "请求查询字段：" + result);

		return result;
	}

	private static Map<String, String> parseMap(Map<String, Object> paramMap) {

		Map<String, String> result = new HashMap<String, String>();

		for (String key : paramMap.keySet()) {
			if (paramMap.get(key) != null) {
				result.put(key, paramMap.get(key).toString());
			}
		}

		return result;
	}
}
