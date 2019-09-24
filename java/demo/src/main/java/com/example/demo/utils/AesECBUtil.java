package com.example.demo.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
public class AesECBUtil {

    public AesECBUtil() {
    }

    public static String encrypt(String sSrc, String sKey) {
        if (StringUtils.isBlank(sSrc)) {
            return sSrc;
        } else {
            try {
                if (sKey.length() != 16) {
                    return null;
                } else {
                    byte[] raw = sKey.getBytes("utf-8");
                    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    cipher.init(1, skeySpec);
                    byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
                    return (new Base64()).encodeToString(encrypted);
                }
            } catch (Exception var6) {
                return null;
            }
        }
    }

    public static String decrypt(String sSrc, String sKey) {
        if (StringUtils.isBlank(sSrc)) {
            return sSrc;
        } else {
            try {
                if (sKey.length() != 16) {
                    return null;
                } else {
                    byte[] raw = sKey.getBytes("utf-8");
                    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
                    Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                    cipher.init(2, skeySpec);
                    byte[] encrypted1 = (new Base64()).decode(sSrc);

                    try {
                        byte[] original = cipher.doFinal(encrypted1);
                        String originalString = new String(original, "utf-8");
                        return originalString;
                    } catch (Exception var8) {

                        return null;
                    }
                }
            } catch (Exception var9) {
                return null;
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(decrypt("ifGNmK+4T5JCWj0R5irbU/5guKXMEsmaOgRLMhYHCks=", "g0Q9;cf#5ae;ab,1"));
    }
}
