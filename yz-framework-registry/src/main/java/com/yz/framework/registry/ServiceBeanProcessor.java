package com.yz.framework.registry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yz.framework.logging.Logger;

public class ServiceBeanProcessor implements BeanPostProcessor {

	private final static Logger logger = Logger.getLogger(ServiceBeanProcessor.class);
	private ServiceRegistry serviceRegistry;

	public ServiceBeanProcessor(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean != null) {
			Class<?> beanClass = bean.getClass();
			if (beanClass.isAnnotationPresent(RequestMapping.class)) {
				serviceRegistry.addService(bean, beanName, beanClass);
				logger.info("postProcessAfterInitialization", "find service resource " + beanName);
			}
		}
		return bean;
	}
}
