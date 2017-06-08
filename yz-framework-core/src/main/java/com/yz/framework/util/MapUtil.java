package com.yz.framework.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.kerberos.KerberosKey;

public class MapUtil {

	private static final String DEFAULT_URL_ENCODE = "utf-8";

	public static String map2QueryString(Map<String, Object> map) {
		if (map == null || map.size() == 0) {
			return "";
		}
		StringBuilder sbBuilder = new StringBuilder();
		Set<Entry<String, Object>> entries = map.entrySet();
		for (Entry<String, Object> entry : entries) {
			Object val = entry.getValue();
			if (val == null) {
				sbBuilder.append(entry.getKey() + "=&");
			} else {
				sbBuilder.append(entry.getKey() + "=" + encode(val) + "&");
			}
		}
		return sbBuilder.substring(0, sbBuilder.length() - 1);
	}

	public static Map<String, Object> queryString2Map(String queryString) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (StringUtil.isBlank(queryString)) {
			return map;
		}
		String[] items = queryString.split("\\&");
		for (String item : items) {
			String[] keyValue = item.split("\\=");
			if (keyValue.length == 2) {
				String key = keyValue[0];
				if (StringUtil.isBlank(key)) {
					continue;
				}
				String value = keyValue[1];
				map.put(key, decode(value));
			}
		}
		return map;
	}

	private static String encode(Object value) {
		if (value == null) {
			return "";
		}
		try {
			return URLEncoder.encode(value.toString(), DEFAULT_URL_ENCODE);
		} catch (Exception e) {
			// never happened
			return value.toString();
		}
	}

	private static String decode(String value) {
		if (value == null) {
			return value;
		}
		try {
			return URLDecoder.decode(value, DEFAULT_URL_ENCODE);
		} catch (UnsupportedEncodingException e) {
			// never happened
			return value;
		}
	}

	/**
	 * 合并两个 Map List，两个List中的Map如果key为uniqueKey的值相同，则合并两个Map为一个Map
	 * 
	 * @param mapList1
	 * @param mapList2
	 * @param uniqueKey
	 * @param overwrite
	 * @return List<Map<K,V>>
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年12月22日 下午4:56:10
	 */
	public static <K, V> List<Map<K, V>> mergeAndCombine(
			List<Map<K, V>> mapList1,
			List<Map<K, V>> mapList2,
			K uniqueKey,
			boolean overwrite) {

		if (mapList1.isEmpty()) {
			return new ArrayList<Map<K, V>>(mapList2);
		}
		if (mapList2.isEmpty()) {
			return new ArrayList<Map<K, V>>(mapList1);
		}
		List<Map<K, V>> returnMapList = new ArrayList<Map<K, V>>(mapList1);
		for (Map<K, V> map : returnMapList) {
			V keyValue = map.get(uniqueKey);
			Map<K, V> findMap = null;
			for (Map<K, V> payMap : mapList2) {
				if (keyValue.equals(payMap.get(uniqueKey))) {
					findMap = payMap;
					merge(map, payMap, overwrite);
					break;
				}
			}
			if (findMap != null) {
				mapList2.remove(findMap);
				findMap = null;
			}
		}
		returnMapList.addAll(mapList2);
		return returnMapList;
	}

	/**
	 * 根据uniqueKey, 把2个list中Map 的Key为uniqueKey的的值合并到第1个Map中，
	 * 
	 * @param mapList1
	 * @param mapList2
	 * @param uniqueKey
	 * @param overwrite
	 * @return List<Map<K,V>>
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年12月16日 下午1:39:11
	 */
	public static <K, V> void merge(
			List<Map<K, V>> mapList1,
			List<Map<K, V>> mapList2,
			K uniqueKey,
			boolean overwrite) {
		List<Map<K, V>> copyMapList2 = new ArrayList<Map<K, V>>(mapList2);
		for (Map<K, V> map1 : mapList1) {
			V keyValue = map1.get(uniqueKey);
			Map<K, V> findMap = null;
			for (Map<K, V> map2 : copyMapList2) {
				if (keyValue.equals(map2.get(uniqueKey))) {
					findMap = map2;
					MapUtil.merge(map1, map2, overwrite);
					break;
				}
			}
			if (findMap != null) {
				copyMapList2.remove(findMap);
				findMap = null;
			}
		}
	}

	public static <K, V> List<String> getStringValueList(
			List<Map<K, V>> mapList,
			K keyName,
			boolean removeDuplicate) {
		return getValueList(mapList, keyName, removeDuplicate);

	}

	@SuppressWarnings("unchecked")
	public static <K, V, FV> List<FV> getValueList(
			List<Map<K, V>> mapList,
			K keyName,
			boolean removeDuplicate) {
		if (removeDuplicate) {
			Set<FV> keyList = new HashSet<FV>();
			for (Map<K, V> map : mapList) {
				keyList.add((FV) map.get(keyName));
			}
			List<FV> valueList = new ArrayList<FV>(keyList);
			return valueList;
		}
		else {
			List<FV> valueList = new ArrayList<FV>();
			for (Map<K, V> map : mapList) {
				valueList.add((FV) map.get(keyName));
			}
			return valueList;
		}

	}

	public static <K, V> void merge(
			Map<K, V> map1,
			Map<K, V> map2,
			boolean overwrite) {
		Set<Entry<K, V>> entries = map2.entrySet();
		if (overwrite) {
			for (Entry<K, V> entry : entries) {
				map1.put(entry.getKey(), entry.getValue());
			}
		}
		else {
			for (Entry<K, V> entry : entries) {
				if (!map1.containsKey(entry.getKey())) {
					map1.put(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public static <K, V> Map<K, V> copyFrom(Map<K, V> sourceMap) {
		Map<K, V> copyMap = new HashMap<K, V>();
		copyMap.putAll(sourceMap);
		return copyMap;
	}

	public static <K, V> List<Map<K, V>> getNotInSecondMapList(
			List<Map<K, V>> mapList1,
			List<Map<K, V>> mapList2,
			K uniqueKeyName) {
		List<Map<K, V>> resultMapList = new ArrayList<Map<K, V>>();
		for (Map<K, V> map1 : mapList1) {
			V v = map1.get(uniqueKeyName);
			boolean found = false;
			for (Map<K, V> map2 : mapList2) {
				if (v.equals(map2.get(uniqueKeyName))) {
					found = true;
					break;
				}
			}
			if (!found) {
				resultMapList.add(map1);
			}
		}
		return resultMapList;
	}

	public static <K, V> Map<V, Map<K, V>> list2Map(List<Map<K, V>> mapList, String valueKeyName) {

		Map<V, Map<K, V>> returnMap = new HashMap<V, Map<K, V>>();
		for (Map<K, V> map : mapList) {
			returnMap.put(map.get(valueKeyName), map);
		}
		return returnMap;
	}

	public static <K, V> Map<V, List<Map<K, V>>> group(List<Map<K, V>> mapList, String groupKeyName) {

		Map<V, List<Map<K, V>>> returnGroupList = new HashMap<V, List<Map<K, V>>>();
		for (Map<K, V> map : mapList) {
			V value = map.get(groupKeyName);
			if (!returnGroupList.containsKey(value)) {
				returnGroupList.put(value, new ArrayList<Map<K, V>>());
			}
			returnGroupList.get(value).add(map);
		}
		return returnGroupList;
	}

	public static boolean isNotNullAndEmpty(Map<?, ?> map) {

		return !isNullOrEmpty(map);
	}

	public static <T> boolean isNullOrEmpty(Map<?, ?> map) {

		return map == null || map.isEmpty();
	}

}
