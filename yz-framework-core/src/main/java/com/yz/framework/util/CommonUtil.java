package com.yz.framework.util;

import com.yz.framework.constant.Constants;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 简述:该类是工具类，提供对字符串，数字等的操作 详述:主要提供了以下几个特性(可以继续扩展):
 * <li>判断字符是否为数字</li>
 * <li>字符串是否是数字串</li>
 * <li>字符是否是小写</li>
 * <li>字符串是否是小写字符</li>
 * <li>字符是否是大写字符</li>
 * <li>字符串是否是大写字符串</li>
 * <li>int值转换为十六进制</li>
 * <li>long值转换为十六进制</li>
 * <li>int值转换为八进制</li>
 * <li>long值转换为八进制</li>
 * <li>int值转换为二进制</li>
 * <li>long值转换为二进制</li>
 * <li>字符串为空对象</li>
 * <li>字符串不为空对象</li>
 * <li>字符串为空字符串</li>
 * <li>字符串为空对象</li>
 * <li>字符串不为空字符串</li>
 * <li>处理为空字符串对象</li>
 * <li>处理为空对象</li>
 * <li>汉字字符编码转换</li>
 * <li>将参数转换为UTF-8编码</li>
 * <li>将参数转换为GBK编码</li>
 * <li>将Object类型转换为String类型</li>
 * <li>将Object类型转换为int类型</li>
 * <li>将Object类型转换为long类型</li>
 * <li>将Object类型转换为float类型</li>
 * <li>将Object类型转换为double类型</li>
 * <li>将String类型转换为int类型</li>
 * <li>将String类型转换为long类型</li>
 * <li>将String类型转换为float类型</li>
 * <li>将String类型转换为double类型</li>
 * </p>
 *
 * @author yazhong
 * @version 1.0
 */
public class CommonUtil {

    private final static String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签

