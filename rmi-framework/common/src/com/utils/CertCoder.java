package com.utils;

import java.security.Key;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.SecureRandom;
import java.security.Signature;  
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
import java.util.Arrays;
  
import javax.crypto.Cipher;  

import com.bean.business.PassBean;
import com.context.*;
  
/** 
 * DSA安全编码组件 
 * @version 1.0 
 * @since 1.0 
 */  
public class CertCoder {  
	/** 签名算法 */
    private static final String KEY_ALGORITHM = "RSA";
    /** 解签算法 */
    private static final String SIGNATURE_ALGORITHM = KEY_ALGORITHM.equals("DSA")?"SHA1withDSA":"SHA1withRSA";
    /** 生成的密钥长度 */
    private static final int SIZE = 1024;
    
    
    /** 
     * 用私钥对信息生成数字签名 
     * @param data	待加密数据 
     * @param privateKey	私钥 
     * @return 加密后的数据
     * @throws Exception 
     */  
    public static String signByPrivateKey(byte[] data, String privateKey) throws Exception {  
        // 解密由base64编码的私钥  
        byte[] keyBytes = StringUtil.base64ToByte(privateKey);  
        // 构造PKCS8EncodedKeySpec对象  
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        // 取私钥匙对象  
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
        // 用私钥对信息生成数字签名  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initSign(priKey);  
        signature.update(data);  
        return StringUtil.toBase64(signature.sign());  
    }
    
//    /** 
//     * 用公钥对信息生成数字签名 
//     * @param data	待加密数据 
//     * @param publicKey	公钥 
//     * @return 加密后的数据
//     * @throws Exception 
//     */  
//    public static String signByPublicKey(byte[] data, String publicKey) throws Exception {  
//        // 解密由base64编码的私钥  
//        byte[] keyBytes = decryptBASE64(publicKey);  
//        // 构造PKCS8EncodedKeySpec对象  
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
//        // KEY_ALGORITHM 指定的加密算法  
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
//        // 取私钥匙对象  
//        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
//        // 用私钥对信息生成数字签名  
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
//        signature.initSign(priKey);  
//        signature.update(data);  
//        return encryptBASE64(signature.sign());  
//    }
  
    /** 
     * 校验数字签名 
     * @param oldValue	原始数据
     * @param passValue	密文 
     * @param publicKey	公钥 
     * @return 校验成功返回true,失败返回false 
     * @throws Exception 
     */  
    public static boolean verify(String oldValue, String passValue, String publicKey) throws Exception {
    	byte[] data = oldValue.getBytes();
        // 解密由base64编码的公钥  
        byte[] keyBytes = StringUtil.base64ToByte(publicKey);  
        // 构造X509EncodedKeySpec对象  
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
        // KEY_ALGORITHM 指定的加密算法  
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
        // 取公钥匙对象  
        PublicKey pubKey = keyFactory.generatePublic(keySpec);  
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
        signature.initVerify(pubKey);  
        signature.update(data);  
        // 验证签名是否正常  
        return signature.verify(StringUtil.base64ToByte(oldValue));  
    }  
  
//    /** 
//     * 用私钥进行加/解密
//     * (加密字符串长度必须是4的倍数且非中文字符串)
//     * @param data	数据(加密对应原文，解密对应的是密文)
//     * @param key	私钥
//     * @param type	操作类型(true:加密，false:解密)
//     * @return	处理出来的数据
//     * @throws Exception 
//     */  
//    public static String doCoderByPrivateKey(String data, String key, boolean type) throws Exception {
//    	int typeValue = type?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE;
//    	byte[] valueBts = type?data.getBytes():StringUtil.base64ToByte(data);
//        // 对密钥解密  
//        byte[] keyBytes = StringUtil.base64ToByte(key);  
//        // 取得私钥  
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
//        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  
//        // 对数据解密  
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
//        cipher.init(typeValue, privateKey);  
//        byte[] bts = cipher.doFinal(valueBts);
//        return type?StringUtil.toBase64(bts):new String(bts, "UTF-8");
//    }
    
