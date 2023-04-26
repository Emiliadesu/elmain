package me.zhengjie.rest;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.ReqLog;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.annotation.type.ReqLogType;
import me.zhengjie.domain.OrderMaterial;
import me.zhengjie.domain.OrderReturn;
import me.zhengjie.domain.SortingLineChuteCode;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.OutboundBatchDeliverOrder;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.OrderDeliverService;
import me.zhengjie.service.OrderMaterialService;
import me.zhengjie.support.dewu.DewuSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
import me.zhengjie.utils.enums.OrderDeliverStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequiredArgsConstructor
@Api(tags = "订单出库管理")
@RequestMapping("/api/orderDeliver")
public class OrderDeliverController {

    /**
     * 坡道异常口返回代码
     */
    public String RET_PD_ERROR = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wmsFxResponse><storer>3302461571</storer><wmwhseid>3302461510</wmwhseid><success>true</success><reasons/><resultCode>00</resultCode><default01>73698071-8</default01><default02>中通速递</default02><default03>02</default03>{0}{1}</wmsFxResponse>";

    /**
     * 坡道返回抽检
     */
    public static final String RET_PD_CHECK = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wmsFxResponse><storer>3302461571</storer><wmwhseid>3302461510</wmwhseid><success>true</success><reasons/><resultCode>00</resultCode><default01>73698071-8</default01><default02>中通速递</default02><default03>00</default03>{0}{1}</wmsFxResponse>";

    /**
     * 坡道返回中通
     */
    public static final String RET_PD_ZTO = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wmsFxResponse><storer>3302461571</storer><wmwhseid>3302461510</wmwhseid><success>true</success><reasons/><resultCode>02</resultCode><default01>73698071-8</default01><default02>中通速递</default02><default03>01</default03>{0}{1}</wmsFxResponse>";

    /**
     * 坡道返回韵达
     */
    public static final String RET_PD_YD = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wmsFxResponse><storer>3302461571</storer><wmwhseid>3302461510</wmwhseid><success>true</success><reasons/><resultCode>02</resultCode><default01>73698071-8</default01><default02>中通速递</default02><default03>03</default03>{0}{1}</wmsFxResponse>";


    /**
     * 坡道返回EMS
     */
    public static final String RET_PD_EMS = "<?xml version=\"1.0\" encoding=\"utf-8\"?><wmsFxResponse><storer>3302461571</storer><wmwhseid>3302461510</wmwhseid><success>true</success><reasons/><resultCode>02</resultCode><default01>73698071-8</default01><default02>EMS</default02><default03>02</default03>{0}{1}</wmsFxResponse>";

    @Autowired
    private OrderDeliverService orderDeliverService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private DewuSupport dewuSupport;

    @Autowired
    private OrderMaterialService orderMaterialService;


    @AnonymousAccess
    @RequestMapping("/f-line-dy-v2")
    @ApiOperation("流水线请求V2")
    public String fLineDYV2(String userid, String msg) {
        JSONObject msgJson = XML.toJSONObject(msg);
        JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
        String mailNo = requestJson.get("mailNo").toString();
        String weight = requestJson.get("weight").toString();// 千克
        SortingLineChuteCode chuteCode = orderDeliverService.deliverDYV2(weight, mailNo, userid);
        String result = orderDeliverService.chuteCodeToXml(chuteCode);
        return result;

//        JSONObject msgJson = XML.toJSONObject(msg);
//        JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
//        String mailNo = requestJson.get("mailNo").toString();
//        String weight = requestJson.get("weight").toString();// 千克
//        SortingLineChuteCode chuteCode = orderDeliverService.deliverDwV2(weight, mailNo, userid);
//        String result = orderDeliverService.chuteCodeToXml(chuteCode);
//        return result;
    }

    /**
     * 得物流水线出库
     * @param userid
     * @param msg
     * @return
     */
    @AnonymousAccess
    @RequestMapping("/f-line-dw-v2")
    @ApiOperation("流水线请求V2")
    public String fLineDwV2(String userid, String msg) {
        JSONObject msgJson = XML.toJSONObject(msg);
        JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
        String mailNo = requestJson.get("mailNo").toString();
        String weight = requestJson.get("weight").toString();// 千克
        SortingLineChuteCode chuteCode = orderDeliverService.deliverDwV2(weight, mailNo, userid);
        String result = orderDeliverService.chuteCodeToXml(chuteCode);
        return result;
    }

