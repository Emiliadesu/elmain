package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;

public class JDCodeResponse {

    @JSONField(name = "item_code")
    private String itemCode;  //购买的收费项目编码

    @JSONField(name = "user_name")
    private String userName;

    @JSONField(name = "end_date")
    private Long endDate;

    @JSONField(name = "version_no")
    private String versionNo;

    @JSONField(name = "source")
    private String source;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
