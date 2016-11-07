package com.pay.core.wechat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.pay.core.alipay.util.MD5;
import com.pay.core.wechat.config.WechatConfig;
import com.utils.LoggerUtil;

public class Signature {

	public static String getSign(Map<String, Object> map) {
		ArrayList<String> list = new ArrayList<String>();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != "") {
				list.add(entry.getKey() + "=" + entry.getValue() + "&");
			}
		}
		int size = list.size();
		String[] arrayToSort = list.toArray(new String[size]);
		Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			sb.append(arrayToSort[i]);
		}
		String result = sb.toString();
		//result += "key=" + WechatConfig.key;

		result = MD5.sign(result, "key=" + WechatConfig.key, WechatConfig.input_charset);

		return result.toUpperCase();
	}

	/**
	 * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
	 * @param responseString API返回的XML数据字符串
	 * @return API签名是否合法
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static boolean checkIsSignValidFromResponseString(String responseString) throws ParserConfigurationException, IOException, SAXException {

		Map<String, Object> map = WechatSubmit.getMapFromXML(responseString);

		String signFromAPIResponse = map.get("sign").toString();
		if (signFromAPIResponse == "" || signFromAPIResponse == null) {
			LoggerUtil.info(Signature.class, "API返回的数据签名数据不存在，有可能被第三方篡改!!!");
			return false;
		}

		map.put("sign", "");
		//将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
		String signForAPIResponse = Signature.getSign(map);

		if (!signForAPIResponse.equals(signFromAPIResponse)) {
			//签名验不过，表示这个API返回的数据有可能已经被篡改了
			LoggerUtil.info(Signature.class, "API返回的数据签名验证不通过，有可能被第三方篡改!!!");
			return false;
		}

		return true;
	}

}
