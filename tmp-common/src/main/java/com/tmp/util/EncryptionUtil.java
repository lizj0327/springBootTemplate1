package com.tmp.util;

/**
 * 加密工具类
 * 
 */
public class EncryptionUtil {

    /**
     * 加密类型
     */
    public static final String HASH_ALGORITHM = "SHA-1";
    /**
     * 加密次数
     */
    public static final int HASH_INTERATIONS = 1024;

    private static final int SALT_SIZE = 8;

    /**
     * 生成随机的salt
     * 
     * @return
     */
    public static byte[] salt() {
        return DigestUtil.generateSalt(SALT_SIZE);
    }

    /**
     * 设定安全的密码，生成随机的salt并经过1024次sha-1,并
     */
    public static String encryptionPassword(String plainPassword, byte[] salt) {
        // byte[] salt = DigestUtil.generateSalt(SALT_SIZE);
        // user.setSalt(Encodes.encodeHex(salt));

        byte[] hashPassword = DigestUtil.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
        return EncodeUtil.encodeHex(hashPassword);
    }
}
