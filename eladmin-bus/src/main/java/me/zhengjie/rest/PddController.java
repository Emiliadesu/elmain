package me.zhengjie.rest;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.service.PddCloudPrintLogService;
import me.zhengjie.service.ShopTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


/**
* @website https://el-admin.vip
* @author wangmiao
* @date 2022-08-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "拼多多接口")
@RequestMapping("/api/pdd")
public class PddController {

    @Autowired
    private ShopTokenService shopTokenService;

    @Log("拼多多商家授权回调")
    @ApiOperation("拼多多商家授权回调")
    @GetMapping(value = "/callback")
    @AnonymousAccess
    public void callback(String code, String state, HttpServletResponse response) throws Exception{
        String resp=HttpUtil.post("http://pdd.fl56.net/api/pdd/callback?code="+code+"&state="+state,"");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setContentType("text/html;charset="+StandardCharsets.UTF_8.toString());
        response.getWriter().write(resp);
    }

    @Log("拼多多批量加密")
    @ApiOperation("拼多多批量加密")
    @PostMapping(value = "/batch-encrypt")
    public JSONObject batchEncrypt(@RequestBody String body,String shopId) {
        Map<String,Object>map = new HashMap<>();

        ShopToken shopToken = shopTokenService.queryByShopId(Long.valueOf(shopId));
        JSONObject obj = new JSONObject();
        obj.putOnce("shopCode",shopToken.getPlatformShopId());
        obj.putOnce("dataList",new JSONArray(body));
        map.put("data",obj);
        map.put("customersCode",1000);
        String resp=HttpUtil.post("http://pdd.fl56.net/api/pdd-erp/batch-encrypt",map);
        return new JSONObject(resp,true,true);
    }
}