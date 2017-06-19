/**
 * yz-framework-core 
 * DateUtil.java 
 * com.yz.framework.util 
 * 
 * @author yazhong
 * @date   2015年9月29日 上午11:18:27 
 * @version   1.0
 */

package com.yz.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * ClassName:DateUtil <br/>
 * Function: 时间工具类. <br/>
 * Date: 2015年9月29日 上午11:18:27 <br/>
 * 
 * @version 1.0
 */
public class DateUtil {
    /**
     * 获取日期字符串
     * 
     * @param patten
     *            格式化字符串
     * @return
     */
    public static String getDate(String patten) {
        SimpleDateFormat sf = new SimpleDateFormat(patten);

        return sf.format(new Date());
    }

    /**
     * 获取日期字符串
     * 
     * @param patten
     *            格式化字符串
     * @return
     */
    public static String getDate(Date date, String patten) {
        SimpleDateFormat sf = new SimpleDateFormat(patten);
        return sf.format(date);
    }

    /**
     * 当前日期减1
     */
    public static String getPreviousDate(String dateStr, String patten)
            throws ParseException {
        return getPreviousDate(dateStr, 1, patten);
    }

    /**
     * 当前日期减offset值
     */
    public static String getPreviousDate(String dateStr, int offset,
            String patten) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(patten);
        Date date = sf.parse(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, (c.get(Calendar.DAY_OF_YEAR) - offset));
        return sf.format(c.getTime());
    }

    /**
     * 当前日期加1
     */
    public static String getNextDate(String dateStr) throws ParseException {
        return getNextDate(dateStr, 1);
    }

    /**
     * 当前日期加offset值
     */
    public static String getNextDate(String dateStr, int offset)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        Date date = sf.parse(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_YEAR, (c.get(Calendar.DAY_OF_YEAR) + offset));
        return sf.format(c.getTime());
    }

    /**
     * 当前月加1
     */
    public static String getNextMonth(String dateStr) throws ParseException {
        return getNextMonth(dateStr, 1);
    }

    /**
     * 当前月加offset值
     */
    public static String getNextMonth(String dateStr, int offset)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
        Date date = sf.parse(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.MONTH, (c.get(Calendar.MONTH) + offset));
        return sf.format(c.getTime());
    }

    /**
     * 当前年加1
     */
    public static String getNextYear(String dateStr) throws ParseException {
        return getNextYear(dateStr, 1);
    }

    /**
     * 当前年加offset值
     */
    public static String getNextYear(String dateStr, int offset)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy");
        Date date = sf.parse(dateStr);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.YEAR, (c.get(Calendar.YEAR) + offset));
        return sf.format(c.getTime());
    }

    /**
     * 返回解析后的Date对象
     * 
     * @param dateStr
     * @param patten
     * @return
     * @throws ParseException
     */
    public static Date getDate(String dateStr, String patten)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat(patten);
        return sf.parse(dateStr);
    }

    /**
     * 获取上一工作日期
     * 
     * @param patten
     *            格式化字符串
     * @return
     */
    public static String getPreWorkDay(String patten) {
        SimpleDateFormat sf = new SimpleDateFormat(patten);
        Calendar ca = Calendar.getInstance();
        int x = ca.get(Calendar.DAY_OF_WEEK);
        if (x == 1)
            ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) - 2);
        else if (x == 2)
            ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) - 3);
        else
            ca.set(Calendar.DAY_OF_MONTH, ca.get(Calendar.DAY_OF_MONTH) - 1);
        return sf.format(ca.getTime());
    }

    /**
     * 按照模式 获取当前日期的字符串形式 如输入yyyy-MM-dd 获取今天为2007-05-22
     * 
     * @param pattern
     * @return
     */
    public static String getFormatDate(String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }

    /**
     * 按照模式 获取指定日期的字符串形式 如输入yyyy-MM-dd 获取日期为2007-05-22
     * 
     * @param pattern
     * @return
     */
    public static String getFormatDate(String pattern, Date date) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * 将日期从一种形式 转换成另一种形式
     * 
     * @param outPattern
     *            日期的输出形式
     * @param inputPattern
     *            日期的输入形式
     * @param inputString
     *            日期的字符串表达式
     * @return
     * @throws Exception
     */
    public static String getFormatDate(String outPattern, String inputPattern,
            String inputString) throws Exception {

        if (inputPattern.length() != inputString.length()) {
            throw new Exception("输入的字符串格式不对");
        }

        DateFormat df = new SimpleDateFormat(inputPattern);

        Date date = null;
        try {
            date = df.parse(inputString);
        } catch (ParseException e) {
            throw new Exception("输入的字符串格式不对");
        }
        df = new SimpleDateFormat(outPattern);
        return df.format(date);
    }

    /**
     * 
     */
    public static String getChangeDate(String dateStr, String pattern)
            throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        Date date = sf.parse(dateStr);
        return getDate(date, pattern);
    }

    /**
     * date型转换long值 (10位)
     * 
     * @param date
     * @return
     */
    public static long date2Long(Date date) {
        long longDate = date.getTime() / 1000; // 得到秒数，Date类型的getTime()返回毫秒数
        return longDate;
    }

    /**
     * Long型转Date字符串
     * 
     * @param dateFormat
     * @param millSec
     * @return
     */
    public static String transferLongToDate(final String dateFormat,
            final Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec * 1000);
        return sdf.format(date);
    }
}
