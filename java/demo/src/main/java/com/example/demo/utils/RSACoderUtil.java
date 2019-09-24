package com.example.demo.utils;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * RSA安全编码组件
 *
 * @author steven on 2018/4/26 下午4:59
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public abstract class RSACoderUtil extends CoderUtil {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    public static String getSign(Map<String, String> map, String privateKey) throws Exception {
        ArrayList<String> keylist = new ArrayList<>(map.keySet());
        Collections.sort(keylist);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keylist.size(); i++) {
            sb.append(keylist.get(i)).append("=").append(map.get(keylist.get(i)));
            if (i < keylist.size() - 1) {
                sb.append("&");
            }
        }
        String result = sign(sb.toString().getBytes(), privateKey);
        result = result.replaceAll("\\n", "");
        result = result.replaceAll("\\r", "");
//        log.info("getSignStr:" + sb.toString());
//        log.info("getSignResult:" + result);
        return result;
    }

    public static boolean verify(Map<String, String> map, String publicKey, String sign) {
        ArrayList<String> keylist = new ArrayList<>(map.keySet());
        Collections.sort(keylist);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keylist.size(); i++) {
            sb.append(keylist.get(i)).append("=").append(map.get(keylist.get(i)));
            if (i < keylist.size() - 1) {
                sb.append("&");
            }
        }
//        log.info("verifyStr:" + sb.toString());
        try {
            return verify(sb.toString().getBytes(), publicKey, sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data       加密数据
     * @param privateKey 私钥
     * @return 数字签名
     * @throws Exception 加密异常
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 解密由base64编码的私钥
        byte[] keyBytes = decryptBASE64(privateKey);

        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取私钥匙对象
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(priKey);
        signature.update(data);

        String sign = encryptBASE64(signature.sign());
        sign = sign.replaceAll("\\n", "");
        return sign;
    }

    /**
     * 校验数字签名
     *
     * @param data      加密数据
     * @param publicKey 公钥
     * @param sign      数字签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception 解密异常
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {

        // 解密由base64编码的公钥
        byte[] keyBytes = decryptBASE64(publicKey);

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // KEY_ALGORITHM 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

        // 取公钥匙对象
        PublicKey pubKey = keyFactory.generatePublic(keySpec);

        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(pubKey);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data 加密数据
     * @param key  私钥
     * @return 返回私钥解密后的byte数组
     * @throws Exception 解密异常
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data 加密数据
     * @param key  公钥
     * @return 返回公钥解密后的byte数组
     * @throws Exception 解密异常
     */
    public static byte[] decryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data 加密数据byte数组
     * @param key  公钥
     * @return 返回公钥解密后的字符串
     * @throws Exception 解密异常
     */
    public static String decryptByPubKey(byte[] data, String key)
            throws Exception {
        return new String(decryptByPublicKey(data, key));
    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data 待加密明文
     * @param key  公钥
     * @return 返回公钥加密后的byte数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptByPublicKey(byte[] data, String key)
            throws Exception {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data 待加密数据
     * @param key  私钥
     * @return 返回私钥加密后的byte数组
     * @throws Exception 加密异常
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key)
            throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return 返回私钥加密后的数组
     * @throws Exception 加密异常，字符串转换异常
     */
    public static byte[] encryptByPrivateKey(String data, String key)
            throws Exception {
        return encryptByPrivateKey(data.getBytes(), key);
    }

    /**
     * 取得私钥
     *
     * @param keyMap 密钥对Map
     * @return 私钥
     * @throws Exception 加密异常
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥对Map
     * @return 公钥
     * @throws Exception 加密异常
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return encryptBASE64(key.getEncoded());
    }

    /**
     * 初始化密钥
     *
     * @return 密钥对Map
     * @throws Exception 密钥生成异常
     */
    public static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(2048);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 获得AES密钥
     * AES算法密钥加密流程：1-RSA算法私钥加密，2-BASE64算法加密
     * AES算法密钥解密流程：1-BASE64算法解密，2-RSA算法公钥解密
     *
     * @param encryptedAesKey 已加密的AES密钥
     * @param rsaPublicKey    RSA算法公钥
     * @return
     */
    public static String decryptAesKey(String encryptedAesKey, String rsaPublicKey) throws Exception {
        return decryptByPubKey(CoderUtil.decryptBASE64(encryptedAesKey), rsaPublicKey);
    }

    private static String readKeyStr(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder buffer = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.charAt(0) != '-') {
                buffer.append(line);
            }
        }
        return buffer.toString();
    }

    public static RSAPublicKey loadPublicKey(InputStream in) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(readKeyStr(in));
        KeyFactory factory = KeyFactory.getInstance("rsa");
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return (RSAPublicKey) factory.generatePublic(spec);
    }

    public static RSAPrivateKey loadPrivateKey(InputStream in) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(readKeyStr(in));
        KeyFactory factory = KeyFactory.getInstance("rsa");
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }


    /**
     * 生成一对公私钥
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Map<String, Object> keyMap = RSACoderUtil.initKey();
        System.out.println("私钥：" + getPrivateKey(keyMap));
        System.out.println("公钥：" + getPublicKey(keyMap));
    }
}

