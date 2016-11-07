package com.manager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.bean.service.RemoteBean;
import com.exception.Error;
import com.exception.SystemException;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.RemoteBaseService;
import com.utils.BeanSort;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;
import com.utils.StringUtil;


/** 集群服务管理类 */
public class MultiServerManager implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 服务群 */
	private final Map<Integer, RemoteBean> serverMap = Collections.synchronizedMap(new HashMap<Integer, RemoteBean>());
	
	/** 将其他的服务集合加入到自身 */
	public void intoSelf(MultiServerManager ms) {
		intoSelf(ms.getServerMap());
	}
	
	/** 将其他的服务集合加入到自身 */
	public void intoSelf(Map<Integer, RemoteBean> rbMap) {
		for(Integer key : rbMap.keySet()) {
			RemoteBean remoteBean = rbMap.get(key);
			// 已存在该服务
			if(serverMap.containsKey(key)) {
				RemoteBean localBean = serverMap.get(key);
				remoteBean.setService(localBean.getService());
				serverMap.put(key, remoteBean);
			} else {
				serverMap.put(key, remoteBean);
				remoteBean.setService(null);
			}
		}
	}
	
	/** 新增一个服务，返回该服务对应的key */
	public synchronized Integer addServer(RemoteBean value) {
		if(!serverMap.containsValue(value)) {
			int nextKey = getNextKey();
			value.setBroken(false);
			serverMap.put(getNextKey(), value);
			return nextKey;
		}
		for(Integer key : serverMap.keySet()) {
			if(serverMap.get(key).equals(value)) {
				serverMap.get(key).setBroken(false);
				return key;
			}
		}
		return -1;
	}
	
	/** 获取缺省远程服务 */
	public RemoteBean getRemoteServer() {
		if(serverMap.size() == 0) {
			return null;
		}
		return serverMap.get(getSortKey().get(0));
	}
	
	/**
	 * 依据处理的存储对象选用对应服务器
	 * @param bean	依据存储的对象
	 * @param ms	存储器
	 * @return	选择出的对应的服务
	 */
	public RemoteBaseService getRemoteService(Object bean) {
		if(bean == null) {
			return getRemoteServer().getService();
		}
		RemoteBean rb = getRemoteServer(bean);
		RemoteBaseService rbs = rb.getService();
		try {
			rbs.heartbeat();
		} catch (Exception e) {
			try {
				rb.setBroken(true);
				RmiProxy.getControlService().badService(rb);
			} catch (Exception e1) {
				throw new SystemException(Error.system_remote_control_error, e1, "上报连接失败事，控制中心连接失败 ["+rb.toString()+"]");
			}
			throw new SystemException(Error.system_remoteService_notopen_error, e, "选用的系统连接失败 ["+rb.toString()+"]");
		}
		return rbs;
	}
	
	/** 依据存储对象获取远程服务 */
	public RemoteBean getRemoteServer(Object saveBean) {
		saveBean = saveBean==null?0:saveBean;
		Object id = null;
		if(saveBean instanceof Long || saveBean instanceof Integer || saveBean instanceof String) {
			id = saveBean;
		} else if(saveBean instanceof Float || saveBean instanceof Double) {
			id = saveBean.toString();
		} else {
			id = ObjectUtil.getAttributeValue(saveBean, "id");
		}
		int hash = id!=null?(StringUtil.toMD5(id.toString())).hashCode():saveBean.hashCode();
		hash = hash>0?hash:Math.abs(hash);
		List<Integer> list = getSortKey();
		switch (list.size()) {
			case 0: {
				throw new SystemException(Error.system_error, "未找到可用的服务");
			}
			case 1: {
				return serverMap.get(list.get(0));
			}
			default: {
				int chooseKey = 0;
				for(int key : list) {
					if(key <= hash) {
						chooseKey = key;
					} else {
						break;
					}
				}
				RemoteBean rb = serverMap.get(chooseKey);
				if(rb.isBroken()) {
					LoggerUtil.error(this.getClass(), "原对应服务已损坏 ["+rb.toString()+"]");
					// 随机选用其他的服务
					for(Integer key : list) {
						RemoteBean tempRb = serverMap.get(key);
						if(!tempRb.isBroken()) {
							LoggerUtil.info(this.getClass(), "原对应服务已损坏 ,选用其他的可用服务["+tempRb.toString()+"]");
							return tempRb;
						}
					}
				}
				return rb;
			}
		}
	}
	
	/** 获取排序后的存储key */
	public List<Integer> getSortKey() {
		List<Integer> list = new ArrayList<Integer>(serverMap.keySet());
		BeanSort<Integer> bs = new BeanSort<Integer>();
		bs.sortList(list);
		return list;
	}
	
	/** 获取到下一个节点的key */
	private int getNextKey() {
		List<Integer> list = getSortKey();
		
		switch (list.size()) {
			case 0: {
				return 0;
			}
			case 1: {
				return Integer.MAX_VALUE;
			}
			default: {
				int val = list.get(1) - list.get(0);
				for(int i=1; i<list.size()-1; i++) {
					int tempVal = list.get(i+1) - list.get(i);
					if(val < Math.abs(tempVal)*0.9) {
						return list.get(i) + tempVal/2;
					}
				}
				return val/2;
			}
		}
	}
	
	/** return pool */
	public Map<Integer, RemoteBean> getServerMap() {
		return serverMap;
	}
	
	/**
	 * 随机选择一个服务
	 * @return	所选中的服务
	 */
	public RemoteBean getRemoteServerByRandom() {
		List<Integer> keys = getSortKey();
		int index = new Random().nextInt(keys.size());
		return serverMap.get(keys.get(index));
	}

	public static void main(String[] args) {
		MultiServerManager ms = new MultiServerManager();
		for(int i=0; i<200; i++) {
			RemoteBean rb = new RemoteBean();
			rb.setServiceType(i);
			System.out.println(ms.addServer(rb));
		}
		System.out.println(ms.getSortKey());
	}
}
