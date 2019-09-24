package com.example.demo.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * 基础加密组件
 *
 * @author george on 2018/4/26 下午4:33
 * @version 1.0
 * @since 1.0
 */
public abstract class CoderUtil {
    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";

    /**
     * MAC算法可选以下多种算法
     *
     * <pre>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    public static final String KEY_MAC = "HmacMD5";

    /**
     * BASE64解密
     *
     * @param key
     * @return BASE解密后的byte数组
     * @throws Exception 解密异常
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return 返回BASE64加密字符串
     * @throws Exception 加密异常
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    /**
     * MD5加密
     *
     * @param data
     * @return 返回MD5加密后的byte数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data);

        return md5.digest();

    }

    /**
     * SHA加密
     *
     * @param data
     * @return 返回SHA加密后的byte数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);

        return sha.digest();

    }

    /**
     * 初始化HMAC密钥
     *
     * @return 返回初始化的HMAC密钥
     * @throws Exception 加密异常
     */
    public static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    /**
     * HMAC加密
     *
     * @param data
     * @param key
     * @return 返回HMAC加密的byte数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);

        return mac.doFinal(data);

    }

    /**
     * 用户姓名脱敏
     * @param name
     * @return 返回脱敏后的用户姓名
     * @throws Exception 数据截取异常
     */
    public static String encryptName(String name) throws Exception {
        if(name!=null && !"".equals(name)) {
            int len = name.length();
            if(len > 1) {
                name = name.replaceAll("(?<=\\d{1})\\d(?=\\d{1})", "$1*");
            } else {
                name += "*";
            }
        }
        return name;
    }

    /**
     * 格式化用户姓名
     * @param userName
     * @return 返回格式化后的用户姓名
     * @throws Exception 正则表达式异常或数据截取异常
     */
    public static String formatName(String userName) throws Exception {
        if (userName == null){
            userName = "";
        } else {
            userName = userName.trim();
        }

        int nameLength = userName.length();

        if (nameLength <= 1) {
            userName += "*";
        } else if (nameLength == 2) {
            userName = userName.substring(0, 1) + "*";
        }  else if (nameLength >= 3) {
            userName = userName.replaceAll("([\\d\\D]{1})(.*)", "$1**");
        }

        return userName;
    }

    /**
     * 手机号码脱敏
     * @param phoneNo
     * @return 返回脱敏后的手机号码
     * @throws Exception 正则表达式异常或数据截取异常
     */
    public static String formatPhone(String phoneNo) throws Exception {
        if(phoneNo!=null) {
            phoneNo = phoneNo.trim();
            if(phoneNo.length()==11) {
                phoneNo = phoneNo.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
            } else if(phoneNo.length()>7) {
                phoneNo = phoneNo.substring(0, 3) + "****" + phoneNo.substring(7);
            }
        }
        return phoneNo;
    }

}
