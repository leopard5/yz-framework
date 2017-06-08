package com.yz.framework.security;

public class TripleDESCoder extends SymmetryCoderBase {
	// 算法名称
	public static final String KEY_ALGORITHM = "DESede";
	// 算法名称/加密模式/填充方式
	// DES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
	public static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";
	private static final int DEFAULT_KEY_SIZE = 112;

	public TripleDESCoder()
	{
		super(KEY_ALGORITHM, CIPHER_ALGORITHM, DEFAULT_KEY_SIZE);
	}

	public TripleDESCoder(
			TripleDESAlgorithm desAlgorithm,
			TripleDESKeySize keySize)
	{
		super(KEY_ALGORITHM, desAlgorithm.getAlgorithm(), keySize.getKeySize());
	}

	public enum TripleDESAlgorithm
	{

		/**
		 * JAVA自带实现
		 */
		DESede_ECB_PKCS5Padding("DESede/ECB/PKCS5Padding"),

		/**
		 * 由Bouncy Castle实现，请确保已经引入Bouncy Castle包
		 */
		DESede_ECB_PKCS7Padding("DESede/ECB/PKCS7Padding"), ;

		private String algorithm;

		TripleDESAlgorithm(
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

	public enum TripleDESKeySize
	{
		/**
		 * Java默认实现，只支持keysize为56
		 */
		size_112(112),

		// size_168(64),

		/**
		 * 由Bouncy Castle实现，请确保已经引入Bouncy Castle包
		 */
		size_128(128),

		/**
		 * 由Bouncy Castle实现，请确保已经引入Bouncy Castle包
		 */
		size_192(192), ;

		private int keySize;

		TripleDESKeySize(
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
