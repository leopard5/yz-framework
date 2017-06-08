package com.yz.framework.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (ApplicationContextUtil.applicationContext == null) {
			ApplicationContextUtil.applicationContext = applicationContext;
		}
		else if (ApplicationContextUtil.applicationContext == applicationContext) {
			throw new BeanDefinitionValidationException("重复定义了bean ApplicationContextUtil");
		}
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}

	public static <T> T getBean(String beanName, Class<T> clazz) {
		return applicationContext.getBean(beanName, clazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String beanName) {
		Object bean = applicationContext.getBean(beanName);
		return bean == null ? null : (T) bean;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
