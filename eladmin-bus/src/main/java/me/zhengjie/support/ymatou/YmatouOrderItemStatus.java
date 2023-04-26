package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouOrderItemStatus {
    @JSONField(name = "refund_status")
    private Integer refundStatus;
    @JSONField(name = "sku_id")
    private String skuId;
    @JSONField(name = "outer_sku_id")
    private String outerSkuId;
    @JSONField(name = "product_id")
    private String productId;

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getOuterSkuId() {
        return outerSkuId;
    }

    public void setOuterSkuId(String outerSkuId) {
        this.outerSkuId = outerSkuId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
