package com.yz.framework.security;

public class DESCoder extends SymmetryCoderBase {
	// 算法名称
	public static final String KEY_ALGORITHM = "DES";
	// 算法名称/加密模式/填充方式
	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
	public static final String CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
	public static final int DEFAULT_KEY_SIZE = 54;

	public DESCoder()
	{
		super(KEY_ALGORITHM, CIPHER_ALGORITHM, DEFAULT_KEY_SIZE);
	}

	public DESCoder(
			DESAlgorithm desAlgorithm,
			DESKeySize keySize)
	{
		super(KEY_ALGORITHM, desAlgorithm.getAlgorithm(), keySize.getKeySize());
	}

	public enum DESAlgorithm
	{
		/**
		 * Java默认实现，只支持keysize为56
		 */
		DES_ECB_PKCS5Padding("DES/ECB/PKCS5Padding"),
		/**
		 * Bouncy Castle实现，请确保已经引入Bouncy Castle包
		 */
		DES_ECB_PKCS7Padding("DES/ECB/PKCS7Padding"), ;

		private String algorithm;

		DESAlgorithm(
				String algorithm)
		{
			this.setValue(algorithm);
		}

		public String getAlgorithm() {
			return algorithm;
		}

		private void setValue(String algorithm) {
			this.algorithm = algorithm;
		}

	}

	public enum DESKeySize
	{
		/**
		 * Java默认实现，只支持keysize为56
		 */
		size_56(56),

		size_64(64), ;

		private int keySize;

		DESKeySize(
				int size)
		{
			this.keySize = size;

		}

		public int getKeySize()
		{
			return this.keySize;
		}
	}
}