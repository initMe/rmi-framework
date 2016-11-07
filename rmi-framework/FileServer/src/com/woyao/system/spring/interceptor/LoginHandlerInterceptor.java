package com.woyao.system.spring.interceptor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.utils.LoggerUtil;

public class LoginHandlerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		String path = request.getServletPath();
		// 做恶意请求拦截
		List<Long> lastTimeList = (List<Long>) request.getSession().getAttribute("lastTimeout");
		Long nowTime = System.currentTimeMillis();
		if(lastTimeList == null) {
			lastTimeList = new ArrayList<Long>();
		}
		lastTimeList.add(nowTime);
		if(lastTimeList.size() > 100) {
			lastTimeList.remove(0);
			// 如果连续100次请求小于100秒(平均没秒1次)
			Long tempTime = lastTimeList.get(0)-lastTimeList.get(lastTimeList.size()-1);
			if(tempTime >= 1000*100) {
				LoggerUtil.error(this.getClass(), "发现恶意请求，请求者 [ "+request.getRemoteAddr()+" ] ，平均每次请求耗时 ["+(tempTime/100)+"毫秒] ，请求路径：[ "+path+" ] ");
			}
			return false;
		}
		request.getSession().setAttribute("lastTimeout", lastTimeList);
		// 做权限拦截
		if (path.indexOf("file") >= 0) {
			return true;
		} 
//		else if (path.matches(Const.NO_INTERCEPTOR_PATH)) {
//			return true;
//		}else {
//			HttpSession session = request.getSession();
//			User user = (User) session.getAttribute(Const.SESSION_USER);
//			if (user != null) {
//				return true;
//			} else {
//				response.sendRedirect(request.getContextPath()+ "/admin/login.do");
//				return false;
//			}
//		}
		return false;
	}

}