package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.PullOrderLog;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.support.aikucun.response.AikucunDeliveryHaitaoOrderListResponse;
import me.zhengjie.support.aikucun.response.common.AikucunCommonResponse;

public interface AikucunService {

    void aiKuCunPullOrder();

    void pullOrder(ShopToken shopToken, ShopInfo shopInfo, PullOrderLog pullTime);

    void handleOrder(AikucunCommonResponse<AikucunDeliveryHaitaoOrderListResponse> res);

    void pullOrderByOrderNo(String[] orderNos,ShopToken shopToken) throws Exception;

    void createOrder(String body) throws Exception;

    void confirmDeliver(String orderId);

    void confirmDeliver(CrossBorderOrder order);

    void pullOrderByPage(String body);

    void confirmOrder(CrossBorderOrder crossBorderOrder);

    void confirmOrder(String orderId);

    void confirmPack(CrossBorderOrder crossBorderOrder);

    void confirmPack(String orderId);

    Integer getOrderStatus(String orderNo, Long shopId);
}
