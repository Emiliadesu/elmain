package me.zhengjie.support.chinaPost;

public class EMSResponse {

    private boolean success;

    private String waybillNo;  //运单号

    private String routeCode; //四段码（分拣码）

    private String packageCode; // 集包地编码

    private String packageCodeName;  //集包地名称

    private String markDestinationCode;  //大头笔编码

    private String markDestinationName;  // 大头笔

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getPackageCodeName() {
        return packageCodeName;
    }

    public void setPackageCodeName(String packageCodeName) {
        this.packageCodeName = packageCodeName;
    }

    public String getMarkDestinationCode() {
        return markDestinationCode;
    }

    public void setMarkDestinationCode(String markDestinationCode) {
        this.markDestinationCode = markDestinationCode;
    }

    public String getMarkDestinationName() {
        return markDestinationName;
    }

    public void setMarkDestinationName(String markDestinationName) {
        this.markDestinationName = markDestinationName;
    }
}
