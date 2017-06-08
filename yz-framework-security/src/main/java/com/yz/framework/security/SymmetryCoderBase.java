package com.yz.framework.security;

import java.io.UnsupportedEncodingException;
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
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * 如果密码是外部提供， 请确保jdk1\jre\lib\security\替换local_policy.jar
 * 和US_export_policy.jar两个文件
 * 
 * @author yazhong.qi
 * @date 2016年5月14日 下午11:15:00
 * @version 1.0
 */
public abstract class SymmetryCoderBase extends CoderBase {
	// 算法名称
	private String keyAlgorithm;
	public String cipherAlgorithm;
	private int keySize;

	public SymmetryCoderBase(
			String keyAlgorithm2, String cipherAlgorithm2, int keySize2) {
		this.setKeyAlgorithm(keyAlgorithm2);
		this.setCipherAlgorithm(cipherAlgorithm2);
		this.setKeySize(keySize2);
	}

	public String getCipherAlgorithm() {
		return cipherAlgorithm;
	}

	public void setCipherAlgorithm(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
	}

	protected Key toKey(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKey secretKey = new SecretKeySpec(key, keyAlgorithm);
		return secretKey;
	}

	protected byte[] initKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(keyAlgorithm);
		keyGenerator.init(keySize);
		SecretKey secretKey = keyGenerator.generateKey();
		return secretKey.getEncoded();
	}

	public String generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
		byte[] key = initKey();
		String keysString = Hex.encodeHexString(key);
		return keysString;
	}

	/**
	 * 如果密码是外部提供， 请确保jdk1\jre\lib\security\替换local_policy.jar
	 * 和US_export_policy.jar两个文件
	 * 
	 * @param cipherText
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 * @throws NoSuchProviderException
	 * @throws UnsupportedEncodingException
	 *             String
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月14日 下午11:16:41
	 */
	public String decrypt(String cipherText, String key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, NoSuchProviderException,
			UnsupportedEncodingException {
		byte[] keyByte = Hex.decodeHex(key.toCharArray());
		byte[] cipherData = Base64Util.decodeBase64(cipherText);
		byte[] data = decrypt(cipherData, keyByte);
		return new String(data, DEFAULT_CHARSET_STRING);
	}

	/**
	 * 加密 请确保jdk1\jre\lib\security\替换local_policy.jar 和US_export_policy.jar两个文件
	 * 
	 * @param plainText
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException
	 * @throws NoSuchProviderException
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月14日 下午11:13:23
	 */
	public String encrypt(String plainText, String key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, DecoderException, NoSuchProviderException {
		byte[] keyByte = Hex.decodeHex(key.toCharArray());
		byte[] data = plainText.getBytes();
		byte[] cipherData = encrypt(data, keyByte);
		return Base64Util.encodeBase64String(cipherData);
	}

	/**
	 * * 如果密码是外部提供， 请确保jdk1\jre\lib\security\替换local_policy.jar
	 * 和US_export_policy.jar两个文件
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchProviderException
	 *             byte[]
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月14日 下午11:16:01
	 */
	public byte[] decrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
			NoSuchProviderException {
		Key secretKey = toKey(key);
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	/**
	 * 如果密码是外部提供， 请确保jdk1\jre\lib\security\替换local_policy.jar
	 * 和US_export_policy.jar两个文件
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchProviderException
	 *             byte[]
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月14日 下午11:16:17
	 */
	public byte[] encrypt(byte[] data, byte[] key) throws InvalidKeyException, NoSuchAlgorithmException,
			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
		Key secretKey = toKey(key);
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return cipher.doFinal(data);
	}

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public int getKeySize() {
		return keySize;
	}

	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
}