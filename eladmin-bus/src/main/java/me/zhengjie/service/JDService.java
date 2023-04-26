package me.zhengjie.service;

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopToken;

public interface JDService {

    //下单取号
    void getOrderNumber(CrossBorderOrder crossBorderOrder);

    //运单申报
    void getDecinfo(CrossBorderOrder crossBorderOrder);

    //刷新token
     void refreshToken() throws Exception;

     //查询运单轨迹
    void queryOrder(String orderNo);
}
