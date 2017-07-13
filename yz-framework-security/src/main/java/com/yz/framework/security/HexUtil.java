package com.yz.framework.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public abstract class HexUtil {

    public static String hex2Base64(String hex) throws DecoderException {
        byte[] data = hex2Bytes(hex);
        return Base64Util.encodeBase64String(data);

    }

    public static byte[] hex2Bytes(String hex) throws DecoderException {
        char[] data = hex.toCharArray();
        return Hex.decodeHex(data);
    }
}
