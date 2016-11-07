package com.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.exception.Error;
import com.exception.SystemException;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;

/** 过滤文字管理类 */
public class WordFilterManager {
	private String initFileName = "filterWord.txt";
	/** 配置文件的相对位置 */
	private String filePath = File.separator + "conf" + File.separator;
	
	private static List<String> filterWrodList = new ArrayList<String>();
	private static WordFilterManager wfm = null;
	/** 单例锁 */
	private static Object lock = new Object();
	private WordFilterManager() {
		init();
	}
	public static WordFilterManager getInstanse() {
		if(wfm == null) {
			synchronized (lock) {
				if(wfm == null) {
					wfm = new WordFilterManager();
				}
			}
		}
		return wfm;
	}
	/**
	 * 初始化
	 */
	public void init() {
		BufferedReader br = null;
		try {
//			String fileName = ConfigUtil.class.getResource(filePath).getFile();
			URL url = ConfigUtil.class.getResource("/"+initFileName);
			String fileUrl = null;
			if(url==null || url.toURI().getPath()==null) {
				url = ConfigUtil.class.getResource(filePath+initFileName);
				if(url==null || url.toURI().getPath()==null) {
					fileUrl = System.getProperty("user.dir") + filePath + initFileName;
				}else {
					fileUrl = url.toURI().getPath();
				}
			} else {
				fileUrl = url.toURI().getPath();
			}
			br = new BufferedReader(new FileReader(fileUrl));
			String filterWord = null;
			while((filterWord=br.readLine()) != null) {
				filterWrodList.add(filterWord.trim());
			}
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_config_read_error, e);
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					LoggerUtil.error(this.getClass(), e);
//					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * 检查敏感字符
	 * @param word	原始字符
	 * @return	是否敏感(true:需要过滤，false:无敏感信息)
	 */
	public boolean checkWord(String word) {
		return getFilterWord(word)!=null;
	}
	
	/**
	 * 检查敏感字符
	 * @param word	原始字符
	 * @return	敏感的字符(null：代表传入字符串不包含任何敏感信息)
	 */
	public String getFilterWord(String word) {
		if(filterWrodList.contains(word)){
			return word;
		}
		for(String filterWord : filterWrodList) {
			if(word.indexOf(filterWord) >= 0) {
				return filterWord;
			}
		}
		return null;
	}
}
