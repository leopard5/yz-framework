package com.yz.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NumberUtil {
	
	/**
	 * 
	 * @param v
	 * @return 如果为null,返回0
	 */
	public static short toPrimary(Short v) {
		return toPrimary(v, (short)0);
	}
	
	/**
	 * 
	 * @param v
	 * @param defaultValue
	 * @return 如果为null,返回defaultValue
	 */
	public static short toPrimary(Short v, short defaultValue) {
		if(v == null) {
			return defaultValue;
		}
		return v;
	}
	
	/**
	 * 
	 * @param v
	 * @return 如果为null,返回0
	 */
	public static int toPrimary(Integer v) {
		return toPrimary(v, 0);
	}
	
	/**
	 * 
	 * @param v
	 * @param defaultValue
	 * @return 如果为null,返回defaultValue
	 */
	public static int toPrimary(Integer v, int defaultValue) {
		if(v == null) {
			return defaultValue;
		}
		return v;
	}
	
	/**
	 * 
	 * @param v
	 * @return 如果为null,返回0
	 */
	public static long toPrimary(Long v) {
		return toPrimary(v, 0L);
	}
	
	/**
	 * 
	 * @param v
	 * @param defaultValue
	 * @return 如果为null,返回defaultValue
	 */
	public static long toPrimary(Long v, long defaultValue) {
		if(v == null) {
			return defaultValue;
		}
		return v;
	}
	
	/**
	 * 将long转换为int，娀果超出int的范围，将抛出{@link IllegalArgumentException}}
	 * @param v
	 * @return
	 */
	public static Integer longToInteger(Long v) {
		if(v == null) return null;
		
		if(v > Integer.MAX_VALUE || v < Integer.MIN_VALUE) {
			throw new IllegalArgumentException();
		}
		return v.intValue();
	}
	
	/**
	 * 将int转换为long
	 * @param longList
	 * @return
	 */
	public static List<Integer> longToInteger(List<Long> longList) {
		List<Integer> lst = new ArrayList<>();
		if(longList != null) {
			for(Long v : longList) {
				lst.add(longToInteger(v));
			}
		}
		return lst;
	}
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	public static Long integerToLong(Integer v) {
		if(v == null) return null;
		
		return v.longValue();
	}
	
	/**
	 * 将int转换为long
	 * @param integerList
	 * @return
	 */
	public static List<Long> integerToLong(List<Integer> integerList) {
		List<Long> lst = new ArrayList<>();
		if(integerList != null) {
			for(Integer v : integerList) {
				lst.add(integerToLong(v));
			}
		}
		return lst;
	}
	
	public static Double bigDecimalToDouble(BigDecimal input) {
		if(input == null) return null;
		
		return input.doubleValue();
	}
	
	public static List<Double> bigDecimalToDouble(List<BigDecimal> inputList) {
		List<Double> lst = new ArrayList<>();
		if(inputList != null) {
			for(BigDecimal v : inputList) {
				lst.add(bigDecimalToDouble(v));
			}
		}
		return lst;
	}
	
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static final Integer toInteger(String s){
		return toInteger(s, null);
	}
	
	public static final Integer toInteger(String s, Integer deafaultValue){
		try {
			return Integer.parseInt(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Integer> toInteger(List<String> list, Integer defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Integer> ret = new ArrayList<>();
		for(String s : list) {
			Integer i = toInteger(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final Long toLong(String s){
		return toLong(s, null);
	}
	
	public static final Long toLong(String s, Long deafaultValue){
		try {
			return Long.parseLong(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Long> toLong(List<String> list, Long defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Long> ret = new ArrayList<>();
		for(String s : list) {
			Long i = toLong(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final Double toDouble(String s){
		return toDouble(s, null);
	}
	
	public static final Double toDouble(String s, Double deafaultValue){
		try {
			return Double.parseDouble(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Double> toDouble(List<String> list, Double defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Double> ret = new ArrayList<>();
		for(String s : list) {
			Double i = toDouble(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final Float toFloat(String s){
		return toFloat(s, null);
	}
	
	public static final Float toFloat(String s, Float deafaultValue){
		try {
			return Float.parseFloat(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Float> toFloat(List<String> list, Float defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Float> ret = new ArrayList<>();
		for(String s : list) {
			Float i = toFloat(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final Short toShort(String s){
		return toShort(s, null);
	}
	
	public static final Short toShort(String s, Short deafaultValue){
		try {
			return Short.parseShort(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Short> toShort(List<String> list, Short defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Short> ret = new ArrayList<>();
		for(String s : list) {
			Short i = toShort(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final Byte toByte(String s){
		return toByte(s, null);
	}
	
	public static final Byte toByte(String s, Byte deafaultValue){
		try {
			return Byte.parseByte(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<Byte> toByte(List<String> list, Byte defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<Byte> ret = new ArrayList<>();
		for(String s : list) {
			Byte i = toByte(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final BigDecimal toBigDecimal(String s){
		return toBigDecimal(s, null);
	}
	
	public static final BigDecimal toBigDecimal(String s, BigDecimal deafaultValue){
		try {
			return new BigDecimal(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<BigDecimal> toBigDecimal(List<String> list, BigDecimal defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<BigDecimal> ret = new ArrayList<>();
		for(String s : list) {
			BigDecimal i = toBigDecimal(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}
	
	public static final BigInteger toBigInteger(String s){
		return toBigInteger(s, null);
	}
	
	public static final BigInteger toBigInteger(String s, BigInteger deafaultValue){
		try {
			return new BigInteger(s);
		}catch(Exception e) {
			return deafaultValue;
		}
	}
	
	public static final List<BigInteger> toBigInteger(List<String> list, BigInteger defaultValue){
		if(list == null || list.isEmpty()) return Collections.emptyList();
		
		List<BigInteger> ret = new ArrayList<>();
		for(String s : list) {
			BigInteger i = toBigInteger(s, defaultValue);
			ret.add(i);
		}
		
		return ret;
	}

	public static String stripTrailingZeros(BigDecimal value){
		if (value == null) {
			return "";
		}
		String s = value.toString();
		if(s.indexOf(".") > 0){  
            s = s.replaceAll("0+?$", "");  //去掉末尾多余的0
            s = s.replaceAll("[.]$", "");  //如最后一位是.则去掉
        }
		return s;
	}
}
