package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class YuncWmsInventoryQueryRequest {
    /**
     * 仓code
     */
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 货主编码
     */
    @JSONField(name = "owner_code")
    private String ownerCode;

    /**
     * 货品列表
     */
    @JSONField(name = "cargo_list")
    private List<String>cargoList;

    /**
     * 是否需要明细，
     * 1 需要返回明细，
     * 2是货品维度库存
     */
    @JSONField(name = "query_type")
    private Integer queryType;

    /**
     * 页码
     */
    @JSONField(name = "page")
    private Integer page;

    /**
     * 偏移量
     */
    @JSONField(name = "page_size")
    private Integer pageSize;

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public List<String> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<String> cargoList) {
        this.cargoList = cargoList;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
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
}
