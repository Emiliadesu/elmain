package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.ShopToken;

import java.util.Date;

public interface MoGuJieService {
    void moGuJiePullOrder();

    void pullOrderByHours(Integer hours);

    String getOrderStatus(String orderNo, Long shopId) throws Exception;

    void pullOrderByOrderNo(String[] orderNos, ShopToken shopToken) throws Exception;

    void confirmDeliver(String orderId) throws Exception;

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    void pullOrderByPage(String body) throws Exception;

    void createOrder(String body) throws Exception;

    void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo) throws Exception;

    void confirmOrder(CrossBorderOrder order);

    long queryShopTotalOrder(String startTime, String endTime, Long shopId);

    void refreshToken(ShopToken shopToken) throws Exception;
}
