package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

public class StockTakingDetail {
    /**
     * 货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 质量等级
     */
    @JSONField(name = "quality_grade")
    private String qualityGrade;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 盘点原因
     * 盘盈
     *   仓库2C作业异常-31
     *   库存盘点-32
     *   到货差异-33
     *   海关抽样-34
     *   其他-35
     * 盘亏
     *   仓内2C作业异常 -31
     *   库存盘点-32
     *   出库异常-36
     *   海关抽样-34
     *   其他-35
     */
    @JSONField(name = "reason_code")
    private String reasonCode;

    /**
     * 具体原因
     * 选填
     */
    @JSONField(name = "reason_msg")
    private String reasonMsg;

    /**
     * 扩展信息
     */
    @JSONField(name = "extend")
    private Map<String,Object> extend;

    /**
     * 责任方
     *   1-服务商原因
     *   2-商家原因
     *   3-监管原因
     *   4-服务商原因（无需赔付）
     */
    @JSONField(name = "duty")
    private String duty;

    /**
     * 备注
     */
    @JSONField(name = "remark")
    private String remark;

    /**
     * 证明材料
     */
    @JSONField(name = "evidence")
    private List<String> evidence;

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonMsg() {
        return reasonMsg;
    }

    public void setReasonMsg(String reasonMsg) {
        this.reasonMsg = reasonMsg;
    }

    public Map<String, Object> getExtend() {
        return extend;
    }

    public void setExtend(Map<String, Object> extend) {
        this.extend = extend;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
}
