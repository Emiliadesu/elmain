package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.rest.model.jackYun.JackYunDeliveryOrderCreateRequest;
import me.zhengjie.rest.model.jackYun.JackYunDeliveryOrderCreateResponse;
import me.zhengjie.rest.model.jackYun.JackYunSingleItemSynchronizeRequest;
import me.zhengjie.rest.model.jackYun.JackYunSingleitemSynchronizeResponse;
import me.zhengjie.service.JackYunService;
import me.zhengjie.support.jackYun.JackYunBasicResponse;
import me.zhengjie.support.jackYun.JackYunSupport;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "吉客云接口")
@RequestMapping("/api/jack-yun")
public class JackYunController {
    @Autowired
    private JackYunSupport support;

    @Autowired
    private JackYunService jackYunService;

    @Log("吉客云消息下发")
    @ApiOperation("吉客云消息下发")
    @PostMapping(value = "/msg-push")
    @AnonymousAccess
    public JackYunBasicResponse entryOrderCreate(String method,
                                                 String format,
                                                 String content,
                                                 String timestamp,
                                                 String customerid,
                                                 String sign){
        System.out.println("吉客云消息下发method==="+method);
        System.out.println("吉客云消息下发format==="+format);
        System.out.println("吉客云消息下发content==="+content);
        System.out.println("吉客云消息下发timestamp==="+timestamp);
        System.out.println("吉客云消息下发customerid==="+customerid);
        System.out.println("吉客云消息下发sign==="+sign);
        String mySign=support.getSign(method,content,customerid,timestamp);
        if (!StringUtil.equals(mySign,sign)){
            JackYunBasicResponse response=new JackYunBasicResponse();
            response.returnFail("验签不过");
            return response;
        }
        return jackYunService.msgPush(method,content,customerid);
    }

    @Log("吉客云发货单下发")
    @ApiOperation("吉客云发货单下发")
    @PostMapping(value = "/entry-order-create")
    @AnonymousAccess
    public JackYunDeliveryOrderCreateResponse entryOrderCreate(@RequestBody String body,
                                                               String method,
                                                               String format,
                                                               String content,
                                                               String timestamp,
                                                               String customerid,
                                                               String sign){
        System.out.println("吉客云发货单body==="+body);
        System.out.println("吉客云发货单method==="+method);
        System.out.println("吉客云发货单format==="+format);
        System.out.println("吉客云发货单content==="+content);
        System.out.println("吉客云发货单timestamp==="+timestamp);
        System.out.println("吉客云发货单customerid==="+customerid);
        System.out.println("吉客云发货单sign==="+sign);
        return jackYunService.createOrder(new JSONObject(content).toBean(JackYunDeliveryOrderCreateRequest.class),customerid);
    }

    @Log("吉客云发货单取消")
    @ApiOperation("吉客云发货单取消")
    @PostMapping(value = "/order-cancel")
    @AnonymousAccess
    public JackYunBasicResponse orderCancel(@RequestBody String body,
                                                       String method,
                                                       String format,
                                                       String content,
                                                       String timestamp,
                                                       String customerid,
                                                       String sign){
        System.out.println("吉客云发货取消body==="+body);
        System.out.println("吉客云发货取消method==="+method);
        System.out.println("吉客云发货取消format==="+format);
        System.out.println("吉客云发货取消content==="+content);
        System.out.println("吉客云发货取消timestamp==="+timestamp);
        System.out.println("吉客云发货取消customerid==="+customerid);
        System.out.println("吉客云发货取消sign==="+sign);

        JackYunBasicResponse response=new JackYunBasicResponse();
        response.returnSucc();
        return response;
    }

    @Log("吉客云同步商品信息")
    @ApiOperation("吉客云同步商品信息")
    @PostMapping(value = "/single-item-synchronize")
    @AnonymousAccess
    public JackYunSingleitemSynchronizeResponse singleItemSync(@RequestBody String body,
                                                          String method,
                                                          String format,
                                                          String content,
                                                          String timestamp,
                                                          String customerid,
                                                          String sign){
        System.out.println("吉客云同步商品信息body==="+body);
        System.out.println("吉客云同步商品信息method==="+method);
        System.out.println("吉客云同步商品信息format==="+format);
        System.out.println("吉客云同步商品信息content==="+content);
        System.out.println("吉客云同步商品信息timestamp==="+timestamp);
        System.out.println("吉客云同步商品信息customerid==="+customerid);
        System.out.println("吉客云同步商品信息sign==="+sign);
        return jackYunService.syncGoodsInfo(new JSONObject(content).toBean(JackYunSingleItemSynchronizeRequest.class));
    }
}
