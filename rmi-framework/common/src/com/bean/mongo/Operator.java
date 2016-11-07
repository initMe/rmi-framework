/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.bean.mongo   
* 文件名：Operator.java   
* 版本信息：   
* 创建日期：2015-4-27-下午06:14:18
*    
*/
package com.bean.mongo;

/**   
 * mongodb运算符
 * 类名：Operator   
 * 类描述：   
 * 创建人：Administrator    
 * 修改时间：2015-4-27 下午06:14:18   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public enum Operator
{
	/** 等于( = ) */
    eq("$eq", "="),
    /** 小于( < ) */
    lt("$lt", "<"),
    /** 大于( > ) */
    gt("$gt", ">"),
    /** 小于等于( <= ) */
    lte("$lte", "<="),
    /** 大于等于( >= ) */
    gte("$gte", ">="),
    /** 不等于( != ) */
    not("$ne", "!="),
    /** 包含( in(exp) ) */
    in("$in", "in(exp)"),
    /** 不包含( not in(exp) ) */
    not_in("$nin", "not in(exp)");
    
    private String mongoCode;
    private String sqlCode;
    
    Operator(String mongoCode, String sqlCode)
    {
        this.mongoCode = mongoCode;
        this.sqlCode = sqlCode;
    }
    
    /** 获取mongodb编码值 */
    public String getMongoCode()
    {
        return mongoCode;
    }
    /** 获取类sql的阅读值 */
    public String getSqlCode()
    {
        return sqlCode;
    }
}
