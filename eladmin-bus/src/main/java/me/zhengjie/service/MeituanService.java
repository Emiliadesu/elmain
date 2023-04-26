package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.rest.model.meituan.MeiTuanCancel;
import me.zhengjie.rest.model.meituan.MeiTuanOrder;
import me.zhengjie.rest.model.meituan.MeiTuanRefund;
import me.zhengjie.support.meituan.MeiTuanCrossBorderDetail;

public interface MeituanService {
    void createOrder(MeiTuanOrder order) throws Exception;

    void cancelOrder(MeiTuanCancel cancel);

    void refundReject(String body) throws Exception;

    void confirmOrder(String body) throws Exception;

    void getCrossBorderInfo(String body) throws Exception;

    void refundAgree(String body) throws Exception;

    void refundOrder(MeiTuanRefund refund);

    void cancelOrder(String body);

    void confirmOrder(CrossBorderOrder order);

    void getCrossBorderInfo(CrossBorderOrder order);

    void setDeclareInfo(MeiTuanCrossBorderDetail detail) throws Exception;

    void setDeclareInfo(String body) throws Exception;

    Integer getOrderStatus(String orderNo, Long shopId);

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    void refundOrder(String body) throws Exception;

    void createOrder(String body) throws Exception;
}
