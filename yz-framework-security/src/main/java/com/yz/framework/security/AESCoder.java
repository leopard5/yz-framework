package com.yz.framework.security;

public class AESCoder extends SymmetryCoderBase {
    // 算法名称
    public static final String KEY_ALGORITHM = "AES";
    public static final String DEFAULT_WORK_MODE = "ECB";
    public static final AESKeySize DEFAULT_KEY_SIZE = AESKeySize.size_128;
    // 算法名称/加密模式/填充方式
    // AES共有四种工作模式-->>ECB：电子密码本模式、CBC：加密分组链接模式、CFB：加密反馈模式、OFB：输出反馈模式
    public static final AESPadding DEFAULT_PADDING = AESPadding.PKCS5Padding;

    public AESCoder() {
        this(DEFAULT_KEY_SIZE);
    }

    public AESCoder(
            AESKeySize keySize) {
        this(DEFAULT_PADDING, keySize);
    }

    public AESCoder(
            AESPadding padding,
            AESKeySize keySize) {
        this(padding, DEFAULT_WORK_MODE, keySize);
    }

    public AESCoder(
            AESPadding padding,
            String workMode,
            AESKeySize keySize) {
        super(KEY_ALGORITHM, constructAlgorithm(padding, workMode), keySize.keySize);
    }

    private static String constructAlgorithm(AESPadding padding, String workMode) {
        return KEY_ALGORITHM + "/" + workMode + "/" + padding.toString();
    }

    public enum AESKeySize {
        size_128(128), size_192(192), size_256(256),;
        private int keySize;

        AESKeySize(
                int size) {
            keySize = size;
        }
    }

    public enum AESPadding {
        NoPadding,
        PKCS5Padding,
        ISO10126Padding,

        PKCS7Padding,
        ZeroBytePadding,;

    }

}
