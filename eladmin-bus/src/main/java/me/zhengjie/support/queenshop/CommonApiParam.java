package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;

public interface CommonApiParam {

    @JSONField(serialize = false)
    String getMethod();

}
