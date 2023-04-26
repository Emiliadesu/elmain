package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.List;

public class BeiBeiOuterTradeOrderGetResponse {
    @JSONField(name = "err_code")
    private String errCode;
    @JSONField(name = "err_msg")
    private String errMsg;
    @JSONField(name = "success")
    private Boolean success;
    @JSONField(name = "message")
    private String message;
    @JSONField(name = "data")
    private List<OrderData> data;
    @JSONField(name = "page_no")
    private Integer pageNo;
    @JSONField(name = "page_size")
    private Integer pageSize;
    @JSONField(name = "cache_count")
    private Integer cacheCount;
    @JSONField(name = "count")
    private Integer count;
    @JSONField(name = "oids")
    private String[] oids;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderData> getData() {
        return data;
    }

    public void setData(List<OrderData> data) {
        this.data = data;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCacheCount() {
        return cacheCount;
    }

    public void setCacheCount(Integer cacheCount) {
        this.cacheCount = cacheCount;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String[] getOids() {
        return oids;
    }

    public void setOids(String[] oids) {
        this.oids = oids;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
