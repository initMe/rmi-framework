package com.search.manager;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

import com.search.base.SearchEngineCore;
import com.search.base.SearchObject;
import com.utils.PackContentObject;

/** 数据源管理 */
public class DataManager {
	
	/** 源数据添加到lucene库中 */
	public static void add(Object obj, String dbName) throws Exception {
		SearchEngineCore se = SearchObject.getInstance().getLuceneContext(dbName);
		Document doc = PackContentObject.convertContentToDoc(obj);
	    se.getTw().addDocument(doc);
		se.commitIndex();
		se.refreshData();
		se.getNRTManager().maybeRefresh();
	}
	
	/** 依据检索条件，移除lucene库中对应数据  */
	public static void remove(Query query, String dbName) throws IOException {
		SearchEngineCore se = SearchObject.getInstance().getLuceneContext(dbName);
		se.remove(query);
		se.commitIndex();
		se.refreshData();
		se.getNRTManager().maybeRefresh();
	}
}
