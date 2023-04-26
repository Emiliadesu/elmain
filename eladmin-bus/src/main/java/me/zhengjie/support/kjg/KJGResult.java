package me.zhengjie.support.kjg;

import cn.hutool.json.JSONObject;
import me.zhengjie.utils.JSONUtils;

public class KJGResult {

    /**
     * 返回接收订单成功
     * @param storer
     * @param externalNo
     * @return
     */
    public static String getOrderResultSucc(String storer, String externalNo) {
        JSONObject result = new JSONObject();
        JSONObject kjsoOrderResponse = new JSONObject();
        kjsoOrderResponse.putOnce("storer", storer);
        kjsoOrderResponse.putOnce("wmwhseid", "3302461510");
        kjsoOrderResponse.putOnce("externalNo", externalNo);
        kjsoOrderResponse.putOnce("success", true);
        kjsoOrderResponse.putOnce("reasons", "成功");
        result.putOnce("kjsoOrderResponse", kjsoOrderResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    /**
     * 返回接收订单失败
     * @param storer
     * @param externalNo
     * @param msg
     * @return
     */
    public static String getOrderResultErr(String storer, String externalNo, String msg) {
        JSONObject result = new JSONObject();
        JSONObject kjsoOrderResponse = new JSONObject();
        kjsoOrderResponse.putOnce("storer", storer);
        kjsoOrderResponse.putOnce("wmwhseid", "3302461510");
        kjsoOrderResponse.putOnce("externalNo", externalNo);
        kjsoOrderResponse.putOnce("success", false);
        kjsoOrderResponse.putOnce("reasons", msg);
        result.putOnce("kjsoOrderResponse", kjsoOrderResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    /**
     * 状态回传
     * @param externalNo
     * @return
     */
    public static String getMftVerResultSucc(String externalNo) {
        JSONObject result = new JSONObject();
        JSONObject mftVerifyResponse = new JSONObject();
        mftVerifyResponse.putOnce("externalNo", externalNo);
        mftVerifyResponse.putOnce("success", true);
        mftVerifyResponse.putOnce("reasons", "成功");
        result.putOnce("mftVerifyResponse", mftVerifyResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    /**
     * 入库回传
     * @param storer
     * @param externalNo
     * @return
     */
    public static String getAsnResultSucc(String storer, String externalNo) {
        JSONObject result = new JSONObject();
        JSONObject kjAsnResponse = new JSONObject();
        kjAsnResponse.putOnce("storer", storer);
        kjAsnResponse.putOnce("wmwhseid", "3302461510");
        kjAsnResponse.putOnce("externalNo", externalNo);
        kjAsnResponse.putOnce("success", true);
        kjAsnResponse.putOnce("reasons", "成功");
        result.putOnce("kjAsnResponse", kjAsnResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    /**
     * 货品
     * @return
     */
    public static String getSkuResultSucc(String storer, String skuKey) {
        JSONObject result = new JSONObject();
        JSONObject kjSkuResponse = new JSONObject();
        kjSkuResponse.putOnce("storer", storer);
        kjSkuResponse.putOnce("skuKey", skuKey);
        kjSkuResponse.putOnce("success", true);
        kjSkuResponse.putOnce("reasons", "成功");
        result.putOnce("kjSkuResponse", kjSkuResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    /**
     * 取消成功
     * @return
     */
    public static String getOrderCancelResultSucc() {
        JSONObject result = new JSONObject();
        JSONObject wmsOrderCancelResponse = new JSONObject();
        wmsOrderCancelResponse.putOnce("success", true);
        wmsOrderCancelResponse.putOnce("reasons", "成功");
        result.putOnce("wmsOrderCancelResponse", wmsOrderCancelResponse);
        String xml = JSONUtils.toXml(result);
        return xml;
    }

    public static String getAsnCancelResultSucc(String storer, String externalNo) {
        JSONObject result = new JSONObject();
        JSONObject kjAsnCancelRequest = new JSONObject();
        kjAsnCancelRequest.putOnce("storer", storer);
        kjAsnCancelRequest.putOnce("wmwhseid", "3302461510");
        kjAsnCancelRequest.putOnce("externalNo", externalNo);
        kjAsnCancelRequest.putOnce("success", true);
        kjAsnCancelRequest.putOnce("reasons", "成功");
        result.putOnce("kjAsnCancelRequest", kjAsnCancelRequest);
        String xml = JSONUtils.toXml(result);
        return xml;
    }
}
