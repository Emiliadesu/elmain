package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;

public class JingdongLdopWaybillReceiveResponce {

    @JSONField(name = "jingdong_ldop_waybill_receive_responce")
    private JDLdopWaybillReceiveResponce jingdongLdopWaybillReceiveResponce;

    public static class JDLdopWaybillReceiveResponce{

        @JSONField(name = "code")
        private String code;
        @JSONField(name = "receiveorderinfo_result")
        private ReceiveorderinfoResult receiveorderinfoResult;

        public static class ReceiveorderinfoResult{

            @JSONField(name = "transType")
            private Integer transType;

            @JSONField(name = "deliveryId")
            private String deliveryId;

            @JSONField(name = "preSortResult")
            private PreSortResult preSortResult;

            @JSONField(name = "needRetry")
            private Boolean needRetry;

            @JSONField(name = "promiseTimeType")
            private Integer promiseTimeType;

            @JSONField(name = "resultCode")
            private Integer resultCode;

            @JSONField(name = "resultMessage")
            private String resultMessage;

            public static class PreSortResult{

                @JSONField(name = "slideNo")
                private String slideNo;

                @JSONField(name = "isHideName")
                private Integer isHideName;

                @JSONField(name = "siteType")
                private Integer siteType;

                @JSONField(name = "agingName")
                private String agingName;

                @JSONField(name = "targetSortCenterId")
                private Long targetSortCenterId;

                @JSONField(name = "sourceSortCenterName")
                private String sourceSortCenterName;

                @JSONField(name = "distributeCode")
                private String distributeCode;

                @JSONField(name = "qrcodeUrl")
                private String qrcodeUrl;

                @JSONField(name = "sourceTabletrolleyCode")
                private String sourceTabletrolleyCodel;

                @JSONField(name = "coverCode")
                private String coverCode;

                @JSONField(name = "road")
                private String road;

                @JSONField(name = "collectionAddress")
                private String collectionAddress;

                @JSONField(name = "siteId")
                private Integer siteId;

                @JSONField(name = "isHideContractNumbers")
                private Integer isHideContractNumbers;

                @JSONField(name = "sourceSortCenterId")
                private Long sourceSortCenterId;

                @JSONField(name = "sourceCrossCode")
                private String sourceCrossCode;

                @JSONField(name = "targetSortCenterName")
                private String targetSortCenterName;

                @JSONField(name = "targetTabletrolleyCode")
                private String targetTabletrolleyCode;

                @JSONField(name = "siteName")
                private String siteName;

                @JSONField(name = "aging")
                private Integer aging;

                public String getSlideNo() {
                    return slideNo;
                }

                public void setSlideNo(String slideNo) {
                    this.slideNo = slideNo;
                }

                public Integer getIsHideName() {
                    return isHideName;
                }

                public void setIsHideName(Integer isHideName) {
                    this.isHideName = isHideName;
                }

                public Integer getSiteType() {
                    return siteType;
                }

                public void setSiteType(Integer siteType) {
                    this.siteType = siteType;
                }

                public String getAgingName() {
                    return agingName;
                }

                public void setAgingName(String agingName) {
                    this.agingName = agingName;
                }

                public Long getTargetSortCenterId() {
                    return targetSortCenterId;
                }

                public void setTargetSortCenterId(Long targetSortCenterId) {
                    this.targetSortCenterId = targetSortCenterId;
                }

                public String getSourceSortCenterName() {
                    return sourceSortCenterName;
                }

                public void setSourceSortCenterName(String sourceSortCenterName) {
                    this.sourceSortCenterName = sourceSortCenterName;
                }

                public String getDistributeCode() {
                    return distributeCode;
                }

                public void setDistributeCode(String distributeCode) {
                    this.distributeCode = distributeCode;
                }

                public String getQrcodeUrl() {
                    return qrcodeUrl;
                }

                public void setQrcodeUrl(String qrcodeUrl) {
                    this.qrcodeUrl = qrcodeUrl;
                }

                public String getSourceTabletrolleyCodel() {
                    return sourceTabletrolleyCodel;
                }

