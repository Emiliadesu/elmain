package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.CrossBorderOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luob
 * @description
 * @date 2021/12/8
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "富勒接口")
@RequestMapping("/api/flux")
@Slf4j
public class FluxController {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @AnonymousAccess
    @RequestMapping("/msg-push")
    @Log("富勒消息推送")
    @ApiOperation("富勒消息推送")
    public String msgPush(@RequestBody(required = false) String data) {
        JSONObject result = new JSONObject();
        log.info("收到富勒推送信息，data：{}", data);
        crossBorderOrderService.updateOrderWmsStatus(data);
        JSONObject Response = new JSONObject();
        JSONObject returnObj = new JSONObject();
        returnObj.putOnce("returnFlag", "1");
        returnObj.putOnce("returnCode", "0000");
        Response.putOnce("return", returnObj);
        result.putOnce("Response", Response);
        return result.toStringPretty();
    }

}
