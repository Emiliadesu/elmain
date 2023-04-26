package me.zhengjie.support.moGuJie;


import me.zhengjie.utils.StringUtil;

public class MogujieTradeGetResp {
    private MogujieTradeSoldGetResp.Status status;
    private Boolean success;
    private Result result;

    public MogujieTradeSoldGetResp.Status getStatus() {
        return status;
    }

    public void setStatus(MogujieTradeSoldGetResp.Status status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private MogujieTradeSoldGetResp.OrderData data;

        public MogujieTradeSoldGetResp.OrderData getData() {
            return data;
        }

        public void setData(MogujieTradeSoldGetResp.OrderData data) {
            this.data = data;
        }
    }

    public boolean isSuccessful() {
        return (this.success!=null && this.success && StringUtil.equals(status.getCode(), "0000000"));
    }
}
