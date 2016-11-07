package com.woyao.system.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMISocketFactory;

import org.springframework.web.multipart.MultipartFile;

import com.bean.service.FileRemoteBean;
import com.exception.Error;
import com.exception.SystemException;
import com.manager.RmiRemoteExecutor;
import com.service.RemoteServiceType;
import com.service.proxy.RmiProxy;
import com.service.rmi.tools.Controllor;
import com.service.rmi.tools.FileService;
import com.utils.ConfigUtil;
import com.utils.LoggerUtil;
import com.utils.StringUtil;

/** 文件服务rmi相关的启动 */
public class FileServerRuntime {
	private static final long serialVersionUID = -3988613159557877948L;
	private Integer rmiSocketPort = ConfigUtil.getInstance().getIntegerValue("file.service.rmi.socket.port", 37081);
	private static FileServiceImpl fileServiceInstance = null;
	private static boolean inited = false;
	private FileRemoteBean fileRemoteBean = new FileRemoteBean();
	
	/** 初始化rmi环境 */
	public synchronized void init() throws Exception {
		if(!inited) {
			inited = true;
		} else {
			LoggerUtil.info(this.getClass(), "FileServerRuntime不能重复初始化");
			return;
		}
		String rmiIp = ConfigUtil.getInstance().getStringValue("file.service.rmi.ip", "127.0.0.1");
		Integer rmiPort = ConfigUtil.getInstance().getIntegerValue("file.service.rmi.port", 37080);
		String rmiName = ConfigUtil.getInstance().getStringValue("file.service.rmi.serviceName", "FileService");
		String webIp = rmiIp;
		Integer webPort = ConfigUtil.getInstance().getIntegerValue("file.web.port", 8080);
		String webName = ConfigUtil.getInstance().getStringValue("file.web.name", "FileServer");
		String savePath = ConfigUtil.getInstance().getStringValue("file.save.path", "c:/woyao/source/");
		fileRemoteBean.setServiceIp(rmiIp);
		fileRemoteBean.setServicePort(rmiPort);
		fileRemoteBean.setServiceName(rmiName);
		fileRemoteBean.setServiceType(RemoteServiceType.file);
		fileRemoteBean.setWebIp(webIp);
		fileRemoteBean.setWebPort(webPort);
		fileRemoteBean.setWebName(webName);
		fileRemoteBean.setSavePath(savePath);
		startRmi();
	}
	
	/** 启动rmi服务 
	 * @throws Exception */
	private void startRmi() throws Exception {
		fileServiceInstance = new FileServiceImpl(fileRemoteBean.getServicePort());
		RMISocketFactory.setSocketFactory(new RmiRemoteExecutor(rmiSocketPort));
		Registry r = LocateRegistry.createRegistry(fileRemoteBean.getServicePort());
		r.rebind(fileRemoteBean.getServiceName(), fileServiceInstance);
		 // 向控制器注册服务
		Controllor controllor = RmiProxy.getControlService();
		controllor.registerService(fileRemoteBean);
		
		RmiProxy.setThisRemoteBean(fileRemoteBean);
	}
	
	/** 
	 * http上传的文件后续处理 (集群服务管理)
	 * @param file	需要存放的文件
	 * @return	存放文件的外部下载地址
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file, Integer w,Integer h) throws IOException {
		FileService fs = RmiProxy.getRemoteService(RemoteServiceType.file);
		String fileName = file.getOriginalFilename();
		byte[] bts = file.getBytes();
		return fs.upload(fileName, w, h, bts);
	}
	public String uploadFile(MultipartFile file ) throws IOException {
		FileService fs = RmiProxy.getRemoteService(RemoteServiceType.file);
		String fileName = file.getOriginalFilename();
		byte[] bts = file.getBytes();
		return fs.upload(fileName, null, null, bts);
	}
	/** 
	 * http下载的文件后续处理 (集群服务管理)
	 * @param url	下载的文件路径(需要base64转义)
	 * @return	存放文件的外部下载地址
	 * @throws IOException
	 */
	public byte[] downloadFile(String url) {
		String fileUrl = StringUtil.base64ToString(url);
		File f = new File(fileUrl);
		if(f.exists() && f.canRead()) {
			FileInputStream fin = null;
			ByteArrayOutputStream byteIn = null;
			try {
				byteIn = new ByteArrayOutputStream();
				fin = new FileInputStream(f);
				byte bts[] = new byte[1024];
				int length = 0;
				while((length=fin.read(bts)) > 0) {
					byteIn.write(bts, 0, length);
					byteIn.flush();
				}
				return byteIn.toByteArray();
			} catch (Exception e) {
				throw new SystemException(Error.system_error, e);
			} finally {
				if(fin != null) {
					try {
						fin.close();
					} catch (IOException e) {
						LoggerUtil.error(this.getClass(), e);
					}
				}
				if(byteIn != null) {
					try {
						byteIn.close();
					} catch (IOException e) {
						LoggerUtil.error(this.getClass(), e);
					}
				}
			}
		}
		String errStr = "文件 ["+fileUrl+"] "+(f.exists()?"不能读取":"不存在");
		LoggerUtil.error(this.getClass(), errStr);
		throw new SystemException(Error.system_error, errStr);
	}

	/** 获取rmi的穿防火墙端口 */
	public Integer getRmiSocketPort() {
		return rmiSocketPort;
	}
	/** 获取rmi注入的实例 */
	public static FileServiceImpl getFileServiceInstance() {
		return fileServiceInstance;
	}
	/** 获取文件服务的环境信息 */
	public FileRemoteBean getFileRemoteBean() {
		return fileRemoteBean;
	}
}
