package me.zhengjie.support.pdd;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.pop.request.PddOverseaCustomsClearanceSubmitRequest;
import com.pdd.pop.sdk.http.api.pop.response.PddOverseaCustomsClearanceSubmitResponse;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.domain.ClearInfo;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.SecureUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 拼多多工具类
 */
@Slf4j
public class PDDSupport {
    @Value("${pdd.url}")
    private String cloudAppUrl;
    @Value("${pdd.signKey}")
    private String key;

    public static Integer translationToDYStatus(String pddStatusCode) throws Exception{
        int statusCode;
        switch (pddStatusCode){
            case "1":
                statusCode=2;
                break;
            case "2":
                statusCode=3;
                break;
            case "3":
                statusCode=5;
                break;
            case "t2":
                //throw new BadRequestException("等待商家处理售后");//拼多多部分商家要求同意退款后才取消订单(目前不清楚有哪些商家，先注释掉看看情况)
                statusCode=16;
                break;
            case "t3":
                statusCode=17;
                break;
            case "t4":
                statusCode=21;
                break;
            default:
                throw new Exception("未知异常:"+pddStatusCode);
        }
        return statusCode;
    }

    public static void main(String[] args) {
        PDDSupport support=new PDDSupport();
        ClearCompanyInfo companyInfo=new ClearCompanyInfo();
        companyInfo.setCustomsCode("3302461510");
        support.clearDeclare("210819-176171314752240",companyInfo,"NINGBO_IMPORT_ORDER","<Message><Header><CreateTime>2021-12-18 14:59:45</CreateTime><OrgName>宁波富立物流有限公司</OrgName><CustomsCode>3302461510</CustomsCode></Header><Body><Order><PostFee>0</PostFee><BuyerAccount>null</BuyerAccount><GrossWeight>0.5376</GrossWeight><Promotions><Promotion><ProRemark>优惠合计</ProRemark><ProAmount>0</ProAmount></Promotion></Promotions><Amount>151</Amount><OrderNo>~AgAAAAEY4EYDf2IR/wARqo7XhKchmaGcHcZXVEH4aqMekjv4IbwxNu+cQnE6a8EZ~/BXc4jVosjMJ5fbJW8mTTqkun3eS8+7AOYC13+JMiO8gNrgfpLwzeYKhcPqxU3kaCLgIOF3BqeoByOOzbCcjHIMiKGTo3sI2TPKmdLcWWQsp~3~~</OrderNo><Goods><Detail><Amount>69.20</Amount><ProductId>P31051510212791616</ProductId><Unit>罐</Unit><GoodsName>ZYDP露得清维A醇抗皱修护新生面霜48g</GoodsName><Price>69.20</Price><Qty>1</Qty></Detail><Detail><Amount>69.20</Amount><ProductId>P31051510213377814</ProductId><Unit>瓶</Unit><GoodsName>ZYDPBIAFINE每日保湿修复身体乳400ml3岁以上适用</GoodsName><Price>69.20</Price><Qty>1</Qty></Detail></Goods><Operation>0</Operation><OrderFrom>1134</OrderFrom><NetWeight>0.4480</NetWeight><BuyerIdnum>#ku0q#AgAAAAEY4EYKf2IR/wD9d0bP7PAzPfMcZW8nHTgyHgj0szcPuRILqqRusJhKWbxt#3##</BuyerIdnum><BuyerIsPayer>1</BuyerIsPayer><DisAmount>0</DisAmount><TaxAmount>12.60</TaxAmount><ConsumptionDutyAmount>0</ConsumptionDutyAmount><Phone>$l1RWc0jpAKhs$AgAAAAEY4EYGf2IR/wB87MMCJHiZUO8XacfLkjIaWZQ=$3$$</Phone><TariffAmount>0</TariffAmount><AddedValueTaxAmount>0</AddedValueTaxAmount><BooksNo>阿署达村</BooksNo><BuyerName>~AgAAAAEY4EYLf2IR/wAipm6QG5LMsht10e1OMX1v7FA=~srAe36UP~3~~</BuyerName></Order><Pay><OrderSeqNo>~AgAAAAEY4EYEf2IR/wBnEUaDUHvZudmp/+p7h18Q6WGwP/zTWhK+JkgZZAomid7wmz1KUsfaGlReETCu0giRWQ==~XTHBujJVW8mTTqkun3eS8+7AOYC1Z8sKhwCq+DM5cld3HhZpHhZpCYqvS0p+e59XI92LD4OEK782begYihBj1N4xzljsEG6/amcVMRp4Iq5QKwcTpN4P~3~~</OrderSeqNo><Paytime>2021-12-18 14:59:56</Paytime><PaymentNo>~AgAAAAEY4EYEf2IR/wBnEUaDUHvZudmp/+p7h18Q6WGwP/zTWhK+JkgZZAomid7wmz1KUsfaGlReETCu0giRWQ==~XTHBujJVW8mTTqkun3eS8+7AOYC1Z8sKhwCq+DM5cld3HhZpHhZpCYqvS0p+e59XI92LD4OEK782begYihBj1N4xzljsEG6/amcVMRp4Iq5QKwcTpN4P~3~~</PaymentNo><Source>64</Source></Pay><Logistics><LogisticsNo>77128115836926</LogisticsNo><ConsigneeAddr>~AgAAAAEY4EYHf2IR/wGQMUF36MAJ6wnywMi7qn1wJMZxVM1xCBWyqyc4ThzpmbryP08BgnCd5CHoq5kWoR8YNwNeMeJsW5GzPBfumpzc9jiKKpHvzEbjDMl4blG6QIJZL5XDqCM1P3Hyih0/qA+Hbw1YwRbSfWYAdAHDMSBqPsE=~Ux1M1Uz2Cq0muI47X1JZKAJGmXni9+mV0jcZIumtE+9slGDFoz/x71Jfoy0Z37Iy9MpsA9AYfbr7iUDM7lxAxaXyBFfelcp5Mnan~3~~</ConsigneeAddr><City>其他区</City><Province>海南省</Province><Consignee>~AgAAAAEY4EYFf2IR/wA+PpokIXXQc+hXNH2Dh7P5N4Y=~srAe36UP~3~~</Consignee><LogisticsName>中通速递</LogisticsName><ConsigneeTel>$l1RWc0jpAKhs$AgAAAAEY4EYGf2IR/wB87MMCJHiZUO8XacfLkjIaWZQ=$3$$</ConsigneeTel><District>其他区</District></Logistics></Body></Message>");
    }

