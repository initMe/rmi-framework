package com.pay.system.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			super.contextInitialized(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext arg0) {
		try {
			return super.initWebApplicationContext(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	protected ApplicationContext loadParentContext(ServletContext arg0) {
		try {
			return super.loadParentContext(arg0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
