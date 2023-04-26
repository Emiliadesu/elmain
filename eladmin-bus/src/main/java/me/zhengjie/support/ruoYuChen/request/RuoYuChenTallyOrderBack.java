package me.zhengjie.support.ruoYuChen.request;

import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenFileUrl;

import java.util.List;

public class RuoYuChenTallyOrderBack extends RuoYuChenRequestAbs{
    /**
     * 入库单号-erp
     * Y
     */
    private String entryOrderCode;
    /**
     * 入库单号-仓库
     * N
     */
    private String entryOrderId;
    /**
     * 到货时间 yyyy-MM-dd HH:mm:ss
     * Y
     */
    private String receivDate;
    /**
     * 理货人
     * N
     */
    private String tallyMan;
    /**
     * 理货时间 yyyy-MM-dd HH:mm:ss
     * Y
     */
    private String tallyDate;
    /**
     * 计划数量 默认合计明细
     * N
     */
    private String planQty;
    /**
     * 实收数量 默认合计明细
     * N
     */
    private String actualQty;
    /**
     * 备注
     * N
     */
    private String remark;
    /**
     * 明细对象
     * Y
     */
    private List<RuoYuChenTallyOrderBackDetail> details;
    /**
     * 理货文件
     * Y
     */
    private List<RuoYuChenFileUrl>attDetails;

    public String getEntryOrderCode() {
        return entryOrderCode;
    }

    public void setEntryOrderCode(String entryOrderCode) {
        this.entryOrderCode = entryOrderCode;
    }

    public String getEntryOrderId() {
        return entryOrderId;
    }

    public void setEntryOrderId(String entryOrderId) {
        this.entryOrderId = entryOrderId;
    }

    public String getReceivDate() {
        return receivDate;
    }

    public void setReceivDate(String receivDate) {
        this.receivDate = receivDate;
    }

    public String getTallyMan() {
        return tallyMan;
    }

    public void setTallyMan(String tallyMan) {
        this.tallyMan = tallyMan;
    }

    public String getTallyDate() {
        return tallyDate;
    }

    public void setTallyDate(String tallyDate) {
        this.tallyDate = tallyDate;
    }

    public String getPlanQty() {
        return planQty;
    }

    public void setPlanQty(String planQty) {
        this.planQty = planQty;
    }

    public String getActualQty() {
        return actualQty;
    }

    public void setActualQty(String actualQty) {
        this.actualQty = actualQty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<RuoYuChenTallyOrderBackDetail> getDetails() {
        return details;
    }

    public void setDetails(List<RuoYuChenTallyOrderBackDetail> details) {
        this.details = details;
    }

    public List<RuoYuChenFileUrl> getAttDetails() {
        return attDetails;
    }

    public void setAttDetails(List<RuoYuChenFileUrl> attDetails) {
        this.attDetails = attDetails;
    }

    @Override
    public String getMethod() {
        return "TallyBack";
    }
}
