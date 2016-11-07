/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.alipay.config   
 * 文件名：AlipayConfig.java   
 * 版本信息：   
 * 创建日期：2016年10月15日-下午6:49:53
 */
package com.pay.core.alipay.config;

import com.utils.ConfigUtil;

/**   
 * 类名：AlipayConfig   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月15日 下午6:49:53   
 * 修改备注：   
 * @version 1.0.0
 */
public class AlipayConfig {

	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = ConfigUtil.getInstance().getStringValue("alipay.partner");

	// 商户的私钥
	public static String key = ConfigUtil.getInstance().getStringValue("alipay.key");

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";

	// 签名方式 不需修改
	public static String sign_type = ConfigUtil.getInstance().getStringValue("alipay.sign.type");

	//服务器异步通知地址
	public static String notify_host = ConfigUtil.getInstance().getStringValue("notify_host");

	//资金预授权业务产品码
	public static String auth_freeze_product_code = "FUND_PRE_AUTH";

	//分账业务产品码
	public static String ledger_product_code = "FUND_TRADE_FAST_PAY";

	//业务场景码，商户签约时由支付宝统一分配。
	public static String scene_code = "AUTOMOBILE";

	//支付模式,WIRELESS：需要在无线端完成支付；PC：支持在电脑上完成支付
	public static String pay_mode = "PC";
}
