package me.zhengjie.rest.model.Flux;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author luob
 * @since 2019-01-06
 */
public class DocOrderHeader{

    private String orderno;

    private String customerid;

    private String soreference1;

    private Date ordertime;

    private Date expectedshipmenttime1;

    private Date expectedshipmenttime2;

    private Date requireddeliverytime;

    private String priority;

    private String consigneeid;

    private String cContact;

    private String consigneename;

    private String cAddress1;

    private String cAddress2;

    private String cAddress3;

    private String cAddress4;

    private String cCity;

    private String cProvince;

    private String cCountry;

    private String cFax;

    private String billingid;

    private String cZip;

    private String bContact;

    private String billingname;

    private String bAddress1;

    private String bAddress2;

    private String bAddress3;

    private String bAddress4;

    private String bCity;

    private String bProvince;

    private String bZip;

    private String bCountry;

    private String deliverytermsdescr;

    private String bFax;

    private String bEmail;

    private String paymenttermsdescr;

    private String deliveryterms;

    private String paymentterms;

    private String door;

    private String route;

    private String stop;

    private String sostatus;

    private String placeofdischarge;

    private String placeofdelivery;

    private String ordertype;

    private String userdefine1;

    private String userdefine2;

    private String userdefine3;

    private String userdefine4;

    private String userdefine5;

    private Date addtime;

    private String addwho;

    private Date edittime;

    private String editwho;

    private String notes;

    private String soreference2;

    private String soreference3;

    private String carrierid;

    private String carriername;

    private String carrieraddress1;

    private String carrieraddress3;

    private String carrieraddress2;

    private String carrieraddress4;

    private String carriercity;

    private String carrierprovince;

    private String carriercountry;

    private String carrierzip;

    private String issuepartyid;

    private String issuepartyname;

    private String iAddress1;

    private String iAddress2;

    private String iAddress3;

    private String iAddress4;

    private String iCity;

    private String iProvince;

    private String iCountry;

    private String iZip;

    private Date lastshipmenttime;

    private String edisendflag;

    private String pickingPrintFlag;

    private String createsource;

    private Date edisendtime;

    private Date edisendtime2;

    private Date edisendtime3;

    private Date edisendtime4;

    private Date edisendtime5;

    private String bTel1;

    private String bTel2;

    private String carriercontact;

    private String carriermail;

    private String carrierfax;

    private String carriertel1;

    private String carriertel2;

    private String cMail;

    private String cTel1;

    private String cTel2;

    private String iContact;

    private String iMail;

    private String iFax;

    private String iTel1;

    private String iTel2;

    private String releasestatus;

    private String transportation;

    private String soreference4;

    private String soreference5;

    private String hEdi02;

    private String userdefine6;

    private String orderPrintFlag;

    private String rfgettask;

    private String warehouseid;

    private String erpcancelflag;

    private String zonegroup;

    private Date medicalxmltime;

    private String placeofloading;

    private String requiredeliveryno;

    private String singlematch;

    private String serialnocatch;

    private String followup;

    private String userdefinea;

    private String userdefineb;

    private String salesorderno;

    private String invoiceprintflag;

    private String invoiceno;

    private String invoicetitle;

    private String invoicetype;

    private String invoiceitem;

    private Double invoiceamount;

    private String archiveflag;

    private String consigneenameE;

    private String puttolocation;

    private String fulAlc;

    private String deliveryno;

    private String channel;

    private String waveno;

    private Double allocationcount;

    private String expressprintflag;

    private String deliverynoteprintflag;

    private String weightingflag;

    private String udfprintflag1;

    private String udfprintflag2;

    private String udfprintflag3;

    private String cartongroup;

    private String cartonid;

    private Integer retentionTime;

    private String retentionReason;

    private String processRate;

    private String sostatusText;

    private String releasestatusText;

    private Integer seqno;

    public Integer getSeqno() {
        return seqno;
    }

    public void setSeqno(Integer seqno) {
        this.seqno = seqno;
    }

