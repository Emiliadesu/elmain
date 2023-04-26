package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.douyin.CBOrderListMain;

import java.util.List;

public class QSOrderListResponse {

    @JSONField(name = "cust_id")
    private Long custId; // 本系统货主ID

    @JSONField(name = "l_shop_id")
    private Long lShopId;// 本系统店铺ID

    @JSONField(name = "lastPage")
    private Boolean lastPage;

    @JSONField(name = "count")
    private Integer count;

    @JSONField(name = "page")
    private Integer page;

    @JSONField(name = "pageSize")
    private Integer pageSize;

    @JSONField(name = "pageTotal")
    private Integer pageTotal;

    @JSONField(name = "pageList")
    private List<QSOrderListMain> list;

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

    public Boolean getLastPage() {
        return lastPage;
    }

    public void setLastPage(Boolean lastPage) {
        this.lastPage = lastPage;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(Integer pageTotal) {
        this.pageTotal = pageTotal;
    }

    public List<QSOrderListMain> getList() {
        return list;
    }

    public void setList(List<QSOrderListMain> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "QSOrderListResponse{" +
                "custId=" + custId +
                ", lShopId=" + lShopId +
                ", lastPage=" + lastPage +
                ", count=" + count +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", pageTotal=" + pageTotal +
                ", list=" + list +
                '}';
    }
}
