package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author luob
 * @description O2O订单
 * @date 2023/3/23
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "O2O订单接口")
@RequestMapping("/api/v4")
@Slf4j
public class O2OOrderController {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @AnonymousAccess
    @RequestMapping("/order")
    @Log("o2o订单推送")
    @ApiOperation("o2o订单推送")
    public JSONObject pushOrder1(@RequestBody(required = false) String dataBody,
                                @RequestParam(name = "client_key", required = false) String client_key,
                                @RequestParam(name = "sign" , required = false) String sign,
                                @RequestParam(name = "data" , required = false) String data) throws UnsupportedEncodingException {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        log.info("收到O2O订单client_key：{}", client_key);
        log.info("收到O2O订单sign：{}", sign);
        log.info("收到O2O订单data：{}", data);
        log.info("收到O2O订单dataBody：{}", dataBody);
        data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");//%转换为%25
        data = URLDecoder.decode(data, "UTF-8");
        log.info("收到O2O订单解码data：{}", data);
        crossBorderOrderService.createO2OOrder(data);
        result.putOnce("ret_num", "0");
        result.putOnce("ret_msg", "推送成功");
        return result;
    }

    @AnonymousAccess
    @RequestMapping("/order/")
    @Log("o2o订单推送")
    @ApiOperation("o2o订单推送")
    public JSONObject pushOrder(@RequestBody(required = false) String dataBody,
                                @RequestParam(name = "client_key", required = false) String client_key,
                                @RequestParam(name = "sign" , required = false) String sign,
                                @RequestParam(name = "data" , required = false) String data) throws UnsupportedEncodingException {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        log.info("收到O2O订单client_key：{}", client_key);
        log.info("收到O2O订单sign：{}", sign);
        log.info("收到O2O订单data：{}", data);
        log.info("收到O2O订单dataBody：{}", dataBody);
        data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");//%转换为%25
        data = URLDecoder.decode(data, "UTF-8");
        log.info("收到O2O订单解码data：{}", data);
        return result;
    }
}
