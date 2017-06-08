package com.yz.framework.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class MACUtil extends CoderBase {

	public static String generateKey(MACAlgorithm algorithm)
	{
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.toString());
			SecretKey secretKey = keyGenerator.generateKey();
			byte[] key = secretKey.getEncoded();
			return Hex.encodeHexString(key);

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String encodeHex(String data, String key, MACAlgorithm algorithm)
			throws DecoderException, InvalidKeyException, NoSuchAlgorithmException
	{
		byte[] signData = encode(data.getBytes(), key.toCharArray(), algorithm);
		return Hex.encodeHexString(signData);
	}

	public static byte[] encode(byte[] data, char[] key, MACAlgorithm algorithm) throws DecoderException, NoSuchAlgorithmException, InvalidKeyException
	{
		SecretKey secretKey = new SecretKeySpec(Hex.decodeHex(key), algorithm.toString());
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		byte[] encodeData = mac.doFinal();
		return encodeData;
	}

	public enum MACAlgorithm {
		HmacMD5,
		HmacSHA1,
		HmacSHA256,
		HmacSHA384,
		HmacSHA512,

		HmacMD2,
		HmacMD4,
		HmacSHA224;
	}
}
