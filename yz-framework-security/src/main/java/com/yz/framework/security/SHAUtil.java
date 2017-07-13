package com.yz.framework.security;

import org.apache.commons.codec.digest.DigestUtils;

public abstract class SHAUtil {
    public static String sha1(String data) {
        return DigestUtils.shaHex(data);
    }

    public static String sha256(String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static String sha384(String data) {
        return DigestUtils.sha384Hex(data);
    }

    public static String sha512(String data) {
        return DigestUtils.sha512Hex(data);
    }

}
