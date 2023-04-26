package me.zhengjie.support.sf;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

public class HmacSha512Coder {
    private static final String algorithm = "HmacSHA512";
    private byte[] keySeeds;
    private final Charset charset = Charset.forName("UTF-8");

    public HmacSha512Coder() {
    }

    public void setKeySeeds(String keeySeedsString) {
        this.keySeeds = (new Base64Codec()).encrypt(keeySeedsString);
    }

    public String generateHMAC(String datas) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(this.keySeeds, "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKey);
        byte[] macData = mac.doFinal(datas.getBytes(this.charset));
        byte[] hashed = (new Hex()).encode(macData);
        return new String(hashed, this.charset);
    }

    public static void main(String[] args) throws Exception {
        HmacSha512Coder sha = new HmacSha512Coder();
        sha.setKeySeeds("123");
        String vStr = "EG4la/dSuBb+75/dfPp0ZjsbEjCl3+sQH2EHG7+kJOA=";
        System.out.println("原数据：" + vStr + "\nHMAC-SHA512:" + sha.generateHMAC(vStr));
    }
}
