package com.yz.framework.registry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

public class ServiceObject {

    private String api;
    private Method method;
    private Object serviceObject;
    private List<ServiceMethodParameter> parameters;

    public ServiceObject(String api, Object serviceObject, Method method, List<ServiceMethodParameter> parameters) {

        this.api = api;
        this.serviceObject = serviceObject;
        this.method = method;
        this.parameters = parameters;

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

    public Object invoke(String jsonMessage) throws Exception {

        if (parameters.isEmpty()) {
            return this.method.invoke(serviceObject);
        }
        Object[] args = new Object[parameters.size()];
        if (parameters.size() == 1) {
            ServiceMethodParameter parameter = parameters.get(0);
            if (parameter.getIsComplex()) {
                args[0] = JSON.parseObject(jsonMessage, parameter.getParameterType());
            } else {
                JSONObject jsonObject = JSON.parseObject(jsonMessage);
                args[0] = parameter.getVal(jsonObject);
            }
        } else {
            JSONObject jsonObject = JSON.parseObject(jsonMessage);
            for (int i = 0; i < parameters.size(); i++) {

                Object obj = null;
                ServiceMethodParameter parameter = this.parameters.get(i);
                if (parameter.getIsComplex()) {
                    obj = JSON.parseObject(jsonObject.getString(parameter.getParameterName()), parameter.getParameterType());
                } else {
                    obj = parameter.getVal(jsonObject);
                }
                args[i] = obj;
            }
        }
        return this.method.invoke(serviceObject, args);
    }
}
