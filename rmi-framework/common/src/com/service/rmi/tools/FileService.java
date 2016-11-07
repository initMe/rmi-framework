package com.service.rmi.tools;

import java.rmi.RemoteException;

import com.service.rmi.control.FileControlImpl;

/** 文件传输服务 */
public interface FileService extends RemoteBaseService {
	/** 获取远程调用的控制类实现(仅在集群情况下选择调用时使用) */
	public FileService controlFileService = new FileControlImpl();
	
	/***
	 * 上传文件
	 * @param fileName	文件名(主要是需要扩展名)
	 * @param w	需要生成压缩的缩略图宽度(w为null，则代表不生成缩略图)
	 * @param h	需要生成压缩的缩略图高度(h为null，则代表不生成缩略图)
	 * @param bts	文件内容
	 * @return	上传后的下载路径
	 */
	public String upload(String fileName,Integer w,Integer h,byte[] bts) throws RemoteException;
	/***
	 * 上传文件
	 * @param fileName	文件名(主要是需要扩展名)
	 * @param bts	文件内容
	 * @return	上传后的下载路径
	 */
	public String upload(String fileName,byte[] bts) throws RemoteException;
	
	/**
	 * 下载url对应的文件
	 * @param url	上传时返回的url(http可下载的url地址)
	 * @return	文件数据
	 */
	public byte[] download(String url) throws RemoteException;
	
	/**
	 * 检查服务器上是否有该路径的文件
	 * hasFile(这里用一句话描述这个方法的作用)   
	 * (这里描述这个方法适用条件 – 可选)   
	 * @param url
	 * @return
	 * @throws RemoteException    
	 * @return boolean   
	 * @exception    
	 * @since  1.0.0
	 */
	public boolean hasFile(String url) throws RemoteException;

}
