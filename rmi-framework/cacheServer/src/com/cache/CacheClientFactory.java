package com.cache;

import com.service.rmi.tools.CacheClient;

public class CacheClientFactory {
    private static final JedisCacheFacade cache=new JedisCacheFacade();
	public static CacheClient getCacheClient()
	{
		 return  cache.getClient();
	}
	
	public static JedisCacheFacade getJedisFacede()
	{
		return cache;
	}
	
	public static long getcurrentUsedCacheClient()
	{
		return cache.getcurrentUsedCacheClient();
	}
}
