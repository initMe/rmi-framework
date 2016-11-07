
package com.woyao.system.spring;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.woyao.system.util.SpringBeanUtil;

public class WebAppContextListener implements ServletContextListener {
	@SuppressWarnings("unused")
	private ServletContextEvent event;

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		this.event = event;
		SpringBeanUtil.WEB_APP_CONTEXT =  WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
	}
}
