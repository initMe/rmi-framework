/**   
 * Copyright (c) 版权所有 2010-2016 驭缘科技有限公司  
 * 产品名：   
 * 包名：com.bean.service   
 * 文件名：FileRemoteBean.java   
 * 版本信息：   
 * 创建日期：2016-7-25-下午01:48:47
 */
package com.bean.service;
/**     
 * 类名：FileRemoteBean   
 * 类描述：   
 * 创建人：SPC(F12_end)
 * 修改人：SPC(F12_end)
 * 修改时间：2016-7-25 下午01:48:47   
 * 修改备注：   
 * @version 1.0.0
 */
public class FileRemoteBean extends RemoteBean {
	private static final long serialVersionUID = -5510578076799694287L;
	
	private String webName;
	private String savePath;
	private String webIp;
	private Integer webPort;
	private String webFileName;
	private String webPath;
	public FileRemoteBean() {
		super();
	}
	
	public FileRemoteBean(String url) {
		url = url.substring(url.indexOf("://")+3, url.length());
		// rmi的url
		if(url.indexOf(":") >= 0) {
			webIp = url.substring(0, url.indexOf(":"));
			String portStr = url.substring(url.indexOf(":")+1, url.indexOf("/"));
			webPort = Integer.parseInt(portStr);
		} 
		// http的url
		else {
			webIp = url.substring(0, url.indexOf("/"));
			webPort = 80;
		}
		url = url.substring(url.indexOf("/")+1, url.length());
		String[] paths = url.split("/");
		webName = paths[0];
		webPath = "";
		for(int i=1; i<paths.length-1; i++) {
			webPath += paths[i]+"/";
		}
		// 是否包含文件
		if(url.lastIndexOf(".") >= 0) {
			webFileName = paths[paths.length-1];
		} else {
			webPath += paths[paths.length-1];
		}
	}
	
	public static void main(String[] args) {
		FileRemoteBean frb = new FileRemoteBean("http://12.12.12.12/aut/1/2/3/dd.txt");
		System.out.println(frb.toString());
	}
	
	@Override
	public String toString() {
		if(super.serviceIp == null) {
			return toWebString();
		}
		return super.toString();
	}
	
	/** 获取web信息 */
	public String toWebString() {
		return "http://"+webIp+":"+webPort+"/"+webName+"/"+webPath+(webFileName==null?"":webFileName);
	}
	
	public String getWebName() {
		return webName;
	}
	public void setWebName(String webName) {
		this.webName = webName;
	}
	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	public String getWebIp() {
		return webIp;
	}
	public void setWebIp(String webIp) {
		this.webIp = webIp;
	}
	public Integer getWebPort() {
		return webPort;
	}
	public void setWebPort(Integer webPort) {
		this.webPort = webPort;
	}

	public String getWebFileName() {
		return webFileName;
	}

	public void setWebFileName(String webFileName) {
		this.webFileName = webFileName;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}
}

