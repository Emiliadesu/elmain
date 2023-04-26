package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class TallyOrderReview {
    /**
     * 理货报告id
     */
    @JSONField(name = "tally_order_id")
    private String tallyOrderId;

    /**
     * 1通过/0驳回
     */
    private Long res;

    /**
     * 驳回原因
     */
    private String remark;

    /**
     * 来源单据号
     */
    @JSONField(name = "source_order_no")
    private String sourceOrderNo;

    public String getTallyOrderId() {
        return tallyOrderId;
    }

    public void setTallyOrderId(String tallyOrderId) {
        this.tallyOrderId = tallyOrderId;
    }

    public Long getRes() {
        return res;
    }

    public void setRes(Long res) {
        this.res = res;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSourceOrderNo() {
        return sourceOrderNo;
    }

    public void setSourceOrderNo(String sourceOrderNo) {
        this.sourceOrderNo = sourceOrderNo;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
