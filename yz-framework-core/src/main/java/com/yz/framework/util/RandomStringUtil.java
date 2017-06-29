package com.yz.framework.util;

import java.util.Random;

/**
 * 生成随机字符串
 */
public class RandomStringUtil {

    private static char[] chars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'};

    public static String random(int maxLength) {
        if (maxLength <= 0) {
            maxLength = 1;
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxLength; i++) {
            sb.append(chars[random.nextInt(chars.length)]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(random(32));
        }
    }


}
