package me.zhengjie.support.sf;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.*;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.BusinessLog;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SFGJSupport {

    @Value("${SF.partner-id}")
    private String partnerID;

    @Value("${SF.cust-id}")
    private String custId;

    @Value("${SF.url}")
    private String url;

    @Value("${SF.aes-key}")
    private String aesKey;

    @Value("${SF.hmac-key}")
    private String HMACKey;

    @Autowired
    private BusinessLogService businessLogService;

    @Autowired
    private ShopInfoService shopInfoService;


    public void getMail(CrossBorderOrder order){
        long start = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<Request>");
        {
            AddHeader(builder);
            builder.append("<Body>");
            {
                builder.append("<OrderList>");
                {
                    builder.append("<Order>");
                    {
                        builder.append("<OrderNo>").append(order.getCrossBorderNo()).append("</OrderNo>");
                        builder.append("<ExpressServiceCode>").append("208").append("</ExpressServiceCode>");
                        builder.append("<IsGenMailNo>").append(1).append("</IsGenMailNo>");
                        builder.append("<ParcelQuantity>").append(1).append("</ParcelQuantity>");
                        builder.append("<MailNo></MailNo>");
                        BigDecimal totalWeight = StringUtils.isNotEmpty(order.getGrossWeight()) ? new BigDecimal(order.getGrossWeight()) : BigDecimal.ZERO;
                        builder.append("<TotalWeight>").append(String.format("%.3f", totalWeight)).append("</TotalWeight>");
                        builder.append("<TotalVolume></TotalVolume>");
                        builder.append("<PaymentMethod>").append(1).append("</PaymentMethod>");
                        builder.append("<PaymentType>").append(1).append("</PaymentType>");
                        builder.append("<PaymentAccountNo>").append(custId).append("</PaymentAccountNo>");
                        builder.append("<IsDoCall>").append(0).append("</IsDoCall>");
                        builder.append("<NeedReturnTrackingNo>").append(0).append("</NeedReturnTrackingNo>");
                        builder.append("<PickupMethod>").append(1).append("</PickupMethod>");
                        builder.append("<DeliveryMethod>").append(1).append("</DeliveryMethod>");
                        builder.append("<AdditionalDataList>");
                        {
                            builder.append("<AdditionalData>");
                            {
                                builder.append("<Key>").append("Freight").append("</Key>");
                                builder.append("<Value>").append(0.00).append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                            builder.append("<AdditionalData>");
                            {
                                builder.append("<Key>").append("insuredFee").append("</Key>");
                                builder.append("<Value>").append(0).append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                            builder.append("<AdditionalData>");
                            {
                                builder.append("<Key>").append("e-commerceCode").append("</Key>");
                                builder.append("<Value>").append(order.getEbpCode()).append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                            builder.append("<AdditionalData>");
                            {
                                builder.append("<Key>").append("cebFlag").append("</Key>");
                                builder.append("<Value>").append("02").append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                            builder.append("<AdditionalData>");
                            {
                                builder.append("<Key>").append("HarmonizedCode").append("</Key>");
                                builder.append("<Value>").append(3105).append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                        }
                        builder.append("</AdditionalDataList>");
                        builder.append("<ShipFromAddress>");
                        {
                            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
                            if (shopInfoDto != null) {
                                builder.append("<Contact>").append(shopInfoDto.getName()).append("</Contact>");
                                builder.append("<Telephone>").append(StringUtils.isBlank(shopInfoDto.getContactPhone())?"0574-86873070":shopInfoDto.getContactPhone()).append("</Telephone>");
                            }else {
                                builder.append("<Contact>").append("富立物流").append("</Contact>");
                                builder.append("<Telephone>").append("0574-86873070").append("</Telephone>");
                            }

                            builder.append("<CountryCode>").append("CN").append("</CountryCode>");
                            builder.append("<StateOrProvince>").append("浙江省").append("</StateOrProvince>");
                            builder.append("<City>").append("宁波市").append("</City>");
                            builder.append("<County>").append("北仑区").append("</County>");
                            builder.append("<AddressLine1>").append("保税东区").append("</AddressLine1>");
                            builder.append("<AddressLine2>").append("兴业四路二号").append("</AddressLine2>");
                            builder.append("<ShipperCode>").append("CN").append("</ShipperCode>");
                            builder.append("<PostalCode>").append("315800").append("</PostalCode>");
                        }
                        builder.append("</ShipFromAddress>");
                        builder.append("<ShipToAddress>");
                        {
                            builder.append("<Contact>").append(order.getConsigneeName()).append("</Contact>");
                            builder.append("<Telephone>").append(order.getConsigneeTel()).append("</Telephone>");
                            builder.append("<CountryCode>").append("CN").append("</CountryCode>");
                            builder.append("<StateOrProvince>").append(order.getProvince()).append("</StateOrProvince>");
                            builder.append("<City>").append(order.getCity()).append("</City>");
                            builder.append("<County>").append(order.getDistrict()).append("</County>");
                            builder.append("<AddressLine1>").append(order.getConsigneeAddr()).append("</AddressLine1>");
                            builder.append("<AddressLine2>").append(" ").append("</AddressLine2>");
                            builder.append("<DeliveryCode>").append("CN").append("</DeliveryCode>");
                            builder.append("<PostalCode>").append("000000").append("</PostalCode>");
                        }
                        builder.append("</ShipToAddress>");
                        builder.append("<Parcels/>");
                        builder.append("<CustomsItem>");{
                        builder.append("<CustomsBatchs>").append(DateUtils.format(new Date(),"yyyy-MM-dd")).append("</CustomsBatchs>");
                        builder.append("<InProcessWaybillNo></InProcessWaybillNo>");
                        builder.append("<DeclaredValueCurrencyCode>").append("CNY").append("</DeclaredValueCurrencyCode>");
                        builder.append("<DeclaredValue>").append(order.getPayment()).append("</DeclaredValue>");
                        builder.append("<AdditionalDataList>");{
                            builder.append("<AdditionalData>");{
                                builder.append("<Key>").append("HarmonizedCode").append("</Key>");
                                builder.append("<Value>").append(3105).append("</Value>");
                            }
                            builder.append("</AdditionalData>");
                        }
                        builder.append("</AdditionalDataList>");
                    }
                        builder.append("</CustomsItem>");
                    }
                    builder.append("</Order>");
                }
                builder.append("</OrderList>");
            }
            builder.append("</Body>");
        }
        builder.append("</Request>");


        System.out.println(builder);
        String[] encs = getEncrypts(builder);
        String serviceCode = "OrderService";
        StringBuilder baseParam = getBaseParam(encs[0], encs[1], serviceCode);
        System.out.println(baseParam);


        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-type", "application/xml;charset=\"UTF-8\"");
        String res = HttpRequest.post(url).body(baseParam.toString()).headerMap(headerMap, false)
                .execute().body();

        // 保存日志
        businessLogService.saveLog(BusTypeEnum.MAIL_GSF, url, order.getCrossBorderNo(),  baseParam.toString(), res, (System.currentTimeMillis() - start));

        JSONObject respJs = XML.toJSONObject(res);
        JSONObject response = respJs.getJSONObject("Response");
        String head = response.getStr("Head");
        if (StringUtils.equalsIgnoreCase("err", head)) {
            throw new BadRequestException(response.getStr("Error"));
        }
        JSONObject orderResp = response.getJSONObject("Body").getJSONObject("OrderResponse");
        if (StringUtils.isBlank(orderResp.getStr("MailNo"))) {
            throw new BadRequestException("未获取到单号");
        }
        order.setLogisticsNo(orderResp.getStr("MailNo"));
        order.setAddMark(orderResp.getStr("DestCode"));
    }

    /**
     * 路由查询
     */
    public void routeQuery(String mailNo) {
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<Request>");
        {
            AddHeader(builder);
            builder.append("<Body>");
            {
                builder.append("<TrackingRequest>");
                {
                    builder.append("<TrackingType>1</TrackingType>");
                    builder.append("<MethodType>1</MethodType>");
                    builder.append("<TrackingNumber>").append(mailNo).append("</TrackingNumber>");
                }
                builder.append("</TrackingRequest>");
            }
            builder.append("</Body>");
        }
        builder.append("</Request>");
        String[] encs = getEncrypts(builder);
        String serviceCode = "RouteQuery";
        StringBuilder baseParam = getBaseParam(encs[0], encs[1], serviceCode);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-type", "application/xml;charset=\"UTF-8\"");
        String res = HttpRequest.post(url).body(baseParam.toString()).headerMap(headerMap, false)
                .execute().body();

        JSONObject respJs = XML.toJSONObject(res);
        cn.hutool.json.JSONObject response = respJs.getJSONObject("Response");
        String head = response.getStr("Head");
        if (StringUtils.equalsIgnoreCase("err", head)) {
            throw new BadRequestException(response.getStr("Error"));
        }
        JSONObject orderResp = response.getJSONObject("Body").getJSONObject("TrackingResponse");
        System.out.println(res);
    }

    /**
     * 得到业务参数加密字符串和请求签名
     *
     * @param builder
     * @return
     */
    private String[] getEncrypts(StringBuilder builder) {
        AES256CipherExternal aes256 = new AES256CipherExternal(aesKey);
        String msgData = "";
        String dataDigest = "";
        try {
            msgData = aes256.AES_Encode(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("顺丰国际获取面单加密报文失败");
        }
        HmacSha512Coder hmac = new HmacSha512Coder();
        hmac.setKeySeeds(HMACKey);
        try {
            dataDigest = hmac.generateHMAC(msgData);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("顺丰国际获取面单签名失败");
        }
        return new String[]{msgData, dataDigest};
    }

    /**
     * 请求头组装
     *
     * @param builder
     */

    private void AddHeader(StringBuilder builder) {
        builder.append("<Header>");
        {
            builder.append("<PartnerID>").append(partnerID).append("</PartnerID>");
            builder.append("<PartnerToken>").append("ISP").append("</PartnerToken>");
            builder.append("<VersionID>").append("V1.0").append("</VersionID>");
            builder.append("<DocumentType>").append("o2OOrderService").append("</DocumentType>");
            builder.append("<SenderID>").append("ISP").append("</SenderID>");
            builder.append("<ReceiverID>").append("SFC").append("</ReceiverID>");
            builder.append("<Timestamp>").append(System.currentTimeMillis() / 1000).append("</Timestamp>");
            builder.append("<RequestID>").append(DateUtils.format(new Date(), "yyyyMMddHHmmssms")).append("</RequestID>");
            builder.append("<Language>").append("zh-CN").append("</Language>");
            builder.append("<TimeZone>").append("+08:00").append("</TimeZone>");
        }
        builder.append("</Header>");
    }

    /**
     * 组装基本公共参数
     *
     * @param msgData
     * @param dataDigest
     * @param serviceCode
     * @return
     */
    private StringBuilder getBaseParam(String msgData, String dataDigest, String serviceCode) {
        StringBuilder baseParam = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        baseParam.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        {
            baseParam.append("<soap:Body>");
            {
                baseParam.append("<tns:sfexpressService xmlns:tns=\"http://service.expressservice.integration.sf.com/\">");
                {
                    baseParam.append("<MsgData>").append(msgData).append("</MsgData>");
                    baseParam.append("<DataDigest>").append(dataDigest).append("</DataDigest>");
                    baseParam.append("<PartnerID>").append(partnerID).append("</PartnerID>");
                    baseParam.append("<ServiceCode>").append(serviceCode).append("</ServiceCode>");
                }
                baseParam.append("</tns:sfexpressService>");
            }
            baseParam.append("</soap:Body>");
        }
        baseParam.append("</soap:Envelope>");
        return baseParam;
    }





//    /**
//     * 获取运单
//     * @param order
//     */
//    public void getMail(CrossBorderOrder order) throws Exception {
//        JSONObject msg = new JSONObject();
//        msg.putOnce("language", "zh-CN");
//        msg.putOnce("orderId", order.getCrossBorderNo());
//        JSONObject customsInfo = new JSONObject();// 报关信息
//        customsInfo.putOnce("declaredValue", order.getPayment());
//        customsInfo.putOnce("declaredValueCurrency", "CNY");
//        msg.putOnce("customsInfo", customsInfo);
//        JSONArray cargoDetails = new JSONArray();// 商品明细
//        List<CrossBorderOrderDetails> itemList = order.getItemList();
//        for (CrossBorderOrderDetails details : itemList) {
//            JSONObject cargoDetail = new JSONObject();
//            cargoDetail.putOnce("name", details.getGoodsName());
//            cargoDetail.putOnce("count", details.getQty());
//            cargoDetail.putOnce("unit", details.getUnit());
//            cargoDetail.putOnce("weight", details.getGrossWeight());
//            cargoDetail.putOnce("amount", details.getDutiableValue());
//            cargoDetail.putOnce("currency", "CNY");
//            cargoDetail.putOnce("sourceArea", details.getMakeCountry());
//            cargoDetails.add(cargoDetail);
//        }
//        msg.putOnce("cargoDetails", cargoDetails);
//        JSONArray contactInfoList = new JSONArray();// 收寄方
//        JSONObject contactInfoJi = new JSONObject();
//        contactInfoJi.putOnce("contactType", 1);
//        contactInfoJi.putOnce("company", "宁波富立物流有限公司");
//        contactInfoJi.putOnce("contact", "罗斌");
//        contactInfoJi.putOnce("mobile", "18888888888");
//        contactInfoJi.putOnce("tel", "0666169082");
//        contactInfoJi.putOnce("zoneCode", "315800");
//        contactInfoJi.putOnce("province", "浙江省");
//        contactInfoJi.putOnce("city", "宁波市");
//        contactInfoJi.putOnce("county", "北仑区");
//        contactInfoJi.putOnce("address", "浙江省宁波市北仑区港东大道29号富立物流");
//        contactInfoList.add(contactInfoJi);
//        JSONObject contactInfoShou = new JSONObject();
//        contactInfoShou.putOnce("contactType", 2);
//        contactInfoShou.putOnce("contact", order.getConsigneeName());
//        contactInfoShou.putOnce("mobile", order.getConsigneeTel());
//        contactInfoShou.putOnce("tel", order.getConsigneeTel());
//        contactInfoShou.putOnce("zoneCode", "CN");
//        contactInfoShou.putOnce("province", order.getProvince());
//        contactInfoShou.putOnce("city", order.getCity());
//        contactInfoShou.putOnce("county", order.getDistrict());
//        contactInfoShou.putOnce("address", order.getConsigneeAddr());
//        contactInfoList.add(contactInfoShou);
//        msg.putOnce("contactInfoList", contactInfoList);
//        msg.putOnce("monthlyCard", custId);
//        msg.putOnce("expressTypeId", "208");
//        msg.putOnce("payMethod", "1");
//
//        String res = request(msg.toString(), "EXP_RECE_CREATE_ORDER");
//        System.out.println(res);
//    }


    /**
     * 获取运单
     */
    public JSONObject queryRoute(String mailNo, String checkPhoneNo) throws Exception {
        JSONObject msg = new JSONObject();
        msg.putOnce("language", "zh-CN");
        msg.putOnce("trackingType", "1");
        JSONArray trackingNumber = new JSONArray();
        trackingNumber.put(mailNo);
        msg.putOnce("trackingNumber", trackingNumber);
        msg.putOnce("methodType", "1");
        msg.putOnce("checkPhoneNo", checkPhoneNo);

        String res = request(msg.toString(), "EXP_RECE_SEARCH_ROUTES");
        JSONObject jsonObject = JSONUtil.parseObj(res);
        return jsonObject;
    }


    /**
     * 发起请求
     * @param msgData
     */
    public String request(String msgData, String serviceCode) throws Exception {
        long timestamp = System.currentTimeMillis();
        String sign = sign(msgData, timestamp);
        Map<String, Object> param = new HashMap<>();
        param.put("partnerID", "FLWL_iginC");
        param.put("requestID", UUID.fastUUID().toString());
        param.put("serviceCode", serviceCode);
        param.put("timestamp", timestamp);
        param.put("msgDigest", sign);
        param.put("msgData", msgData);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        String res = HttpRequest.post("https://sfapi.sf-express.com/std/service").form(param).headerMap(headerMap, false)
                .execute().body();
        System.out.println(res);
        return res;
    }

    private String checkWord = "olc6XbIn4CclQyxcNZTqSrrLacPJ1ZQ3";

    /**
     * 签名
     * @param msgData
     * @return
     */
    private String sign(String msgData, long timestamp) throws Exception {

        //将业务报文+时间戳+校验码组合成需加密的字符串(注意顺序)
        String toVerifyText = msgData+timestamp+checkWord;

        //因业务报文中可能包含加号、空格等特殊字符，需要urlEnCode处理
        toVerifyText = URLEncoder.encode(toVerifyText,"UTF-8");

        //进行Md5加密
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(toVerifyText.getBytes("UTF-8"));
        byte[] md = md5.digest();

        //通过BASE64生成数字签名
        String msgDigest = new String(new BASE64Encoder().encode(md));
        return msgDigest;
    }



    public String getPartnerID() {
        return partnerID;
    }

    public void setPartnerID(String partnerID) {
        this.partnerID = partnerID;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getHMACKey() {
        return HMACKey;
    }

    public void setHMACKey(String HMACKey) {
        this.HMACKey = HMACKey;
    }
}
