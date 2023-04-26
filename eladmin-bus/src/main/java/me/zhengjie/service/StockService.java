package me.zhengjie.service;

import cn.hutool.json.JSONObject;

public interface StockService {

    JSONObject queryStock(String decData, String customersCode);

    JSONObject querySapStock(String decData, String customersCode);
}
