package com.yz.framework.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApiServiceObject {
    private String api;
    private Method method;
    private Object serviceObject;

    public ApiServiceObject(String api, Object serviceObject, Method method) {
        this.api = api;
        this.serviceObject = serviceObject;
        this.method = method;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public Object getServiceObject() {
        return serviceObject;

    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public Object invoke(Object... args) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        return method.invoke(serviceObject, args);
    }
}
