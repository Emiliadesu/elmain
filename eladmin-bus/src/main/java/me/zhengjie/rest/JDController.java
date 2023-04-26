package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.JDService;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.support.jingdong.JDSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.ResponseEntityUtil;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
@Api(tags = "京东对外API")
@RequestMapping("/api/jd")
public class JDController {

    @Autowired
    private JDService jdService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private JDSupport jdSupport;

    @Log("京东物流授权回调地址")
    @ApiOperation("京东物流授权回调地址")
    @AnonymousGetMapping(value = "/callback")
    @AnonymousAccess
    public ResponseEntity callback(String code,String state){
        ShopToken token=new ShopToken();
        token.setCode(code);
        if (StringUtil.isNotBlank(state)){
            token.setId(Long.valueOf(state));
        }
        try {
            jdSupport.getToken(token);
        } catch (Throwable e) {
            e.printStackTrace();
            return ResponseEntityUtil.getFail("授权失败，获取token失败-"+e.getMessage());
        }
        //判断accessToken和state 都不为空
        if (StringUtil.isNotEmpty(token.getAccessToken())&&StringUtil.isNotEmpty(state)){
            ShopToken exist=shopTokenService.queryById(Long.valueOf(state));
            if (exist!=null){
                token.setCode(code);
                token.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
                shopTokenService.update(token);
            }
        }
        return ResponseEntityUtil.getSuccess("授权成功");
    }

    @Log("京东物流查询物流消息")
    @ApiOperation("京东物流运单轨迹")
    @AnonymousGetMapping(value = "/orderGet")
    @AnonymousAccess
    public ResponseEntity orderGet(String orderNo){
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByOrderNo(orderNo);
        if (crossBorderOrder == null)
            throw new BadRequestException("该orderNo错误");
        if (crossBorderOrder.getOrderNo().equals(orderNo)){
            jdService.queryOrder(orderNo);
        }else{
            throw new BadRequestException("京东物流查询轨迹失败");
        }
        return ResponseEntityUtil.getSuccess("查询成功,请查看订单日志");
    }
}
