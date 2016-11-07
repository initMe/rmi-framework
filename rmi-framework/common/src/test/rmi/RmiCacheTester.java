package test.rmi;

import com.exception.SystemException;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.CacheClient;

/** rmi缓存测试类 */
public class RmiCacheTester {
	public RmiCacheTester() {
		try {
			CacheClient cc = RmiProxy.getRemoteService(RemoteServiceType.cache);
			System.out.println("ok -- "+cc);
		} catch (SystemException e) {
			e.printStackTrace();
		} 
	}
	public static void main(String[] args) {
		new RmiCacheTester();
	}
}