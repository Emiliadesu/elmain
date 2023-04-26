package me.zhengjie.service;

import cn.hutool.json.JSONObject;
import me.zhengjie.domain.SortingLineChuteCode;
import me.zhengjie.rest.model.OutboundBatchDeliverOrder;
import me.zhengjie.utils.enums.OrderDeliverStatusEnum;

import java.util.List;
import java.util.Map;

public interface OrderDeliverService {

    /**
     * 请求出库，返回出库格口
     * @param weight
     * @param mailNo
     * @return
     */
    OrderDeliverStatusEnum deliver(String weight, String mailNo, String userid);

    Boolean packageInspection(String packageCode, String mailNo);

    SortingLineChuteCode sortOnly(String mailNo);

    /**
     * 出库请求V2版
     * @param weight
     * @param mailNo
     * @param userId
     */
    SortingLineChuteCode deliverV2(String weight, String mailNo, String userId);

    String chuteCodeToXml(SortingLineChuteCode chuteCode);

    SortingLineChuteCode deliverDwV2(String weight, String mailNo, String userId);

    SortingLineChuteCode deliverDYV2(String weight, String mailNo, String userid);

    void addOrderMaterial(JSONObject dwResObject);

    List<OutboundBatchDeliverOrder> getOutboundBatchDeliverOrder(String waveNo);

    List<Map<String, Object>> getOutboundBatchDeliverOrder2(String waveNo);

    SortingLineChuteCode deliverTest(String weight, String mailNo, String userid);
}
