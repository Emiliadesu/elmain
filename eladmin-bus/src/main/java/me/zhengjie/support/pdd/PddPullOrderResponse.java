package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PddPullOrderResponse {
    @JSONField(name = "order_list")
    private List<PddOrder> orderList;
    @JSONField(name = "total_count")
    private Integer totalCount;
    @JSONField(name = "total_page")
    private Integer totalPage;
    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID
    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    public PddPullOrderResponse() {

    }

    public List<PddOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<PddOrder> orderList) {
        this.orderList = orderList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getlShopId() {
        return lShopId;
    }

    public void setlShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    @Override
    public String toString() {
        return "PddPullOrderResponse{" +
                "orderList=" + orderList +
                ", totalCount=" + totalCount +
                ", totalPage=" + totalPage +
                ", custId=" + custId +
                ", lShopId=" + lShopId +
                '}';
    }
}
