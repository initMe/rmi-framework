package com.service.rmi.control;

import java.rmi.RemoteException;
import java.util.Map;

import com.bean.service.RemoteBean;
import com.manager.MultiServerManager;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.FileService;

public class FileControlImpl implements FileService {

	/** 文件管理 */
	private MultiServerManager fileManager = RmiProxy.getServer(FileService.class);

	/** 依据数据选择对应的服务 */
	private FileService chooseService(Object bean) {
		return (FileService) fileManager.getRemoteService(bean);
	}

	@Override
	public String upload(String fileName, byte[] bts) throws RemoteException {
		return chooseService(fileName).upload(fileName, bts);
	}

	@Override
	public void heartbeat() throws RemoteException {}

	@Override
	public String upload(String fileName, Integer w, Integer h, byte[] bts) throws RemoteException {
		return chooseService(fileName).upload(fileName,w,h,bts);
	}

	@Override
	public byte[] download(String url) throws RemoteException {
		Map<Integer, RemoteBean> remoteMap = fileManager.getServerMap();
		for(RemoteBean rb : remoteMap.values()) {
			FileService fs = (FileService)rb.getService();
			if(fs.hasFile(url)) {
				return fs.download(url);
			}
		}
		return null;
	}
	
	@Override
	public boolean hasFile(String url) throws RemoteException {
		Map<Integer, RemoteBean> remoteMap = fileManager.getServerMap();
		for(RemoteBean rb : remoteMap.values()) {
			FileService fs = (FileService)rb.getService();
			if(fs.hasFile(url)) {
				return true;
			}
		}
		return false;
	}
}
