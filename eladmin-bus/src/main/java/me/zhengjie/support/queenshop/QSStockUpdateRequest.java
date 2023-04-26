package me.zhengjie.support.queenshop;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

import java.util.List;

public class QSStockUpdateRequest implements CommonApiParam{

    @Override
    public String getMethod(){return "qs.pop.goods.stock.update";}

    @Override
    public String getKeyWord() {
        return String.valueOf(wmsId);
    }

    //更新原因
    @JSONField(name = "remark")
    private String remark;

    @JSONField(name = "wmsId")
    private Integer wmsId;

    @JSONField(name = "stockDetails")
    private List<QSStockDetails> stockDetails;

    public List<QSStockDetails> getStockDetails() {
        return stockDetails;
    }

    public void setStockDetails(List<QSStockDetails> stockDetails) {
        this.stockDetails = stockDetails;
    }

    public Integer getWmsId() {
        return wmsId;
    }

    public void setWmsId(Integer wmsId) {
        this.wmsId = wmsId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    @Override
    public String toString() {
        return "QSStockUpdateRequest{" +
                "remark='" + remark + '\'' +
                ", wmsId=" + wmsId +
                '}';
    }
}
