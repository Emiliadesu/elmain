package me.zhengjie.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class StringUtil extends StringUtils {

    /**
     * 字符串变成charList
     *
     * @param src
     * @return
     */
    public static List<String> string2CharList(String src) {
        if (isBlank(src)) return null;
        List<String> target = new ArrayList<>();
        for (int i = 0; i < src.toCharArray().length; i++) {
            target.add(String.valueOf(src.toCharArray()[i]));
        }
        return target;
    }

    /**
     * 检查是否有重复字符
     *
     * @param src
     * @return
     */
    public static boolean checkDuplicate(String src) {
        List<String> list = string2CharList(src);
        if (list == null) return false;
        Set<String> stringSet = new HashSet<>();
        stringSet.addAll(list);
        if (list.size() == stringSet.size()) {
            return false;
        }
        return true;
    }

    /**
     * 每个字符后添加逗号
     *
     * @param src
     * @return
     */
    public static String addComma(String src) {
        List<String> strings = string2CharList(src);
        return join(strings, ",");
    }

    /**
     * 检查s1是否包含在s2中
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean checkContain(String s1, String s2) {
        if (isBlank(s1) || isBlank(s2)) return false;
        List<String> list1 = string2CharList(s1);
        List<String> list2 = string2CharList(s2);
        for (String s : list1) {
            if (!list2.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static String genVerifyCode(int length) {
        String str = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 字符串排序
     *
     * @param str
     * @return
     */
    public static String sortStr(String str) {
        if (isBlank(str))
            return null;
        char[] chs = str.toCharArray();
        Arrays.sort(chs);
        return new String(chs);
    }

    //字母
    public static boolean isL(String c) {
        return c.matches("\\p{L}");
    }

    //标点
    public static boolean isPoint(String c) {
        return c.matches("\\p{P}");
    }

    //数字
    public static boolean isNum(String c) {
        return c.matches("\\d+");
    }

    //特殊字符(空格)
    public static boolean isSpecial1(String c) {
        return c.matches("\\p{Z}");
    }

    //特殊字符(符号)
    public static boolean isSpecial2(String c) {
        return c.matches("\\p{S}");
    }

    //特殊字符(标记符号)
    public static boolean isSpecial3(String c) {
        return c.matches("\\p{M}");
    }

    //得到字符串占用的字节长度
    public static int getStrLenght(String s) {
        List<String> list = string2CharList(s);
        int lenght = 0;
        for (String c : list) {
            boolean L = isL(c);
            boolean P = isPoint(c);
            boolean N = isNum(c);
            if (L || P) {
                if (isChinese(c)) {
                    lenght += 2;
                } else {
                    lenght += 1;
                }
            } else if (N) {
                lenght += 1;
            } else {
                lenght += 2;
            }
        }
        return lenght;
    }

    public static boolean hasSpecial(String s) {
        List<String> list = string2CharList(s);
        for (String c : list) {
            boolean s1 = isSpecial1(c);
            boolean s2 = isSpecial2(c);
            boolean s3 = isSpecial3(c);
            boolean L = isL(c);
            boolean P = isPoint(c);
            boolean N = isNum(c);
            if ((!L && !P && !N)
                    || (s1 || s2 || s3)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isChinese(String c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c.charAt(0));
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isChineseAll(String s){
        char[]chars=s.toCharArray();
        for (char c : chars) {
            if (!isChinese(c+"")){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String s = "张s，,";
        System.out.println(isChineseAll(s));
    }

    /**
     * 字符串读取request中的body内容
     */
    public static String getBodyTextAsRequest(HttpServletRequest request) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder("");
        try {
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    //判断txt文本的内容编码是不是UTF-8
    public static boolean isUTF8(byte[] rawtext) {
        int score = 0;
        int i, rawtextlen = 0;
        int goodbytes = 0, asciibytes = 0;
        // Maybe also use UTF8 Byte Order Mark: EF BB BF
        // Check to see if characters fit into acceptable ranges
        rawtextlen = rawtext.length;
        for (i = 0; i < rawtextlen; i++) {
            if ((rawtext[i] & (byte) 0x7F) == rawtext[i]) {
                // 最高位是0的ASCII字符
                asciibytes++;
                // Ignore ASCII, can throw off count
            } else if (-64 <= rawtext[i] && rawtext[i] <= -33
                    //-0x40~-0x21
                    && // Two bytes
                    i + 1 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65) {
                goodbytes += 2;
                i++;
            } else if (-32 <= rawtext[i]
                    && rawtext[i] <= -17
                    && // Three bytes
                    i + 2 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65 && -128 <= rawtext[i + 2]
                    && rawtext[i + 2] <= -65) {
                goodbytes += 3;
                i += 2;
            }
        }
        if (asciibytes == rawtextlen) {
            return false;
        }
        score = 100 * goodbytes / (rawtextlen - asciibytes);
        if (score > 98) {
            return true;
        } else if (score > 95 && goodbytes > 30) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * emoji表情替换
     *
     * @param source 原字符串
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source) {
        if (source != null && source.length() > 0) {
            return source.replaceAll("[\ud800\udc00-\udbff\udfff\ud800-\udfff]", "");
        } else {
            return source;
        }
    }

    /**
     * 字符串转换成为16进制(无需Unicode编码)
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }
    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
    public static String removeEscape(String str) {
        if (StringUtil.isEmpty(str)){
            return "";
        }
        return str.replaceAll("[\n\r\t ]+","");
    }
    /**
     * 十六进制字符串转二进制
     *
     * @param hexString
     * @return
     */
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }
    /**
     * 二进制字符串转十六进制
     *
     * @param bString
     * @return
     */
    public static String binaryString2hexString(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp=new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    public static String exceptionStackInfoToString(Throwable th){
        StringWriter sw = new StringWriter();
        th.printStackTrace( new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }
    /**
     转半角的函数(DBC case)<br/><br/>
     全角空格为12288，半角空格为32
     其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * @param input 任意字符串
     * @return 半角字符串
     *
     */
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                //全角空格为12288，半角空格为32
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                //其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }
    /**
     转全角的方法(SBC case)<br/><br/>
     全角空格为12288，半角空格为32
     其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
     * @param input 任意字符串
     * @return 半角字符串
     *
     */
    public static String toSBC(String input)
    {
        //半角转全角：
        char[] c=input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i]==32)
            {
                c[i]=(char)12288;
                continue;
            }
            if (c[i]<127)
                c[i]=(char)(c[i]+65248);
        }
        return new String(c);
    }

    /**
     * 对url的中文部分进行url编码
     * @param chineseStr
     * @return
     */
    public static String urlEncodeCn(String chineseStr) {
        if (isBlank(chineseStr))
            return "";
        char[]chineseChars=chineseStr.toCharArray();
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < chineseChars.length; i++) {
            String str=new String(new char[]{chineseChars[i]});
            if (isChinese(str)){
                //这个字符是中文，进行编码
                try {
                    builder.append(URLEncoder.encode(str,"UTF-8"));
                }catch (UnsupportedEncodingException e){
                    e.getMessage();
                }
            }else
                builder.append(chineseChars[i]);
        }
        return builder.toString();
    }
    public static String getPinyin(String text, String separator) {
//text 文本, separator 转换后添加的分隔符
        char[] chars = text.toCharArray();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        // 设置大小写
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 设置声调表示方法
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        // 设置字母u表示方法
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        String[] s;
        String rs = "";
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                // 判断是否为汉字字符
                if (String.valueOf(chars[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    s = PinyinHelper.toHanyuPinyinStringArray(chars[i], format);
                    if (s != null) {
                        sb.append(s[0]).append(separator);
                        continue;
                    }
                }
                sb.append(chars[i]);
                if ((i + 1 >= chars.length) || String.valueOf(chars[i + 1]).matches("[\\u4E00-\\u9FA5]+")) {
                    sb.append(separator);
                }
            }
            rs = sb.substring(0, sb.length());
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return rs;
    }
}
