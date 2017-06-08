package com.yz.framework.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class ListUtil {

	public static <T> List<T> copyFrom(List<T> list) {
		List<T> newList = new ArrayList<T>(list);
		return newList;
	}

	public static <T> List<T> fromSet(Set<T> set) {
		List<T> newList = new ArrayList<T>(set);
		return newList;
	}

	public static Integer[] toIntegerArray(Collection<Integer> collection) {
		Integer[] result = new Integer[collection.size()];
		collection.toArray(result);
		return result;
	}

	public static String[] toStringArray(Collection<String> collection) {
		String[] result = new String[collection.size()];
		collection.toArray(result);
		return result;
	}

	public static <T> List<T> combine(List<T> list1, List<T> list2) {
		Set<T> set = new HashSet<T>();
		set.addAll(list1);
		set.addAll(list2);
		return fromSet(set);
	}

	public static <T> List<T> fromArray(T[] tArray) {
		return Arrays.asList(tArray);
	}

	/**
	 * 以英文逗号或者分号拆分字符串
	 * 
	 * @param input
	 * @return
	 */
	public static List<String> fromString(String input) {
		return fromString(input, "\\,|\\;");
	}

	public static List<String> fromString(String input, String regex) {
		String[] array = input.split(regex);
		return fromArray(array);
	}

	public static String connectToString(List<?> list, char delimiter) {

		if (isNullOrEmpty(list)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Object object : list) {
			if (object != null) {
				sb.append(object.toString() + delimiter);
			}
		}
		if (sb.length() > 0) {
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}

	public static List<Integer> getIntListfromString(String input, String regex) {
		String[] array = input.split(regex);
		List<Integer> list = new ArrayList<Integer>(array.length);
		for (String item : array) {
			if (StringUtil.isNotBlank(item) && StringUtil.isNumeric(item)) {
				list.add(Integer.valueOf(item));
			}
		}
		return list;
	}

	public static <T> boolean isNotNullAndEmpty(Collection<T> list) {

		return !isNullOrEmpty(list);
	}

	public static <T> boolean isNullOrEmpty(Collection<T> list) {

		return list == null || list.isEmpty();
	}

	/**
	 * 使用equals方法判断列表中是否有该商品
	 * 
	 * @param list
	 * @param findT
	 * @return T
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年3月30日 下午6:03:25
	 */
	public static <T> T find(List<T> list, T findT) {

		if (list == null || list.isEmpty() || findT == null) {
			return null;
		}
		for (T t : list) {
			if (t.equals(findT)) {
				return t;
			}
		}
		return null;
	}

	/**
	 * 移除重复项
	 * 
	 * @param matchedGoodsList
	 * @return List<T>
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月2日 下午4:21:30
	 */
	public static <T> List<T> removeDuplicates(List<T> matchedGoodsList) {
		Set<T> sets = new LinkedHashSet<T>();
		sets.addAll(matchedGoodsList);
		return new ArrayList<T>(sets);
	}

}
