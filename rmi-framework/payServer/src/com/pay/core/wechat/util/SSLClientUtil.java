/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.wechat.util   
 * 文件名：SSLClient.java   
 * 版本信息：   
 * 创建日期：2016年10月24日-下午11:46:43
 */
package com.pay.core.wechat.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.pay.core.wechat.config.WechatConfig;
import com.utils.LoggerUtil;

/**   
 * 类名：SSLClient   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月24日 下午11:46:43   
 * 修改备注：   
 * @version 1.0.0
 */
public class SSLClientUtil {

	public static String doUrl(String xmlData, String keyPath, String password, String charset) throws Exception {

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(keyPath));
		try {
			keyStore.load(instream, password.toCharArray());
		}
		finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, password.toCharArray())
				.build();
		// Allow TLSv1 protocol only
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				new String[] { "TLSv1" },
				null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
		String result = "";
		try {

			HttpPost httpPost = new HttpPost(WechatConfig.refund_url);
			httpPost.setEntity(new StringEntity(xmlData));

			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					httpPost.abort();
					LoggerUtil.error(SSLClientUtil.class, "HttpClient,error status code :" + statusCode);
					return result;
				}
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, charset);
				}
				EntityUtils.consume(entity);
			}
			finally {
				response.close();
			}
		}
		finally {
			httpclient.close();
		}

		return result;
	}

}
