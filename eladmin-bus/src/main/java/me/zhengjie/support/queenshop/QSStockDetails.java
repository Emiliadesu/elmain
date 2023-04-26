package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.stereotype.Component;

@Component
public class QSStockDetails {
    //商家子品编码
    @JSONField(name = "supSkuNo")
    private Integer supSkuNo;

    //更新库存数量
    @JSONField(name = "stock")
    private Integer stock;

    //0 增加 1减少
    @JSONField(name = "type")
    private Integer type;

    public Integer getSupSkuNo() {
        return supSkuNo;
    }

    public void setSupSkuNo(Integer supSkuNo) {
        this.supSkuNo = supSkuNo;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
