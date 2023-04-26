package me.zhengjie.support.youzan;

import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;

public class YouZanSoldFullOrderInfo extends YouzanTradesSoldGetResult.YouzanTradesSoldGetResultFullorderinfolist {
    private Long custId;
    private Long lShopId;

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }
}
