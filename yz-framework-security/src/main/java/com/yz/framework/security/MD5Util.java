package com.yz.framework.security;

import org.apache.commons.codec.digest.DigestUtils;



public class MD5Util {

	public static byte[] md5(String data)
	{
		return DigestUtils.md5(data);
	}

	public static String md5Hex(String data)
	{
		
		return DigestUtils.md5Hex(data);
	}

	public static String md5Hex(byte[] data)
	{
		return DigestUtils.md5Hex(data);
	}

	public static String md5Base64(String data)
	{
		return Base64Util.encodeBase64String(md5(data));
	}
}
