package com.service.rmi.tools;

import java.rmi.RemoteException;
import java.util.List;

import com.bean.BaseBean;
import com.service.rmi.control.SearchControlImpl;

/** 搜索器接口 */
public interface Searcher extends RemoteBaseService {

	/** 实时用户的lucene库的库名 */
	public String BASE_DB = "base_db";
	
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public Searcher controlSearcher = new SearchControlImpl();
	
	/**
	 * 源数据添加到lucene库中
	 * @param obj	存储对象
	 * @param dbName	lucene库名{Searcher.WOYAO_DB || Searcher.RESUME_DB 等}
	 * @throws RemoteException
	 */
	public void addElement(Object obj, String dbName) throws RemoteException;
	
	/**
	 * 添加另外一个查询条件到本条件
	 * @param querySessionId	创建出来的查询条件的远程sessionId
	 * @param addQuerySessionId	在该条件上要添加的查询条件的远程sessionId
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addQuery(String querySessionId, String addQuerySessionId, boolean isAnd) throws RemoteException;
	
	/**
	 * 开始创建查询条件
	 * @return	查询条件对应的远程sessionId(如果是非远程操作/调用，可不做处理)
	 */
	public String createQuery() throws RemoteException;
	
	/**
	 * 开始创建查询条件
	 * @param dbName	lucene库名{Searcher.WOYAO_DB || Searcher.RESUME_DB 等}
	 * @return	查询条件对应的远程sessionId(如果是非远程操作/调用，可不做处理)
	 */
	public String createQuery(String dbName) throws RemoteException;
	
	/** 
	 * 检索完全匹配的数据(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * 相当于 key.val==val校验
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addEqualsQuery(String querySessionId, String key, String val, boolean isAnd) throws RemoteException;
	
	/** 
	 * 检索相似数据(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 key.val.indexOf(val)校验)
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addLikeQuery(String querySessionId, String key, String val, boolean isAnd) throws RemoteException;
	
	/**
	 * 检索逐词相似数据(与val相似的数据, 会对val进行分词,把分词填充入查询条件)
	 * <br/>(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 val.charArray() 逐词匹配)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param parserIsAnd	把条件分词之后进行匹配的规则，是否是且(&&)匹配
	 * @param isAnd	与其他query匹配，是否是且(&&)匹配
	 */
	public void addParserQuery(String querySessionId, String key, String val, boolean parserIsAnd, boolean isAnd) throws RemoteException;
	
	/** 
	 * 检索出在此范围内的数据(包含范围边缘)(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 beginVal＜=key.val && key.val＜=endVal校验)<br/>
	 * (例: beginVal="2014-8-8"  endVal="2014-9-1")
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且匹配(true:&&匹配   false:||匹配)
	 */
	public void addRangleQuery(String querySessionId, String key, String beginVal, String endVal, boolean isAnd) throws RemoteException;
	
	/** 
	 * 检索出在此范围内的数据(包含范围边缘)(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 beginVal＜=key.val)<br/>
	 * (例: beginVal="2014-8-8"  endVal="2014-9-1")
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param beginVal	大于该值
	 * @param isAnd	是否是且匹配(true:&&匹配   false:||匹配)
	 */
	public void addRangleForBeginQuery(String querySessionId, String key, String beginVal, boolean isAnd) throws RemoteException;
	
	/** 
	 * 检索出在此范围内的数据(包含范围边缘)(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 key.val＜=endVal校验)<br/>
	 * (例: beginVal="2014-8-8"  endVal="2014-9-1")
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param endVal	小于该值
	 * @param isAnd	是否是且匹配(true:&&匹配   false:||匹配)
	 */
	public void addRangleForEndQuery(String querySessionId, String key, String endVal, boolean isAnd) throws RemoteException;
	
	/** 
	 * 检索不包含val的数据<br/>
	 * (相当于 !key.val.indexOf(val) 校验)
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 */
	public void addNotQuery(String querySessionId, String key, String val) throws RemoteException;
	
	/***
	 * 开始执行查询
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @return	查询出来的对象集
	 * @throws Exception
	 */
	public List<?> doSearch(String querySessionId) throws RemoteException;
	
	/***
	 * 开始执行查询
	 * @param querySessionId	查询条件对应的远程sessionId(如果是非远程操作/调用，可为null)
	 * @return	查询出来的对象集
	 * @throws Exception
	 */
	public <T extends BaseBean> T doSearchOnce(String querySessionId) throws RemoteException;
	
	/**
	 * 移除搜索库数据<br/>
	 * (主要依据userId属性删除)
	 * @param bb	数据对象
	 * @param dbName	lucene库名{Searcher.WOYAO_DB || Searcher.RESUME_DB 等}
	 */
	public void removeData(String querySessionId) throws RemoteException;
	
	/**
	 * 移除搜索库数据<br/>
	 * (主要依据userId属性删除)
	 * @param bb	数据对象
	 * @param dbName	lucene库名{Searcher.WOYAO_DB || Searcher.RESUME_DB 等}
	 */
	public void removeData(String querySessionId, String dbName) throws RemoteException;
	
	/**
	 * 借用搜索库来实现分词
	 * @param word	原句
	 * @return	分词集合
	 * @throws RemoteException
	 */
	public List<String> cutWord(String word) throws RemoteException;
	
	/**
	 * 更新woyao库中的一个数据
	 * @param bb	要更新的数据
	 * @param isCoverAllClass	是否按同类名更新
	 * @param dbName	lucene库名{Searcher.WOYAO_DB || Searcher.RESUME_DB 等}
	 * @throws RemoteException
	 */
	public void updateData(BaseBean bb, String dbName, boolean isCoverAllClass) throws RemoteException;
	
	/**
	 * 更新base库中的一个数据
	 * @param bb	要更新的数据
	 * @throws RemoteException
	 */
	public void updateBaseData(BaseBean bb) throws RemoteException;
	
	/**
	 * 更新base库中的数据
	 * @param bb	要更新的数据
	 * @param isCoverAllClass	是否按同类名更新
	 * @throws RemoteException
	 */
	public void updateBaseData(BaseBean bb, boolean isCoverAllClass) throws RemoteException;
	
}
