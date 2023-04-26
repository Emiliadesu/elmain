package me.zhengjie.rest;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.rest.model.meituan.MeiTuanCancel;
import me.zhengjie.rest.model.meituan.MeiTuanOrder;
import me.zhengjie.rest.model.meituan.MeiTuanRefund;
import me.zhengjie.service.MeituanService;
import me.zhengjie.support.meituan.MeiTuanCrossBorderDetail;
import me.zhengjie.support.meituan.MeiTuanSupport;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Api(tags = "美团接口")
@Slf4j
@RequestMapping("/api/meituan")
public class MeituanController {
    @Value("${meiTuan.url}")
    private String url;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private MeiTuanSupport meiTuanSupport;

    @Autowired
    private MeituanService meituanService;

    @AnonymousAccess
    @Log("美团订单下发")
    @ApiOperation("美团订单下发")
    @PostMapping("/order")
    public JSONObject returnOrder(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        //log.info("应用级参数：{}", dataMap);
        if (CollectionUtil.isEmpty(request.getParameterMap())){
            result.put("data", "ng");
            Map<String,Object>error=new HashMap<>();
            error.put("code",701);
            error.put("msg","缺少参数，数据不完整");
            result.put("error",error);
            return result;
        }
        TreeMap<String,String>map=new TreeMap<>();
        reqMapToMap(request.getParameterMap(),map);
        String timestamp=map.get("timestamp");
        String appId=map.get("app_id");
        String sig=map.get("sig");
        log.info("参数："+JSONObject.toJSONString(map));
        JSONObject object=JSONObject.parseObject(JSONObject.toJSONString(map));
        String data=object.toJSONString();
        map.remove("sig");
        log.info("收到美团订单下发，timestamp：{}，app_id：{}，sig：{}", timestamp, appId, sig);
        log.info("应用级参数：{}", data);
        String sign;
        try {
            sign= meiTuanSupport.getSignByIsv(map,"/api/meituan/order",url);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg","验签失败"+e.getMessage());
            result.put("data", "ng");
            return result;
        }
        System.err.println(sig.equals(sign));
        result.put("data", "ok");
        System.out.println(data);
        cbOrderProducer.send(
                MsgType.CB_ORDER_200_MEITUAN,
                data,
                object.getLong("order_id").toString()
        );
        /*MeiTuanOrder order=JSONObject.parseObject(data,MeiTuanOrder.class);
        try {
            meituanService.createOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return result;
    }

    private void reqMapToMap(Map<String, String[]> parameterMap, TreeMap<String, String> map) {
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            map.put(entry.getKey(),URLUtil.decode(entry.getValue()[0]));
            log.info("{}:{}",entry.getKey(),map.get(entry.getKey()));
        }
    }

    @AnonymousAccess
    @PostMapping("/cancel")
    @Log("美团订单取消通知")
    @ApiOperation("美团订单取消通知")
    public JSONObject cancelMsg(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        if (CollectionUtil.isEmpty(request.getParameterMap())){
            result.put("data", "ng");
            Map<String,Object>error=new HashMap<>();
            error.put("code",701);
            error.put("msg","缺少参数，数据不完整");
            result.put("error",error);
            return result;
        }
        TreeMap<String,String>map=new TreeMap<>();
        reqMapToMap(request.getParameterMap(),map);
        String timestamp=map.get("timestamp");
        String appId=map.get("app_id");
        String sig=map.get("sig");
        log.info("参数："+JSONObject.toJSONString(map));
        map.remove("sig");
        log.info("收到美团订单取消通知，timestamp：{}，app_id：{}，sig：{}", timestamp, appId, sig);
        String sign;
        try {
            sign= meiTuanSupport.getSignByIsv(map,"/api/meituan/cancel",url);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg","验签失败"+e.getMessage());
            result.put("data", "ng");
            return result;
        }
        System.err.println(sig.equals(sign));
        String data=JSONObject.toJSONString(map);
        MeiTuanCancel cancel= com.alibaba.fastjson.JSONObject.parseObject(data,MeiTuanCancel.class);
        try {
            cbOrderProducer.send(
                    MsgType.MEITUAN_CANCEL,
                    data,
                    cancel.getOrderId()+"");
            //meituanService.cancelOrder(cancel);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("data", "ok");
        System.out.println(JSONObject.toJSONString(map));
        return result;
    }

    @AnonymousAccess
    @PostMapping("/refund")
    @Log("美团订单退款通知")
    @ApiOperation("美团订单退款通知")
    /**
     *
     */
    public JSONObject refund(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        if (CollectionUtil.isEmpty(request.getParameterMap())){
            result.put("data", "ng");
            Map<String,Object>error=new HashMap<>();
            error.put("code",701);
            error.put("msg","缺少参数，数据不完整");
            result.put("error",error);
            return result;
        }
        TreeMap<String,String>map=new TreeMap<>();
        reqMapToMap(request.getParameterMap(),map);
        String timestamp=map.get("timestamp");
        String appId=map.get("app_id");
        String sig=map.get("sig");
        log.info("参数："+JSONObject.toJSONString(map));
        map.remove("sig");
        log.info("收到美团订单退款通知，timestamp：{}，app_id：{}，sig：{}", timestamp, appId, sig);
        String sign;
        try {
            sign= meiTuanSupport.getSignByIsv(map,"/api/meituan/refund",url);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg","验签失败"+e.getMessage());
            result.put("data", "ng");
            return result;
        }
        System.err.println(sig.equals(sign));
        String data = JSONObject.toJSONString(map);
        MeiTuanRefund refund= com.alibaba.fastjson.JSONObject.parseObject(data,MeiTuanRefund.class);
        try {
            cbOrderProducer.send(
                    MsgType.MEITUAN_LOCK,
                    data,
                    refund.getOrderId()+""
            );
            //meituanService.refundOrder(refund);
        }catch (Exception e){
            e.printStackTrace();
        }
        result.put("data", "ok");
        System.out.println(JSONObject.toJSONString(map));
        return result;
    }

    @AnonymousAccess
    @PostMapping("/pay-declare-push")
    @Log("美团清关信息推送通知")
    @ApiOperation("美团清关信息推送通知")
    public JSONObject payDeclarePsh(HttpServletRequest request) {
        JSONObject result = new JSONObject();
        if (CollectionUtil.isEmpty(request.getParameterMap())){
            result.put("data", "ng");
            Map<String,Object>error=new HashMap<>();
            error.put("code",701);
            error.put("msg","缺少参数，数据不完整");
            result.put("error",error);
            return result;
        }
        TreeMap<String,String>map=new TreeMap<>();
        reqMapToMap(request.getParameterMap(),map);
        String timestamp=map.get("timestamp");
        String appId=map.get("app_id");
        String sig=map.get("sig");
        log.info("参数："+JSONObject.toJSONString(map));
        map.remove("sig");
        log.info("收到美团清关信息推送通知，timestamp：{}，app_id：{}，sig：{}", timestamp, appId, sig);
        String sign;
        try {
            sign= meiTuanSupport.getSignByIsv(map,"/api/meituan/pay-declare-push",url);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg","验签失败"+e.getMessage());
            result.put("data", "ng");
            return result;
        }
        System.err.println(sig.equals(sign));
        MeiTuanCrossBorderDetail detail= com.alibaba.fastjson.JSONObject.parseObject(JSONObject.toJSONString(map),MeiTuanCrossBorderDetail.class);
        try {
            /*TimeUnit.SECONDS.sleep(1);
            meituanService.setDeclareInfo(detail);*/
            cbOrderProducer.delaySend(MsgType.MEITUAN_SET_CROSSBORD_INFO,
                    detail.toString(),
                    detail.getWmOrderIdView(),
                    3000);
        }catch (Exception e){
            e.printStackTrace();
        }

        result.put("data", "ok");
        System.out.println(JSONObject.toJSONString(map));
        return result;
    }
}
