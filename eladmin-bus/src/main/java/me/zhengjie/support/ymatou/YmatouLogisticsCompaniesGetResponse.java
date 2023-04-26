package me.zhengjie.support.ymatou;

import com.alibaba.fastjson.annotation.JSONField;

public class YmatouLogisticsCompaniesGetResponse {
    @JSONField(name = "logistics_companies")
    private YmatouLogisticsCompany[] logisticsCompanies;

    public YmatouLogisticsCompany[] getLogisticsCompanies() {
        return logisticsCompanies;
    }

    public void setLogisticsCompanies(YmatouLogisticsCompany[] logisticsCompanies) {
        this.logisticsCompanies = logisticsCompanies;
    }
}
