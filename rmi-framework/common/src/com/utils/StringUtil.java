package com.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.exception.Error;
import com.exception.SystemException;

/** 字符串处理类 */
public class StringUtil {

	/**
	 * 重写toString方法，处理了空指针问题</br>
	 * (默认如果对象为null则替换成"")
	 * @param obj	String类型的Object对象
	 * @return	转换后的字符串
	 */
	public static String toString(Object obj) {
		return toString(obj, "");
	}

	/**
	 * 重写toString方法，处理了空指针问题
	 * @param obj	String类型的Object对象
	 * @param defaultValue	如果obj是null，则以defaultValue的值返回
	 * @return	转换后的字符串
	 */
	public static String toString(Object obj, String defaultValue) {
		if (obj == null) {
			return defaultValue;
		}
		return obj.toString();
	}

	/** 将字符串过滤处理成正常的Double值 */
	public static Double toDouble(String value) {
		int startIndex = 48;
		int endIndex = 57;
		boolean hasDian = false; // 已经验证出了小数点
		boolean hasNum = false; // 已经验证出了数字
		StringBuffer sb = new StringBuffer(value);
		for (int i = 0; i < value.length(); i++) {
			char ch = sb.charAt(i);
			//  处理小数点
			if (hasNum && !hasDian && ch == '.') {
				hasDian = true;
				continue;
			}
			// 处理数字
			if (ch >= startIndex && ch <= endIndex) {
				hasNum = true;
				continue;
			}
			// 排除掉其他字符
			sb.setCharAt(i, ' ');
		}
		try {
			return Double.parseDouble(sb.toString().replace(" ", ""));
		}
		catch (Exception e) {
			return null;
		}
	}

	/** 将字符串过滤处理成正常的Double值 */
	public static Integer toInt(String value) {
		int startIndex = 48;
		int endIndex = 57;
		StringBuffer sb = new StringBuffer(value);
		for (int i = 0; i < value.length(); i++) {
			char ch = sb.charAt(i);
			// 排除掉其他字符
			if (ch < startIndex || ch > endIndex) {
				sb.setCharAt(i, ' ');
			}
		}
		try {
			return Integer.parseInt(sb.toString().replace(" ", ""));
		}
		catch (Exception e) {
			return null;
		}
	}

	/** 校验对象是不是为null或者内容是"" */
	public static boolean isEmpty(Object obj) {
		return obj == null || obj.toString().trim().equals("");
	}

