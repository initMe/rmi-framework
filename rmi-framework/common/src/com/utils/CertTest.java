package com.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class CertTest {

    public static RSAPublicKey loadPublicKeyFromFile(String keyPath)
            throws Exception {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    keyPath)));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(line);
                    sb.append('\r');
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return loadPublicKey(sb.toString());
    }

    public static RSAPublicKey loadPublicKey(String publicKeyStr)
            throws Exception {
        byte[] buffer = StringUtil.base64ToByte(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }
    
    public static String encode(RSAPublicKey publicKey, String oldStr) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ByteArrayOutputStream out = null;
        byte[] bts = oldStr.getBytes();
        try {
            out = new ByteArrayOutputStream();
            int BLOCK_SIZE = 128 - 11;
            for (int i = 0; i < bts.length; i += BLOCK_SIZE) {
                int leftBytes = bts.length - i;
                int length = (leftBytes <= BLOCK_SIZE) ? leftBytes : BLOCK_SIZE;
                out.write(cipher.doFinal(bts, i, length));
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return StringUtil.toBase64(out.toByteArray());
    }

    public static void main(String[] args) throws Exception {
    	RSAPublicKey publicKey = loadPublicKeyFromFile("C:\\Users\\F12_end\\Desktop\\rsaPublic_key.pem");
    	String encodeStr = CertTest.encode(publicKey, "asdwwwF12");
    	System.out.println(encodeStr);
    }
}
