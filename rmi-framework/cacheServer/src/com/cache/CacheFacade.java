package com.cache;

import com.service.rmi.tools.CacheClient;

public interface CacheFacade {
	public CacheClient getClient();
}
