package me.zhengjie.support.fuliPre;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author luob
 * @since 2022-06-12
 */
public class DocOrderPackingSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private String traceid;
    private String orderno;
    private String cartongroup;
    private Double grossweight;
    private Double cubic;
    private Double qty;
    private String udf1;
    private String udf2;
    private String udf3;
    private String udf4;
    private String udf5;
    private Date addtime;
    private String addwho;
    private Date edittime;
    private String editwho;
    private String deliveryno;
    private Double length;
    private Double width;
    private Double height;
    private String returnrefno;


    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getCartongroup() {
        return cartongroup;
    }

    public void setCartongroup(String cartongroup) {
        this.cartongroup = cartongroup;
    }

    public Double getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(Double grossweight) {
        this.grossweight = grossweight;
    }

    public Double getCubic() {
        return cubic;
    }

    public void setCubic(Double cubic) {
        this.cubic = cubic;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

    public String getUdf3() {
        return udf3;
    }

    public void setUdf3(String udf3) {
        this.udf3 = udf3;
    }

    public String getUdf4() {
        return udf4;
    }

    public void setUdf4(String udf4) {
        this.udf4 = udf4;
    }

    public String getUdf5() {
        return udf5;
    }

    public void setUdf5(String udf5) {
        this.udf5 = udf5;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getAddwho() {
        return addwho;
    }

    public void setAddwho(String addwho) {
        this.addwho = addwho;
    }

    public Date getEdittime() {
        return edittime;
    }

    public void setEdittime(Date edittime) {
        this.edittime = edittime;
    }

    public String getEditwho() {
        return editwho;
    }

    public void setEditwho(String editwho) {
        this.editwho = editwho;
    }

    public String getDeliveryno() {
        return deliveryno;
    }

    public void setDeliveryno(String deliveryno) {
        this.deliveryno = deliveryno;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getReturnrefno() {
        return returnrefno;
    }

    public void setReturnrefno(String returnrefno) {
        this.returnrefno = returnrefno;
    }

    @Override
    public String toString() {
        return "DocOrderPackingSummary{" +
        "traceid=" + traceid +
        ", orderno=" + orderno +
        ", cartongroup=" + cartongroup +
        ", grossweight=" + grossweight +
        ", cubic=" + cubic +
        ", qty=" + qty +
        ", udf1=" + udf1 +
        ", udf2=" + udf2 +
        ", udf3=" + udf3 +
        ", udf4=" + udf4 +
        ", udf5=" + udf5 +
        ", addtime=" + addtime +
        ", addwho=" + addwho +
        ", edittime=" + edittime +
        ", editwho=" + editwho +
        ", deliveryno=" + deliveryno +
        ", length=" + length +
        ", width=" + width +
        ", height=" + height +
        ", returnrefno=" + returnrefno +
        "}";
    }
}
