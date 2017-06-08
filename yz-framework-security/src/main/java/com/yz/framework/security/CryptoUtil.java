package com.yz.framework.security;

public abstract class CryptoUtil {

	private static String securityKey;
	private static AESCoder defaultAesCoder;
	static {
		defaultAesCoder = new AESCoder();
		securityKey = getKey();

	}

	public static String encrypt(String data)
			throws Exception
	{
		return defaultAesCoder.encrypt(data, securityKey);

	}

	private static String getKey() {
		String a1 = "ZWNl-ZmZh-ZDg5-YmIx-Y2U1-ZTBk-Zjg5-MTVh";
		String b1 = "Njc3-ZjI2-NTgz-ZDMx-N2Jm-ZTU3-Zjcy-MTE2";
		String x1 = "MDM4ZDkwMzZmMDg0MmJkMmRmZDUyNjhlYzFlYTNlMDY=";
		String aa1 = a1.replaceAll("\\-", "");
		String bb1 = b1.replaceAll("\\-", "");
		String xyz = aa1 + bb1;
		byte[] xyzx = Base64Util.decodeBase64(xyz);
		String c1 = new String(xyzx);
		StringBuilder sb = new StringBuilder();
		for (int i = (c1.length() - 1); i >= 0; i--) {
			sb.append(c1.charAt(i));
		}
		String d = sb.toString();
		sb = new StringBuilder();
		for (int i = (d.length() - 1); i >= 0; i--) {
			sb.append(c1.charAt(i));
		}
		String yy = sb.toString().toLowerCase();
		String re = new String(Base64Util.decodeBase64(x1));
		sb = new StringBuilder();
		for (int i = (re.length() - 1); i >= 0; i--) {
			sb.append(re.charAt(i));
		}
		return sb.toString();
	}

	public static String decrypt(String cipherData) throws Exception
	{
		return defaultAesCoder.decrypt(cipherData, securityKey);
	}

	public static String mixKey(String string) {
		StringBuilder sb = new StringBuilder();
		for (int i = (string.length() - 1); i >= 0; i--) {
			sb.append(string);
		}
		String ss = sb.toString();
		ss = Base64Util.encodeBase64String(string.getBytes());
		return ss;
	}
}
