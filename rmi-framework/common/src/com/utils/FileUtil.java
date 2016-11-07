package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.UUID;

/** 文件工具类 */
public class FileUtil {
	
	/**
	 * 获取唯一标识
	 * @return	但服务器的唯一标识(时间+序列号)
	 */
	public static String getOnlyOneFlag() {
		return UUID.randomUUID().toString();
	}
	
	/***
	 * 依据旧的文件名，获取本服务器唯一的文件名
	 * @param oldFileName	旧文件名(主要是需要扩展名)
	 * @return	唯一文件名
	 */
	public static String getOnlyOneFileName(String oldFileName) {
		String suffix = oldFileName.substring(oldFileName.lastIndexOf("."), oldFileName.length());
		return getOnlyOneFlag() + suffix;
	}
	
	/***
	 * 获取文件的md5值
	 * @param file	文件
	 * @return	文件的md5值
	 */
	public static String getFileMD5(String filePath) throws Exception {
		File file = new File(filePath);
	    if (!file.exists() || !file.isFile()) {
	        return null;
	    }
	    MessageDigest digest = null;
	    FileInputStream in = null;
	    byte buffer[] = new byte[8192];
	    int len;
	    try {
	        digest = MessageDigest.getInstance("MD5");
	        in = new FileInputStream(file);
	        while ((len = in.read(buffer)) != -1) {
	            digest.update(buffer, 0, len);
	        }
//	        BigInteger bigInt = new BigInteger(1, digest.digest());
//	        return bigInt.toString(16);		// 这两行代码有时候会导致输出的MD5值只有31位
	        return StringUtil.byteArrayToHex(digest.digest());
	    } catch (Exception e) {
	    	LoggerUtil.error(FileUtil.class, e);
	        throw e;
	    } finally {
	        try {
	            in.close();
	        } catch (Exception e) {
	        	LoggerUtil.error(FileUtil.class, e);
//	            e.printStackTrace();
	        }
	    }
	}
	
	
	/***
	 * 删除目录下所有文件(含子目录)
	 * @param filePath	文件路径
	 */
	public static void deleteFile(String filePath) {
		File f = new File(filePath);
		if(f.exists()) {
			doDelete(f);
		}
	}
	
	/**
	 * 开始执行删除
	 * @param file	准备删除的文件
	 */
	private static void doDelete(File file) {
		if(file.isDirectory()) {
			File[] fs = file.listFiles();
			for(File f : fs) {
				doDelete(f);
			}
		}
		file.delete();
	}
	
	public static void main(String[] args) {
//		try {
//			String md51 = FileUtil.getFileMD5("D:\\xxx\\1394776231970_2.cer");
//			String md52 = FileUtil.getFileMD5("D:\\work_code\\.metadata\\.me_tcat\\webapps\\AppManage\\cert\\1394776231970_2.cer");
//			System.out.println(md51);
//			System.out.println(md52);
//			System.out.println(md51.equalsIgnoreCase(md52));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		FileUtil.deleteFile("D:\\xxx\\b9aa42a9-f2ec-4143-ac6d-cfe5ffa42fbd");
	}
}
