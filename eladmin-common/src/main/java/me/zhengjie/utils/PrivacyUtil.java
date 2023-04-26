package me.zhengjie.utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据脱敏工具类
 */
public class PrivacyUtil {
    public static String maskSensitiveData(String str, int headCharCount, int tailCharCount) {
        if (StringUtil.isBlank(str))
            return "";
        if (str.length()==2) {
            char[] chars=str.toCharArray();
            chars[0]='*';
            return new String(chars);
        }
        if(str.length()<headCharCount+tailCharCount){
            return "";
        }
        String repeat = "";

        int len = str.length() - headCharCount - tailCharCount;
        if (len > 0) {
            char[] buf = new char[len];
            AtomicInteger integer = new AtomicInteger(0);
            Arrays.asList(new Integer[len]).stream().forEach(b -> buf[integer.getAndIncrement()] = '*');
            repeat = new String(buf);
        }
        return str.substring(0, headCharCount) + repeat + str.substring(str.length() - tailCharCount);
    }

    /**
     * 姓名之类的脱敏
     * @param name
     * @return
     */
    public static String maskNameData(String name){
        return maskSensitiveData(name,1,1);
    }

    /**
     * 手机号脱敏
     * @param phone
     * @return
     */
    public static String maskPhoneData(String phone){
        return maskSensitiveData(phone,3,4);
    }

    /**
     * 银行卡号脱敏
     * @param bankCard
     * @return
     */
    public static String maskBankCardNoData(String bankCard){
        return maskSensitiveData(bankCard,4,4);
    }

    /**
     * 邮箱脱敏
     * @param mailNo
     * @return
     */
    public static String maskMailNoData(String mailNo){
        return maskSensitiveData(mailNo,4,4);
    }

    /**
     * 地址脱敏
     * @param address
     * @return
     */
    public static String maskAddressData(String address){
        if (StringUtil.isBlank(address))
            return "";
        String numberCh="零一二三四五六七八九十壹贰叁肆伍陆柒捌玖拾";
        return address.replaceAll("[0-9A-Za-z"+numberCh+"]","*");
    }

    /**
     * 通用脱敏
     * @param str
     * @return
     */
    public static String maskGenData(String str){
        return maskSensitiveData(str,3,3);
    }

    /**
     * 身份证号码脱敏
     * @param idNum
     * @return
     */
    public static String maskIdCardNumData(String idNum) {
        return maskSensitiveData(idNum,3,4);
    }
}
