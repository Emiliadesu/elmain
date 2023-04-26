package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;
/**
 * 服务商向平台回告入库和提货出库信息
 * */
public class WarehouseInOutboundEventRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "crossborder.warehouseInOutboundEvent";
    }

    @Override
    public String getKeyWord() {
        return this.getSpOrderNo();
    }

    /**
     * 单据状态
     * 必填
     */
    @JSONField(name = "in_outbound_event_type")
    private Integer inOutboundEventType;

    /**
     * 来源单据号
     * 必填
     */
    @JSONField(name = "source_order_no")
    private String sourceOrderNo;

    /**
     * 服务商单据编号
     * 选填
     */
    @JSONField(name = "sp_order_no")
    private String spOrderNo;

    /**
     * 实操时间戳
     * 单位：毫秒
     * 必填
     */
    @JSONField(name = "occurrence_time")
    private Long occurrenceTime;

    /**
     * 理货报告附件url，当回告节点为理货完成时，必传
     */
    @JSONField(name = "tally_report_url")
    private List<TallyReportUrl>tallyReportUrlList;

    /**
     * 入库货品信息，回传入库相关节点时，必传
     */
    @JSONField(name = "inbound_cargo_infos")
    private List<InboundCargoInfo>inboundCargoInfos;

    /**
     * 出库货品信息，回传出库相关节点时，必传
     */
    @JSONField(name = "outbound_cargo_infos")
    private List<OutboundCargoInfo>outboundCargoInfos;

    /**
     * 异常原因
     * 选填
     */
    @JSONField(name = "error_reason")
    private String errorReason;

    /**
     * 服务商编码
     */
    private String vendor;

    @JSONField(name = "tally_report")
    private TallyReport tallyReport;

    public Integer getInOutboundEventType() {
        return inOutboundEventType;
    }

    public void setInOutboundEventType(Integer inOutboundEventType) {
        this.inOutboundEventType = inOutboundEventType;
    }

    public String getSourceOrderNo() {
        return sourceOrderNo;
    }

    public void setSourceOrderNo(String sourceOrderNo) {
        this.sourceOrderNo = sourceOrderNo;
    }

    public String getSpOrderNo() {
        return spOrderNo;
    }

    public void setSpOrderNo(String spOrderNo) {
        this.spOrderNo = spOrderNo;
    }

    public Long getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(Long occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public List<TallyReportUrl> getTallyReportUrlList() {
        return tallyReportUrlList;
    }

    public void setTallyReportUrlList(List<TallyReportUrl> tallyReportUrlList) {
        this.tallyReportUrlList = tallyReportUrlList;
    }

    public List<InboundCargoInfo> getInboundCargoInfos() {
        return inboundCargoInfos;
    }

    public void setInboundCargoInfos(List<InboundCargoInfo> inboundCargoInfos) {
        this.inboundCargoInfos = inboundCargoInfos;
    }

    public List<OutboundCargoInfo> getOutboundCargoInfos() {
        return outboundCargoInfos;
    }

    public void setOutboundCargoInfos(List<OutboundCargoInfo> outboundCargoInfos) {
        this.outboundCargoInfos = outboundCargoInfos;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public TallyReport getTallyReport() {
        return tallyReport;
    }

    public void setTallyReport(TallyReport tallyReport) {
        this.tallyReport = tallyReport;
    }
}
