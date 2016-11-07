package com.woyao.system.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import com.bean.service.FileRemoteBean;
import com.exception.Error;
import com.exception.SystemException;
import com.service.rmi.tools.FileService;
import com.utils.ConfigUtil;
import com.utils.FileUtil;
import com.utils.LoggerUtil;
import com.woyao.system.util.SpringBeanUtil;

public class FileServiceImpl extends UnicastRemoteObject implements FileService {
	private static final long serialVersionUID = 6551450083014387050L;

	protected FileServiceImpl() throws RemoteException {
		super();
	}
	
	protected FileServiceImpl(int port) throws RemoteException {
		super(port);
	}
	@Override
	public String upload(String fileName,byte[] bts) throws RemoteException {
		return this.upload(fileName, null,null, bts);
	}
	@Override
	public String upload(String fileName,Integer w,Integer h, byte[] bts) throws RemoteException {
		FileServerRuntime fsr = SpringBeanUtil.getBean(FileServerRuntime.class);
		String savePath = "source" + File.separator + getTimeUrl() + File.separator;
//		String filePath = fsr.getFileRemoteBean().getSavePath();
		String rootPath = ConfigUtil.getInstance().getWebRootUrl();
		File saveFile = new File(rootPath + savePath);
		if(!saveFile.exists()) {
			saveFile.mkdirs();
		}
		savePath += FileUtil.getOnlyOneFileName(fileName);
		saveFile = new File(rootPath + savePath);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(saveFile);
			out.write(bts);
			out.flush();
			if(null!=w||null!=h){
				LoggerUtil.info(this.getClass(), "开始生成缩略图");
				//类型不为空 则生成缩略图
				String p=saveFile.getPath();
				if(zoomImage(p, p.replace(saveFile.getName(),"small_"+saveFile.getName()), w, h)){
					LoggerUtil.info(this.getClass(), "生成缩略图成功");
					//缩略图生成成功更换返回路径为缩略图路径
					savePath = savePath.replace(saveFile.getName(), "small_"+saveFile.getName());
				}else{
					LoggerUtil.info(this.getClass(), "生成缩略图失败");
				}
			}
			
		} catch (Exception e) {
			LoggerUtil.error(this.getClass(), e);
			throw new SystemException(Error.system_error, e);
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					LoggerUtil.error(this.getClass(), e);
				}
			}
		}
		FileRemoteBean frb = fsr.getFileRemoteBean();
//		return "http://"+frb.getWebIp()+":"+frb.getWebPort()+"/"+frb.getWebName()+"/file/download.do?url="+StringUtil.toBase64(saveFile.getAbsolutePath(), "UTF-8");
		savePath = savePath.replace("\\", "/");
		return "http://"+frb.getWebIp()+":"+frb.getWebPort()+"/"+frb.getWebName()+"/" + savePath;
	}

	/** 获取当前的时间文件夹 */
	@SuppressWarnings("deprecation")
	private String getTimeUrl() {
		String timeStr = new Date().toLocaleString();
		timeStr = timeStr.substring(0, timeStr.indexOf(":")) + "h";
		timeStr = timeStr.replace(" ", "_");
		return timeStr;
	}

	/**
	 * 根据尺寸缩放图片[等比例缩放:参数height为null,按宽度缩放比例缩放;参数width为null,按高度缩放比例缩放]
	 * @param imagePath 源图片路径
	 * @param newPath 处理后图片路径
	 * @param width 缩放后的图片宽度
	 * @param height 缩放后的图片高度
	 * @return 返回true说明缩放成功,否则失败
	 */
	public  boolean zoomImage(String imagePath, String newPath, Integer width, Integer height) {
		boolean flag = false;
		try {
			IMOperation op = new IMOperation();
			op.addImage(imagePath);
			if (width == null) {// 根据高度缩放图片
				op.resize(null, height);
			} else if (height == null) {// 根据宽度缩放图片
				op.resize(width);
			} else {
				op.resize(width, height);
			}
			op.addImage(newPath);
			ConvertCmd convert = new ConvertCmd(true);
			convert.run(op);
			flag = true;
		} catch (Exception e) {
			System.out.println("缩略图处理失败:"+e.getMessage());
		} finally {
		}
		return flag;
	}
	@Override
	public void heartbeat() throws RemoteException {}

	@Override
	public byte[] download(String url) throws RemoteException {
		ByteArrayOutputStream byteOut = null;
		FileInputStream input = null;
		try {
			byteOut = new ByteArrayOutputStream();
			input = new FileInputStream(urlToFile(url));
			int length = 0;
			byte[] bts = new byte[1024];
			while((length=input.read(bts)) != -1) {
				byteOut.write(bts, 0, length);
			}
			byteOut.flush();
			return byteOut.toByteArray();
		} catch (Exception e) {
			throw new SystemException(Error.system_error, e);
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(byteOut != null) {
				try {
					byteOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/** 将url转换为本地文件对象 */
	private File urlToFile(String url) {
		String rootPath = ConfigUtil.getInstance().getWebRootUrl();
		FileRemoteBean frb = new FileRemoteBean(url);
		return new File(rootPath + frb.getSavePath().replaceAll("/", File.separator) + frb.getWebFileName());
	}

	@Override
	public boolean hasFile(String url) throws RemoteException {
		File file = urlToFile(url);
		if(file.exists()) {
			return true;
		}
		return false;
	}
}
