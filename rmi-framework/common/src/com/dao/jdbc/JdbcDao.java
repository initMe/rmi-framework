package com.dao.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.exception.DataBaseException;
import com.exception.Error;
import com.utils.ObjectUtil;

/** 数据库管理实现 */
public abstract class JdbcDao<T> {
	
	private SqlManager sm = SqlManager.getInstance();
	private Class<T> beanClass;
	
	/** 获取jdbc管理类 */
	public SqlManager getSqlManager() {
		return sm;
	}
	
	public JdbcDao() {
		beanClass = ObjectUtil.getGeneric(this.getClass());
	}
	
	/***
	 * 新增数据
	 * @param t	数据对象
	 * @return	插入是否成功
	 */
	public boolean insert(T t) {
		try {
			return  sm.insertByBean(t)>0;
		} catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
	}

	/**
	 * 删除数据
	 * @param t	参照对象t的id属性
	 * @return	删除是否成功
	 */
	public boolean delete(T t) {
		try {
			sm.deleteByBean(t);
		} catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
		return false;
	}

	/***
	 * 修改对象
	 * @param t	参照对象t的id属性
	 * @return	修改是否成功
	 */
	public boolean update(T t) {
		try {
			sm.modifyByBean(t);
		} catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
		return false;
	}

	/***
	 * 按对象的id查询对象
	 * @param id	id值
	 * @return	查询出来的对象
	 */
	public T queryById(Long id) {
		try {
			return sm.queryById(beanClass, null, id);
		} catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
	}
	/**
	 * 查询表中的所有数据
	 * @return	所有表数据对象
	 */
	public List<T> queryAll() {
		try {
			T user = ObjectUtil.initObject(beanClass, null);
			return sm.query(user, null);
		} catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
	}
	
	/**
	 * 通过sql语句查询
	 * @param sql	sql语句
	 * @param params	问号传参
	 * @return    查询结果
	 */
	public List<T> query(String sql, Object... params) {
		try {
			return (List<T>) getSqlManager().query(beanClass, null, null, sql, params);
		}
		catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
	}
	
	/**
	 * 通过sql语句查询
	 * @param sql	sql语句
	 * @param params	问号传参
	 * @return    查询结果
	 */
	public T queryOne(String sql, Object... params) {
		try {
			List<T> list = query(sql, params);
			return list!=null&&list.size()>0?list.get(0):null;
		}
		catch (Exception e) {
			throw new DataBaseException(Error.database_error, e);
		}
	}
	
	/**
	 * 分页查询
	 * @param page	分页数据
	 * @param sql	sql语句
	 * @param input	参数集合
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Page getPageList(Page page, String sql, Object... input) {
		try {
			QueryResultExecutor qre = new QueryResultExecutor() {
				@Override
				public List execute(Class returnClass, ResultSet rs) throws Exception {
					List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
					// 数据集的列
					ResultSetMetaData meta = rs.getMetaData();
					// 列长度
					int count = meta.getColumnCount();
					// 每列的列名
					List<String> countNameList = new ArrayList<String>();
					// 遍历列
					for(int i=1; i<=count; i++) {
						countNameList.add(meta.getColumnName(i));
					}
					// 遍历结果集
					while(rs.next()) {
						Map<String, Object> rsMap = new HashMap<String, Object>();
						for(String countName : countNameList) {
							Object arrtValue = rs.getObject(countName);
							rsMap.put(countName, arrtValue.toString());
						}
						rsList.add(rsMap);
					}
					return rsList;
				}
			};
			page.setPageData((List<Map<String, Object>>) sm.query(beanClass, qre, page, sql, input));
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询，以对象列不为[null,"",0]的为条件
	 * @param t	条件对象
	 * @return	结果集
	 */
	public List<T> queryForObject(T t) {
		try {
			return sm.query(t, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询，以对象列不为[null,"",0]的为条件
	 * @param t	条件对象
	 * @param exterConditionsSql	附加sql条件(例如：and xxx=?)
	 * @param params	附加条件的问号参数值
	 * @return	结果集
	 */
	public List<T> queryForObjectExter(T t, String exterConditionsSql, Object... params) {
		try {
			sm.queryForExter(t, null, exterConditionsSql, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
