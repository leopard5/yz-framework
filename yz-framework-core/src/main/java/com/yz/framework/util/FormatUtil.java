package com.yz.framework.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

	/**
	 * DecimalFormat("#0.00")
	 */
	public final static DecimalFormat DF_0_00 = new DecimalFormat("#0.00");

	//
	/**
	 * DecimalFormat("#0")
	 */
	public final static DecimalFormat DF_0 = new DecimalFormat("#0");

	private final static String SDF_yyyyMMdd = "yyyyMMdd";

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	private final static String SDF_SS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * yyyy-MM-dd
	 */
	private final static String SDF_DD = "yyyy-MM-dd";

	/**
	 * yyyy-MM-dd HH:mm:ss SSS
	 */
	private final static String SDF_SSS = "yyyy-MM-dd HH:mm:ss SSS";

	/**
	 * yyyyMMddHHmmss
	 */
	private final static String SDF_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

	/**
	 * yyyyMMddHHmmssSSS
	 */
	private final static String SDF_YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";

	/**
	 * 格式化日期，格式：yyyyMMdd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYYYYMMDD(Date date) {
		return new SimpleDateFormat(SDF_yyyyMMdd).format(date);
	}

	/**
	 * 日期格式化，格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatSS(Date date) {
		return new SimpleDateFormat(SDF_SS).format(date);
	}

	
	
	/**
	 * 日期格式化，格式：yyyy-MM-dd
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDD(Date date) {
		return new SimpleDateFormat(SDF_DD).format(date);
	}

	/**
	 * 日期格式化，格式：yyyy-MM-dd HH:mm:ss SSS
	 * 
	 * @param date
	 * @return
	 */
	public static String formatSSS(Date date) {
		return new SimpleDateFormat(SDF_SSS).format(date);
	}

	/**
	 * 日期格式化，格式：yyyyMMddHHmmss
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYYYYMMDDHHMMSS(Date date) {
		return new SimpleDateFormat(SDF_YYYYMMDDHHMMSS).format(date);
	}

	/**
	 * 日期格式化，格式：yyyyMMddHHmmssSSS
	 * 
	 * @param date
	 * @return
	 */
	public static String formatYYYYMMDDHHMMSSSSS(Date date) {
		return new SimpleDateFormat(SDF_YYYYMMDDHHMMSSSSS).format(date);
	}

}
