package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.support.oms.OrderMain;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

public interface OmsService {

    void pushOrder(@Valid OrderMain orderMain);

    void cancelOrder(String orderNo, String remark, Long customerId) throws Exception;

    void confirmOrder(CrossBorderOrder crossBorderOrder);

    void confirmClearStart(CrossBorderOrder crossBorderOrder);

    void confirmClearSuccess(CrossBorderOrder crossBorderOrder);

    void confirmDeliver(CrossBorderOrder crossBorderOrder);

    void confirmCleanErr(CrossBorderOrder crossBorderOrder);
}
