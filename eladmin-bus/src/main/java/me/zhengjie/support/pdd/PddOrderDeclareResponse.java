package me.zhengjie.support.pdd;

public class PddOrderDeclareResponse {
    private String declareNo;
    private String mailNo;
    private String addMark;
    private String orderNo;
    private String msg;


    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getAddMark() {
        return addMark;
    }

    public void setAddMark(String addMark) {
        this.addMark = addMark;
    }

    public String getDeclareNo() {
        return declareNo;
    }

    public void setDeclareNo(String declareNo) {
        this.declareNo = declareNo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public String toString() {
        return "PddOrderDeclareResponse{" +
                "declareNo='" + declareNo + '\'' +
                ", mailNo='" + mailNo + '\'' +
                ", addMark='" + addMark + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
