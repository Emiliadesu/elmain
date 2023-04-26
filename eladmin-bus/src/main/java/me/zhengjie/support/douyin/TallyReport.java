package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class TallyReport {
    /**
     * 理货单id
     */
    @JSONField(name = "tally_order_id")
    private String tallyOrderId;

    /**
     * 报关单号
     */
    @JSONField(name = "customs_order_id")
    private String customsOrderId;

    /**
     * 理货总件数
     */
    @JSONField(name = "tally_total_qty")
    private Integer tallyTotalQty;

    /**
     * 报关数量
     */
    @JSONField(name = "customs_qty")
    private Integer customsQty;

    /**
     * 出库理货日期戳 秒
     */
    @JSONField(name = "outbound_tally_date")
    private Long outboundTallyDate;

    /**
     * 到货时间戳 秒
     */
    @JSONField(name = "arrival_date")
    private Long arrivalDate;

    /**
     * 理货完成时间戳 秒
     */
    @JSONField(name = "tally_completed_time")
    private Long tallyCompletedTime;

    /**
     * 理货货品明细
     */
    @JSONField(name = "tally_report_cargo_details")
    private List<TallyReportCarGoDetail> tallyReportCargoDetails;

    public String getTallyOrderId() {
        return tallyOrderId;
    }

    public void setTallyOrderId(String tallyOrderId) {
        this.tallyOrderId = tallyOrderId;
    }

    public String getCustomsOrderId() {
        return customsOrderId;
    }

    public void setCustomsOrderId(String customsOrderId) {
        this.customsOrderId = customsOrderId;
    }

    public Integer getTallyTotalQty() {
        return tallyTotalQty;
    }

    public void setTallyTotalQty(Integer tallyTotalQty) {
        this.tallyTotalQty = tallyTotalQty;
    }

    public Integer getCustomsQty() {
        return customsQty;
    }

    public void setCustomsQty(Integer customsQty) {
        this.customsQty = customsQty;
    }

    public Long getOutboundTallyDate() {
        return outboundTallyDate;
    }

    public void setOutboundTallyDate(Long outboundTallyDate) {
        this.outboundTallyDate = outboundTallyDate;
    }

    public Long getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Long arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Long getTallyCompletedTime() {
        return tallyCompletedTime;
    }

    public void setTallyCompletedTime(Long tallyCompletedTime) {
        this.tallyCompletedTime = tallyCompletedTime;
    }

    public List<TallyReportCarGoDetail> getTallyReportCargoDetails() {
        return tallyReportCargoDetails;
    }

    public void setTallyReportCargoDetails(List<TallyReportCarGoDetail> tallyReportCargoDetails) {
        this.tallyReportCargoDetails = tallyReportCargoDetails;
    }
}
