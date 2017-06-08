package com.yz.framework.data;

import java.util.HashMap;
import java.util.Map;

public class MapperThreadLocal extends ThreadLocal<Map<Class<?>, Object>> {

	@Override
	protected Map<Class<?>, Object> initialValue() {
		return new HashMap<Class<?>, Object>();
	}
}
