package com.service.rmi.control;

import java.rmi.RemoteException;
import java.util.List;

import com.bean.BaseBean;
import com.manager.MultiServerManager;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Searcher;

public class SearchControlImpl implements Searcher {

	/** 搜索管理 */
	private MultiServerManager searchManager = RmiProxy.getServer(Searcher.class);
	
	/** 依据数据选择对应的服务 */
	private Searcher chooseService(Object bean) {
		return (Searcher) searchManager.getRemoteService(bean);
	}
	
	@Override
	public void addElement(Object obj, String dbName) throws RemoteException {
		chooseService(null).addElement(obj, dbName);
	}

	@Override
	public void addEqualsQuery(String querySessionId, String key, String val,
			boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addEqualsQuery(querySessionId, key, val, isAnd);
	}

	@Override
	public void addLikeQuery(String querySessionId, String key, String val,
			boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addLikeQuery(querySessionId, key, val, isAnd);
	}

	@Override
	public void addNotQuery(String querySessionId, String key, String val)
			throws RemoteException {
		chooseService(querySessionId).addNotQuery(querySessionId, key, val);
	}

	@Override
	public void addParserQuery(String querySessionId, String key, String val,
			boolean parserIsAnd, boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addParserQuery(querySessionId, key, val, parserIsAnd, isAnd);
	}

	@Override
	public void addQuery(String querySessionId, String addQuerySessionId,
			boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addQuery(querySessionId, addQuerySessionId, isAnd);
	}

	@Override
	public void addRangleForBeginQuery(String querySessionId, String key,
			String beginVal, boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addRangleForBeginQuery(querySessionId, key, beginVal, isAnd);
	}

	@Override
	public void addRangleForEndQuery(String querySessionId, String key,
			String endVal, boolean isAnd) throws RemoteException {
		chooseService(querySessionId).addRangleForEndQuery(querySessionId, key, endVal, isAnd);
	}

	@Override
	public void addRangleQuery(String querySessionId, String key,
			String beginVal, String endVal, boolean isAnd)
			throws RemoteException {
		chooseService(querySessionId).addRangleQuery(querySessionId, key, beginVal, endVal, isAnd);
	}

	@Override
	public String createQuery() throws RemoteException {
		return chooseService(null).createQuery();
	}

	@Override
	public String createQuery(String dbName) throws RemoteException {
		return chooseService(null).createQuery(dbName);
	}

	@Override
	public List<?> doSearch(String querySessionId) throws RemoteException {
		return chooseService(querySessionId).doSearch(querySessionId);
	}

	@Override
	public void removeData(String querySessionId) throws RemoteException {
		chooseService(querySessionId).removeData(querySessionId);
	}

	@Override
	public void removeData(String querySessionId, String dbName)
			throws RemoteException {
		chooseService(querySessionId).removeData(querySessionId, dbName);
	}

	@Override
	public void heartbeat() throws RemoteException {
		chooseService(null).heartbeat();
	}

	@Override
	public List<String> cutWord(String word) throws RemoteException {
		// 随机使用
		return chooseService(word).cutWord(word);
	}

	@Override
	public void updateBaseData(BaseBean bb) throws RemoteException {
		chooseService(null).updateBaseData(bb);
	}

	@Override
	public void updateBaseData(BaseBean bb, boolean isCoverAllClass)
			throws RemoteException {
		chooseService(null).updateBaseData(bb, isCoverAllClass);
	}

	@Override
	public <T extends BaseBean> T doSearchOnce(String querySessionId)
			throws RemoteException {
		return chooseService(querySessionId).doSearchOnce(querySessionId);
	}

	@Override
	public void updateData(BaseBean bb, String dbName, boolean isCoverAllClass) throws RemoteException {
		chooseService(dbName).updateData(bb, dbName, isCoverAllClass);
	}
}
