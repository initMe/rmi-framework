package com.pay.system.spring;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;

public class MapperScannerConfigurer extends org.mybatis.spring.mapper.MapperScannerConfigurer {
	
	@Override
	public BeanNameGenerator getNameGenerator() {
		try {
			return super.getNameGenerator();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		try {
			super.setApplicationContext(applicationContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setBeanName(String name) {
		try {
			super.setBeanName(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setSqlSessionFactoryBeanName(String sqlSessionFactoryName) {
		try {
			super.setSqlSessionFactoryBeanName(sqlSessionFactoryName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
