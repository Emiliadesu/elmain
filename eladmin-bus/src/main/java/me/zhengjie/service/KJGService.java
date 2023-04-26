package me.zhengjie.service;

import me.zhengjie.domain.InboundOrder;

/**
 * 跨境购服务类
 */
public interface KJGService {
    String msgPush(String msgtype, String msg);

    String handleInCancel(String msg);

    String handleSku(String msg);

    String handleIn(String msg);

    String handleStatus(String msg);

    String handleOrder(String msg);

    String handleOrderCancel(String msg);

    void confirmInSucc(InboundOrder order);
}
