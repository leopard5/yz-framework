package com.yz.framework.security.signature;

public class RSASignature extends SignatureBase {

	public static final String KEY_ALGORITHM = "RSA";
	public static final String DEFAULT_SIGNATURE_ALGORITHM = "MD5withRSA";

	public RSASignature()
	{
		super(KEY_ALGORITHM, DEFAULT_SIGNATURE_ALGORITHM);
	}

	public RSASignature(
			RsaSinatureAlgorithm rsaSinatureAlgorithm)
	{
		super(KEY_ALGORITHM, rsaSinatureAlgorithm.getAlgorithm());
	}

	public enum RsaSinatureAlgorithm
	{
		MD2withRSA,
		MD5withRSA,
		SHA1withRSA,
		SHA224withRSA,
		SHA256withRSA,
		SHA384withRSA,
		SHA512withRSA,
		RIPEMD128withRSA,
		RIPEMD160withRSA;
		;

		public String getAlgorithm()
		{
			return this.toString();
		}

	}
}
