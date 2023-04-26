package me.zhengjie.support.guomei;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.date.DatePattern;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenInBoundOrder;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenResponse;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.Md5Utils;
import me.zhengjie.utils.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GuomeiSupport {

    @Value("${guoMei.apiUrl}")
    private String apiUrl;

    @Value("${guoMei.key}")
    private String key;

    @Value("${guoMei.signSecret}")
    private String signSecret;

    @Value("${guoMei.vendor}")
    private String vendor;

     public static void main(String[] args) {

        GuomeiSupport guomeiSupport = new GuomeiSupport();
        guomeiSupport.apiUrl = "http://silk.road.test.gome.com.cn";
        guomeiSupport.key = "303d8cc1dc074d7ab2701d2ca65022a2";
        guomeiSupport.signSecret = "fd42b9a2725a412194087c44d74ea890";
        guomeiSupport.vendor = "VC004";
        String jsonStr = "{\n" +
                " \"ebpName\": \"国美真快乐电子商务有限公司\",\n" +
                " \"buyerTelephone\": \"13659630006\",\n" +
                " \"buyerIdType\": 1,\n" +
                " \"city\": \"北京市\",\n" +
                " \"freight\": 0,\n" +
                " \"taxTotal\": 2721,\n" +
                " \"discount\": 3000,\n" +
                " \"goodsValue\": 29900,\n" +
                " \"consigneeTelephone\": \"13659630006\",\n" +
                " \"payTransactionId\": \"f259cdcdb3bb4d19aba0b6885954805d\",\n" +
                " \"buyerIdNumber\": \"110101199003072930\",\n" +
                " \"province\": \"北京市\",\n" +
                " \"acturalPaid\": 29621,\n" +
                " \"ebpCode\": \"3114963884\",\n" +
                " \"currency\": \"142\",\n" +
                " \"shopId\": \"c97ec20f53e24768af918be94e2c7c18\",\n" +
                " \"payName\": \"银盈通支付有限公司\",\n" +
                " \"consigneeAddress\": \"北京市朝阳区 XX 小区 1 号楼 3 单元 502 室\",\n" +
                " \"traceId\": \"7b34de2309f94d369cc5851e88d29369\",\n" +
                " \"buyerRegNo\": \"110101199003072930\",\n" +
                " \"customsClearType\": 1,\n" +
                " \"orderNo\": \"8855225522542214\",\n" +
                " \"consignee\": \"赵六\",\n" +
                " \"buyerName\": \"赵六\",\n" +
                " \"batchNumbers\": \"\",\n" +
                " \"consigneeDistrict\": \"110105012\",\n" +
                " \"orderDetailList\": [\n" +
                " {\n" +
                " \"totalPrice\": 29900,\n" +
                " \"itemNo\": \"1533a983533e422ca8d5dbcec194fd37\",\n" +
                " \"gnum\": 1,\n" +
                " \"barCode\": \"3434353754355\",\n" +
                " \"itemName\": \"深海鱼油\",\n" +
                " \"unit\": \"瓶\",\n" +
                " \"assemCountry\": \"AU\",\n" +
                " \"hsCode\": \"1504100090\",\n" +
                " \"firstMeasureQty\": 0.1,\n" +
                " \"gModel\": \"100 粒装 胶囊 成人\",\n" +
                " \"price\": 29900,\n" +
                " \"qty\": 1,\n" +
                " \"currency\": \"142\",\n" +
                " \"itemDescribe\": \"\",\n" +
                " \"firstMeasureUnit\": \"千克\"\n" +
                " }\n" +
                " ],\n" +
                " \"district\": \"\",\n" +
                " \"payCode\": \"1102961253\",\n" +
                " \"orderDate\": \"2022-03-25 15:28:33\",\n" +
                " \"customsCode\": \"5165\"\n" +
                "}";
         TreeMap<String,Object> request = new JSONObject(jsonStr).toBean(TreeMap.class);
         long timestamp=System.currentTimeMillis();
         System.out.println(timestamp);
         request.put("sign",guomeiSupport.getSign(jsonStr,guomeiSupport.signSecret,timestamp));
         System.out.println(guomeiSupport.getSign(jsonStr,guomeiSupport.signSecret,timestamp));
         request.put("vendor",guomeiSupport.vendor);
         request.put("timestamp",timestamp+"");
         String bendiUrl = "t43015f571.xicp.fun/api/guomei/pushOrder";
         String respStr = HttpRequest.post(bendiUrl)
                 .contentType(ContentType.JSON.getValue())
                 .body(new JSONObject(request).toString())
                 .execute()
                 .body();
         System.out.println(respStr);
//         GuoMeiCommonParamResponse response = new JSONObject(respStr).toBean(GuoMeiCommonParamResponse.class);
//         System.out.println("对方加密的结果：" + response.getCode() + "," +response.getMessage());
//         System.out.println("我方加密的结果：" + request.get("sign"));

//         String jsonStr = "{\n" +
//                 " \"traceId\": \"7c9f38138de34b2e9e322e4f8b75be4a\",\n" +
//                 " \"orderNo\": \"55022185205685214\",\n" +
//                 " \"statusReason\": \"具体原因\",\n" +
//                 " \"vendor\": \"VC004\",\n" +
//                 " \"operateRemark\": \"备注\",\n" +
//                 " \"operateTime\": \"2022-03-26 15:28:33\",\n" +
//                 " \"operator\": \"张三\",\n" +
//                 " \"status\": 0\n" +
//                 "}";
//         TreeMap<String,Object> request = new JSONObject(jsonStr).toBean(TreeMap.class);
//         long timestamp=System.currentTimeMillis();
//         request.put("sign",guomeiSupport.getSign(jsonStr,guomeiSupport.signSecret,timestamp));
//         request.put("vendor",guomeiSupport.vendor);
//         request.put("timestamp",timestamp+"");
//         String respStr = HttpRequest.post(guomeiSupport.apiUrl+"/border/callback/orderConfirm")
//                 .contentType(ContentType.JSON.getValue())
//                 .body(new JSONObject(request).toString())
//                 .execute()
//                 .body();
//         System.out.println(respStr);
//         GuoMeiCommonParamResponse response = new JSONObject(respStr).toBean(GuoMeiCommonParamResponse.class);
//         System.out.println("对方加密的结果：" + response.getCode() + "," +response.getMessage());
//         System.out.println("我方加密的结果：" + request.get("sign"));
    }



    //国美统一消息请求
    public GuoMeiCommonParamResponse request(String json,String reqUrl){
        String url = apiUrl + reqUrl;
        TreeMap<String, Object> reqMap = new JSONObject(json).toBean(TreeMap.class);
//        reqMap.put("key",key);
        long timestamp=System.currentTimeMillis();
        reqMap.put("timestamp",String.valueOf(timestamp));
        reqMap.put("sign",getSign(json,signSecret,timestamp));
        System.out.println("要发送给国美的请求文 :" + reqMap);
        try {
            String respStr = HttpRequest.post(url)
                    .contentType(ContentType.JSON.getValue())
                    .body(new JSONObject(reqMap).toString())
                    .execute()
                    .body();
            System.out.println(respStr);
            GuoMeiCommonParamResponse response = new JSONObject(respStr).toBean(GuoMeiCommonParamResponse.class);
            return response;
        } catch (Exception e) {
            throw new BadRequestException("国美消息请求失败:"+e.getMessage());
        }
    }


    //生成签名
    public String getSign(String json,String signSecret,long timestamp){
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.get("timestamp")==null)
            jsonObject.putOnce("timestamp",timestamp+"");
        StringBuilder builder = new StringBuilder(vendor+"|"+key+"|"+timestamp+"|");
        appendSignStr(builder, jsonObject,false);
        System.out.println("我方拼接的加密字符串：" + builder);
        try {
            return sign(signSecret,builder.toString());
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    private String sign(String signSecret,String stringToBeSigned) throws Exception{
        byte[] byteArrayToBeSigned =
                stringToBeSigned.getBytes(StandardCharsets.UTF_8);
        byte[] signSecretByteArray =
                signSecret.getBytes(StandardCharsets.UTF_8);
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(signSecretByteArray,
                "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] signByteArray = hmacSHA256.doFinal(byteArrayToBeSigned);
        String sign = Base64.encodeBase64String(signByteArray);
        System.out.println(sign);
        return sign;
    }

    //拿到国美status   暂时不用此方法
//    public GuoMeiOrderConfimResponse getGuoMeiStatus(String json, String reqUrl) {
//        String url = apiUrl + reqUrl;
//        TreeMap<String, Object> reqMap = new JSONObject(json).toBean(TreeMap.class);
//        long timestamp=System.currentTimeMillis();
//        reqMap.put("timestamp",String.valueOf(timestamp));
//        reqMap.put("sign",getSign(json,signSecret,timestamp));
//        System.out.println(reqMap);
//
//        try {
//            String respStr = HttpRequest.post(url)
//                    .contentType(ContentType.JSON.getValue())
//                    .body(new JSONObject(reqMap).toString())
//                    .execute()
//                    .body();
//            System.out.println(respStr);
//            GuoMeiOrderConfimResponse response = new JSONObject(respStr).toBean(GuoMeiOrderConfimResponse.class);
//            return response;
//        } catch (Exception e) {
//            throw new BadRequestException("拿取国美status请求失败:"+e.getMessage());
//        }
//    }

    /**
     *
     * @param builder
     * @param object
     * @param isArray 处理array中的json对象时不添加{}
     */
    public void appendSignStr(StringBuilder builder, Object object,boolean isArray) {
        //这个object我只处理JSONObj和JSONArray，其他的一律不处理
        if (object instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) object;
            //利用HashSet的默认按首字母排序的特性来完成签名之前的拼接
            Set<String> keySet = jsonObj.keySet();
            if (!(keySet instanceof TreeSet)) {
                //如果不是HashSet实现类，需要先把这个实现类转化成HashSet
                keySet = new TreeSet<>(jsonObj.keySet());
            }
            if (!CollectionUtils.isEmpty(keySet)) {
                //如果不是首次拼接则需要加上{}
                builder.append("{");
                int i = 0;
                for (String key : keySet) {
                    Object obj = jsonObj.getObj(key);
                    builder.append("\"").append(key).append("\"").append(":");
                    if (obj instanceof JSONObject || obj instanceof JSONArray) {
                        //如果迭代到的元素是数组或者是对象则进行递归
                        appendSignStr(builder, obj,false);
                    } else {
                        if (obj instanceof String)
                            builder.append("\"").append(obj).append("\"");
                        else
                            builder.append(obj);
                    }
                    if (i < keySet.size()-1) {
                        builder.append(",");
                    }
                    i++;
                }
                builder.append("}");
            }
        } else if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
            if (!CollectionUtils.isEmpty(array)) {
                //如果不是首次拼接则需要加上[]
                    builder.append("[");
                for (int i = 0; i < array.size(); i++) {
                    Object obj = array.get(i);
                    if (obj instanceof JSONObject || obj instanceof JSONArray) {
                        //如果迭代到的元素是数组或者是对象则进行递归
                        appendSignStr(builder, obj,true);
                    } else {
                        if (obj instanceof String)
                            builder.append("\"").append(obj).append("\"");
                        else
                            builder.append(obj);
                    }
                    if (i<array.size()-1){
                        builder.append(",");
                    }
                }
                builder.append("]");

            }
        }
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSignSecret() {
        return signSecret;
    }

    public void setSignSecret(String signSecret) {
        this.signSecret = signSecret;
    }
}
