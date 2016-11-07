package com.manager;

import java.rmi.RemoteException;
import java.util.List;

import com.bean.Version;
import com.context.CallClientCache;
import com.context.CacheContext;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.CacheClient;

/** 客户端缓存管理类 */
public class CallCacheManager {
	
	/**
	 * 客户端是否需要更新自己缓存
	 * @param call	请求号
	 * @param version	版本号
	 * @return	本次返回的数据对应的最新版本号<br/>
	 * (null:不需要新的数据；-1:与缓存无关；>0:需要的新数据的版本号)
	 * @throws RemoteException 
	 */
	public static Long needUpdate(int call, String json, Version versionBean) throws RemoteException {
		if(versionBean == null) {
			return -1L;
		}
		Long version = versionBean.getVersion();
		Long userId = versionBean.getUserId();
		CacheClient cc = RmiProxy.getRemoteService(RemoteServiceType.cache);
		// 属于获取类的请求，准备获取缓存校验版本号
		if(CallClientCache.getCallMap().containsKey(call)) {
			Long tempVersion = (Long) cc.getMapValue(CacheContext.cache_call_client_version, call+"_"+userId);
			if(tempVersion==null || !tempVersion.equals(version)) {
				return tempVersion;
			}
		} 
		// 属于影响获取的类的请求，准备更新缓存中的版本号
		if(CallClientCache.getAffectCallMap().containsKey(call)) {
			List<CallClientCache> callList = CallClientCache.getAffectCallMap().get(call);
			if(callList != null) {
				Long newVersion = System.currentTimeMillis();
				for(CallClientCache callCache : callList) {
					cc.setMapValue(CacheContext.cache_call_client_version, callCache.getCall()+"_"+userId, newVersion);
				}
			}
		}
		return 0L;
	}
	
	/***
	 * 检测是否可以从缓存中取数据
	 * @param call	请求号
	 * @param json	请求数据
	 * @param versionBean	关联数据
	 * @return	如果检测到缓存有数据，则返回
	 * @throws RemoteException 
	 */
	public static Object checkGetCache(int call, String json, Version versionBean) throws RemoteException {
		if(versionBean == null) {
			return null;
		}
		Long userId = versionBean.getUserId();
		if(userId!=null && userId.intValue()>0) {
			// 检查是否是可以从缓存获取的请求
			if(CallClientCache.getCallMap().containsKey(call)) {
				CacheClient cc = RmiProxy.getRemoteService(RemoteServiceType.cache);
				return cc.getMapValue(CacheContext.cache_call_server, String.valueOf(call)+String.valueOf(userId)+json);
			}
		}
		return null;
	}
	
	/** 检查是否可以向缓存写入数据 */
	public static void checkAddCache(int call, String json, Version versionBean, Object returnValue) throws RemoteException {
		if(versionBean == null) {
			return;
		}
		Long userId = versionBean.getUserId();
		if(userId!=null && userId.intValue()>0) {
			// 验证是否是会影响缓存的请求
			List<CallClientCache> cscList = CallClientCache.getAffectCallMap().get(call);
			if(cscList != null) {
				for(CallClientCache csc : cscList) {
					CacheClient cc = RmiProxy.getRemoteService(RemoteServiceType.cache);
					cc.setMapValue(CacheContext.cache_call_server, String.valueOf(csc.getCall())+String.valueOf(userId)+json, returnValue);
				}
			}
		}
	}
}
