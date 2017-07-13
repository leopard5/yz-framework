package com.yz.framework.security;

import java.security.Provider;
import java.security.Security;

public class CoderBase {
    public static final String DEFAULT_CHARSET_STRING = "UTF-8";

    static {
        if (Security.getProvider("BC") == null) {
            try {
                Class<?> clazz = CoderBase.class.getClassLoader().loadClass("org.bouncycastle.jce.provider.BouncyCastleProvider");
                Provider provider = (Provider) clazz.newInstance();
                Security.addProvider(provider);
            } catch (Exception e) {

            }

        }
    }
}
