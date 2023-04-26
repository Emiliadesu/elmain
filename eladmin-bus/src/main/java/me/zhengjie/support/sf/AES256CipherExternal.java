package me.zhengjie.support.sf;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

public class AES256CipherExternal {

    private static Logger log = LoggerFactory.getLogger(AES256CipherExternal.class);
    public static byte[] ivBytes = new byte[16];
    private Cipher cipherEncrypt = null;
    private Cipher cipherDncrypt = null;

    public AES256CipherExternal(String key) {
        try {
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            this.cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipherEncrypt.init(1, newKey, ivSpec);
            this.cipherDncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.cipherDncrypt.init(2, newKey, ivSpec);
        } catch (Exception var4) {
            log.error("AES256CipherExternal init Fail,key:" + key, var4);
        }

    }

    public String AES_Encode(String str) throws Exception {
        byte[] textBytes = str.getBytes("UTF-8");
        return Base64.encodeBase64String(this.cipherEncrypt.doFinal(textBytes));
    }

    public String AES_Decode(String str) throws Exception {
        byte[] textBytes = Base64.decodeBase64(str);
        return new String(this.cipherDncrypt.doFinal(textBytes), "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String key = "abcdefghijklmnopqrstuvwxyz123456";
        AES256CipherExternal aes256 = new AES256CipherExternal(key);
        String plainText = "test中文信息内容1234567890";
        String encodeText = aes256.AES_Encode(plainText);
        System.out.println("AES256_Encode : " + encodeText);
        String decodeText = aes256.AES_Decode(encodeText);
        System.out.println("AES256_Decode : " + decodeText);
    }
}
