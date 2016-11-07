/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.config   
 * 文件名：MerchantConfig.java   
 * 版本信息：   
 * 创建日期：2016年10月16日-上午12:08:12
 */
package com.pay.core.config;

import com.utils.ConfigUtil;

/**   
 * 类名：MerchantConfig   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月16日 上午12:08:12   
 * 修改备注：   
 * @version 1.0.0
 */
public class MerchantConfig {

	public final static String key = ConfigUtil.getInstance().getStringValue("merchant.key");

	public final static String sign_type = ConfigUtil.getInstance().getStringValue("merchant.sign.type");

	public final static String input_charset = "utf-8";

}
