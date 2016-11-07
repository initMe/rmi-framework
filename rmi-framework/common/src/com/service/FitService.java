package com.service;

import java.rmi.RemoteException;
import java.util.List;

import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Searcher;

/** 匹配校验 */
public class FitService {
	/** 单例对象 */
	private static FitService fitService = null;
	private static Object lock = new Object();
	private FitService() {}
	
	/** 获取单例对象 */
	public static FitService getInstance() {
		if(fitService == null) {
			synchronized (fitService==null?lock:fitService) {
				if(fitService == null) {
					fitService = new FitService();
				}
			}
		}
		return fitService;
	}
	
	/**
	 * 校验两组词句是否相匹配
	 * @param arg1	第一组词句
	 * @param arg2	第二组词句
	 * @return	true:相匹配，false:不匹配
	 * @throws RemoteException 
	 */
	public boolean checkFit(String arg1Word, String arg2Word) throws RemoteException {
		if(arg1Word==null || arg1Word.trim().equals("")) {
			return false;
		}
		if(arg2Word==null || arg2Word.trim().equals("")) {
			return false;
		}
		Searcher search = RmiProxy.getRemoteService(RemoteServiceType.searcher);
		List<String> arg1List = search.cutWord(arg1Word);
		List<String> arg2List = search.cutWord(arg2Word);
		for(String arg1 : arg1List) {
			for(String arg2 : arg2List) {
				if(arg1.equalsIgnoreCase(arg2)) {
					return true;
				}
			}
		}
		return false;
	}
}
