
package com.bean.db;

import java.util.Date;

import com.bean.BaseBean;
import com.utils.JsonUtil;
import com.utils.ObjectUtil;

public class ID extends BaseBean {

	private static final long serialVersionUID = 8359088677417040990L;

	/** 数据id */
	protected Long id;

	/** 分页 */
	protected Page page;
    
    /** 数据创建时间 */
    private Long create_time = System.currentTimeMillis();
    private Long tempCreate_time = create_time;
    
    @SuppressWarnings("unused")
	private String create_time_view;
	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long createTime) {
		create_time = createTime;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/** 清空创建时间和修改时间字段 */
	public void cleanInitTimeValue() {
		if(tempCreate_time!=null && create_time!=null && tempCreate_time.equals(create_time)) {
			create_time = null;
		}
		tempCreate_time = null;
	}
	
	/** 清空所有初始字段 */
	public void cleanInitValue() {
		cleanInitTimeValue();
		ObjectUtil.cleanInitValue(this);
	}
	
	@Override
	public int hashCode() {
		if(id == null) {
			return super.hashCode();
		}
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ID other = (ID) obj;
		if (id!=null && id.equals(other.id)) {
			return true;
		}
		return super.equals(obj) || JsonUtil.objToJson(obj).equals(JsonUtil.objToJson(this));
	}

    @SuppressWarnings("deprecation")
	public String getCreate_time_view() {
    	if(create_time == null) {
    		return null;
    	}
        return new Date(create_time).toLocaleString();
    }
}
