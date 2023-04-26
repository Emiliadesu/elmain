package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public class CrossborderMsgPush {
    @JsonProperty(value = "msg_type",required = true)
    @NotNull
    @JSONField(name = "msg_type")
    private String msgType;

    @JsonProperty(value = "logid")
    @JSONField(name = "logid")
    private String logId;

    @JsonProperty(value = "order_push")
    @JSONField(name = "order_push")
    private OrderPush orderPush;

    @JsonProperty(value = "inbound_bill_push")
    @JSONField(name = "inbound_bill_push")
    private InboundBillPush inboundBillPush;

    @JsonProperty(value = "lading_bill_push")
    @JSONField(name = "lading_bill_push")
    private LadingBillPush ladingBillPush;

    @JsonProperty(value = "order_interception_push")
    @JSONField(name = "order_interception_push")
    private OrderInterceptionPush orderInterceptionPush;

    @JsonProperty(value = "take_logistics_info_push")
    @JSONField(name = "take_logistics_info_push")
    private TakeLogisticsInfoPush takeLogisticsInfoPush;

    @JsonProperty(value = "order_info_notify")
    @JSONField(name = "order_info_notify")
    private OrderInfoNotify orderInfoNotify;

    @JsonProperty(value = "order_interception_notify")
    @JSONField(name = "order_interception_notify")
    private OrderInterceptionNotify orderInterceptionNotify;

    @JsonProperty(value = "tally_order_review")
    @JSONField(name = "tally_order_review")
    private TallyOrderReview tallyOrderReview;

    @JsonProperty(value = "reverse_inbound_order")
    @JSONField(name = "reverse_inbound_order")
    private ReverseInboundOrder reverseInboundOrder;

    @JsonProperty(value = "reverse_outbound_order")
    @JSONField(name = "reverse_outbound_order")
    private ReverseOutboundOrder reverseOutboundOrder;

    @JsonProperty(value = "create_warehouse_fee_order")
    @JSONField(name = "create_warehouse_fee_order")
    private CreateWarehouseFeeOrder createWarehouseFeeOrder;

    @JsonProperty("notify_adjust_result")
    @JSONField(name = "notify_adjust_result")
    private NotifyAdjustResult notifyAdjustResult;

    @JsonProperty("push_sale_return_order")
    @JSONField(name = "push_sale_return_order")
    private SaleReturnOrder saleReturnOrder;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public OrderPush getOrderPush() {
        return orderPush;
    }

    public void setOrderPush(OrderPush orderPush) {
        this.orderPush = orderPush;
    }

    public InboundBillPush getInboundBillPush() {
        return inboundBillPush;
    }

    public void setInboundBillPush(InboundBillPush inboundBillPush) {
        this.inboundBillPush = inboundBillPush;
    }

    public LadingBillPush getLadingBillPush() {
        return ladingBillPush;
    }

    public void setLadingBillPush(LadingBillPush ladingBillPush) {
        this.ladingBillPush = ladingBillPush;
    }

    public OrderInterceptionPush getOrderInterceptionPush() {
        return orderInterceptionPush;
    }

    public void setOrderInterceptionPush(OrderInterceptionPush orderInterceptionPush) {
        this.orderInterceptionPush = orderInterceptionPush;
    }

    public TakeLogisticsInfoPush getTakeLogisticsInfoPush() {
        return takeLogisticsInfoPush;
    }

    public void setTakeLogisticsInfoPush(TakeLogisticsInfoPush takeLogisticsInfoPush) {
        this.takeLogisticsInfoPush = takeLogisticsInfoPush;
    }

    public OrderInfoNotify getOrderInfoNotify() {
        return orderInfoNotify;
    }

    public void setOrderInfoNotify(OrderInfoNotify orderInfoNotify) {
        this.orderInfoNotify = orderInfoNotify;
    }

    public OrderInterceptionNotify getOrderInterceptionNotify() {
        return orderInterceptionNotify;
    }

    public void setOrderInterceptionNotify(OrderInterceptionNotify orderInterceptionNotify) {
        this.orderInterceptionNotify = orderInterceptionNotify;
    }

    public ReverseInboundOrder getReverseInboundOrder() {
        return reverseInboundOrder;
    }

    public void setReverseInboundOrder(ReverseInboundOrder reverseInboundOrder) {
        this.reverseInboundOrder = reverseInboundOrder;
    }

    public ReverseOutboundOrder getReverseOutboundOrder() {
        return reverseOutboundOrder;
    }

    public void setReverseOutboundOrder(ReverseOutboundOrder reverseOutboundOrder) {
        this.reverseOutboundOrder = reverseOutboundOrder;
    }

    public TallyOrderReview getTallyOrderReview() {
        return tallyOrderReview;
    }

    public void setTallyOrderReview(TallyOrderReview tallyOrderReview) {
        this.tallyOrderReview = tallyOrderReview;
    }

    public CreateWarehouseFeeOrder getCreateWarehouseFeeOrder() {
        return createWarehouseFeeOrder;
    }

    public void setCreateWarehouseFeeOrder(CreateWarehouseFeeOrder createWarehouseFeeOrder) {
        this.createWarehouseFeeOrder = createWarehouseFeeOrder;
    }

    public NotifyAdjustResult getNotifyAdjustResult() {
        return notifyAdjustResult;
    }

    public void setNotifyAdjustResult(NotifyAdjustResult notifyAdjustResult) {
        this.notifyAdjustResult = notifyAdjustResult;
    }

    public SaleReturnOrder getSaleReturnOrder() {
        return saleReturnOrder;
    }

    public void setSaleReturnOrder(SaleReturnOrder saleReturnOrder) {
        this.saleReturnOrder = saleReturnOrder;
    }
}
