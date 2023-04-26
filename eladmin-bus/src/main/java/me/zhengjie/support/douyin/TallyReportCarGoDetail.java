package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

public class TallyReportCarGoDetail {
    /**
     * 抖音货品编码
     */
    @JSONField(name = "cargo_code")
    private String cargoCode;

    /**
     * 原产国
     */
    @JSONField(name = "origin_country")
    private String originCountry;

    /**
     * 货品名称
     */
    @JSONField(name = "cargo_name")
    private String cargoName;

    /**
     * 长
     */
    private Integer length;

    /**
     * 宽
     */
    private Integer width;

    /**
     * 高
     */
    private Integer height;

    /**
     * 体积
     */
    private Integer volume;

    /**
     * 长度单位，默认mm
     */
    @JSONField(name = "length_unit")
    private Integer lengthUnit;

    /**
     * 重量
     */
    private Integer weight;

    /**
     * 重量单位
     */
    @JSONField(name = "weight_unit")
    private String weightUnit;

    /**
     * 商品条码
     */
    private String barcode;

    /**
     * 理货良品件数
     */
    @JSONField(name = "good_qty")
    private Integer goodQty;

    /**
     * 理货次品件数
     */
    @JSONField(name = "defective_qty")
    private Integer defectiveQty;

    /**
     * 报关数量
     */
    @JSONField(name = "customs_qty")
    private Integer customsQty;

    /**
     * 实收数量
     */
    @JSONField(name = "receive_qty")
    private Integer receiveQty;

    /**
     * 生产批次
     */
    @JSONField(name = "product_batch_no")
    private String productBatchNo;

    /**
     * 生产日期时间戳 秒
     */
    @JSONField(name = "product_date")
    private Long productDate;

    /**
     * 失效日期时间戳 秒
     */
    @JSONField(name = "expiration_date")
    private Long expirationDate;

    /**
     * 表述
     * 该货品ID，xx产国，总预约到货数为XX，实际理货件数为XX，差异为XX
     * 若遇同品多行需在表述中注明商品存在多条理货明细，需商家关注
     */
    private String difference;

    /**
     * 备注
     */
    private String remark;

    public String getCargoCode() {
        return cargoCode;
    }

    public void setCargoCode(String cargoCode) {
        this.cargoCode = cargoCode;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    public String getCargoName() {
        return cargoName;
    }

    public void setCargoName(String cargoName) {
        this.cargoName = cargoName;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getLengthUnit() {
        return lengthUnit;
    }

    public void setLengthUnit(Integer lengthUnit) {
        this.lengthUnit = lengthUnit;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getGoodQty() {
        return goodQty;
    }

    public void setGoodQty(Integer goodQty) {
        this.goodQty = goodQty;
    }

    public Integer getDefectiveQty() {
        return defectiveQty;
    }

    public void setDefectiveQty(Integer defectiveQty) {
        this.defectiveQty = defectiveQty;
    }

    public Integer getCustomsQty() {
        return customsQty;
    }

    public void setCustomsQty(Integer customsQty) {
        this.customsQty = customsQty;
    }

    public Integer getReceiveQty() {
        return receiveQty;
    }

    public void setReceiveQty(Integer receiveQty) {
        this.receiveQty = receiveQty;
    }

    public String getProductBatchNo() {
        return productBatchNo;
    }

    public void setProductBatchNo(String productBatchNo) {
        this.productBatchNo = productBatchNo;
    }

    public Long getProductDate() {
        return productDate;
    }

    public void setProductDate(Long productDate) {
        this.productDate = productDate;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getDifference() {
        return difference;
    }

    public void setDifference(String difference) {
        this.difference = difference;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