	/**
	 * 将base64字符串处理成String字节<br/>
	 * @param str	base64的字符串
	 * @return	原字节数据
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String str) throws IOException {
		try {
			if (str == null) {
				return null;
			}
			return new BASE64Decoder().decodeBuffer(str);
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/**
	 * 将base64字符串处理成String<br/>
	 * (用默认的String编码集)
	 * @param str	base64的字符串
	 * @return	可显示的字符串
	 * @throws IOException
	 */
	public static String base64ToString(String str) {
		try {
			if (str == null) {
				return null;
			}
			return new String(base64ToByte(str), "UTF-8");
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/**
	 * 将base64字符串处理成String<br/>
	 * (用默认的String编码集)
	 * @param str	base64的字符串
	 * @param charset	编码格式(UTF-8/GBK)
	 * @return	可显示的字符串
	 * @throws IOException
	 */
	public static String base64ToString(String str, String charset) {
		try {
			if (str == null) {
				return null;
			}
			return new String(base64ToByte(str), charset);
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/**
	 * 将字节数据处理成base64字符串<br/>
	 * @param bts	字节数据
	 * @return	base64编码后的字符串(用于传输)
	 * @throws IOException
	 */
	public static String toBase64(byte[] bts) throws IOException {
		if (bts == null || bts.length == 0) {
			return null;
		}
		return new BASE64Encoder().encode(bts);
	}

	/**
	 * 将String处理成base64字符串<br/>
	 * (用默认的String编码集)
	 * @param oldStr	原字符串
	 * @return base64编码后的字符串(用于传输)
	 * @throws  
	 */
	public static String toBase64(String oldStr) {
		if (oldStr == null) {
			return null;
		}
		byte[] bts = oldStr.getBytes();
		return new BASE64Encoder().encode(bts);
	}

	/**
	 * 将String处理成base64字符串<br/>
	 * (用默认的String编码集)
	 * @param oldStr	原字符串
	 * @return base64编码后的字符串(用于传输)
	 * @throws UnsupportedEncodingException
	 */
	public static String toBase64(String oldStr, String charset) {
		try {
			if (oldStr == null) {
				return null;
			}
			byte[] bts = oldStr.getBytes(charset);
			return new BASE64Encoder().encode(bts);
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/** 下面这个函数用于将字节数组换成成16进制的字符串 */
	public static String byteArrayToHex(byte[] byteArray) {
		// 首先初始化一个字符数组，用来存放每个16进制字符
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		// new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
		char[] resultCharArray = new char[byteArray.length * 2];
		// 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		// 字符数组组合成字符串返回
		return new String(resultCharArray);
	}

	/** 将byte转换为MD5 */
	public static String toMD5(byte[] sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData);
			return byteArrayToHex(digest.digest());
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/** 将字符串转换为MD5 */
	public static String toMD5(String sourceData) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData.getBytes());
			return byteArrayToHex(digest.digest());
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/** 将字符串转换为MD5 
	 * @throws UnsupportedEncodingException */
	public static String toMD5(String sourceData, String sourceCharset) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(sourceData.getBytes(sourceCharset));
			return byteArrayToHex(digest.digest());
		}
		catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		}
	}

	/**
	 * 转换为全角
	 * @param input 需要转换的字符串
	 * @return 全角字符串.
	 */
	public static String toFullString(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 空格单独处理, 其余的偏移量为65248
			if (c[i] == ' ') {
				c[i] = '\u3000'; // 中文空格
			}
			else if (c[i] < 128) {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 转换为半角
	 * @param input 需要转换的字符串
	 * @return 半角字符串
	 */
	public static String toHalfString(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// 是否是中文空格， 单独处理
			if (c[i] == '\u3000') {
				c[i] = ' ';
			}
			// 校验是否字符值是否在此数值之间
			else if (c[i] > 65248 && c[i] < (128 + 65248)) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	/**
	 * 将字符串转换为unicode编码
	 * @param input	要转换的字符串(主要是包含中文的字符串)
	 * @return	转换后的unicode编码
	 */
	public static String toUnicode(String input) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < input.length(); i++) {
			// 取出每一个字符
			char c = input.charAt(i);
			String hexStr = Integer.toHexString(c);
			while (hexStr.length() < 4) {
				hexStr = "0" + hexStr;
			}
			// 转换为unicode
			unicode.append("\\u" + hexStr);
		}
		return unicode.toString();
	}

	/**
	 * 将字符串转换为unicode编码
	 * @param input	unicode编码的字符串
	 * @return	原始字符串
	 */
	public static String unicodeToString(String input) {
		StringBuffer string = new StringBuffer();
		String[] hex = input.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	/** 将字符串转换为url参数形式(中文和特殊字符会以%xx表示) */
	public static String toUrlStr(String input) {
		return toUrlStr(input, "UTF-8");
	}

	/** 将字符串转换为url参数形式(中文和特殊字符会以%xx表示) */
	public static String toUrlStr(String input, String charset) {
		try {
			return URLEncoder.encode(input, charset);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** 将url参数形式的字符串转换为原始字符串(中文和特殊字符会以%xx表示) */
	public static String urlStrToString(String input) {
		return urlStrToString(input, "UTF-8");
	}

	/** 将url参数形式的字符串转换为原始字符串(中文和特殊字符会以%xx表示) */
	public static String urlStrToString(String input, String charset) {
		try {
			return URLDecoder.decode(input, charset);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		//    	String str = "eyJhZGRyZXNzIjoi5p2l5ZKv5LqGIiwiY29udGVudCI6IuiAg+iZkeiAg+iZkSIsInBheSI6IuiAg+iZkeiAg+iZkSIsImxhdCI6MC4wLCJsbmciOjAuMCwiaWQiOjAsInVzZXJfaWQiOjE1LCJ0aW1lb3V0IjoxNDQxODgyMzkyMDY3LCJzY29wZSI6MX0=";
		////    	String base64 = toBase64(str);
		////    	System.out.println("密文："+base64);
		////    	System.out.println("解密："+base64ToString(base64));
		//
		////    	byte bts[] = new byte[]{1,2,3};
		////    	byte bts1[] = new byte[]{5,6,7};
		////    	byte[] result = Arrays.copyOf(bts, bts.length + bts1.length);  
		////		System.arraycopy(bts1, 0, result, bts.length, bts1.length);
		//		System.out.println(base64ToString(str, "UTF-8"));

		String str = "Yzpcd295YW9cc291cmNlXDIwMTUtMTEtMTZfMTJoXDYxNDkyNDBmLTIyZDYtNGZhNC04MjAzLWFk\r\nYTA0MjY1OWE1My5naWY=";
		System.out.println(base64ToString(str));
	}
}
