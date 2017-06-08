package com.yz.framework.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;

import com.yz.framework.security.AESCoder.AESKeySize;
import com.yz.framework.security.DESCoder.DESAlgorithm;
import com.yz.framework.security.DESCoder.DESKeySize;
import com.yz.framework.security.MACUtil.MACAlgorithm;
import com.yz.framework.security.PBECoder.PBEAlgorithm;
import com.yz.framework.security.TripleDESCoder.TripleDESAlgorithm;
import com.yz.framework.security.TripleDESCoder.TripleDESKeySize;
import com.yz.framework.security.cert.X509CertCoder;
import com.yz.framework.security.signature.DSASignature;
import com.yz.framework.security.signature.DSASignature.DsaSinatureAlgorithm;
import com.yz.framework.security.signature.RSASignature;
import com.yz.framework.security.signature.RSASignature.RsaSinatureAlgorithm;

public class Demo {

	public static void main(String[] args) {
		// CryptoUtil.xxx();
		CryptoUtilTest();
		// AESCoder aseCoder = new AESCoder();
		// String data = "hello world";
		//
		// String cipherData;
		// try {
		// String x = CryptoUtil.mixKey("038d9036f0842bd2dfd5268ec1ea3e06");
		// System.out.println(x);
		// String key = aseCoder.generateKey();
		// System.out.println(key);
		// cipherData = aseCoder.encrypt(data, key);
		// String plainData = aseCoder.decrypt(cipherData, key);
		// System.out.println(data.equalsIgnoreCase(plainData));
		// } catch (Exception ex)
		// {
		// ex.printStackTrace();
		// }

	}

	public static void MDTest()
	{
		String data = "hello world";
		String sign = MD5Util.md5Hex(data);
		System.out.println("结果:" + sign);
	}

	public static void SHATest()
	{
		String data = "hello world";
		String sign = SHAUtil.sha1(data);
		System.out.println("sha1结果:" + sign);
		sign = SHAUtil.sha256(data);
		System.out.println("sha256结果:" + sign);
		sign = SHAUtil.sha384(data);
		System.out.println("sha384结果:" + sign);
		sign = SHAUtil.sha512(data);
		System.out.println("sha512结果:" + sign);
	}

