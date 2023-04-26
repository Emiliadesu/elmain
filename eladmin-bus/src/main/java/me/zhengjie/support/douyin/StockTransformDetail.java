package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;
import java.util.Map;

public class StockTransformDetail {
    /**
     * 货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 调整前质量等级
     */
    @JSONField(name = "from_grade")
    private Integer fromGrade;

    /**
     * 调整后质量等级
     */
    @JSONField(name = "to_grade")
    private Integer toGrade;

    /**
     * 调整数量
     */
    private Integer quantity;

    /**
     * 调整原因编码
     * 正转残
     *   库内异常致残-51
     *   效期原因-52
     * 残转正
     *   商品修复后转良-53
     */
    @JSONField(name = "reason_code")
    private Integer reasonCode;

    /**
     * 扩展信息
     */
    @JSONField(name = "extend")
    private Map<String,Object> extend;

    /**
     * 具体原因
     */
    @JSONField(name = "reason_msg")
    private String reasonMsg;

    /**
     * 责任方
     *   1-服务商原因
     *   2-商家原因
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

    public Integer getFromGrade() {
        return fromGrade;
    }

    public void setFromGrade(Integer fromGrade) {
        this.fromGrade = fromGrade;
    }

    public Integer getToGrade() {
        return toGrade;
    }

    public void setToGrade(Integer toGrade) {
        this.toGrade = toGrade;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(Integer reasonCode) {
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