    /**
     * 报关
     * @param orderSn
     * @param clearCompanyInfo
     * @param declareXmlStr
     */
    public void clearDeclare(String orderSn, ClearCompanyInfo clearCompanyInfo,String msgType,String declareXmlStr){
        PddOverseaCustomsClearanceSubmitRequest request=new PddOverseaCustomsClearanceSubmitRequest();
        request.setBaseTransferCopCode(clearCompanyInfo.getCustomsCode());
        request.setClearanceMessageType(msgType);
        request.setCustomsAreaCode("3105");
        request.setOrderSn(orderSn);
        request.setOverseaBusinessType("bbc");
        request.setContent(declareXmlStr);
        PopClient client = new PopHttpClient("b55d01e2b32f49b19cb2923cc697aaac", "6758f8448a200f7c28cb50760b3921e7d055e0ed");
        PddOverseaCustomsClearanceSubmitResponse response;
        try {
            response=client.syncInvoke(request,"df3529a83e5a4e0da46a0ec47c86df93d608ceb3");
        }catch (Exception e){
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        if (response.getErrorResponse() != null) {
            throw new BadRequestException(response.getErrorResponse().getErrorCode() + ""+ response.getErrorResponse().getErrorMsg());
        }
        System.out.println();
    }

    public <T> PddCommonResponse request(Class<T> clazz,PddCommonRequest request) throws Exception{
        String resp=HttpRequest.post(cloudAppUrl+request.getApiPath()).contentType(ContentType.FORM_URLENCODED.getValue())
                .form("customersCode","1000").form("data",new JSONObject(request))
                .execute().body();
        log.info("多多云响应"+resp);
        resp = StringUtil.filterEmoji(resp);
        JSONObject respJs=new JSONObject(resp);

        PddCommonResponse<T>response=respJs.toBean(PddCommonResponse.class);
        if (!response.isSuccess()){
            if (StringUtils.contains(request.getApiPath(),"pdd-erp/order-declare")){
                String detailErr=(String)response.getData();
                if (JSONUtils.isObj(detailErr)){
                    JSONObject object = new JSONObject(detailErr);
                    throw new BadRequestException("拼多多官代报失败："+object.getStr("error_msg")+":"+object.getStr("sub_msg"));
                }
                errAnalyse(detailErr);
            }
            throw new BadRequestException(response.getMsg());
        }
        if (!clazz.equals(EmptyResponse.class)){
            if (response.getData() != null&&!JSONUtil.isNull(response.getData())) {
                if (JSONUtils.isObj(respJs.getJSONObject("data"), "data")) {
                    response.setData(respJs.getJSONObject("data").get("data",clazz));
                } else {
                    List<T> array = JSONArray.parseArray(respJs.getJSONObject("data").getStr("data"), clazz);
                    response.setDataArray(array);
                }
            }
        }
        return response;
    }

    private void errAnalyse(String errorStr) {
        String errMsg="拼多多官代报失败：";
        if (errorStr==null)
            throw new BadRequestException(errMsg+"报错信息为空");
        if (errorStr.indexOf("清关失败：")==0){
            errorStr=errorStr.replaceFirst("^清关失败：","");
            JSONObject errObj=new JSONObject(errorStr);
            String detailErr=errObj.getJSONObject("customsClearanceSubmitResponse").getStr("callCustomsApiResult");
            if (JSONUtils.isObj(detailErr)){
                throw new BadRequestException(errMsg+detailErr);
            }else if (JSONUtils.isXml(detailErr)){
                JSONObject deTailErrObj=JSONUtil.xmlToJson(detailErr);
                JSONObject header=deTailErrObj.getJSONObject("Message").getJSONObject("Header");
                if ("F".equals(header.getStr("Result")))
                    throw new BadRequestException(errMsg+header.getStr("ResultMsg"));
            }
        }
    }

    public static String translationStatus(String statusCode){
        switch (statusCode){
            case "1":
                statusCode="待发货";break;
            case "2":
                statusCode="已发货";break;
            case "3":
                statusCode="已签收";break;
            case "t2":
                statusCode="售后处理中";break;
            case "t3":
                statusCode="退款中";break;
            case "t4":
                statusCode="退款完成";break;
        }
        return statusCode;
    }

    public void addShopToken(ShopToken shopToken) {
        PddAddShopTokenRequest request=new PddAddShopTokenRequest();
        request.setShopId(shopToken.getPlatformShopId());
        request.setPlatform("PDD");
        request.setPullOrderAble(shopToken.getPullOrderAble());
        request.setShopName(shopToken.getShopName());
        request.setId(shopToken.getId()+"");
        try {
            PddCommonResponse<EmptyResponse>resp= request(EmptyResponse.class,request);
            if (!resp.isSuccess()){
                //添加到云服务的授权失败
                log.error("添加到拼多多云服务器的授权信息失败："+resp.getMsg());
                log.error("request:"+ com.alibaba.fastjson.JSONObject.toJSONString(request));
                log.error("response:"+ com.alibaba.fastjson.JSONObject.toJSONString(resp));
            }
        } catch (Exception e) {
            e.printStackTrace();
            //添加到云服务的授权失败
            log.error("添加到拼多多云服务器的授权信息失败："+e.getMessage());
            log.error("request:"+ com.alibaba.fastjson.JSONObject.toJSONString(request));
        }
    }
}
