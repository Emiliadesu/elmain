package me.zhengjie.rest.model.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateWarehouseFeeOrder {
    /**
     * 仓租单号
     */
    @JsonProperty(value = "ws_store_no")
    @JSONField(name = "ws_store_no")
    private String wsStoreNo;

    /**
     * 仓库编码
     */
    @JsonProperty(value = "warehouse_code")
    @JSONField(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 计费日期。十位秒级时间戳，需要截断时分秒。如 1640966400 表示2022-01-01 00:00:00
     */
    @JsonProperty(value = "fee_date")
    @JSONField(name = "fee_date")
    private Long feeDate;

    /**
     * 店铺ID
     * 非必填
     */
    @JsonProperty(value = "shop_id")
    @JSONField(name = "shop_id")
    private Long shopId;

    /**
     * 货主ID
     * 非必填
     */
    @JsonProperty(value = "owner_id")
    @JSONField(name = "owner_id")
    private Long ownerId;

    /**
     * 货主类型: "CB_OWNER"-类自营 "CB_OWNER_POP"-跨境pop货主。 货主类型为 CB_OWNER_POP时，取店铺ID获取对应货品信息；货主类型为CB_OWNER时，取货主ID获取对应货品信息。
     */
    @JsonProperty(value = "owner_type")
    @JSONField(name = "owner_type")
    private String ownerType;

    public String getWsStoreNo() {
        return wsStoreNo;
    }

    public void setWsStoreNo(String wsStoreNo) {
        this.wsStoreNo = wsStoreNo;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Long getFeeDate() {
        return feeDate;
    }

    public void setFeeDate(Long feeDate) {
        this.feeDate = feeDate;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }
}
