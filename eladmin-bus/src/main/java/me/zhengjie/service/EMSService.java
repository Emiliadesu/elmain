package me.zhengjie.service;

import cn.hutool.json.JSONObject;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.support.chinaPost.OrderCreateResponse;

public interface EMSService {

    //下单取号
    void getOrderNumber(CrossBorderOrder crossBorderOrder);


    //运单申报
    void getDecinfo(CrossBorderOrder crossBorderOrder);
}
