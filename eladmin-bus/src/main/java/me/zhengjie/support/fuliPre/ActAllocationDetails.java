package me.zhengjie.support.fuliPre;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author luob
 * @since 2021-08-25
 */
public class ActAllocationDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    private String allocationdetailsid;
    private String orderno;
    private Long orderlineno;
    private Long skulineno;
    private String customerid;
    private String sku;
    private String lotnum;
    private String uom;
    private String location;
    private Double qty;
    private String traceid;
    private Double qtyEach;
    private String packid;
    private String waveno;
    private String status;
    private String addwho;
    private Date addtime;
    private String editwho;
    private Date edittime;
    private Double qtypickedEach;
    private Double qtyshippedEach;
    private String notes;
    private String picktolocation;
    private String picktotraceid;
    private Date pickedtime;
    private String pickedwho;
    private String packflag;
    private String checkwho;
    private Date checktime;
    private Date shipmenttime;
    private String reasoncode;
    private String shipmentwho;
    private String softallocationdetailsid;
    private Double cubic;
    private Double grossweight;
    private Double netweight;
    private Double price;
    private String sortationlocation;
    private String ordernoOld;
    private Long orderlinenoOld;
    private String allocationdetailsidOld;
    private String printflag;
    private String contrainerid;
    private String doublecheckby;
    private String shipmentconfirmby;
    private Double cartonseqno;
    private String dropid;
    private String pickingtransactionid;
    private String cartonid;
    private String palletize;
    private String workstation;
    private String udfprintflag1;

    private String lotAtt02;
    private String lotAtt03;
    private String lotAtt04;
    private String lotAtt08;
    private String lotAtt09;

    public String getLotAtt02() {
        return lotAtt02;
    }

    public void setLotAtt02(String lotAtt02) {
        this.lotAtt02 = lotAtt02;
    }

    public String getLotAtt03() {
        return lotAtt03;
    }

    public void setLotAtt03(String lotAtt03) {
        this.lotAtt03 = lotAtt03;
    }

    public String getLotAtt04() {
        return lotAtt04;
    }

    public void setLotAtt04(String lotAtt04) {
        this.lotAtt04 = lotAtt04;
    }

    public String getLotAtt08() {
        return lotAtt08;
    }

    public void setLotAtt08(String lotAtt08) {
        this.lotAtt08 = lotAtt08;
    }

    public String getLotAtt09() {
        return lotAtt09;
    }

    public void setLotAtt09(String lotAtt09) {
        this.lotAtt09 = lotAtt09;
    }

    public String getAllocationdetailsid() {
        return allocationdetailsid;
    }

    public void setAllocationdetailsid(String allocationdetailsid) {
        this.allocationdetailsid = allocationdetailsid;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public Long getOrderlineno() {
        return orderlineno;
    }

    public void setOrderlineno(Long orderlineno) {
        this.orderlineno = orderlineno;
    }

    public Long getSkulineno() {
        return skulineno;
    }

    public void setSkulineno(Long skulineno) {
        this.skulineno = skulineno;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getLotnum() {
        return lotnum;
    }

    public void setLotnum(String lotnum) {
        this.lotnum = lotnum;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getTraceid() {
        return traceid;
    }

    public void setTraceid(String traceid) {
        this.traceid = traceid;
    }

    public Double getQtyEach() {
        return qtyEach;
    }

    public void setQtyEach(Double qtyEach) {
        this.qtyEach = qtyEach;
    }

    public String getPackid() {
        return packid;
    }

    public void setPackid(String packid) {
        this.packid = packid;
    }

    public String getWaveno() {
        return waveno;
    }

    public void setWaveno(String waveno) {
        this.waveno = waveno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddwho() {
        return addwho;
    }

    public void setAddwho(String addwho) {
        this.addwho = addwho;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public String getEditwho() {
        return editwho;
    }

    public void setEditwho(String editwho) {
        this.editwho = editwho;
    }

    public Date getEdittime() {
        return edittime;
    }

    public void setEdittime(Date edittime) {
        this.edittime = edittime;
    }

    public Double getQtypickedEach() {
        return qtypickedEach;
    }

    public void setQtypickedEach(Double qtypickedEach) {
        this.qtypickedEach = qtypickedEach;
    }

    public Double getQtyshippedEach() {
        return qtyshippedEach;
    }

    public void setQtyshippedEach(Double qtyshippedEach) {
        this.qtyshippedEach = qtyshippedEach;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPicktolocation() {
        return picktolocation;
    }

    public void setPicktolocation(String picktolocation) {
        this.picktolocation = picktolocation;
    }

    public String getPicktotraceid() {
        return picktotraceid;
    }

    public void setPicktotraceid(String picktotraceid) {
        this.picktotraceid = picktotraceid;
    }

    public Date getPickedtime() {
        return pickedtime;
    }

    public void setPickedtime(Date pickedtime) {
        this.pickedtime = pickedtime;
    }

    public String getPickedwho() {
        return pickedwho;
    }

    public void setPickedwho(String pickedwho) {
        this.pickedwho = pickedwho;
    }

    public String getPackflag() {
        return packflag;
    }

    public void setPackflag(String packflag) {
        this.packflag = packflag;
    }

    public String getCheckwho() {
        return checkwho;
    }

    public void setCheckwho(String checkwho) {
        this.checkwho = checkwho;
    }

    public Date getChecktime() {
        return checktime;
    }

    public void setChecktime(Date checktime) {
        this.checktime = checktime;
    }

    public Date getShipmenttime() {
        return shipmenttime;
    }

    public void setShipmenttime(Date shipmenttime) {
        this.shipmenttime = shipmenttime;
    }

    public String getReasoncode() {
        return reasoncode;
    }

    public void setReasoncode(String reasoncode) {
        this.reasoncode = reasoncode;
    }

    public String getShipmentwho() {
        return shipmentwho;
    }

    public void setShipmentwho(String shipmentwho) {
        this.shipmentwho = shipmentwho;
    }

    public String getSoftallocationdetailsid() {
        return softallocationdetailsid;
    }

    public void setSoftallocationdetailsid(String softallocationdetailsid) {
        this.softallocationdetailsid = softallocationdetailsid;
    }

    public Double getCubic() {
        return cubic;
    }

    public void setCubic(Double cubic) {
        this.cubic = cubic;
    }

    public Double getGrossweight() {
        return grossweight;
    }

    public void setGrossweight(Double grossweight) {
        this.grossweight = grossweight;
    }

    public Double getNetweight() {
        return netweight;
    }

    public void setNetweight(Double netweight) {
        this.netweight = netweight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSortationlocation() {
        return sortationlocation;
    }

    public void setSortationlocation(String sortationlocation) {
        this.sortationlocation = sortationlocation;
    }

    public String getOrdernoOld() {
        return ordernoOld;
    }

    public void setOrdernoOld(String ordernoOld) {
        this.ordernoOld = ordernoOld;
    }

    public Long getOrderlinenoOld() {
        return orderlinenoOld;
    }

    public void setOrderlinenoOld(Long orderlinenoOld) {
        this.orderlinenoOld = orderlinenoOld;
    }

    public String getAllocationdetailsidOld() {
        return allocationdetailsidOld;
    }

    public void setAllocationdetailsidOld(String allocationdetailsidOld) {
        this.allocationdetailsidOld = allocationdetailsidOld;
    }

    public String getPrintflag() {
        return printflag;
    }

    public void setPrintflag(String printflag) {
        this.printflag = printflag;
    }

    public String getContrainerid() {
        return contrainerid;
    }

    public void setContrainerid(String contrainerid) {
        this.contrainerid = contrainerid;
    }

    public String getDoublecheckby() {
        return doublecheckby;
    }

    public void setDoublecheckby(String doublecheckby) {
        this.doublecheckby = doublecheckby;
    }

    public String getShipmentconfirmby() {
        return shipmentconfirmby;
    }

    public void setShipmentconfirmby(String shipmentconfirmby) {
        this.shipmentconfirmby = shipmentconfirmby;
    }

    public Double getCartonseqno() {
        return cartonseqno;
    }

    public void setCartonseqno(Double cartonseqno) {
        this.cartonseqno = cartonseqno;
    }

    public String getDropid() {
        return dropid;
    }

    public void setDropid(String dropid) {
        this.dropid = dropid;
    }

    public String getPickingtransactionid() {
        return pickingtransactionid;
    }

    public void setPickingtransactionid(String pickingtransactionid) {
        this.pickingtransactionid = pickingtransactionid;
    }

    public String getCartonid() {
        return cartonid;
    }

    public void setCartonid(String cartonid) {
        this.cartonid = cartonid;
    }

    public String getPalletize() {
        return palletize;
    }

    public void setPalletize(String palletize) {
        this.palletize = palletize;
    }

    public String getWorkstation() {
        return workstation;
    }

    public void setWorkstation(String workstation) {
        this.workstation = workstation;
    }

    public String getUdfprintflag1() {
        return udfprintflag1;
    }

    public void setUdfprintflag1(String udfprintflag1) {
        this.udfprintflag1 = udfprintflag1;
    }

    @Override
    public String toString() {
        return "ActAllocationDetails{" +
                "allocationdetailsid=" + allocationdetailsid +
                ", orderno=" + orderno +
                ", orderlineno=" + orderlineno +
                ", skulineno=" + skulineno +
                ", customerid=" + customerid +
                ", sku=" + sku +
                ", lotnum=" + lotnum +
                ", uom=" + uom +
                ", location=" + location +
                ", qty=" + qty +
                ", traceid=" + traceid +
                ", qtyEach=" + qtyEach +
                ", packid=" + packid +
                ", waveno=" + waveno +
                ", status=" + status +
                ", addwho=" + addwho +
                ", addtime=" + addtime +
                ", editwho=" + editwho +
                ", edittime=" + edittime +
                ", qtypickedEach=" + qtypickedEach +
                ", qtyshippedEach=" + qtyshippedEach +
                ", notes=" + notes +
                ", picktolocation=" + picktolocation +
                ", picktotraceid=" + picktotraceid +
                ", pickedtime=" + pickedtime +
                ", pickedwho=" + pickedwho +
                ", packflag=" + packflag +
                ", checkwho=" + checkwho +
                ", checktime=" + checktime +
                ", shipmenttime=" + shipmenttime +
                ", reasoncode=" + reasoncode +
                ", shipmentwho=" + shipmentwho +
                ", softallocationdetailsid=" + softallocationdetailsid +
                ", cubic=" + cubic +
                ", grossweight=" + grossweight +
                ", netweight=" + netweight +
                ", price=" + price +
                ", sortationlocation=" + sortationlocation +
                ", ordernoOld=" + ordernoOld +
                ", orderlinenoOld=" + orderlinenoOld +
                ", allocationdetailsidOld=" + allocationdetailsidOld +
                ", printflag=" + printflag +
                ", contrainerid=" + contrainerid +
                ", doublecheckby=" + doublecheckby +
                ", shipmentconfirmby=" + shipmentconfirmby +
                ", cartonseqno=" + cartonseqno +
                ", dropid=" + dropid +
                ", pickingtransactionid=" + pickingtransactionid +
                ", cartonid=" + cartonid +
                ", palletize=" + palletize +
                ", workstation=" + workstation +
                ", udfprintflag1=" + udfprintflag1 +
                "}";
    }
}
