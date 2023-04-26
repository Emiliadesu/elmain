package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class DYOrderListResponse {

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    @JSONField(name = "has_next")
    private Boolean hasNext;

    @JSONField(name = "total")
    private Long total;

    @JSONField(name = "data")
    private List<CBOrderListMain> list;

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<CBOrderListMain> getList() {
        return list;
    }

    public void setList(List<CBOrderListMain> list) {
        this.list = list;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    @Override
    public String toString() {
        return "DYOrderListResponse{" +
                "custId=" + custId +
                ", lShopId=" + lShopId +
                ", hasNext=" + hasNext +
                ", total=" + total +
                ", list=" + list +
                '}';
    }
}
