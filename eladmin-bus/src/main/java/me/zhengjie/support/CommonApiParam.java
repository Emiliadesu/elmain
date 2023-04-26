package me.zhengjie.support;

import com.alibaba.fastjson.annotation.JSONField;

public interface CommonApiParam {

    @JSONField(serialize = false)
    String getMethod();

    @JSONField(serialize = false)
    String getKeyWord();

}
