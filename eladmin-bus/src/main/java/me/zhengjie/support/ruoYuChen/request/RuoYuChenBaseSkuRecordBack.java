package me.zhengjie.support.ruoYuChen.request;

import java.math.BigDecimal;

public class RuoYuChenBaseSkuRecordBack extends RuoYuChenRequestAbs{
    /**
     * 商品编码
     * Y
     */
    private String itemCode;
    /**
     * 备案商品编码
     * N
     */
    private String cusItemCode;
    /**
     * 备案条形码
     * N
     */
    private String cusBarCode;
    /**
     * 备案商品名称
     * N
     */
    private String cusItemName;
    /**
     * 备案账册号
     * N
     */
    private String cusBooksCode;
    /**
     * 备案货号
     * N
     */
    private String cusArtCode;
    /**
     * 备案序号
     * N
     */
    private String cusSerialCode;
    /**
     * 商检备案货号
     * N
     */
    private String mchArtCode;
    /**
     * 备案商品id
     * N
     */
    private String cusItemId;
    /**
     * 海关HSCode
     * N
     */
    private String cusHsCode;
    /**
     * 原产国
     * N
     */
    private String cusCountry;
    /**
     * 法定第一数量
     * N
     */
    private BigDecimal cusOneQty;
    /**
     * 法定第一单位
     * N
     */
    private String cusOneDw;
    /**
     * 法定第二数量
     * N
     */
    private BigDecimal cusTwoQty;
    /**
     * 法定第二单位
     * N
     */
    private String cusTwoDw;
    /**
     * 销售单位
     * N
     */
    private String cusSaleDw;
    /**
     * 备案价
     * N
     */
    private String cusRecordPrice;
    /**
     * 币种
     * N
     */
    private String cusCurrency;
    /**
     * 规格型号
     * N
     */
    private String cusSpecModel;
    /**
     * 品牌编码
     * N
     */
    private String cusBradCode;
    /**
     * 品牌名称
     * N
     */
    private String cusBradName;
    /**
     * 用途
     * N
     */
    private String cusPurpose;
    /**
     * 成分含量
     * N
     */
    private String cusIngredient;
    /**
     * 生产企业名称
     * N
     */
    private String cusManufacturer;
    /**
     * 生产企业地址
     * N
     */
    private String cusManufacturerAddress;
    /**
     * 净重
     * N
     */
    private BigDecimal cusNetWeight;
    /**
     * 毛重
     * N
     */
    private BigDecimal cusGrossWeight;
    /**
     * 申报要素
     * N
     */
    private String cusDecElement;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getCusItemCode() {
        return cusItemCode;
    }

    public void setCusItemCode(String cusItemCode) {
        this.cusItemCode = cusItemCode;
    }

    public String getCusBarCode() {
        return cusBarCode;
    }

    public void setCusBarCode(String cusBarCode) {
        this.cusBarCode = cusBarCode;
    }

    public String getCusItemName() {
        return cusItemName;
    }

    public void setCusItemName(String cusItemName) {
        this.cusItemName = cusItemName;
    }

    public String getCusBooksCode() {
        return cusBooksCode;
    }

    public void setCusBooksCode(String cusBooksCode) {
        this.cusBooksCode = cusBooksCode;
    }

    public String getCusArtCode() {
        return cusArtCode;
    }

    public void setCusArtCode(String cusArtCode) {
        this.cusArtCode = cusArtCode;
    }

    public String getCusSerialCode() {
        return cusSerialCode;
    }

    public void setCusSerialCode(String cusSerialCode) {
        this.cusSerialCode = cusSerialCode;
    }

    public String getMchArtCode() {
        return mchArtCode;
    }

    public void setMchArtCode(String mchArtCode) {
        this.mchArtCode = mchArtCode;
    }

    public String getCusItemId() {
        return cusItemId;
    }

    public void setCusItemId(String cusItemId) {
        this.cusItemId = cusItemId;
    }

    public String getCusHsCode() {
        return cusHsCode;
    }

    public void setCusHsCode(String cusHsCode) {
        this.cusHsCode = cusHsCode;
    }

    public String getCusCountry() {
        return cusCountry;
    }

    public void setCusCountry(String cusCountry) {
        this.cusCountry = cusCountry;
    }

    public BigDecimal getCusOneQty() {
        return cusOneQty;
    }

    public void setCusOneQty(BigDecimal cusOneQty) {
        this.cusOneQty = cusOneQty;
    }

    public String getCusOneDw() {
        return cusOneDw;
    }

    public void setCusOneDw(String cusOneDw) {
        this.cusOneDw = cusOneDw;
    }

    public BigDecimal getCusTwoQty() {
        return cusTwoQty;
    }

    public void setCusTwoQty(BigDecimal cusTwoQty) {
        this.cusTwoQty = cusTwoQty;
    }

    public String getCusTwoDw() {
        return cusTwoDw;
    }

    public void setCusTwoDw(String cusTwoDw) {
        this.cusTwoDw = cusTwoDw;
    }

    public String getCusSaleDw() {
        return cusSaleDw;
    }

    public void setCusSaleDw(String cusSaleDw) {
        this.cusSaleDw = cusSaleDw;
    }

    public String getCusRecordPrice() {
        return cusRecordPrice;
    }

    public void setCusRecordPrice(String cusRecordPrice) {
        this.cusRecordPrice = cusRecordPrice;
    }

    public String getCusCurrency() {
        return cusCurrency;
    }

    public void setCusCurrency(String cusCurrency) {
        this.cusCurrency = cusCurrency;
    }

    public String getCusSpecModel() {
        return cusSpecModel;
    }

    public void setCusSpecModel(String cusSpecModel) {
        this.cusSpecModel = cusSpecModel;
    }

    public String getCusBradCode() {
        return cusBradCode;
    }

    public void setCusBradCode(String cusBradCode) {
        this.cusBradCode = cusBradCode;
    }

    public String getCusBradName() {
        return cusBradName;
    }

    public void setCusBradName(String cusBradName) {
        this.cusBradName = cusBradName;
    }

    public String getCusPurpose() {
        return cusPurpose;
    }

    public void setCusPurpose(String cusPurpose) {
        this.cusPurpose = cusPurpose;
    }

    public String getCusIngredient() {
        return cusIngredient;
    }

    public void setCusIngredient(String cusIngredient) {
        this.cusIngredient = cusIngredient;
    }

    public String getCusManufacturer() {
        return cusManufacturer;
    }

    public void setCusManufacturer(String cusManufacturer) {
        this.cusManufacturer = cusManufacturer;
    }

    public String getCusManufacturerAddress() {
        return cusManufacturerAddress;
    }

    public void setCusManufacturerAddress(String cusManufacturerAddress) {
        this.cusManufacturerAddress = cusManufacturerAddress;
    }

    public BigDecimal getCusNetWeight() {
        return cusNetWeight;
    }

    public void setCusNetWeight(BigDecimal cusNetWeight) {
        this.cusNetWeight = cusNetWeight;
    }

    public BigDecimal getCusGrossWeight() {
        return cusGrossWeight;
    }

    public void setCusGrossWeight(BigDecimal cusGrossWeight) {
        this.cusGrossWeight = cusGrossWeight;
    }

    public String getCusDecElement() {
        return cusDecElement;
    }

    public void setCusDecElement(String cusDecElement) {
        this.cusDecElement = cusDecElement;
    }

    @Override
    public String getMethod() {
        return "MaterialBack";
    }
}
