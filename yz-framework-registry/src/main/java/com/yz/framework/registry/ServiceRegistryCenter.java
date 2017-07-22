package com.yz.framework.registry;

import com.yz.framework.logging.Logger;
import com.yz.framework.reflect.ReflectUtil;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRegistryCenter implements BeanPostProcessor {
    private static final Logger LOGGER = Logger.getLogger(ServiceRegistryCenter.class);
    public static final String API_DELIMITER = ".";
    private final Map<String, Class<?>> serviceClassMap = new HashMap<String, Class<?>>();
    private final Map<String, Object> serviceObjectMap = new HashMap<String, Object>();
    private final Map<String, ServiceObject> serviceObjectCache = new HashMap<String, ServiceObject>();

    private volatile boolean isClassClassPathLoad;

    public void addService(Object bean, String beanName, Class<?> beanClass) throws NotFoundException {
        serviceClassMap.put(beanName, beanClass);
        serviceObjectMap.put(beanName, bean);

        ClassPool classPool = ClassPool.getDefault();
        if (!isClassClassPathLoad) {
            synchronized (this) {
                if (!isClassClassPathLoad) {
                    ClassClassPath classClassPath = new ClassClassPath(beanClass);
                    classPool.appendClassPath(classClassPath);
                    isClassClassPathLoad = true;
                }
            }
        }
        generateServiceObject(classPool, beanName);
    }

    public Map<String, Class<?>> getServiceClassMap() {
        return serviceClassMap;
    }

    public void generateServiceObject(ClassPool classPool, String beanName) throws NotFoundException {

        Class<?> beanClass = serviceClassMap.get(beanName);
        CtClass ctClass = classPool.get(beanClass.getName());
        if (!beanClass.isAnnotationPresent(ServiceName.class)) {
            return;
        }
        Class<?>[] interfaces = beanClass.getInterfaces();
        Map<String, Class<?>> allMethods = new HashMap<String, Class<?>>();
        for (Class<?> interfaceClass : interfaces) {
            Method[] methods = interfaceClass.getMethods();
            for (Method method : methods) {
                allMethods.put(method.getName(), interfaceClass);
            }
        }

        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            if (ReflectUtil.isDefaultMethod(method)) {
                continue;
            }
            Class<?> interfaceClass = allMethods.get(method.getName());
            if (interfaceClass == null) {
                continue;
            }

            String methodName = method.getName();
            String api = interfaceClass.getName() + API_DELIMITER + methodName;
            List<ServiceMethodParameter> parameters = getServiceMethodParameters(ctClass, method);
            ServiceObject serviceObject = new ServiceObject(api, serviceObjectMap.get(beanName), method, parameters);
            serviceObjectCache.put(api, serviceObject);
        }
    }

    public ServiceObject getServiceObject(String api) throws Exception {
        return serviceObjectCache.get(api);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // TODO 自动生成的方法存根
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean != null) {
            Class<?> beanClass = bean.getClass();

            if (beanClass.isAnnotationPresent(ServiceName.class)) {
                try {
                    this.addService(bean, beanName, beanClass);
                    LOGGER.info("postProcessAfterInitialization", "find service resource " + beanName);
                } catch (NotFoundException e) {
                    LOGGER.error("postProcessAfterInitialization", "生成serviceObject失败： " + beanName, e);
                }
            }
        }
        return bean;
    }

    private static List<ServiceMethodParameter> getServiceMethodParameters(
            CtClass ctClass,
            Method method) {

        List<ServiceMethodParameter> parameters = new ArrayList<ServiceMethodParameter>();
        try {
            CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
            int pos = Modifier.isStatic(ctMethod.getModifiers()) ? 0 : 1;
            MethodInfo methodInfo = ctMethod.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            Class<?>[] parameterClasses = method.getParameterTypes();
            Type[] parameterTypes = method.getGenericParameterTypes();
            for (int i = 0; i < parameterClasses.length; i++) {
                String paramName = attr.variableName(i + pos);
                if (paramName.equalsIgnoreCase("i$")) {
                    LOGGER.error("getServiceMethodParameters", "获取参数名称失败", ctClass.getName() + API_DELIMITER + method.getName());
                }
                Class<?> parameterClass = parameterClasses[i];
                ServiceMethodParameter serviceMethodParameter = new ServiceMethodParameter();
                serviceMethodParameter.setParameterName(paramName);
                serviceMethodParameter.setParameterClass(parameterClass);
                serviceMethodParameter.setParameterType(parameterTypes[i]);
                parameters.add(serviceMethodParameter);
            }

        } catch (Exception e) {
            LOGGER.error("getServiceMethodParameters", "不应该发生的错误", e);
        }
        return parameters;

    }
    /*
	 * public static void main(String[] args) throws NotFoundException {
	 * ServiceRegistryCenter serviceRegistryCenter = new
	 * ServiceRegistryCenter(); UserServiceImpl bean = serviceRegistryCenter.new
	 * UserServiceImpl(); serviceRegistryCenter.addService(bean, "userService",
	 * bean.getClass()); }
	 * 
	 * public interface UserService { public UserInfo getData(List<UserInfo>
	 * data); }
	 * 
	 * @ServiceName public class UserServiceImpl implements UserService { public
	 * UserInfo getData(List<UserInfo> data) { for (UserInfo userInfo : data) {
	 * System.out.println(userInfo.getUserName()); } return data.get(0); } }
	 * 
	 * public class UserInfo {
	 * 
	 * private String userName;
	 * 
	 * public String getUserName() { return userName; }
	 * 
	 * public void setUserName(String userName) { this.userName = userName; }
	 * 
	 * }
	 */
}
