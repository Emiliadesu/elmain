package me.zhengjie.support.ymatou;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.Md5Utils;

import java.util.Date;

@Slf4j
public class YmatouSupport {
    private String appId;
    private String appSecret;
    private String authCode;
    //API的参数
    private CommonApiParam apiParam;
    //API的公共参数
    private BaseParam commonParam;

    public static Integer translationToDYStatus(int statusCode) throws Exception{
        switch (statusCode){
            case 17://待发货
                return 2;
            case 3://已发货
                return 3;
            case 4://订单完成
                return 5;
            case 100://退款待审核
            case 101://退款审核通过
                return 16;
            case 12://买家取消订单
            case 13://卖家取消订单
            case 18://系统自动取消
                return 21;
            default:
                throw new Exception("未知异常:"+statusCode);
        }
    }

    public static String getStatusText(Integer statusCode) {
        String statusText;
        switch (statusCode) {
            case 17:
                statusText = "待发货";
                break;
            case 3:
                statusText = "已发货";
                break;
            case 4:
                statusText = "确认收货";
                break;
            case 12:
            case 13:
            case 18:
                statusText = "已取消";
                break;
            case 100:
                statusText = "退款待审核";
                break;
            case 101:
                statusText = "退款审核通过";
                break;
            default:
                statusText = String.valueOf(statusCode);
        }
        return statusText;
    }

    public <T> YmatouCommonResponse request(Class<T> clazz) {
        commonParam = new BaseParam();
        commonParam.setAuthCode(authCode);
        commonParam.setBizContent(JSON.toJSONString(apiParam));
        commonParam.setNonceStr(IdUtil.simpleUUID());
        commonParam.setTimestamp(DateUtils.formatDateTime(new Date()));
        log.info("洋码头请求:"+JSON.toJSONString(this.apiParam));
        //签名
        String sign = getSign();
        commonParam.setSign(sign);
        JSON body = JSON.parseObject(JSON.toJSONString(commonParam));
        String url = "https://open.ymatou.com/apigateway/v1";
        String resp = HttpUtil.post(url + "?app_id=" + appId + "&method=" + apiParam.getMethod(), body.toJSONString());
        log.info("洋码头响应:"+resp);
        JSONObject respJson = JSONObject.parseObject(resp);
        YmatouCommonResponse<T> response = JSON.parseObject(resp, YmatouCommonResponse.class);
        response.setContent(respJson.getObject("content", clazz));
        System.out.println(resp);
        return response;
    }

    public String getSign() {
        String str = "app_id=" + appId + "&auth_code=" + commonParam.getAuthCode() +
                "&biz_content=" + commonParam.getBizContent() + "&method=" + apiParam.getMethod() +
                "&nonce_str=" + commonParam.getNonceStr() + "&sign_method=" + commonParam.getSignMethod() +
                "&timestamp=" + commonParam.getTimestamp() + "&app_secret=" + appSecret;
        return Md5Utils.md5Hex(str).toUpperCase();
    }

    public static void main(String[] args) {
        YmatouSupport client = new YmatouSupport();
        client.setAppId("YhxgL0PljzkG8y5lRz");
        client.setAppSecret("IqRuLNYlhrhd0f5IfESJmqd99FVktxET");
        client.setAuthCode("bsCVtjyhC71OPYZQPVSQ4U3o1Fc04T6c");
        YmatouPaymentPushAPIRequest request = new YmatouPaymentPushAPIRequest();
        request.setOrderId(14605864352L);
        request.setCustoms("NINGBO");
        client.setApiParam(request);
        YmatouCommonResponse<YmatouPaymentPushResponse> response;
        try {
            response=client.request(YmatouPaymentPushResponse.class);
        }catch (Exception e){
            log.error("支付单推送失败");
            log.error(e.getMessage(),e);
            e.printStackTrace();
            return;
        }
        System.out.println(response);
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public CommonApiParam getApiParam() {
        return apiParam;
    }

    public void setApiParam(CommonApiParam apiParam) {
        this.apiParam = apiParam;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