    public List<DocOrderDetails> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DocOrderDetails> detailList) {
        this.detailList = detailList;
    }

    private List<DocOrderDetails>detailList;

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getCustomerid() {
        return customerid;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public String getSoreference1() {
        return soreference1;
    }

    public void setSoreference1(String soreference1) {
        this.soreference1 = soreference1;
    }

    public Date getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Date ordertime) {
        this.ordertime = ordertime;
    }

    public Date getExpectedshipmenttime1() {
        return expectedshipmenttime1;
    }

    public void setExpectedshipmenttime1(Date expectedshipmenttime1) {
        this.expectedshipmenttime1 = expectedshipmenttime1;
    }

    public Date getExpectedshipmenttime2() {
        return expectedshipmenttime2;
    }

    public void setExpectedshipmenttime2(Date expectedshipmenttime2) {
        this.expectedshipmenttime2 = expectedshipmenttime2;
    }

    public Date getRequireddeliverytime() {
        return requireddeliverytime;
    }

    public void setRequireddeliverytime(Date requireddeliverytime) {
        this.requireddeliverytime = requireddeliverytime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getConsigneeid() {
        return consigneeid;
    }

    public void setConsigneeid(String consigneeid) {
        this.consigneeid = consigneeid;
    }

    public String getcContact() {
        return cContact;
    }

    public void setcContact(String cContact) {
        this.cContact = cContact;
    }

    public String getConsigneename() {
        return consigneename;
    }

    public void setConsigneename(String consigneename) {
        this.consigneename = consigneename;
    }

    public String getcAddress1() {
        return cAddress1;
    }

    public void setcAddress1(String cAddress1) {
        this.cAddress1 = cAddress1;
    }

    public String getcAddress2() {
        return cAddress2;
    }

    public void setcAddress2(String cAddress2) {
        this.cAddress2 = cAddress2;
    }

    public String getcAddress3() {
        return cAddress3;
    }

    public void setcAddress3(String cAddress3) {
        this.cAddress3 = cAddress3;
    }

    public String getcAddress4() {
        return cAddress4;
    }

    public void setcAddress4(String cAddress4) {
        this.cAddress4 = cAddress4;
    }

    public String getcCity() {
        return cCity;
    }

    public void setcCity(String cCity) {
        this.cCity = cCity;
    }

    public String getcProvince() {
        return cProvince;
    }

    public void setcProvince(String cProvince) {
        this.cProvince = cProvince;
    }

    public String getcCountry() {
        return cCountry;
    }

    public void setcCountry(String cCountry) {
        this.cCountry = cCountry;
    }

    public String getcFax() {
        return cFax;
    }

    public void setcFax(String cFax) {
        this.cFax = cFax;
    }

    public String getBillingid() {
        return billingid;
    }

    public void setBillingid(String billingid) {
        this.billingid = billingid;
    }

    public String getcZip() {
        return cZip;
    }

    public void setcZip(String cZip) {
        this.cZip = cZip;
    }

    public String getbContact() {
        return bContact;
    }

    public void setbContact(String bContact) {
        this.bContact = bContact;
    }

    public String getBillingname() {
        return billingname;
    }

    public void setBillingname(String billingname) {
        this.billingname = billingname;
    }

    public String getbAddress1() {
        return bAddress1;
    }

    public void setbAddress1(String bAddress1) {
        this.bAddress1 = bAddress1;
    }

    public String getbAddress2() {
        return bAddress2;
    }

    public void setbAddress2(String bAddress2) {
        this.bAddress2 = bAddress2;
    }

    public String getbAddress3() {
        return bAddress3;
    }

    public void setbAddress3(String bAddress3) {
        this.bAddress3 = bAddress3;
    }

    public String getbAddress4() {
        return bAddress4;
    }

    public void setbAddress4(String bAddress4) {
        this.bAddress4 = bAddress4;
    }

    public String getbCity() {
        return bCity;
    }

    public void setbCity(String bCity) {
        this.bCity = bCity;
    }

    public String getbProvince() {
        return bProvince;
    }

    public void setbProvince(String bProvince) {
        this.bProvince = bProvince;
    }

    public String getbZip() {
        return bZip;
    }

    public void setbZip(String bZip) {
        this.bZip = bZip;
    }

    public String getbCountry() {
        return bCountry;
    }

    public void setbCountry(String bCountry) {
        this.bCountry = bCountry;
    }

    public String getDeliverytermsdescr() {
        return deliverytermsdescr;
    }

    public void setDeliverytermsdescr(String deliverytermsdescr) {
        this.deliverytermsdescr = deliverytermsdescr;
    }

    public String getbFax() {
        return bFax;
    }

    public void setbFax(String bFax) {
        this.bFax = bFax;
    }

    public String getbEmail() {
        return bEmail;
    }

    public void setbEmail(String bEmail) {
        this.bEmail = bEmail;
    }

    public String getPaymenttermsdescr() {
        return paymenttermsdescr;
    }

    public void setPaymenttermsdescr(String paymenttermsdescr) {
        this.paymenttermsdescr = paymenttermsdescr;
    }

    public String getDeliveryterms() {
        return deliveryterms;
    }

    public void setDeliveryterms(String deliveryterms) {
        this.deliveryterms = deliveryterms;
    }

    public String getPaymentterms() {
        return paymentterms;
    }

    public void setPaymentterms(String paymentterms) {
        this.paymentterms = paymentterms;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getSostatus() {
        return sostatus;
    }

    public void setSostatus(String sostatus) {
        this.sostatus = sostatus;
    }

    public String getPlaceofdischarge() {
        return placeofdischarge;
    }

    public void setPlaceofdischarge(String placeofdischarge) {
        this.placeofdischarge = placeofdischarge;
    }

    public String getPlaceofdelivery() {
        return placeofdelivery;
    }

    public void setPlaceofdelivery(String placeofdelivery) {
        this.placeofdelivery = placeofdelivery;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getUserdefine1() {
        return userdefine1;
    }

    public void setUserdefine1(String userdefine1) {
        this.userdefine1 = userdefine1;
    }

    public String getUserdefine2() {
        return userdefine2;
    }

    public void setUserdefine2(String userdefine2) {
        this.userdefine2 = userdefine2;
    }

    public String getUserdefine3() {
        return userdefine3;
    }

    public void setUserdefine3(String userdefine3) {
        this.userdefine3 = userdefine3;
    }

    public String getUserdefine4() {
        return userdefine4;
    }

    public void setUserdefine4(String userdefine4) {
        this.userdefine4 = userdefine4;
    }

    public String getUserdefine5() {
        return userdefine5;
    }

    public void setUserdefine5(String userdefine5) {
        this.userdefine5 = userdefine5;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSoreference2() {
        return soreference2;
    }

    public void setSoreference2(String soreference2) {
        this.soreference2 = soreference2;
    }

    public String getSoreference3() {
        return soreference3;
    }

    public void setSoreference3(String soreference3) {
        this.soreference3 = soreference3;
    }

    public String getCarrierid() {
        return carrierid;
    }

    public void setCarrierid(String carrierid) {
        this.carrierid = carrierid;
    }

    public String getCarriername() {
        return carriername;
    }

    public void setCarriername(String carriername) {
        this.carriername = carriername;
    }

    public String getCarrieraddress1() {
        return carrieraddress1;
    }

    public void setCarrieraddress1(String carrieraddress1) {
        this.carrieraddress1 = carrieraddress1;
    }

    public String getCarrieraddress3() {
        return carrieraddress3;
    }

    public void setCarrieraddress3(String carrieraddress3) {
        this.carrieraddress3 = carrieraddress3;
    }

    public String getCarrieraddress2() {
        return carrieraddress2;
    }

    public void setCarrieraddress2(String carrieraddress2) {
        this.carrieraddress2 = carrieraddress2;
    }

    public String getCarrieraddress4() {
        return carrieraddress4;
    }

    public void setCarrieraddress4(String carrieraddress4) {
        this.carrieraddress4 = carrieraddress4;
    }

    public String getCarriercity() {
        return carriercity;
    }

    public void setCarriercity(String carriercity) {
        this.carriercity = carriercity;
    }

    public String getCarrierprovince() {
        return carrierprovince;
    }

    public void setCarrierprovince(String carrierprovince) {
        this.carrierprovince = carrierprovince;
    }

    public String getCarriercountry() {
        return carriercountry;
    }

    public void setCarriercountry(String carriercountry) {
        this.carriercountry = carriercountry;
    }

    public String getCarrierzip() {
        return carrierzip;
    }

    public void setCarrierzip(String carrierzip) {
        this.carrierzip = carrierzip;
    }

    public String getIssuepartyid() {
        return issuepartyid;
    }

    public void setIssuepartyid(String issuepartyid) {
        this.issuepartyid = issuepartyid;
    }

    public String getIssuepartyname() {
        return issuepartyname;
    }

    public void setIssuepartyname(String issuepartyname) {
        this.issuepartyname = issuepartyname;
    }

    public String getiAddress1() {
        return iAddress1;
    }

    public void setiAddress1(String iAddress1) {
        this.iAddress1 = iAddress1;
    }

    public String getiAddress2() {
        return iAddress2;
    }

    public void setiAddress2(String iAddress2) {
        this.iAddress2 = iAddress2;
    }

    public String getiAddress3() {
        return iAddress3;
    }

    public void setiAddress3(String iAddress3) {
        this.iAddress3 = iAddress3;
    }

    public String getiAddress4() {
        return iAddress4;
    }

    public void setiAddress4(String iAddress4) {
        this.iAddress4 = iAddress4;
    }

    public String getiCity() {
        return iCity;
    }

    public void setiCity(String iCity) {
        this.iCity = iCity;
    }

    public String getiProvince() {
        return iProvince;
    }

    public void setiProvince(String iProvince) {
        this.iProvince = iProvince;
    }

    public String getiCountry() {
        return iCountry;
    }

    public void setiCountry(String iCountry) {
        this.iCountry = iCountry;
    }

    public String getiZip() {
        return iZip;
    }

    public void setiZip(String iZip) {
        this.iZip = iZip;
    }

    public Date getLastshipmenttime() {
        return lastshipmenttime;
    }

    public void setLastshipmenttime(Date lastshipmenttime) {
        this.lastshipmenttime = lastshipmenttime;
    }

    public String getEdisendflag() {
        return edisendflag;
    }

    public void setEdisendflag(String edisendflag) {
        this.edisendflag = edisendflag;
    }

    public String getPickingPrintFlag() {
        return pickingPrintFlag;
    }

    public void setPickingPrintFlag(String pickingPrintFlag) {
        this.pickingPrintFlag = pickingPrintFlag;
    }

    public String getCreatesource() {
        return createsource;
    }

    public void setCreatesource(String createsource) {
        this.createsource = createsource;
    }

    public Date getEdisendtime() {
        return edisendtime;
    }

    public void setEdisendtime(Date edisendtime) {
        this.edisendtime = edisendtime;
    }

    public Date getEdisendtime2() {
        return edisendtime2;
    }

    public void setEdisendtime2(Date edisendtime2) {
        this.edisendtime2 = edisendtime2;
    }

    public Date getEdisendtime3() {
        return edisendtime3;
    }

    public void setEdisendtime3(Date edisendtime3) {
        this.edisendtime3 = edisendtime3;
    }

    public Date getEdisendtime4() {
        return edisendtime4;
    }

    public void setEdisendtime4(Date edisendtime4) {
        this.edisendtime4 = edisendtime4;
    }

    public Date getEdisendtime5() {
        return edisendtime5;
    }

    public void setEdisendtime5(Date edisendtime5) {
        this.edisendtime5 = edisendtime5;
    }

    public String getbTel1() {
        return bTel1;
    }

    public void setbTel1(String bTel1) {
        this.bTel1 = bTel1;
    }

    public String getbTel2() {
        return bTel2;
    }

    public void setbTel2(String bTel2) {
        this.bTel2 = bTel2;
    }

    public String getCarriercontact() {
        return carriercontact;
    }

    public void setCarriercontact(String carriercontact) {
        this.carriercontact = carriercontact;
    }

    public String getCarriermail() {
        return carriermail;
    }

    public void setCarriermail(String carriermail) {
        this.carriermail = carriermail;
    }

    public String getCarrierfax() {
        return carrierfax;
    }

    public void setCarrierfax(String carrierfax) {
        this.carrierfax = carrierfax;
    }

    public String getCarriertel1() {
        return carriertel1;
    }

    public void setCarriertel1(String carriertel1) {
        this.carriertel1 = carriertel1;
    }

    public String getCarriertel2() {
        return carriertel2;
    }

    public void setCarriertel2(String carriertel2) {
        this.carriertel2 = carriertel2;
    }

    public String getcMail() {
        return cMail;
    }

    public void setcMail(String cMail) {
        this.cMail = cMail;
    }

    public String getcTel1() {
        return cTel1;
    }

    public void setcTel1(String cTel1) {
        this.cTel1 = cTel1;
    }

    public String getcTel2() {
        return cTel2;
    }

    public void setcTel2(String cTel2) {
        this.cTel2 = cTel2;
    }

    public String getiContact() {
        return iContact;
    }

    public void setiContact(String iContact) {
        this.iContact = iContact;
    }

    public String getiMail() {
        return iMail;
    }

    public void setiMail(String iMail) {
        this.iMail = iMail;
    }

    public String getiFax() {
        return iFax;
    }

    public void setiFax(String iFax) {
        this.iFax = iFax;
    }

    public String getiTel1() {
        return iTel1;
    }

    public void setiTel1(String iTel1) {
        this.iTel1 = iTel1;
    }

    public String getiTel2() {
        return iTel2;
    }

    public void setiTel2(String iTel2) {
        this.iTel2 = iTel2;
    }

    public String getReleasestatus() {
        return releasestatus;
    }

    public void setReleasestatus(String releasestatus) {
        this.releasestatus = releasestatus;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation = transportation;
    }

    public String getSoreference4() {
        return soreference4;
    }

    public void setSoreference4(String soreference4) {
        this.soreference4 = soreference4;
    }

    public String getSoreference5() {
        return soreference5;
    }

    public void setSoreference5(String soreference5) {
        this.soreference5 = soreference5;
    }

//    public String gethEdi01() {
//        return hEdi01;
//    }
//
//    public void sethEdi01(String hEdi01) {
//        this.hEdi01 = hEdi01;
//    }
//
    public String gethEdi02() {
        return hEdi02;
    }

    public void sethEdi02(String hEdi02) {
        this.hEdi02 = hEdi02;
    }
//
//    public String gethEdi03() {
//        return hEdi03;
//    }
//
//    public void sethEdi03(String hEdi03) {
//        this.hEdi03 = hEdi03;
//    }
//
//    public String gethEdi04() {
//        return hEdi04;
//    }
//
//    public void sethEdi04(String hEdi04) {
//        this.hEdi04 = hEdi04;
//    }
//
//    public String gethEdi05() {
//        return hEdi05;
//    }
//
//    public void sethEdi05(String hEdi05) {
//        this.hEdi05 = hEdi05;
//    }
//
//    public String gethEdi06() {
//        return hEdi06;
//    }
//
//    public void sethEdi06(String hEdi06) {
//        this.hEdi06 = hEdi06;
//    }
//
//    public String gethEdi07() {
//        return hEdi07;
//    }
//
//    public void sethEdi07(String hEdi07) {
//        this.hEdi07 = hEdi07;
//    }
//
//    public String gethEdi08() {
//        return hEdi08;
//    }
//
//    public void sethEdi08(String hEdi08) {
//        this.hEdi08 = hEdi08;
//    }
//
//    public Double gethEdi09() {
//        return hEdi09;
//    }
//
//    public void sethEdi09(Double hEdi09) {
//        this.hEdi09 = hEdi09;
//    }
//
//    public Double gethEdi10() {
//        return hEdi10;
//    }
//
//    public void sethEdi10(Double hEdi10) {
//        this.hEdi10 = hEdi10;
//    }

    public String getUserdefine6() {
        return userdefine6;
    }

    public void setUserdefine6(String userdefine6) {
        this.userdefine6 = userdefine6;
    }

    public String getOrderPrintFlag() {
        return orderPrintFlag;
    }

    public void setOrderPrintFlag(String orderPrintFlag) {
        this.orderPrintFlag = orderPrintFlag;
    }

    public String getRfgettask() {
        return rfgettask;
    }

    public void setRfgettask(String rfgettask) {
        this.rfgettask = rfgettask;
    }

    public String getWarehouseid() {
        return warehouseid;
    }

    public void setWarehouseid(String warehouseid) {
        this.warehouseid = warehouseid;
    }

    public String getErpcancelflag() {
        return erpcancelflag;
    }

    public void setErpcancelflag(String erpcancelflag) {
        this.erpcancelflag = erpcancelflag;
    }

    public String getZonegroup() {
        return zonegroup;
    }

    public void setZonegroup(String zonegroup) {
        this.zonegroup = zonegroup;
    }

    public Date getMedicalxmltime() {
        return medicalxmltime;
    }

    public void setMedicalxmltime(Date medicalxmltime) {
        this.medicalxmltime = medicalxmltime;
    }

    public String getPlaceofloading() {
        return placeofloading;
    }

    public void setPlaceofloading(String placeofloading) {
        this.placeofloading = placeofloading;
    }

    public String getRequiredeliveryno() {
        return requiredeliveryno;
    }

    public void setRequiredeliveryno(String requiredeliveryno) {
        this.requiredeliveryno = requiredeliveryno;
    }

    public String getSinglematch() {
        return singlematch;
    }

    public void setSinglematch(String singlematch) {
        this.singlematch = singlematch;
    }

    public String getSerialnocatch() {
        return serialnocatch;
    }

    public void setSerialnocatch(String serialnocatch) {
        this.serialnocatch = serialnocatch;
    }

    public String getFollowup() {
        return followup;
    }

    public void setFollowup(String followup) {
        this.followup = followup;
    }

    public String getUserdefinea() {
        return userdefinea;
    }

    public void setUserdefinea(String userdefinea) {
        this.userdefinea = userdefinea;
    }

    public String getUserdefineb() {
        return userdefineb;
    }

    public void setUserdefineb(String userdefineb) {
        this.userdefineb = userdefineb;
    }

    public String getSalesorderno() {
        return salesorderno;
    }

    public void setSalesorderno(String salesorderno) {
        this.salesorderno = salesorderno;
    }

    public String getInvoiceprintflag() {
        return invoiceprintflag;
    }

    public void setInvoiceprintflag(String invoiceprintflag) {
        this.invoiceprintflag = invoiceprintflag;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getInvoicetitle() {
        return invoicetitle;
    }

    public void setInvoicetitle(String invoicetitle) {
        this.invoicetitle = invoicetitle;
    }

    public String getInvoicetype() {
        return invoicetype;
    }

    public void setInvoicetype(String invoicetype) {
        this.invoicetype = invoicetype;
    }

    public String getInvoiceitem() {
        return invoiceitem;
    }

    public void setInvoiceitem(String invoiceitem) {
        this.invoiceitem = invoiceitem;
    }

    public Double getInvoiceamount() {
        return invoiceamount;
    }

    public void setInvoiceamount(Double invoiceamount) {
        this.invoiceamount = invoiceamount;
    }

    public String getArchiveflag() {
        return archiveflag;
    }

    public void setArchiveflag(String archiveflag) {
        this.archiveflag = archiveflag;
    }

    public String getConsigneenameE() {
        return consigneenameE;
    }

    public void setConsigneenameE(String consigneenameE) {
        this.consigneenameE = consigneenameE;
    }

    public String getPuttolocation() {
        return puttolocation;
    }

    public void setPuttolocation(String puttolocation) {
        this.puttolocation = puttolocation;
    }

    public String getFulAlc() {
        return fulAlc;
    }

    public void setFulAlc(String fulAlc) {
        this.fulAlc = fulAlc;
    }

    public String getDeliveryno() {
        return deliveryno;
    }

    public void setDeliveryno(String deliveryno) {
        this.deliveryno = deliveryno;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getWaveno() {
        return waveno;
    }

    public void setWaveno(String waveno) {
        this.waveno = waveno;
    }

    public Double getAllocationcount() {
        return allocationcount;
    }

    public void setAllocationcount(Double allocationcount) {
        this.allocationcount = allocationcount;
    }

    public String getExpressprintflag() {
        return expressprintflag;
    }

    public void setExpressprintflag(String expressprintflag) {
        this.expressprintflag = expressprintflag;
    }

    public String getDeliverynoteprintflag() {
        return deliverynoteprintflag;
    }

    public void setDeliverynoteprintflag(String deliverynoteprintflag) {
        this.deliverynoteprintflag = deliverynoteprintflag;
    }

    public String getWeightingflag() {
        return weightingflag;
    }

    public void setWeightingflag(String weightingflag) {
        this.weightingflag = weightingflag;
    }

    public String getUdfprintflag1() {
        return udfprintflag1;
    }

    public void setUdfprintflag1(String udfprintflag1) {
        this.udfprintflag1 = udfprintflag1;
    }

    public String getUdfprintflag2() {
        return udfprintflag2;
    }

    public void setUdfprintflag2(String udfprintflag2) {
        this.udfprintflag2 = udfprintflag2;
    }

    public String getUdfprintflag3() {
        return udfprintflag3;
    }

    public void setUdfprintflag3(String udfprintflag3) {
        this.udfprintflag3 = udfprintflag3;
    }

    public String getCartongroup() {
        return cartongroup;
    }

    public void setCartongroup(String cartongroup) {
        this.cartongroup = cartongroup;
    }

    public String getCartonid() {
        return cartonid;
    }

    public void setCartonid(String cartonid) {
        this.cartonid = cartonid;
    }

//    public String gethEdi11() {
//        return hEdi11;
//    }
//
//    public void sethEdi11(String hEdi11) {
//        this.hEdi11 = hEdi11;
//    }


    public Integer getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Integer retentionTime) {
        this.retentionTime = retentionTime;
    }

    public String getRetentionReason() {
        return retentionReason;
    }

    public void setRetentionReason(String retentionReason) {
        this.retentionReason = retentionReason;
    }

    public String getProcessRate() {
        return processRate;
    }

    public void setProcessRate(String processRate) {
        this.processRate = processRate;
    }

    public String getSostatusText() {
        return sostatusText;
    }

    public void setSostatusText(String sostatusText) {
        this.sostatusText = sostatusText;
    }

    public String getReleasestatusText() {
        return releasestatusText;
    }

    public void setReleasestatusText(String releasestatusText) {
        this.releasestatusText = releasestatusText;
    }


//    public String gethEdi01() {
//        return hEdi01;
//    }
//
//    public void sethEdi01(String hEdi01) {
//        this.hEdi01 = hEdi01;
//    }
//
//    public String gethEdi02() {
//        return hEdi02;
//    }
//
//    public void sethEdi02(String hEdi02) {
//        this.hEdi02 = hEdi02;
//    }
//
//    public String gethEdi03() {
//        return hEdi03;
//    }
//
//    public void sethEdi03(String hEdi03) {
//        this.hEdi03 = hEdi03;
//    }
//
//    public String gethEdi04() {
//        return hEdi04;
//    }
//
//    public void sethEdi04(String hEdi04) {
//        this.hEdi04 = hEdi04;
//    }
//
//    public String gethEdi05() {
//        return hEdi05;
//    }
//
//    public void sethEdi05(String hEdi05) {
//        this.hEdi05 = hEdi05;
//    }
//
//    public String gethEdi06() {
//        return hEdi06;
//    }
//
//    public void sethEdi06(String hEdi06) {
//        this.hEdi06 = hEdi06;
//    }
//
//    public String gethEdi07() {
//        return hEdi07;
//    }
//
//    public void sethEdi07(String hEdi07) {
//        this.hEdi07 = hEdi07;
//    }
//
//    public String gethEdi08() {
//        return hEdi08;
//    }
//
//    public void sethEdi08(String hEdi08) {
//        this.hEdi08 = hEdi08;
//    }
//
//    public Double gethEdi09() {
//        return hEdi09;
//    }
//
//    public void sethEdi09(Double hEdi09) {
//        this.hEdi09 = hEdi09;
//    }
//
//    public Double gethEdi10() {
//        return hEdi10;
//    }
//
//    public void sethEdi10(Double hEdi10) {
//        this.hEdi10 = hEdi10;
//    }
//
//    public String gethEdi11() {
//        return hEdi11;
//    }
//
//    public void sethEdi11(String hEdi11) {
//        this.hEdi11 = hEdi11;
//    }

    @Override
    public String toString() {
        return "DocOrderHeader{" +
        "orderno=" + orderno +
        ", customerid=" + customerid +
        ", soreference1=" + soreference1 +
        ", ordertime=" + ordertime +
        ", expectedshipmenttime1=" + expectedshipmenttime1 +
        ", expectedshipmenttime2=" + expectedshipmenttime2 +
        ", requireddeliverytime=" + requireddeliverytime +
        ", priority=" + priority +
        ", consigneeid=" + consigneeid +
        ", cContact=" + cContact +
        ", consigneename=" + consigneename +
        ", cAddress1=" + cAddress1 +
        ", cAddress2=" + cAddress2 +
        ", cAddress3=" + cAddress3 +
        ", cAddress4=" + cAddress4 +
        ", cCity=" + cCity +
        ", cProvince=" + cProvince +
        ", cCountry=" + cCountry +
        ", cFax=" + cFax +
        ", billingid=" + billingid +
        ", cZip=" + cZip +
        ", bContact=" + bContact +
        ", billingname=" + billingname +
        ", bAddress1=" + bAddress1 +
        ", bAddress2=" + bAddress2 +
        ", bAddress3=" + bAddress3 +
        ", bAddress4=" + bAddress4 +
        ", bCity=" + bCity +
        ", bProvince=" + bProvince +
        ", bZip=" + bZip +
        ", bCountry=" + bCountry +
        ", deliverytermsdescr=" + deliverytermsdescr +
        ", bFax=" + bFax +
        ", bEmail=" + bEmail +
        ", paymenttermsdescr=" + paymenttermsdescr +
        ", deliveryterms=" + deliveryterms +
        ", paymentterms=" + paymentterms +
        ", door=" + door +
        ", route=" + route +
        ", stop=" + stop +
        ", sostatus=" + sostatus +
        ", placeofdischarge=" + placeofdischarge +
        ", placeofdelivery=" + placeofdelivery +
        ", ordertype=" + ordertype +
        ", userdefine1=" + userdefine1 +
        ", userdefine2=" + userdefine2 +
        ", userdefine3=" + userdefine3 +
        ", userdefine4=" + userdefine4 +
        ", userdefine5=" + userdefine5 +
        ", addtime=" + addtime +
        ", addwho=" + addwho +
        ", edittime=" + edittime +
        ", editwho=" + editwho +
        ", notes=" + notes +
        ", soreference2=" + soreference2 +
        ", soreference3=" + soreference3 +
        ", carrierid=" + carrierid +
        ", carriername=" + carriername +
        ", carrieraddress1=" + carrieraddress1 +
        ", carrieraddress3=" + carrieraddress3 +
        ", carrieraddress2=" + carrieraddress2 +
        ", carrieraddress4=" + carrieraddress4 +
        ", carriercity=" + carriercity +
        ", carrierprovince=" + carrierprovince +
        ", carriercountry=" + carriercountry +
        ", carrierzip=" + carrierzip +
        ", issuepartyid=" + issuepartyid +
        ", issuepartyname=" + issuepartyname +
        ", iAddress1=" + iAddress1 +
        ", iAddress2=" + iAddress2 +
        ", iAddress3=" + iAddress3 +
        ", iAddress4=" + iAddress4 +
        ", iCity=" + iCity +
        ", iProvince=" + iProvince +
        ", iCountry=" + iCountry +
        ", iZip=" + iZip +
        ", lastshipmenttime=" + lastshipmenttime +
        ", edisendflag=" + edisendflag +
        ", pickingPrintFlag=" + pickingPrintFlag +
        ", createsource=" + createsource +
        ", edisendtime=" + edisendtime +
        ", edisendtime2=" + edisendtime2 +
        ", edisendtime3=" + edisendtime3 +
        ", edisendtime4=" + edisendtime4 +
        ", edisendtime5=" + edisendtime5 +
        ", bTel1=" + bTel1 +
        ", bTel2=" + bTel2 +
        ", carriercontact=" + carriercontact +
        ", carriermail=" + carriermail +
        ", carrierfax=" + carrierfax +
        ", carriertel1=" + carriertel1 +
        ", carriertel2=" + carriertel2 +
        ", cMail=" + cMail +
        ", cTel1=" + cTel1 +
        ", cTel2=" + cTel2 +
        ", iContact=" + iContact +
        ", iMail=" + iMail +
        ", iFax=" + iFax +
        ", iTel1=" + iTel1 +
        ", iTel2=" + iTel2 +
        ", releasestatus=" + releasestatus +
        ", transportation=" + transportation +
        ", soreference4=" + soreference4 +
        ", soreference5=" + soreference5 +
//        ", hEdi01=" + hEdi01 +
//        ", hEdi02=" + hEdi02 +
//        ", hEdi03=" + hEdi03 +
//        ", hEdi04=" + hEdi04 +
//        ", hEdi05=" + hEdi05 +
//        ", hEdi06=" + hEdi06 +
//        ", hEdi07=" + hEdi07 +
//        ", hEdi08=" + hEdi08 +
//        ", hEdi09=" + hEdi09 +
//        ", hEdi10=" + hEdi10 +
        ", userdefine6=" + userdefine6 +
        ", orderPrintFlag=" + orderPrintFlag +
        ", rfgettask=" + rfgettask +
        ", warehouseid=" + warehouseid +
        ", erpcancelflag=" + erpcancelflag +
        ", zonegroup=" + zonegroup +
        ", medicalxmltime=" + medicalxmltime +
        ", placeofloading=" + placeofloading +
        ", requiredeliveryno=" + requiredeliveryno +
        ", singlematch=" + singlematch +
        ", serialnocatch=" + serialnocatch +
        ", followup=" + followup +
        ", userdefinea=" + userdefinea +
        ", userdefineb=" + userdefineb +
        ", salesorderno=" + salesorderno +
        ", invoiceprintflag=" + invoiceprintflag +
        ", invoiceno=" + invoiceno +
        ", invoicetitle=" + invoicetitle +
        ", invoicetype=" + invoicetype +
        ", invoiceitem=" + invoiceitem +
        ", invoiceamount=" + invoiceamount +
        ", archiveflag=" + archiveflag +
        ", consigneenameE=" + consigneenameE +
        ", puttolocation=" + puttolocation +
        ", fulAlc=" + fulAlc +
        ", deliveryno=" + deliveryno +
        ", channel=" + channel +
        ", waveno=" + waveno +
        ", allocationcount=" + allocationcount +
        ", expressprintflag=" + expressprintflag +
        ", deliverynoteprintflag=" + deliverynoteprintflag +
        ", weightingflag=" + weightingflag +
        ", udfprintflag1=" + udfprintflag1 +
        ", udfprintflag2=" + udfprintflag2 +
        ", udfprintflag3=" + udfprintflag3 +
        ", cartongroup=" + cartongroup +
        ", cartonid=" + cartonid +
        "}";
    }
}
