package com.yz.framework.security;

public class StrKeyPair {

	public StrKeyPair(
			String publickKey, String privateKey)
	{
		this.setPublicKey(publickKey);
		this.setPrivateKey(privateKey);

	}

	private String publicKey;
	private String privateKey;

	public String getPublicKey() {
		return publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	private void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	private void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
