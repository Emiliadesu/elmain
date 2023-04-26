package me.zhengjie.rest.model.jackYun;

public class JackYunSingleItem {
    /**
     * 商品编码
     */
    private String itemCode;
    /**
     * 仓储商品编码
     */
    private String itemId;
    /**
     * 货号
     */
    private String goodsCode;
    /**
     * 品名
     */
    private String itemName;
    /**
     * 简称
     */
    private String shortName;
    /**
     * 英文名
     */
    private String englishName;
    /**
     * 条码
     */
    private String barCode;
    /**
     * 计量单位
     */
    private String stockUnit;
    /**
     * 长
     */
    private String length;
    /**
     * 宽
     */
    private String width;
    /**
     * 高
     */
    private String height;
    /**
     * 体积
     */
    private String volume;
    /**
     * 毛重
     */
    private String grossWeight;
    /**
     * 净重
     */
    private String netWeight;
    /**
     * 商品类型型(ZC=正常商
     * 品;FX=分销商品;ZH=
     * 组合商品;ZP=赠
     * 品;BC=包材;HC=耗
     * 材;FL=辅料;XN=虚拟
     * 品;FS=附属品;CC=残
     * 次品; OTHER=其它;只
     * 传英文编码
     */
    private String itemType;
    /**
     * 品牌编码
     */
    private String brandCode;
    /**
     * 品牌名
     */
    private String brandName;
    /**
     * 是否SN管理
     */
    private String isSNMgmt;
    /**
     * 是否效期管理
     */
    private String isShelfLifeMgmt;
    /**
     * 保质期 小时
     */
    private Integer shelfLife;
    /**
     * 是否批次管理
     */
    private String isBatchMgmt;
    /**
     * 箱规
     */
    private String pcs;
    /**
     * 原产地址
     */
    private String originAddress;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
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

    public String getIsSNMgmt() {
        return isSNMgmt;
    }

    public void setIsSNMgmt(String isSNMgmt) {
        this.isSNMgmt = isSNMgmt;
    }

    public String getIsShelfLifeMgmt() {
        return isShelfLifeMgmt;
    }

    public void setIsShelfLifeMgmt(String isShelfLifeMgmt) {
        this.isShelfLifeMgmt = isShelfLifeMgmt;
    }

    public Integer getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(Integer shelfLife) {
        this.shelfLife = shelfLife;
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
