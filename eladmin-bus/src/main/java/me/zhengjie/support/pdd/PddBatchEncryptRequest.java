package me.zhengjie.support.pdd;

import java.util.List;

public class PddBatchEncryptRequest {
    private List<PddEncrypt> dataList;
    private String shopCode;

    public List<PddEncrypt> getDataList() {
        return dataList;
    }

    public void setDataList(List<PddEncrypt> dataList) {
        this.dataList = dataList;
    }

    public String getShopCode() {
        return shopCode;
    }

    public void setShopCode(String shopCode) {
        this.shopCode = shopCode;
    }

    public static class PddEncrypt{
        private String data;
        private Boolean search;
        private String type;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public Boolean getSearch() {
            return search;
        }

        public void setSearch(Boolean search) {
            this.search = search;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
