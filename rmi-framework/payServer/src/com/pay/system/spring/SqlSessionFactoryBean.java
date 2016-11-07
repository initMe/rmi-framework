package com.pay.system.spring;

import java.io.IOException;
import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.Resource;

public class SqlSessionFactoryBean extends org.mybatis.spring.SqlSessionFactoryBean {
	
	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		try {
			return super.buildSqlSessionFactory();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void setConfigurationProperties(
			Properties sqlSessionFactoryProperties) {
		try {
			super.setConfigurationProperties(sqlSessionFactoryProperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setMapperLocations(Resource[] mapperLocations) {
		try {
			super.setMapperLocations(mapperLocations);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
