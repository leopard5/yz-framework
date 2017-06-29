package com.yz.framework.util;

public class NumberEqual {

    public static boolean equals(Integer a, Integer b) {
        if (a == null) {
            return b == null;
        }
        return b != null && b.intValue() == a.intValue();
    }

    public static boolean equals(Byte a, Byte b) {
        if (a == null) {
            return b == null;
        }
        return b != null && b.byteValue() == a.byteValue();
    }

    public static boolean equals(Long a, Long b) {
        if (a == null) {
            return b == null;
        }
        return b != null && b.longValue() == a.longValue();
    }
}
