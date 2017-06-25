/**
 * yz-framework-core
 * DateUtil.java
 * com.yz.framework.util
 *
 * @author yazhong
 * @date 2015年9月29日 上午11:18:27
 * @version 1.0
 */

package com.yz.framework.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * ClassName:DateUtil <br/>
 * Function: 时间工具类. <br/>
 * Date: 2017年06月20日 上午11:18:27 <br/>
 *
 * @version 1.0
 */
public class DateUtil {

    public static final long MILLI_SECONDS_IN_ONE_DAY = 24 * 3600 * 1000;

    //2001-07-04T12:08:56.235-07或2001-07-04T12:08:56.235Z
    public static final String PATTERN_ISO8601_ONELETTER = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    //2001-07-04T12:08:56.235-0700或2001-07-04T12:08:56.235Z
    public static final String PATTERN_ISO8601_TWOLETTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
    //2001-07-04T12:08:56.235-07:00或2001-07-04T12:08:56.235Z
    public static final String PATTERN_ISO8601_THREELETTER = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    //2001-07-04T12:08:56.235-0700或2001-07-04T12:08:56.235+0000
    public static final String PATTERN_RFC822 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String LOCAL_TIMESTAMP_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String LOCAL_DATE_PATTERN = "yyyy/MM/dd";

    public static final String DEFAULT_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIMESTAMP_PATTERN_NO_SEONCDS = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_TIMESTAMP_PATTERN_NO_SEONCDS2 = "yyyy-MM-dd HHmm";
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_TIME_PATTERN_NO_SEONCDS = "HH:mm";

    public static final String DEFAULT_TIME_ZONE_ID = TimeZone.getDefault().getID();

    private static final DatatypeFactory dtf;

    static {
        try {
            dtf = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param s
     * @return
     */
    public static Date parseTimestampWithoutSeconds(String s) {
        return parse(s, DEFAULT_TIMESTAMP_PATTERN_NO_SEONCDS);
    }

    /**
     * @param s
     * @return
     */
    public static Date parseTimestamp(String s) {
        return parse(s, DEFAULT_TIMESTAMP_PATTERN);
    }

    /**
     * @param s
     * @return
     */
    public static Date parseDate(String s) {
        return parse(s, DEFAULT_DATE_PATTERN);
    }

    /**
     * @param s
     * @return
     */
    public static Date parseTime(String s) {
        return parse(s, DEFAULT_TIME_PATTERN);
    }

    /**
     * @param s
     * @return
     */
    public static Date parseTimeWithoutSeconds(String s) {
        return parse(s, DEFAULT_TIME_PATTERN_NO_SEONCDS);
    }

    /**
     * @param s
     * @param pattern
     * @return
     */
    public static Date parse(String s, String pattern) {
        if (StringUtil.isNullorEmpty(s) || StringUtil.isNullorEmpty(pattern)) {
            return null;
        }

        try {
            return new SimpleDateFormat(pattern).parse(s);
        } catch (ParseException e) {
            //ignore
            return null;
        }
    }

    /**
     * @param date
     * @return
     */
    public static String formatTimestamp(Date date) {
        return format(date, DEFAULT_TIMESTAMP_PATTERN);
    }

    /**
     * @param date
     * @return
     */
    public static String formatTimestampWithoutSeconds(Date date) {
        return format(date, DEFAULT_TIMESTAMP_PATTERN_NO_SEONCDS);
    }

    /**
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        return format(date, DEFAULT_DATE_PATTERN);
    }

    /**
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        return format(date, DEFAULT_TIME_PATTERN);
    }

    /**
     * @param date
     * @return
     */
    public static String formatTimeWithoutSeconds(Date date) {
        return format(date, DEFAULT_TIME_PATTERN_NO_SEONCDS);
    }

    /**
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        if (date == null || StringUtil.isNullorEmpty(pattern)) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * @return
     */
    public static Timestamp GetCurrentTimeStamp() {
        return new Timestamp(new Date().getTime());
    }

    public static Date now() {
        return new Date();
    }

    /**
     * @param date
     * @return
     */
    public static Date minInDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param date
     * @return
     */
    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) return null;

        return new java.sql.Date(date.getTime());
    }

    /**
     * @param date
     * @return
     */
    public static java.sql.Time toSqlTime(Date date) {
        if (date == null) return null;

        return new java.sql.Time(date.getTime());
    }

    /**
     * 使用date的年月日和time的时分秒，拼接成timestamp
     *
     * @param date
     * @param time
     * @return
     */
    public static java.sql.Timestamp toSqlTimeStamp(Date date, Date time) {
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, tmp.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, tmp.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, tmp.get(Calendar.MILLISECOND));

        return new java.sql.Timestamp(calendar.getTimeInMillis());
    }

    /**
     * @param date
     * @return
     */
    public static java.sql.Timestamp toSqlTimeStamp(Date date) {
        if (date == null) return null;

        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * @param date
     * @return
     */
    public static java.util.Date toUtilDate(java.sql.Date date) {
        if (date == null) return null;

        return new java.util.Date(date.getTime());
    }

    /**
     * @param time
     * @return
     */
    public static java.util.Date toUtilDate(java.sql.Time time) {
        if (time == null) return null;

        return new java.util.Date(time.getTime());
    }

    /**
     * @param timestamp
     * @return
     */
    public static java.util.Date toUtilDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) return null;

        return new java.util.Date(timestamp.getTime());
    }

    /**
     * @param date
     * @return
     */
    public static Calendar toCalendar(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param date
     * @return
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        XMLGregorianCalendar ret = dtf.newXMLGregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                calendar.get(Calendar.MILLISECOND),
                calendar.get(Calendar.ZONE_OFFSET) / (1000 * 60));

        return ret;
    }

    /**
     * @param calendar
     * @return
     */
    public static Date toDate(Calendar calendar) {
        if (calendar == null) return null;

        return calendar.getTime();
    }

    /**
     * @param xmlGregorianCalendar
     * @return
     */
    public static Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) return null;

        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    /**
     * @param day1 不能为空
     * @param day2 不能为空
     * @return
     */
    public static boolean isSameDay(Date day1, Date day2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String ds1 = sdf.format(day1);
        String ds2 = sdf.format(day2);
        if (ds1.equals(ds2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间 ，不能为空
     * @param bdate  较大的时间 ，不能为空
     * @return 相差天数
     * @throws ParseException
     */
    public static long daysBetween(Date smdate, Date bdate) {
        long time1 = minInDay(smdate).getTime();
        long time2 = minInDay(bdate).getTime();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return between_days;
    }

    public static String getHour(long time) {
        float hour = (float) ((float) (time) / (float) (60 * 60 * 1000));
        hour = (float) (Math.round(hour * 10) / (float) 10);
        return hour + "";
    }

    public static Date addDay(Date date, int dayNum) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, dayNum);//把日期往后增加一天.整数往后推,负数往前移动
        return calendar.getTime();   //这个时间就是日期往后推一天的结果
    }

    /***
     * 去掉时分秒信息
     * @throws ParseException
     */
    public static Date getDate(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(date);
        return sdf.parse(s);
    }

    //----------------------------------------------------------------------------

    /**
     * 获取日期字符串
     *
     * @param patten 格式化字符串
     * @return
     */
    public static String getDate(String patten) {
        SimpleDateFormat sf = new SimpleDateFormat(patten);

        return sf.format(new Date());
    }

    /**
     * 获取日期字符串
     *
     * @param patten 格式化字符串
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
     * @param patten 格式化字符串
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
     * @param outPattern   日期的输出形式
     * @param inputPattern 日期的输入形式
     * @param inputString  日期的字符串表达式
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
