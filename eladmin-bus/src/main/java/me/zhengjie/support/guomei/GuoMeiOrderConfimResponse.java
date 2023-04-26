package me.zhengjie.support.guomei;



public class GuoMeiOrderConfimResponse {

    private String code;

    private String message;

    private Data data;

    public static class Data{

        private Integer cancelStatus;

        public Integer getCancelStatus() {
            return cancelStatus;
        }

        public void setCancelStatus(Integer cancelStatus) {
            this.cancelStatus = cancelStatus;
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
