package me.zhengjie.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

public class Md5Utils {
    public final static String md5key = "Ms2";
    /**
     * MD5方法
     * @param text 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5Hex(String text, String key){
        //加密后的字符串
        String encodeStr= DigestUtils.md5Hex(text + key);
        return encodeStr;
    }
    public static String md5Hex(String text){
        //加密后的字符串
        return DigestUtils.md5Hex(text);
    }
    public static String md5(String text, String key){
        //加密后的字符串
        return new String(DigestUtils.md5((text + key).getBytes()),StandardCharsets.UTF_8);
    }
    public static String md5(String text){
        //加密后的字符串
        return new String(DigestUtils.md5(text.getBytes()), StandardCharsets.UTF_8);
    }

    /**
     * MD5验证方法
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5Hex(text, key);
        if(md5Text.equalsIgnoreCase(md5))
        {
            System.out.println("MD5验证通过");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(md5Hex("cf#81192").toUpperCase());
    }
}
