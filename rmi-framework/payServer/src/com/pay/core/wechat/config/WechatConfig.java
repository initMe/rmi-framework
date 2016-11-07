/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.wechat.config   
 * 文件名：WechatConfig.java   
 * 版本信息：   
 * 创建日期：2016年10月24日-下午3:22:51
 */
package com.pay.core.wechat.config;

import com.utils.ConfigUtil;

/**   
 * 类名：WechatConfig   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月24日 下午3:22:51   
 * 修改备注：   
 * @version 1.0.0
 */
public class WechatConfig {

	public static String pay_url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	public static String refund_url = "https://api.mch.weixin.qq.com/secapi/pay/refund";

	public static String appid = ConfigUtil.getInstance().getStringValue("wechat.appid");

	public static String appSecret = ConfigUtil.getInstance().getStringValue("wechat.appSecret");

	public static String mchid = ConfigUtil.getInstance().getStringValue("wechat.mchid");

	public static String key = ConfigUtil.getInstance().getStringValue("wechat.key");

	public static String sign_type = ConfigUtil.getInstance().getStringValue("wechat.sign.type");

	public static String input_charset = "utf-8";

	public static String notify_host = ConfigUtil.getInstance().getStringValue("notify_host");

	public static String trade_type = "NATIVE";

}
