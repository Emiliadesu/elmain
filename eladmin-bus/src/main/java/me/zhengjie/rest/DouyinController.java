package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBReturnProducer;
import me.zhengjie.rest.model.douyin.*;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.DouyinService;
import me.zhengjie.support.douyin.DYSupport;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

@RestController
@RequiredArgsConstructor
@Api(tags = "抖音接口")
@RequestMapping("/api/douyin")
@Slf4j
public class DouyinController {

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private CBReturnProducer cbReturnProducer;

    @Autowired
    private DYSupport dySupport;

    @Autowired
    private BusinessLogService businessLogService;

    @AnonymousAccess
    @RequestMapping("/return-order")
    @Log("抖音退货订单下发")
    @ApiOperation("抖音退货订单下发")
    public JSONObject returnOrder(@RequestBody(required = false) String data) {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        try {
            log.info("收到抖音退货下发，下发参数：{}", data);
            com.alibaba.fastjson.JSONObject obj = JSON.parseObject(data);
            String order = obj.getString("order");
            // 发送收货回传通知
            cbReturnProducer.send(
                    MsgType.CB_RETURN_300,
                    order,
                    ""
            );
            result.putOnce("err_no", 0);
            result.putOnce("message", "success");
            businessLogService.saveLog(BusTypeEnum.DOUYIN_OUT, "DOUYIN_OUT", "", "",  data, result.toString(), (System.currentTimeMillis() - start));
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.putOnce("err_no", 23);
            result.putOnce("message", e.getMessage());
            return result;
        }
    }

    @AnonymousAccess
    @RequestMapping("/callback")
    @Log("抖音授权")
    @ApiOperation("抖音授权")
    public String callback(String code) throws Exception {
        if (StringUtil.isBlank(code))
            return "code is null, please try again!";
        ShopToken shopToken = new ShopToken();
        shopToken.setCode(code);
        douyinService.getToken(code);
        return "token get success";
    }

