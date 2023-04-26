package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenRequestHeader {
    private String appId;
    private String signTimeStamp;
    private String sign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSignTimeStamp() {
        return signTimeStamp;
    }

    public void setSignTimeStamp(String signTimeStamp) {
        this.signTimeStamp = signTimeStamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
