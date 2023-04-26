package me.zhengjie.support.chinaPost;

import java.util.List;

public class WaybillTrackInformationPushRequest {

    private String sendID;  //发送方标识	 JDPT：寄递平台

    private String proviceNo;  // 数据生产的省公司代码	 对不能确定的省份取99

    private String msgKind;  //消息类别	接口编码（JDPT_XXXX_TRACE）

    private String serialNo;

    private String sendDate;  //消息发送日期时间

    private String receiveID;  //代表接收方标识

    private String batchNo;  //批次号

    private String dataType;  //数据类型	1-JSON  2-XML  3-压缩后的Byte[]

    private String dataDigest;  //

    private MsgBody msgBody;

    public static class MsgBody{
        private List<EMSTraces> traces;

        public List<EMSTraces> getTraces() {
            return traces;
        }

        public void setTraces(List<EMSTraces> traces) {
            this.traces = traces;
        }
    }

    public String getSendID() {
        return sendID;
    }

    public void setSendID(String sendID) {
        this.sendID = sendID;
    }

    public String getProviceNo() {
        return proviceNo;
    }

    public void setProviceNo(String proviceNo) {
        this.proviceNo = proviceNo;
    }

    public String getMsgKind() {
        return msgKind;
    }

    public void setMsgKind(String msgKind) {
        this.msgKind = msgKind;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(String receiveID) {
        this.receiveID = receiveID;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataDigest() {
        return dataDigest;
    }

    public void setDataDigest(String dataDigest) {
        this.dataDigest = dataDigest;
    }

    public MsgBody getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(MsgBody msgBody) {
        this.msgBody = msgBody;
    }
}
