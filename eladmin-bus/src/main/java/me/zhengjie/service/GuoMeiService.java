package me.zhengjie.service;

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.CrossBorderOrder;

public interface GuoMeiService {

    //接收国美订单
    void createOrder(JSONObject object);

    //接单回传
    void confirmOrder(CrossBorderOrder order);

    //清关作业状态回传
    void confirmClearStart(CrossBorderOrder order);

    //拣货回传
    void confirmPickStart(CrossBorderOrder order);

    //拣货回传完成
    void confirmPickEnd(CrossBorderOrder order);

    //回传打包
    void confirmPack(CrossBorderOrder order);

    //出库节点回传
    void confirmDeliver(CrossBorderOrder order);

    //拦截成功
    void confirmInterceptionSucc(CrossBorderOrder order,Integer cancelStatus);

    //拦截失败
    void confirmInterceptionErr(CrossBorderOrder order,Integer cancelStatus);

//    void orderCancel(CrossBorderOrder order);

    //更改订购人信息
    void updateOrder(JSONObject object);

    //接收国美订单取消
    void orderCancel(JSONObject object);

    //清关回传异常
    void confirmClearErr(String orderNo)throws Exception;

    //清关回传成功
    void confirmClearSuccess(CrossBorderOrder order) throws Exception;

    //接单失败
    void confirmOrderErr(CrossBorderOrder order);

    void orderCancelBySys(CrossBorderOrder order,Integer cancelStatus);

    Integer guomeiOrderCancel(CrossBorderOrder order);

    void orderCancelByMq(String body);
}
