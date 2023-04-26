package me.zhengjie.support.ruoYuChen.request;

public class RuoYuChenTallyOrderBackDetail {
    /**
     * 产品编码
     * Y
     */
    private String itemCode;
    /**
     * 总数量
     * Y
     */
    private String sumQty;
    /**
     * 良品数
     * Y
     */
    private String goodsQty;
    /**
     * 次品数
     * Y
     */
    private String defectiveQty;
    /**
     * 生产日期 yyyy-MM-dd
     * N
     */
    private String productDate;
    /**
     * 失效日期 yyyy-MM-dd
     * Y
     */
    private String expireDate;
    /**
     * 备注
     * N
     */
    private String remark;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getSumQty() {
        return sumQty;
    }

    public void setSumQty(String sumQty) {
        this.sumQty = sumQty;
    }

    public String getGoodsQty() {
        return goodsQty;
    }

    public void setGoodsQty(String goodsQty) {
        this.goodsQty = goodsQty;
    }

    public String getDefectiveQty() {
        return defectiveQty;
    }

    public void setDefectiveQty(String defectiveQty) {
        this.defectiveQty = defectiveQty;
    }

    public String getProductDate() {
        return productDate;
    }

    public void setProductDate(String productDate) {
        this.productDate = productDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
