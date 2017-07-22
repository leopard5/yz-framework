package com.yz.framework.registry;

import com.alibaba.fastjson.JSONObject;
import com.yz.framework.reflect.ReflectUtil;

import java.lang.reflect.Type;

public class ServiceMethodParameter {

    private String parameterName;
    private Type parameterType;
    private Class<?> parameterClass;
    private Boolean isComplexType;
    private Boolean isList;
    private Boolean isArray;
    private Boolean isComplex;
    private String primitiveType;
    private boolean isByte;
    private boolean isBoolean;
    private boolean isPrimitive;
    private boolean isString;
    private boolean isInt;
    private boolean isLong;
    private boolean isChar;
    private boolean isFloat;
    private boolean isDouble;
    private boolean isShort;

    public ServiceMethodParameter() {
    }

    public Object getVal(JSONObject jsonObject) {
        if (jsonObject.containsKey(this.parameterName)) {
            if (isString) {
                return jsonObject.get(this.parameterName);
            }
            if (isInt) {
                return jsonObject.getInteger(this.parameterName);
            }
            if (isLong) {
                return jsonObject.getLong(this.parameterName);
            }
            if (isByte) {
                return jsonObject.getByte(this.parameterName);
            }
            if (isBoolean) {
                return jsonObject.getBoolean(this.parameterName);
            }
            if (isChar) {
                return jsonObject.getByte(this.parameterName);
            }
            if (isFloat) {
                return jsonObject.getFloat(this.parameterName);
            }
            if (isDouble) {
                return jsonObject.getDouble(this.parameterName);
            }
            if (isShort) {
                return jsonObject.getShort(this.parameterName);
            }
            return jsonObject.get(this.parameterName);
        }
        if (isPrimitive) {
            return getDefaultValue();
        }
        return null;
    }

    private Object getDefaultValue() {
        if (isInt) {
            return 0;
        }
        if (isLong) {
            return 0L;
        }
        if (isByte) {
            return (byte) 0;
        }
        if (isBoolean) {
            return false;
        }
        if (isChar) {
            char c = 0;
            return c;
        }
        if (isFloat) {
            return 0f;
        }
        if (isDouble) {
            return 0d;
        }
        if (isShort) {
            return (short) 0;
        }
        return null;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Type getParameterType() {
        return parameterType;
    }

    public void setParameterType(Type parameterType) {
        this.parameterType = parameterType;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(Class<?> parameterClass) {
        this.parameterClass = parameterClass;
        this.isList = ReflectUtil.isList(parameterClass);
        this.isArray = parameterClass.isArray();
        this.isComplexType = ReflectUtil.isComplexClass(parameterClass);
        this.isComplex = this.isList || this.isArray || this.isComplexType;

        if (!this.isComplex) {
            primitiveType = ReflectUtil.getPrimitive(parameterClass);
            this.isPrimitive = parameterClass.isPrimitive();
            this.isString = primitiveType.equalsIgnoreCase("string");
            this.isInt = primitiveType.equalsIgnoreCase("int");
            this.isLong = primitiveType.equalsIgnoreCase("long");
            this.isByte = primitiveType.equalsIgnoreCase("byte");
            this.isBoolean = primitiveType.equalsIgnoreCase("boolean");
            this.isFloat = primitiveType.equalsIgnoreCase("float");
            this.isDouble = primitiveType.equalsIgnoreCase("double");
            this.isChar = primitiveType.equalsIgnoreCase("char");
            this.isShort = primitiveType.equalsIgnoreCase("short");
        }
    }

    public Boolean isComplexType() {
        return isComplexType;
    }

    public void setIsComplexType(Boolean isComplexType) {
        this.isComplexType = isComplexType;
    }

    public Boolean isList() {
        return isList;
    }

    public void setIsList(Boolean isList) {
        this.isList = isList;
    }

    public Boolean isArray() {
        return isArray;
    }

    public void setIsArray(Boolean isArray) {
        this.isArray = isArray;
    }

    public Boolean getIsComplex() {
        return isComplex;
    }

    public void setIsComplex(Boolean isComplex) {
        this.isComplex = isComplex;
    }

}
