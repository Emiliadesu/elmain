package me.zhengjie.support.douyin;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;

public class CBOrderReturnChild {

    @JSONField(name = "item_no")
    private String itemNo;

    @JSONField(name = "bar_code")
    private String barCode;

    @JSONField(name = "item_name")
    private String itemName;

    @JSONField(name = "qty")
    private Integer qty;

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
