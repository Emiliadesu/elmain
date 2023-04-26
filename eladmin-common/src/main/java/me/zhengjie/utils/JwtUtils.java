package me.zhengjie.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import me.zhengjie.exception.BadRequestException;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

public class JwtUtils {
    public static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String byte2Hex(byte[] data) {
        int length = data.length;
        char[] hexChars = new char[length * 2];
        int index = 0;
        byte[] var4 = data;
        int var5 = data.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            byte value = var4[var6];
            hexChars[index++] = HEX_DIGITS[value >>> 4 & 15];
            hexChars[index++] = HEX_DIGITS[value & 15];
        }

        return new String(hexChars);
    }

    private static byte[] digest(String message) {
        try {
            MessageDigest md5Instance = MessageDigest.getInstance("MD5");
            md5Instance.update(message.getBytes("UTF-8"));
            return md5Instance.digest();
        } catch (Exception var2) {
            throw new RuntimeException("签名验证失败", var2);
        }
    }


    /**
     * 包含报文体的加签
     *
     * @param key     私钥
     * @param payload 请求体
     * @return
     */
    public static String createToken(String key, String payload) {
        try {
            byte[] abstractMessage = digest(payload);
            String subject = byte2Hex(abstractMessage);
            Algorithm algorithm = Algorithm.ECDSA256(null,
                    (ECPrivateKey) getPrivateKey(Base64.decodeBase64(key), "EC"));
            String token = JWT.create()
                    .withSubject(subject)
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))   // 生成签名的有效期,5分钟
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("生成token失败");
        }
    }

    /**
     * 不需要加签报文体
     *
     * @param key 私钥
     * @return
     */
    public static String createToken(String key) {
        try {
            Algorithm algorithm = Algorithm.ECDSA256(null,
                    (ECPrivateKey) getPrivateKey(Base64.decodeBase64(key), "EC"));
            String token = JWT.create()
                    .withIssuer("auth0")
                    .withExpiresAt(new Date(System.currentTimeMillis() + 5 * 60 * 1000))   // 生成签名的有效期,5分钟
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("生成token失败");
        }
    }


    /**
     * 不包含报文体的验签
     *
     * @param token
     * @param key   公钥
     * @return
     */
    public static boolean verifyToken(String token, String key) {
        try {
            Algorithm algorithm = Algorithm.ECDSA256((ECPublicKey) getPublicKey(
                    Base64.decodeBase64(key),
                    "EC"), null);
            DecodedJWT jwt = JWT.require(algorithm)
                    .acceptLeeway(1)
                    .withIssuer("auth0")
                    .build().verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 包含报文体的验签
     *
     * @param token
     * @param key     公钥
     * @param payload 请求体
     * @return
     */
    public static boolean verifyToken(String token, String key, String payload) {
        try {
            byte[] abstractMessage = digest(payload);
            String subject = byte2Hex(abstractMessage);
            Algorithm algorithm = Algorithm.ECDSA256((ECPublicKey) getPublicKey(
                    Base64.decodeBase64(key),
                    "EC"), null);
            DecodedJWT jwt = JWT.require(algorithm)
                    .acceptLeeway(1)
                    .withSubject(subject)
                    .withIssuer("auth0")
                    .build().verify(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***
     * 获取公钥对象
     * @param keyBytes
     * @param algorithm
     * @return
     */
    private static PublicKey getPublicKey(byte[] keyBytes, String algorithm) {
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;
    }

    /**
     * 获取私钥对象
     *
     * @param keyBytes
     * @param algorithm
     * @return
     */
    private static PrivateKey getPrivateKey(byte[] keyBytes, String algorithm) {
        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;
    }

    public static void main(String[] args) {
        generateKey();
    }

    /**
     * 命令生成私钥跟公钥
     * openssl ecparam -genkey -name prime256v1 -noout -out ecdsa-p256-private.key
     * openssl pkcs8 -topk8 -inform pem -in ecdsa-p521-private.key -outform pem -nocrypt -out ecdsa-p256-private.pem
     * <p>
     * openssl ec -in ecdsa-p256-private.pem -pubout -out ecdsa-p256-public.pem
     **/
    private static void generateKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

            keyPairGenerator.initialize(256, random);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
            // First print our generated private key and public key
            String privateKey = Base64.encodeBase64String(ecPrivateKey.getEncoded());
            System.out.println("Private key: " + privateKey);
            String publicKey = Base64.encodeBase64String(ecPublicKey.getEncoded());
            System.out.println("Public key: " + publicKey);
            String token = createToken(privateKey, "{}");
            System.out.println("Token" + token);
            boolean verifyToken = verifyToken(token, publicKey, "{}");
            System.out.println("verify:" + verifyToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
