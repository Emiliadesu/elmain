package me.zhengjie.support.queenshop;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.client.core.YouZanClient;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradeGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetResult;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.support.douyin.DYCommonResponse;
import me.zhengjie.support.pdd.PddCommonResponse;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 圈尚工具类
 */
@Service
public class QSSupport {

    @Value("${qs.url}")
    private String url;

    @Value("${qs.partner}")
    private String partner;

    @Value("${qs.secret}")
    private String secret;

    //API的参数
    private CommonApiParam apiParam;
    private QSOrderListMain request;

    public static Integer translationToDYStatus(String qsStatusCode) throws Exception{
        Integer statusCode;
        switch (qsStatusCode){
            case "WAIT_SELLER_DELIVERY": //待发货
                statusCode=2;
                break;
            case "WAIT_BUYER_CONFIRM": //已发货
                statusCode=3;
                break;
            case "TRADE_FINISH":  //已签收
                statusCode=5;
                break;
            case "WAIT_REFUND":  //退款中
                //throw new BadRequestException("等待商家处理售后");//拼多多部分商家要求同意退款后才取消订单(目前不清楚有哪些商家，先注释掉看看情况)
                statusCode=16;
                break;
            case "NO_REFUND":  //无退款 或 退款关闭
                statusCode=25;
                break;
            case "REFUND_FINISH":   //退款成功
                statusCode=21;
                break;
            default:
                throw new Exception("未知异常:"+qsStatusCode);
        }
        return statusCode;
    }


    public <T> QSCommonResponse request(Class<T> clazz) throws Exception {
        String timestamp = String.valueOf((System.currentTimeMillis() / 1000));
            BeanMap beanMap = BeanMap.create(apiParam);
            TreeMap<String, Object> paramMap = new TreeMap<>();
            paramMap.put("partner", URLEncoder.encode(partner, "utf-8"));
            paramMap.put("method", URLEncoder.encode(apiParam.getMethod(), "utf-8"));
            paramMap.put("signType", URLEncoder.encode("1", "utf-8"));
            paramMap.put("timestamp", URLEncoder.encode(timestamp, "utf-8"));
            beanMap.forEach((key, value) -> {
            if (!StringUtils.equals("custId", key.toString())
                && !StringUtils.equals("shopId", key.toString())) {
                if (value instanceof ArrayList && apiParam.getMethod().equals("qs.pop.goods.stock.update")) {
                    for (int i = 0; i < ((ArrayList<QSStockDetails>) value).size(); i++) {
                        paramMap.put("stockDetails["+i+"].supSkuNo", ((ArrayList<QSStockDetails>) value).get(i).getSupSkuNo());
                        paramMap.put("stockDetails["+i+"].stock", ((ArrayList<QSStockDetails>) value).get(i).getStock());
                        paramMap.put("stockDetails["+i+"].type", ((ArrayList<QSStockDetails>) value).get(i).getType());
                    }
                }else
                paramMap.put((String) key, value);
            }
        });
        String sign = sign(paramMap);
        paramMap.put("sign", URLEncoder.encode(sign, "utf-8"));

        String resp = HttpUtil.post(url + "/tapp/api/business", paramMap);
        JSONObject respJson = JSONObject.parseObject(resp);
        QSCommonResponse<T> response = JSON.parseObject(resp, QSCommonResponse.class);
        if (!clazz.equals(EmptyResponse.class)) {
            if (response.getResult() != null) {
                if (JSONUtils.isObj(respJson, "result")) {
                    response.setResult(respJson.getObject("result", clazz));
                }
            }
        }
        return response;
    }


    private String sign(Map<String, Object> map) {
        StringBuilder signBuilder = (new StringBuilder()).append(secret);
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)iterator.next();
            if (entry.getValue() != null) {
                signBuilder.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()))
                        .append("&");
            }

        }
        String signStr = signBuilder.substring(0, (signBuilder.length() - 1));
        signStr = signStr + secret;
        return  DigestUtils.md5Hex(signStr).toUpperCase();
    }

    public CommonApiParam getApiParam() {
        return apiParam;
    }

    public void  setApiParam(CommonApiParam apiParam) {
        this.apiParam = apiParam;
    }

    public void setRequest(QSOrderListMain request) {
        this.request = request;
    }
}
