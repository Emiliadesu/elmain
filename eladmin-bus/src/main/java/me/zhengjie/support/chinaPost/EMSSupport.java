package me.zhengjie.support.chinaPost;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
//import cn.hutool.json.JSON;
import cn.hutool.json.XML;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class EMSSupport {

    private ChinaPostCommonParam commonParam;

    private String logisticsInterface;

    private String dataDigest;

    private String msgType;

    private String ecCompanyId;

    @Value("${EMS.partner_id}")
    private String partnerId;

    @Value("${EMS.get-number-url}")
    private String getNumberUrl;

    @Value("${EMS.dec-url}")
    private String decUrl;

    @Autowired
    private BusinessLogService businessLogService;

    //贴XML标签,运单申报请求
    public String declare(EMSCrossBorderHeadRequest head, EMSCrossBorderFreightRequest freight,
                          EMSCrossBorderFreightRequest.KzInfo kzInfo,EMSCrossborderJyjyInfoRequest jyjyInfo) throws Exception {
        long start = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append("<Manifest>");
        {
            builder.append("<Head>");
            {
                builder.append("<MessageID>").append(head.getMessageID()).append("</MessageID>");
                builder.append("<FunctionCode>").append(head.getFunctionCode()).append("</FunctionCode>");
                builder.append("<MessageType>").append(head.getMessageType()).append("</MessageType>");
                builder.append("<SenderID>").append(head.getSenderID()).append("</SenderID>");
                builder.append("<ReceiverID>").append(head.getReceiverID()).append("</ReceiverID>");
                builder.append("<SendTime>").append(head.getSendTime()).append("</SendTime>");
                builder.append("<Version>").append(head.getVersion()).append("</Version>");
            }
            builder.append("</Head>");
            builder.append("<Declaration>");
            {
                builder.append("<Freights>");
                {
                    builder.append("<Freight>");
                    {
                        builder.append("<appType>").append(freight.getAppType()).append("</appType>");
                        builder.append("<appTime>").append(freight.getAppTime()).append("</appTime>");
                        builder.append("<appStatus>").append(freight.getAppStatus()).append("</appStatus>");
                        builder.append("<logisticsCode>").append(freight.getLogisticsCode()).append("</logisticsCode>");
                        builder.append("<logisticsName>").append(freight.getLogisticsName()).append("</logisticsName>");
                        builder.append("<logisticsNo>").append(freight.getLogisticsNo()).append("</logisticsNo>");
                        builder.append("<billNo>").append(freight.getBillNo()).append("</billNo>");
                        builder.append("<freight>").append(freight.getFreight()).append("</freight>");
                        builder.append("<insuredFee>").append(freight.getInsuredFee()).append("</insuredFee>");
                        builder.append("<currency>").append(freight.getCurrency()).append("</currency>");
                        builder.append("<weight>").append(freight.getWeight()).append("</weight>");
                        builder.append("<packNo>").append(freight.getPackNo()).append("</packNo>");
                        builder.append("<goodsInfo>").append(freight.getGoodsInfo()).append("</goodsInfo>");
                        builder.append("<consignee>").append(freight.getConsignee()).append("</consignee>");
                        builder.append("<consigneeAddress>").append(freight.getConsigneeAddress()).append("</consigneeAddress>");
                        builder.append("<consigneeTelephone>").append(freight.getConsigneeTelephone()).append("</consigneeTelephone>");
                        builder.append("<note>").append(freight.getNote()).append("</note>");
                        builder.append("<orderNo>").append(freight.getOrderNo()).append("</orderNo>");
                        builder.append("<ebpCode>").append(freight.getEbpCode()).append("</ebpCode>");
                        builder.append("<KzInfo>");
                        {
                            builder.append("<shipper>").append(kzInfo.getShipper()).append("</shipper>");
                            builder.append("<shipperAddress>").append(kzInfo.getShipperAddress()).append("</shipperAddress>");
                            builder.append("<shipperTelephone>").append(kzInfo.getShipperTelephone()).append("</shipperTelephone>");
                            builder.append("<consigneeProvince>").append(kzInfo.getConsigneeProvince()).append("</consigneeProvince>");
                            builder.append("<consigneeCity>").append(kzInfo.getConsigneeCity()).append("</consigneeCity>");
                            builder.append("<consigneeCounty>").append(kzInfo.getConsigneeCounty()).append("</consigneeCounty>");
                            builder.append("<mailType>").append(kzInfo.getMailType()).append("</mailType>");
                            builder.append("<ebpName>").append(kzInfo.getEbpName()).append("</ebpName>");
                            builder.append("<ebcCode>").append(kzInfo.getEbcCode()).append("</ebcCode>");
                            builder.append("<ebcName>").append(kzInfo.getEbcName()).append("</ebcName>");
                            builder.append("<isNotDeclSend>").append(kzInfo.getIsNotDeclSend()).append("</isNotDeclSend>");
                        }
                        builder.append("</KzInfo>");
                        builder.append("<JyjyInfo>");
                        {
                            builder.append("<ebpCiqCode>").append(jyjyInfo.getEbpCiqCode()).append("</ebpCiqCode>");
                            builder.append("<ebpCiqName>").append(jyjyInfo.getEbpCiqName()).append("</ebpCiqName>");
                        }
                        builder.append("</JyjyInfo>");
                    }
                    builder.append("</Freight>");
                }
                builder.append("</Freights>");
            }
            builder.append("</Declaration>");
        }
        builder.append("</Manifest>");

        String xml = String.valueOf(builder);
        log.info("中国邮政申报请求:"+ JSON.toJSONString(builder));
        BASE64Encoder base64en = new BASE64Encoder();
        String body=base64en.encode(xml.getBytes("UTF-8"));
        Map<String,Object> map= new TreeMap<>();
        map.put("content",body);
        String resp = HttpUtil.post(decUrl,map);
        // 保存日志
        businessLogService.saveLog(BusTypeEnum.DEC_EMS, decUrl, freight.getOrderNo(),  xml, resp, (System.currentTimeMillis() - start));

        String res = JSONUtil.toJsonStr(resp);
        log.info("中国邮政申报响应:"+res);
        return res;
    }


    //相应消息头
    public OrderCreateResponse request(String toXmlString, String orderNo) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        commonParam = new ChinaPostCommonParam();
        commonParam.setLogisticsInterface(toXmlString);
        commonParam.setMsgType(msgType);
        commonParam.setEcCompanyId(ecCompanyId);
        log.info("中国邮政取号请求:"+ JSON.toJSONString(this.commonParam));
        String sign = sign();
        String url = getNumberUrl + "?logistics_interface=" +logisticsInterface
                +"&data_digest="+sign +"&msg_type="+msgType+"&ecCompanyId="+ecCompanyId;
        String resp = HttpUtil.post(url,"");
        JSONObject res = JSONUtil.parseFromXml(resp);
        log.info("中国邮政取号响应:"+res);
        String respJson = JSONUtil.toJsonStr(res);
        System.out.println(res);
        OrderCreateResponse response = JSON.parseObject(respJson,OrderCreateResponse.class);
        // 保存日志
        businessLogService.saveLog(BusTypeEnum.MAIL_EMS, getNumberUrl, orderNo,  toXmlString, resp, (System.currentTimeMillis() - start));

//        response.setResponseItems((T) respJson.getObj("responseItems", clazz));
        return response;
    }

    //下单取号签名
    public String sign() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String str = commonParam.getLogisticsInterface() + partnerId;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        String sign=base64en.encode(md5.digest((str).getBytes("UTF-8")));
        return sign;
    }




    public String getLogisticsInterface() {
        return logisticsInterface;
    }

    public void setLogisticsInterface(String logisticsInterface) {
        this.logisticsInterface = logisticsInterface;
    }

    public String getDataDigest() {
        return dataDigest;
    }

    public void setDataDigest(String dataDigest) {
        this.dataDigest = dataDigest;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getEcCompanyId() {
        return ecCompanyId;
    }

    public void setEcCompanyId(String ecCompanyId) {
        this.ecCompanyId = ecCompanyId;
    }

}
