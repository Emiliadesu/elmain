package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;

public class MeiTuanUpdateStockRequest implements CommonApiParam {
    /**
     *APP方门店id，即商家中台系统里门店的编码。如商家在操作绑定门店至开放平台应用中时，未绑定三方门店id信息，则默认APP方门店id与美团门店id相同
     */
    @JSONField(name = "app_poi_code")
    private String appPoiCode;

    /**
     * 药品仓库库存数据集合的json格式数组，
     * (1)上传参数包括：
     * a）app_medicine_code(必填项)，APP方药品id，即商家中台系统里药品的编码（spu_code值）。
     * b）stock(必填项)，为药品的库存，传非负整数,范围为0-99999。
     * (2)可传药品库存数据限定不超过200组。
     * (3)支持药品库存部分同步成功，部分同步失败。
     */
    @JSONField(name = "medicine_data")
    private String medicineData;


    public String getAppPoiCode() {
        return appPoiCode;
    }

    public void setAppPoiCode(String appPoiCode) {
        this.appPoiCode = appPoiCode;
    }

    public String getMedicineData() {
        return medicineData;
    }

    public void setMedicineData(String medicineData) {
        this.medicineData = medicineData;
    }

    @Override
    public String getMethod() {
        return "/api/v1/medicine/warehouse/stock";
    }

    @Override
    public String getKeyWord() {
        return getAppPoiCode();
    }
}
