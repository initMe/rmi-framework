package com.pay.core.alipay.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.pay.api.ServiceEnum;
import com.pay.core.BaseBean;
import com.pay.core.alipay.config.AlipayConfig;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;
import com.utils.UrlConnectUtil;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {

	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

	/**
	 * 生成签名结果
	 * @param sPara 要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestMysign(Map<String, String> sPara) {
		String prestr = AlipayCore.createLinkString(sPara); //把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		if (AlipayConfig.sign_type.equals("MD5")) {
			mysign = MD5.sign(prestr, AlipayConfig.key, AlipayConfig.input_charset);
		}
		return mysign;
	}

	/**
	 * @param sParaTemp 请求前的参数数组
	 * @return 要请求的参数数组
	 */
	public static String buildRequestString(Map<String, String> sParaTemp, String key) {
		Map<String, String> sPara = buildRequestPara(sParaTemp);

		String queryString = "";
		for (String ke : sPara.keySet()) {
			try {
				queryString = queryString + ke + "=" + URLEncoder.encode(sPara.get(ke), "utf-8") + "&";
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return queryString.substring(0, queryString.length() - 1);
	}

	/**
	 * 生成要请求给支付宝的参数数组
	 * @param sParaTemp 请求前的参数数组
	 * @return 要请求的参数数组
	 */
	public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp) {
		//除去数组中的空值和签名参数
		Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
		//生成签名结果
		String mysign = buildRequestMysign(sPara);

		//签名结果与签名方式加入请求提交参数组中
		sPara.put("sign", mysign);
		sPara.put("sign_type", AlipayConfig.sign_type);

		return sPara;
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 * @param properties  MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
		}

		return nameValuePair;
	}

	/**
	 * 用于防钓鱼，调用接口query_timestamp来获取时间戳的处理函数
	 * 注意：远程解析XML出错，与服务器是否支持SSL等配置有关
	 * @return 时间戳字符串
	 * @throws IOException
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	public static String query_timestamp() throws MalformedURLException,
			DocumentException, IOException {

		//构造访问query_timestamp接口的URL串
		String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + AlipayConfig.partner + "&_input_charset=" + AlipayConfig.input_charset;
		StringBuffer result = new StringBuffer();

		SAXReader reader = new SAXReader();
		Document doc = reader.read(new URL(strUrl).openStream());

		List<Node> nodeList = doc.selectNodes("//alipay/*");

		for (Node node : nodeList) {
			// 截取部分不需要解析的信息
			if (node.getName().equals("is_success") && node.getText().equals("T")) {
				// 判断是否有成功标示
				List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
				for (Node node1 : nodeList1) {
					result.append(node1.getText());
				}
			}
		}

		return result.toString();
	}

	public static boolean isSuccess(String xml, String method) {

		LoggerUtil.info(AlipaySubmit.class, "待转换的xml串：" + xml);
		Document document;
		try {
			document = DocumentHelper.parseText(xml);
		}
		catch (DocumentException e) {
			return false;
		}
		Element root = document.getRootElement();

		List<Node> nodeList = document.selectNodes("//alipay/*");

		String success_sign = "";

		for (Node node : nodeList) {
			// 截取部分不需要解析的信息
			if (node.getName().equals("is_success") && node.getText().equals("T")) {
				// 判断是否有成功标示
				List<Node> childnNodes = new ArrayList<Node>();
				if (method.equals(ServiceEnum.REFUND.getMethod())) {
					childnNodes = document.selectNodes("//response/order/*");
					success_sign = "SUCCESS";
				}
				if (method.equals(ServiceEnum.PRORATE.getMethod())) {
					childnNodes = document.selectNodes("//response/alipay/*");
					success_sign = "ORDER_SUCCESS_PAY_SUCCESS";
				}

				for (Node childNode : childnNodes) {
					if (childNode.getName().equals("result_code") && childNode.getText().equals(success_sign)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static String buildParams(BaseBean bean) {
		Map<String, String> fields = ObjectUtil.getFields(bean);

		String params = "";
		for (String field : fields.keySet()) {
			String val = "";
			if (field.equals("total_fee")) {
				Object value = fields.get("total_fee");
				val = value.toString();
			}
			else {
				val = fields.get(field);
			}
			params = params + field + "=" + val + "&";
		}

		return params.substring(0, params.length() - 1);
	}

	public static void sendRedirect(HttpServletResponse response, Map<String, String> sPara) throws IOException {
		Map<String, String> parms = buildRequestPara(sPara);
		String queryString = "";
		for (String key : parms.keySet()) {
			queryString = queryString + key + "=" + URLEncoder.encode(parms.get(key), AlipayConfig.input_charset) + "&";
		}
		queryString = queryString.substring(0, queryString.length() - 1);
		LoggerUtil.info(AlipaySubmit.class, "请求支付宝url:" + ALIPAY_GATEWAY_NEW + queryString);
		response.sendRedirect(ALIPAY_GATEWAY_NEW + queryString);
	}

	public static String doUrl(HttpServletResponse response, Map<String, String> sPara) throws IOException {
		Map<String, String> parms = buildRequestPara(sPara);
		String queryString = "";
		for (String key : parms.keySet()) {
			queryString = queryString + key + "=";
			if (key.equals("royalty_parameters")) {
				queryString = queryString + parms.get(key) + "&";
			}
			else {
				queryString = queryString + URLEncoder.encode(parms.get(key), AlipayConfig.input_charset) + "&";
			}
		}
		queryString = queryString.substring(0, queryString.length() - 1);

		LoggerUtil.info(AlipaySubmit.class, "请求支付宝url:" + ALIPAY_GATEWAY_NEW + queryString);

		String result = UrlConnectUtil.doUrl(ALIPAY_GATEWAY_NEW + queryString);
		LoggerUtil.info(AlipaySubmit.class, "支付宝同步返回xml信息:" + result);

		return result;
	}

	public static void main(String[] args) throws MalformedURLException, DocumentException, IOException {
		query_timestamp();
	}
}
