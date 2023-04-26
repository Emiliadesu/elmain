package me.zhengjie.service.impl;


import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.CommonResponse;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.support.aikucun.AikucunSupport;
import me.zhengjie.support.aikucun.apiParam.AikucunDeliveryHaitaoOrderListApiParam;
import me.zhengjie.support.aikucun.apiParam.AikucunDeliveryOrderSendApiParam;
import me.zhengjie.support.aikucun.response.AikucunDeliveryHaitaoOrderListResponse;
import me.zhengjie.support.aikucun.response.common.AikucunCommonResponse;
import me.zhengjie.utils.BigDecimalUtils;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AikucunServiceImpl implements AikucunService {

    private static final AikucunDeliveryOrderSendApiParam.ShipInfo SHIP_INFO;
    static {
        SHIP_INFO=new AikucunDeliveryOrderSendApiParam.ShipInfo();
        SHIP_INFO.setShipFromAddress("保税东区兴业四路二号");
        SHIP_INFO.setShipFromAreaName("北仑区");
        SHIP_INFO.setShipFromCityName("宁波市");
        SHIP_INFO.setShipFromName("富立物流");
        SHIP_INFO.setShipFromProvinceName("浙江省");
        SHIP_INFO.setShipFromTel("0574-86873070");
    }

    @Autowired
    private PlatformService platformService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private AikucunSupport aikucunSupport;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CombinationOrderService combinationOrderService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CombSplitService combSplitService;

    @Override
    public void aiKuCunPullOrder() {
        Platform platform = platformService.findByCode("AiKuCun");
        if (platform == null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            try {
                shopTokenService.testTokenOverdue(shopToken);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            ShopInfo shopInfo = shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopToken.getShopId());
            if (pullTime == null) {
                // 新商家一次都还没拉单过，那么拉单时间设置到前一天开始
                // 注意，如果是从老系统切换过来的商家，那么数据库手动新增一条结束时间到当天16点05分拉单成功数据
                // 这样老商家就会从当天16点开始拉单，达到切换效果
                pullTime = new PullOrderLog();
                pullTime.setPageNo(1);
                pullTime.setPageSize(100);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600 * 1000 * 24));
            }
            if (pullTime.getPageNo() == 0) {
                pullTime.setPageNo(1);
            }
            pullOrder(shopToken, shopInfo, pullTime);
        }
    }

    @Override
    public void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime) {
        try {
            //定义参数
            aikucunSupport.setAppId(shopToken.getClientId());
            aikucunSupport.setAppSecret(shopToken.getClientSecret());
            aikucunSupport.setMethod("Post");
            AikucunDeliveryHaitaoOrderListApiParam request = new AikucunDeliveryHaitaoOrderListApiParam();
            request.setStartDate(DateUtils.formatDateTime(pullTime.getStartTime()));
            request.setEndDate(DateUtils.formatDateTime(pullTime.getEndTime()));
            request.setStatus(60); //60-等待发货 70-发货中 80-等待确认收货 90-已完成 100-已取消 0-全部
            request.setPage(pullTime.getPageNo());
            request.setPageSize(pullTime.getPageSize());
            request.setFields("*");
            log.info("爱库存拉单：请求参数：" + JSONObject.toJSONString(request));
            AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse> res = aikucunSupport.request(AikucunDeliveryHaitaoOrderListResponse.class,request);
            log.info("爱库存拉单：返回：{}", JSONObject.toJSONString(res));
            Long totalNum;
            if (res.getSuccess()) {
                // 拉取成功
                request.setCustId(shopInfo.getCustId());
                request.setlShopId(shopInfo.getId());
                totalNum = res.getData().getTotalPage().longValue();
                if (totalNum < pullTime.getPageSize()) {
                    //没有下一页
                    res.getData().setlShopId(shopInfo.getId());
                    res.getData().setCustId(shopInfo.getCustId());
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pullTime.getPageNo(),
                            pullTime.getPageSize(),
                            "F",
                            totalNum.toString(),
                            "T",
                            "成功," + JSON.toJSONString(res)
                    );
                    pullOrderLogService.create(log);
                    handleOrder(res);
                } else {
                    // 循环分页拉取数据
                    // 计算总页数
                    long totalPage = res.getData().getTotalPage() % pullTime.getPageSize() == 0 ?
                            res.getData().getTotalPage() / pullTime.getPageSize()
                            : res.getData().getTotalPage() / pullTime.getPageSize() + 1;
                    for (int i = pullTime.getPageNo(); i <= totalPage; i++) {
                        request.setPage(i);
                        Map<String,Object>map=new HashMap<>();
                        map.put("request",request);
                        map.put("appId",shopToken.getClientId());
                        map.put("secret",shopToken.getClientSecret());

                        String jsonString = JSON.toJSONString(map);
                        // 发送MQ消息
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PULL_AIKUCUN,  // 这个要改成爱库存的msg
                                jsonString,
                                null
                        );
                        //pullOrderByPage(jsonString);
                    }
                }
            }
        } catch (Exception e) {
            // catch住异常信息并保存到日志表，继续循环下一个店铺
            PullOrderLog log = new PullOrderLog(
                    shopToken.getShopId(),
                    pullTime.getStartTime(),
                    pullTime.getEndTime(),
                    1,
                    0,
                    "E",
                    "0",
                    "F",
                    StringUtil.exceptionStackInfoToString(e)
            );
            pullOrderLogService.create(log);
            e.printStackTrace();
        }
    }

    @Override
    public void handleOrder(AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse>res) {
        log.info("爱库存订单分页拉单开始处理，参数：{}", res);
        List<AikucunDeliveryHaitaoOrderListResponse.Order> dataList = res.getData().getOrderList();
        if (CollectionUtils.isEmpty(dataList))
            return;
        for (AikucunDeliveryHaitaoOrderListResponse.Order order : dataList) {
            order.setlShopId(res.getData().getlShopId());
            order.setCustId(res.getData().getCustId());
            String jsonString = JSON.toJSONString(order);
            log.info("爱库存订单拉单单个订单发送消息队列：{}", jsonString);
            // 保存订单通知
            cbOrderProducer.send(
                    MsgType.CB_ORDER_200_AIKUCUN,
                    jsonString,
                    order.getAdorderId()
            );
            //createOrder(jsonString);
        }
        log.info("爱库存订单分页拉单开始处理完成");
    }

    /**
     * 根据订单号拉单
     *
     */
    @Override
    public void pullOrderByOrderNo(String[] orderNos,ShopToken shopToken) throws Exception {
        if (ArrayUtil.isEmpty(orderNos))
            throw new BadRequestException("单号为空");
        for (String orderNo : orderNos) {
            aikucunSupport.setAppId(shopToken.getClientId());
            aikucunSupport.setAppSecret(shopToken.getClientSecret());
            aikucunSupport.setMethod("Post");
            AikucunDeliveryHaitaoOrderListApiParam apiParam = new AikucunDeliveryHaitaoOrderListApiParam();
            apiParam.setPageSize(10);
            apiParam.setPage(1);
            apiParam.setStatus(60);
            apiParam.setFields("*");
            apiParam.setAdOrderId(orderNo);
            AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse> response = aikucunSupport.request(AikucunDeliveryHaitaoOrderListResponse.class,apiParam);
            if (!response.getSuccess())
                continue;
            if (CollectionUtils.isNotEmpty(response.getData().getOrderList())&&response.getData().getOrderList().get(0)!=null)
                createOrder(JSONObject.toJSONString(response.getData().getOrderList().get(0)));
        }
    }

    /**
     * 保存订单
     *
     * @param body 订单正体json字符串
     * @throws Exception
     */
    @Override
    public void createOrder(String body) throws Exception {
        AikucunDeliveryHaitaoOrderListResponse.Order aikucunOrder = JSON.parseObject(body,AikucunDeliveryHaitaoOrderListResponse.Order.class);
        try {

            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(aikucunOrder.getAdorderId());
            if (exists != null) {
                // 已保存过
                return;
            }
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();
//            order.setShopId(orderData.getLShopId()); //爱库存的shopId??
            ShopInfo shopInfo=shopInfoService.findById(Long.valueOf(aikucunOrder.getlShopId()));
            order.setCustomersId(shopInfo.getCustId());
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setOrderCreateTime(new Timestamp(aikucunOrder.getCreateTime().getTime()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            int aiKuCunStatus = aikucunOrder.getOrderStatus();
            switch (aiKuCunStatus){
                case 1:
                    order.setUpStatus("待发货");
                    break;
                case 2:
                    order.setUpStatus("已发货");
                    break;
                case 3:
                    order.setUpStatus("已取消");
                    break;
                default:
                    throw new BadRequestException("没有爱库存订单status");
            }

            order.setOrderNo(aikucunOrder.getAdorderId());
            order.setCrossBorderNo(aikucunOrder.getRcOrderId());
            order.setOrderCreateTime(new Timestamp(aikucunOrder.getCreateTime().getTime()));
            Platform platform=platformService.queryByPlafCode("AiKuCun");
            if (platform==null) {
                order.setEbpCode("-");
                order.setEbpName("-");
                order.setOrderForm("-");
            }else {
                order.setEbpCode(platform.getEbpCode());
                order.setEbpName(platform.getEbpName());
                order.setOrderForm(platform.getOrderForm());
            }
            // 这些数据一期时间紧先写死
            order.setPlatformCode("AiKuCun");
            order.setDisAmount(new BigDecimal(aikucunOrder.getCoupon().toString()).divide(BigDecimalUtils.ONEHUNDRED,2, RoundingMode.HALF_UP).toString());
            order.setIsWave("0");
            order.setIsPrint("0");
            order.setSendPickFlag("0");
            order.setPlatformStatus(2);

            order.setBooksNo(shopInfo.getBooksNo());
            order.setPostFee(new BigDecimal(aikucunOrder.getShippingFee().toString()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP).toString());
            order.setBuyerAccount(aikucunOrder.getBuyerOnlyCode());
            order.setBuyerPhone(aikucunOrder.getDeliveryMobile());
            order.setBuyerIdNum(aikucunOrder.getOrdererIdCard());
            order.setBuyerName(aikucunOrder.getOrdererName());
            order.setPayTime(DateUtils.parseDateTime(aikucunOrder.getPayInfo().getPayCompleteTime()));
            order.setPayment(new BigDecimal(aikucunOrder.getActualpayment()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP).toString()); //看不懂
            order.setPaymentNo(aikucunOrder.getPayInfo().getPayNo());
            order.setOrderSeqNo(order.getPaymentNo());
            order.setPayCode("13");
            order.setProvince(aikucunOrder.getProvince());
            order.setCity(aikucunOrder.getCity());
            order.setDistrict(aikucunOrder.getArea());
            order.setConsigneeAddr(aikucunOrder.getAddress());
            order.setConsigneeTel(aikucunOrder.getDeliveryMobile());
            order.setConsigneeName(aikucunOrder.getDeliveryName());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";

            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<AikucunDeliveryHaitaoOrderListResponse.Goods> itemLists = aikucunOrder.getGoodsList();
            if (CollectionUtils.isNotEmpty(itemLists)) {
                for (AikucunDeliveryHaitaoOrderListResponse.Goods item : itemLists) {
                    CrossBorderOrderDetails details;
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(item.getMerchantSkuId());
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit>splitList=combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            details = new CrossBorderOrderDetails();
                            int num = combSplit.getQty() * item.getTotalQty();
                            BigDecimal payment;
                            BigDecimal price;
                            BigDecimal tax;
                            if (combinationOrder.getSplitList().size() == 1) {
                                payment = new BigDecimal(item.getAmount()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP);
                                //买家实际支付价=实付价+优惠
                                price = new BigDecimal(item.getPrice()+"").divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)
                                        .divide(new BigDecimal(combSplit.getQty()), 2, BigDecimal.ROUND_HALF_UP);
                                //单价
                                tax=new BigDecimal(item.getGoodsTotalTax()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP);
                                //税费
                            } else {
                                payment = new BigDecimal(item.getAmount()+"").divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)
                                        .multiply(new BigDecimal(combSplit.getQty() + ""))
                                        .divide(new BigDecimal(combinationOrder.getSplitList().size()), 2, BigDecimal.ROUND_HALF_UP);
                                price = payment.divide(new BigDecimal(combSplit.getQty()), 2, BigDecimal.ROUND_HALF_UP);
                                tax = new BigDecimal(item.getGoodsTotalTax()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)
                                        .multiply(new BigDecimal(combSplit.getQty() + ""))
                                        .divide(new BigDecimal(combinationOrder.getSplitList().size()), 2, BigDecimal.ROUND_HALF_UP);
                                /**
                                 * 支付价/拆包数
                                 */
                            }
                            BaseSku baseSku = baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            } else {
                                details.setGoodsId(baseSku.getId());
                                details.setGoodsCode(baseSku.getGoodsCode());
                                details.setBarCode(baseSku.getBarCode());
                            }
                            details.setPlatSkuId(item.getSkuId());
                            details.setOrderNo(order.getOrderNo());
                            details.setGoodsNo(combSplit.getSplitSkuId());
                            details.setQty(num + "");
                            details.setPayment(payment + "");
                            details.setDutiableValue(price.toString());
                            details.setDutiableTotalValue(new BigDecimal(item.getAmount()+"").divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)+"");
                            details.setTaxAmount(tax+"");
                            list.add(details);
                        }
                    } else {
                        details = new CrossBorderOrderDetails();
                        BaseSku baseSku = baseSkuService.queryByGoodsNo(item.getMerchantSkuId());
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = item.getMerchantSkuId() + "未创建货品";
                        } else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setPlatSkuId(item.getSkuId());
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(item.getMerchantSkuId());//供应商sku
                        details.setQty(item.getTotalQty() + "");
                        details.setPayment(new BigDecimal(item.getAmount()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)
                                .add(new BigDecimal(aikucunOrder.getTotalTax()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP))+"");
                        details.setTaxAmount(new BigDecimal(aikucunOrder.getTotalTax()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)+"");
                        details.setDutiableTotalValue(new BigDecimal(item.getAmount()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)+"");
                        details.setDutiableValue(new BigDecimal(item.getPrice()).divide(BigDecimalUtils.ONEHUNDRED,2,RoundingMode.HALF_UP)+"");
                        list.add(details);
                    }
                }
                order.setItemList(list);

                if(needFreeze){
                    if (StringUtil.isBlank(order.getPaymentNo())){
                        freezeReason+=",无支付单号";
                    }
                }else {
                    if (StringUtil.isBlank(order.getPaymentNo())){
                        freezeReason+=",无支付单号";
                        needFreeze=true;
                    }
                }

                CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
                // 保存订单日志
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLogService.create(orderLog);
                // 冻结逻辑
                if (needFreeze) {
                    order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                    order.setFreezeReason(freezeReason);
                    crossBorderOrderService.update(order);
                    OrderLog freezeLog = new OrderLog(
                            order.getId(),
                            order.getOrderNo(),
                            String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
                            BooleanEnum.SUCCESS.getCode(),
                            freezeReason
                    );
                    orderLogService.create(freezeLog);
                } else {
                    // 接单回传通知（预售单不往下走）
                    cbOrderProducer.send(
                            MsgType.CB_ORDER_215,
                            String.valueOf(orderDto.getId()),
                            order.getOrderNo()
                    );
                    //confirmOrder(order);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    String.valueOf(aikucunOrder.getAdorderId()),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
        }


    }

    @Override
    public void confirmDeliver(String orderId) {
        CrossBorderOrder order=crossBorderOrderService.queryByIdWithDetails(Long.valueOf(orderId));
        if (order==null)
            throw new BadRequestException("订单id"+orderId+"不存在");
        confirmDeliver(order);
    }

    @Override
    public void confirmDeliver(CrossBorderOrder order) {
        try {
            ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
            if (shopToken==null)
                throw new BadRequestException("店铺授权令牌为空");
            List<AikucunDeliveryOrderSendApiParam.BackSalesOrderDetailsDto>dtoList=new ArrayList<>();
            if (CollectionUtils.isEmpty(order.getItemList()))
                order=crossBorderOrderService.queryByIdWithDetails(order.getId());
            for (CrossBorderOrderDetails details : order.getItemList()) {
                AikucunDeliveryOrderSendApiParam.BackSalesOrderDetailsDto dto=new AikucunDeliveryOrderSendApiParam.BackSalesOrderDetailsDto();
                dto.setBarcode(details.getBarCode());
                dto.setQty(Integer.parseInt(details.getQty()));
                dto.setRealQty(Integer.parseInt(details.getQty()));
                dto.setSkuId(details.getPlatSkuId());
                dtoList.add(dto);
            }
            AikucunDeliveryOrderSendApiParam.DeliveryInfo deliveryInfo=new AikucunDeliveryOrderSendApiParam.DeliveryInfo();
            deliveryInfo.setCarrierId("zhongtong");
            deliveryInfo.setCarrierName("中通快递");
            deliveryInfo.setDeliveryNo(order.getLogisticsNo());
            deliveryInfo.setShippedTime(new Date());
            deliveryInfo.setShipInfo(SHIP_INFO);
            deliveryInfo.setShipmentList(dtoList);
            deliveryInfo.setShipmentList(dtoList);
            List<AikucunDeliveryOrderSendApiParam.DeliveryInfo>deliveryInfoList=new ArrayList<>();
            deliveryInfoList.add(deliveryInfo);
            AikucunDeliveryOrderSendApiParam.AdOrder adOrder=new AikucunDeliveryOrderSendApiParam.AdOrder();
            adOrder.setDeliveryList(deliveryInfoList);
            adOrder.setAdOrderId(order.getOrderNo());
            List<AikucunDeliveryOrderSendApiParam.AdOrder>adOrderList=new ArrayList<>();
            adOrderList.add(adOrder);
            AikucunDeliveryOrderSendApiParam param=new AikucunDeliveryOrderSendApiParam();
            param.setAdOrderList(adOrderList);
            aikucunSupport.setMethod("Post");
            aikucunSupport.setAppId(shopToken.getClientId());
            aikucunSupport.setAppSecret(shopToken.getClientSecret());
            AikucunCommonResponse<EmptyResponse>response=aikucunSupport.request(EmptyResponse.class,param);
            if (response.isSuccess()){
                order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
                crossBorderOrderService.update(order);
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderLog.setReqMsg(param.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
            }else {
                // 出库回传失败
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderLog.setReqMsg(param.toString());
                orderLog.setResMsg(response.toString());
                orderLogService.create(orderLog);
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e){
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void pullOrderByPage(String body) {
        JSONObject data=JSONObject.parseObject(body);
        aikucunSupport.setAppId(data.getString("appId"));
        aikucunSupport.setAppSecret(data.getString("secret"));
        AikucunDeliveryHaitaoOrderListApiParam param=data.getObject("request",AikucunDeliveryHaitaoOrderListApiParam.class);
        long custId=param.getCustId();
        long shopId=param.getlShopId();
        param.setCustId(null);
        param.setlShopId(null);
        AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse>response=aikucunSupport.request(AikucunDeliveryHaitaoOrderListResponse.class,param);
        if (response.isSuccess()){
            boolean resHasNext = response.getData().getOrderList().size()>=param.getPageSize();
            response.getData().setlShopId(shopId);
            response.getData().setCustId(custId);
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            Integer totalNum = response.getData().getTotalRecord();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    param.getlShopId(),
                    DateUtils.parseDateTime(param.getStartDate()),
                    DateUtils.parseDateTime(param.getEndDate()),
                    param.getPage(),
                    param.getPageSize(),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功,"+response.toString()
            );
            pullOrderLogService.create(log);
            handleOrder(response);
        }else {
            // catch住异常信息并保存到日志表，继续循环下一个店铺
            PullOrderLog log = new PullOrderLog(
                    param.getlShopId(),
                    DateUtils.parseDateTime(param.getStartDate()),
                    DateUtils.parseDateTime(param.getEndDate()),
                    1,
                    0,
                    "E",
                    "0",
                    "F",
                    response.getMessage()
            );
            pullOrderLogService.create(log);
        }
    }

    @Override
    public void confirmOrder(CrossBorderOrder order) {
        order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
        crossBorderOrderService.update(order);
        cbOrderProducer.send(
                MsgType.CB_ORDER_220,
                String.valueOf(String.valueOf(order.getId())),
                order.getOrderNo()
        );
    }

    @Override
    public void confirmOrder(String orderId) {
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order!=null)
            confirmOrder(order);
    }

    @Override
    public void confirmPack(CrossBorderOrder order) {
        order.setPackBackTime(new Timestamp(System.currentTimeMillis()));
        order.setStatus(CBOrderStatusEnum.STATUS_240.getCode());
        crossBorderOrderService.update(order);
        // 500ms后回传出库
        cbOrderProducer.delaySend(
                MsgType.CB_ORDER_245,
                String.valueOf(order.getId()),
                order.getOrderNo(),
                500
        );
    }

    @Override
    public void confirmPack(String orderId) {
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order!=null)
            confirmPack(order);
    }

    @Override
    public Integer getOrderStatus(String orderNo, Long shopId) {
        ShopToken shopToken=shopTokenService.queryByShopId(shopId);
        if (shopToken==null)
            throw new BadRequestException("店铺令牌不存在");
        aikucunSupport.setAppId(shopToken.getClientId());
        aikucunSupport.setAppSecret(shopToken.getClientSecret());
        aikucunSupport.setMethod("Post");
        AikucunDeliveryHaitaoOrderListApiParam apiParam = new AikucunDeliveryHaitaoOrderListApiParam();
        apiParam.setPageSize(10);
        apiParam.setPage(1);
        apiParam.setStatus(60);
        apiParam.setFields("*");
        apiParam.setAdOrderId(orderNo);
        AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse> response = aikucunSupport.request(AikucunDeliveryHaitaoOrderListResponse.class,apiParam);
        if (!response.getSuccess())
            throw new BadRequestException(orderNo+"状态查询失败:"+response.getMessage());
        if (CollectionUtils.isEmpty(response.getData().getOrderList()))
            throw new BadRequestException(orderNo+"订单不存在");
        return response.getData().getOrderList().get(0).getOrderStatus();
    }

}
