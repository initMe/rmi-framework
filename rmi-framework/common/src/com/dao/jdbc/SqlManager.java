package com.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.utils.ConfigUtil;
import com.utils.ObjectUtil;


/** 数据库管理抽象类 */
public class SqlManager {
	
	/** 连接池中初始化连接的数量 */
	private final static int initConnSize = 20;
	/** 获取连接失败后，可重新获取连接池的次数 */
	private final int reGetCount = 5;
	/** 获取连接失败后，再次重新获取连接的等待时间 */
	private final long reGetWaitTime = 5*1000;

	/** 数据库连接字符串 */
	private final static String sqlDriverName = ConfigUtil.getInstance().getStringValue("jdbc.driverName", "com.mysql.jdbc.Driver");
	/** 数据库连接账号 */
	private final static String sqlUserName = ConfigUtil.getInstance().getStringValue("jdbc.username", "root");
	/** 数据库连接密码 */
	private final static String sqlPassWord = ConfigUtil.getInstance().getStringValue("jdbc.password","123456");
	/** 数据库连接地址*/
	private final static String sqlPath = ConfigUtil.getInstance().getStringValue("jdbc.url", "jdbc:mysql://localhost:3306/bjxd?characterEncoding=utf8");
	/** 单例 */
	private static SqlManager sm = null;
	
	/** 连接池 */
	private static List<ConnectionBean> connBeanList = new ArrayList<ConnectionBean>();
	