    /** 
     * 用私钥进行加/解密
     * (加密字符串长度必须是4的倍数且非中文字符串)
     * @param data	数据(加密对应原文，解密对应的是密文)
     * @param key	私钥
     * @param type	操作类型(true:加密，false:解密)
     * @return	处理出来的数据
     * @throws Exception 
     */  
    public static String doCoderByPrivateKey(String data, String key, boolean type) throws Exception {
    	int typeValue = type?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE;
    	byte[] valueBts = type?data.getBytes():StringUtil.base64ToByte(data);
        // 对密钥解密  
        byte[] keyBytes = StringUtil.base64ToByte(key);
        
        // 数据最大长度(分段处理)
           int maxDataSize = type?64:128;
//         int maxDataSize = type?256:512;
        byte[] bts = new byte[]{};
    	int index=0;
    	do {
    		index += maxDataSize;
    		// 取得私钥  
    		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
    		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
    		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
//    		Key privateKey = loadKeyForPKCS8(keyFactory, keyBytes, true);
    		// 对数据解密  
    		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm()); 
    		cipher.init(typeValue, privateKey);  
    		byte[] tempBts = cipher.doFinal(Arrays.copyOfRange(valueBts, index-maxDataSize, index<valueBts.length?index:valueBts.length));
    		byte[] result = Arrays.copyOf(bts, bts.length + tempBts.length);
    		System.arraycopy(tempBts, 0, result, bts.length, tempBts.length);
    		bts = result;
    	} while (index < valueBts.length);
        return type?StringUtil.toBase64(bts):new String(bts, "UTF-8");
    }
    
    /***
     * 将密钥以PKCS8的格式读取
     * @param keyFactory	密钥工厂
     * @param keyBuffer		密钥串
     * @param isPrivate		是否是私钥(true:私钥；false:公钥)
     * @return	可使用的key对象
     * @throws InvalidKeySpecException
     */
    public static Key loadKeyForPKCS8(KeyFactory keyFactory, byte[] keyBytes, boolean isPrivate) throws InvalidKeySpecException {
    	if(isPrivate) {
    		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
    		return keyFactory.generatePrivate(pkcs8KeySpec);
    	}
    	X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
    	return keyFactory.generatePublic(x509KeySpec);
    }
    
    /***
     * 将密钥以PKCS1的格式读取
     * @param keyFactory	密钥工厂
     * @param keyBuffer		密钥串
     * @param isPrivate		是否是私钥(true:私钥；false:公钥)
     * @return	可使用的key对象
     */
    public static Key loadKeyForPKCS1(KeyFactory keyFactory, byte[] keyBuffer, boolean isPrivate)
		    throws Exception {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBuffer);
		return (isPrivate?keyFactory.generatePrivate(keySpec):keyFactory.generatePublic(keySpec));
	}
  
//    /** 
//     * 用公钥进行加/解密
//     * @param data	数据(加密对应原文，解密对应的是密文)
//     * @param key	公钥
//     * @param type	操作类型(true:加密，false:解密)
//     * @return	处理出来的数据
//     * @throws Exception 
//     */  
//    public static String doCoderByPublicKey(String data, String key, boolean type) throws Exception {
//    	int typeValue = type?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE;
//    	byte[] valueBts = type?data.getBytes():StringUtil.base64ToByte(data);
//        // 对密钥解密  
//        byte[] keyBytes = StringUtil.base64ToByte(key);  
//        // 取得公钥  
//        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
//        Key publicKey = keyFactory.generatePublic(x509KeySpec);  
//        // 对数据解密  
//        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
//        cipher.init(typeValue, publicKey);  
//        byte[] bts = cipher.doFinal(valueBts);
//        return type?StringUtil.toBase64(bts):new String(bts, "UTF-8");
//    }
    
    /** 
     * 用公钥进行加/解密
     * @param data	数据(加密对应原文，解密对应的是密文)
     * @param key	公钥
     * @param type	操作类型(true:加密，false:解密)
     * @return	处理出来的数据
     * @throws Exception 
     */  
    public static String doCoderByPublicKey(String data, String key, boolean type) throws Exception {
    	int typeValue = type?Cipher.ENCRYPT_MODE:Cipher.DECRYPT_MODE;
    	byte[] valueBts = type?data.getBytes():StringUtil.base64ToByte(data);
        // 对密钥解密  
         byte[] keyBytes = StringUtil.base64ToByte(key);
        
        // 数据最大长度(分段处理)
        int maxDataSize = type?64:128;
        byte[] bts = new byte[]{};
    	int index=0;
    	do {
    		index += maxDataSize;
    		// 取得公钥  
    		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
    		Key publicKey = loadKeyForPKCS8(keyFactory, keyBytes, false);  
    		// 对数据解密  
    		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
    		cipher.init(typeValue, publicKey);  
    		byte[] tempBts = cipher.doFinal(Arrays.copyOfRange(valueBts, index-maxDataSize, index<valueBts.length?index:valueBts.length));
    		byte[] result = Arrays.copyOf(bts, bts.length + tempBts.length);
    		System.arraycopy(tempBts, 0, result, bts.length, tempBts.length);
    		bts = result;
    	} while (index < valueBts.length);
        return type?StringUtil.toBase64(bts):new String(bts, "UTF-8");
    }
    
    /** 
     * 初始化密钥 
     * @return	密钥对
     * @throws Exception 
     */  
    public static PassBean initKey() throws Exception {  
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);  
        keyPairGen.initialize(SIZE, new SecureRandom());  
        KeyPair keyPair = keyPairGen.generateKeyPair();  
        PassBean pb = new PassBean(); 
        if(KEY_ALGORITHM.equals("DSA")) {
        	// 公钥  
        	DSAPublicKey publicKey = (DSAPublicKey) keyPair.getPublic();  
        	// 私钥  
        	DSAPrivateKey privateKey = (DSAPrivateKey) keyPair.getPrivate();  
            pb.setPrivateKey(StringUtil.toBase64(privateKey.getEncoded()));
            pb.setPublicKey(StringUtil.toBase64(publicKey.getEncoded())); 
        } else if(KEY_ALGORITHM.equals("RSA")) {
        	// 公钥  
        	RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
        	// 私钥  
        	RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
        	pb.setPrivateKey(StringUtil.toBase64(privateKey.getEncoded()));
            pb.setPublicKey(StringUtil.toBase64(publicKey.getEncoded())); 
        }
        return pb;  
    }
    
