package com.search.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.BytesRef;

import com.exception.Error;
import com.exception.SystemException;
import com.search.base.SearchEngineCore;
import com.search.base.SearchObject;
import com.service.rmi.tools.Searcher;
import com.utils.LoggerUtil;
import com.utils.PackContentObject;

/** 条件检索管理类 */
public class QueryManager {
	/** 检索核心条件 */
	private BooleanQuery bq = new BooleanQuery();
	
	private String dbName;
	
	public QueryManager() {
		this.dbName = Searcher.BASE_DB;
	}
	
	/**
	 * 查询管理的对象创建
	 * @param dbName	lucene库名称{Searcher.BASE_DB || Searcher.USER_DB 等}
	 */
	public QueryManager(String dbName) {
		this.dbName = dbName;
	}
	
	public void addQuery(Query q, boolean isAnd) {
		bq.add(q, isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/** 
	 * 检索完全匹配的数据(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * 相当于 key.val==val校验
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addEqualsQuery(String key, String val, boolean isAnd) {
		bq.add(new TermQuery(new Term(key, val.toLowerCase())), isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/***
	 * 前缀搜索(匹配开头几个字的结果集)<br/>
	 * 相当于 key.val.indexOf(val) == 0
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addPrefixQuery(String key, String val, boolean isAnd) {
		bq.add(new PrefixQuery(new Term(key, val.toLowerCase())), isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/***
	 * 多列搜索(可以同时检索多个列)<br/>
	 * 相当于 key.val.indexOf(val) == 0
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addFuzzyArrayQuery(String[] keys, String[] vals, boolean isAnd) {
		SearchEngineCore sc =  SearchObject.getInstance().getLuceneContext(dbName);
        
		Query query = null;
		try {
			Occur[] ocs = new Occur[keys.length];
			for(int i=0; i<ocs.length; i++) {
				ocs[i] = isAnd?Occur.MUST:Occur.SHOULD;
			}
			query = MultiFieldQueryParser.parse(sc.getVersion(), vals, keys, ocs, sc.getAnalyzer());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		bq.add(query, isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/** 
	 * 检索相似数据(包含val的数据)
	 * <br/>(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 key.val.indexOf(val)校验)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addLikeQuery(String key, String val, boolean isAnd) {
		bq.add(new WildcardQuery(new Term(key, "*"+val.toLowerCase()+"*")), isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/** 
	 * 检索相似数据(与val相似的数据,可以有个别不匹配的单个字母,数字,汉字,符号)
	 * <br/>(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且(&&)匹配
	 */
	public void addFuzzyQuery(String key, String val, boolean isAnd) {
		bq.add(new FuzzyQuery(new Term(key, val.toLowerCase())), isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/**
	 * 检索逐词相似数据(与val相似的数据, 会对val进行分词,把分词填充入查询条件)
	 * <br/>(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 val.charArray() 逐词匹配)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param parserIsAnd	把条件分词之后进行匹配的规则，是否是且(&&)匹配
	 * @param isAnd	与其他query匹配，是否是且(&&)匹配
	 * @throws ParseException
	 */
	public void addParserQuery(String key, String val, boolean parserIsAnd, boolean isAnd) {
		try {
			Query query = getParserQuery(key, val.toLowerCase(), parserIsAnd);
			bq.add(query, isAnd?Occur.MUST:Occur.SHOULD);
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_error, e);
		}
	}
	
	/** 获取分词查询 */
	private Query getParserQuery(String key, String val, boolean parserIsAnd) throws ParseException {
		SearchEngineCore sc =  SearchObject.getInstance().getLuceneContext(dbName);
		QueryParser qp = new QueryParser(sc.getVersion(), key, sc.getAnalyzer());
		qp.setDefaultOperator(parserIsAnd?Operator.AND:Operator.OR);
		return qp.parse(val);
	}


	/** 
	 * 检索出在此范围内的数据(包含范围边缘)(当只有一个条件时，isAnd是true或false都可以)<br/>
	 * (相当于 beginVal＜=key.val && key.val＜=endVal校验)<br/>
	 * (例: beginVal="2014-8-8"  endVal="2014-9-1")
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 * @param isAnd	是否是且匹配(true:&&匹配   false:||匹配)
	 */
	public void addRangleQuery(String key, String beginVal, String endVal, boolean isAnd) {
		bq.add(new TermRangeQuery(key, new BytesRef(beginVal.toLowerCase()), new BytesRef(endVal.toLowerCase()), true, true), isAnd?Occur.MUST:Occur.SHOULD);
	}
	
	/** 
	 * 检索不包含val的数据<br/>
	 * (相当于 !key.val.indexOf(val) 校验)
	 * @param key	要验证的索引的列名
	 * @param val	该列要匹配的值
	 */
	public void addNotQuery(String key, String val) {
		bq.add(new WildcardQuery(new Term(key, "*"+val.toLowerCase()+"*")), Occur.MUST_NOT);
	}
	
	/***
	 * 开始执行查询
	 * @return	查询出来的对象集
	 * @throws Exception
	 */
	public List<?> doSearch() {
		try {
			IndexSearcher	indexSearch = SearchObject.getInstance().getSearcher(dbName);
			List<Object> searcheResult = new ArrayList<Object>();
			TopScoreDocCollector topCollector = TopScoreDocCollector.create(10000, true);
			indexSearch.search(bq, topCollector); 
			TopDocs topDocs = topCollector.topDocs();//取得查询结果 
			int resultCount=topDocs.totalHits;
			for(int i=0;i<resultCount;i++){
				ScoreDoc sd = topDocs.scoreDocs[i];
				Document doc = indexSearch.doc(sd.doc);
				//转换Document对象为内容对象 
				Object content = PackContentObject.convertDocToContent(doc, sd.score);
				//加入到结果列表 返回给前台页面获取
				searcheResult.add(content);
			}
			SearchEngineCore sec = SearchObject.getInstance().getLuceneContext(dbName);
			sec.releaseSearcher(indexSearch);
			return searcheResult;
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_error, e);
		}
	}
	
	/** 获取拼接完成的query对象 */
	public Query getQuery() {
		return bq;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