    @AnonymousAccess
    @PostMapping("/crossborder/msg/push")
    @Log("抖音跨境消息统一推送接口")
    @ApiOperation("抖音跨境消息统一推送接口")
    public JSONObject crossborderMsgPush(@RequestBody String body, String sign,@RequestParam(name = "app_key") String appKey, String timestamp) {
        long start = System.currentTimeMillis();
        JSONObject returnJs=new JSONObject();
        boolean verifi;
        try {
            verifi=vertifi(body, sign, appKey, timestamp);
        }catch (Exception e){
            returnJs.putOnce("code",100003);
            returnJs.putOnce("message",e.getMessage());
            return returnJs;
        }
        if (false){
            returnJs.putOnce("code",100001);
            returnJs.putOnce("message","验签失败");
        }else {
            CrossborderMsgPush crossborderMsgPush;
            try {
                body = StringUtil.filterEmoji(body);
                crossborderMsgPush=JSON.parseObject(body,CrossborderMsgPush.class);
            }catch (Exception e){
                returnJs.putOnce("code",100003);
                returnJs.putOnce("message","业务参数param_json解析json失败");
                return returnJs;
            }
            try {
                douyinService.crossborderMsgPush(crossborderMsgPush);
                returnJs.putOnce("code", 0);
                returnJs.putOnce("message", "success");
            } catch (BadRequestException e) {
                returnJs.putOnce("code", 100002);
                returnJs.putOnce("message", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                returnJs.putOnce("code", 100003);
                returnJs.putOnce("message", e.getMessage());
            }
            businessLogService.saveLog(BusTypeEnum.DOUYIN_MSG, crossborderMsgPush.getMsgType(), "", crossborderMsgPush.getLogId(),  body, returnJs.toString(), (System.currentTimeMillis() - start));
        }
        return returnJs;
    }

//    @Log("抖音获取推送运单")
//    @ApiOperation("抖音获取运单")
//    @RequestMapping("/order-getmail")
//    public Result orderTransport(@RequestBody(required = false)TakeLogisticsInfoPush request,
//                                 @RequestParam("customersCode") String customersCode){
//
//        try {
//            CustomerKeyDto custCode = customerKeyService.findByCustCode(customersCode);
//            if (custCode == null)
//                throw new BadRequestException("customersCode不存在或客户未配置key");
//            String signKey = custCode.getSignKey();
//            String originData = SecureUtils.decryptDesHex(request.toString(), signKey);
//            douyinService.getMailNo(originData,custCode.getCustomerId());
//            return ResultUtils.getSuccess();
//        } catch (Exception e) {
//            return ResultUtils.getFail(e.getMessage());
//        }
//    }


    @AnonymousAccess
    @RequestMapping("/pull-order-by-shop")
    @Log("抖音按店铺拉单")
    @ApiOperation("抖音按店铺拉单")
    public JSONObject pullOrderByShop(Long shopId, String start, String end) {
        JSONObject result = new JSONObject();
        try {
            douyinService.pullOrderByShop(shopId, start, end);
            result.putOnce("err_no", 0);
            result.putOnce("message", "success");
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            result.putOnce("err_no", 23);
            result.putOnce("message", e.getMessage());
            return result;
        }
    }

    private boolean vertifi(String body,String sign,String appKey,String timestamp){
        if (StringUtil.isBlank(body)){
            throw new BadRequestException("业务参数param_json不能为空");
        }
        if (!((body.indexOf("{")==0&&body.lastIndexOf("}")==body.length()-1))){
            throw new BadRequestException("业务参数param_json不是标准json字符串");
        }
        System.out.println(body);
        log.info(body);
        log.info("请求的sign==="+sign);
        log.info("请求的app_key==="+appKey);
        log.info("本机配置的app_key==="+dySupport.appKey);
        log.info("timestamp==="+timestamp);
        com.alibaba.fastjson.JSONObject js=JSON.parseObject(body);
        TreeMap<String,Object>map =new TreeMap<>();
        JSONUtils.sortJsonToMap(js,map);
        String sign1=dySupport.getSpiSign(map,timestamp);
        System.out.println("本机签名sign1==="+sign1);
        System.out.println("sign==sign1==="+sign.equals(sign1));
        return StringUtil.equals(sign,sign1);
    }

    @AnonymousAccess
    @PostMapping("/crossBorder/inventory/reconciliation")
    @Log("抖音库存对账接口")
    @ApiOperation("抖音库存对账接口")
    public JSONObject crossBorderInventoryReconciliation(@RequestBody String body, String sign,@RequestParam(name = "app_key") String appKey, String timestamp/*,@RequestParam(name = "param_json")String paramJson*/) {
        long start = System.currentTimeMillis();
        JSONObject returnJs=new JSONObject();
        boolean verifi;
        try {
            verifi=vertifi(body, sign, appKey, timestamp);
        }catch (Exception e){
            returnJs.putOnce("code",100003);
            returnJs.putOnce("message",e.getMessage());
            return returnJs;
        }
        if (!verifi){
            returnJs.putOnce("code",100001);
            returnJs.putOnce("message","验签失败");
        }else {
            Reconciliation reconciliation;
            try {
                reconciliation=JSON.parseObject(body,Reconciliation.class);
            }catch (Exception e){
                returnJs.putOnce("code",100003);
                returnJs.putOnce("message","业务参数param_json解析json失败");
                return returnJs;
            }
            try {
                ReconciliationRespData data=douyinService.inventoryReconciliation(reconciliation);
                returnJs.putOnce("data",new JSONObject(JSON.toJSONString(data)));
                returnJs.putOnce("code", 0);
                returnJs.putOnce("message", "success");
            } catch (BadRequestException e) {
                returnJs.putOnce("code", 100002);
                returnJs.putOnce("message", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                returnJs.putOnce("code", 100003);
                returnJs.putOnce("message", e.getMessage());
            }
        }
        businessLogService.saveLog(BusTypeEnum.DOUYIN_MSG, "inventory", "", "",  body, returnJs.toString(), (System.currentTimeMillis() - start));
        return returnJs;
    }

    @AnonymousAccess
    @PostMapping("/yunc/wms/inventory/query")
    @Log("抖音库存实时查询")
    @ApiOperation("抖音库存实时查询")
    public JSONObject yuncWmsInventoryQuery(@RequestBody String body, String sign,@RequestParam(name = "app_key") String appKey, String timestamp) {
        long start = System.currentTimeMillis();
        JSONObject returnJs=new JSONObject();
        boolean verifi;
        try {
            verifi=vertifi(body, sign, appKey, timestamp);
        }catch (Exception e){
            returnJs.putOnce("code",100003);
            returnJs.putOnce("message",e.getMessage());
            return returnJs;
        }
        if (!verifi){
            returnJs.putOnce("code",100001);
            returnJs.putOnce("message","验签失败");
        }else {
            YuncWmsInventoryQueryRequest queryRequest;
            try {
                queryRequest=JSON.parseObject(body,YuncWmsInventoryQueryRequest.class);
            }catch (Exception e){
                returnJs.putOnce("code",100003);
                returnJs.putOnce("message","业务参数param_json解析json失败");
                return returnJs;
            }
            try {
                YuncWmsInventoryQueryResponse data=douyinService.yuncWmsInventoryQuery(queryRequest);
                returnJs.putOnce("data",new JSONObject(JSON.toJSONString(data)));
                returnJs.putOnce("code", 0);
                returnJs.putOnce("message", "success");
            } catch (BadRequestException e) {
                returnJs.putOnce("code", 100002);
                returnJs.putOnce("message", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                returnJs.putOnce("code", 100003);
                returnJs.putOnce("message", e.getMessage());
            }
        }
        businessLogService.saveLog(BusTypeEnum.DOUYIN_MSG, "inventory", "", "",  body, returnJs.toString(), (System.currentTimeMillis() - start));
        return returnJs;
    }
}
