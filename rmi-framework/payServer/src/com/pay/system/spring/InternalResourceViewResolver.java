package com.pay.system.spring;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class InternalResourceViewResolver extends org.springframework.web.servlet.view.InternalResourceViewResolver {
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		try {
			return super.buildView(viewName);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	@Override
	protected boolean canHandle(String viewName, Locale locale) {
		try {
			return super.canHandle(viewName, locale);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	protected View createView(String arg0, Locale arg1) throws Exception {
		try {
			return super.createView(arg0, arg1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	@Override
	public void setAttributes(Properties props) {
		try {
			super.setAttributes(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		try {
			return super.loadView(viewName, locale);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	@Override
	public void setAttributesMap(Map<String, ?> attributes) {
		try {
			super.setAttributesMap(attributes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
