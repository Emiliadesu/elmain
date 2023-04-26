package me.zhengjie.service.dto;

import lombok.Data;

@Data
public class StockDto extends BaseSkuDto{

    private Integer qty = 0; //总库存

    private Integer avaQty = 0; //可用库存

    private Integer occuQty = 0; //占用库存

    private String locationId;// 库位

    private String prodDate;// 生产日期

    private String expireDate;// 失效日期

    private String inStockDate;// 入库日期

    private String customerBatch;// 产品批次

    private String avaOrDef;// 库存属性

    private String goodsNo;// 货号

    private String barCode;

    private String goodsName;

    private String shopName;

    private String platformName;

    private String customerName;

    private String wmsBatchNo;
}
