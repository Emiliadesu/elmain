package me.zhengjie.rest.model.ruoyuchen.request;

public class RuoYuChenBaseSku {
    /**
     * 货主编码
     */
    private String ownerCode;
    /**
     * 商品编码
     */
    private String itemCode;
    /**
     * 商品货号
     */
    private String goodsCode;
    /**
     * 商品名称
     */
    private String itemName;
    /**
     * 商品简称
     */
    private String shortName;
    /**
     * 英文名称
     */
    private String englishName;
    /**
     * 条码
     */
    private String barCode;
    /**
     * 商品属性
     */
    private String skuProperty;
    /**
     * 计量单位
     */
    private String stockUnit;
    /**
     * 长cm
     */
    private String length;
    /**
     * 宽cm
     */
    private String width;
    /**
     * 高cm
     */
    private String height;
    /**
     * 体积cm³
     */
    private String volume;
    /**
     * 毛重kg
     */
    private String grossWeight;
    /**
     * 净重kg
     */
    private String netWeight;
    /**
     * 颜色
     */
    private String color;
    /**
     * 尺寸
     */
    private String size;
    /**
     * 商品分类
     */
    private String categoryName;
    /**
     * 商品类型型(ZC=正常商品;FX=分销商品;ZH=组合商品;
     * ZP=赠品;BC=包材;HC=耗材;FL=辅料;XN=虚拟品;FS=附
     * 属品; CC=残次品; OTHER=其它;只传英文编码)
     */
    private String itemType;
    /**
     * 吊牌价
     */
    private String tagPrice;
    /**
     * 零售价
     */
    private String retailPrice;
    /**
     * 成本价
     */
    private String costPrice;
    /**
     * 采购价
     */
    private String purchasePrice;
    /**
     * 季节编码
     */
    private String seasonCode;
    /**
     * 季节名称
     */
    private String seasonName;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 是否保质期管理(Y/N ;默认为N)
     */
    private String isShelfLifeMgmt;
    /**
     * 保质期
     */
    private String shelfLife;
    /**
     * 商品禁收期 单位 天
     */
    private String rejectLifecycle;
    /**
     * 商品禁售期 单位 天
     */
    private String lockupLifecycle;
    /**
     * 保质期预警天数 单位 天
     */
    private String adventLifecycle;
    /**
     * 是否批次管理
     */
    private String isBatchMgmt;
    /**
     * 箱规
     */
    private String pcs;
    /**
     * 原产地
     */
    private String originAddress;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;

    public String getOwnerCode() {
        return ownerCode;
    }

    public void setOwnerCode(String ownerCode) {
        this.ownerCode = ownerCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSkuProperty() {
        return skuProperty;
    }

    public void setSkuProperty(String skuProperty) {
        this.skuProperty = skuProperty;
    }

    public String getStockUnit() {
        return stockUnit;
    }

    public void setStockUnit(String stockUnit) {
        this.stockUnit = stockUnit;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getGrossWeight() {
        return grossWeight;
    }

    public void setGrossWeight(String grossWeight) {
        this.grossWeight = grossWeight;
    }

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getTagPrice() {
        return tagPrice;
    }

    public void setTagPrice(String tagPrice) {
        this.tagPrice = tagPrice;
    }

    public String getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }

    public String getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getSeasonCode() {
        return seasonCode;
    }

    public void setSeasonCode(String seasonCode) {
        this.seasonCode = seasonCode;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getIsShelfLifeMgmt() {
        return isShelfLifeMgmt;
    }

    public void setIsShelfLifeMgmt(String isShelfLifeMgmt) {
        this.isShelfLifeMgmt = isShelfLifeMgmt;
    }

    public String getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(String shelfLife) {
        this.shelfLife = shelfLife;
    }

    public String getRejectLifecycle() {
        return rejectLifecycle;
    }

    public void setRejectLifecycle(String rejectLifecycle) {
        this.rejectLifecycle = rejectLifecycle;
    }

    public String getLockupLifecycle() {
        return lockupLifecycle;
    }

    public void setLockupLifecycle(String lockupLifecycle) {
        this.lockupLifecycle = lockupLifecycle;
    }

    public String getAdventLifecycle() {
        return adventLifecycle;
    }

    public void setAdventLifecycle(String adventLifecycle) {
        this.adventLifecycle = adventLifecycle;
    }

    public String getIsBatchMgmt() {
        return isBatchMgmt;
    }

    public void setIsBatchMgmt(String isBatchMgmt) {
        this.isBatchMgmt = isBatchMgmt;
    }

    public String getPcs() {
        return pcs;
    }

    public void setPcs(String pcs) {
        this.pcs = pcs;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
