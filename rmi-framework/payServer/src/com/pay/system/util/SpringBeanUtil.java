package com.pay.system.util;

import org.springframework.context.ApplicationContext;

/** spring工具类 */
public class SpringBeanUtil {
	/** spring上下文 */
	public static ApplicationContext WEB_APP_CONTEXT = null;
	
	/**
	 * 依据配置文件中的id名称获取bean对象
	 * @param <T>	任意在spring配置文件中配置过的bean
	 * @param beanName	bean的id
	 * @return	容器中的spring对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanId) {
		return (T) WEB_APP_CONTEXT.getBean(beanId);
	}
	
	/**
	 * 依据配置文件中的id名称获取bean对象
	 * @param <T>	任意在spring配置文件中配置过的bean
	 * @param beanName	bean的id
	 * @return	容器中的spring对象
	 */
	public static <T> T getBean(Class<T> clazz) {
		String clazzName = clazz.getSimpleName();
		String beanName = (clazzName.charAt(0)+"").toLowerCase() + clazzName.substring(1, clazzName.length());
		return getBean(beanName);
	}
}
