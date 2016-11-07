package com.bean.db;

/** 字典表 */
public class Dictionary extends ID {
	private static final long serialVersionUID = 612102551132627482L;
	/** 字典种类 */
	private String type;
	/** 字典种类内的父code */
	private String parentCode;
	/** 字典种类内的code */
	private String code;
	/** 字典种类内的code的显示值 */
	private String name;
	/** 是否包含子节点(0:不包含；1：包含) */
	private Integer hasChildren = 0;
	
	/**
	 * 校验与该字典是否相同
	 * @param dict	字典对象
	 * @return	是否相同
	 */
	public boolean equalsDictionary(Dictionary dict) {
		StringBuffer selfFlag = new StringBuffer(type);
		selfFlag.append(parentCode);
		selfFlag.append(code);
		
		StringBuffer flag = new StringBuffer(dict.getType());
		flag.append(dict.getParentCode());
		flag.append(dict.getCode());
		
		if(selfFlag.toString().equals(flag.toString())) {
			return true;
		} 
		return equals(dict);
	}
	
	public String getParentCode() {
		return parentCode;
	}



	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Integer getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(Integer hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
