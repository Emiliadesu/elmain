package me.zhengjie.support.youzan;

import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetResult;

public class YouzanFullOrderInfo extends YouzanTradeGetResult.YouzanTradeGetResultData {
    private Long lShopId;
    private Long custId;

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }
}
