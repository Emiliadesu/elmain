package me.zhengjie.support.jingdong;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class JDLdopWaybillReceiveRequest implements Serializable {
    private String salePlat;  //销售平台（0010001京东商城；0010002天猫、淘宝订单；0030001其他平台）

    private String customerCode;  // 商家编码

    private String orderId;

    private String thrOrderId;   //销售平台订单号(例如京东订单号或天猫订单号等等

    private String senderName;  //寄件人姓名，说明：不能为生僻字，暂不支持emoji

    private String senderAddress;

    private String senderTel;

    private String senderMobile;

    private String senderPostcode;  //邮编

    private String receiveName;  //收件人名

    private String receiveAddress;

    private String province;

    private String city;

    private String county;

    private String town;

    private Integer provinceId;  //收件人省编码

    private Integer cityId;

    private Integer countyId;

    private Integer townId;

    private Integer siteType;

    private Integer siteId;

    private String siteName;  //站点名称

    private String receiveTel;

    private String receiveMobile;

    private String postcode;  //收件人邮编

    private Integer packageCount;  //包裹数(大于0，小于1000)

    private BigDecimal weight;

    private BigDecimal vloumLong;

    private BigDecimal vloumWidth;

    private BigDecimal vloumHeight;

    private BigDecimal vloumn;

    private String description;  //商品描述

    private Integer collectionValue;  //是否代收货款(是：1，否：0。不填或者超出范围，默认是0)

    private BigDecimal collectionMoney;  //代收货款金额

    private Integer guaranteeValue;  //是否保价(是：1，否：0。不填或者超出范围，默认是0)

    private BigDecimal guaranteeValueAmount;  //保价金额

    private Integer signReturn;  //签单返还(签单返还类型：0-不返单，1-普通返单，2-校验身份返单，3-电子签返单，4-电子返单+普通返单)

    private Integer aging;//时效(普通：1，工作日：2，非工作日：3，晚间：4。O2O一小时达：5。O2O定时达：6。不填或者超出范围，默认是1)

    private Integer transType;  //运输类型(陆运：1，航空：2。不填默认是1)

    private String remark;  //备注

    private Integer goodsType;  //配送业务类型（ 1:普通，2:生鲜常温，5:鲜活，6:控温，7:冷藏，8:冷冻，9:深冷）默认是1；若是生鲜相关产品，则填写枚举2、5、6、7、8、9，否则不填或填1

    private Integer orderType; //运单类型。(普通外单：0，O2O外单：1)默认为0

    private String shopCode; //门店编码(只O2O运单需要传，普通运单不需要传)

    private String orderSendTime;  //预约配送时间（格式：yyyy-MM-dd HH:mm:ss）

    private String warehouseCode;  //发货仓编码

    private Integer areaProvId;  //接货省ID

    private Integer areaCityId;

    private Timestamp shipmentStartTime; //配送起始时间

    private Timestamp shipmentEndTime;   //配送结束时间

    private String idNumber;  //身份证号

    private String addedService;  //拓展业务

    private String extendField1;

    private String extendField2;

    private String extendField3;

    private Integer extendField4;

    private Integer extendField5;

    private String senderCompany;

    private String receiveCompany;

    private String goods;  //托寄物名称

    private Integer goodsCount;  //寄托物数量

    private Integer promiseTimeType;  //产品类型（1：特惠送 2：特快送 4：城际闪送 7：微小件 8: 生鲜专送 16：生鲜特快 17、生鲜特惠 20:函数达 21：特惠包裹 26：冷链专送）

    private BigDecimal freight;  //运费

    private Timestamp pickUpStartTime;  //2099-09-18 08:30:00	预约取件开始时间

    private Timestamp pickUpEndTime;

    private String unpackingInspection;  //开箱验货标识（1：随心验(收费)，2：开商品包装验货，3：开物流包装验货，4：不支持开箱验货）

    private List<String> boxCode; //商家箱号

    private String fileUrl;  //https://www.jd.com/	函速达的文件地址，如果pickMethod是上门收货，则此字段可以不填

    private String pickMethod;  //取件方式（填写中文：【上门收货】或【自送】，不填写：商家无诉求，此字段无意义）

    private List<String> customerBoxCode;  //客户箱型编号

    private List<Integer> customerBoxNumber;  //客户箱型箱数

    private String salesChannel;  //销售渠道

    public String getSalePlat() {
        return salePlat;
    }

    public void setSalePlat(String salePlat) {
        this.salePlat = salePlat;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getThrOrderId() {
        return thrOrderId;
    }

    public void setThrOrderId(String thrOrderId) {
        this.thrOrderId = thrOrderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getSenderTel() {
        return senderTel;
    }

    public void setSenderTel(String senderTel) {
        this.senderTel = senderTel;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getSenderPostcode() {
        return senderPostcode;
    }

    public void setSenderPostcode(String senderPostcode) {
        this.senderPostcode = senderPostcode;
    }

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Integer getCountyId() {
        return countyId;
    }

    public void setCountyId(Integer countyId) {
        this.countyId = countyId;
    }

    public Integer getTownId() {
        return townId;
    }

    public void setTownId(Integer townId) {
        this.townId = townId;
    }

    public Integer getSiteType() {
        return siteType;
    }

    public void setSiteType(Integer siteType) {
        this.siteType = siteType;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getReceiveTel() {
        return receiveTel;
    }

    public void setReceiveTel(String receiveTel) {
        this.receiveTel = receiveTel;
    }

    public String getReceiveMobile() {
        return receiveMobile;
    }

    public void setReceiveMobile(String receiveMobile) {
        this.receiveMobile = receiveMobile;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVloumLong() {
        return vloumLong;
    }

    public void setVloumLong(BigDecimal vloumLong) {
        this.vloumLong = vloumLong;
    }

    public BigDecimal getVloumWidth() {
        return vloumWidth;
    }

    public void setVloumWidth(BigDecimal vloumWidth) {
        this.vloumWidth = vloumWidth;
    }

    public BigDecimal getVloumHeight() {
        return vloumHeight;
    }

    public void setVloumHeight(BigDecimal vloumHeight) {
        this.vloumHeight = vloumHeight;
    }

    public BigDecimal getVloumn() {
        return vloumn;
    }

    public void setVloumn(BigDecimal vloumn) {
        this.vloumn = vloumn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCollectionValue() {
        return collectionValue;
    }

    public void setCollectionValue(Integer collectionValue) {
        this.collectionValue = collectionValue;
    }

    public BigDecimal getCollectionMoney() {
        return collectionMoney;
    }

    public void setCollectionMoney(BigDecimal collectionMoney) {
        this.collectionMoney = collectionMoney;
    }

    public Integer getGuaranteeValue() {
        return guaranteeValue;
    }

    public void setGuaranteeValue(Integer guaranteeValue) {
        this.guaranteeValue = guaranteeValue;
    }

    public BigDecimal getGuaranteeValueAmount() {
        return guaranteeValueAmount;
    }

    public void setGuaranteeValueAmount(BigDecimal guaranteeValueAmount) {
        this.guaranteeValueAmount = guaranteeValueAmount;
    }

    public Integer getSignReturn() {
        return signReturn;
    }

    public void setSignReturn(Integer signReturn) {
        this.signReturn = signReturn;
    }

    public Integer getAging() {
        return aging;
    }

    public void setAging(Integer aging) {
        this.aging = aging;
    }

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(Integer goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public String getOrderSendTime() {
        return orderSendTime;
    }

    public void setOrderSendTime(String orderSendTime) {
        this.orderSendTime = orderSendTime;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public Integer getAreaProvId() {
        return areaProvId;
    }

    public void setAreaProvId(Integer areaProvId) {
        this.areaProvId = areaProvId;
    }

    public Integer getAreaCityId() {
        return areaCityId;
    }

    public void setAreaCityId(Integer areaCityId) {
        this.areaCityId = areaCityId;
    }

    public Timestamp getShipmentStartTime() {
        return shipmentStartTime;
    }

    public void setShipmentStartTime(Timestamp shipmentStartTime) {
        this.shipmentStartTime = shipmentStartTime;
    }

    public Timestamp getShipmentEndTime() {
        return shipmentEndTime;
    }

    public void setShipmentEndTime(Timestamp shipmentEndTime) {
        this.shipmentEndTime = shipmentEndTime;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAddedService() {
        return addedService;
    }

    public void setAddedService(String addedService) {
        this.addedService = addedService;
    }

    public String getExtendField1() {
        return extendField1;
    }

    public void setExtendField1(String extendField1) {
        this.extendField1 = extendField1;
    }

    public String getExtendField2() {
        return extendField2;
    }

    public void setExtendField2(String extendField2) {
        this.extendField2 = extendField2;
    }

    public String getExtendField3() {
        return extendField3;
    }

    public void setExtendField3(String extendField3) {
        this.extendField3 = extendField3;
    }

    public Integer getExtendField4() {
        return extendField4;
    }

    public void setExtendField4(Integer extendField4) {
        this.extendField4 = extendField4;
    }

    public Integer getExtendField5() {
        return extendField5;
    }

    public void setExtendField5(Integer extendField5) {
        this.extendField5 = extendField5;
    }

    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }

    public String getReceiveCompany() {
        return receiveCompany;
    }

    public void setReceiveCompany(String receiveCompany) {
        this.receiveCompany = receiveCompany;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public Integer getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(Integer goodsCount) {
        this.goodsCount = goodsCount;
    }

    public Integer getPromiseTimeType() {
        return promiseTimeType;
    }

    public void setPromiseTimeType(Integer promiseTimeType) {
        this.promiseTimeType = promiseTimeType;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public Timestamp getPickUpStartTime() {
        return pickUpStartTime;
    }

    public void setPickUpStartTime(Timestamp pickUpStartTime) {
        this.pickUpStartTime = pickUpStartTime;
    }

    public Timestamp getPickUpEndTime() {
        return pickUpEndTime;
    }

    public void setPickUpEndTime(Timestamp pickUpEndTime) {
        this.pickUpEndTime = pickUpEndTime;
    }

    public String getUnpackingInspection() {
        return unpackingInspection;
    }

    public void setUnpackingInspection(String unpackingInspection) {
        this.unpackingInspection = unpackingInspection;
    }

    public List<String> getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(List<String> boxCode) {
        this.boxCode = boxCode;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPickMethod() {
        return pickMethod;
    }

    public void setPickMethod(String pickMethod) {
        this.pickMethod = pickMethod;
    }

    public List<String> getCustomerBoxCode() {
        return customerBoxCode;
    }

    public void setCustomerBoxCode(List<String> customerBoxCode) {
        this.customerBoxCode = customerBoxCode;
    }

    public List<Integer> getCustomerBoxNumber() {
        return customerBoxNumber;
    }

    public void setCustomerBoxNumber(List<Integer> customerBoxNumber) {
        this.customerBoxNumber = customerBoxNumber;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }
}
