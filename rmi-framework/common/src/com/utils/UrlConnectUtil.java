package com.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.exception.Error;
import com.exception.SystemException;

/** URL请求工具 */
public class UrlConnectUtil {

	/***
	 * 请求一个url，获取返回值
	 * @param webUrl     url路径
	 * @param method     请求方式(POST/GET)
	 * @param property   请求头
	 * @param paramMap   请求参数(主要POST请求场景所需)
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method, Map<String, String> property, Map<String, ?> paramMap) {
		// 参数
		String paramStr = "";
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				paramStr += key + "=";
				paramStr += paramMap.get(key) + "&";
			}
			paramStr = paramStr.substring(0, paramStr.length() - 1);
		}
		return doUrl(webUrl, method, property, paramStr, Charset.defaultCharset());
	}

	/**
	  * 请求一个url，获取返回值
	 * @param webUrl     url路径
	 * @param method     请求方式(POST/GET)
	 * @param property   请求头
	 * @param params   请求参数(主要POST请求场景所需)
	 * @param charset   对返回结果进行编码，如果为空默认为UTF-8
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method, Map<String, String> property, String params, Charset charset) {

		try {
			URL url = new URL(webUrl);
			URLConnection connect = url.openConnection();
			connect.setUseCaches(false);
			// 请求头
			if (property != null) {
				for (String key : property.keySet()) {
					connect.setRequestProperty(key, property.get(key));
				}
			}

			// 请求方式
			if (method != null) {
				((HttpURLConnection) connect).setRequestMethod(method);
			}
			// 参数
			if (!StringUtil.isEmpty(params)) {
				connect.setDoOutput(true);
				connect.getOutputStream().write(params.getBytes());
			}

			// 获取响应结果
			connect.connect();
			connect.getInputStream();
			InputStream in = connect.getInputStream();
			byte[] bts = new byte[1024];
			int length = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while ((length = in.read(bts)) > 0) {
				bout.write(bts, 0, length);
				bout.flush();
			}

			if (charset == null) {
				charset = Charset.defaultCharset();
			}

			return new String(bout.toByteArray(), charset);
		}
		catch (Exception e) {
			//return "";
			throw new SystemException(Error.system_request_error, e);
		}
	}

	/***
	 * 请求一个url，获取返回值
	 * @param webUrl   url路径
	 * @param method   请求方式(POST/GET)
	 * @param property 请求头
	 * @param key      单个参数key
	 * @param val  单个参数val
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method, Map<String, String> property, String key, String val) {
		Map<String, String> param = new HashMap<String, String>();
		param.put(key, val);
		return doUrl(webUrl, method, property, param);
	}

	/***
	 * 请求一个url，获取返回值
	 * @param webUrl url路径
	 * @param method 请求方式(POST/GET)
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl, String method) {
		return doUrl(webUrl, method, null, null);
	}

	/***
	 * 请求一个url，GET方式,获取返回值
	 * 
	 * @param webUrl url路径
	 * @return 返回该url的响应结果
	 */
	public static String doUrl(String webUrl) {
		return doUrl(webUrl, "GET", null, null);
	}

	public static String SendHttpsPOST(String url)
	{
		String result = null;

		try {
			//设置SSLContext 
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

			URL requestUrl = new URL(url);
			URLConnection urlConnection = requestUrl.openConnection();
			HttpsURLConnection httpsConn = (HttpsURLConnection) urlConnection;

			SSLSocketFactory sslsocketfactory = sslcontext.getSocketFactory();

			httpsConn.setSSLSocketFactory(sslsocketfactory);

			//加入数据 
			httpsConn.setRequestMethod("POST");
			httpsConn.setDoOutput(true);
			DataOutputStream out = new DataOutputStream(
					httpsConn.getOutputStream());

			out.flush();
			out.close();

			//获取输入流 
			BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
			int code = httpsConn.getResponseCode();
			if (HttpsURLConnection.HTTP_OK == code) {
				String temp = in.readLine();
				/*连接成一个字符串*/
				while (temp != null) {
					if (result != null)
						result += temp;
					else
						result = temp;
					temp = in.readLine();
				}
			}
		}
		catch (Exception e) {
			throw new SystemException(Error.system_request_error, e);
		}

		return result;
	}

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	};

}
