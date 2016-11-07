package com.woyao.system.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.bean.BaseBean;
import com.context.Context;
import com.exception.BusinessException;
import com.exception.Error;
import com.manager.Tasker;
import com.utils.CertCoder;
import com.utils.JsonUtil;
import com.utils.LoggerUtil;
import com.utils.ObjectUtil;

public class ResponseUtil {

	/***
	 * 写出简单消息
	 * @param response	写出流对象
	 * @param text	要写的文本
	 */
	public static void out(HttpServletResponse response, String text) {
		try {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.println(text);
            out.flush();
            out.close();
            LoggerUtil.info(ResponseUtil.class, "\n返回消息：\n" + text + "\n----------------------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 向客户端返回消息
	 * @param response	写出流对象
	 * @param json	要写的信息
	 * @param outObjTask	扩展处理
	 */
	public static void out(HttpServletResponse response, Object textMap, Map<String, Object> exMap) {
		out(response, textMap, exMap, true);
	}
	
	/**
	 * 向客户端返回消息
	 * @param response	写出流对象
	 * @param json	要写的信息
	 * @param outObjTask	扩展处理
	 * @param needBase64	是否需要base64操作
	 */
	public static void out(HttpServletResponse response, Object textObj, Map<String, Object> exMap, boolean needBase64) {
		String text = "";
		if(exMap!=null && !exMap.isEmpty()) {
			Map<String, Object> textMap = JsonUtil.objToMap(textObj);
			textMap.putAll(exMap);
			text = JsonUtil.objToJson(textMap);
		} else {
			text = JsonUtil.objToJson(textObj);
		}
		try {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            String returnMsg = text;
            if(needBase64) {
            	returnMsg = CertCoder.doCoderByPrivateKey(text, Context.privateKey, true);
            }
            out.println(returnMsg);
            out.flush();
            out.close();
            LoggerUtil.info(ResponseUtil.class, "\n返回消息：\n" + text + "\n----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(ResponseUtil.class, "\n返回消息出错\n", e);
        }
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param json	要写的信息
	 * @param outObjTask	扩展处理
	 * @
	 */
	public static void outSuccess(HttpServletResponse response, Tasker... outObjTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", 1);
        out(response, map, doTask(null, outObjTask));
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param json	要写的信息
	 * @param outObjTask	扩展处理
	 * @
	 */
	public static void outSuccess(HttpServletResponse response, Object obj, Tasker... outObjTask) {
		outSuccess(response, obj, true, outObjTask);
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param json	要写的信息
	 * @param outObjTask	扩展处理
	 * @param needBase64	是否需要做base64处理
	 * @
	 */
	public static void outSuccess(HttpServletResponse response, Object obj, boolean needBase64, Tasker... outObjTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", 1);
		if (obj == null) {
			map.put("info", new Object());
		} else {
			if(obj instanceof Boolean) {
				map.put("result", ((Boolean)obj)?1:0);
			} else {
				map.put("info", obj);
			}
		}
        out(response, map, doTask(obj, outObjTask), needBase64);
	}
	
	/***
	 * 向客户端返回消息对象(最终转换成json返回)
	 * (默认返回数据，做base64处理)(同outSuccess(...))
	 * @param response	写出流对象
	 * @param outObjTask	扩展处理
	 * @
	 */
	public static void outObject(HttpServletResponse response, Tasker... outObjTask) {
		outSuccess(response, outObjTask);
	}
	
	/***
	 * 向客户端返回成功消息(最终转换成json返回)
	 * (默认返回数据，做base64处理)
	 * @param response	写出流对象
	 * @param obj	要返回的对象(默认会附加成功标记)
	 * @param outObjTask	扩展处理
	 * @
	 */
	public static void outObject(HttpServletResponse response, Object obj, Tasker... outObjTask) {
		outSuccess(response, obj, outObjTask);
	}
	
	/***
	 * 向客户端返回消息对象(最终转换成json返回)
	 * @param response	写出流对象
	 * @param obj	要返回的对象(默认会附加成功标记)
	 * @param outObjTask	扩展处理
	 * @param needBase64	是否需要做base64处理
	 * @
	 */
	public static void outObject(HttpServletResponse response, Object obj, boolean needBase64, Tasker... outObjTask) {
		outSuccess(response, obj, needBase64, outObjTask);
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param e	出错的异常
	 * @param outObjTask	扩展处理
	 * @
	 */
	public static void outError(HttpServletResponse response, Exception e, Tasker... outObjTask) {
		outError(response, e, true, outObjTask);
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param e	出错的异常
	 * @param outObjTask	扩展处理
	 * @param needBase64	是否需要做base64处理
	 * @
	 */
	public static void outError(HttpServletResponse response, Exception e, boolean needBase64, Tasker... outObjTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", 0);
		map.put("msg", e.getMessage());
        out(response, map, doTask(null, outObjTask), needBase64);
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param errorMsg	提示错误
	 * @
	 */
	public static void outError(HttpServletResponse response, String errorMsg, Tasker... outObjTask) {
		outError(response, errorMsg, true, outObjTask);
	}
	
	/**
	 * 向客户端返回成功消息
	 * @param response	写出流对象
	 * @param errorMsg	提示错误
	 * @param outObjTask	扩展处理
	 * @param needBase64	是否需要做base64处理
	 * @
	 */
	public static void outError(HttpServletResponse response, String errorMsg, boolean needBase64, Tasker... outObjTask) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", 0);
		map.put("msg", errorMsg);
        out(response, map, doTask(null, outObjTask), needBase64);
	}
	
	/**
	 * 执行扩展处理，返回附加数据
	 * @param outObjTask	处理类
	 * @return	附加数据
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> doTask(Object outObj, Tasker... outObjTask) {
		if(outObjTask == null) {
			return null;
		}
		Map<String, Object> exMsg = new HashMap<String, Object>();
		for(Tasker task : outObjTask) {
			if(task == null) {
				continue;
			}
			try {
				exMsg.putAll((Map<String, Object>)task.doTask(outObj));
			} catch (Exception e) {
				e.printStackTrace();
				throw new BusinessException(Error.system_utils_tasker_error, e);
			}
		}
		return exMsg;
	}
	

	/**
	 * 构造分页返回的执行器
	 * @param paramBean	参数对象
	 * @return	如果有分页数据，则构造分页执行器
	 */
	public static Tasker pageTask(BaseBean paramBean) throws Exception {
		final Object page = ObjectUtil.getAttributeValue(paramBean, "page");
		if(page != null) {
			return new Tasker() {
				private static final long serialVersionUID = 1L;
				@Override
				public Object doTask(Object... objs) throws Exception {
					Map<String, Object> exMap = new HashMap<String, Object>();
					Map<String, Object> pageMap = JsonUtil.objToMap(page);
					System.out.println(pageMap);
					pageMap.remove("pageStr");
					pageMap.remove("mobileStr");
					System.out.println(pageMap);
					exMap.put("page", pageMap);
					if(objs!=null && objs.length>0 && objs[0]!=null && objs[0] instanceof BaseBean) {
						ObjectUtil.setAttribute(objs[0], "page", null);
					}
					return exMap;
				}
			};
		}
		return null;
	}
}
