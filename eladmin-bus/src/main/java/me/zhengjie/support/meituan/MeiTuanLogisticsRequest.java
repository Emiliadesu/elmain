package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanLogisticsRequest implements CommonApiParam {
    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    /**
     * 商家使用的快递公司，code及快递的枚举值：
     * 10001-顺丰，10002-达达，10003-闪送，10004-蜂鸟，10008-点我达，10010-生活半径，10017-其他，10018-圆通，
     * 10020-申通，10021-中通，10022-百世汇通，10023-韵达，10024-EMS，10025-邮政包裹，10026-宅急送，10027-天天，10028-如风达，10029-全峰快递，
     * 10030-优速物流，10031-京东快递，10032-美团跑腿，10033-安能物流，10034-德邦物流，10035-极兔速递，10036-中通快运，10037-众邮快递
     */
    @JSONField(name = "logistics_provider_code")
    private String logisticsProviderCode;

    /**
     * 配送物流单号，支持字母或数字，最长不超过25位。
     */
    @JSONField(name = "logistics_code")
    private String logisticsCode;

    /**
     * 收件人手机号， 如为顺丰物流，上述收件人手机号必填，否则无法正常订阅物流更新信息，其他快递方式目前暂不校验此字段
     */
    @JSONField(name = "recipient_phone")
    private String recipientPhone;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getLogisticsProviderCode() {
        return logisticsProviderCode;
    }

    public void setLogisticsProviderCode(String logisticsProviderCode) {
        this.logisticsProviderCode = logisticsProviderCode;
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    @Override
    public String getMethod() {
        return "/api/v1/ecommerce/order/logistics/btoc/sync";
    }

    @Override
    public String getKeyWord() {
        return getOrderId();
    }
}
