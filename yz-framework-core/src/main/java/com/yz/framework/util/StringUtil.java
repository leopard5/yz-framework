package com.yz.framework.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author yazhong.qi
 */
public class StringUtil {
    public static Pattern patternHex = Pattern.compile("^[\\d|a-f|A-F]*$");
    public static final String LINE = new String(new byte[]{0x0D, 0x0A});

    public static final NumberFormat NUM_FORMAT = NumberFormat
            .getCurrencyInstance(Locale.CHINA);

    /**
     * 左边填充
     */
    public static int FILL_TYPE_LEFT = 0;

    /**
     * 右边填充
     */
    public static int FILL_TYPE_RIGHT = 1;

    /**
     * 两端填充
     */
    public static int FILL_TYPE_BOTH = 2;

    /**
     * 元
     */
    public static int MONEY_TYPE_YUAN = 1;

    /**
     * 分
     */
    public static int MONEY_TYPE_FEN = 2;

    private StringUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String randomString(int length) {
        if (length < 1) {
            return UUID.randomUUID().toString();
        }
        String ss = UUID.randomUUID().toString();
        if (length >= ss.length()) {
            return ss;
        }

        return ss.substring(0, length);
    }

    public static String trim(String str, String trimedStr) {
        if (str == null) {
            return str;
        }

        String patternStr = Pattern.quote(trimedStr);
        StringBuilder regex = new StringBuilder().append("^(?:").append(patternStr)
                .append(")+|(?:").append(patternStr).append(")+$");

        return str.replaceAll(regex.toString(), "");
    }

    public static boolean isNullorEmpty(String str) {
        return isNullorEmpty(str, true);
    }

    public static boolean equals(String s1, String s2) {
        if (isNullorEmpty(s1) && isNullorEmpty(s2)) {
            return true;
        }
        if (s1 != null && s1.equals(s2)) {
            return true;
        }
        return false;
    }

    public static boolean isNullorEmpty(String str, boolean doTrim) {
        if (str == null) {
            return true;
        }

        if (doTrim) {
            str = trim(str);
        }
        if (str.length() == 0) {
            return true;
        }

        return false;
    }

    public static boolean contains(String[] strs, String value) {
        if (strs == null) {
            return false;
        }

        for (int i = 0; i < strs.length; i++) {
            if (strs[i].equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static String getEmailPrefix(String email) {
        if (StringUtil.isNullorEmpty(email)) {
            return "";
        }

        int index = email.indexOf("@");
        if (index > 0) {
            return email.substring(0, index);
        } else {
            return email;
        }
    }


    /***************************************************************************
     * 检查是否包含某个字符串
     *
     * @param set
     * @param id
     * @return
     */
    public static boolean inSet(String set, String id) {
        return ("," + set + ",").indexOf("," + id + ",") != -1;
    }

    /**
     * 字符串填充方法
     *
     * @param source   原始字符串
     * @param fillStr  填充字符串
     * @param length   填充后的长度
     * @param fillType 填充类型
     * @return
     */
    public static String fillString(String source, String fillStr, int length,
                                    int fillType) {
        int len = source.length();
        if (len >= length) {
            return fillType == FILL_TYPE_LEFT ? source.substring(len - length)
                    : source.substring(0, length);
        }
        int i = 0;
        while (source.length() < length) {
            source = ((fillType == FILL_TYPE_LEFT) || (fillType == FILL_TYPE_BOTH && (i++) % 2 == 0)) ? fillStr
                    + source
                    : source + fillStr;
        }
        return (fillType == FILL_TYPE_RIGHT) ? source.substring(0, length)
                : source.substring(source.length() - length, source.length());

    }

    /**
     * 将List转化为指定字符串分割的长字符串
     *
     * @param rs
     * @param split
     * @return
     */
    public static String list2String(List rs, String split) {
        if (rs == null || rs.size() <= 0) {
            return null;
        }
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < rs.size(); i++) {
            temp.append(rs.get(i));
            temp.append(split);
        }
        StringBuffer result = new StringBuffer(temp.substring(0, temp.length()
                - split.length()));
        return result.toString();
    }

    /**
     * Description : 字符串转List <br>
     *
     * @param p_Param
     * @param p_Delim
     * @return
     */
    public static List String2List(String p_Param, String p_Delim) {
        if (p_Param == null || p_Param.trim().equals("")) {
            return null;
        }

        StringTokenizer st;
        if (p_Delim != null) {
            st = new StringTokenizer(p_Param, p_Delim);
        } else {
            st = new StringTokenizer(p_Param);
        }
        List result = new ArrayList();
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    /**
     * 将字符串数据转化为指定字符串分割的长字符串
     *
     * @param rs
     * @param split
     * @return
     */
    public static String array2String(Object[] rs, String split) {
        StringBuffer result = new StringBuffer(rs[0].toString());
        for (int i = 1, n = rs.length; i < n; i++) {
            result.append(split).append(rs[i]);
        }
        return result.toString();
    }

    /**
     * 获取货币格式
     */
    public static String formatMoney(double money) {
        return NUM_FORMAT.format(money).replaceAll("￥", "");
    }

    private static String[] upperChars = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};

    /**
     * 将字符串中数字大写
     *
     * @param str 原包含小写数字的字符串
     * @return String
     * @author created by Henry, commented by yazhong.qi
     * @date 2015年7月27日 上午9:52:53
     * @since JDK 1.7
     */
    public static String numberUpper(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            sb.append((c >= '0' && c <= '9') ? upperChars[c - '0'] : c + "");
        }
        return sb.toString();
    }

    /**
     * 以最小整数为分显示金额
     *
     * @param money
     * @return
     * @throws NumberFormatException
     * @author yazhong.qi
     * @date 2015年7月27日 上午9:56:51
     * @since JDK 1.7
     */
    public static String fen2yuan(String money) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0.00");
        money = df.format(new Double(money).doubleValue() / 100);
        return money;
    }

