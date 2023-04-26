package me.zhengjie.support.chinaPost;

import org.apache.poi.ss.formula.functions.T;

public class ChinaPostCommonParam {

    private String logisticsInterface;  //  消息内容

    private String dataDigest;  //消息签名

    private String msgType;  //消息类型，如“ORDERCREATE”

    private String ecCompanyId; //电商标识，如“DKH”


    public String getLogisticsInterface() {
        return logisticsInterface;
    }

    public void setLogisticsInterface(String logisticsInterface) {
        this.logisticsInterface = logisticsInterface;
    }

    public String getDataDigest() {
        return dataDigest;
    }

    public void setDataDigest(String dataDigest) {
        this.dataDigest = dataDigest;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getEcCompanyId() {
        return ecCompanyId;
    }

    public void setEcCompanyId(String ecCompanyId) {
        this.ecCompanyId = ecCompanyId;
    }

}
