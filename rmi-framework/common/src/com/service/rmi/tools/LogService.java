/**   
* Copyright (c) 版权所有 2010-2015
* 产品名：   
* 包名：com.service.rmi   
* 文件名：MongoService.java   
* 版本信息：   
* 创建日期：2015-4-27-上午10:21:11
*    
*/
package com.service.rmi.tools;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.bean.db.ID;
import com.manager.GroupParams;
import com.manager.QueryParams;
import com.service.rmi.control.LogControlImpl;

/**   
 *    
 * 类名：MongoService   
 * 类描述：   
 * 创建人：Administrator    
 * 修改时间：2015-4-27 上午10:21:11   
 * 修改备注：   
 * @version 1.0.0   
 *    
 */
public interface LogService extends RemoteBaseService
{
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public LogService controlChatService = new LogControlImpl();
	
    /**
    * add   
    * (添加)   
    * @param obj 实体对象
    * @throws RemoteException     
    * @since  1.0.0
    */
    public void add(ID obj) throws RemoteException;
    
    /**
    * add   
    * (添加)   
    * @param clazz 实体类
    * @param map 封装装了要添加的数据的字段
    * @throws RemoteException     
    * @since  1.0.0
    */
    public void add(Class<?> clazz,Map<String,Object> map) throws RemoteException;
    
    /**
     * delete   
     * @param bean	参数
     * @throws RemoteException     
     * @since  1.0.0
     */
     public void deleteForId(ID bean) throws RemoteException;
     
     /**
      * 依据不为空的参数删除数据
      * @param bean	参数对象
      * @throws RemoteException     
      * @since  1.0.0
      */
      public void delete(ID bean) throws RemoteException;
      
      /**
       * 依据查询条件删除数据
       * @param qp	参数对象
       * @throws RemoteException     
       * @since  1.0.0
       */
       public void delete(QueryParams qp) throws RemoteException;
    
    /**
    * delete   
    * (这里描述这个方法适用条件 – 可选)   
    * @param clazz
    * @param map map类型查询参数 例：map.put("id",1);
    * @throws RemoteException     
    * @since  1.0.0
    */
    public void delete(Class<?> clazz,Map<String,Object> map) throws RemoteException;
    
    /**
    * deleteAll   
    * (这里描述这个方法适用条件 – 可选)   
    * @param clazz
    * @throws RemoteException     
    * @since  1.0.0
    */
    public void deleteAll(Class<?> clazz) throws RemoteException;
    
    /**
     * modify   
     * (修改)   
     * @param query 查询参数
     * @param updateMap 修改数据
     * @throws RemoteException     
     * @since  1.0.0
     */
     public void modifyForId(ID bean) throws  RemoteException;
     
     /**
      * modify   
      * (修改)   
      * @param query 查询参数
      * @param bean 修改数据
      * @throws RemoteException     
      * @since  1.0.0
      */
      public void modify(QueryParams query, ID bean) throws  RemoteException;
    
    /**
    * modify   
    * (修改)   
    * @param query 查询参数
    * @param updateMap 修改数据
    * @throws RemoteException     
    * @since  1.0.0
    */
    public void modify(QueryParams query,Map<String,Object> updateMap) throws  RemoteException;
    
    /**
     * findOne   
     * (查询数据，适用于单条数据的查询)   
     * @param obj 实体对象
     * @return     
     * @since  1.0.0
     */
     public <T> T findOne(T obj) throws RemoteException;
     
      /**
      * findOne   
      * (单条数据查询，指定返回列)   
      * @param query 查询参数
      * @param write 指定返回列(可为null)
      * @return
      * @throws RemoteException     
      * @since  1.0.0
      */
      public <T> T findOne(QueryParams query,QueryParams write) throws RemoteException;
      
      /**
       * find   
       * (数据查询，查询所有字段)   
       * @param id 查询参数
       * @return 实体对象集合
       * @throws RemoteException     
       * @since  1.0.0
       */
      public <T extends ID>List<T> findOneForId(T bean) throws RemoteException;
      
       /**
        * find   
        * (数据查询，查询所有字段)   
        * @param queryMap 查询参数
        * @return 实体对象集合
        * @throws RemoteException     
        * @since  1.0.0
        */
      public <T extends ID>List<T> find(T bean) throws RemoteException;
       
      /**
      * find   
      * (数据查询，查询所有字段)   
      * @param queryMap 查询参数
      * @return 实体对象集合
      * @throws RemoteException     
      * @since  1.0.0
      */
      public <T extends ID>List<T> find(QueryParams queryMap) throws RemoteException;
      
      /**
      * find   
      * (数据查询，指定返回字段)   
      * @param queryMap 查询参数
      * @param writeMap 指定返回字段
      * @return 实体对象集合
      * @throws RemoteException     
      * @since  1.0.0
      */
      public <T extends ID>List<T> find(QueryParams queryMap,QueryParams writeMap) throws RemoteException;


     /**
  	 * 聚合查询(mongodb-driver3.2)
  	 * @param queryParams	查询对象
  	 * @param groupParams	分组对象
  	 * @return	返回结果集合(分组数据，聚合数据等) 
  	 * @see GroupParams getResultKey()
  	 */
  	public List<Map<String, Object>> findGroup(QueryParams queryParams, GroupParams groupParams) throws RemoteException;
  	
  	/**
  	 * 聚合查询(mongodb-driver3.2)，返回一条数据
  	 * @param queryParams	查询对象
  	 * @param groupParams	分组对象
  	 * @return	返回结果 (分组数据，聚合数据等) 
  	 * @see GroupParams getResultKey()
  	 */
  	public Map<String, Object> findGroupOne(QueryParams queryParams, GroupParams groupParams) throws RemoteException;
}
