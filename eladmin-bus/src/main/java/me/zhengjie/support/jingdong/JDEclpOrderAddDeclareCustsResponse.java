package me.zhengjie.support.jingdong;

import com.alibaba.fastjson.annotation.JSONField;

public class JDEclpOrderAddDeclareCustsResponse {

    @JSONField(name = "jingdong_eclp_order_addDeclareOrderCustoms_responce")
    private JingdongEclpOrderAddDeclareOrderCustomsResponce jingdongEclpOrderAddDeclareOrderCustomsResponce;

    public static class JingdongEclpOrderAddDeclareOrderCustomsResponce{

        @JSONField(name = "code")
        private String code;

        @JSONField(name = "declaredOrderCustoms_result")
        private DeclaredOrderCustomsResult declaredOrderCustomsResult;

        public static class DeclaredOrderCustomsResult{

            @JSONField(name = "resultCode")
            private String resultCode;

            @JSONField(name = "message")
            private String message;

            public String getResultCode() {
                return resultCode;
            }

            public void setResultCode(String resultCode) {
                this.resultCode = resultCode;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public DeclaredOrderCustomsResult getDeclaredOrderCustomsResult() {
            return declaredOrderCustomsResult;
        }

        public void setDeclaredOrderCustomsResult(DeclaredOrderCustomsResult declaredOrderCustomsResult) {
            this.declaredOrderCustomsResult = declaredOrderCustomsResult;
        }
    }

    public JingdongEclpOrderAddDeclareOrderCustomsResponce getJingdongEclpOrderAddDeclareOrderCustomsResponce() {
        return jingdongEclpOrderAddDeclareOrderCustomsResponce;
    }

    public void setJingdongEclpOrderAddDeclareOrderCustomsResponce(JingdongEclpOrderAddDeclareOrderCustomsResponce jingdongEclpOrderAddDeclareOrderCustomsResponce) {
        this.jingdongEclpOrderAddDeclareOrderCustomsResponce = jingdongEclpOrderAddDeclareOrderCustomsResponce;
    }
}
