package com.woyao.system.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamAdapter;
import org.springframework.web.multipart.MultipartFile;

import com.utils.LoggerUtil;

public class ApacheFtpUtil {

	private FTPClient ftp;

	/**
	 * FTP 连接
	 * @param ip   地址
	 * @param port 端口号
	 * @param name 用户名
	 * @param pwd  密码
	 * @param path 上传到ftp服务器的路径
	 * @return
	 * @throws Exception
	 */
	public boolean connect(String ip, int port, String name, String pwd, String path) throws Exception {
		ftp = new FTPClient();
		ftp.connect(ip, port);
		ftp.login(name, pwd);
		ftp.setControlEncoding("UTF-8");
		ftp.changeWorkingDirectory(File.separator);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			LoggerUtil.error(this.getClass(), "登录FTP服务器[" + ip + ":" + port + "]失败!ReplyCode:" + reply);
			return false;
		}
		ftp.changeWorkingDirectory(path);
		return true;
	}
	
	/**
	 * 断开FTP连接
	 * @throws IOException
	 */
	public void logout() throws Exception {
		try {
			if (ftp != null) {
				ftp.logout();
				ftp.disconnect();
			}
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
		}
	}

	/**
	 * FTP上传
	 * @param file 上传的文件或文件夹
	 * 
	 * @throws Exception
	 */
	public void upload(File file) throws Exception {
		if (file.isDirectory()) {
			ftp.makeDirectory(file.getName());
			ftp.changeWorkingDirectory(file.getName());
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				File file1 = new File(file.getPath() + File.separator + files[i]);
				if (file1.isDirectory()) {
					upload(file1);
					ftp.changeToParentDirectory();
				} else {
					File file2 = new File(file.getPath() + File.separator + files[i]);
					FileInputStream input = new FileInputStream(file2);
					ftp.storeFile(file2.getName(), input);
					input.close();
				}
			}
		} else {
			File file2 = new File(file.getPath());
			FileInputStream input = new FileInputStream(file2);
			boolean isSuccess = ftp.storeFile(new String(file2.getName().getBytes("UTF-8"),"iso-8859-1"), input);
			LoggerUtil.info(this.getClass(), "上传文件至FTP服务器：" + isSuccess);
			input.close();
		}
	}

	/**
	 * FTP上传
	 * @param file   上传的文件
	 * @param path   远程路径
	 * @param pName  保存的文件名
	 * @throws Exception
	 */
	public void upload(MultipartFile file, String path, String pName) throws Exception {
		InputStream input = file.getInputStream();
		if (!ftp.changeWorkingDirectory(path)) {
			this.makeDirectory(path);
			ftp.changeWorkingDirectory(path);
		}
		final Long size = file.getSize();
		final String fileName = pName;
		CopyStreamAdapter streamListener = new CopyStreamAdapter() {
			@Override
			public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
				int percent = (int) (totalBytesTransferred * 100 / size);
				if (totalBytesTransferred%(1024*1024) == 0) {
					LoggerUtil.info(this.getClass(), "文件" + fileName + "已上传" + totalBytesTransferred + "k，上传百分比：" + percent + "%");
				}
			}
		};
		ftp.setCopyStreamListener(streamListener);
		boolean isSuccess = ftp.storeFile(new String(pName.getBytes("UTF-8"), "iso-8859-1"), input);
		LoggerUtil.info(this.getClass(), "上传文件至FTP服务器：" + isSuccess);
		input.close();
	}
	
	/**
	 * @param ftpFile ftp服务器上的文件
	 * @param dstFile 目标文件
	 * @throws Exception
	 */
	public void downLoad(String ftpFile, String dstFile) throws IOException {
		File file = new File(dstFile);
		FileOutputStream fos = new FileOutputStream(file);
		boolean isSuccess = ftp.retrieveFile(new String(ftpFile.getBytes("UTF-8"),"iso-8859-1"), fos);
		LoggerUtil.info(this.getClass(), "从FTP服务器下载文件：" + isSuccess);
		fos.close();
	}
	
	/**
	 * @param ftpFile ftp服务器上的文件
	 * @param dstFile 目标文件
	 * @throws Exception
	 */
	public InputStream downLoad(String ftpFile) throws IOException {
		InputStream in = ftp.retrieveFileStream(new String(ftpFile.getBytes("UTF-8"),"iso-8859-1"));
		return in;
	}
	
	/** 删除FTP文件 */
	public boolean remove(String filePath) throws IOException {
		return ftp.deleteFile(filePath);
	}
	
	/**
	 * 创建目录（可多级）
	 * @param path 路径
	 * @throws IOException 
	 * @throws Exception
	 */
	public void makeDirectory(String path) throws IOException {
		String[] paths = path.split(File.separator+File.separator);
		for (int i=0; i<paths.length; i++) {
			if(!ftp.changeWorkingDirectory(paths[i])) {
				ftp.makeDirectory(paths[i]);
			}
			ftp.changeWorkingDirectory(paths[i]);
		}
	}
	
	/** 关闭连接 */
	public void close() throws IOException {
		ftp.disconnect();
	}
	
	public static void main(String args[]) throws Exception {
		ApacheFtpUtil t = new ApacheFtpUtil();
		t.connect("121.42.12.142", 21, "xiaoyuan", "yuanqun5510", File.separator);
		//File file = new File("D:/ip.png");
		//t.upload(file);
		//t.downLoad("ip.png", "d:/ip.png");
		t.remove("/download/app/v1.88/12.txt");
		
	}
}
