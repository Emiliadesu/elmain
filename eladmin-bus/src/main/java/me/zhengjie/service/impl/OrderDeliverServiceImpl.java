package me.zhengjie.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.rest.model.OutboundBatchDeliverOrder;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CustomerInfoDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.CNSupport;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.dewu.DewuSupport;
import me.zhengjie.support.douyin.SorterSupport;
import me.zhengjie.support.fuliPre.DocOrderPackingSummary;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import me.zhengjie.utils.enums.OrderDeliverStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * 订单出库实现类
 */
@Service
@RequiredArgsConstructor
public class OrderDeliverServiceImpl implements OrderDeliverService {

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private DewuSupport dewuSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private OrderDeliverLogService orderDeliverLogService;

    @Autowired
    private SortingLineChuteCodeService sortingLineChuteCodeService;

    @Autowired
    private CNSupport cnSupport;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private OrderMaterialService orderMaterialService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private SorterSupport sorterSupport;

    /**
     * 得物订单出库V2
     * @param weight
     * @param mailNo
     * @param userId
     * @return
     */
    @Override
    public SortingLineChuteCode deliverDwV2(String weight, String mailNo, String userId) {
        long start = new Date().getTime();
        if (StringUtils.isEmpty(userId)) {
            userId = "default";
        }
        SortingLineChuteCode chuteCode;
        // 查询ERP中的订单
        try {
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            if (order == null)
                throw new BadRequestException("得物订单未质检，请去质检");
            if (!StringUtils.equals(PlatformConstant.DW, order.getPlatformCode()))
                throw new BadRequestException("非得物订单请去非得物出库页面扫描");
            BigDecimal bWeight = new BigDecimal(weight);
            if (bWeight.compareTo(new BigDecimal(0)) <= 0)
                throw new BadRequestException("称重异常，请重新扫描");
            String res = kjgSupport.deliver(weight, mailNo);
            JSONObject resJson = XML.toJSONObject(res);
            JSONObject wmsFxResponse = resJson.getJSONObject("wmsFxResponse");
            Boolean success = wmsFxResponse.getBool("success");
            String resultCode = wmsFxResponse.getStr("resultCode");
            String default01 = wmsFxResponse.getStr("default01");
            if (success) {
                if (StringUtil.equals("02", resultCode) || StringUtil.equals("04", resultCode)) {
                    // 放行成功
                    // 更改订单状态，记录订单日志
                    String dwRes = dewuSupport.autoHandover(order.getLogisticsNo(), new BigDecimal(weight).multiply(new BigDecimal(1000)), order.getMaterialCode());
                    res = res + "---" + dwRes;
                    JSONObject dwResObject = JSONUtil.parseObj(dwRes);
                    if (200 != dwResObject.getInt("code")) {
                        // 得物放行不成功
                        throw new BadRequestException("得物订单放行失败：" + dwResObject.getStr("msg"));
                    }
                    order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                    order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                    order.setPackWeight(weight);
                    crossBorderOrderService.update(order);
                    chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userId, default01);
                }else if(StringUtil.equals("03", resultCode)
                        || StringUtil.equals("05", resultCode)) {
                    // 抽检
                    resultCode = "03";
                    default01 = resultCode;
                    chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userId, default01);
                    chuteCode.setDes("海关抽检");
                }else if(StringUtil.equals("01", resultCode)) {
                    // 取消中
                    throw new BadRequestException("跨境购订单取消中，请联系技术" );
                }else if(StringUtil.equals("00", resultCode)) {
                    // 异常状态
                    throw new BadRequestException("系统异常，请联系技术");
                }else {
                    throw new BadRequestException("未知异常，请联系技术");
                }
            }else {
                throw new BadRequestException(wmsFxResponse.getStr("reasons"));
            }
            OrderDeliverLog log = new OrderDeliverLog(
                    order.getShopId(),
                    "DW",
                    StringUtils.isEmpty(SecurityUtils.getCurrentUsernameForNull()) ? userId: SecurityUtils.getCurrentUsername(),
                    mailNo,
                    mailNo,
                    chuteCode.getRuleCode(),
                    weight,
                    "",
                    res,
                    (new Date().getTime()-start)
            );
            orderDeliverLogService.create(log);
            return chuteCode;
        }catch (Exception e) {
            // 如果是异常，则查询本条线配置的异常口
            chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userId, "ERROR");
            if (chuteCode != null)
                chuteCode.setDes(e.getMessage());
            OrderDeliverLog log = new OrderDeliverLog(
                    null,
                    "DW",
                    StringUtils.isEmpty(SecurityUtils.getCurrentUsernameForNull()) ? userId: SecurityUtils.getCurrentUsername(),
                    mailNo,
                    mailNo,
                    chuteCode == null ? "ERROR" : chuteCode.getRuleCode(),
                    weight,
                    "",
                    e.getMessage(),
                    (new Date().getTime()-start)
            );
            orderDeliverLogService.create(log);
            return chuteCode;
        }
    }

    /**
     * 抖音出库
     * @param weight
     * @param mailNo
     * @param userid
     * @return
     */
    @Override
    public SortingLineChuteCode deliverDYV2(String weight, String mailNo, String userid) {
        long start = new Date().getTime();
        SortingLineChuteCode chuteCode;
        try {
//            String default01 = crossBorderOrderService.deliver(weight, mailNo);
//            chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userid, default01);
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            if (order != null)
                order.setPackWeight(weight);
            // 抖音流水线称重只记录重量
            chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userid, "ERROR");
            return chuteCode;
        } catch (Exception e) {
            chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userid, "ERROR");
            if (chuteCode != null)
                chuteCode.setDes(e.getMessage());
            OrderDeliverLog log = new OrderDeliverLog(
                    null,
                    "DY",
                    StringUtils.isEmpty(SecurityUtils.getCurrentUsernameForNull()) ? userid: SecurityUtils.getCurrentUsername(),
                    mailNo,
                    mailNo,
                    chuteCode == null ? "ERROR" : chuteCode.getRuleCode(),
                    weight,
                    "",
                    e.getMessage(),
                    (new Date().getTime()-start)
            );
            orderDeliverLogService.create(log);
            return chuteCode;
        }
    }

    /**
     * 保存得物下发的耗材信息
     * @param dwResObject
     */
    @Override
    public void addOrderMaterial(JSONObject dwResObject) {
        String logisticsNo = dwResObject.getStr("expressCode");
        OrderMaterial orderMaterial = new OrderMaterial();
        orderMaterial.setLogisticsNo(logisticsNo);
        CrossBorderOrder order = crossBorderOrderService.queryByLogisticsNo(logisticsNo);
        if (order == null)
            throw new BadRequestException("查不到此运单的订单号");
        if (order.getStatus() == CBOrderStatusEnum.STATUS_245.getCode().intValue())
            throw new BadRequestException("该运单：" + logisticsNo + " 已出库");
        orderMaterial.setOrderNo(order.getOrderNo());
        orderMaterial.setCustomersId(order.getCustomersId());
        orderMaterial.setShopId(order.getShopId());
        CustomerInfoDto customerInfoDto = customerInfoService.queryById(orderMaterial.getCustomersId());
        ShopInfoDto shopInfoDto = shopInfoService.queryById(orderMaterial.getShopId());
        orderMaterial.setCustomersName(customerInfoDto.getCustName());
        orderMaterial.setShopName(shopInfoDto.getName());
        orderMaterial.setQty(1);
        orderMaterial.setOrderId(order.getId());

        JSONObject outBox = dwResObject.getJSONObject("outbox");
        JSONArray inBoxes = dwResObject.getJSONArray("inBoxes");
        if (outBox == null && inBoxes == null)
            throw new BadRequestException("inBoxes,outbox 两个个字段不能同时为空");
        if (inBoxes != null){
            int i = 1;
            if (i == inBoxes.size()){
                orderMaterial.setMaterialCode(inBoxes.getJSONObject(i-1).getStr("code"));
                orderMaterial.setMaterialName(inBoxes.getJSONObject(i-1).getStr("name"));
            }else {
                orderMaterial.setDefault01(inBoxes.toString());
            }
            if (outBox != null){
                orderMaterial.setMaterialOutCode(outBox.getStr("code"));
                orderMaterial.setMaterialOutName(outBox.getStr("name"));
            }
        }
        orderMaterialService.create(orderMaterial);
    }

    @Override
    public List<OutboundBatchDeliverOrder> getOutboundBatchDeliverOrder(String waveNo) {
        List<DocOrderHeader>orderHeaders = wmsSupport.queryOrderByWaveNo(waveNo);
        if (CollectionUtils.isEmpty(orderHeaders))
            throw new BadRequestException("无效的波次号");
        List<OutboundBatchDeliverOrder>deliverOrders = new ArrayList<>();
        for (DocOrderHeader orderHeader : orderHeaders) {
            OutboundBatchDeliverOrder deliverOrder = new OutboundBatchDeliverOrder();
            deliverOrder.setIndex(orderHeader.getSeqno());
            deliverOrder.setLogisticsNo(orderHeader.getSoreference5());
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(orderHeader.getSoreference5());
            deliverOrder.setWeight(order == null ? "0" : order.getPackWeight());
            deliverOrder.setProvince(order == null ? "-" : order.getProvince());
            deliverOrders.add(deliverOrder);
        }
        return deliverOrders;
    }

    @Override
    public List<Map<String, Object>> getOutboundBatchDeliverOrder2(String waveNo) {
        List<Map<String,Object>>mapList = new ArrayList<>();
        List<OutboundBatchDeliverOrder> orderList = getOutboundBatchDeliverOrder(waveNo);
        if (CollectionUtils.isNotEmpty(orderList)){
            for (OutboundBatchDeliverOrder deliverOrder : orderList) {
                Map<String,Object>map = new LinkedHashMap<>();
                map.put("序号",deliverOrder.getIndex());
                map.put("省份",deliverOrder.getProvince());
                map.put("运单号",deliverOrder.getLogisticsNo());
                map.put("重量KG",deliverOrder.getWeight());
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 生成测试结果
     * @param weight
     * @param mailNo
     * @param userid
     * @return
     */
    @Override
    public SortingLineChuteCode deliverTest(String weight, String mailNo, String userid) {
        String qz = "0";
        Random random = new Random();
        qz = qz + random.nextInt(4);
        SortingLineChuteCode sortingLineChuteCode = new SortingLineChuteCode();
        sortingLineChuteCode.setRuleCode(qz);
        sortingLineChuteCode.setRuleName(qz);
        sortingLineChuteCode.setChuteCode(qz);
        return sortingLineChuteCode;
    }

    /**
     * 直接分拣
     * @param mailNo
     * @return
     */
    @Override
    public SortingLineChuteCode sortOnly(String mailNo) {
        if (StringUtils.equals("NOREAD", mailNo)) {
            return sortingLineChuteCodeService.queryByUserIdAndRuleCode1("ly", "ERROR");
        }else if (StringUtils.startsWith(mailNo, "SF")) {
            return sortingLineChuteCodeService.queryByUserIdAndRuleCode1("ly", "66849640-5");
        }else {
            return sortingLineChuteCodeService.queryByUserIdAndRuleCode1("ly", "73698071-8");
        }
    }

    /**
     * 非得物订单请求出库V2
     * @param weight 重量
     * @param mailNo 运单号
     * @param userId 用户ID
     */
    @Override
    public SortingLineChuteCode deliverV2(String weight, String mailNo, String userId) {
        long start = new Date().getTime();
        if (StringUtils.isEmpty(userId)) {
            userId = "default";
        }
        SortingLineChuteCode chuteCode;
        // 查询ERP中的订单
        try {
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            String res;
            if (order != null) {
//                if (StringUtils.equals("gx02", userId))
//                    throw new BadRequestException("力源搬流水线先不进行出库");
                if (!StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()))
                    throw new BadRequestException("非抖音订单，不在此流水线出库");

                return sortOnly(mailNo);
            }else {
                if (StringUtils.equals("ly", userId)) {
                    // 抖音自研WMS走这条路
                    String dyCode = sorterSupport.getSort(mailNo, new BigDecimal(weight));
                    chuteCode = sortingLineChuteCodeService.queryByUserIdAndChuteCode(userId, dyCode);
                    res = dyCode;
                }else {
                    // 请求菜鸟出库
                    String cnChuteCode = cnSupport.requestCN(new BigDecimal(weight), mailNo, userId);
                    chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode2(userId, cnChuteCode);
                    res = cnChuteCode;
                }
            }
            OrderDeliverLog log = new OrderDeliverLog(
                    order == null ? null : order.getShopId(),
                    order == null ? "CN" : order.getPlatformCode(),
                    StringUtils.isEmpty(SecurityUtils.getCurrentUsernameForNull()) ? userId: SecurityUtils.getCurrentUsername(),
                    mailNo,
                    mailNo,
                    chuteCode.getRuleCode(),
                    weight,
                    "",
                    res,
                    (new Date().getTime()-start)
            );
            orderDeliverLogService.create(log);
            return chuteCode;
        }catch (Exception e) {
            // 如果是异常，则查询本条线配置的异常口
            chuteCode = sortingLineChuteCodeService.queryByUserIdAndRuleCode1(userId, "ERROR");
            if (chuteCode != null)
                chuteCode.setDes(e.getMessage());
            String errCode = "ERROR";
            if(StringUtils.equals(userId, "xy")) {
                errCode = "ERROR2";
            }
            OrderDeliverLog log = new OrderDeliverLog(
                    null,
                    "CN",
                    StringUtils.isEmpty(SecurityUtils.getCurrentUsernameForNull()) ? userId: SecurityUtils.getCurrentUsername(),
                    mailNo,
                    mailNo,
                    chuteCode == null ? errCode : chuteCode.getRuleCode(),
                    weight,
                    "",
                    e.getMessage(),
                    (new Date().getTime()-start)
            );
            orderDeliverLogService.create(log);
            return chuteCode;
        }
    }

    @Override
    public Boolean packageInspection(String packageCode, String mailNo) {
        if (!packageCode.matches("^2\\d{3}$") 
                && !packageCode.startsWith("F")
                && !packageCode.startsWith("B")
                && !packageCode.startsWith("Y"))
            throw new BadRequestException("包材编码不规范");
        if (!mailNo.matches("^SF\\w+")
            && !mailNo.matches("^JD\\w+")
            && !mailNo.matches("^11\\w+"))
            throw new BadRequestException("快递单号不规范");
        CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
        if (order == null) {
            order=new CrossBorderOrder();
            order.setOrderNo(mailNo);
            order.setLogisticsNo(mailNo);
            order.setMaterialCode(packageCode);
            order.setPlatformCode(PlatformConstant.DW);
            order.setShopId(394L);
            order.setCustomersId(108L);
            order.setStatus(CBOrderStatusEnum.STATUS_237.getCode());
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy(SecurityUtils.getCurrentUsername());
            crossBorderOrderService.create(order);
        }else {
            order.setMaterialCode(packageCode);
            order.setStatus(CBOrderStatusEnum.STATUS_237.getCode());
            crossBorderOrderService.update(order);
        }
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                String.valueOf(order.getStatus()),
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderLogService.create(orderLog);
        return true;
    }


    @Override
    public String chuteCodeToXml(SortingLineChuteCode chuteCode) {
        JSONObject result = new JSONObject();
        JSONObject wmsFxResponse = new JSONObject();
        wmsFxResponse.putOnce("storer", "3302461510");
        wmsFxResponse.putOnce("wmwhseid", "3302461510");
        wmsFxResponse.putOnce("success", true);
        wmsFxResponse.putOnce("reasons", "");
        wmsFxResponse.putOnce("resultCode", "");
        wmsFxResponse.putOnce("default01", chuteCode.getRuleCode());
        wmsFxResponse.putOnce("default02", chuteCode.getRuleName());
        wmsFxResponse.putOnce("default03", chuteCode.getChuteCode());
        result.putOnce("wmsFxResponse", wmsFxResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }


    /**
     * 请求出库，废弃
     * @param weight
     * @param mailNo
     * @return 返回出库格口
     */
    @Override
    public OrderDeliverStatusEnum deliver(String weight, String mailNo, String userid) {
        CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
        if (order != null
                && !StringUtils.equals(order.getPlatformCode(), PlatformConstant.DW))
            throw new BadRequestException("非得物订单不要在此界面扫描出库");

        OrderDeliverStatusEnum result = OrderDeliverStatusEnum.ERROR;
        long start = new Date().getTime();
        // 现阶段只对接得物出库，也就是只请求海关放行接口
        String res = kjgSupport.deliver(weight, mailNo);
        JSONObject resJson = XML.toJSONObject(res);
        JSONObject wmsFxResponse = resJson.getJSONObject("wmsFxResponse");
        Boolean success = wmsFxResponse.getBool("success");
        String resultCode = wmsFxResponse.getStr("resultCode");
        String default01 = wmsFxResponse.getStr("default01");
        if (success) {
            if (StringUtil.equals("00", resultCode)) {
                //异常状态
                return OrderDeliverStatusEnum.ERROR;
            }else if (StringUtil.equals("01", resultCode)) {
                return OrderDeliverStatusEnum.CANCEL;
            }else if (StringUtil.equals("02", resultCode)
                    || StringUtil.equals("04", resultCode)) {

                if (order != null) {
                    order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                    order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                    crossBorderOrderService.update(order);
                }

                dewuSupport.reqPackInfo(mailNo, new BigDecimal(weight).multiply(new BigDecimal(1000)));
                switch (default01) {
                    case "73698071-8":
                        result = OrderDeliverStatusEnum.ZTO;
                        break;
                    case "55796739-0":
                        result = OrderDeliverStatusEnum.EMS;
                        break;
                    case "66849640-5":
                        result = OrderDeliverStatusEnum.SF;
                        break;
                    case "59961300-5":
                        result = OrderDeliverStatusEnum.JD;
                        break;
                    default:
                        result = OrderDeliverStatusEnum.ERROR;
                        break;
                }
            }else if (StringUtil.equals("03", resultCode)
                    || StringUtil.equals("05", resultCode)) {
                result = OrderDeliverStatusEnum.CHECK;
            }
        }
        return result;
    }

}
