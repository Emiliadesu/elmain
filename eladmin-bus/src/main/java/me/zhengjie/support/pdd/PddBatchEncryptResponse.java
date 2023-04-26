package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PddBatchEncryptResponse {
    @JSONField(name = "data_encrypt_list")
    private List<PddEncrypt> dataEncryptList;

    public List<PddEncrypt> getDataEncryptList() {
        return dataEncryptList;
    }

    public void setDataEncryptList(List<PddEncrypt> dataEncryptList) {
        this.dataEncryptList = dataEncryptList;
    }

    public static class PddEncrypt extends PddBatchEncryptRequest.PddEncrypt {
        @JSONField(name = "encrypt_data")
        private String encryptData;
        private Boolean success;

        public String getEncryptData() {
            return encryptData;
        }

        public void setEncryptData(String encryptData) {
            this.encryptData = encryptData;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }
    }
}