//    /** 将字符串处理成base64字节 
//     * @throws IOException */
//    private static byte[] decryptBASE64(String str) throws IOException {
////    	return Base64.decodeBase64(str);		// 第三方jar包中的base64处理，RAS解码正常，但是与如果长度不足32位，则尾字母会出问题
//    	return new BASE64Decoder().decodeBuffer(str);	// JDK中的base64编码，会导致RSA解密时抛出长度大于256异常，不足32位一样有问题
//    }
//    
//    /** 将base64字节处理成字符串 */
//    private static String encryptBASE64(byte[] bts) {
////    	return Base64.encodeBase64String(bts);		// 第三方jar包中的base64处理，RAS解码正常，但是与如果位数不足64位，则尾字母会出问题
//    	return new BASE64Encoder().encode(bts);		// JDK中的base64编码，会导致RSA解密时抛出长度大于256异常，不足32位一样有问题
//    }
    
    /** 
     * @param args 
     * @throws Exception  
     */  
    public static void main(String[] args) throws Exception {  
//        //初始化密钥  
//        //生成密钥对  
//    	PassBean pb = CertCoder.initKey(); 
//        //公钥  
//        String publicKey = pb.getPublicKey();  
//        //私钥  
//        String privateKey = pb.getPrivateKey();
//        System.out.println("公钥："+publicKey);  
//        System.out.println("私钥："+privateKey);  
          
//        System.out.println("================密钥对构造完毕=============");  
//        
        String newStr = "{}";
        System.out.println("原文:"+newStr); 
        String encodeStr = CertCoder.doCoderByPublicKey(newStr, Context.publicKey, true);
//        String encodeStr = "fOncmC4vdstzWJIoBg1lE+1tgDFoRyN3UaZXp7G7N8RfA5ARusJUL0ceDvzU1M7qHX+dAPL8GzEb1wozVEa/fJjIJ0nMkTBsrz3JDzonhLwUvRNkGeVFm0NLbaZKjtZ/chXLKnItZqNjxAZpTau1b8tpe7T3t1t10yknWQsZRos=";
        System.out.println("密文:\n" + encodeStr);
        String oldStr = CertCoder.doCoderByPrivateKey(encodeStr, Context.privateKey, false);
        System.out.println("\n");
        System.out.println("解密:" + oldStr);
        
        
        // 原文base64处理与反base64，对比
//        String base64 = StringUtil.toBase64(newStr);
//        String newNewStr = StringUtil.base64ToString(base64);
//        System.out.println("原文直接base64解密：" + newNewStr);
    }  
}
