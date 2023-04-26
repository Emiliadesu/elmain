package me.zhengjie.support.jingdong;

import java.util.List;

public class JDLdopReceiveTraceGetResponse {

    private String code;  //返回编码

    private String messsage;  //返回信息

    private List<Data> data;

    public static class Data{

        private String opeTitle;

        private String opeRemark;

        private String opeName;

        private String opeTime;

        private String waybillCode;

        private String courier;

        private String courierTel;

        public String getOpeTitle() {
            return opeTitle;
        }

        public void setOpeTitle(String opeTitle) {
            this.opeTitle = opeTitle;
        }

        public String getOpeRemark() {
            return opeRemark;
        }

        public void setOpeRemark(String opeRemark) {
            this.opeRemark = opeRemark;
        }

        public String getOpeName() {
            return opeName;
        }

        public void setOpeName(String opeName) {
            this.opeName = opeName;
        }

        public String getOpeTime() {
            return opeTime;
        }

        public void setOpeTime(String opeTime) {
            this.opeTime = opeTime;
        }

        public String getWaybillCode() {
            return waybillCode;
        }

        public void setWaybillCode(String waybillCode) {
            this.waybillCode = waybillCode;
        }

        public String getCourier() {
            return courier;
        }

        public void setCourier(String courier) {
            this.courier = courier;
        }

        public String getCourierTel() {
            return courierTel;
        }

        public void setCourierTel(String courierTel) {
            this.courierTel = courierTel;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
