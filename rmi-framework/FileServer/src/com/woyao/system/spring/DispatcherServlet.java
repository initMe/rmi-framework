package com.woyao.system.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.context.WebApplicationContext;

public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {
	private static final long serialVersionUID = -3018364114939051129L;

	@Override
	protected void applyInitializers(ConfigurableApplicationContext arg0) {
		try {
			super.applyInitializers(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected LocaleContext buildLocaleContext(HttpServletRequest request) {
		try {
			return super.buildLocaleContext(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doService(HttpServletRequest arg0, HttpServletResponse arg1)
			throws Exception {
		try {
			super.doService(arg0, arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected WebApplicationContext findWebApplicationContext() {
		try {
			return super.findWebApplicationContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected WebApplicationContext initWebApplicationContext() {
		try {
			return super.initWebApplicationContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setContextConfigLocation(String contextConfigLocation) {
		try {
			super.setContextConfigLocation(contextConfigLocation);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
