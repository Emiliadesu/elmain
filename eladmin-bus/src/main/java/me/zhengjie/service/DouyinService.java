package me.zhengjie.service;

import me.zhengjie.domain.*;
import me.zhengjie.rest.model.douyin.*;

/**
 * 抖音服务接口
 */
public interface DouyinService {

    /**
     * 抖音按照时间拉单
     */
    void pullOrder();

    /**
     * 保存订单(从MQ消费者获取订单报文)
     * @param body
     */
    void createOrder(String body);

    /**
     * 根据订单号拉单
     * @param shopId
     * @param orderId
     * @throws Exception
     */
    void pullOrderById(String shopId, String orderId) throws Exception;

    void confirmOrder(CrossBorderOrder order) throws Exception;

    void confirmClearStart(CrossBorderOrder crossBorderOrder) throws Exception;
    /**
     * 清关异常回传
     * @param orderId
     */
    void confirmClearErr(String orderId) throws Exception;

    void confirmClearSuccess(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmPackByTool(CrossBorderOrder order) throws Exception;

    /**
     * 回传打包
     * @param crossBorderOrder
     */
    void confirmPack(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    // 拦截成功
    void confirmInterceptionSucc(CrossBorderOrder order) throws Exception;

    // 拦截失败
    void confirmInterceptionErr(CrossBorderOrder order) throws Exception;

    /**
     * 查询平台的订单状态
     * @return
     */
    Integer getStatus(CrossBorderOrder order) throws Exception;

    Integer getUpStatus(CrossBorderOrder order, ShopToken shopToken) throws Exception;

    /**
     * 将面单信息推送到WMS系统
     * @param body
     */
    void pushPrintInfoToWms(String body);

    void confirmOrderByTools(CrossBorderOrder order) throws Exception;

    /**
     * 接收退货订单
     * @param data
     */
    void recReturnOrder(String data);

    void confirmReturnBook(String body) throws Exception;

    void confirmReturnCheck(String body) throws Exception;

    void confirmReturnDecStart(String body) throws Exception;

    void dyConfirmOrder(InboundOrder inboundOrder, InboundOrderLog log) throws Exception;

    void dyConfirmArrive(InboundOrder inboundOrder,InboundOrderLog log) throws Exception;

    void dyConfirmStockTally(InboundOrder inboundOrder,InboundOrderLog log) throws Exception;

    void dyConfirmStockedTally(InboundOrder inboundOrder,InboundOrderLog log) throws Exception;

    void dyConfirmUp(InboundOrder inboundOrder,InboundOrderLog log) throws Exception;

    void dyConfirmCancel(InboundOrder inboundOrder,InboundOrderLog log) throws Exception;

    void dyConfirmCancel(OutboundOrder outboundOrder,OutboundOrderLog log) throws Exception;

    void dyConfirmOut(OutboundOrder outboundOrder,OutboundOrderLog log) throws Exception;

    void dyConfirmOrder(OutboundOrder outboundOrder,OutboundOrderLog log) throws Exception;

    void dyConfirmStockedTally(OutboundOrder outboundOrder, OutboundOrderLog log) throws Exception;

    void dyConfirmStockTally(OutboundOrder outboundOrder, OutboundOrderLog log) throws Exception;

    void confirmReturnDecEnd(String body) throws Exception;

    void confirmReturnGround(String body) throws Exception;

    //刷新TOKEN
    void refreshToken(ShopToken shopToken) throws Exception;

    void getToken(String code) throws Exception;

    void reportHourOrder();

    Long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception;

    void confirmTally(String s) throws Exception;

    void confirmOutReturnGround(String s) throws Exception;

    void confirmReturnCheckErr(String s) throws Exception;

    void pullOrderByPage(String body) throws Exception ;

    void confirmPickStart(CrossBorderOrder order) throws Exception;

    void confirmPickEnd(CrossBorderOrder order)throws Exception;

    void confirmClearStartByTools(CrossBorderOrder order) throws Exception;

    void confirmClearSuccessByTools(CrossBorderOrder order) throws Exception;

    void pullOrderDYDetails();

    void confirmReturnDecErr(String s) throws Exception;

    void crossborderMsgPush(CrossborderMsgPush crossborderMsgPush)throws Exception;

    void createWarehouseFeeOrder(String body);

    void notifyAdjustResult(String body);

    void getMailNo();

    void pushStockTransform(DyStockTransform dyStockTransform) throws Exception;

    void pushStockTaking(DyStockTaking dyStockTaking) throws Exception;

    void pullOrderByOrderNo(String body) throws Exception;

    void pullOrderByShop(Long shopId, String start, String end);

    void orderInterception(String body) throws Exception;

    void getMailNo(String body)throws Exception;

    void lockOrder(String body) throws Exception;

    void confirmOrderErr(CrossBorderOrder order) throws Exception;

    void cancelOrder(String body) throws Exception;

    void lockOrder(CrossBorderOrder order) throws Exception;

    void refreshToken();

    void createSaleReturnOrder(SaleReturnOrder saleReturnOrder);

    void confirmReturnByTools(OrderReturn orderReturn, String status) throws Exception;

    ReconciliationRespData inventoryReconciliation(Reconciliation reconciliation);

    void confirmDelClearSuccess(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmDelClearStart(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmCloseClearSuccess(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmDelClearFail(CrossBorderOrder crossBorderOrder) throws Exception;

    void syncInventorySnapshot();

    void syncInventoryLogFlow();

    YuncWmsInventoryQueryResponse yuncWmsInventoryQuery(YuncWmsInventoryQueryRequest queryRequest);

    void createWarehouseFeeOrderPush(DyCangzuFee dyCangzuFee);
}
