package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class CBOrderSorterRegisterRequest implements CommonApiParam {
    @Override
    public String getMethod() {
        return "/api/sorter/register";
    }

    @Override
    public String getKeyWord() {
        return this.getName();
    }

    /**
     * 设备名称
     */
    @JSONField(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
