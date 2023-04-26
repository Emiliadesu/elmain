package me.zhengjie.support.chinaPost;

import com.alibaba.fastjson.annotation.JSONField;

public class EMSCrossBorderHeadRequest {

    private String MessageID;  //每份报文唯一的编号，规则：GUID企业系统生成36位唯一序号（英文字母大写）

    private String FunctionCode;  // 企业自定义.默认填写:0

    private String MessageType;  //企业报文类型.默认填写:511

    private String SenderID;  // 填写发送方编号   宁波富立物流有限公司    SenderID：3011

    private String ReceiverID;  //填写接收方编码(默认填写：EMS)

    private String SendTime;

    private String Version;  //默认：1.0

    public String getMessageID() {
        return MessageID;
    }

    public void setMessageID(String messageID) {
        MessageID = messageID;
    }

    public String getFunctionCode() {
        return FunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        FunctionCode = functionCode;
    }

    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String messageType) {
        MessageType = messageType;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getReceiverID() {
        return ReceiverID;
    }

    public void setReceiverID(String receiverID) {
        ReceiverID = receiverID;
    }

    public String getSendTime() {
        return SendTime;
    }

    public void setSendTime(String sendTime) {
        SendTime = sendTime;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }
}
