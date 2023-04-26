package me.zhengjie.service;

import me.zhengjie.domain.*;
import me.zhengjie.rest.model.jackYun.*;
import me.zhengjie.support.jackYun.JackYunBasicResponse;

public interface JackYunService {
    JackYunDeliveryOrderCreateResponse createOrder(JackYunDeliveryOrderCreateRequest request,String customerId);
    JackYunSingleitemSynchronizeResponse syncGoodsInfo(JackYunSingleItemSynchronizeRequest request);
    JackYunBasicResponse cancelOrder(JackYunCancelOrderRequest request);

    JackYunBasicResponse msgPush(String method, String content, String customerid);

    void confirmUp(InboundOrder inboundOrder, InboundOrderLog log);

    void confirmOut(OutboundOrder outboundOrder, OutboundOrderLog log);

    void deliver(CrossBorderOrder crossBorderOrder);
}
