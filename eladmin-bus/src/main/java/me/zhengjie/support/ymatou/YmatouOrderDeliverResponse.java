package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouOrderDeliverResponse {
    @JSONField(name = "results")
    private DeliverResult[]results;

    public DeliverResult[] getResults() {
        return results;
    }

    public void setResults(DeliverResult[] results) {
        this.results = results;
    }

    public static class DeliverResult{
        /**
         * 订单号
         */
        @JSONField(name = "order_id")
        private Long orderId;
        /**
         * 是否执行成功
         */
        @JSONField(name = "exec_success")
        private Boolean execSuccess;
        /**
         * 错误码
         */
        @JSONField(name = "error_code")
        private Integer errorCode;
        /**
         * 处理信息
         */
        @JSONField(name = "msg")
        private String msg;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Boolean getExecSuccess() {
            return execSuccess;
        }

        public void setExecSuccess(Boolean execSuccess) {
            this.execSuccess = execSuccess;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSuccess(){
            return execSuccess;
        }
    }
}
