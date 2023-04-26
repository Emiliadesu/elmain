package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;

public interface QSService {

    void pullOrder();

    void pullOrderByPage(String body) throws Exception;

    void createOrder(String body);

    void confirmClearErr(CrossBorderOrder order) throws Exception;

    void confirmDeliver(CrossBorderOrder order) throws Exception;

    void QSStockUpdate(CrossBorderOrder order) throws Exception;

    String getOrderStatus(String orderNo, String shopCode) throws Exception;
}
