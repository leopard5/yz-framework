package com.yz.framework.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.springframework.util.Base64Utils;

public class DESUtil {
	// 算法名称
	public static final String KEY_ALGORITHM = "DES";
	// 算法名称/加密模式/填充方式
	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
	public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";

	private static Key toKey(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		DESKeySpec keSpec = new DESKeySpec(key);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
		SecretKey secretKey = secretKeyFactory.generateSecret(keSpec);
		return secretKey;
	}

	public static byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Key secretKey = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	public static byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Key secretKey = toKey(key);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	public static byte[] initKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
		keyGenerator.init(56);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();
	}

	public static String generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[] key = initKey();
		String keysString = Base64Utils.encodeToString(key);
		return keysString;
	}

	public static String decrypt(String cipherText, String key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyByte = Base64Utils.decodeFromString(key);
		byte[] cipherData = Base64Utils.decodeFromString(cipherText);
		byte[] data = decrypt(cipherData, keyByte);
		return new String(data);
	}

	public static String encrypt(String plainText, String key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] keyByte = Base64Utils.decodeFromString(key);
		byte[] data = plainText.getBytes();
		byte[] cipherData = encrypt(data, keyByte);
		return Base64Utils.encodeToString(cipherData);
	}
}