    /**
     * 数字正则表达式
     */
    private static final Pattern REG_INTEGER = Pattern.compile("(\\+|\\-)?\\d+");
    /**
     * 小写字符表达式
     */
    private static final Pattern IS_LOWCASE = Pattern.compile("[a-z]+");
    /**
     * 大写字符表达式
     */
    private static final Pattern IS_UPPERCASE = Pattern.compile("[A-Z]+");
    /**
     * 十六进制字符
     */
    private static final char[] CHAR_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};
    /**
     * 手机号表达式
     */
    private static final Pattern MOBILE_PATTERN = Pattern
            .compile("^(13[0-9]|14[5|7]|15[0-3|5-9]|18[0|2|3|5-9])\\d{8}$");
    /**
     * 邮件地址正则表达式
     */
    private static final Pattern MAIL_PATTERN = Pattern.compile("^\\b[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b$");


    private static InetAddress mInetAddr = null;
    private static String mHostname = null;
    private static String mIpAddress = null;
    private static String mCanonical = null;

    private static int UniqueInt = 1000;

    /**
     * 判断字符是否是数字
     * @param sch
     * @return
     */
    public static boolean isDigit(final char sch) {
        final int temp = sch & Constants.NUM_INT255;
        return temp >= Constants.NUM_INT48 && temp <= Constants.NUM_INT57;
    }

    /**
     * <p>
     * 字符串是否是数字串
     * </p>
     *
     * @param string 字符串
     * @return boolean true:数字 false：非数字
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isDigit(final String string) {
        boolean digit = false;
        if (isNotBlank(string)) {
            digit = REG_INTEGER.matcher(string).matches();
        }
        return digit;
    }

    /**
     * <p 字符是否是小写,只能包含a-z字符，不能包含特殊字符
     * </p>
     *
     * @param sch 字符
     * @return boolean true:小写字符
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isLowerCase(final char sch) {
        final int temp = sch & Constants.NUM_INT255;
        return temp >= Constants.NUM_INT97 && temp <= Constants.NUM_INT122;
    }

    /**
     * <p>
     * 字符串是否是小写字符
     * </p>
     *
     * @param string 字符串
     * @return boolean true:字符串为小写字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isLowerCase(final String string) {
        return isNotBlank(string) ? IS_LOWCASE.matcher(string).matches() : false;
    }

    /**
     * <p>
     * 字符是否是大写字符
     * </p>
     *
     * @param sch 字符
     * @return boolean true:大写字符
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isUppercase(final char sch) {
        final int temp = sch & Constants.NUM_INT255;
        return temp >= Constants.NUM_INT65 && temp <= Constants.NUM_INT90;
    }

    /**
     * <p>
     * 字符串是否是大写字符串
     * </p>
     *
     * @param string 字符串
     * @return boolean true:字符串为大写字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isUppercases(final String string) {
        return isNotBlank(string) ? IS_UPPERCASE.matcher(string).matches() : false;
    }

    /**
     * <p>
     * <li>将字符串转换成hexstring</li>
     * </p>
     *
     * @param sourceStr 需要转换的字符串
     * @return String 转换后的字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toHexString(final String sourceStr) {
        final StringBuffer strBuff = new StringBuffer("");
        if (isNotBlank(sourceStr)) {
            for (char ch : sourceStr.toCharArray()) {
                strBuff.append(toHexString(ch));
            }
        }
        return strBuff.toString();
    }

    /**
     * <p>
     * <li>简述：将将字节数组转换成十六进制数字</li>
     * <li>详述：字节转换，循环转换字节数组，将单个字节数组转换成十六进制字符</li>
     * </p>
     *
     * @param bytes 字节数组
     * @return String 返回转换后的字符串
     * @author yazhong.qi
     */
    public static String bytesToHexString(final byte[] bytes) {
        final StringBuilder sBuilder = new StringBuilder("");
        if (bytes != null && bytes.length > Constants.NUM_INT0) {
            for (byte byitem : bytes) {
                final int tmpv = byitem & 0XFF;
                final String thv = Integer.toHexString(tmpv);
                if (thv.length() < Constants.NUM_INT2) {
                    sBuilder.append(Constants.NUM_INT0);
                }
                sBuilder.append(thv);
            }
        }
        return sBuilder.toString();
    }

    private static String toUnsignedString(final long nums, final int shifts) {
        long tmpNums = nums;
        char[] chs = new char[Constants.NUM_INT64];
        int charPosition = Constants.NUM_INT64;
        final int radix = Constants.NUM_INT1 << shifts;
        final int value = radix - Constants.NUM_INT1;
        do {
            chs[--charPosition] = CHAR_ARRAY[(int) (tmpNums & value)];
            tmpNums >>>= shifts;
        } while (Constants.NUM_INT0 < tmpNums);
        return new String(chs, charPosition, Constants.NUM_INT64 - charPosition);
    }

    /**
     * <p>
     * int值转换为十六进制
     * </p>
     *
     * @param nums int值
     * @return String 十六进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toHexString(final int nums) {
        return toUnsignedString(nums, Constants.NUM_INT4);
    }

    /**
     * <p>
     * long值转换为十六进制
     * </p>
     *
     * @param nums long值
     * @return String 十六进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toHexString(final long nums) {
        return toUnsignedString(nums, Constants.NUM_INT4);
    }

    /**
     * <p>
     * int值转换为八进制
     * </p>
     *
     * @param nums int值
     * @return String 八进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toOctalString(final int nums) {
        return toUnsignedString(nums, Constants.NUM_INT3);
    }

    /**
     * <p>
     * long值转换为八进制
     * </p>
     *
     * @param nums long值
     * @return String 八进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toOctalString(final long nums) {
        return toUnsignedString(nums, Constants.NUM_INT3);
    }

    /**
     * <p>
     * int值转换为二进制
     * </p>
     *
     * @param nums int值
     * @return String 二进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toBinaryString(final int nums) {
        return toUnsignedString(nums, Constants.NUM_INT1);
    }

    /**
     * <p>
     * long值转换为二进制
     * </p>
     *
     * @param nums long值
     * @return String 二进制字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String toBinaryString(final long nums) {
        return toUnsignedString(nums, Constants.NUM_INT1);
    }

    /**
     * <p>
     * 判断对象是否为空
     * </p>
     *
     * @param object 源对象
     * @return boolean true:空对象 false:非空对象
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isEmpty(final Object object) {
        return null == object;
    }

    /**
     * <p>
     * 判断空对象不为空
     * </p>
     *
     * @param object 源对象
     * @return boolean true:非空对象 false:空对象
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isNotEmpty(final Object object) {
        return !isEmpty(object);
    }

    /**
     * <p>
     * 字符串为空字符串
     * </p>
     *
     * @param string 源字符串
     * @return boolean true:空字符串 false:非空字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isBlank(final String string) {
        return isEmpty(string) || "".equals(string.trim());
    }

    /**
     * <p>
     * 字符串不为空字符串
     * </p>
     *
     * @param string 源字符串
     * @return boolean true:非空字符串 false:空字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static boolean isNotBlank(final String string) {
        return isNotEmpty(string) && !"".equals(string.trim());
    }

    /**
     * <p>
     * 字符串转换成UTF-8字符串
     * </p>
     *
     * @param string 源字符串
     * @return
     */
    public static String toUTF8(final String string) {
        if (isBlank(string)) {
            throw new IllegalArgumentException("The param can not be null.");
        }
        try {
            return new String(string.getBytes(Constants.CHARSET_UTF8), Constants.CHARSET_ISO88591);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * <p>
     * 字符串转换成GBK字符串
     * </p>
     *
     * @param string 源字符串
     * @return
     */
    public static String toGBK(final String string) {
        if (isBlank(string)) {
            throw new IllegalArgumentException("The param can not be null.");
        }
        try {
            return new String(string.getBytes(Constants.CHARSET_GBK), Constants.CHARSET_ISO88591);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * <p>
     * <li>验证手机号是否正确</li>
     * </p>
     *
     * @param mobile 手机号
     * @return boolean true:正确的手机号 false:错误的手机号
     * @author yazhong.qi
     */
    public static boolean isLegalMobile(final String mobile) {
        return isNotBlank(mobile) ? MOBILE_PATTERN.matcher(mobile).find() : false;
    }

    /**
     * <p>
     * <li>验证邮箱是否非法</li>
     * </p>
     *
     * @param mail 邮件地址
     * @return boolean true:是否是正确的邮件地址 false:非法邮件地址
     * @author yazhong.qi
     */
    public static boolean isLegalMail(final String mail) {
        return isNotBlank(mail) ? MAIL_PATTERN.matcher(mail.toUpperCase(Locale.getDefault())).find() : false;
    }

    /**
     * <p>
     * <li>获取ByteArrayOutputStream字节，并转换成字符串,默认使用UTF-8字符集</li>
     * </p>
     *
     * @param baos 字节数组输出流
     * @return String 将内容转换成字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String byteArrayStreamToString(ByteArrayOutputStream baos) {
        return byteArrayStreamToString(baos, Constants.CHARSET_UTF8);
    }

    /**
     * <p>
     * <li>获取ByteArrayOutputStream字节，并转换成字符串</li>
     * </p>
     *
     * @param baos    字节数组输出流
     * @param charset 字符集名称
     * @return String 将内容转换成字符串
     * @author yazhong.qi
     * @since 1.0
     */
    public static String byteArrayStreamToString(ByteArrayOutputStream baos, String charset) {
        if (null == baos) {
            throw new IllegalArgumentException("The param baos can not be null.");
        }
        try {
            return baos.toString(Constants.CHARSET_UTF8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理Null或空字符串
     *
     * @param string
     * @return String
     * @author yazhong.qi
     * @date 2012-11-30 上午10:45:17
     */
    public static String excNullToString(String string) {
        return excNullToString(string, "");
    }

    /**
     * 处理Null或空字符串
     *
     * @param string
     * @param added
     * @return String
     * @author yazhong.qi
     * @date 2012-11-30 上午10:45:55
     */
    public static String excNullToString(String string, String added) {
        if (isEmpty(string)) {
            string = added;
        }
        return string;
    }

    /**
     * 处理Null或空对象
     *
     * @param obj
     * @return Object
     * @author yazhong.qi
     * @date 2012-11-30 上午10:45:17
     */
    public static Object excNullToObject(Object obj) {
        return excNullToObject(obj, new Object());
    }

    /**
     * 处理Null或空对象
     *
     * @return Object
     * @author yazhong.qi
     * @date 2012-11-30 上午10:45:55
     */
    public static Object excNullToObject(Object obj, Object added) {
        if (isEmpty(obj)) {
            obj = added;
        }
        return obj;
    }

    /**
     * 将字Object转换为字符串
     *
     * @param obj
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static String objectToString(Object obj) {
        return objectToString(obj, null);
    }

    /**
     * 将字Object转换为字符串
     *
     * @param obj
     * @param added
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static String objectToString(Object obj, String added) {
        String result = added;
        if (obj != null) {
            result = String.valueOf(obj);
        }
        return result;
    }

    /**
     * 将字符串转换成整数类型，如果为空则转换成0
     *
     * @param string
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static int stringToInt(String string) {
        return stringToInt(string, 0);
    }

    /**
     * 将字符串转换成整数类型，如果为空则转换成指定值
     *
     * @param string
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static int stringToInt(String string, int added) {
        int result = 0;
        try {
            result = Integer.parseInt(string);
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将object转换成整数类型，如果为空则转换成0
     *
     * @param obj
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static int objectToInt(Object obj) {
        return objectToInt(obj, 0);
    }

    /**
     * 将object转换成整数类型，如果为空则转换成指定值
     *
     * @param obj
     * @param added
     * @return
     * @author yazhong.qi
     * @date Object-11-30 上午11:29:11
     */
    public static int objectToInt(Object obj, int added) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        }
        return stringToInt(objectToString(obj), added);
    }

    /**
     * 将字符串转换成长整数类型，如果为空则转换成0
     *
     * @param string
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static long stringToLong(String string) {
        return stringToLong(string, 0);
    }

    /**
     * 将字符串转换成长整数类型，如果为空则转换成指定值
     *
     * @param string
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static long stringToLong(String string, long added) {
        long result = 0;
        try {
            result = Long.parseLong(string);
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将字符串转换成长整数类型，如果为空则转换成0
     *
     * @param obj
     * @return
     * @author yazhong.qi
     */
    public static long objectToLong(Object obj) {
        return objectToLong(obj, 0);
    }

    /**
     * 将字符串转换成长整数类型，如果为空则转换成指定值
     *
     * @param obj
     * @param added
     * @author yazhong.qi
     */
    public static long objectToLong(Object obj, long added) {
        long result = 0;
        try {
            result = Long.parseLong(obj.toString());
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将字符串转换成float类型
     *
     * @param string
     * @return float
     * @author yazhong.qi
     * @date 2012-12-11 上午11:58:44
     */
    public static float stringToFloat(String string) {
        return stringToFloat(string, 0.0f);
    }

    /**
     * 将字符串转换成float类型,如果为空则转为指定的值
     *
     * @param string
     * @return float
     * @author yazhong.qi
     * @date 2012-12-11 上午11:58:44
     */
    public static float stringToFloat(String string, float added) {
        float result = 0.0f;
        try {
            result = Float.parseFloat(string);
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将字符串转换成float类型，如果为空则转换成0
     *
     * @param obj
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static float objectToFloat(Object obj) {
        return objectToFloat(obj, 0f);
    }

    /**
     * 将字符串转换成float类型，如果为空则转换成指定值
     *
     * @param obj
     * @param added
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static float objectToFloat(Object obj, float added) {
        float result = 0;
        try {
            result = Float.parseFloat(obj.toString());
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将字符串转换成double类型,如果为空则转为指定的值
     *
     * @param string
     * @return double
     * @author yazhong.qi
     * @date 2012-12-11 上午11:58:44
     */
    public static double stringToDouble(String string) {
        return stringToDouble(string, 0.0d);
    }

    /**
     * 将字符串转换成double类型,如果为空则转为指定的值
     *
     * @param string
     * @return double
     * @author yazhong.qi
     * @date 2012-12-11 上午11:58:44
     */
    public static double stringToDouble(String string, double added) {
        double result = 0.0d;
        try {
            result = Double.parseDouble(string);
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    /**
     * 将字符串转换成double类型，如果为空则转换成0
     *
     * @param obj
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static double objectToDouble(Object obj) {
        return objectToDouble(obj, 0f);
    }

    /**
     * 将字符串转换成double类型，如果为空则转换成指定值
     *
     * @param obj
     * @param added
     * @return
     * @author yazhong.qi
     * @date 2012-11-30 上午11:29:11
     */
    public static double objectToDouble(Object obj, double added) {
        double result = 0;
        try {
            result = Double.parseDouble(obj.toString());
        } catch (Exception e) {
            result = added;
        }
        return result;
    }

    public static String firstCharToUpperCase(String content) {
        if (!isEmpty(content)) {
            String tou = content.substring(0, 1);
            String wei = content.substring(1);
            content = tou.toUpperCase() + wei;
        }
        return content;
    }

    public static String firstCharToLowerCase(String content) {
        if (!isEmpty(content)) {
            String tou = content.substring(0, 1);
            String wei = content.substring(1);
            content = tou.toLowerCase() + wei;
        }
        return content;
    }

    /**
     * 将Unicode编码转换为正常字符
     *
     * @param param
     * @return
     * @author yazhong.qi
     * @date 2012-12-11 下午12:03:57
     */
    public static String stringUncode(String param) {
        if (param != null && !param.trim().equals("")) {
            try {
                param = URLDecoder.decode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return param;
    }

    /**
     * 将字符转换为Unicode编码
     *
     * @param param
     * @return
     * @author yazhong.qi
     * @date 2012-12-11 下午12:04:05
     */
    public static String stringEncode(String param) {
        if (param != null && !param.trim().equals("")) {
            try {
                param = URLEncoder.encode(param, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return param;
    }

    /**
     * 字符串格式化
     *
     * @param resource
     * @param target
     * @return
     */
    public static String format(String resource, String... target) {
        if (resource == null) {
            throw new IllegalArgumentException("parameter is null");
        }
        if (target != null && target.length > 0) {
            for (int i = 0, k = target.length; i < k; i++) {
                resource = resource.replace("{" + i + "}", target[i]);
            }
        }
        return resource;
    }

    public static long getTimeUnix() {
        return new Date().getTime();
    }

    /**
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     * <p>
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    public static Collection intersect(Collection a, Collection b) {
        ArrayList al = new ArrayList();
        if ((a == null) || (a.size() == 0) || (b == null) || (b.size() == 0))
            return al;
        al.addAll(a);
        al.retainAll(b);
        return al;
    }

    public static Collection union(Collection a, Collection b) {
        ArrayList al = new ArrayList();
        if (a != null)
            al.addAll(a);
        if (b != null) {
            Iterator it = b.iterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!al.contains(o))
                    al.add(o);
            }
        }
        return al;
    }

    public static final boolean eq(Object left, Object right) {
        if ((left == null) && (right == null))
            return true;
        if ((left == null) || (right == null))
            return false;

        if (((left instanceof Date)) && ((right instanceof Date))) {
            return ((Date) left).getTime() / 1000L == ((Date) right).getTime() / 1000L;
        }
        return left.equals(right);
    }

    public static boolean eqw(String left, String right) {
        if (left == null)
            left = "";
        if (right == null)
            right = "";
        return left.trim().equals(right.trim());
    }

    public static ArrayList arrayToList(Object[] array) {
        ArrayList returnValue = new ArrayList(array.length);

        for (int i = 0; i < array.length; i++) {
            returnValue.add(array[i]);
        }

        return returnValue;
    }

    public static String[] listToArray(List list) {
        String[] array = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            array[i] = ((String) list.get(i));
        }

        return array;
    }

    public static ArrayList setToList(Set set) {
        ArrayList returnValue = new ArrayList(set.size());

        for (Iterator it = set.iterator(); it.hasNext(); ) {
            returnValue.add(it.next());
        }

        return returnValue;
    }

    public static boolean isInteractiveUserThread() {
        return Thread.currentThread().getName().indexOf("Thread-TaskID") == -1;
    }

    private static void initInet() {
        if (mInetAddr == null) {
            try {
                mInetAddr = InetAddress.getLocalHost();
                mHostname = mInetAddr.getHostName();
                mIpAddress = mInetAddr.getHostAddress();
                mCanonical = mInetAddr.getCanonicalHostName();
            } catch (UnknownHostException ex) {
                mHostname = "localhost";
                mIpAddress = "localhost";
                mCanonical = "localhost";
            }
        }
    }

    public static boolean arrayContains(Object[] array, Object needle) {
        if ((array == null) || (array.length == 0))
            return false;
        if (!array[0].getClass().equals(needle.getClass()))
            return false;
        for (int i = 0; i < array.length; i++) {
            if (needle.equals(array[i]))
                return true;
        }
        return false;
    }

    public static String getCodeSourceLocation(Class cls) {
        if (cls.getProtectionDomain().getCodeSource() != null)
            return cls.getProtectionDomain().getCodeSource().getLocation()
                    .getFile();
        return null;
    }

    public static synchronized int getUniqueInt() {
        UniqueInt += 1;
        if (UniqueInt == 100000000)
            UniqueInt = 1000;
        return UniqueInt;
    }
}
