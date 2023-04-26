package me.zhengjie.service.dto;

import java.util.Objects;

public class DocAsnDetail {
    private String asnno;
    private String sku;
    private Double asnlineno;
    private String pono;
    private Double expectedqty;
    private Double receivedqty;
    private Integer goodQty;
    private Integer badQty;
    private String lotnum;
    private String lotatt01;
    private String lotatt02;
    private String lotatt03;
    private String lotatt04;
    private String lotatt05;
    private String lotatt06;
    private String lotatt07;
    private String lotatt08;
    private String lotatt09;
    private String lotatt10;
    private String lotatt11;
    private String lotatt12;

    public String getAsnno() {
        return asnno;
    }

    public void setAsnno(String asnno) {
        this.asnno = asnno;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getAsnlineno() {
        return asnlineno;
    }

    public Integer getGoodQty() {
        return goodQty;
    }

    public void setGoodQty(Integer goodQty) {
        this.goodQty = goodQty;
    }

    public Integer getBadQty() {
        return badQty;
    }

    public void setBadQty(Integer badQty) {
        this.badQty = badQty;
    }

    public void setAsnlineno(Double asnlineno) {
        this.asnlineno = asnlineno;
    }

    public Double getExpectedqty() {
        return expectedqty;
    }

    public void setExpectedqty(Double expectedqty) {
        this.expectedqty = expectedqty;
    }

    public Double getReceivedqty() {
        return receivedqty;
    }

    public void setReceivedqty(Double receivedqty) {
        this.receivedqty = receivedqty;
    }

    public String getPono() {
        return pono;
    }

    public void setPono(String pono) {
        this.pono = pono;
    }

    public String getLotnum() {
        return lotnum;
    }

    public void setLotnum(String lotnum) {
        this.lotnum = lotnum;
    }

    public String getLotatt01() {
        return lotatt01;
    }

    public void setLotatt01(String lotatt01) {
        this.lotatt01 = lotatt01;
    }

    public String getLotatt02() {
        return lotatt02;
    }

    public void setLotatt02(String lotatt02) {
        this.lotatt02 = lotatt02;
    }

    public String getLotatt03() {
        return lotatt03;
    }

    public void setLotatt03(String lotatt03) {
        this.lotatt03 = lotatt03;
    }

    public String getLotatt04() {
        return lotatt04;
    }

    public void setLotatt04(String lotatt04) {
        this.lotatt04 = lotatt04;
    }

    public String getLotatt05() {
        return lotatt05;
    }

    public void setLotatt05(String lotatt05) {
        this.lotatt05 = lotatt05;
    }

    public String getLotatt06() {
        return lotatt06;
    }

    public void setLotatt06(String lotatt06) {
        this.lotatt06 = lotatt06;
    }

    public String getLotatt07() {
        return lotatt07;
    }

    public void setLotatt07(String lotatt07) {
        this.lotatt07 = lotatt07;
    }

    public String getLotatt08() {
        return lotatt08;
    }

    public void setLotatt08(String lotatt08) {
        this.lotatt08 = lotatt08;
    }

    public String getLotatt09() {
        return lotatt09;
    }

    public void setLotatt09(String lotatt09) {
        this.lotatt09 = lotatt09;
    }

    public String getLotatt10() {
        return lotatt10;
    }

    public void setLotatt10(String lotatt10) {
        this.lotatt10 = lotatt10;
    }

    public String getLotatt11() {
        return lotatt11;
    }

    public void setLotatt11(String lotatt11) {
        this.lotatt11 = lotatt11;
    }

    public String getLotatt12() {
        return lotatt12;
    }

    public void setLotatt12(String lotatt12) {
        this.lotatt12 = lotatt12;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocAsnDetail that = (DocAsnDetail) o;
        return asnno.equals(that.asnno) &&
                sku.equals(that.sku) &&
                Objects.equals(lotatt02, that.lotatt02) &&
                lotatt08.equals(that.lotatt08);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asnno, sku, lotatt02, lotatt08);
    }
    public void copy(DocAsnDetail target){
        target.asnno=this.asnno;
        target.sku=this.sku;
        target.asnlineno=this.asnlineno;
        target.pono=this.pono;
        target.expectedqty=this.expectedqty;
        target.receivedqty=this.receivedqty;
        target.goodQty=this.goodQty;
        target.badQty=this.badQty;
        target.lotatt01=this.lotatt01;
        target.lotatt02=this.lotatt02;
        target.lotatt03=this.lotatt03;
        target.lotatt04=this.lotatt04;
        target.lotatt05=this.lotatt05;
        target.lotatt06=this.lotatt06;
        target.lotatt07=this.lotatt07;
        target.lotatt08=this.lotatt08;
        target.lotatt09=this.lotatt09;
        target.lotatt10=this.lotatt10;
        target.lotatt11=this.lotatt11;
        target.lotatt12=this.lotatt12;
    }
}
