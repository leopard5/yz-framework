package com.yz.framework.reflect;

import java.awt.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.yz.framework.logging.Logger;

public class ReflectUtil {

	private static final Logger LOGGER = Logger.getLogger(ReflectUtil.class);

	public static boolean isComplexClass(Class<?> dataTypeClass) {
		if (dataTypeClass.isPrimitive()) {
			return false;
		}
		return getPrimitive(dataTypeClass) == null;
	}

	private static String[] DEFAULT_METHOD = new String[] {
			"wait", "equals", "notify", "notifyAll", "toString", "hashCode", "getClass" };

	/**
	 * 是不是默认方法： wait, equals, notify, notifyAll, toString, hashCode, getClass
	 * 
	 * @param method2
	 * @return boolean
	 * @throws null
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年11月16日 下午4:22:20
	 */
	public static boolean isDefaultMethod(Method method2) {
		for (String methodName : DEFAULT_METHOD) {
			if (method2.getName().equalsIgnoreCase(methodName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取类型的原型类型
	 * 
	 * @param dataTypeClass
	 * @return String
	 * @throws null
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年11月16日 下午4:22:05
	 */
	public static String getPrimitive(Class<?> dataTypeClass) {
		if (dataTypeClass.isPrimitive()) {
			return dataTypeClass.getName();
		}
		String name = dataTypeClass.getName();
		if (name.endsWith(".String")) {
			return "string";
		}
		try {
			Field[] fiels = dataTypeClass.getFields();
			for (Field field : fiels) {
				if (field.getName().equalsIgnoreCase("TYPE")) {
					Field filed = dataTypeClass.getField("TYPE");
					Class<?> obj = (Class<?>) filed.get(null);
					if (obj.isPrimitive()) {
						return obj.getName();
					}
				}
			}
			return null;
		} catch (Exception e) {
			//
			LOGGER.error("getPrimitive", "不应该发生的异常", e);
		}
		return null;

	}

	public static boolean isList(Class<?> parameterType) {
		return List.class.isAssignableFrom(parameterType);
	}
}
