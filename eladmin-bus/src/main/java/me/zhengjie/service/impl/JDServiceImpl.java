package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.jingdong.*;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class JDServiceImpl implements JDService {

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private JDSupport jdSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    //京东物流下单取号
    @Override
    public void getOrderNumber(CrossBorderOrder crossBorderOrder) {
        //保存请求信息
        JDLdopWaybillReceiveRequest receive = new JDLdopWaybillReceiveRequest();
        receive.setSalePlat("0030001");//销售平台（0010001京东商城；0010002天猫、淘宝订单；0030001其他平台）
        receive.setCustomerCode("021K1421608");  // 商家编码  (青龙)
        String orderId = crossBorderOrder.getCrossBorderNo();
        if (StringUtils.isEmpty(orderId))
            throw new BadRequestException("orderNo不该为空");
        receive.setOrderId(orderId);
        receive.setThrOrderId(crossBorderOrder.getCrossBorderNo());  //销售平台订单号
        receive.setSenderName("富立物流");
        receive.setSenderAddress("浙江省宁波市北仑区港东大道29号");
        receive.setSenderTel("0666169082");
        receive.setSenderMobile("15178241301");
        receive.setSenderPostcode("315800");
        String receiveName = crossBorderOrder.getConsigneeName();
        if (StringUtils.isEmpty(receiveName))
            throw new BadRequestException("consigneeName不该为空");
        receive.setReceiveName(receiveName);
        String receiveAddress = crossBorderOrder.getConsigneeAddr();
        if (StringUtils.isEmpty(receiveAddress))
            throw new BadRequestException("consigneeAddr不该为空");
        receive.setReceiveAddress(receiveAddress);
        String province = crossBorderOrder.getProvince();
        receive.setProvince(province);
        String city = crossBorderOrder.getCity();
        receive.setCity(city);
        String county = crossBorderOrder.getDistrict();
        receive.setCounty(county);
        receive.setSiteName("无");
        String receiveMobile = crossBorderOrder.getConsigneeTel();
        if (StringUtils.isEmpty(receiveMobile))
            throw new BadRequestException("consigneeTel不该为空");
        receive.setReceiveMobile(receiveMobile);
        receive.setPackageCount(1);  //默认一个包裹数
       BigDecimal wight =new BigDecimal(crossBorderOrder.getGrossWeight());
        if (wight == null)
            throw new BadRequestException("grossWeight不该为空");
        BigDecimal weigh = wight;
        BigDecimal weight = weigh.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
        receive.setWeight(weight);
        List<CrossBorderOrderDetails> details = crossBorderOrder.getItemList();
        receive.setVloumn(new BigDecimal("4000"));
        receive.setCollectionValue(0);
        receive.setGuaranteeValue(0);
        receive.setSignReturn(1);
        receive.setAging(1);
        receive.setTransType(1);
        receive.setGoodsType(1);
        receive.setOrderType(0);
        String idNumber = crossBorderOrder.getBuyerIdNum();
        receive.setIdNumber(idNumber);
        receive.setSenderCompany("富立物流");
//        Long freight = Long.valueOf(crossBorderOrder.getPostFee());
//        receive.setFreight(BigDecimal.valueOf(freight));  由于京东有邮费时才必填,不然会报错    小于等于0
        //receive.setFileUrl("");  函速达的文件地址???
        String json = JSONObject.toJSONString(receive);
        //1. 先获取token
        ShopInfo shopInfo = shopInfoService.queryByShopCode("JD_MAIL_SHOP");
        ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
        //2.保存下特殊信息传到support
        String js = "360buy_param_json";
        String method = "jingdong.ldop.waybill.receive";
        String timestamp = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ");
        //请求下单取号
        try {
            JingdongLdopWaybillReceiveResponce response = jdSupport.requests(js,json,shopToken,timestamp,method);
            if (StringUtils.equals("0",response.getJingdongLdopWaybillReceiveResponce().getCode())){
                //取运单号成功   保存信息
                crossBorderOrder.setLogisticsNo(response.getJingdongLdopWaybillReceiveResponce().getReceiveorderinfoResult().getDeliveryId());
                crossBorderOrder.setLogisticsName("京东快递");
                crossBorderOrder.setLogisticsCode("JD");
                crossBorderOrder.setAddMark(response.getJingdongLdopWaybillReceiveResponce().getReceiveorderinfoResult().getPreSortResult().getSourceTabletrolleyCodel());
                //记录日志
                OrderLog orderLog = new OrderLog(
                        crossBorderOrder.getId()==null?0L:crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        crossBorderOrder.getId()==null?(crossBorderOrder.getStatus()+""):(CBOrderStatusEnum.STATUS_200.getCode()+""),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(JSONUtil.toJsonStr(json));
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
                orderLog.setReqMsg(JSONUtil.toJsonStr(json));
                orderLog.setResMsg(JSONUtil.toJsonStr(response));
                orderLogService.create(orderLog);
            }
        } catch (Exception e) {
            throw new BadRequestException("京东物流下单取号失败:"+e.getMessage());
        }

    }

    //获取申报所需信息
    @Override
    public void getDecinfo(CrossBorderOrder crossBorderOrder) {
        JDEclpOrderAddDeclareCustsRequest declare = new JDEclpOrderAddDeclareCustsRequest();
        ShopInfoDto shopInfoDto = shopInfoService.queryById(crossBorderOrder.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        declare.setPlatformId("8040752");  //三方平台编码：8040752
        declare.setPlatformName("宁波富立物流有限公司");  //三方平台名称：宁波富立物流有限公司
        declare.setCustomsId("ningbo");   //保税区编码
        declare.setCustomsCode("3105");  //关区编码：3105
        declare.setDeptNo("EBU4418054849799");  //事业部编码:EBU4418054849799
        declare.setIsvSource("ISV0020008042781");  //ISV编码：ISV0020008042781
        declare.setPattern("beihuo");   //   业务模式：beihuo
        declare.setLogisticsCode("11089609XE");   //物流企业代码：11089609XE
        declare.setLogisticsName("北京京邦达贸易有限公司");    //物流企业名称：北京京邦达贸易有限公司
        declare.setAppType("1");  //写死
        declare.setInsuredFee(BigDecimal.ZERO); //写死
        declare.setPackNo(1);  //写死
        declare.setShipper("富立物流");
        declare.setShipperAddress("浙江省宁波市北仑区港东大道29号");
        declare.setShipperTelephone("15178241301");
        declare.setShipperCountry("中国");
        declare.setConsigneeCountry("中国");
        declare.setBuyerIdType("1");  //先写死  为身份证
        declare.setPlatformType(2);  //写死
        declare.setPostType("I");//先写死,I-进口商品订单；E-出口商品订单
        declare.setIstax(0);  //0-包税；1-不包税
        declare.setIsDelivery(0);  //是否货到付款，1-是；0-否
        declare.setIsvUUID(UUID.randomUUID().toString().toUpperCase());  //isvUUID
        String logisticsNo = crossBorderOrder.getLogisticsNo();
        if (StringUtils.isEmpty(logisticsNo))
            throw new BadRequestException("LogisticsNo不该为空");
        declare.setLogisticsNo(logisticsNo);
        BigDecimal freight = BigDecimal.ZERO;
        if (!StringUtils.isBlank(crossBorderOrder.getPostFee())) {
            freight = new BigDecimal(crossBorderOrder.getPostFee());
        }
        declare.setFreight(freight);
        BigDecimal netWeight = new BigDecimal(crossBorderOrder.getNetWeight());
        if (netWeight == null)
            throw new BadRequestException("netWeight不该为空");
        declare.setNetWeight(netWeight);
        BigDecimal weight = new BigDecimal(crossBorderOrder.getGrossWeight());
        if (weight == null)
            throw new BadRequestException("grossWeight不该为空");
        declare.setWeight(weight);
        BigDecimal worth = new BigDecimal(crossBorderOrder.getPayment());
        if (worth == null)
            throw new BadRequestException("payment不该为空");
        declare.setWorth(worth);
        String orderNo = crossBorderOrder.getCrossBorderNo();
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("CrossBorderNo不该为空");
        declare.setOrderNo(orderNo);
        String consigneeProvince = crossBorderOrder.getProvince();
        if (StringUtils.isEmpty(consigneeProvince))
            throw new BadRequestException("province不该为空");
        declare.setConsigneeProvince(consigneeProvince);
        String consigneeCity = crossBorderOrder.getCity();
        if (StringUtils.isEmpty(consigneeCity))
            throw new BadRequestException("city不该为空");
        declare.setConsigneeCity(consigneeCity);
        String consigneeDistrict = crossBorderOrder.getDistrict();
        declare.setConsigneeDistrict(consigneeDistrict);
        String consingee = crossBorderOrder.getConsigneeName();
        if (StringUtils.isEmpty(consingee))
            throw new BadRequestException("consigneeName不该为空");
        declare.setConsingee(consingee);
        String consigneeAddress = crossBorderOrder.getConsigneeAddr();
        if (StringUtils.isEmpty(consigneeAddress))
            throw new BadRequestException("consigneeAddr不该为空");
        declare.setConsigneeAddress(consigneeAddress);
        String consigneeTelephone = crossBorderOrder.getConsigneeTel();
        if (StringUtils.isEmpty(consigneeTelephone))
            throw new BadRequestException("consigneeTel不该为空");
        declare.setConsigneeTelephone(consigneeTelephone);
        String buyerIdNumber = crossBorderOrder.getBuyerIdNum();
        declare.setBuyerIdNumber(buyerIdNumber);
        declare.setSalesPlatformCreateTime(new Timestamp(System.currentTimeMillis()));
        String ebpCode = crossBorderOrder.getEbpCode();
        declare.setEbpCode(ebpCode);
        String ebpName = crossBorderOrder.getEbpName();
        declare.setEbpName(ebpName);
        String ebpCiqCode = crossBorderOrder.getEbpCode();
        declare.setEbpCiqCode(ebpCiqCode);
        String ebpCiqName = crossBorderOrder.getEbpName();
        declare.setEbpCiqName(ebpCiqName);
        String ebcCode = clearCompanyInfo.getCustomsCode();
        declare.setEbcCode(ebcCode);
        String ebcName = clearCompanyInfo.getClearCompanyName();
        declare.setEbcName(ebcName);
        String ebcCiqCode = clearCompanyInfo.getCustomsCode();
        declare.setEbcCiqCode(ebcCiqCode);
        String ebcCiqName = clearCompanyInfo.getClearCompanyName();
        declare.setEbcCiqName(ebcCiqName);
        List<CrossBorderOrderDetails> details = crossBorderOrder.getItemList();
        for (CrossBorderOrderDetails detail : details) {
            declare.setGoodsName(detail.getGoodsName());
        }
        String json = JSONObject.toJSONString(declare);
        //1. 先获取token
        ShopInfo shopInfo = shopInfoService.queryByShopCode("JD_MAIL_SHOP");
        ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());

        //2.保存下特殊信息传到support
        String js = "360buy_param_json";
        String method = "jingdong.eclp.order.addDeclareOrderCustoms";
        String timestamp = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ");
        //请求运单申报
        try {
            JDEclpOrderAddDeclareCustsResponse response = jdSupport.request(js,json,shopToken,timestamp,method);
            OrderLog orderLog;
            if ("1".equals(response.getJingdongEclpOrderAddDeclareOrderCustomsResponce().getDeclaredOrderCustomsResult().getResultCode())){
                //记录日志
                orderLog = new OrderLog(
                        crossBorderOrder.getId() == null ? 0L : crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        crossBorderOrder.getId() == null ? (crossBorderOrder.getStatus() + "") : (CBOrderStatusEnum.STATUS_200.getCode() + ""),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
            }else {
                orderLog = new OrderLog(
                        crossBorderOrder.getId() == null ? 0L : crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        String.valueOf(crossBorderOrder.getStatus()),
                        BooleanEnum.FAIL.getCode(),
                        BooleanEnum.FAIL.getDescription()
                );
            }
            orderLog.setReqMsg(JSONUtil.toJsonStr(json));
            orderLog.setResMsg(JSONUtil.toJsonStr(response));
            orderLogService.create(orderLog);
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException("京东物流运单申报失败:"+e.getMessage());
        }
    }

    //查询物流跟踪消息
    @Override
    public void queryOrder(String orderNo) {
        //传入运单号,商家编码
        JDLdopReceiveTraceGetRequest orderGet = new JDLdopReceiveTraceGetRequest();
        orderGet.setWaybillCode(orderNo);
        orderGet.setCustomerCode("021K1421608"); //富立物流使用青龙账号
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByOrderNo(orderNo);
        if (crossBorderOrder == null)
            throw new BadRequestException("此orderNo有误");
        ShopToken shopToken = shopTokenService.queryByShopId(crossBorderOrder.getShopId());
        if (shopToken == null)
            throw new BadRequestException("shopToken为空");
        //设置公共参数,请求京东去查询
        JDCommonParam commonParam = new JDCommonParam();
        commonParam.setJs("360buy_param_json");
        commonParam.setJson(JSONObject.toJSONString(orderGet));
        commonParam.setMethod("jingdong.ldop.receive.trace.get");
        commonParam.setTimestamp(DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.sssZ"));
        commonParam.setShopToken(shopToken);
        try {
            JDLdopReceiveTraceGetResponse response = jdSupport.requests(commonParam);
            if (response.getCode().equals("100")){
                //记录日志
                OrderLog orderLog = new OrderLog(
                        crossBorderOrder.getId()==null?0L:crossBorderOrder.getId(),
                        crossBorderOrder.getOrderNo(),
                        crossBorderOrder.getId()==null?(crossBorderOrder.getStatus()+""):(CBOrderStatusEnum.STATUS_200.getCode()+""),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(JSONUtil.toJsonStr(commonParam.getJson()));
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
                orderLog.setReqMsg(JSONUtil.toJsonStr(commonParam.getJson()));
                orderLog.setResMsg(JSONUtil.toJsonStr(response));
                orderLogService.create(orderLog);
            }
        } catch (Exception e) {
            throw new BadRequestException("京东物流查询物流信息失败:"+e.getMessage());
        }
    }

    public static void main(String[] args) {
        JDLdopWaybillReceiveRequest receive = new JDLdopWaybillReceiveRequest();
        receive.setSalePlat("0030001");//销售平台（0010001京东商城；0010002天猫、淘宝订单；0030001其他平台）
        receive.setCustomerCode("021K1421608");  // 商家编码  (青龙)
        String orderId ="123211";
        if (StringUtils.isEmpty(orderId))
            throw new BadRequestException("orderNo不该为空");
        receive.setOrderId(orderId);
        receive.setThrOrderId("544554");  //销售平台订单号
        receive.setSenderName("富立物流");
        receive.setSenderAddress("浙江省宁波市北仑区保税东区兴业四路二号");
        receive.setSenderTel("0666169082");
        receive.setSenderMobile("18888888888");
        receive.setSenderPostcode("315800");
        String receiveName = "ConsigneeName";
        if (StringUtils.isEmpty(receiveName))
            throw new BadRequestException("consigneeName不该为空");
        receive.setReceiveName(receiveName);
        String receiveAddress = "ConsigneeAddr";
        if (StringUtils.isEmpty(receiveAddress))
            throw new BadRequestException("consigneeAddr不该为空");
        receive.setReceiveAddress(receiveAddress);
        String province = "Province";
        receive.setProvince(province);
        String city = "City";
        receive.setCity(city);
        String county = "District";
        receive.setCounty(county);
        receive.setSiteName("无");
        String receiveMobile = "ConsigneeTel";
        if (StringUtils.isEmpty(receiveMobile))
            throw new BadRequestException("consigneeTel不该为空");
        receive.setReceiveMobile(receiveMobile);
        receive.setPackageCount(1);  //默认一个包裹数
        Long wight = Long.valueOf("124");
        if (wight == null)
            throw new BadRequestException("grossWeight不该为空");
        BigDecimal weigh = BigDecimal.valueOf(wight);
        BigDecimal weight = weigh.divide(BigDecimal.ONE,2,BigDecimal.ROUND_HALF_UP);
        receive.setWeight(weight);
        receive.setCollectionValue(0);
        receive.setGuaranteeValue(0);
        receive.setSignReturn(1);
        receive.setAging(1);
        receive.setTransType(1);
        receive.setGoodsType(1);
        receive.setOrderType(0);
        String remark = "BuyerRemark";
        receive.setRemark(remark);
        String idNumber = "1222221111555488";
        receive.setIdNumber(idNumber);
        receive.setSenderCompany("富立物流");
//        Long freight = Long.valueOf("556");
//        receive.setFreight(BigDecimal.valueOf(freight));  由于京东必须有油费才填, 不然会报错  小于等于0
       // receive.setFileUrl("");  函速达的文件地址???
        //类转成JSON序列化字符串
        String str = JSONObject.toJSONString(receive);
        System.out.println(str);
        //建议利用TreeMap来重新排序类  (京东传输需求)
        TreeMap<String,Object> map = JSONObject.parseObject(str,TreeMap.class);
        Set<Map.Entry<String,Object>> entrySet = map.entrySet();
        for (Map.Entry<String, Object> stringObjectEntry : entrySet) {
            String json = JSONObject.toJSONString(stringObjectEntry);
            System.out.println(json);
        }
    }

    @Override
    public void refreshToken() throws Exception {
        ShopInfo shopInfo = shopInfoService.queryByShopCode("JD_MAIL_SHOP");
        ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
        //刷新token
        jdSupport.refreshToken(shopToken);
        //保存shopToken库
        shopTokenService.update(shopToken);
    }


}
