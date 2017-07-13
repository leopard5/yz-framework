package com.yz.framework.security;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class PBECoder extends CoderBase {
    // 算法名称
    private PBEAlgorithm algorithm;

    public PBECoder(
            PBEAlgorithm pbeAlgorithm) {
        this.algorithm = pbeAlgorithm;
    }

    public PBECoder() {
        this.algorithm = PBEAlgorithm.PBEwithMD5andDES;
    }

    public static final int ITERATION_COUNT = 100;

    private Key toKey(String password) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm.getAlgorithm());
        SecretKey secretKey = keyFactory.generateSecret(keySpec);
        return secretKey;
    }

    private byte[] genSalt() {
        SecureRandom sRandom = new SecureRandom();
        return sRandom.generateSeed(8);
    }

    public String generateSalt() {
        byte[] salt = genSalt();
        return Base64Util.encodeBase64String(salt);
    }

    public String encrypt(String plainText, String password, String salt) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] data = plainText.getBytes();
        byte[] salts = Base64Util.decodeBase64(salt);
        byte[] cipherData = doEncrypt(data, password, salts);
        return Base64Util.encodeBase64String(cipherData);
    }

    /**
     * @param cipherText 需要解密的密文
     * @param password   密码
     * @param salt       盐
     * @return
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public String decrypt(String cipherText, String password, String salt) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] cipherData = Base64Util.decodeBase64(cipherText);
        byte[] salts = Base64Util.decodeBase64(salt);
        byte[] decrypteData = doDecrypt(cipherData, password, salts);
        return new String(decrypteData);
    }

    /**
     * 加密
     *
     * @param data     数据
     * @param password 密码
     * @param salt     盐
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception
     */
    public byte[] doEncrypt(byte[] data, String password, byte[] salt) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);

    }

    /**
     * 解密
     *
     * @param data     数据
     * @param password 密码
     * @param salt     盐
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws Exception
     */
    public byte[] doDecrypt(byte[] data, String password, byte[] salt) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Key key = toKey(password);
        PBEParameterSpec paramSpec = new PBEParameterSpec(salt, 100);
        Cipher cipher = Cipher.getInstance(algorithm.algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        return cipher.doFinal(data);

    }

    public enum PBEAlgorithm {

        /**
         * JAVA自带实现
         */
        PBEwithMD5andDES("PBEwithMD5andDES", 56),
        PBEwithMD5andTripleDES("PBEwithMD5andDES", 168),
        PBEwithSHA1andDESede("PBEwithSHA1andDESede", 168),
        PBEwithSHA1andRC2_40("PBEwithSHA1andRC2_40", 128),

        /**
         * BC实现，请确保
         */
        PBEwithMD5andDES_BC("PBEwithMD5andDES", 64),
        PBEwithMD5andRC2("PBEwithMD5andRC2", 128),
        PBEwithSHA1andDES_BC("PBEwithSHA1andDES", 64),
        PBEwithSHA1andRC2("PBEwithSHA1andRC2", 128),;

        private String algorithm;
        private int defaultKeySize;
        ;

        PBEAlgorithm(
                String algorithm, int defaultKeySize) {
            this.algorithm = algorithm;
            this.defaultKeySize = defaultKeySize;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public int getDefaultKeySize() {
            return defaultKeySize;
        }

    }
}