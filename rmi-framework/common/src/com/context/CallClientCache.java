package com.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 请求号对应客户端缓存的枚举类 */
public enum CallClientCache {
	/** 空间缓存 */
	space(101, new int[]{102, 103});
	
	/** 需要返回数据的call(客户端需要做缓存的call) */
	private int call;
	/** 会影响到call请求的返回值,的相关call值 */
	private int[] affectCall;
	
	/** 需要返回数据的call对应的枚举值 */
	private final static Map<Integer, CallClientCache> callMap = new HashMap<Integer, CallClientCache>();
	/** 会产生数据影响的call对应枚举合集 */
	private final static Map<Integer, List<CallClientCache>> affectCallMap = new HashMap<Integer, List<CallClientCache>>();
	static {
		CallClientCache ccs[] = CallClientCache.values();
		for(CallClientCache cc : ccs) {
			callMap.put(cc.getCall(), cc);
		}
		for(CallClientCache cc : ccs) {
			int affects[] = cc.getAffectCall();
			for(int affect : affects) {
				List<CallClientCache> ccList = affectCallMap.get(affect);
				if(ccList == null) {
					ccList = new ArrayList<CallClientCache>();
					affectCallMap.put(affect, ccList);
				}
				ccList.add(cc);
			}
		}
	}
	
	/** 需要返回数据的call对应的枚举值 */
	public static Map<Integer, CallClientCache> getCallMap() {
		return callMap;
	}
	/** 会产生数据影响的call对应枚举合集 */
	public static Map<Integer, List<CallClientCache>> getAffectCallMap() {
		return affectCallMap;
	}

	private CallClientCache(int call, int[] affectCall) {
		this.call = call;
		this.affectCall = affectCall;
	}

	/** 需要返回数据的call(客户端需要做缓存的call) */
	public int getCall() {
		return call;
	}

	/** 会影响到call请求的返回值,的相关call值 */
	public int[] getAffectCall() {
		return affectCall;
	}
}
