package me.zhengjie.support.jackYun;

public class JackYunBasicResponse {
    private String flag;
    private String code;
    private String message;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void returnSucc() {
        this.flag="success";
        this.code="0";
    }

    public void returnFail(String msg){
        this.flag="failure";
        this.code="400";
        this.message=msg;
    }
}
