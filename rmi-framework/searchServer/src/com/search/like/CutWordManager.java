package com.search.like;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;

import com.search.query.QueryManager;
import com.service.rmi.tools.Searcher;

public class CutWordManager {
	/** 单例 */
	private static CutWordManager cutWord;
	private CutWordManager() {}
	public static CutWordManager getInstance() {
		if(cutWord == null) {
			cutWord = new CutWordManager();
		}
		return cutWord;
	}
	
	/**
	 * 获取分词的数据
	 * @param text	原始数据
	 * @return	分词数据
	 */
	public List<String> getLikeWord(String text) {
		QueryManager qm = new QueryManager(Searcher.BASE_DB);
		Set<String> words = new HashSet<String>();
		try {
			qm.addParserQuery("", text, true, true);
			Term tm = new Term("", "要");
			Set<Term> termSet = new HashSet<Term>();
			termSet.add(tm);
			qm.getQuery().extractTerms(termSet);
			String queryStr = qm.getQuery().toString();
			queryStr = queryStr.replace("(", "").replace(")", "").replace(":", "");
			String[] orgWord = queryStr.split("\\+");
			List<String> tempList = Arrays.asList(orgWord);
			for(String temp : tempList) {
				temp = temp.trim();
				if(!temp.equals("") && !words.contains(temp)) {
					words.add(temp);
				}
			}
//			for(char chr : text.toCharArray()) {
//				if(chr == ' ') {
//					continue;
//				}
//				words.add(String.valueOf(chr));
//			}
//			words.add(text);
			List<String> wordList = new ArrayList<String>();
			wordList.addAll(words);
			return wordList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	
	public static void main(String[] args) {
		List<String> wordList = cutWord.getInstance().getLikeWord("我要西红柿 我要番茄");
		System.out.println(wordList);
		wordList = cutWord.getInstance().getLikeWord("喵星人");
		System.out.println(wordList);
	}
}
