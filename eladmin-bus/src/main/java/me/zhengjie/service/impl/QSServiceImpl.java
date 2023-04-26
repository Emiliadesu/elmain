package me.zhengjie.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.queenshop.*;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 圈尚接口服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QSServiceImpl implements QSService {

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private QSSupport qsSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private PlatformService platformService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private QSStockDetails qsStockDetails;



    /*
    圈商 拉单任务
     */
    @Override
    public void pullOrder() {
        //查询圈商的商家平台,做基础的业务判断
        List<ShopInfo> shopList = shopInfoService.queryByPlafCode(PlatformConstant.QS);
        if (CollectionUtils.isEmpty(shopList))
            throw new BadRequestException("无可拉单商家，请检查平台商家配置");
        //遍历 查询店铺ID,做是否开启拉单操作的判断
        for (ShopInfo shopInfo : shopList) {
            ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
            if (!StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }
            //把拉单日志记录保存到数据库中
            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopInfo.getId());
            if (pullTime == null) {
                pullTime = new PullOrderLog();
                pullTime.setPageNo(1);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
            }
            int pageNo = pullTime.getPageNo()==0?1:pullTime.getPageNo();
            int pageSize = 100;
            Date startTime = new Date(pullTime.getStartTime().getTime());
            Date endTime = new Date(pullTime.getEndTime().getTime());
            System.err.println(pullTime);

            //保存圈商清单请求
            QSOrderListRequest request = new QSOrderListRequest();
            request.setOrderStatus("WAIT_SELLER_DELIVERY");
            request.setPage(pageNo);
            request.setPageSize(pageSize);
            request.setDateType(1);
            request.setStartTime(startTime.getTime() / 1000);
            request.setEndTime(endTime.getTime() / 1000);
            request.setCustId(shopInfo.getCustId());
            request.setShopId(shopInfo.getId());
            qsSupport.setApiParam(request);
            try {
                log.info("圈尚拉单，请求参数：{}", request);
                QSCommonResponse<QSOrderListResponse> response = qsSupport.request(QSOrderListResponse.class);
                log.info("圈尚拉单，返回：{}", response);
                if (response.getSuccess()) {
                    if (!response.getResult().getLastPage()) {
                        // 有下一页，那么就往下循环拉取
                        Integer pageTotal = response.getResult().getPageTotal();
                        for (int i = pageNo; i < pageTotal.intValue(); i++) {
                            request.setPage(i);
                            request.setPageSize(pageSize);
                            // 发送MQ消息
                            String jsonString = JSON.toJSONString(request);

                        }
                    }else {
                        // 没有下一页
                        response.getResult().setlShopId(shopInfo.getId());
                        response.getResult().setCustId(shopInfo.getCustId());
                        // 记录拉单日志
                        PullOrderLog log = new PullOrderLog(
                                shopInfo.getId(),
                                pullTime.getStartTime(),
                                pullTime.getEndTime(),
                                pageNo,
                                pageSize,
                                "F",
                                response.getResult().getCount().toString(),
                                "T",
                                "成功"
                        );
                        pullOrderLogService.create(log);
                        handleOrder(response);
                    }

                }else {
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "E",
                            "0",
                            "F",
                            response.getMessage()
                    );
                    pullOrderLogService.create(log);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void pullOrderByPage(String body) throws Exception {
        QSOrderListRequest request = JSON.parseObject(body, QSOrderListRequest.class);
        qsSupport.setApiParam(request);
        log.info("圈尚拉单，请求参数：{}", request);
        QSCommonResponse<QSOrderListResponse> response = qsSupport.request(QSOrderListResponse.class);
        log.info("圈尚拉单，返回：{}", response);
        if (response.getSuccess()) {
            Boolean resHasNext = response.getResult().getLastPage();
            response.getResult().setlShopId(request.getShopId());
            response.getResult().setCustId(request.getCustId());

            Integer totalNum = response.getResult().getCount();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    request.getShopId(),
                    new Timestamp(request.getStartTime()),
                    new Timestamp(request.getEndTime()),
                    Integer.valueOf(request.getPage()),
                    Integer.valueOf(request.getPageSize()),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功"
            );
            pullOrderLogService.create(log);
            handleOrder(response);
        }else {
            PullOrderLog log = new PullOrderLog(
                    request.getShopId(),
                    new Timestamp(request.getStartTime()),
                    new Timestamp(request.getEndTime()),
                    Integer.valueOf(request.getPage()),
                    Integer.valueOf(request.getPageSize()),
                    "E",
                    "0",
                    "F",
                    response.getMessage()
            );
            pullOrderLogService.create(log);
        }
    }

    private synchronized void handleOrder(QSCommonResponse<QSOrderListResponse> response) {
        List<QSOrderListMain> list = response.getResult().getList();
        for (QSOrderListMain qsOrderListMain : list) {
            qsOrderListMain.setlShopId(response.getResult().getlShopId());
            qsOrderListMain.setCustId(response.getResult().getCustId());
            String jsonString = JSON.toJSONString(qsOrderListMain);

            createOrder(jsonString);
            // 保存订单通知
//            cbOrderProducer.send(
//                    MsgType.CB_ORDER_200,
//                    String.valueOf(jsonString),
//                    cbOrderListMain.getOrderId()
//            );


        }
    }

    @Override
    public synchronized void confirmClearErr(CrossBorderOrder order) throws Exception{
        log.info("圈尚开始回传清关异常,单号：{}", order.getOrderNo());
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_225.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传清关异常：" + order.getOrderNo());
        }
        QSOrderClearErrRequest request = new QSOrderClearErrRequest();
        request.setOrderNo(order.getOrderNo());
            request.setReason(order.getDeclareMsg());
        request.setNotifyTime(DateUtils.nowDate());
        qsSupport.setApiParam(request);
        log.info("圈尚回传清关异常,单号：{},请求参数：{}", order.getOrderNo(), qsSupport.getApiParam());
        QSCommonResponse <EmptyResponse> response = qsSupport.request(EmptyResponse.class);
        log.info("圈尚回传清关异常,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.getSuccess()) {
            // 回传成功
            order.setStatus(CBOrderStatusEnum.STATUS_227.getCode());
            crossBorderOrderService.update(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    /*
    确认圈商订单的出库,发货
     */
    @Override
    public synchronized void confirmDeliver(CrossBorderOrder order)  throws Exception{
//        判断状态字段 :  需要查询平台状态是否允许出库
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
        }
        log.info("圈尚开始回传出库,单号：{}", order.getOrderNo());
        //记录圈商出库请求
        QSOrderDeliveryRequest request = new QSOrderDeliveryRequest();
        request.setOrderNo(order.getOrderNo());
        request.setLogisticsNo(order.getLogisticsNo());
        request.setLogisticsName("中通快递");
        qsSupport.setApiParam(request);
        log.info("圈商回传出库,单号：{},请求参数：{}", order.getOrderNo(), qsSupport.getApiParam());
        QSCommonResponse <EmptyResponse> response = qsSupport.request(EmptyResponse.class);
        log.info("圈商回传出库,单号：{},返回：{}", order.getOrderNo(), response);
         if (response.getSuccess()) {
            // 请求成功
            order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
            //update 保存操作
            crossBorderOrderService.update(order);
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
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
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    /*
    确认圈商库存更新
     */
    @Override
    public synchronized void QSStockUpdate(CrossBorderOrder order)  throws Exception{
//        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
//            throw new BadRequestException("此状态不能更新库存：" + order.getOrderNo());
//        }
        log.info("圈尚开始更新库存,单号：{}", order.getOrderNo());
        //记录圈商更新库存请求
        QSStockUpdateRequest request = new QSStockUpdateRequest();
        request.setRemark("test111");
        request.setWmsId(2584);
        List<QSStockDetails> details = new ArrayList<>();

        QSStockDetails qsStockDetails = new QSStockDetails();
        qsStockDetails.setStock(20);
        qsStockDetails.setSupSkuNo(111);
        qsStockDetails.setType(1);
        details.add(qsStockDetails);
//         request莫忘记保存进去details
        request.setStockDetails(details);
        qsSupport.setApiParam(request);
        log.info("圈商回传更新库存,单号：{},请求参数：{}", order.getOrderNo(), qsSupport.getApiParam());
        QSCommonResponse <EmptyResponse> response = qsSupport.request(EmptyResponse.class);
        log.info("圈商回传更新库存,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.getSuccess()) {
            // 请求成功
            order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_240.getCode());
            //update 保存操作
            crossBorderOrderService.update(order);
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLogService.create(orderLog);

        }else {
            // 更新库存失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public String getOrderStatus(String orderStatus, String refundStatus) throws Exception{
        //判断orderStatus 是否 空
        if (StringUtil.isEmpty(orderStatus))
            return null;
        QSOrderListMain request = new QSOrderListMain();
        request.setOrderStatus(orderStatus);
        request.setRefundStatus(refundStatus);
        qsSupport.setRequest(request);
        QSCommonResponse<QSOrderListResponse>response = qsSupport.request(QSOrderListResponse.class);
        List<QSOrderListMain>statuses= response.getResult().getList();
        if (CollectionUtils.isEmpty(statuses))
            throw new BadRequestException("无此订单");
        if (StringUtil.equals(statuses.get(0).getRefundStatus(),"NO_REFUND")) {
            return statuses.get(0).getOrderStatus();
        }
        return statuses.get(0).getRefundStatus();
    }


    // 保存订单
    @Override
    public synchronized void createOrder(String body) {
        try {
            QSOrderListMain orderMain = JSON.parseObject(body, QSOrderListMain.class);
            if (!StringUtil.equals("WAIT_SELLER_DELIVERY", orderMain.getOrderStatus())) {
                // 非待发货状态的订单不保存
                return;
            }
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(orderMain.getOrderNo());
            if (exists != null) {
                // 已保存过
                return;
            }
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
            order.setShopId(orderMain.getlShopId());
            order.setCustomersId(orderMain.getCustId());
            order.setUpStatus(orderMain.getOrderStatus());


            order.setOrderNo(orderMain.getOrderNo());
            order.setCrossBorderNo(orderMain.getDeclareNo());
            order.setOrderCreateTime(DateUtils.parseDate(orderMain.getCreateTime()));
            order.setEbpCode(orderMain.getEbpCode());
            order.setEbpName(orderMain.getEbpName());

            // 这些数据一期时间紧先写死
            // 查询平台相关信息
            Platform platform = platformService.queryByPlafCode(PlatformConstant.QS);
            order.setPlatformCode(PlatformConstant.QS);
            order.setOrderForm(platform.getOrderForm());// 需要更改
            order.setDisAmount(orderMain.getDiscount().toString());
            order.setPostFee(orderMain.getPostAmount().toString());
            order.setPayment(orderMain.getPayAmount().toString());// 实际支付金额
            order.setBuyerAccount(orderMain.getCustomerId());
            order.setBuyerPhone(orderMain.getTel());
            order.setBuyerIdNum(orderMain.getPayerNumber());
            order.setBuyerName(orderMain.getPayerName());
            order.setPayTime(DateUtils.parseDate(orderMain.getPayTime()));
            order.setTaxAmount(orderMain.getTaxAmount().toString());// 总税费
            order.setPaymentNo(orderMain.getPayNo());
            order.setOrderSeqNo(orderMain.getPayNo());
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            order.setBooksNo(shopInfoDto.getBooksNo());
            order.setPreSell("0");
//            order.setExpDeliverTime(new Timestamp(Long.valueOf(orderMain.getExpShipTime())));
            switch (orderMain.getPayType()) {
                case 2:
                    order.setPayCode("13");//财付通
                    break;
                case 1:
                    order.setPayCode("02");//支付宝
                    break;
                case 3:
                    order.setPayCode("02");//银联
                    break;
                default:
                    order.setPayCode(orderMain.getPayType().toString());
            }
            order.setConsigneeName(orderMain.getReceiver());
            order.setConsigneeTel(orderMain.getTel());
            order.setProvince(orderMain.getProvince());
            order.setCity(orderMain.getCity());
            order.setDistrict(orderMain.getDistinct());
            order.setConsigneeAddr(orderMain.getProvince()+orderMain.getCity()+orderMain.getDistinct()+orderMain.getAddress());

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";

            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<QSOrderListChild> itemList = orderMain.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                for (QSOrderListChild item : itemList) {
                    if (item.getSupSkuNo()!=null){
                        // 有些商家瞎填货号有空格，直接去除
                        item.setSupSkuNo(StringUtil.removeEscape(item.getSupSkuNo()));
                    }

                    CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                    BaseSku baseSku = baseSkuService.queryByGoodsNo(item.getSupSkuNo());
                    if (baseSku == null || order.getShopId().intValue() != baseSku.getShopId().intValue()) {
                        needFreeze = true;
                        freezeReason = item.getSupSkuNo() + "未创建货品";
                    }else {
                        details.setGoodsId(baseSku.getId());
                        details.setGoodsCode(baseSku.getGoodsCode());
                    }
                    // 一期不做商品映射关系
                    details.setOrderNo(order.getOrderNo());
                    details.setFontGoodsName(item.getGoodsName());
                    if (StringUtil.isBlank(details.getGoodsCode())) {
                        details.setGoodsCode(item.getSupSkuNo());
                    }
                    details.setGoodsNo(item.getSupSkuNo());
                    details.setQty(String.valueOf(item.getNum()));
                    list.add(details);
                    // 查询商品库存信息，无库存则冻结订单
                    Integer qty = wmsSupport.queryInventoryBySku(orderMain.getOrderNo(),orderMain.getCustomerId());
                    if (qty != null && qty.intValue() == 0 ) {
                        needFreeze = true;
                        freezeReason = "商品无库存";
                    }
                }
            }
            order.setItemList(list);
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
            }else {
//                cbOrderProducer.send(
//                        MsgType.CB_ORDER_215,
//                        String.valueOf(orderDto.getId()),
//                        order.getOrderNo()
//                );
            }

        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    Long.valueOf(0),
                    String.valueOf(0),
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
}
