package com.search;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import com.bean.BaseBean;
import com.exception.Error;
import com.exception.SystemException;
import com.pool.ObjectPool;
import com.search.data.DataManager;
import com.search.like.CutWordManager;
import com.search.query.QueryManager;
import com.service.rmi.tools.Searcher;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

/** 搜索器的实现类，rmi接口的实现 */
public class SearchManager extends UnicastRemoteObject implements Searcher {
	public SearchManager() throws RemoteException {
		super();
	}
	
	public SearchManager(int port) throws RemoteException {
		super(port);
	}

	private static final long serialVersionUID = -4122823931495286351L;
	
	@Override
	public void addElement(Object obj, String dbName) throws RemoteException {
		try {
			DataManager.add(obj, dbName);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), "远程服务内部出错", e);
//			e.printStackTrace();
			throw new RemoteException("远程服务内部出错", e);
		}
	}

	@Override
	public String createQuery(String dbName) throws RemoteException {
		QueryManager qm = new QueryManager(dbName);
		return ObjectPool.getInstance().addObject(qm);
	}
	
	@Override
	public String createQuery() throws RemoteException {
		return createQuery(Searcher.BASE_DB);
	}

	@Override
	public void addEqualsQuery(String querySessionId, String key, String val,
			boolean isAnd) throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addEqualsQuery(key, val, isAnd);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public void addLikeQuery(String querySessionId, String key, String val,
			boolean isAnd) throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addLikeQuery(key, val, isAnd);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_remote_exec_error, e);
		}
		
	}
	
	@Override
	public void addParserQuery(String querySessionId, String key, String val, boolean parserIsAnd, boolean isAnd) throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addParserQuery(key, val, parserIsAnd, isAnd);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public void addNotQuery(String querySessionId, String key, String val)
			throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addNotQuery(key, val);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
//			e.printStackTrace();
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public void addRangleForBeginQuery(String querySessionId, String key, String beginVal, boolean isAnd) throws RemoteException {
		addRangleQuery(querySessionId, key, beginVal, String.valueOf(Integer.MAX_VALUE), isAnd);
	}
	
	@Override
	public void addRangleForEndQuery(String querySessionId, String key, String endVal, boolean isAnd)
			throws RemoteException {
		addRangleQuery(querySessionId, key, String.valueOf(Integer.MIN_VALUE), endVal, isAnd);
	}
	@Override
	public void addRangleQuery(String querySessionId, String key,
			String beginVal, String endVal, boolean isAnd)
			throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addRangleQuery(key, beginVal, endVal, isAnd);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
//			e.printStackTrace();
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}
	
	@Override
	public void addQuery(String querySessionId, String addQuerySessionId, boolean isAnd) throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			QueryManager addQm = ObjectPool.getInstance().getObject(addQuerySessionId);
			if(qm==null || addQm==null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			qm.addQuery(addQm.getQuery(), isAnd);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
//			e.printStackTrace();
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public List<?> doSearch(String querySessionId) throws RemoteException {
		try {
			QueryManager qm = ObjectPool.getInstance().getObject(querySessionId);
			if(qm == null) {
				LoggerUtil.error(this.getClass(), "操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)");
				throw new SystemException(Error.system_error, 
						new NullPointerException("操作已超时,远程查询对象因超时被清理。(与sessionId对应的远程QueryManager对象为null)"));
			}
			return qm.doSearch();
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
//			e.printStackTrace();
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public void removeData(String querySessionId) throws RemoteException {
		QueryManager query = ObjectPool.getInstance().getObject(querySessionId);
		removeData(querySessionId, query.getDbName());
	}
	
	@Override
	public void removeData(String querySessionId, String dbName) throws RemoteException {
		QueryManager query = ObjectPool.getInstance().getObject(querySessionId);
		try {
			DataManager.remove(query.getQuery(), dbName);
		} catch (IOException e) {
			LoggerUtil.error(this.getClass(),"远程搜索服务内部出错", e);
//			e.printStackTrace();
			throw new SystemException(Error.system_remote_exec_error, e);
		}
	}

	@Override
	public List<String> cutWord(String word) throws RemoteException {
		return CutWordManager.getInstance().getLikeWord(word);
	}
	
	@Override
	public void updateBaseData(BaseBean bb) throws RemoteException {
		updateBaseData(bb, true);
	}
	
	@Override
	public void updateBaseData(BaseBean bb, boolean isCoverAllClass) throws RemoteException {
		updateData(bb, Searcher.BASE_DB, isCoverAllClass);
	}
	
	@Override
	public void heartbeat() throws RemoteException {}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseBean> T doSearchOnce(String querySessionId)
			throws RemoteException {
		List<?> resultList = doSearch(querySessionId);
		if(resultList!=null && resultList.size()>0) {
			return (T) resultList.get(resultList.size()-1);
		}
		return null;
	}

	@Override
	public void updateData(BaseBean bb, String dbName, boolean isCoverAllClass) throws RemoteException {
		try {
			QueryManager qm = new QueryManager(dbName);
			Object flag = ObjectUtil.getAttributeValue(bb, "id");
			if(flag == null) {
				flag = ObjectUtil.getAttributeValue(bb, "userId");
				if(flag == null) {
					flag = ObjectUtil.getAttributeValue(bb, "user_id");
					qm.addEqualsQuery("user_id", flag.toString(), true);
				} else {
					qm.addEqualsQuery("userId", flag.toString(), true);
				}
			} else {
				qm.addEqualsQuery("id", flag.toString(), true);
			}
			// 是否按类型匹配
			if(isCoverAllClass) {
				qm.addEqualsQuery("class", bb.getClass().getName(), true);
			}
			
			List<?> bbList = (List<?>) qm.doSearch();
			if(bbList!=null && bbList.size()>0) {
				// 如果有多条，则覆盖提取最后一条作为基础数据
				BaseBean baseData = (BaseBean) bbList.get(bbList.size()-1);
				DataManager.remove(qm.getQuery(), Searcher.BASE_DB);
				// 合并数据
				ObjectUtil.insertObj(baseData, bb);
				// 添加合并后的数据
				DataManager.add(baseData, Searcher.BASE_DB);
				LoggerUtil.info(this.getClass(), "更新搜索库时，发现已有数据，执行覆盖操作!");
			} else {
				DataManager.add(bb, Searcher.BASE_DB);
				LoggerUtil.info(this.getClass(), "未发现已有数据，更新成功!");
			}
		} catch (RemoteException e) {
			LoggerUtil.error(this.getClass(),e);
			throw new SystemException(Error.system_error, e);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(),"工具类使用异常",e);
			throw new SystemException(Error.system_utils_error, e);
		}
	}
}
