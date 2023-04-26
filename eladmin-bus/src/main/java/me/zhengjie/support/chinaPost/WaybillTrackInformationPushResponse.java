package me.zhengjie.support.chinaPost;

import java.util.List;

public class WaybillTrackInformationPushResponse<T> {

    private String receiveId;  //接收方标识

    private boolean responseState;

    private String errorDesc;

    private T responseItems;

    public static class ResponseItems{

        private String traceNo; //运单号

        private boolean success;

        private String reason;

        public String getTraceNo() {
            return traceNo;
        }

        public void setTraceNo(String traceNo) {
            this.traceNo = traceNo;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public boolean isResponseState() {
        return responseState;
    }

    public void setResponseState(boolean responseState) {
        this.responseState = responseState;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public T getResponseItems() {
        return responseItems;
    }

    public void setResponseItems(T responseItems) {
        this.responseItems = responseItems;
    }
}
