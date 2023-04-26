package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class CBOrderOperateRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "crossBorder.orderOperate";
    }
    @Override
    public String getKeyWord() {
        return this.getOrderId();
    }


    /**
     * 订单id
     */
    @JSONField(name = "order_id")
    private String orderId;

    @JSONField(name = "vendor")
    private String vendor;

    @JSONField(name = "status")
    private String status;

    @JSONField(name = "occurrence_time")
    private String occurrenceTime;

    @JSONField(name = "transportation")
    private Transportation transportation;

    @JSONField(name = "package")
    private Package apackage;

    @JSONField(name = "error_info")
    private ErrorInfo error_info;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOccurrenceTime() {
        return occurrenceTime;
    }

    public void setOccurrenceTime(String occurrenceTime) {
        this.occurrenceTime = occurrenceTime;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public ErrorInfo getError_info() {
        return error_info;
    }

    public void setError_info(ErrorInfo error_info) {
        this.error_info = error_info;
    }

    public Package getApackage() {
        return apackage;
    }

    public void setApackage(Package apackage) {
        this.apackage = apackage;
    }

    public static class Transportation{
        @JSONField(name = "domestic_carrier")
        private String domesticCarrier;

        @JSONField(name = "domestic_trans_no")
        private String domesticTransNo;

        @JSONField(name = "domestic_ship_address")
        private String domesticShipAddress;

        public String getDomesticCarrier() {
            return domesticCarrier;
        }

        public void setDomesticCarrier(String domesticCarrier) {
            this.domesticCarrier = domesticCarrier;
        }

        public String getDomesticTransNo() {
            return domesticTransNo;
        }

        public void setDomesticTransNo(String domesticTransNo) {
            this.domesticTransNo = domesticTransNo;
        }

        public String getDomesticShipAddress() {
            return domesticShipAddress;
        }

        public void setDomesticShipAddress(String domesticShipAddress) {
            this.domesticShipAddress = domesticShipAddress;
        }

        @Override
        public String toString() {
            return "Transportation{" +
                    "domesticCarrier='" + domesticCarrier + '\'' +
                    ", domesticTransNo='" + domesticTransNo + '\'' +
                    ", domesticShipAddress='" + domesticShipAddress + '\'' +
                    '}';
        }
    }

    public static class Package{
        /**
         * 包材信息
         */
        @JSONField(name = "packing_materials")
        private List<PackingMaterials> packingMaterials;

        /**
         * 耗材信息
         * 非必填
         */
        @JSONField(name = "consumables_materials")
        private List<ConsumablesMaterials> consumablesMaterials;

        /**
         * 耗材重量
         */
        @JSONField(name = "package_weight")
        private Long packageWeight;

        public static class PackingMaterials{
            /**
             * 包材编码
             */
            @JSONField(name = "packing_material_code")
            private String packingMaterialCode;

            /**
             * 包材名称
             */
            @JSONField(name = "packing_material_name")
            private String packingMaterialName;

            /**
             * 包材数量
             */
            @JSONField(name = "packing_material_num")
            private Integer packingMaterialNum;

            public String getPackingMaterialCode() {
                return packingMaterialCode;
            }

            public void setPackingMaterialCode(String packingMaterialCode) {
                this.packingMaterialCode = packingMaterialCode;
            }

            public String getPackingMaterialName() {
                return packingMaterialName;
            }

            public void setPackingMaterialName(String packingMaterialName) {
                this.packingMaterialName = packingMaterialName;
            }

            public Integer getPackingMaterialNum() {
                return packingMaterialNum;
            }

            public void setPackingMaterialNum(Integer packingMaterialNum) {
                this.packingMaterialNum = packingMaterialNum;
            }

            @Override
            public String toString() {
                return "PackingMaterials{" +
                        "packingMaterialCode='" + packingMaterialCode + '\'' +
                        ", packingMaterialName='" + packingMaterialName + '\'' +
                        ", packingMaterialNum=" + packingMaterialNum +
                        '}';
            }
        }

        public static class ConsumablesMaterials{
            /**
             * 包材编码
             */
            @JSONField(name = "consumables_code")
            private String consumablesCode;

            /**
             * 包材名称
             */
            @JSONField(name = "consumables_name")
            private String consumablesName;

            /**
             * 包材数量
             */
            @JSONField(name = "consumables_num")
            private Integer consumablesNum;

            public String getConsumablesCode() {
                return consumablesCode;
            }

            public void setConsumablesCode(String consumablesCode) {
                this.consumablesCode = consumablesCode;
            }

            public String getConsumablesName() {
                return consumablesName;
            }

            public void setConsumablesName(String consumablesName) {
                this.consumablesName = consumablesName;
            }

            public Integer getConsumablesNum() {
                return consumablesNum;
            }

            public void setConsumablesNum(Integer consumablesNum) {
                this.consumablesNum = consumablesNum;
            }

            @Override
            public String toString() {
                return "ConsumablesMaterials{" +
                        "consumablesCode='" + consumablesCode + '\'' +
                        ", consumablesName='" + consumablesName + '\'' +
                        ", consumablesNum=" + consumablesNum +
                        '}';
            }
        }

        public List<PackingMaterials> getPackingMaterials() {
            return packingMaterials;
        }

        public void setPackingMaterials(List<PackingMaterials> packingMaterials) {
            this.packingMaterials = packingMaterials;
        }

        public List<ConsumablesMaterials> getConsumablesMaterials() {
            return consumablesMaterials;
        }

        public void setConsumablesMaterials(List<ConsumablesMaterials> consumablesMaterials) {
            this.consumablesMaterials = consumablesMaterials;
        }

        public Long getPackageWeight() {
            return packageWeight;
        }

        public void setPackageWeight(Long packageWeight) {
            this.packageWeight = packageWeight;
        }

        @Override
        public String toString() {
            return "Package{" +
                    "packingMaterials=" + packingMaterials +
                    ", consumablesMaterials=" + consumablesMaterials +
                    ", packageWeight=" + packageWeight +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CBOrderOperateRequest{" +
                "orderId='" + orderId + '\'' +
                ", vendor='" + vendor + '\'' +
                ", status='" + status + '\'' +
                ", occurrenceTime='" + occurrenceTime + '\'' +
                ", transportation=" + transportation +
                ", error_info=" + error_info +
                '}';
    }
}
