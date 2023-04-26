package me.zhengjie.support.chinaPost;

import java.sql.Timestamp;

public class EMSTraces {

    private String traceNo;  //运单号

    private Timestamp opTime;

    private String opCode;  //操作码

    private String opName;

    private String opDesc;

    private String opOrgProvName;

    private String opOrgCity;

    private String opOrgCode;

    private String opOrgName;

    private String operatorNo;  //操作员工号

    private String operatorName;  //操作员工名称

    public String getTraceNo() {
        return traceNo;
    }

    public void setTraceNo(String traceNo) {
        this.traceNo = traceNo;
    }

    public Timestamp getOpTime() {
        return opTime;
    }

    public void setOpTime(Timestamp opTime) {
        this.opTime = opTime;
    }

    public String getOpCode() {
        return opCode;
    }

    public void setOpCode(String opCode) {
        this.opCode = opCode;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOpDesc() {
        return opDesc;
    }

    public void setOpDesc(String opDesc) {
        this.opDesc = opDesc;
    }

    public String getOpOrgProvName() {
        return opOrgProvName;
    }

    public void setOpOrgProvName(String opOrgProvName) {
        this.opOrgProvName = opOrgProvName;
    }

    public String getOpOrgCity() {
        return opOrgCity;
    }

    public void setOpOrgCity(String opOrgCity) {
        this.opOrgCity = opOrgCity;
    }

    public String getOpOrgCode() {
        return opOrgCode;
    }

    public void setOpOrgCode(String opOrgCode) {
        this.opOrgCode = opOrgCode;
    }

    public String getOpOrgName() {
        return opOrgName;
    }

    public void setOpOrgName(String opOrgName) {
        this.opOrgName = opOrgName;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }
}
