/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：test   
 * 文件名：SearchTest.java   
 * 版本信息：   
 * 创建日期：2016-9-23-下午03:21:05
 */
package test;

import java.rmi.RemoteException;

import com.bean.business.User;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Searcher;

/**     
 * 类名：SearchTest   
 * 类描述：   
 * 创建人：SPC(F12_end)
 * 修改人：SPC(F12_end)
 * 修改时间：2016-9-23 下午03:21:05   
 * 修改备注：   
 * @version 1.0.0
 */
public class SearchTest {
	public SearchTest() throws RemoteException {
		User user = new User();
		user.setName("aaaa");
		user.setId(1L);
		// 获取服务
		Searcher s = RmiProxy.getRemoteService(RemoteServiceType.searcher);
		// 添加数据
		s.addElement(user, Searcher.BASE_DB);
		// 更新数据
		s.updateData(user, Searcher.BASE_DB, true);
		
		// 创建查询
		String sessionId = s.createQuery();
		// 添加条件
		s.addEqualsQuery(sessionId, "name", "aaaa", true);
		// 查询结果
		User result = s.doSearchOnce(sessionId);
	}
	public static void main(String[] args) {
		try {
			new SearchTest();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
