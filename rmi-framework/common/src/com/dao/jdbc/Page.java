package com.dao.jdbc;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * 项目名称：root 类名称：Page 类描述：分页基类 创建人：张昌路 创建时间：2013-11-30 上午3:14:38 修改人：张昌路
 * 修改时间：2013-11-30 上午3:14:38 修改备注：
 * 
 * @version
 * 
 */
public class Page {
	/**
	 * 第一页
	 */
	public static final Integer _FIRST_PAGE = 1;
	/**
	 * 默认显示条数
	 */
	public static final Integer _DEFAULT_SIZE = 11;
	private Integer totalPage = 0;// 总页数
	private Integer totalRows = 0;// 总行数
	private Integer currtPage = 1;// 当前页
	private Integer showSize = 11;// 显示条数
	private List<Map<String, Object>> pageData;// 数据集合
	private String sortname;// 排序列名
	private String sortorder;// 排序顺序
	private Map<String, String> otherParam;// 其他参数
	// 查询所需
	private Integer firstRow;// 第一条数据行数

	public Integer getCurrtPage() {
		return currtPage;
	}

	public void setCurrtPage(Integer currtPage) {
		this.currtPage = currtPage;
	}

	public Integer getShowSize() {
		return showSize;
	}

	public void setShowSize(Integer showSize) {
		this.showSize = showSize;
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		this.sortname = sortname;
	}

	public String getSortorder() {
		return sortorder;
	}

	public void setSortorder(String sortorder) {
		this.sortorder = sortorder;
	}

	public Map<String, String> getOtherParam() {
		return otherParam;
	}

	public void setOtherParam(Map<String, String> otherParam) {
		this.otherParam = otherParam;
	}

	public List<Map<String, Object>> getPageData() {
		return pageData;
	}

	public void setPageData(List<Map<String, Object>> pageData) {
		this.pageData = pageData;
	}

	public Integer getTotalPage() {
		// 总行数
		Integer allRows = this.getTotalRows();
		// 每页显示条数
		Integer pageSize = this.getShowSize();
		int allr = allRows / pageSize;
		int sy = allRows % pageSize;
		// 如果有剩余则+1
		if (sy > 0) {
			allr += 1;
		}
		this.setTotalPage(allr);
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public Integer getFirstRow() {
		int first = (this.getCurrtPage() - 1) * this.getShowSize();
		// 最小为0
		first = first < 0 ? 0 : first;
		this.setFirstRow(first);
		return firstRow;
	}

	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}
}
