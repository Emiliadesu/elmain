package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.ShopToken;

import java.util.Date;

public interface YouZanOrderService {
    void youZanPullOrder();

    void pullOrderByHours(Integer hours);

    String getOrderStatus(String orderNo, String shopCode) throws Exception;

    void pullOrderByOrderNo(String[] orderSns, ShopToken shopToken) throws Exception;

    void confirmDeliver(String orderId) throws Exception;

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    void pullOrderByPage(String body) throws Exception;

    void createOrder(String body) throws Exception;

    void pushPayOrder(CrossBorderOrder order) throws Exception;

    void orderDecrypt(CrossBorderOrder order) throws Exception;

    void orderEncrypt(CrossBorderOrder order) throws Exception;

    void decryptMask(CrossBorderOrder order) throws Exception;

    void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo) throws Exception;

    void confirmOrder(CrossBorderOrder crossBorderOrder)throws Exception;

    long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception;

    String rePushPayOrder(CrossBorderOrder order) throws Exception;
}
