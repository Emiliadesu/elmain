package me.zhengjie.support.youzan;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult;

public class YouzanPayCustomsDeclarationReportpaymentReportResponse extends YouzanPayCustomsDeclarationReportpaymentReportResult {
    @JSONField(name = "gw_err_resp")
    private GwErrResp gwErrResp;

    public GwErrResp getGwErrResp() {
        return gwErrResp;
    }

    public void setGwErrResp(GwErrResp gwErrResp) {
        this.gwErrResp = gwErrResp;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
