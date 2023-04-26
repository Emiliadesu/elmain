package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.ShopToken;

import java.util.Date;

public interface YmatouService {
    void ymatouPullOrder();

    void pullOrderByHours(Integer hours);

    void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo) throws Exception;

    void pullOrderByOrderNo(String[] orderNos, ShopToken shopToken) throws Exception;

    Integer getOrderStatus(String orderNo, String shopCode) throws Exception;

    void confirmDeliver(String orderId) throws Exception;

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    void pullOrderByPage(String body) throws Exception;

    void createOrder(String body) throws Exception;

    void pushPayOrder(CrossBorderOrder order) throws Exception;

    void confirmOrder(CrossBorderOrder order);

    long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception;

    String[] rePushPayOrder(CrossBorderOrder order) throws Exception;
}
