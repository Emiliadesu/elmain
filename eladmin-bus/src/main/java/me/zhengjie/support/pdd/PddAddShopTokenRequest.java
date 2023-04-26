package me.zhengjie.support.pdd;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class PddAddShopTokenRequest extends PddShopToken implements PddCommonRequest{

    @Override
    public String getApiPath() {
        return "/api/pdd/add-shop-token";
    }
}
