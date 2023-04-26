package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouPaymentPushResponse {
    @JSONField(name = "result")
    private YmatouPaymentPushResult result;

    public YmatouPaymentPushResult getResult() {
        return result;
    }

    public void setResult(YmatouPaymentPushResult result) {
        this.result = result;
    }
}