    /**
     * 以最小整数为元显示金额
     *
     * @param money
     * @return String
     * @throws NumberFormatException
     * @author henry, commented by yazhong.qi
     * @date 2015年7月27日 上午9:59:15
     * @since JDK 1.7
     */
    public static String yuan2fen(String money) {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("0");
        money = df.format(new Double(money).doubleValue() * 100);
        return money;
    }

    /**
     * 给金额 添加 逗号
     *
     * @param moneyType
     * @param money
     * @return
     */
    public static String showMoney(int moneyType, String money) {
        String formatMoney = null;
        if (money != null && !money.equalsIgnoreCase("")) {
            String prefix = null;
            if (money.startsWith("-")) {
                prefix = money.substring(0, 1);
                money = money.substring(1);
            }
            if (moneyType == MONEY_TYPE_YUAN) {
                money = yuan2fen(money);
            }
            long v1, v2;
            money = money.trim();
            long moneyInt = Long.parseLong(money);
            v1 = moneyInt / 100;
            v2 = moneyInt % 100;
            StringBuffer result = new StringBuffer();
            result.append(".");
            if (v2 < 10) {
                result.append("0");
            }
            result.append(String.valueOf(v2));
            while (v1 >= 1000) {
                v2 = (v1 - 1000) % 1000;
                v1 = v1 / 1000;
                result.insert(0,
                        fillString(String.valueOf(v2), "0", 3, FILL_TYPE_LEFT));
                result.insert(0, ",");
            }
            result.insert(0, String.valueOf(v1));
            if (prefix != null) {
                result.insert(0, prefix);
            }
            formatMoney = result.toString();
        }
        return formatMoney;
    }

    /**
     * 比较两个日期相差的天数，date1-date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int compareDay(Date date1, Date date2) {
        return (int) ((date1.getTime() - date2.getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 获取Exception中的Message
     *
     * @param e
     * @return
     */
    public static String getMessage(Exception e) {
        String ss = e.getMessage();
        int n = ss.indexOf(":");
        return ss.substring(n == -1 ? 0 : n + 1);
    }

