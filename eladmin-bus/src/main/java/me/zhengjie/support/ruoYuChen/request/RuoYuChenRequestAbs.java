package me.zhengjie.support.ruoYuChen.request;

import cn.hutool.core.util.IdUtil;
import me.zhengjie.support.CommonApiParam;

public abstract class RuoYuChenRequestAbs implements CommonApiParam {
    @Override
    public String getKeyWord() {
        return IdUtil.randomUUID();
    }
}
