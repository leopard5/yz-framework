package com.yz.framework.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * ClassName:CommonUtil <br/>
 * Function: 通用工具类. <br/>
 * Date: 2016年12月13日 下午1:36:41 <br/>
 *
 * @author yazhong
 * @version 1.0
 */
public class CommonUtil {
    private static InetAddress mInetAddr = null;
    private static String mHostname = null;
    private static String mIpAddress = null;
    private static String mCanonical = null;

    private static int UniqueInt = 1000;

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