                public void setSourceTabletrolleyCodel(String sourceTabletrolleyCodel) {
                    this.sourceTabletrolleyCodel = sourceTabletrolleyCodel;
                }

                public String getCoverCode() {
                    return coverCode;
                }

                public void setCoverCode(String coverCode) {
                    this.coverCode = coverCode;
                }

                public String getRoad() {
                    return road;
                }

                public void setRoad(String road) {
                    this.road = road;
                }

                public String getCollectionAddress() {
                    return collectionAddress;
                }

                public void setCollectionAddress(String collectionAddress) {
                    this.collectionAddress = collectionAddress;
                }

                public Integer getSiteId() {
                    return siteId;
                }

                public void setSiteId(Integer siteId) {
                    this.siteId = siteId;
                }

                public Integer getIsHideContractNumbers() {
                    return isHideContractNumbers;
                }

                public void setIsHideContractNumbers(Integer isHideContractNumbers) {
                    this.isHideContractNumbers = isHideContractNumbers;
                }

                public Long getSourceSortCenterId() {
                    return sourceSortCenterId;
                }

                public void setSourceSortCenterId(Long sourceSortCenterId) {
                    this.sourceSortCenterId = sourceSortCenterId;
                }

                public String getSourceCrossCode() {
                    return sourceCrossCode;
                }

                public void setSourceCrossCode(String sourceCrossCode) {
                    this.sourceCrossCode = sourceCrossCode;
                }

                public String getTargetSortCenterName() {
                    return targetSortCenterName;
                }

                public void setTargetSortCenterName(String targetSortCenterName) {
                    this.targetSortCenterName = targetSortCenterName;
                }

                public String getTargetTabletrolleyCode() {
                    return targetTabletrolleyCode;
                }

                public void setTargetTabletrolleyCode(String targetTabletrolleyCode) {
                    this.targetTabletrolleyCode = targetTabletrolleyCode;
                }

                public String getSiteName() {
                    return siteName;
                }

                public void setSiteName(String siteName) {
                    this.siteName = siteName;
                }

                public Integer getAging() {
                    return aging;
                }

                public void setAging(Integer aging) {
                    this.aging = aging;
                }
            }

            public Integer getTransType() {
                return transType;
            }

            public void setTransType(Integer transType) {
                this.transType = transType;
            }

            public String getDeliveryId() {
                return deliveryId;
            }

            public void setDeliveryId(String deliveryId) {
                this.deliveryId = deliveryId;
            }

            public PreSortResult getPreSortResult() {
                return preSortResult;
            }

            public void setPreSortResult(PreSortResult preSortResult) {
                this.preSortResult = preSortResult;
            }

            public Boolean getNeedRetry() {
                return needRetry;
            }

            public void setNeedRetry(Boolean needRetry) {
                this.needRetry = needRetry;
            }

            public Integer getPromiseTimeType() {
                return promiseTimeType;
            }

            public void setPromiseTimeType(Integer promiseTimeType) {
                this.promiseTimeType = promiseTimeType;
            }

            public Integer getResultCode() {
                return resultCode;
            }

            public void setResultCode(Integer resultCode) {
                this.resultCode = resultCode;
            }

            public String getResultMessage() {
                return resultMessage;
            }

            public void setResultMessage(String resultMessage) {
                this.resultMessage = resultMessage;
            }
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public ReceiveorderinfoResult getReceiveorderinfoResult() {
            return receiveorderinfoResult;
        }

        public void setReceiveorderinfoResult(ReceiveorderinfoResult receiveorderinfoResult) {
            this.receiveorderinfoResult = receiveorderinfoResult;
        }
    }

    public JDLdopWaybillReceiveResponce getJingdongLdopWaybillReceiveResponce() {
        return jingdongLdopWaybillReceiveResponce;
    }

    public void setJingdongLdopWaybillReceiveResponce(JDLdopWaybillReceiveResponce jingdongLdopWaybillReceiveResponce) {
        this.jingdongLdopWaybillReceiveResponce = jingdongLdopWaybillReceiveResponce;
    }
}
