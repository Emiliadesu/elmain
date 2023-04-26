package me.zhengjie.support.beidian;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class BeiDianCommonResponse<T> {
    /**
     * 响应是否成功
     */
    @JSONField(name = "success")
    private Boolean success;
    /**
     * 响应消息
     */
    @JSONField(name = "message")
    private String message;

    private T data;

    private List<T> dataArray;

    /**
     * 错误码
     */
    @JSONField(name = "err_code")
    private Integer errCode;

    /**
     * 错误信息
     */
    @JSONField(name = "err_msg")
    private String errMsg;

    /**
     * 页码
     */
    @JSONField(name = "page_no")
    private Integer pageNo;

    /**
     * 页大小
     */
    @JSONField(name = "page_size")
    private Integer pageSize;

    /**
     * 计数缓存
     */
    @JSONField(name = "cache_count")
    private Integer cacheCount;

    /**
     * 订单总数
     */
    @JSONField(name = "count")
    private Integer count;

    /**
     * 命中的订单号数组
     */
    @JSONField(name = "oids")
    private List<String>oids;

    /**
     * 查询订单所使用响应时间
     */
    @JSONField(name = "process_time")
    private Float processTime;

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDataArray() {
        return dataArray;
    }

    public void setDataArray(List<T> dataArray) {
        this.dataArray = dataArray;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
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

    public List<String> getOids() {
        return oids;
    }

    public void setOids(List<String> oids) {
        this.oids = oids;
    }

    public Float getProcessTime() {
        return processTime;
    }

    public void setProcessTime(Float processTime) {
        this.processTime = processTime;
    }
}