    @AnonymousAccess
    @RequestMapping("/f-line-v2")
    @ApiOperation("流水线请求V2")
    public String fLineV2(String userid, String msg) {
        JSONObject msgJson = XML.toJSONObject(msg);
        JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
        String mailNo = requestJson.get("mailNo").toString();
        String weight = requestJson.get("weight").toString();// 千克
        SortingLineChuteCode chuteCode = orderDeliverService.deliverV2(weight, mailNo, userid);
        String result = orderDeliverService.chuteCodeToXml(chuteCode);
        return result;
    }

    @AnonymousAccess
    @RequestMapping("/f-line-test")
    @ApiOperation("流水线请求V2")
    public String fLineTest(String userid, String msg) {
        JSONObject msgJson = XML.toJSONObject(msg);
        JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
        String mailNo = requestJson.get("mailNo").toString();
        String weight = requestJson.get("weight").toString();// 千克
        SortingLineChuteCode chuteCode = orderDeliverService.deliverTest(weight, mailNo, userid);
        String result = orderDeliverService.chuteCodeToXml(chuteCode);
        return result;
    }



    @RequestMapping("/hand-out-v2")
    @PreAuthorize("@el.check('orderDeliver:handOut')")
    public JSONObject handOutV2(@Validated String weight, @Validated String mailNo) {
        JSONObject result = new JSONObject();
        SortingLineChuteCode chuteCode = orderDeliverService.deliverV2(weight, mailNo, null);
        if (chuteCode == null) {
            result.putOnce("success", false);
            result.putOnce("msg", "流水线规则未配置，请联系技术配置");
            return result;
        }
        if (StringUtils.isNotEmpty(chuteCode.getDes())) {
            result.putOnce("msg", chuteCode.getDes());
        }else {
            result.putOnce("msg", chuteCode.getRuleName());
        }
        if (StringUtils.equals(chuteCode.getRuleCode(), "CHECK") || StringUtils.equals(chuteCode.getRuleCode(), "ERROR")) {
            result.putOnce("success", false);
        }else {
            result.putOnce("success", true);
        }
        return result;
    }

    /**
     * 得物页面出库
     * @param weight
     * @param mailNo
     * @return
     */
    @RequestMapping("/hand-out-dw-v2")
    @PreAuthorize("@el.check('orderDeliver:handOut')")
    public JSONObject handOutDwV2(@Validated String weight, @Validated String mailNo) {
        JSONObject result = new JSONObject();
        SortingLineChuteCode chuteCode = orderDeliverService.deliverDwV2(weight, mailNo, null);
        if (chuteCode == null) {
            result.putOnce("success", false);
            result.putOnce("msg", "流水线规则未配置，请联系技术配置");
            return result;
        }
        if (StringUtils.isNotEmpty(chuteCode.getDes())) {
            result.putOnce("msg", chuteCode.getDes());
        }else {
            result.putOnce("msg", chuteCode.getRuleName());
        }
        if (StringUtils.equals(chuteCode.getRuleCode(), "CHECK") || StringUtils.equals(chuteCode.getRuleCode(), "ERROR")) {
            result.putOnce("success", false);
        }else {
            result.putOnce("success", true);
        }
        return result;
    }

    /**
     *
     * @param body
     * @param appKey
     * @param timestamp
     * @param dwSign
     * @return
     */
    @Log("接收得物耗材回传")
    @ApiOperation("接收得物耗材回传")
    @AnonymousPostMapping("/pushMaterial")
    public JSONObject pushMaterial(@RequestBody(required = false) String body, @RequestHeader(name = "appkey")String appKey,
                                   @RequestHeader(name = "timestamp")String timestamp, @RequestHeader(name = "sign")String dwSign){
        JSONObject result = new JSONObject();
        TreeMap<String,Object>map=new TreeMap<>();
        map.put("appkey",appKey);
        map.put("timestamp",timestamp);
        System.out.println(body);
        JSONObject dwResObject = JSONUtil.parseObj(body);
        map.putAll(dwResObject.toBean(TreeMap.class));
        System.out.println(dwResObject);
        String format;
        String appSecret = "8m92ZmhT0j0uF4KRtZJycP6guZJMm6"; //测试服的数据appSecret
        String expressCode = dwResObject.getStr("expressCode");
        String inBoxes = dwResObject.getStr("inBoxes");
        String outbox = dwResObject.getStr("outbox");
        if (outbox == null && inBoxes == null){
            throw new BadRequestException("inBoxes,outbox 两个个字段不能同时为空");
        }

        String sign = dewuSupport.encoderByMd5(getSignStr(map)+"key="+appSecret);
        try {
            //验签
            if (StringUtil.equals(dwSign, sign)) {
                //保存数据
                orderDeliverService.addOrderMaterial(dwResObject);
                result.putOnce("message", "success");
            }else {
                result.putOnce("message", "false,  验签失败");
            }
            return result;
        } catch (Exception e) {
            result.putOnce("message","false :" + e.getMessage());
            return result;
        }
    }

