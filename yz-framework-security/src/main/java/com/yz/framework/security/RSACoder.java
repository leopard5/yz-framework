package com.yz.framework.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class RSACoder {
    // 算法名称
    private static final String KEY_ALGORITHM = "RSA";

    private static final int DEFAULT_KAY_SIZE = 1024;

    public static StrKeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
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

    public static StrKeyPair generateKeyPair() {
        return generateKeyPair(DEFAULT_KAY_SIZE);
    }

    /**
     * @param keySize 必须是64的倍数
     * @return
     */
    public static String encryptWithPublicKey(String data, String publicKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] ecrypteData = encryptWithPublicKey(data.getBytes(), Base64Util.decodeBase64(publicKey));
        return Base64Util.encodeBase64String(ecrypteData);

    }

    public static String encryptWithPrivateKey(String data, String privateKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] ecrypteData = encryptWithPrivateKey(data.getBytes(), Base64Util.decodeBase64(privateKey));
        return Base64Util.encodeBase64String(ecrypteData);
    }

    public static String decryptWithPublicKey(String data, String publicKey) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException {
        byte[] plainData = decryptWithPublicKey(Base64Util.decodeBase64(data), Base64Util.decodeBase64(publicKey));
        return new String(plainData);
    }

    public static String decryptWithPrivateKey(String data, String privateKey) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        byte[] plainData = decryptWithPrivateKey(Base64Util.decodeBase64(data), Base64Util.decodeBase64(privateKey));
        return new String(plainData);
    }

    public static byte[] encryptWithPublicKey(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] encryptWithPrivateKey(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE,
                privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithPublicKey(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decryptWithPrivateKey(byte[] data, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public enum RsaAlgorithm {

    }
}