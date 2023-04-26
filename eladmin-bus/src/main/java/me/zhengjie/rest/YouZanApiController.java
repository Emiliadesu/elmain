/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousGetMapping;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.support.youzan.YouZanSupport;
import me.zhengjie.utils.ResponseEntityUtil;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

/**
* @author wangm
* @date 2021年8月30日
*/
@RestController
@RequiredArgsConstructor
@Api(tags = "有赞对外API")
@RequestMapping("/api/youzan")
public class YouZanApiController {
    @Autowired
    private YouZanSupport youZanSupport;

    @Autowired
    private ShopTokenService shopTokenService;

    @Log("接口请求测试")
    @ApiOperation("接口请求测试")
    @AnonymousGetMapping(value = "/test")
    @AnonymousAccess
    public ResponseEntity test() {
        return ResponseEntityUtil.getSuccess("成功");
    }

    @Log("有赞授权回调地址")
    @ApiOperation("有赞授权回调地址")
    @AnonymousGetMapping(value = "/callback")
    @AnonymousAccess
    public ResponseEntity callback(@RequestParam(name = "code") String code, String state) {
        ShopToken token=new ShopToken();
        try {
            youZanSupport.getTokenByToolType(code,token);
        }catch (Throwable e){
            e.printStackTrace();
            return ResponseEntityUtil.getFail("授权失败，获取token失败-"+e.getMessage());
        }
        if (StringUtil.isNotEmpty(token.getAccessToken())){
            ShopToken exist=shopTokenService.queryByPaltShopId(token.getPlatformShopId());
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
