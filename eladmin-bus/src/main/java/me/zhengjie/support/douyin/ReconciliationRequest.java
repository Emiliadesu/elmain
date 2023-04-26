package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class ReconciliationRequest implements CommonApiParam {

    @Override
    public String getMethod() {
        return "crossBorder.inventory.reconciliation";
    }


    @Override
    public String getKeyWord() {
        return "";
    }

}
