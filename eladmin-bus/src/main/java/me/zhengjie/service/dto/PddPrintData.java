package me.zhengjie.service.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.domain.PddCloudPrintData;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PddPrintData {
    public PddPrintData() {
    }

    /**
     *
     * @param dataList 要打印的面单数据
     * @param pddUserId 云打单的拼多多用户id
     * @param templateUrl1 主面单模板url
     * @param templateUrl2 自定义数据的模板url
     */
    public PddPrintData(List<PddCloudPrintData> dataList, String pddUserId,String templateUrl1,String templateUrl2) {
        this.erpId = 303379400;
        this.cmd = "print";
        this.requestID = String.format("%08x", System.currentTimeMillis());
        this.task = new PddPrintDataTask(dataList, pddUserId,templateUrl1,templateUrl2);
        this.version = "1.0";
    }

    @JSONField(name = "ERPId")
    private Integer erpId;
    private String cmd;
    @JSONField(name = "requestID")
    private String requestID;
    private String version;
    private PddPrintDataTask task;

    public static class PddPrintDataTask {
        {
            this.documents = new ArrayList<>();
            this.notifyType = new ArrayList<>();
            this.notifyType.add("print");
            this.preview = false;
            this.previewType = "image";
            this.printer = "";
            this.taskID = String.valueOf(System.currentTimeMillis() / 1000);
        }

        public PddPrintDataTask() {
        }

        public PddPrintDataTask(List<PddCloudPrintData> dataList, String userId,String templateUrl1,String templateUrl2) {
            if (dataList == null)
                throw new BadRequestException("打印数据为空");
            for (PddCloudPrintData pddCloudPrintData : dataList) {
                PddPrintDataDocument document = new PddPrintDataDocument(pddCloudPrintData, userId,templateUrl1,templateUrl2);
                this.documents.add(document);
            }
        }

        private List<PddPrintDataDocument> documents;
        @JSONField(name = "notifyType")
        private List<String> notifyType;
        private Boolean preview;
        @JSONField(name = "previewType")
        private String previewType;
        /**
         * 指定的打印机名字
         */
        private String printer;
        /**
         * 任务id
         */
        @JSONField(name = "taskID")
        private String taskID;

        public List<PddPrintDataDocument> getDocuments() {
            return documents;
        }

        public void setDocuments(List<PddPrintDataDocument> documents) {
            this.documents = documents;
        }

        public List<String> getNotifyType() {
            return notifyType;
        }

        public void setNotifyType(List<String> notifyType) {
            this.notifyType = notifyType;
        }

        public Boolean getPreview() {
            return preview;
        }

        public void setPreview(Boolean preview) {
            this.preview = preview;
        }

        public String getPreviewType() {
            return previewType;
        }

        public void setPreviewType(String previewType) {
            this.previewType = previewType;
        }

        public String getPrinter() {
            return printer;
        }

        public void setPrinter(String printer) {
            this.printer = printer;
        }

        public String getTaskID() {
            return taskID;
        }

        public void setTaskID(String taskID) {
            this.taskID = taskID;
        }
    }

    public static class PddPrintDataDocument {
        {
            this.contents = new ArrayList<>();
        }

        public PddPrintDataDocument() {
        }

        public PddPrintDataDocument(PddCloudPrintData data, String userId,String templateUrl1,String templateUrl2) {
            PddPrintDataContent content = new PddPrintDataContent(data, userId);
            if (StringUtil.isNotBlank(templateUrl1))
                content.setTemplateUrl(templateUrl1);
            this.contents.add(content);
            if (StringUtil.isNotBlank(templateUrl2)){
                PddPrintDataContent cusContent = new PddPrintDataContent();
                Map<String,Object>dataMap=new HashMap<>();
                dataMap.put("soNo", data.getSoNo());
                dataMap.put("waveNo",data.getWaveNo());
                dataMap.put("crossBorderOrderNo",data.getCrossBorderOrderNo());
                dataMap.put("basketNum",data.getBasketNum());
                dataMap.put("skuTotal",data.getSkuTotal());
                dataMap.put("total",data.getTotal());
                dataMap.put("city",data.getCity());
                dataMap.put("date", DateUtils.nowDate());
                dataMap.put("mailNo",data.getMailNo());
                dataMap.put("detail",data.getDetail());
                dataMap.put("shopName",data.getSender());
                cusContent.setData(dataMap);
                cusContent.setTemplateUrl2(templateUrl2);
                cusContent.setTemplateUrl(null);
                this.contents.add(cusContent);
            }
            this.documentID = data.getMailNo();
        }

        private List<PddPrintDataContent> contents;
        @JSONField(name = "documentID")
        private String documentID;

        public List<PddPrintDataContent> getContents() {
            return contents;
        }

        public void setContents(List<PddPrintDataContent> contents) {
            this.contents = contents;
        }

        public String getDocumentID() {
            return documentID;
        }

        public void setDocumentID(String documentID) {
            this.documentID = documentID;
        }
    }

    public static class PddPrintDataContent {
        {
            /**
             * 代码块，每个构造函数都会执行的代码
             */

        }

        public PddPrintDataContent() {
        }

        public PddPrintDataContent(PddCloudPrintData data, String userId) {
            this.addData = new HashMap<>();
            Map<String, Object> sender = new HashMap<>();
            Map<String, Object> address = new HashMap<>();
            address.put("city", "宁波市");
            address.put("detail", "浙江省宁波市北仑区保税东区兴业四路二号");
            address.put("district", "北仑区");
            address.put("province", "浙江省");
            sender.put("address", address);
            sender.put("mobile", StringUtil.isBlank(data.getSenderPhone())?"18888888888":data.getSenderPhone());
            sender.put("name", StringUtil.isBlank(data.getSender())?"富立物流":data.getSender());
            this.addData.put("sender", sender);
            JSONObject printData = JSONObject.parseObject(data.getPrintData());
            this.encryptedData = printData.getString("encryptedData");
            this.signature = printData.getString("signature");
            this.ver = printData.getString("ver");
            this.userid = userId;
        }

        /**
         * 额外数据
         */
        @JSONField(name = "addData")
        private Map<String, Object> addData;

        @JSONField(name = "data")
        private Map<String, Object> data;
        /**
         * 电子面单打印数据，密文
         */
        @JSONField(name = "encryptedData")
        private String encryptedData;
        /**
         * 电子面单密文的电子签名
         */
        @JSONField(name = "signature")
        private String signature;
        /**
         * 电子面单模板url
         */
        @JSONField(name = "templateUrl")
        private String templateUrl = "https://file-link.pinduoduo.com/ztointer_std";

        @JSONField(name = "templateURL")
        private String templateUrl2;
        /**
         * 拼多多用户id
         */
        private String userid;
        /**
         * 电子面单版本
         */
        private String ver;

        public Map<String, Object> getAddData() {
            return addData;
        }

        public void setAddData(Map<String, Object> addData) {
            this.addData = addData;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public void setTemplateUrl2(String templateUrl2) {
            this.templateUrl2 = templateUrl2;
        }

        public void setTemplateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
        }

        public String getEncryptedData() {
            return encryptedData;
        }

        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }

        public String getTemplateUrl2() {
            return templateUrl2;
        }
    }

    public Integer getErpId() {
        return erpId;
    }

    public void setErpId(Integer erpId) {
        this.erpId = erpId;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public PddPrintDataTask getTask() {
        return task;
    }

    public void setTask(PddPrintDataTask task) {
        this.task = task;
    }
}