    /**
     * 用于解析模板文件，并替换其中的${}变量
     *
     * @param var
     * @param tempFile
     * @param encoding
     * @return
     * @throws Exception
     */
    public static String parseTempFile(Map var, String tempFile, String encoding)
            throws Exception {
        BufferedReader br = null;
        InputStreamReader ir = null;
        FileInputStream fs = null;
        String str = null;
        try {
            fs = new FileInputStream(tempFile);
            ir = new InputStreamReader(fs, encoding);
            br = new BufferedReader(ir, 2048);
            StringBuffer sb = new StringBuffer();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append(LINE);
            }
            str = sb.toString();
            for (Iterator it = var.keySet().iterator(); it.hasNext(); ) {
                String key = it.next().toString();
                str = str.replaceAll("\\$\\{" + key + "\\}", var.get(key)
                        .toString());
            }
            // 替换所有未定义的${}
            str = str.replaceAll("\\$\\{.*\\}", "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                fs.close();
            }
            if (ir != null) {
                ir.close();
            }
            if (br != null) {
                br.close();
            }
        }
        return str;
    }

    /**
     * 从字符串中取出一定长度的内容
     *
     * @param str
     * @param len
     * @return
     */
    public static String prefixLenStr(String str, int len) {
        String result = null;
        if (str != null) {
            if (str.length() < len) {
                result = str;
            } else {
                result = str.substring(0, len);
            }
        }
        return result;
    }

    /**
     * Description : 过滤空字符串 <br>
     * Created on 2017-6-18 16:03:13 <br>
     *
     * @param inputStr
     * @return
     */
    public static String null2Blank(String inputStr) {
        if (inputStr == null || "null".equals(inputStr)) {
            return "";
        } else {
            return inputStr;
        }
    }

    /**
     * Description : 过滤空对象 <br>
     * Created on 2007-5-16 9:56:30 <br>
     *
     * @param obj
     * @return
     */
    public static String null2Blank(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return obj.toString();
        }
    }

    /**
     * Description : 返回_ID集合中的每个id加上引号，并以逗号隔开 <br>
     * Created on 2007-5-22 10:14:12 <br>
     *
     * @param idList
     * @return
     */
    public static String getIdsForSql(List idList) {
        StringBuilder strBuf = new StringBuilder();
        if (idList != null && idList.size() > 0) {
            for (int i = 0; i < idList.size(); i++) {
                strBuf.append((0 == i) ? "" : ",");
                strBuf.append("'").append(idList.get(i)).append("'");
            }
        } else {
            strBuf.append("''");
        }
        return strBuf.toString();
    }

    /**
     * <p>
     * Checks if a String is whitespace, empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * StringUtils.isBlank("  b o b  ") = false
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     * @since 2.0
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if a String is not empty (""), not null and not whitespace only.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str the String to check, may be null
     * @return <code>true</code> if the String is not empty and not null and not
     * whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String dealTagOptionCode(String optionCode) throws Exception {
        String result = optionCode;
        if (null != optionCode && !"".equals(optionCode)) {
            if (optionCode.length() % 2 != 0) {
                result = "0" + result;
            }
        }
        return result;
    }

    /**
     * JSON字符串特殊字符处理，比如：“\A1;1300”
     *
     * @param s
     * @return String
     */
    public static String string2Json(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String trim(String str) {
        return isBlank(str) ? "" : str.trim();
    }

    /**
     * 判断字符串是否是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在指定字符串的左边添加指定数量（totalLength - str.value）的字符
     *
     * @param str         原字符串
     * @param totalLength 总长度
     * @param padChar     需要补充的字符
     * @return 长度等 totalLength新字符串
     * @author yazhong.qi
     * @date 2015年7月27日 上午9:42:50
     * @since JDK 1.7
     */
    public static String padLeft(String str, int totalLength, char padChar) {
        StringBuilder sb = new StringBuilder();
        if (null == str) {
            str = "";
        }
        int padCharNum = totalLength - str.trim().length();
        if (totalLength > 0) {
            for (int i = 0; i < padCharNum; i++) {
                sb.append(padChar);
            }
        }
        return sb.toString() + str.trim();
    }

    private static final String BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String getRandomString(int length) { // length表示生成字符串的长度
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASE.length());
            sb.append(BASE.charAt(number));
        }
        return sb.toString();
    }
}
