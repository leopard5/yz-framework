package com.yz.framework.security.cert;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.yz.framework.security.Base64Util;

public class X509CertCoder {

	private static final String CERT_TYPE = "X.509";

	/**
	 * 获取私密
	 * 
	 * @param keystorePath
	 * @param password
	 * @param alias
	 * @return PrivateKey
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月9日 下午3:54:34
	 */
	public static PrivateKey getPrivateKey(String keystorePath, String password, String alias) throws Exception {

		KeyStore keyStore = getKeyStore(keystorePath, password);
		return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
	}

	/**
	 * 获取私密
	 * 
	 * @param inputStream
	 * @param password
	 * @param alias
	 * @return PrivateKey
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月9日 下午3:54:08
	 */
	public static PrivateKey getPrivateKey(InputStream inputStream, String password, String alias) throws Exception {

		KeyStore keyStore = getKeyStore(inputStream, password);
		return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
	}

	public static PublicKey getPublicKey(String certificatePath) throws Exception {
		Certificate certificate = getCertificate(certificatePath);
		return certificate.getPublicKey();
	}

	public static PublicKey getPublicKey(InputStream inputStream) throws Exception {
		Certificate certificate = getCertificate(inputStream);
		return certificate.getPublicKey();
	}

	public static Certificate getCertificate(String certificatePath)
			throws Exception {
		FileInputStream inpuStream = null;
		try {
			inpuStream = new FileInputStream(certificatePath);
			return getCertificate(inpuStream);
		} catch (Exception e) {
			throw e;
		} finally {
			if (inpuStream != null) {
				inpuStream.close();
				inpuStream = null;
			}
		}
	}

	public static Certificate getCertificate(InputStream inpuStream) throws CertificateException {
		CertificateFactory certificateFactory = CertificateFactory.getInstance(CERT_TYPE);
		Certificate certificate = certificateFactory.generateCertificate(inpuStream);
		return certificate;
	}

	public static KeyStore getKeyStore(String keystorePath, String password) throws Exception {

		FileInputStream inpuStream = null;
		try {
			inpuStream = new FileInputStream(keystorePath);
			return getKeyStore(inpuStream, password);
		} catch (Exception e) {
			throw e;
		} finally {
			if (inpuStream != null) {
				inpuStream.close();
			}
		}
	}

	public static KeyStore getKeyStore(InputStream inpuStream, String password) throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(inpuStream, password.toCharArray());
		return keyStore;
	}

	/**
	 * 使用证书私钥进行字符串加密
	 * 
	 * @param data
	 *            待加密的数据
	 * @param keystorePath
	 *            证书路径
	 * @param password
	 *            证书密码
	 * @param alias
	 *            证书别名
	 * @return 加密后的字符串
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月7日 下午6:29:52
	 */
	public static String encrypteByPrivateKey(String data, String keystorePath, String password, String alias)
			throws Exception
	{
		byte[] cipherData = encrypteByPrivateKey(data.getBytes(), keystorePath, password, alias);
		return Base64Util.encodeBase64String(cipherData);
	}

	public static byte[] encrypteByPrivateKey(byte[] data, String keystorePath, String password, String alias)
			throws Exception
	{
		PrivateKey privateKey = getPrivateKey(keystorePath, password, alias);
		return encrypteByPrivateKey(data, privateKey, password, alias);
	}

	public static byte[] encrypteByPrivateKey(byte[] data, PrivateKey privateKey, String password, String alias)
			throws Exception
	{
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	public static String decryptByPublicKey(String cipherData, String cerPath)
			throws Exception
	{
		byte[] data = decryptByPublicKey(Base64Util.decodeBase64(cipherData), cerPath);
		return new String(data);
	}

	public static byte[] decryptByPublicKey(byte[] data, String cerPath)
			throws Exception
	{
		PublicKey publicKey = getPublicKey(cerPath);
		return decryptByPublicKey(data, publicKey);
	}

	public static byte[] decryptByPublicKey(byte[] data, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	public static byte[] encryptByPublicKey(byte[] data, String cerPath)
			throws Exception
	{
		PublicKey publicKey = getPublicKey(cerPath);
		return encryptByPublicKey(data, publicKey);
	}

	public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey)
			throws Exception
	{
		Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	public static byte[] decryptByPrivateKey(byte[] data, String keystorePath, String password, String alias)
			throws Exception
	{
		PrivateKey privateKey = getPrivateKey(keystorePath, password, alias);
		return decryptByPrivateKey(data, privateKey);
	}

	public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey)
			throws Exception
	{
		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(data);
	}

	public static byte[] sign(
			byte[] data,
			String certificatePath,
			String keystorePath,
			String alias,
			String password)
			throws Exception
	{
		X509Certificate certificate = (X509Certificate) getCertificate(certificatePath);
		PrivateKey privateKey = getPrivateKey(keystorePath, password, alias);
		return sign(data, certificate, privateKey);
	}

	public static byte[] sign(
			byte[] data,
			X509Certificate certificate,
			PrivateKey privateKey
			) throws NoSuchAlgorithmException, InvalidKeyException,
					SignatureException {
		Signature signature = Signature.getInstance(certificate.getSigAlgName());
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	public static String sign(
			String data,
			String cerPath,
			String keystorePath,
			String alias,
			String password) throws Exception {
		byte[] signData = sign(data.getBytes(), cerPath, keystorePath, alias, password);
		return Hex.encodeHexString(signData);
	}

	public static boolean verify(
			byte[] data,
			String certificatePath,
			byte[] sign)
			throws Exception
	{
		X509Certificate certificate = (X509Certificate) getCertificate(certificatePath);
		return verify(data, sign, certificate);
	}

	public static boolean verify(
			byte[] data,
			byte[] sign,
			X509Certificate certificate)
			throws NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {
		Signature signature = Signature.getInstance(certificate.getSigAlgName());
		signature.initVerify(certificate);
		signature.update(data);
		return signature.verify(sign);
	}

	/**
	 * 验证签名
	 * 
	 * @param data
	 * @param cerPath
	 * @param sign
	 * @return boolean
	 * @throws DecoderException
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月9日 下午4:00:48
	 */
	public static boolean verify(String data, String cerPath, String sign) throws DecoderException, Exception {
		return verify(data.getBytes(), cerPath, Hex.decodeHex(sign.toCharArray()));
	}

	/**
	 * 使用公钥进行加密
	 * 
	 * @param data
	 * @param cerPath
	 * @return String
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月9日 下午4:00:15
	 */
	public static String encryptByPublicKey(
			String data,
			String cerPath) throws Exception {
		byte[] cipherData = encryptByPublicKey(data.getBytes(), cerPath);
		return Base64Util.encodeBase64String(cipherData);
	}

	/**
	 * 使用证书私钥进行解密
	 * 
	 * @param cipherData
	 *            加密数据
	 * @param keystorePath
	 *            证书路径
	 * @param password
	 *            证书密码
	 * @param alias
	 *            证书别名
	 * @return
	 * @throws Exception
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年8月7日 下午6:26:36
	 */
	public static String decryptByPrivateKey(
			String cipherData,
			String keystorePath,
			String password,
			String alias) throws Exception {
		byte[] data = decryptByPrivateKey(Base64Util.decodeBase64(cipherData), keystorePath, password, alias);
		return new String(data);
	}
}
