package com.yz.framework.registry;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

public class ServiceRegistry {

	public static final String EMPTY_STRING = "";
	public static final String PATH_DELIMITER = "/";
	public static final String API_DELIMITER = ".";
	private final Map<String, Class<?>> serviceClassMap = new HashMap<String, Class<?>>();
	private final Map<String, Object> serviceObjectMap = new HashMap<String, Object>();
	private final Map<String, ApiServiceObject> apiServiceObjectCache = new HashMap<String, ApiServiceObject>();
	private final List<String> services = new ArrayList<String>();

	private volatile boolean isServiceGenerated = false;
	private String appName;

	public ServiceRegistry(String appNameString) {
		this.appName = appNameString;
	}

	public void addService(Object bean, String beanName, Class<?> beanClass) {
		serviceClassMap.put(beanName, beanClass);
		serviceObjectMap.put(beanName, bean);
	}

	public Map<String, Class<?>> getServiceClassMap() {
		return serviceClassMap;
	}

	public List<String> getServices() {
		if (!isServiceGenerated) {
			generateServicePath();
		}
		return services;
	}

	private void generateServicePath() {
		synchronized (services) {
			if (isServiceGenerated) {
				return;
			}
			services.clear();
			for (String beanName : serviceClassMap.keySet()) {
				Class<?> beanClass = serviceClassMap.get(beanName);
				Object bean = serviceObjectMap.get(beanName);
				String pachageName = beanClass.getPackage().getName();
				RequestMapping serviceClass = beanClass.getAnnotation(RequestMapping.class);
				String serviceMappingName = getMappingName(serviceClass.value());
				if (serviceMappingName == null) {
					continue;
				}
				String servicePath = PATH_DELIMITER + appName + PATH_DELIMITER + serviceMappingName;
				String apiRootName = pachageName + API_DELIMITER + serviceMappingName;
				Method[] methods = beanClass.getMethods();
				for (Method method : methods) {
					if (!method.isAnnotationPresent(RequestMapping.class)) {
						continue;
					}
					RequestMapping methodPath = method.getAnnotation(RequestMapping.class);
					String methodMappingName = getMappingName(methodPath.value());
					String serviceFullPath = servicePath + PATH_DELIMITER + methodMappingName;
					String api = apiRootName + API_DELIMITER + methodMappingName;
					ApiServiceObject apiServiceObject = new ApiServiceObject(api, bean, method);
					apiServiceObjectCache.put(api, apiServiceObject);
					services.add(serviceFullPath);
				}
			}
			isServiceGenerated = true;
		}
	}

	private String getMappingName(String[] values) {
		if (values != null && values.length > 0) {
			String name = values[0];
			// eg. /user/index.html

			int lastDotIndex = name.lastIndexOf(API_DELIMITER);
			if (lastDotIndex > 0) {
				name = name.substring(0, name.lastIndexOf(API_DELIMITER));
			}

			char pathDelimiter = PATH_DELIMITER.charAt(0);

			while (name.startsWith(PATH_DELIMITER)) {
				name = StringUtils.trimLeadingCharacter(name, pathDelimiter);
			}
			while (name.endsWith(PATH_DELIMITER)) {
				name = StringUtils.trimTrailingCharacter(name, pathDelimiter);
			}
			return name;
		}
		return null;
	}

	public ApiServiceObject getApiServiceObject(String api) {
		if (!isServiceGenerated) {
			generateServicePath();
		}
		return apiServiceObjectCache.get(api);
	}

}