    @Log("得物出库耗材")
    @ApiOperation("得物出库耗材")
    @GetMapping(value = "/check-deliver")
    public ResponseEntity<Object> checkorderDeliver(@Validated String mailNo){
        OrderMaterial orderMaterial = orderMaterialService.queryByMailNo(mailNo);
        if(orderMaterial == null){
            return null;
        }
        return new ResponseEntity<>(orderMaterial, HttpStatus.OK);
    }

    private String getSignStr(TreeMap<String,Object> map){

        StringBuilder builder=new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (ObjectUtil.isNull(entry.getValue())||"null".equalsIgnoreCase(entry.getValue()+""))
                continue;
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            builder.append("&");
        }
        return builder.toString();
    }

    @RequestMapping("/hand-out")
    @PreAuthorize("@el.check('orderDeliver:handOut')")
    public JSONObject handOut(@Validated String weight, @Validated String mailNo) {
        JSONObject jsonObject = new JSONObject();
        OrderDeliverStatusEnum deliver = orderDeliverService.deliver(weight, mailNo, null);
        jsonObject.putOnce("msg", deliver.getDescription());
        if (StringUtils.equals(OrderDeliverStatusEnum.CHECK.getCode(), deliver.getCode())
                || StringUtils.equals(OrderDeliverStatusEnum.ERROR.getCode(), deliver.getCode())) {
            jsonObject.putOnce("success", false);
        }else {
            jsonObject.putOnce("success", true);
        }
        return jsonObject;
    }

    // 批量出库，只能抖音使用
    @RequestMapping("/batch-hand-out")
    @PreAuthorize("@el.check('orderDeliver:handOut')")
    public JSONObject batchHandOut(@Validated String weight, @Validated String waveNo, @Validated String materialCode) {
        JSONObject jsonObject = crossBorderOrderService.batchDeliver(weight, waveNo, materialCode);
        return jsonObject;
    }

    @RequestMapping("/package-inspection")
    @PreAuthorize("@el.check('orderDeliver:packInspect')")
    public JSONObject packageInspection(@Validated String packageCode, @Validated String mailNo) {
        JSONObject jsonObject = new JSONObject();
        try {
            Boolean isSuccess = orderDeliverService.packageInspection(packageCode, mailNo);
            jsonObject.putOnce("success", isSuccess);
            jsonObject.putOnce("msg", "质检成功");
        }catch (Exception e){
            jsonObject.putOnce("success", false);
            jsonObject.putOnce("msg",e.getMessage()==null?"null":e.getMessage());
        }
        return jsonObject;
    }
    @RequestMapping("/print-outbound-batch-deliver-order")
    @AnonymousAccess
    @ApiOperation("批次交接单打印")
    public ResponseEntity<Object> printOutboundBatchDeliverOrder(String waveNo){
        List<OutboundBatchDeliverOrder>deliverOrders=orderDeliverService.getOutboundBatchDeliverOrder(waveNo);
        Map<String,Object>map = new HashMap<>();
        map.put("success",true);
        Map<String,Object>data = new HashMap<>();
        data.put("list",deliverOrders);
        data.put("printTime", DateUtils.now());
        map.put("data",data);
        return new ResponseEntity<>(map,HttpStatus.OK);
    }


    /**
     * 流水线出库，废弃
     * @param userid
     * @param msg
     * @return
     */
    @AnonymousAccess
    @RequestMapping("/f-line")
    @ApiOperation("流水线请求")
    public String fLine(String userid,
                        String msg) {
        try {
            JSONObject msgJson = XML.toJSONObject(msg);
            System.out.println("请求："+ msg);
            JSONObject requestJson = msgJson.getJSONObject("wmsFxRequest");
            String mailNo = requestJson.get("mailNo").toString();
            String weight = requestJson.get("weight").toString();// 千克
            OrderDeliverStatusEnum deliver = orderDeliverService.deliver(weight, mailNo, userid);
            if (StringUtil.equals(deliver.getCode(), OrderDeliverStatusEnum.CHECK.getCode())) {
                // 抽检
                return RET_PD_CHECK;
            }if (StringUtil.equals(deliver.getCode(), OrderDeliverStatusEnum.ERROR.getCode())) {
                // 异常
                return RET_PD_ERROR;
            }if (StringUtil.equals(deliver.getCode(), OrderDeliverStatusEnum.SF.getCode())) {
                // 抽检
                return RET_PD_ZTO;
            }else {
                return RET_PD_ERROR;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return RET_PD_ERROR;
        }
    }

}
