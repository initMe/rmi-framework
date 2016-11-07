package com.search.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;

import com.service.rmi.tools.Searcher;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;
/**
 *  @version 1.0
 */
public  class SearchObject {

	/** lucene上下文 */
	private static Map<String,SearchEngineCore> luceneContextMap = new HashMap<String, SearchEngineCore>();
	/** 所有的搜索库的搜索索引集合 */
	private static List<IndexSearcher> searcherList = new ArrayList<IndexSearcher>();
	/** 所有搜索库的读取器集合 */
	private static List<IndexReader> readerList = new ArrayList<IndexReader>();
	private static SearchObject instance;
	private SearchObject() {}
	
	/** 初始化对象*/
	public static SearchObject getInstance(){
		if(instance==null){
			init();
			instance = new SearchObject();
		}
		return instance;
	}
	
	/**创建Lucene索引对象*/
	public static void init(){
		try{
			ConfigUtil cu = ConfigUtil.getInstance();
			luceneContextMap.put(Searcher.BASE_DB, new SearchEngineCore(Searcher.BASE_DB, cu.getSearchUserDb()));
//			luceneContextMap.put(Searcher.VIE_DB, new SearchEngineCore(Searcher.VIE_DB, cu.getSearchVieDb()));
//			luceneContextMap.put(Searcher.WORKING_DB, new SearchEngineCore(Searcher.WORKING_DB, cu.getSearchWorkingDb()));
			for(String key : luceneContextMap.keySet()) {
				IndexSearcher is = luceneContextMap.get(key).getSearcher();
				searcherList.add(is);
				readerList.add(is.getIndexReader());
			}
		}catch(Exception e){
			LoggerUtil.error(SearchObject.class, e);
		}
	}

	
	/**
	 *  Function:取得所有查询对象
	 *  @return 返回所有查询对象
	 */
	public  IndexSearcher[] getSearchers(){
		return (IndexSearcher[]) searcherList.toArray();
	}
	
	/**取得所有查询对象Reader*/
	public  IndexReader[] getSearcherReads(){
		return readerList.toArray(new IndexReader[readerList.size()]);
	}
	
	/**
	 *  Function:取得制定查询对象,如果dbName==null，则返回总库(即所有库)
	 *  @param dbName	lucene库名
	 *  @return
	 */
	public  IndexSearcher getSearcher(String dbName){
		IndexSearcher indexSearch = null;
		if(dbName == null) {
			IndexReader[] readers = getSearcherReads();
			MultiReader mReaders = new MultiReader(readers);
			indexSearch = new IndexSearcher(mReaders);
		} else {
			indexSearch = luceneContextMap.get(dbName).getSearcher();
		}
		return indexSearch;
	}
	
	public  NRTManager getNRTManager(String dbName){
		return getLuceneContext(dbName).getNRTManager();
	}
	
	public  SearchEngineCore getLuceneContext(String dbName){
		return luceneContextMap.get(dbName);
	}
}
