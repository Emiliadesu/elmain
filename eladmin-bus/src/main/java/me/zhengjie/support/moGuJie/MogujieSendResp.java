package me.zhengjie.support.moGuJie;

import com.alibaba.fastjson.annotation.JSONField;

public class MogujieSendResp {
    @JSONField(name = "result")
    private Result result;
    @JSONField(name = "status")
    private Status status;
    @JSONField(name = "success")
    private Boolean success;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public static class Result {
        @JSONField(name = "data")
        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }
    }

    public static class Status {
        @JSONField(name = "code")
        private String code;
        @JSONField(name = "msg")
        private String msg;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
    public static class Data{
        @JSONField(name = "hasShipSuccess")
        private Boolean hasShipSuccess;

        public Boolean getHasShipSuccess() {
            return hasShipSuccess;
        }

        public void setHasShipSuccess(Boolean hasShipSuccess) {
            this.hasShipSuccess = hasShipSuccess;
        }
    }
}
