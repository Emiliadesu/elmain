package me.zhengjie.support.ruoYuChen;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenInBoundOrder;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenOutBoundOrder;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.support.ruoYuChen.request.RuoYuChenRequest;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenFileUpload;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenResponse;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.Md5Utils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class RuoYuChenSupport {
    @Value("${ruoYuChen.appId}")
    private String appId;
    @Value("${ruoYuChen.appSecret}")
    private String appSecret;
    @Value("${ruoYuChen.url}")
    private String url;
    @Value("${ruoYuChen.erpCode}")
    private String erpCode;

    public String getAppId() {
        return appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public static void main(String[] args) {
        RuoYuChenSupport support = new RuoYuChenSupport();
        support.erpCode = "ERP001";
        support.url = "http://120.26.140.254:8080/stock/checkSign/test";
        support.appId = "MTY0ODY5MjM4MzQxNA==";
        support.appSecret = "ZjYxMjg1ZWE1N2RjOGMzNDlkNGRkNDk5MzFhMjA1MTA=";
        String body="{\"itemCode\":\"SLS10221HTCG003\",\"itemName\":\"Selsun深层清洁洗发水紫瓶375毫升/瓶\",\"englishName\":\"Selsun Blue Deep Cleansing Shampoo 375ml\",\"barCode\":\"9351791001389\",\"skuProperty\":\"350915\",\"stockUnit\":\"瓶\",\"length\":\"7.7\",\"width\":\"4.5\",\"height\":\"22.4\",\"volume\":\"776.16\",\"grossWeight\":\"0.42\",\"category1Name\":\"美妆\",\"category2Name\":\"洗护\",\"category3Name\":\"洗发水\",\"categoryName\":\"洗发水\",\"itemType\":\"ZC\",\"brandCode\":\"215\",\"brandName\":\"SELSUN\",\"isShelfLifeMgmt\":\"Y\",\"shelfLife\":\"26280\",\"rejectLifecycle\":\"790\",\"lockupLifecycle\":\"40\",\"adventLifecycle\":\"130\",\"isBatchMgmt\":\"Y\",\"pcs\":\"24\",\"erpId\":\"19a6b7bf-8768-4d04-9799-94ebf9f1fea1\",\"id\":\"40c6057e-9035-402b-8847-b115be9525f2\",\"updateTime\":\"2022-04-12 18:12:43\",\"warehouseCode\":\"3302461510\",\"ownerCode\":\"rycTest\"}";
        Map<String,Object>map=new HashMap<>();
        map.put("name","AA");
        map.put("age","15");
        map.put("class","obj");
        map.put("childs",new String[]{"def","2sa"});
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("secret", support.appSecret);
        bodyMap.put("data", new JSONObject(body));
        String sign=support.getSign(new JSONObject(body));
        String respStr = HttpRequest.post(support.url)
                .contentType(ContentType.JSON.getValue())
                .body(new JSONObject(bodyMap).toString())
                .execute()
                .body();
        RuoYuChenResponse response = new JSONObject(respStr).toBean(RuoYuChenResponse.class);
        JSONObject dataObj = new JSONObject(response.getData());
        System.out.println("对方拼接的加密字符串：" + dataObj.getStr("strSign"));
        System.out.println("我方加密的结果：" + sign);
        System.out.println("对方加密的结果：" + dataObj.getStr("sign"));
    }

    public void signTest(Object obj){
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("secret", appSecret);
        bodyMap.put("data", obj);
        String sign=getSign(obj);
        String respStr = HttpRequest.post("http://47.99.106.238:8080/stock/checkSign/test")
                .contentType(ContentType.JSON.getValue())
                .body(new JSONObject(bodyMap).toString())
                .execute()
                .body();
        RuoYuChenResponse response = new JSONObject(respStr).toBean(RuoYuChenResponse.class);
        JSONObject dataObj = new JSONObject(response.getData());
        System.out.println("对方拼接的加密字符串：" + dataObj.getStr("strSign"));
        System.out.println("我方加密的结果：" + sign);
        System.out.println("对方加密的结果：" + dataObj.getStr("sign"));
    }

    public RuoYuChenResponse request(CommonApiParam apiParam) {
        Map<String, String> reqHeaderMap = new HashMap<>();
        reqHeaderMap.put("appId", this.appId);
        reqHeaderMap.put("signTimeStamp", DateUtils.format(new Date(), "yyyyMMddHHmmss"));
        RuoYuChenRequest request = new RuoYuChenRequest();
        request.setMethod(apiParam.getMethod());
        request.setErpCode(this.erpCode);
        request.setData(apiParam);
        reqHeaderMap.put("sign", getSign(request));
        signTest(request);
        String respStr = HttpRequest.post(this.url)
                .headerMap(reqHeaderMap, false)
                .contentType(ContentType.JSON.getValue())
                .body(new JSONObject(request).toString())
                .execute()
                .body();
        return new JSONObject(respStr).toBean(RuoYuChenResponse.class);
    }

    public String getSign(Object data, String appSecret) {
        JSONObject jsonObject = new JSONObject(data);
        StringBuilder builder = new StringBuilder();
        appendSignStr(builder, jsonObject,false);
        builder.append("&").append(StringUtils.isEmpty(appSecret) ? this.appSecret : appSecret);
        System.out.println("我方拼接的加密字符串：" + builder);
        return Md5Utils.md5Hex(builder.toString());
    }

    public String getSign(Object data) {
        return getSign(data, null);
    }

    public boolean verify(Object data, String sign, String appSecret) {
        String mySign=getSign(data, appSecret);
        System.out.println("我方签名："+mySign);
        return StringUtils.equals(mySign, sign);
    }

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
                boolean flag = false;
                if (builder.length() > 0&&!isArray) {
                    builder.append("{");
                    flag = true;
                }
                int i = 0;
                for (String key : keySet) {
                    Object obj = jsonObj.getObj(key);
                    if (obj instanceof JSONObject || obj instanceof JSONArray) {
                        //如果迭代到的元素是数组或者是对象则进行递归
                        builder.append(key).append("=");
                        appendSignStr(builder, obj,false);
                    } else if (obj ==null || obj instanceof JSONNull || StringUtil.equalsIgnoreCase("null",obj+"")||StringUtil.isBlank(obj+"")){
                        i++;
                        continue;
                    }else
                        builder.append(key).append("=").append(obj);
                    if (i < keySet.size()-1) {
                        builder.append("&");
                    }
                    i++;
                }
                if (flag&&!isArray)
                    builder.append("}");
            }
        } else if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
                //如果不是首次拼接则需要加上[]
            if (builder.length() > 0) {
                    builder.append("[");
            }
            if (!CollectionUtils.isEmpty(array)) {
                for (int i = 0; i < array.size(); i++) {
                    Object obj = array.get(i);
                    if (obj instanceof JSONObject || obj instanceof JSONArray) {
                        //如果迭代到的元素是数组或者是对象则进行递归
                        appendSignStr(builder, obj,true);
                    } else {
                        builder.append(obj);
                    }
                }
            }
            builder.append("]");
        }
    }

    public RuoYuChenFileUpload uploadFile(MultipartFile file) {
        try {
            Map<String,String>mapHeader=new HashMap<>();
            mapHeader.put("appId", this.appId);
            mapHeader.put("signTimeStamp", DateUtils.format(new Date(), "yyyyMMddHHmmss"));
            //mapHeader.put("sign", getSign(outBoundOrder));
            Resource resource=new InputStreamResource(file.getInputStream(),file.getOriginalFilename());
            String resp=HttpRequest.post("http://120.26.140.254:8080/sys/file/upload")
                    .headerMap(mapHeader,false)
                    .contentType(ContentType.MULTIPART.getValue())
                    .form("name",resource)
                    .execute()
                    .body();
            RuoYuChenResponse response=new JSONObject(resp).toBean(RuoYuChenResponse.class);
            if (!response.isSuccess())
                throw new Exception(response.getMsg());
            return new JSONObject(response.getData()).toBean(RuoYuChenFileUpload.class);
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
}
