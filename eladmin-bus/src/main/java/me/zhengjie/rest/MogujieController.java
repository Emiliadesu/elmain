package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.support.moGuJie.MoGuJieSupport;
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
@Api(tags = "蘑菇街对外API")
@RequestMapping("/api/mgj")
public class MogujieController {
    @Autowired
    private ShopTokenService shopTokenService;
    @Autowired
    private MoGuJieSupport moGuJieSupport;

    @Log("蘑菇街授权回调地址")
    @ApiOperation("蘑菇街授权回调地址")
    @AnonymousGetMapping(value = "/callback")
    @AnonymousAccess
    public ResponseEntity callback(@RequestParam(name = "code") String code, String state) {
        ShopToken token=new ShopToken();
        if (StringUtil.isNotBlank(state)){
            token.setId(Long.valueOf(state));
        }
        try {
            moGuJieSupport.getToken(code,token);
        }catch (Throwable e){
            e.printStackTrace();
            return ResponseEntityUtil.getFail("授权失败，获取token失败-"+e.getMessage());
        }
        if (StringUtil.isNotEmpty(token.getAccessToken())&&StringUtil.isNotEmpty(state)){
            ShopToken exist=shopTokenService.queryById(Long.valueOf(state));
            if (exist!=null){
                token.setId(exist.getId());
                token.setCode(code);
                token.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
                shopTokenService.update(token);
            }
        }
        return ResponseEntityUtil.getSuccess("授权成功");
    }
}
