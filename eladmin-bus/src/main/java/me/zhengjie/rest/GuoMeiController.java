package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenMsgPush;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.GuoMeiService;
import me.zhengjie.support.guomei.GuoMeiCommonParamResponse;
import me.zhengjie.support.guomei.GuomeiSupport;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import me.zhengjie.utils.JSONUtils;

import java.sql.Timestamp;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@Api(tags = "国美对外API")
@RequestMapping("/api/guomei")
public class GuoMeiController {

    @Autowired
    private GuoMeiService guoMeiService;

    @Autowired
    private GuomeiSupport guomeiSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Log("接收国美订单")
    @ApiOperation("接收国美订单下发")
    @AnonymousPostMapping("/pushOrder")
    public JSONObject pushGuoMeiOrder(@RequestBody(required = false) String data) {
        JSONObject result = new JSONObject();
        System.out.println(data);
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(data);
        System.out.println(object);
        String guomeiSign = object.getString("sign");
        Long timestamp = Long.parseLong(object.getString("timestamp"));
        System.out.println(timestamp);
        object.remove("sign");
        String sign = guomeiSupport.getSign(object.toString(), guomeiSupport.getSignSecret(), timestamp);
        try {
            if (StringUtil.equals(guomeiSign, sign)) {
                guoMeiService.createOrder(object);
                result.putOnce("code", 200);
                result.putOnce("message", "success");
            } else {
                result.putOnce("code", 999);
                result.putOnce("message", "false,  验签失败");
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            result.putOnce("code", 999);
            result.putOnce("message", e.getMessage());
            return result;
        }
    }

    @Log("国美订单取消信息")
    @ApiOperation("接收国美订单取消信息")
    @AnonymousPostMapping("/pushOrderCancel")
    public JSONObject pushOrderCancel(@RequestBody(required = false) String data) {
        System.out.println(data);
        JSONObject result = new JSONObject();
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(data);
        String guomeiSign = object.getString("sign");
        Long timestamp = Long.parseLong(object.getString("timestamp"));
        object.remove("sign");
        String sign = guomeiSupport.getSign(object.toString(), guomeiSupport.getSignSecret(), timestamp);
        try {
            if (StringUtil.equals(guomeiSign, sign)) {
                guoMeiService.orderCancel(object);
                result.putOnce("code", 200);
                result.putOnce("message", "success");
                return result;

            } else {
                result.putOnce("code", 999);
                result.putOnce("message", "false,  验签失败");
                return result;

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            result.putOnce("code", 200);
            result.putOnce("message", "success");
            return result;
        }
    }

    @Log("国美更改订购人信息")
    @ApiOperation("接收国美更改订购人信息")
    @AnonymousPostMapping("/updateOrderMessage")
    public JSONObject pushUpdateOrder(@RequestBody(required = false) String data) {
        JSONObject result = new JSONObject();
        com.alibaba.fastjson.JSONObject object = JSON.parseObject(data);
        String guomeiSign = object.getString("sign");
        Long timestamp = Long.parseLong(object.getString("timestamp"));
        object.remove("sign");
        String sign = guomeiSupport.getSign(object.toString(), guomeiSupport.getSignSecret(), timestamp);
        try {
            if (StringUtil.equals(guomeiSign, sign)) {
                guoMeiService.updateOrder(object);
                result.putOnce("code", 200);
                result.putOnce("message", "success");
                return result;
            } else {
                result.putOnce("code", 999);
                result.putOnce("message", "false,  验签失败");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.putOnce("code", 200);
            result.putOnce("message", "success");
            return result;
        }
    }

    @Log("测试国美回传")
    @ApiOperation("测试国美回传")
    @AnonymousPostMapping("/testHuiChuan")
    public void testHuiChuan(@RequestBody(required = false) String data) {
      CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
      crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_200.getCode().intValue());
      crossBorderOrder.setIsLock("0");
      guoMeiService.confirmOrder(crossBorderOrder);
    }

    @Log("接单异常回传")
    @ApiOperation("接单异常回传")
    @AnonymousPostMapping("/testHuiChuanErr")
    public void testHuiChuanErr(@RequestBody(required = false) String data) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_999.getCode().intValue());
        guoMeiService.confirmOrderErr(crossBorderOrder);
    }

    @Log("清关作业状态回传开始")
    @ApiOperation("清关作业状态回传开始")
    @AnonymousPostMapping("/testQingGuan")
    public void testQingGuan(@RequestBody(required = false) String data) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_220.getCode().intValue());
        guoMeiService.confirmClearStart(crossBorderOrder);
    }

    @Log("清关回传成功")
    @ApiOperation("清关回传成功")
    @AnonymousPostMapping("/testQingGuanSuc")
    public void testQingGuanSuc(@RequestBody(required = false) String data) throws Exception {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_230.getCode().intValue());
        guoMeiService.confirmClearSuccess(crossBorderOrder);
    }

    @Log("拣货开始")
    @ApiOperation("拣货开始")
    @AnonymousPostMapping("/testJianHuo")
    public void testJianHuo(@RequestBody(required = false) String data) throws Exception {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        guoMeiService.confirmPickStart(crossBorderOrder);
    }

    @Log("回传拣货完成")
    @ApiOperation("回传拣货完成")
    @AnonymousPostMapping("/testJianHuoSuc")
    public void testJianHuoSuc(@RequestBody(required = false) String data) throws Exception {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        guoMeiService.confirmPickEnd(crossBorderOrder);
    }

    @Log("回传打包")
    @ApiOperation("回传打包")
    @AnonymousPostMapping("/testDaBao")
    public void testDaBao(@RequestBody(required = false) String data) throws Exception {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_240.getCode().intValue());
        crossBorderOrder.setSendPickFlag("1");
        guoMeiService.confirmPack(crossBorderOrder);
    }

    @Log("出库节点回传")
    @ApiOperation("出库节点回传")
    @AnonymousPostMapping("/testChuKu")
    public void testChuKu(@RequestBody(required = false) String data) throws Exception {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_240.getCode().intValue());
        crossBorderOrder.setPackBackTime(new Timestamp(System.currentTimeMillis()));
        crossBorderOrder.setSendPickFlag("1");
        guoMeiService.confirmDeliver(crossBorderOrder);
    }

    @Log("拦截成功")
    @ApiOperation("拦截成功")
    @AnonymousPostMapping("/testQuXiaoSuc")
    public void testQuXiaoSuc(@RequestBody(required = false) String data,Integer status) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        guoMeiService.confirmInterceptionSucc(crossBorderOrder,status);
    }

    @Log("拦截成功")
    @ApiOperation("拦截成功")
    @AnonymousPostMapping("/testQuXiaoFal")
    public void testQuXiaoFal(@RequestBody(required = false) String data,Integer status) {
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByCrossBorderNo(data);
        guoMeiService.confirmInterceptionErr(crossBorderOrder,status);
    }
}
