package me.zhengjie.utils;


import me.zhengjie.exception.BadRequestException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class SecureUtils {


    /**
     * sha256_HMAC加密
     * @param message 消息
     * @param secret  秘钥
     * @return 加密后字符串
     */
    public static String hmacSHA256(String message, String secret) throws Exception {
        String hash = "";
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        hmacSha256.init(secret_key);
        byte[] bytes = hmacSha256.doFinal(message.getBytes());
        hash = byteArrayToHexString(bytes);
        return hash;
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public  static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b!=null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    /**
     * des加密
     * @param data
     * @param key
     * @return
     */
    public static String encryptDexHex(String data, String key) {
        try{
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            byte[] bytes = cipher.doFinal(data.getBytes("UTF-8"));
            BASE64Encoder base64en = new BASE64Encoder();
            return base64en.encode(bytes);
        }catch(Throwable e){
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
    }


    /**
     * des解密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptDesHex(String data, String key){
        try {
            BASE64Decoder base64en = new BASE64Decoder();
            byte[] bt = base64en.decodeBuffer(data);
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key.getBytes());
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            return new String(cipher.doFinal(bt), "UTF-8");
        }catch (Exception e) {
            throw new BadRequestException("数据解析失败,请检查sign是否正确：" + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception{
//        String data = "{\"barCode\":\"富立物流\",\"goodsCode\":\"这是一个测试2号\"," +
//                "\"shopCode\":\"dy_ceshi\",\"snControl\": \"1233\",\"hsCode\": \"0\",\"goodsNameC\": \"ccv00\"," +
//                "\"makeContry\":\"P1234653441542\",\"brand\": \"81154946129684\",\"unit\": \"49674651463549\"," +
//                "\"supplier\": \"hsahdiasLK\"}";

//        String data = "{\"addCode\":\"56789\",\"addName\":\"这是一个测试编码\"," +
//                "\"remake\":\"测试的说明\"}";

//        String data = "{\"merchantId\":\"ZZDM\"," +
//                "\"valueId\":\"ceShiZengZhi223\",\"addCode\": \"ceShiMa233\",\"price\": \"777\"}";

//        String data = "{\"merchantId\":\"ZZDM\"," +
//                "\"status\": \"1\",\"warehouseId\": \"7985216\",\"vasNo\": \"33165\"," +
//                "\"type\":\"6\",\"refNo\": \"ceShi66\",\"notes\": \"11\"," +
//                "\"contractsinvtcode\": \"KYT66524\"}";

//        String data = "{\"orderId\":\"66882\",\"type\":\"5\",\"productId\":\"测试店铺13\"," +
//                "\"shopId\":\"12\",\"poNo\": \"123546\",\"default01\": \"测试为null??\",\"labelType\": \"5\"," +
//                "\"toLabelType\":\"5\",\"qty\": \"6\"}";

        String data="{\"shopCode\":\"\",\"pageSize\":30,\"startTime\":\"2021-07-12 08:02:58\",\"pageNo\":0,\"endTime\":\"2021-07-12 08:03:00\"}\n";
//        String data="{\"shopCode\":\"dy_sw\",\"pageSize\":30,\"startTime\":\"2021-01-01 00:00:00\",\"pageNo\":1,\"endTime\":\"2021-05-26 00:00:00\"}\n";

//        String data="{\n" +
//                "    \"orderNo\": \"4791820189523799076\",\n" +
//                "}";
        String s = SecureUtils.encryptDexHex(data, "WEIHwxqNYRP1eeq1XCbRnEPLuLb3EUZ3");
        System.out.println(s);
//        s=StringUtil.removeEscape(s);
//        Map<String, Object> params = new HashMap<>();
//        //String post = HttpUtil.post("http://127.0.0.1:8081/api/pdd/print-order", params);
//        System.out.println(URLEncoder.encode(s,"UTF-8"));

//        String sign = "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";
//
//        String s1 = SecureUtils.decryptDesHex(sign, "80db75f13e0e4dce80117e6591666d99");
//        System.out.println(s1);
    }

}
