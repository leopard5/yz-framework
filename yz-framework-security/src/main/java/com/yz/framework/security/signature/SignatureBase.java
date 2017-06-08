package com.yz.framework.security.signature;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.yz.framework.security.Base64Util;
import com.yz.framework.security.CoderBase;

public class SignatureBase extends CoderBase {
	public SignatureBase(
			String keyAlgorithm, String signatureAlgorithm)
	{
		this.setKeyAlgorithm(keyAlgorithm);
		this.setSignatureAlgorithm(signatureAlgorithm);
	}

	private String keyAlgorithm;

	public String getKeyAlgorithm() {
		return keyAlgorithm;
	}

	public void setKeyAlgorithm(String keyAlgorithm) {
		this.keyAlgorithm = keyAlgorithm;
	}

	public String getSignatureAlgorithm() {
		return SignatureAlgorithm;
	}

	public void setSignatureAlgorithm(String signatureAlgorithm) {
		SignatureAlgorithm = signatureAlgorithm;
	}

	private String SignatureAlgorithm;

	public String sign(String data, String privateKey)
			throws NoSuchAlgorithmException,
			InvalidKeySpecException,
			InvalidKeyException,
			SignatureException
	{
		byte[] sign = sign(data.getBytes(), Base64Util.decodeBase64(privateKey));
		return Hex.encodeHexString(sign);
	}

	public boolean verify(String data, String publicKey, String sign)
			throws NoSuchAlgorithmException,
			InvalidKeySpecException,
			InvalidKeyException,
			SignatureException, DecoderException
	{

		return doVerity(data.getBytes(),
				Base64Util.decodeBase64(publicKey),
				Hex.decodeHex(sign.toCharArray()));
	}

	public byte[] sign(byte[] data, byte[] privateKey)
			throws NoSuchAlgorithmException,
			InvalidKeySpecException,
			InvalidKeyException,
			SignatureException
	{
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
		PrivateKey privateKey2 = keyFactory.generatePrivate(keySpec);
		Signature signature = Signature.getInstance(SignatureAlgorithm);
		signature.initSign(privateKey2);
		signature.update(data);
		return signature.sign();
	}

	public boolean doVerity(byte[] data, byte[] publicKey, byte[] sign)
			throws NoSuchAlgorithmException,
			InvalidKeySpecException,
			InvalidKeyException,
			SignatureException
	{
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
		PublicKey privateKey2 = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SignatureAlgorithm);
		signature.initVerify(privateKey2);
		signature.update(data);
		return signature.verify(sign);
	}
}
