package me.zhengjie.service;

public interface MqCBOrderCommonService {
    void pullOrderByPage(String body);

    void createOrder(String body);

    void pushPrintInfoToWms(String body);

    void confirmOrder(String orderId) throws Exception;

    void confirmClearStart(String orderId) throws Exception;

    void confirmClearSuccess(String orderId) throws Exception;

    void confirmDelClearStart(String orderId) throws Exception;

    void confirmDelClearSuccess(String orderId) throws Exception;

    void confirmDelClearFail(String orderId) throws Exception;

    void confirmCloseClearSuccess(String orderId) throws Exception;

    void confirmDeliver(String orderId) throws Exception;

    void confirmPickStart(String body) throws Exception;

    void confirmPickEnd(String body) throws Exception ;

    void confirmWeight(String body) throws Exception ;

    void pushPayOrder(String body) throws Exception;

    void confirmPack(String body) throws Exception;
}
