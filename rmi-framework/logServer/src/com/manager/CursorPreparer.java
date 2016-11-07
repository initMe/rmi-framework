/**   
 * Copyright (c) 版权所有 2010-2015
 * 产品名：   
 * 包名：com.manager   
 * 文件名：CursorPrepare.java   
 * 版本信息：   
 * 创建日期：2015-4-24-下午04:58:19
 *    
 */
package com.manager;

import com.mongodb.DBCursor;

/**
 * 
 * 类名：CursorPreparer 类描述： 数据处理接口 
 * 创建人：Administrator 修改时间：2015-4-24 下午04:58:19
 * 修改备注：
 * @version 1.0.0
 */
public interface CursorPreparer {
	DBCursor prepare(DBCursor cursor);
}
