package me.zhengjie.support.jackYun;

public class JackYunDeliverInvoice {
    /**
     * 发票抬头
     */
    private String header;
    /**
     * 发票金额
     */
    private String amount;
    /**
     * 发票内容
     */
    private String content;
    /**
     * 发货详情
     */
    private JackYunDeliverDetail detail;

    /**
     * 发票代码
     */
    private String code;

    /**
     * 发票号码
     */
    private String number;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public JackYunDeliverDetail getDetail() {
        return detail;
    }

    public void setDetail(JackYunDeliverDetail detail) {
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
