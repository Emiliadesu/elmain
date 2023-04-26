package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alipay.api.internal.util.StringUtils;
import lombok.SneakyThrows;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.repository.CrossBorderOrderRepository;
import me.zhengjie.repository.OrderReturnRepository;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CustomerInfoDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.chinaPost.*;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EMSServiceImpl implements EMSService {

    @Autowired
    private EMSSupport emsSupport;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Value("${EMS.ecommerce-no}")
    private String ecommerceNo;

    @Value("${EMS.ec-company-id}")
    private String ecCompanyId;

    //中国邮政取号
    @Override
    public void getOrderNumber(CrossBorderOrder crossBorderOrder) {
       //保存请求信息
        OrderCreateRequest.OrderNormal orderNormal = new OrderCreateRequest.OrderNormal();
        orderNormal.setCreated_time(DateUtils.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        //logistics_provider  先写死
        orderNormal.setLogistics_provider('B');
        //因与ecCompanyId电商标识保持一致先写死
        orderNormal.setEcommerce_no(ecommerceNo);
        //ecommerce_user_id 即 shopId
        orderNormal.setEcommerce_user_id(String.valueOf(crossBorderOrder.getShopId()));
        //默认为1
        orderNormal.setSender_type(1);
        orderNormal.setOne_bill_flag(0); //不是一票多件
        //inner_channel		内部订单来源标识	必填默认0：直接对接
        orderNormal.setInner_channel(0);
        String orderNo = crossBorderOrder.getCrossBorderNo();
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("缺少交易号");
        orderNormal.setLogistics_order_no(orderNo);
        String batchNo = crossBorderOrder.getWaveNo();
        orderNormal.setBatch_no(batchNo);
        orderNormal.setBiz_product_no("");
        //base_product_no	        基础产品代码  如果logistics _provider  物流承运方  传B：速递     base_product_no    基础产品代码 传 1：标准快递
        orderNormal.setBase_product_no("2");
        orderNormal.setPickup_notes(crossBorderOrder.getBuyerRemark());
        List<CrossBorderOrderDetails> itemList = crossBorderOrder.getItemList();
        for (CrossBorderOrderDetails sku : itemList) {
            OrderCreateRequest.OrderNormal.Cargo cargo = new OrderCreateRequest.OrderNormal.Cargo();
            cargo.setCargo_name(sku.getGoodsName());
            List<OrderCreateRequest.OrderNormal.Cargo> cargos = new ArrayList<>();
            cargos.add(cargo);
            orderNormal.setCargos(cargos);
        }
        EMSAddress sender = new EMSAddress();
        sender.setName("富立物流");
        sender.setMobile("0666169082");
        sender.setProv("浙江省");
        sender.setCity("宁波市");
        sender.setCounty("北仑区");
        sender.setAddress("浙江省宁波市北仑区保税东区港东大道29号");
        orderNormal.setSender(sender);
        EMSAddress receiver = new EMSAddress();
        receiver.setName(crossBorderOrder.getConsigneeName());
        receiver.setMobile(crossBorderOrder.getConsigneeTel());
        receiver.setProv(crossBorderOrder.getProvince());
        receiver.setCity(crossBorderOrder.getCity());
        receiver.setCounty(crossBorderOrder.getDistrict());
        receiver.setAddress(crossBorderOrder.getConsigneeAddr());
        orderNormal.setReceiver(receiver);

        JSON request = JSONUtil.parseObj(orderNormal);
        Map<String,Object> map=new HashMap<>();
        map.put("OrderNormal",request);
        JSON req = JSONUtil.parseObj(map);
        String toXmlString = JSONUtil.toXmlStr(req);
        emsSupport.setLogisticsInterface(toXmlString);
        emsSupport.setMsgType("ORDERCREATE");
        emsSupport.setEcCompanyId(ecCompanyId);
        //请求中国邮政   保存相应数据 ?  记录订单日志
        try {
             OrderCreateResponse response = emsSupport.request(toXmlString, crossBorderOrder.getCrossBorderNo());
            if (response.getResponses().getResponseItems().getResponse().isSuccess() == true) {
                //取运单号   保存信息
                crossBorderOrder.setLogisticsNo(response.getResponses().getResponseItems().getResponse().getWaybillNo());
                crossBorderOrder.setLogisticsName("中国邮政");
                crossBorderOrder.setLogisticsCode("EMS");
                crossBorderOrder.setAddMark(response.getResponses().getResponseItems().getResponse().getRouteCode());
                //记录日志
                OrderLog orderLog = new OrderLog(
                        crossBorderOrder.getId()==null?0L:crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        crossBorderOrder.getId()==null?(crossBorderOrder.getStatus()+""):(CBOrderStatusEnum.STATUS_200.getCode()+""),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(JSONUtil.toJsonStr(request));
                orderLog.setResMsg(JSONUtil.toJsonStr(response));
                orderLogService.create(orderLog);
            }else {
                OrderLog orderLog = new OrderLog(
                        crossBorderOrder.getId()==null?0L:crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        String.valueOf(crossBorderOrder.getStatus()),
                        BooleanEnum.FAIL.getCode(),
                        BooleanEnum.FAIL.getDescription()
                );
                orderLog.setReqMsg(JSONUtil.toJsonStr(request));
                orderLog.setResMsg(JSONUtil.toJsonStr(response));
                orderLogService.create(orderLog);
                throw new BadRequestException("邮政取号失败!");
            }
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("邮政取号申请失败①:"+e.getLocalizedMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException("邮政取号申请失败②"+e.getMessage());
        }

    }

    /**
     * 获取申报所需信息
     * @param crossBorderOrder)
     */
    public void getDecinfo(CrossBorderOrder crossBorderOrder){
        EMSCrossBorderHeadRequest head = new EMSCrossBorderHeadRequest();
        head.setMessageID(IdUtil.simpleUUID().toUpperCase());
        head.setFunctionCode("0"); //默认0
        head.setMessageType("511"); //默认填写:511
        head.setSenderID("3011");  //宁波富立物流有限公司    SenderID：3011
        head.setReceiverID("EMS"); //默认填写：EMS
        head.setSendTime(DateUtils.formatDateTime(new Timestamp(System.currentTimeMillis()))); //格式是  2015-06-09 11:36:34
        head.setVersion("1.0");// 默认1.0
        EMSCrossBorderFreightRequest freight = new EMSCrossBorderFreightRequest();
        freight.setAppType("1");  //企业报送类型。默认为1
        freight.setAppTime(DateUtils.format(new Timestamp(System.currentTimeMillis()),"yyyyMMddHHmmss"));  // 格式是  20160511010101
        freight.setAppStatus("2");  //业务状态:1-暂存,2-申报,默认为2

        freight.setLogisticsCode("330296T004");  //物流企业代码
        freight.setLogisticsName("中国邮政速递物流股份有限公司宁波市分公司");  //物流企业名称
        String logisticsNo = crossBorderOrder.getLogisticsNo();
        if (StringUtils.isEmpty(logisticsNo))
            throw new BadRequestException("缺少logisticsNo");
        freight.setLogisticsNo(logisticsNo);
        freight.setBillNo("*");  //提运单号   是交易号?
        freight.setFreight(BigDecimal.ZERO);
        BigDecimal insuredFee = BigDecimal.ZERO;
        freight.setInsuredFee(insuredFee);
        String currency = "142";
        freight.setCurrency(currency);  //限定为人民币，填写“142”
        BigDecimal weight = new BigDecimal(crossBorderOrder.getGrossWeight());
        if (weight == null)
            throw new BadRequestException("缺少weight必填");
        freight.setWeight(weight);
        freight.setPackNo(1);  //单个运单下包裹数，限定为“1”
        List<CrossBorderOrderDetails> crossBorderOrderDetails = crossBorderOrder.getItemList();
        for (CrossBorderOrderDetails orderDetail : crossBorderOrderDetails) {
            String goodsInfo = orderDetail.getGoodsCode()+orderDetail.getGoodsName()+
                    orderDetail.getGoodsNo()+orderDetail.getQty();
            freight.setGoodsInfo(goodsInfo);
        }
        String consignee = crossBorderOrder.getConsigneeName();
        if (StringUtils.isEmpty(consignee))
            throw new BadRequestException("缺少consigneeName");
        freight.setConsignee(consignee);
        String consigneeAddress = crossBorderOrder.getConsigneeAddr();
        if (StringUtils.isEmpty(consigneeAddress))
            throw new  BadRequestException("consigneeAddress必须填写");
        freight.setConsigneeAddress(consigneeAddress);
        String consigneeTelephone = crossBorderOrder.getConsigneeTel();
        if (StringUtils.isEmpty(consigneeTelephone))
            throw new BadRequestException("consigneeTelephone必须填写");
        freight.setConsigneeTelephone(consigneeTelephone);
        String note = crossBorderOrder.getBuyerRemark();
        freight.setNote(note);
        String orderNo = crossBorderOrder.getCrossBorderNo();
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("缺少CrossBorderNo");
        freight.setOrderNo(orderNo);
        String ebpCode = crossBorderOrder.getEbpCode();
        if (StringUtils.isEmpty(ebpCode))
            throw new BadRequestException("缺少ebpCode");
        freight.setEbpCode(ebpCode);

        EMSCrossBorderFreightRequest.KzInfo kzInfo = new EMSCrossBorderFreightRequest.KzInfo();
        String shipper = "富立物流";
        kzInfo.setShipper(shipper);
        String shipperAddress = "浙江省宁波市北仑区保税东区兴业四路二号";
        kzInfo.setShipperAddress(shipperAddress);
        String shipperTelephone = "0666169082";
        kzInfo.setShipperTelephone(shipperTelephone);

        String consigneeProvince = crossBorderOrder.getProvince();
        if (StringUtils.isEmpty(consigneeProvince))
            throw new BadRequestException("缺少consigneeProvince");
        kzInfo.setConsigneeProvince(consigneeProvince);
        String consigneeCity = crossBorderOrder.getCity();
        if (StringUtils.isEmpty(consigneeCity))
            throw new BadRequestException("缺少consigneeCity");
        kzInfo.setConsigneeCity(consigneeCity);
        String consigneeCounty = crossBorderOrder.getDistrict();
        if (StringUtils.isEmpty(consigneeCounty))
            throw new BadRequestException("缺少District");
        kzInfo.setConsigneeCounty(consigneeCounty);
        String mailType = "1";
        kzInfo.setMailType(mailType); //  邮件类型??
        String ebpName = crossBorderOrder.getEbpName();
        if (StringUtils.isEmpty(ebpName))
            throw new BadRequestException("缺少ebpName");
        kzInfo.setEbpName(ebpName);
        ShopInfoDto shopInfoDto = shopInfoService.queryById(crossBorderOrder.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        String ebcCode = clearCompanyInfo.getCustomsCode();
        if (StringUtils.isEmpty(ebcCode))
            throw new BadRequestException("缺少ebcCode");
        kzInfo.setEbcCode(ebcCode);
        String ebcName = clearCompanyInfo.getClearCompanyName();
        if (StringUtils.isEmpty(ebcName))
            throw new BadRequestException("缺少ebcName");
        kzInfo.setEbcName(ebcName);
        kzInfo.setIsNotDeclSend("Y");  //是否发送海关报关?
        freight.setKzInfo(kzInfo);

        EMSCrossborderJyjyInfoRequest jyjyInfo = new EMSCrossborderJyjyInfoRequest();
        jyjyInfo.setEbpCiqCode(crossBorderOrder.getEbpCode()); //电商平台编码(检)
        jyjyInfo.setEbpCiqName(crossBorderOrder.getEbpName()); //电商平台名称(检)

        try {
            //获取申报请求
            String response = emsSupport.declare(head,freight,kzInfo,jyjyInfo);
            // 保存订单日志
            OrderLog orderLog= new OrderLog(
                    crossBorderOrder.getId()==null?0L:crossBorderOrder.getId(),
                    crossBorderOrder.getOrderNo(),
                    crossBorderOrder.getId()==null?(crossBorderOrder.getStatus()+""):(CBOrderStatusEnum.STATUS_220.getCode()+""),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setResMsg(JSONUtil.toJsonStr(response));
            orderLogService.create(orderLog);
        } catch (Exception e) {
            throw new BadRequestException("运单申报失败"+e.getMessage());
        }

    }

    public String getEcommerceNo() {
        return ecommerceNo;
    }

    public void setEcommerceNo(String ecommerceNo) {
        this.ecommerceNo = ecommerceNo;
    }

    public String getEcCompanyId() {
        return ecCompanyId;
    }

    public void setEcCompanyId(String ecCompanyId) {
        this.ecCompanyId = ecCompanyId;
    }
}
