package test;

import java.rmi.RemoteException;

import com.cache.JedisCacheManager;

public class ClearCache {
	
	public ClearCache() {
		try {
			new JedisCacheManager().cleanAllCache();
			System.out.println("缓存已清空");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new ClearCache();
	}
}
