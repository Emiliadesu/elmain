package me.zhengjie.support.pdd;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class PddSyncTrackRequest implements PddCommonRequest{

    private TrackBody orderDeclare;

    private ErrMsg other;

    public TrackBody getOrderDeclare() {
        return orderDeclare;
    }

    public void setOrderDeclare(TrackBody orderDeclare) {
        this.orderDeclare = orderDeclare;
    }

    public ErrMsg getOther() {
        return other;
    }

    public void setOther(ErrMsg other) {
        this.other = other;
    }

    public static class TrackBody{
        private String statusCode;

        private String platformOrderNo;

        private String createTime;

        private String logisticsNo;

        private String logisticsCode;

        public String getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(String statusCode) {
            this.statusCode = statusCode;
        }

        public String getPlatformOrderNo() {
            return platformOrderNo;
        }

        public void setPlatformOrderNo(String platformOrderNo) {
            this.platformOrderNo = platformOrderNo;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getLogisticsNo() {
            return logisticsNo;
        }

        public void setLogisticsNo(String logisticsNo) {
            this.logisticsNo = logisticsNo;
        }

        public String getLogisticsCode() {
            return logisticsCode;
        }

        public void setLogisticsCode(String logisticsCode) {
            this.logisticsCode = logisticsCode;
        }
    }

    public static class ErrMsg{

        @JSONField(name = "fail_reason")
        private String failReason;

        public String getFailReason() {
            return failReason;
        }

        public void setFailReason(String failReason) {
            this.failReason = failReason;
        }
    }

    @Override
    public String getApiPath() {
        return "/api/pdd-erp/sync-track";
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
