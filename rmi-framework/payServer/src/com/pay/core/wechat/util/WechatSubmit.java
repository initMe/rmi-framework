/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.pay.core.wechat.util   
 * 文件名：WechatSubmit.java   
 * 版本信息：   
 * 创建日期：2016年10月24日-下午3:42:48
 */
package com.pay.core.wechat.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pay.core.alipay.util.AlipayCore;
import com.pay.core.alipay.util.AlipaySubmit;
import com.pay.core.alipay.util.MD5;
import com.pay.core.wechat.config.WechatConfig;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;
import com.utils.UrlConnectUtil;

/**   
 * 类名：WechatSubmit   
 * 类描述：   
 * 创建人：  YYC
 * 修改人：  YYC
 * 修改时间：2016年10月24日 下午3:42:48   
 * 修改备注：   
 * @version 1.0.0
 */
public class WechatSubmit {

	private static String rootPath = ConfigUtil.getInstance().getRootUrl();

	/**
	 * 生成签名结果
	 * @param sPara 要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestMysign(Map<String, String> sPara) {
		String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		if (WechatConfig.sign_type.equals("MD5")) {
			mysign = MD5.sign(prestr, "&key=" + WechatConfig.key, WechatConfig.input_charset);
		}
		return mysign.toUpperCase();
	}

	/**
	 * 生成要请求给微信的xml
	 * @param sParaTemp 请求前的参数数组
	 * @return 要请求的参数数组
	 */
	public static String buildRequestPara(Map<String, String> sParaTemp) {
		//除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		//生成签名结果
		String mysign = buildRequestMysign(sPara);

		//签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);

		String reqXml = buildXmlString(sPara);

		return reqXml;
	}

	public static String buildXmlString(Map<String, String> sParaTemp) {

		String reqXml = "<xml>";

		for (String key : sParaTemp.keySet()) {
			String val = sParaTemp.get(key);
			if (isNumeric(val)) {
				reqXml = reqXml + "<" + key + ">" + val + "</" + key + ">";
			}
			else {
				reqXml = reqXml + "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
			}
		}

		reqXml = reqXml + "</xml>";

		return reqXml;
	}

	public static boolean checkResponseState(Map<String, Object> sParaTemp) {
		Object return_code = sParaTemp.get("return_code");
		Object result_code = sParaTemp.get("result_code");

		if (!StringUtil.isEmpty(result_code) && !StringUtil.isEmpty(return_code)) {
			if (return_code.toString().equals("SUCCESS") &&
					result_code.toString().equals("SUCCESS")) {
				return true;
			}
		}

		return false;
	}

	private static boolean isNumeric(String val) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(val);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String getNonceStr(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getIpAddr() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
		}
		catch (UnknownHostException e) {
			LoggerUtil.error(WechatSubmit.class, e.getMessage());
		}

		return ia.getHostAddress();
	}

	public static String doUrl(HttpServletResponse response, Map<String, String> sPara) throws IOException {
		String xmlString = buildRequestPara(sPara);
		LoggerUtil.info(WechatSubmit.class, "请求微信xml数据为：" + xmlString);

		String result = UrlConnectUtil.doUrl(WechatConfig.pay_url, "POST", null, xmlString, "utf-8");
		LoggerUtil.info(AlipaySubmit.class, "微信同步返回xml信息:" + result);

		return result;
	}

	public static String doHttpsUrl(HttpServletResponse response, Map<String, String> sPara) throws IOException {
		String xmlString = buildRequestPara(sPara);
		LoggerUtil.info(WechatSubmit.class, "请求微信xml数据为：" + xmlString);

		String keyPath = rootPath + "apiclient_cert.p12";
		String result = null;
		try {
			result = SSLClientUtil.doUrl(xmlString, keyPath, WechatConfig.mchid, "utf-8");
		}
		catch (Exception e) {
			LoggerUtil.error(WechatSubmit.class, "请求微信异常：" + e.getMessage());
		}

		LoggerUtil.info(AlipaySubmit.class, "微信同步返回xml信息:" + result);

		return result;
	}

	private static InputStream getStringStream(String sInputString) {
		ByteArrayInputStream tInputStringStream = null;
		if (sInputString != null && !sInputString.trim().equals("")) {
			tInputStringStream = new ByteArrayInputStream(sInputString.getBytes());
		}
		return tInputStringStream;
	}

	public static Map<String, Object> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

		//这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = getStringStream(xmlString);
		Document document = (Document) builder.parse(is);

		//获取到document里面的全部结点
		NodeList allNodes = ((Node) document).getFirstChild().getChildNodes();
		Node node;
		Map<String, Object> map = new HashMap<String, Object>();
		int i = 0;
		while (i < allNodes.getLength()) {
			node = allNodes.item(i);
			if (node instanceof Element) {
				map.put(node.getNodeName(), node.getTextContent());
			}
			i++;
		}
		return map;

	}

	public static void main(String[] args) {
		System.out.println(getIpAddr());
	}
}
