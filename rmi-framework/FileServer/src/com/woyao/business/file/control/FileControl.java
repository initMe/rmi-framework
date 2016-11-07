package com.woyao.business.file.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.exception.BaseException;
import com.exception.BusinessException;
import com.exception.Error;
import com.exception.SystemException;
import com.utils.LoggerUtil;
import com.woyao.system.manager.FileServerRuntime;
import com.woyao.system.util.ResponseUtil;
import com.woyao.system.util.SpringBeanUtil;

@Controller
@RequestMapping("/file")
public class FileControl {
	
	@ResponseBody
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public void upload(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="file") MultipartFile[] files) {
		String requestText = "无参数";
		try {
//			Integer call = 0;
//			try {
//				call = Integer.parseInt(request.getParameter("call"));
//			} catch (Exception e) {}
//			String json = request.getParameter("json");
//			requestText = "call="+call+", json="+json;
			final List<String> webUrlList = new ArrayList<String>();
			String headImgs = "";
			if(files==null || files.length==0) {
				throw new BusinessException(Error.system_file_null_error, new NullPointerException("上传的文件出错"));
			}
			FileServerRuntime fsr = SpringBeanUtil.getBean(FileServerRuntime.class);
			for(MultipartFile file : files) {
				if(file==null || file.isEmpty() || file.getBytes().length==0) {
					throw new BusinessException(Error.system_file_null_error, new NullPointerException("上传的文件出错"));
				}
				Integer w=null==request.getParameter("w")?null:Integer.parseInt(request.getParameter("w"));
				Integer h=null==request.getParameter("h")?null:Integer.parseInt(request.getParameter("h"));
				String webUrl = fsr.uploadFile(file,w,h);
				LoggerUtil.info(this.getClass(), "存储的文件请求链接:"+webUrl);
				webUrlList.add(webUrl);
				headImgs += webUrl+",";
			}
//			final String newWebUrl = (getServerHead(request)+"app/file.do?imgUrl="+StringUtil.toBase64(webUrl)).replace("\n", "");
			// 返回数据
			Object returnObj = new Object(){
				private List<String> url = webUrlList;

				@SuppressWarnings("unused")
				public List<String> getUrl() {
					return url;
				}
				@SuppressWarnings("unused")
				public void setUrl(List<String> url) {
					this.url = url;
				}
			};
			ResponseUtil.outObject(response, returnObj);
		} catch (Exception e) {
			if(e instanceof BaseException) {
				BaseException woyaoExcetpion = (BaseException)e;
				woyaoExcetpion.setRequestText(requestText);
				ResponseUtil.outError(response, 
						String.valueOf((woyaoExcetpion).getErrorCode().getCode()));
				woyaoExcetpion.printStackTrace();
			} else {
				SystemException se = new SystemException(Error.notKnow_error, e);
				se.setRequestText(requestText);
				ResponseUtil.outError(response, se);
				se.printStackTrace();
			}
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/download")
	public void download(HttpServletRequest request, HttpServletResponse response, String url) {
		if(url != null) {
			FileServerRuntime fsr = SpringBeanUtil.getBean(FileServerRuntime.class);
			byte[] bts = fsr.downloadFile(url);
			try {
				OutputStream out = response.getOutputStream();
				out.write(bts);
				out.flush();
			} catch (IOException e) {
				LoggerUtil.error(this.getClass(), e);
			}
		}
	}
}
