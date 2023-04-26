package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouIdCard {
    /**
     * 证件类型
     */
    @JSONField(name = "receiver_id_type")
    private Integer receiverIdType;
    /**
     * 证件号
     */
    @JSONField(name = "receiver_id_no")
    private String receiverIdNo;
    /**
     * 证件姓名
     */
    @JSONField(name = "receiver_name")
    private String receiverName;
    /**
     * 身份证一致性检测是否通过
     */
    @JSONField(name = "id_card_check_result")
    private Boolean idCardCheckResult;

    public int getReceiverIdType() {
        return receiverIdType;
    }

    public void setReceiverIdType(int receiverIdType) {
        this.receiverIdType = receiverIdType;
    }

    public String getReceiverIdNo() {
        return receiverIdNo;
    }

    public void setReceiverIdNo(String receiverIdNo) {
        this.receiverIdNo = receiverIdNo;
    }

    public void setReceiverIdType(Integer receiverIdType) {
        this.receiverIdType = receiverIdType;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public Boolean getIdCardCheckResult() {
        return idCardCheckResult;
    }

    public void setIdCardCheckResult(Boolean idCardCheckResult) {
        this.idCardCheckResult = idCardCheckResult;
    }
}