	private static void DSASignatureTest() {
		try {

			String data = "hello world";

			// 创建一个DSA签名对象
			DSASignature dsaSignature = new DSASignature(DsaSinatureAlgorithm.SHA1withDSA);

			// 生成密密钥对
			StrKeyPair keyPair = dsaSignature.generateKeyPair();

			// 私钥进行签名
			String sign = dsaSignature.sign(data, keyPair.getPrivateKey());
			System.out.println("签名:" + sign);

			// 公钥验证签名
			boolean isOk = dsaSignature.verify(data, keyPair.getPublicKey(), sign);
			System.out.println(DsaSinatureAlgorithm.SHA1withDSA.getAlgorithm() + "签名是否正确:" + isOk);

			// 第一步:生成密钥对
			for (DSASignature.DsaSinatureAlgorithm dsaSinatureAlgorithm : DSASignature.DsaSinatureAlgorithm.values()) {
				dsaSignature = new DSASignature(dsaSinatureAlgorithm);

				// System.out.println("私密码:" + keyPair.getPrivateKey() + "\n");
				// System.out.println("公密码:" + keyPair.getPublicKey() + "\n");
				// System.out.println("明文:" + data + "\n");
				try {

					// 使用私钥进行签名
					sign = dsaSignature.sign(data, keyPair.getPrivateKey());
					System.out.println("签名:" + sign + "\n");

					// 使用公钥进行验证签名
					isOk = dsaSignature.verify(data, keyPair.getPublicKey(), sign);
					System.out.println(dsaSinatureAlgorithm.getAlgorithm() + "签名是否正确:" + isOk);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void RSASignatureTest() {

		String data = "hello world";
		System.out.println("明文:" + data + "\n");

		try {
			// 利用RSACoder生成密钥对
			StrKeyPair keyPair = RSACoder.generateKeyPair(1024);
			System.out.println("私密码:" + keyPair.getPrivateKey());
			System.out.println("公密码:" + keyPair.getPublicKey());
			RSASignature rsaSignature = new RSASignature(RsaSinatureAlgorithm.MD5withRSA);

			// 进行签名
			String sign = rsaSignature.sign(data, keyPair.getPrivateKey());
			System.out.println("签名:" + sign);

			// 进行验证
			boolean isOk = rsaSignature.verify(data, keyPair.getPublicKey(), sign);
			System.out.println(RsaSinatureAlgorithm.MD5withRSA + "签名是否正确:" + isOk + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RSASignature.RsaSinatureAlgorithm rsaSinatureAlgorithm : RSASignature.RsaSinatureAlgorithm.values()) {
			RSASignature rsaSignature = new RSASignature(rsaSinatureAlgorithm);
			try {
				StrKeyPair keyPair = RSACoder.generateKeyPair(1024);

				// 使用私钥进行签名
				String sign = rsaSignature.sign(data, keyPair.getPrivateKey());
				System.out.println("签名:" + sign + "\n");

				// 使用公钥进行验证签名
				boolean isOk = rsaSignature.verify(data, keyPair.getPublicKey(), sign);
				System.out.println(rsaSinatureAlgorithm.getAlgorithm() + "签名是否正确:" + isOk + "\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static void PBEUtilTest() {
		try {
			String data = "hello world";
			String key = "123";
			for (PBEAlgorithm pbeAlgorithm : PBEAlgorithm.values()) {
				PBECoder pbeCoder = new PBECoder(pbeAlgorithm);
				String salt = pbeCoder.generateSalt();
				System.out.println("密码:" + key + "\n");
				System.out.println("明文:" + data + "\n");
				String ecryptData = pbeCoder.encrypt(data, key, salt);
				System.out.println("加密后的密文:" + ecryptData + "\n");
				String decryptData = pbeCoder.decrypt(ecryptData, key, salt);
				System.out.println("解密后的明文:" + decryptData + "\n");
				System.out.println(pbeAlgorithm.getAlgorithm() + "  解密，是否相等:" + data.equals(decryptData) + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void TripleDESTest() {
		try {
			String data = "hello world";
			System.out.println("明文:" + data + "\n");

			TripleDESCoder tripleDESCoder = new TripleDESCoder(TripleDESAlgorithm.DESede_ECB_PKCS7Padding,
					TripleDESKeySize.size_192);
			// 生成Key
			String key = tripleDESCoder.generateKey();
			System.out.println("密码:" + key + "\n");

			// 加密
			String ecryptData = tripleDESCoder.encrypt(data, key);
			System.out.println("加密后的密文:" + ecryptData + "\n");

			// 解密
			String decryptData = tripleDESCoder.decrypt(ecryptData, key);
			System.out.println("解密后的明文:" + decryptData + "\n");
			System.out.println("是否相等:" + data.equals(decryptData) + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void DESTest() {
		try {

			String data = "hello world";
			System.out.println("明文:" + data + "\n");

			// 实例化一个DESCoder，当以DES_ECB_PKCS7Padding作为加密算法或者KeySize=64时，必须引入BC
			DESCoder desCoder = new DESCoder(DESAlgorithm.DES_ECB_PKCS7Padding, DESKeySize.size_56);

			// 生成密码
			String key = desCoder.generateKey();
			System.out.println("密码:" + key + "\n");

			// 加密
			String ecryptData = desCoder.encrypt(data, key);
			System.out.println("加密后的密文:" + ecryptData + "\n");

			// 解密
			String decryptData = desCoder.decrypt(ecryptData, key);
			System.out.println("解密后的明文:" + decryptData + "\n");
			System.out.println("是否相等:" + data.equals(decryptData) + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void AESTest() {
		try {
			String data = "hello world";
			AESCoder aesCoder = new AESCoder(AESKeySize.size_192);
			// 生成Key
			String key = aesCoder.generateKey();
			System.out.println("密码:" + key + "\n");
			System.out.println("明文:" + data + "\n");

			// 加密
			String ecryptData = aesCoder.encrypt(data, key);
			System.out.println("加密后的密文:" + ecryptData + "\n");

			// 解密
			String decryptData = aesCoder.decrypt(ecryptData, key);
			System.out.println("解密后的明文:" + decryptData + "\n");
			System.out.println("是否相等:" + data.equals(decryptData) + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void CryptoUtilTest() {
		try {

			// 待加密的数据
			String data = "hello world";
			// 加密
			String cipherData = CryptoUtil.encrypt(data);
			// 解密
			String plainData = CryptoUtil.decrypt(cipherData);

			long start = System.currentTimeMillis();
			for (int i = 0; i < 10000; i++) {
				// 加密
				cipherData = CryptoUtil.encrypt(data);

				plainData = CryptoUtil.decrypt(cipherData);
				if (!data.equals(plainData)) {
					System.out.println("解密失败");
				}
			}
			long end = System.currentTimeMillis();
			System.out.println("解密10000次用时:" + (end - start) + "毫秒\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void rsaTest() {
		try {
			String data = "hello world";
			System.out.println("明文:" + data + "\n");

			// 获取密钥对，KeySize必须是64的倍数
			StrKeyPair rsaKeyPair = RSACoder.generateKeyPair(1024);
			System.out.println("公钥:" + rsaKeyPair.getPublicKey() + "\n");
			System.out.println("私钥:" + rsaKeyPair.getPrivateKey() + "\n");

			// 利用公钥加密
			String ecryptData = RSACoder.encryptWithPublicKey(data, rsaKeyPair.getPublicKey());
			System.out.println("公钥加密后的密文:" + ecryptData + "\n");

			// 利用私钥解密
			String decryptData = RSACoder.decryptWithPrivateKey(ecryptData, rsaKeyPair.getPrivateKey());
			System.out.println("私钥解密后的明文:" + decryptData + "\n");
			System.out.println("是否相等:" + data.equals(decryptData) + "\n");

			// 利用私钥加密
			ecryptData = RSACoder.encryptWithPrivateKey(data, rsaKeyPair.getPrivateKey());
			System.out.println("私钥加密后的密文:" + ecryptData + "\n");

			// 利用公钥解密
			decryptData = RSACoder.decryptWithPublicKey(ecryptData, rsaKeyPair.getPublicKey());
			System.out.println("公钥解密后的明文:" + decryptData + "\n");
			System.out.println("是否相等:" + data.equals(decryptData) + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void certTest() {
		try {

			String data = "hello world";
			String keystorePath = "D:\\certs\\yz.store";
			String password = "123456";
			String cerPath = "D:\\certs\\yz.cer";
			String alias = "www.yz.com";

			// 使用私钥进行加密
			String cipherData = X509CertCoder.encrypteByPrivateKey(data, keystorePath, password, alias);
			System.out.println("私钥加密:" + cipherData);

			// 使用公钥进行解密
			String data01 = X509CertCoder.decryptByPublicKey(cipherData, cerPath);
			System.out.println("公钥解密:" + data01);
			System.out.println("是否相等:" + data.equals(data01));

			// 使用公钥进行加密
			cipherData = X509CertCoder.encryptByPublicKey(data, cerPath);
			System.out.println("公钥加密:" + cipherData);

			// 使用私钥进行解密
			data01 = X509CertCoder.decryptByPrivateKey(cipherData, keystorePath, password, alias);
			System.out.println("私钥解密:" + data01);
			System.out.println("是否相等:" + data.equals(data01));

			// 使用私钥进行签名，注意：只能用私钥进行签名
			String sign = X509CertCoder.sign(data, cerPath, keystorePath, alias, password);
			System.out.println("证书私钥签名:" + sign);

			// 使用公钥进行验证签名，注意：只能用公钥进行验证签名
			boolean isPass = X509CertCoder.verify(data, cerPath, sign);
			System.out.println("证书公钥验证签名:" + isPass);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void macTest() {
		try {

			String data = "hello world";

			// 生成Key
			String key = MACUtil.generateKey(MACAlgorithm.HmacMD5);
			System.out.println(MACAlgorithm.HmacMD5 + " Key:" + key);

			// 加密并以Hex编码格式返回
			String sign = MACUtil.encodeHex(data, key, MACAlgorithm.HmacMD5);
			System.out.println(MACAlgorithm.HmacMD5 + " 签名:" + sign);

			for (MACAlgorithm algorithm : MACAlgorithm.values()) {

				key = MACUtil.generateKey(algorithm);
				System.out.println(algorithm + " Key:" + key);
				sign = MACUtil.encodeHex(data, key, algorithm);
				System.out.println(algorithm + " 签名:" + sign);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
