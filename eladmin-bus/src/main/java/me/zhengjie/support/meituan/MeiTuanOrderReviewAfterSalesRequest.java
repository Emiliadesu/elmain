package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanOrderReviewAfterSalesRequest implements CommonApiParam {
    /**
     * 订单展示id
     */
    @JSONField(name = "wm_order_id_view")
    private String wmOrderIdView;

    /**
     * 审核当前售后单的结果类型
     */
    @JSONField(name = "review_type")
    private String reviewType;

    /**
     * 退货退款”驳回退货的原因
     * 审核结果类型=2时，必传。其他情况忽略
     */
    @JSONField(name = "reject_reason_code")
    private String rejectReasonCode;

    /**
     * 驳回原因说明
     * 驳回原因编码为-1时，必须传此参数，其他情况忽略
     */
    @JSONField(name = "reject_other_reason")
    private String rejectOtherReason;


    public String getWmOrderIdView() {
        return wmOrderIdView;
    }

    public void setWmOrderIdView(String wmOrderIdView) {
        this.wmOrderIdView = wmOrderIdView;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public String getRejectReasonCode() {
        return rejectReasonCode;
    }

    public void setRejectReasonCode(String rejectReasonCode) {
        this.rejectReasonCode = rejectReasonCode;
    }

    public String getRejectOtherReason() {
        return rejectOtherReason;
    }

    public void setRejectOtherReason(String rejectOtherReason) {
        this.rejectOtherReason = rejectOtherReason;
    }

    @Override
    public String getMethod() {
        return "/api/v1/ecommerce/order/reviewAfterSales";
    }

    @Override
    public String getKeyWord() {
        return getWmOrderIdView();
    }
}
