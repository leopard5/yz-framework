package com.yz.framework.security.signature;

import com.yz.framework.security.Base64Util;
import com.yz.framework.security.StrKeyPair;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class DSASignature extends SignatureBase {

    public static final String KEY_ALGORITHM = "DSA";
    public static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA1withDSA";

    public DSASignature() {
        super(KEY_ALGORITHM, DEFAULT_SIGNATURE_ALGORITHM);
    }

    public DSASignature(
            DsaSinatureAlgorithm signatureAlgorithm) {
        super(KEY_ALGORITHM, signatureAlgorithm.getAlgorithm());
    }

    public StrKeyPair generateKeyPair() {
        return generateDSAKeyPair(1024);
    }

    public StrKeyPair generateDSAKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            String publicKey = Base64Util.encodeBase64String(keyPair.getPublic().getEncoded());
            String privateKey = Base64Util.encodeBase64String(keyPair.getPrivate().getEncoded());
            return new StrKeyPair(publicKey, privateKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum DsaSinatureAlgorithm {
        SHA1withDSA,
        SHA224withDSA,
        SHA256withDSA,
        SHA384withDSA,
        SHA512withDSA,
        // RIPEMD128withRSA,
        // RIPEMD160withRSA;
        ;

        public String getAlgorithm() {
            return this.toString();
        }

    }
}
