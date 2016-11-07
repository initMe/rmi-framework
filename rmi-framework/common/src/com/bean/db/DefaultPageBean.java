package com.bean.db;

import com.bean.BaseBean;

/** 缺省的page参数bean */
public class DefaultPageBean extends BaseBean {

	private static final long serialVersionUID = 8359088677417040990L;

    private Page page;
    
	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	/** 设置每页显示多少条 */
	public void setShowNum(int num) {
		if(num > 0) {
			if(page == null) {
				page = new Page();
			}
			page.setShowCount(num);
		}
	}
}
