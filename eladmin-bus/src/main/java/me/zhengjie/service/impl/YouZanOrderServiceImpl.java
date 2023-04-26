package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.youzan.*;
import me.zhengjie.domain.*;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.PrivacyUtil;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class YouZanOrderServiceImpl implements YouZanOrderService {
    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private YouZanSupport youZanSupport;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private CombinationOrderService combinationOrderService;

    @Autowired
    private CombSplitService combSplitService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public void youZanPullOrder() {
        Platform platform=platformService.findByCode("YZ");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            try {
                shopTokenService.testTokenOverdue(shopToken);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopToken.getShopId());
            if (pullTime == null) {
                // 新商家一次都还没拉单过，那么拉单时间设置到前一天开始
                // 注意，如果是从老系统切换过来的商家，那么数据库手动新增一条结束时间到当天16点05分拉单成功数据
                // 这样老商家就会从当天16点开始拉单，达到切换效果
                pullTime = new PullOrderLog();
                pullTime.setPageNo(1);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
            }
            if (pullTime.getPageNo()==0){
                pullTime.setPageNo(1);
            }
            pullOrder(shopToken, shopInfo, pullTime);
        }
    }

    private void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime) {
        Date startTime = new Date(pullTime.getStartTime().getTime());
        Date endTime = new Date(pullTime.getEndTime().getTime());
        int pageNo = pullTime.getPageNo();
        int pageSize = 100;
        YouzanTradesSoldGetParams request = new YouzanTradesSoldGetParams();
        request.setEndCreated(endTime);
        request.setStartCreated(startTime);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        request.setOfflineId(Long.parseLong(shopToken.getPlatformShopId()));
        request.setStatus("WAIT_SELLER_SEND_GOODS");//要拉取的订单状态是待发货
        request.setLShopId(shopInfo.getId());
        request.setCustId(shopInfo.getCustId());
        Long totalNum = null;
        try {
            log.info("有赞拉单：请求参数：{}", request);
            // 第一次先查询本次有多少订单
            YouzanTradesSoldGetResponse response = youZanSupport.pullOrder(request,shopToken.getAccessToken());
            log.info("有赞拉单：返回：{}", response);
            if (response.getSuccess()) {
                // 拉取成功
                totalNum = response.getData().getTotalResults();
                if (totalNum < pageSize) {
                    //没有下一页
                    response.setlShopId(shopInfo.getId());
                    response.setCustId(shopInfo.getCustId());
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "F",
                            totalNum.toString(),
                            "T",
                            "成功,"+JSON.toJSONString(response)
                    );
                    pullOrderLogService.create(log);
                    handleOrder(response,shopToken.getPlatformShopId());
                } else {
                    // 循环分页拉取数据
                    // 计算总页数
                    long totalPage = response.getData().getTotalResults()%request.getPageSize()==0?
                            response.getData().getTotalResults()/request.getPageSize()
                            :response.getData().getTotalResults()/request.getPageSize()+1;
                    for (int i = pageNo; i <= totalPage; i++) {
                        request.setPageNo(i);
                        request.setPageSize(pageSize);
                        String jsonString=JSON.toJSONString(request);
                        // 发送MQ消息
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PULL_YZ,
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

    private void handleOrder(YouzanTradesSoldGetResponse response,String shopCode) throws Exception{
        log.info("有赞订单分页拉单开始处理，参数：{}", response);
        List<YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist> list = response.getData().getFullOrderInfoList();
        for (YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist youzanOrder : list) {
            YzOrder yzOrder=new YzOrder(youzanOrder.getFullOrderInfo());
            yzOrder.setCustId(response.getCustId());
            yzOrder.setlShopId(response.getlShopId());
            yzOrder.setShopId(shopCode);
            if (StringUtil.equals("1", yzOrder.getIsCrossBorder())
                    && StringUtil.equals("NN", yzOrder.getCustomsCode())){
                String jsonString = JSON.toJSONString(yzOrder);
                log.info("有赞订单拉单单个订单发送消息队列：{}", jsonString);
                // 保存订单通知
                cbOrderProducer.send(
                        MsgType.CB_ORDER_200_YZ,
                        jsonString,
                        yzOrder.getTid()
                );
                //createOrder(jsonString);
            }
        }
        log.info("有赞订单分页拉单开始处理完成");
    }

    @Override
    public void pullOrderByHours(Integer hours) {
        Platform platform=platformService.findByCode("YZ");
        if (platform==null)
            return;
        List<ShopToken> shopTokenList = shopTokenService.queryEnableTokenByPlatId(platform.getId());
        for (ShopToken shopToken : shopTokenList) {
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            PullOrderLog pullTime = new PullOrderLog();
            pullTime.setPageNo(1);
            pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
            pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*hours));
            pullTime.setPageNo(1);
            pullOrder(shopToken, shopInfo, pullTime);
        }
    }

    @Override
    public String getOrderStatus(String orderNo, String shopCode) throws Exception {
        if (StringUtil.isEmpty(orderNo))
            return null;
        ShopToken shopToken=shopTokenService.queryByPaltShopId(shopCode);
        shopTokenService.testTokenOverdue(shopToken);
        return youZanSupport.queryOrderStatus(orderNo,shopToken.getAccessToken());
    }

    @Override
    public void pullOrderByOrderNo(String[] orderNos, ShopToken shopToken) throws Exception {
        if (ArrayUtil.isEmpty(orderNos))
            throw new BadRequestException("单号为空");
        for (String orderNo : orderNos) {
            YouzanTradeGetResult result=youZanSupport.pullOrderByTid(orderNo,shopToken.getAccessToken());
            if (!result.getSuccess())
                continue;
            YzOrder yzOrder=new YzOrder(result.getData());
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            yzOrder.setlShopId(shopInfo.getId());
            yzOrder.setCustId(shopInfo.getCustId());
            yzOrder.setShopId(shopToken.getPlatformShopId());
            try {
                createOrder(new JSONObject(yzOrder).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void confirmDeliver(String orderId) throws Exception {
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.parseLong(orderId));
        if (order==null)
            throw new BadRequestException("订单id:"+orderId+"不存在");
        confirmDeliver(order);
    }

    @Override
    public void confirmDeliver(CrossBorderOrder order) throws Exception {
        try {
            if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
                throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
            }
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + order.getOrderNo());
            }
            log.info("有赞开始回传出库,单号：{}", order.getOrderNo());
            YouzanLogisticsOnlineConfirmParams params=new YouzanLogisticsOnlineConfirmParams();
            params.setTid(order.getOrderNo());//有赞订单号
            if (StringUtil.isBlank(order.getLogisticsCode()))
                order.setLogisticsCode("ZTO");
            switch (order.getLogisticsCode()){
                case "330296T004":
                case "EMS":
                    params.setOutStype("11");
                    break;
                case "11089609XE":
                case "JD":
                    params.setOutStype("138");
                    break;
                default:
                    params.setOutStype("289");
                    break;
            }
            params.setOutSid(order.getLogisticsNo());//快递单号
            YouzanLogisticsOnlineConfirmResult result=youZanSupport.deliverGoods(params,shopToken.getAccessToken());
            if (!result.getSuccess()){
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                        BooleanEnum.FAIL.getCode(),
                        result.getMessage()
                );
                orderLog.setReqMsg(com.alibaba.fastjson.JSONObject.toJSONString(params));
                orderLog.setResMsg(com.alibaba.fastjson.JSONObject.toJSONString(result));
                orderLogService.create(orderLog);
                throw new BadRequestException(result.getMessage());
            }
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
            orderLog.setReqMsg(JSON.toJSONString(params));
            orderLog.setResMsg(JSON.toJSONString(result));
            orderLogService.create(orderLog);
        }catch (Exception e) {
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void pullOrderByPage(String body) throws Exception {
        YouzanTradesSoldGetParams params=JSON.parseObject(body,YouzanTradesSoldGetParams.class);
        ShopToken shopToken=shopTokenService.queryByShopId(params.getLShopId());
        shopTokenService.testTokenOverdue(shopToken);
        log.info("有赞拉单：请求参数：{}", JSON.toJSONString(params));
        YouzanTradesSoldGetResponse response=youZanSupport.pullOrder(params,shopToken.getAccessToken());
        log.info("有赞拉单：返回：{}", JSON.toJSONString(response));
        if (response.getSuccess()) {
            boolean resHasNext = response.getData().getFullOrderInfoList().size()>=params.getPageSize();
            response.setlShopId(params.getLShopId());
            response.setCustId(params.getCustId());
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            Long totalNum = response.getData().getTotalResults();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    params.getLShopId(),
                    new Timestamp(params.getStartCreated().getTime()),
                    new Timestamp(params.getEndCreated().getTime()),
                    params.getPageNo(),
                    params.getPageSize(),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功,"+JSON.toJSONString(response)
            );
            pullOrderLogService.create(log);
            handleOrder(response,shopToken.getPlatformShopId());
        }else {
            PullOrderLog log = new PullOrderLog(
                    params.getLShopId(),
                    new Timestamp(params.getStartCreated().getTime()),
                    new Timestamp(params.getEndCreated().getTime()),
                    params.getPageNo(),
                    params.getPageSize(),
                    "E",
                    "0",
                    "F",
                    response.getMessage()+","+JSON.toJSONString(response)
            );
            pullOrderLogService.create(log);
        }
    }

    @Override
    public void createOrder(String body) throws Exception {
        YzOrder yzOrder = JSON.parseObject(body,YzOrder.class);
        try {
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(yzOrder.getTid());
            if (exists != null) {
                // 已保存过
                return;
            }
            ShopInfo shopInfo=shopInfoService.findById(yzOrder.getlShopId());
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setShopId(yzOrder.getlShopId());
            order.setPlatformShopId(yzOrder.getShopId());
            order.setCustomersId(yzOrder.getCustId());
            order.setUpStatus(yzOrder.getStatus());


            order.setOrderNo(yzOrder.getTid());
            order.setCrossBorderNo(yzOrder.getSubOrderNo());
            order.setOrderCreateTime(new Timestamp(yzOrder.getCreated().getTime()));
            order.setEbpCode("3301961H10");
            order.setEbpName("杭州起码科技有限公司");

            // 这些数据一期时间紧先写死
            order.setPlatformCode("YZ");
            order.setOrderForm("1174");
            order.setDisAmount("0.0");
            order.setPostFee(yzOrder.getPostFee());
            order.setPayment(yzOrder.getPayment());// 实际支付金额
            order.setBuyerAccount("null");
            order.setBuyerPhone(StringUtil.isEmpty(yzOrder.getBuyerPhone()) ? yzOrder.getReceiverTel() : yzOrder.getBuyerPhone());
            order.setBuyerIdNum(yzOrder.getIdCardNumber());
            order.setBuyerName(yzOrder.getIdCardName());
            order.setPayTime(new Timestamp(yzOrder.getPayTime().getTime()));
            order.setPaymentNo(null);
            order.setOrderSeqNo(null);
            order.setBooksNo(shopInfo.getBooksNo());
            order.setPayCode("38");
            order.setProvince(yzOrder.getDeliveryProvince());
            order.setCity(yzOrder.getDeliveryCity());
            order.setDistrict(yzOrder.getDeliveryDistrict());
            order.setConsigneeAddr(yzOrder.getDeliveryAddress());
            order.setConsigneeTel(yzOrder.getReceiverTel());
            order.setConsigneeName(yzOrder.getReceiverName());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";
            BigDecimal amount=BigDecimal.ZERO;
            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<YzOrderDetails> yzOrderDetails = yzOrder.getDetails();
            if (CollectionUtils.isNotEmpty(yzOrderDetails)) {
                for (YzOrderDetails yzOrderDetail : yzOrderDetails) {
                    String goodsNo = null;
                    if (StringUtil.isNotEmpty(yzOrderDetail.getOuterItemId())){
                        goodsNo=yzOrderDetail.getOuterItemId();
                    }else {
                        goodsNo=yzOrderDetail.getOuterSkuId();
                    }
                    amount=amount.add(new BigDecimal(yzOrderDetail.getFenxiaoPayment()));
                    CombinationOrder combinationOrder = combinationOrderService.queryByCombSku(goodsNo);
                    if (combinationOrder != null) {
                        //组合单
                        List<CombSplit>splitList=combSplitService.queryByCombId(combinationOrder.getId());
                        for (CombSplit combSplit : splitList) {
                            BigDecimal payment;
                            if (splitList.size() == 1&&yzOrderDetails.size()==1) {//如果拆出的商品只有一种sku，直接用有赞的支付价格
                                if (StringUtil.isNotEmpty(yzOrderDetail.getFenxiaoPayment())&&Double.parseDouble(yzOrderDetail.getFenxiaoPayment())!=0)
                                    payment = new BigDecimal(yzOrderDetail.getFenxiaoPayment());
                                else {
                                    payment = new BigDecimal(yzOrderDetail.getPayment());
                                }
                            } else {
                                if (StringUtil.isNotEmpty(yzOrderDetail.getFenxiaoPayment())&&Double.parseDouble(yzOrderDetail.getFenxiaoPayment())!=0)
                                    payment = new BigDecimal(yzOrderDetail.getFenxiaoPayment()).
                                            multiply(new BigDecimal(combSplit.getQty())).
                                            divide(new BigDecimal(combinationOrder.getSplitQty()),2,BigDecimal.ROUND_HALF_UP);
                                else {
                                    payment = new BigDecimal(yzOrderDetail.getPayment()).
                                            multiply(new BigDecimal(combSplit.getQty())).
                                            divide(new BigDecimal(combinationOrder.getSplitQty()),2,BigDecimal.ROUND_HALF_UP);
                                }
                            /*details.getPayment()*combSplit.getQty()/combinationOrder.getSplitQty()
                            组合包实付价*被拆出商品的数量/一共拆出的数量
                            */
                            }

                            CrossBorderOrderDetails detail = new CrossBorderOrderDetails();
                            BaseSku baseSku=baseSkuService.queryByGoodsNo(combSplit.getSplitSkuId());
                            if (baseSku == null) {
                                needFreeze = true;
                                freezeReason = combSplit.getSplitSkuId() + "未创建货品";
                            }else {
                                detail.setGoodsId(baseSku.getId());
                                detail.setGoodsCode(baseSku.getGoodsCode());
                                detail.setBarCode(baseSku.getBarCode());
                            }
                            detail.setOrderNo(order.getOrderNo());
                            detail.setGoodsNo(combSplit.getSplitSkuId());
                            detail.setQty((combSplit.getQty()*yzOrderDetail.getNum())+"");
                            detail.setPayment(payment.toString());
                            list.add(detail);
                        }
                    } else {
                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        BaseSku baseSku=baseSkuService.queryByGoodsNo(goodsNo);
                        if (baseSku == null) {
                            needFreeze = true;
                            freezeReason = goodsNo + "未创建货品";
                        }else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        details.setOrderNo(order.getOrderNo());
                        details.setGoodsNo(goodsNo);
                        details.setQty(yzOrderDetail.getNum()+"");
                        details.setPayment(yzOrderDetail.getPayment());
                        list.add(details);
                    }
                }
            }
            order.setItemList(list);
            if (order.getCity().length() <= 1)
                order.setCity(order.getDistrict());
            if (StringUtil.contains(order.getCity(),"直辖县")){
                order.setCity(order.getDistrict());
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
            if(needFreeze){
                if (StringUtil.isBlank(yzOrder.getSubOrderNo())){
                    freezeReason+=",无跨境单号";
                }
            }else {
                if (StringUtil.isBlank(yzOrder.getSubOrderNo())){
                    freezeReason+=",无跨境单号";
                    needFreeze=true;
                }
            }
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
            }else {
                // 推送支付单
                cbOrderProducer.send(
                        MsgType.CB_ORDER_205,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );
                //pushPayOrder(order);
            }
        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    yzOrder.getTid(),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage(),
                    StringUtil.exceptionStackInfoToString(e)
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
        }

    }

    @Override
    public void pushPayOrder(CrossBorderOrder order) throws Exception {
        if (!order.getStatus().equals(CBOrderStatusEnum.STATUS_200.getCode())){
            throw new BadRequestException("非接单状态不可推送支付单");
        }
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        shopTokenService.testTokenOverdue(shopToken);
        YouzanPayCustomsDeclarationReportpaymentReportResponse response=youZanSupport.pushYzOrder(order.getOrderNo(),shopToken);
        //判断是否有错误响应
        if (response.getGwErrResp()!=null){
            throw new BadRequestException("有赞支付申报失败："+JSON.toJSONString(response));
        }
        if (response.getSuccess()){
            YouzanPayCustomsDeclarationReportpaymentReportResult.YouzanPayCustomsDeclarationReportpaymentReportResultData data = response.getData();
            if (data != null) {
                // 支付报关开始成功
                order.setPaymentNo(data.getDeclareCustomsNo());
                order.setOrderSeqNo(data.getDeclareCustomsNo());
                order.setStatus(CBOrderStatusEnum.STATUS_205.getCode());
                crossBorderOrderService.update(order);
                OrderLog orderLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_205.getCode()),
                        "",
                        response.toString(),
                        BooleanEnum.SUCCESS.getCode(),
                        "推送支付单成功"
                );
                orderLogService.create(orderLog);
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(order.getId()),
                        order.getOrderNo()
                );
                //confirmOrder(order);
            }
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_205.getCode()),
                    "",
                    com.alibaba.fastjson.JSONObject.toJSONString(response),
                    BooleanEnum.FAIL.getCode(),
                    "推送支付单失败"
            );
            orderLogService.create(orderLog);
            throw new BadRequestException(JSON.toJSONString(response));
        }
    }



    @Override
    public void orderDecrypt(CrossBorderOrder order) throws Exception {
        order.setConsigneeName(youZanSupport.decryptData(order.getPlatformShopId(), order.getConsigneeName()));
        order.setConsigneeTel(youZanSupport.decryptData(order.getPlatformShopId(), order.getConsigneeTel()));
        order.setConsigneeAddr(youZanSupport.decryptData(order.getPlatformShopId(), order.getConsigneeAddr()));
        order.setBuyerName(youZanSupport.decryptData(order.getPlatformShopId(), order.getBuyerName()));
        order.setBuyerIdNum(youZanSupport.decryptData(order.getPlatformShopId(), order.getBuyerIdNum()));
        order.setBuyerPhone(youZanSupport.decryptData(order.getPlatformShopId(), order.getBuyerPhone()));
    }

    @Override
    public void orderEncrypt(CrossBorderOrder order) throws Exception {
        order.setConsigneeName(youZanSupport.encryptData(order.getPlatformShopId(), order.getConsigneeName()));
        order.setConsigneeTel(youZanSupport.encryptData(order.getPlatformShopId(), order.getConsigneeTel()));
        order.setConsigneeAddr(youZanSupport.encryptData(order.getPlatformShopId(), order.getConsigneeAddr()));
        order.setBuyerName(youZanSupport.encryptData(order.getPlatformShopId(), order.getBuyerName()));
        order.setBuyerIdNum(youZanSupport.encryptData(order.getPlatformShopId(), order.getBuyerIdNum()));
        order.setBuyerPhone(youZanSupport.encryptData(order.getPlatformShopId(), order.getBuyerPhone()));
    }

    @Override
    public void decryptMask(CrossBorderOrder order) {
        order.setConsigneeName(PrivacyUtil.maskNameData(order.getConsigneeName()));
        order.setConsigneeTel(PrivacyUtil.maskPhoneData(order.getConsigneeTel()));
        order.setConsigneeAddr(PrivacyUtil.maskAddressData(order.getConsigneeAddr()));
        order.setBuyerName(PrivacyUtil.maskNameData(order.getBuyerName()));
        order.setBuyerIdNum(PrivacyUtil.maskIdCardNumData(order.getBuyerIdNum()));
        order.setBuyerPhone(PrivacyUtil.maskPhoneData(order.getBuyerPhone()));
    }

    @Override
    public void pullOrderByTimeRange(Date startTime, Date endTime,ShopInfo shopInfo) throws Exception{
        ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
        shopTokenService.testTokenOverdue(shopToken);
        PullOrderLog pullTime = new PullOrderLog();
        pullTime.setPageNo(1);
        pullTime.setEndTime(new Timestamp(endTime.getTime()));
        pullTime.setStartTime(new Timestamp(startTime.getTime()));
        pullTime.setPageNo(1);
        pullOrder(shopToken, shopInfo, pullTime);
    }

    @Override
    public void confirmOrder(CrossBorderOrder order) throws Exception {
        order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
        crossBorderOrderService.update(order);
        cbOrderProducer.send(
                MsgType.CB_ORDER_220,
                String.valueOf(String.valueOf(order.getId())),
                order.getOrderNo()
        );
    }

    @Override
    public long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception {
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(shopId);
            int pageNo = 1;
            int pageSize = 1;

            YouzanTradesSoldGetParams request = new YouzanTradesSoldGetParams();
            request.setStartCreated(DateUtils.parse(startTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setEndCreated(DateUtils.parse(endTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setPageNo(pageNo);
            request.setPageSize(pageSize);
            request.setOfflineId(Long.parseLong(shopToken.getPlatformShopId()));
            // 第一次先查询本次有多少订单
            YouzanTradesSoldGetResponse response=youZanSupport.pullOrder(request,shopToken.getAccessToken());
            if (response.getSuccess()) {
                return response.getData().getTotalResults();
            }else {
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String rePushPayOrder(CrossBorderOrder order) throws Exception{
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        shopTokenService.testTokenOverdue(shopToken);
        YouzanPayCustomsDeclarationReportpaymentReportResponse response=youZanSupport.pushYzOrder(order.getOrderNo(),shopToken);
        //判断是否有错误响应
        if (response.getGwErrResp()!=null){
            throw new BadRequestException("有赞支付申报失败："+JSON.toJSONString(response));
        }
        if (response.getSuccess()){
            YouzanPayCustomsDeclarationReportpaymentReportResult.YouzanPayCustomsDeclarationReportpaymentReportResultData data = response.getData();
            if (data != null) {
                // 支付报关开始成功
                return data.getDeclareCustomsNo();
            }else {
                throw new BadRequestException("推送支付单的响应data为null");
            }
        }else {
            throw new BadRequestException(JSON.toJSONString(response));
        }
    }
}