	static {
		try {
			Class.forName(sqlDriverName);
			for(int i=0; i<initConnSize; i++) {
				ConnectionBean connBean = new ConnectionBean();
				Connection conn = createConnection();
				connBean.setConn(conn);
				connBeanList.add(connBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private SqlManager() {}
	
	public static synchronized SqlManager getInstance() {
		if(sm == null) {
			sm = new SqlManager();
		}
		return sm;
	}
	
	/** 创建连接 
	 * @throws SQLException */
	private static Connection createConnection() throws SQLException {
		return DriverManager.getConnection(sqlPath, sqlUserName, sqlPassWord);
	}
	
	/** 从连接池中取出一个未使用的连接，并标记成已使用状态 
	 * @throws Exception */
	private synchronized ConnectionBean getConnectionBean() throws Exception {
		ConnectionBean connBean = checkConnectionBean(0);
		if(connBean != null) {
			Connection conn = connBean.getConn();
			// 处理数据库连接超时
			if(!conn.isValid(5000)) {
				try {
					conn.close();
				} catch (Exception e) {}
				conn = createConnection();
				connBean.setConn(conn);
			}
			connBean.setUse(true);
			return connBean;
		}
		throw new Exception("数据库连接池中获取连接失败");
	}
	
	/** 检查连接池中的 */
	private ConnectionBean checkConnectionBean(int count) {
		if(count > reGetCount) {
			return null;
		}
		for(ConnectionBean connBean : connBeanList) {
			if(!connBean.isUse()) {
				return connBean;
			}
		}
		try {
			synchronized (this) {
				this.wait(reGetWaitTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return checkConnectionBean(++count);
	}
	
	/** 释放数据库连接 */
	private void closeConnectBean(ConnectionBean connBean) {
		connBean.setUse(false);
		synchronized (this) {
			this.notify();
		}
	}
	
	/**
	 * 执行查询操作
	 * @param clazz		返回的实体的类型
	 * @param executor	结果集处理器(为null则使用默认的)
	 * @param page		分页参数(为null, 则不按分页查询)
	 * @param sql		查询语句
	 * @param params	查询语句的参数
	 * @return	查询的结果集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <T> Object query(Class<T> clazz, QueryResultExecutor executor, Page page, String sql, Object... params) throws Exception {
		if(executor == null) {
			executor = QueryResultExecutor.defaultExecutor;
		}
		if(page != null) {
			sql = parsePageSql(clazz, page, sql);
		}
		ConnectionBean connBean = getConnectionBean();
		PreparedStatement p = connBean.getConn().prepareStatement(sql);
		if(params != null) {
			for(int i=0; i<params.length; i++) {
				p.setObject(i+1, params[i]);
			}
		}
		ResultSet rs = p.executeQuery();
		Object returnObj = executor.execute(clazz, rs);
		p.close();
		rs.close();
		closeConnectBean(connBean);
		return returnObj;
	}
	
	/***
	 * 将sql语句转换成分页语句
	 * (会重新复制分页参数)
	 * @param tableName	表名
	 * @param page	分页参数
	 * @param sql	原始语句
	 * @return	分页sql语句
	 * @throws Exception 
	 */
	private <T>String parsePageSql(Class<T> clazz, Page page, String sql) throws Exception {
		String tableName = Tables.getTable(clazz).getTableName();
		String countSql = "select count(*) as rowNum from " + tableName;
		// 先查询到总条数
		List<RowNum> pageList = (List<RowNum>) query(RowNum.class, null, null, countSql, null);
		// 补充分页参数
		if(pageList!=null && pageList.size()>0) {
			int totalRows = new Long(pageList.get(0).getRowNum()).intValue();
			int showSize = page.getShowSize();
			// 总行数
			page.setTotalRows(totalRows);
			// 总页数
			if(totalRows>0) {
				int pageCount = totalRows/showSize;
				page.setTotalPage(totalRows%showSize!=0?pageCount+1:pageCount);
			} else {
				page.setTotalPage(0);
			}
		}
		String pageSql = "select * from ("+sql+") as " 
							+ tableName + " order by " 
							+ page.getSortname() + " " 
							+ page.getSortorder() + " limit "
							+ (page.getCurrtPage()-1)*page.getShowSize()+","
							+ page.getShowSize();
		return pageSql;
	}
	
	/**
	 * 依据id查询
	 * @param clazz	返回的实体类型
	 * @param id	查询条件( "... where id="+id )
	 * @return	查询的结果集合
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public <T>T queryById(Class<T> clazz, QueryResultExecutor executor, Long id) throws Exception {
		String sql = "select * from "+Tables.getTable(clazz).getTableName()+" where id=?";
		List<T> list = (List<T>) query(clazz, executor, null, sql, id);
		if(list==null || list.size()==0) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 依据给的实体bean的非空的属性查询
	 * (非空：除[null, "", 0, '']以外的值)
	 * @param bean		实体Bean	(默认返回值类型也是此类型)
	 * @param executor	处理器
	 * @return	查询的结果集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> query(T bean, QueryResultExecutor executor) throws Exception {
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		map.remove("serialVersionUID");
		String sql = " select * from " + Tables.getTable(bean.getClass()).getTableName()
					+" where 1=1 ";
		for(String name : map.keySet()) {
			sql += " and " + name + "=? ";
		}
		return (List<T>) query(bean.getClass(), executor, null, sql, map.values().toArray());
	}
	
	/**
	 * 依据给的实体bean的非空的属性查询
	 * (非空：除[null, "", 0, '']以外的值)
	 * @param bean		实体Bean	(默认返回值类型也是此类型)
	 * @param executor	处理器
	 * @return	查询的结果集合
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForExter(T bean, QueryResultExecutor executor, String exterConditionsSql, Object... params) throws Exception {
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		map.remove("createTime");
		String sql = " select * from " + Tables.getTable(bean.getClass()).getTableName()
					+" where 1=1 ";
		for(String name : map.keySet()) {
			sql += " and " + name + "=? ";
		}
		sql += exterConditionsSql;
		List<Object> allParam = new ArrayList<Object>();
		allParam.addAll(map.values());
		if(params!=null && params.length>0) {
			for(Object param : params) {
				allParam.add(param);
			}
		}
		return (List<T>) query(bean.getClass(), executor, null, sql, allParam.toArray());
	}
	
	/** 
	 * 执行修改
	 * @param sql		修改语句
	 * @param params	参数集合
	 * @return	影响行数
	 * @throws Exception 
	 */
	public int update(String sql, Object... params) throws Exception {
		ConnectionBean connBean = getConnectionBean();
		PreparedStatement p = connBean.getConn().prepareStatement(sql);
		if(params != null) {
			for(int i=0; i<params.length; i++) {
				p.setObject(i+1, params[i]);
			}
		}
		int size = p.executeUpdate();
		p.close();
		closeConnectBean(connBean);
		return size;
	}
	
	/**
	 * 依据对象修改数据库
	 * @param bean	依照此对象来进行修改
	 * @return	影响行数
	 * @throws Exception
	 */
	public int modifyByBean(Object bean) throws Exception {
		String id = (String)ObjectUtil.getAttributeValue(bean, "id");
		String sql = "update "+Tables.getTable(bean.getClass()).getTableName()+" set ";
		Map<String, Object> map = ObjectUtil.getFields(bean);
		map.remove("id");
		for(String key : map.keySet()) {
			sql += key+"=?,";
		}
		sql = sql.substring(0, sql.length()-1);
		sql += " where id=? ";
		Collection<Object> c = map.values();
		List<Object> array = new ArrayList<Object>(c);
		array.add(id);
		return update(sql, array.toArray());
	}
	
	/**
	 * 依据对象的id删除数据库数据
	 * @param bean	依照此对象的id来删除
	 * @return	影响行数
	 * @throws Exception
	 */
	public int deleteByBean(Object bean) throws Exception {
		String id = (String)ObjectUtil.getAttributeValue(bean, "id");
		String sql = "delete from "+Tables.getTable(bean.getClass()).getTableName()+" where id=? ";
		return update(sql, id);
	}
	
	/**
	 * 依据对象的新增数据库数据
	 * @param bean	依照此对象的id来删除
	 * @return	影响行数
	 * @throws Exception
	 */
	public int insertByBean(Object bean) throws Exception {
		String sql = "insert into "+Tables.getTable(bean.getClass()).getTableName()+"(";
		Map<String, Object> map = ObjectUtil.getNotNullFields(bean);
		for(String key : map.keySet()) {
			sql += key+",";
		}
		sql = sql.substring(0, sql.length()-1);
		sql += ") values (";
		// 拼接?
		for(int i=0; i<map.size(); i++) {
			sql += "?,";
		}
		sql = sql.substring(0, sql.length()-1);
		sql += ");";
		return update(sql, map.values().toArray());
	}
}
