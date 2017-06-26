package com.yz.framework.util;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

public abstract class CollectionUtil {

    public static <T> ArrayList<T> removeDuplicate(List<T> list) {
        return new ArrayList<T>(new HashSet<T>(list));
    }

    public static <T> T first(List<T> cols) {
        if (cols == null || cols.size() < 1) return null;

        for (T c : cols) {
            if (c != null) return c;
        }
        return null;
    }

    public static <T> T first(List<T> cols, String propName, Object propValue, Class<T> clazz) {
        if (cols == null || cols.size() < 1) return null;

        try {
            for (T c : cols) {
                Field field = clazz.getDeclaredField(propName);
                field.setAccessible(true);
                Object value = field.get(c);

                if (value.equals(propValue)) {
                    return c;
                }

            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static <T> BigDecimal sum(List<T> cols, String propName, Class<T> clazz) {
        if (cols == null || cols.size() < 1) return null;

        BigDecimal result = BigDecimal.ZERO;

        try {
            for (T c : cols) {
                Field field = clazz.getDeclaredField(propName);
                field.setAccessible(true);
                BigDecimal value = (BigDecimal) field.get(c);

                result = result.add(value);
            }
        } catch (Exception ex) {
            return null;
        }
        return result;
    }

    /****
     * 在数组中找出其中一个字段，组成新的数组，
     * @param cols
     * @param selectCol
     * @param clazz
     * @return
     */
    public static <T, K> List<K> selectDistinct(List<T> cols, String selectCol, Class<T> clazz) {
        List<K> result = new LinkedList<K>();

        try {
            for (T c : cols) {
                Field field = clazz.getDeclaredField(selectCol);
                field.setAccessible(true);
                Object key = field.get(c);
                if (key != null) {
                    if (!result.contains((K) key)) {
                        result.add((K) key);
                    }
                }
            }
        } catch (Exception ex) {
            return result;
        }


        return result;
    }

    /***
     * 根据某个字段 将 数组 进行分组返回
     * @param cols
     * @param groupBy
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T, K> Map<K, List<T>> groupBy(List<T> cols, String groupBy, Class<T> clazz) {
        Map<K, List<T>> result = new HashMap<K, List<T>>();
        try {
            for (T c : cols) {
                Field field = clazz.getDeclaredField(groupBy);
                field.setAccessible(true);
                K key = (K) field.get(c);

                if (result.keySet().contains(key)) {
                    List<T> list = result.get(key);
                    list.add(c);
                    result.put(key, list);
                } else {
                    List<T> list = new ArrayList<T>();
                    list.add(c);
                    result.put(key, list);
                }

            }
        } catch (Exception ex) {
            return null;
        }

        return result;
    }


    public static <T> void sort(List<T> list, String property, boolean asc) {
        if (list == null) {
            return;
        }
        Comparator<?> comparator = ComparableComparator.getInstance();
        comparator = ComparatorUtils.nullLowComparator(comparator);
        if (!asc) {
            comparator = ComparatorUtils.reversedComparator(comparator);
        }
        Collections.sort(list, new BeanComparator(property, comparator));
    }
}
