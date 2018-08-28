package com.yz.framework.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SimpleCompareDifferenceUtil<T> {
    public String contrastObj(Object oldBean, Object newBean) {
        StringBuilder stringBuilder = new StringBuilder();
        T pojo1 = (T) oldBean;
        T pojo2 = (T) newBean;
        try {
            Class clazz = pojo1.getClass();
            Field[] fields = pojo1.getClass().getDeclaredFields();
            int i = 1;
            for (Field field : fields) {
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(pojo1);
                Object o2 = getMethod.invoke(pojo2);
                if (o1 == null || o2 == null) {
                    continue;
                }
                if (!o1.toString().equals(o2.toString())) {
                    if (i != 1) {
                        stringBuilder.append(";");
                    }
                    stringBuilder.append(i)
                            .append("、字段名称")
                            .append(field.getName())
                            .append(",旧值:")
                            .append(o1)
                            .append(",新值:")
                            .append(o2);
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
