package com.main;

import java.util.List;

import com.search.query.QueryManager;
import com.service.rmi.tools.Searcher;

public class SearchTest {
	
	@SuppressWarnings("unchecked")
	public SearchTest() {
		QueryManager qm = new QueryManager(Searcher.BASE_DB);
		List<Object> list = (List<Object>) qm.doSearch();
		System.out.println(list);
		
	}
	public static void main(String[] args) {
		new SearchTest();
	}
}
