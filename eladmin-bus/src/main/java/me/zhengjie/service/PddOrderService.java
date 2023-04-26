/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.PddCloudPrintData;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.support.pdd.PddOrder;
import me.zhengjie.domain.ShopToken;

import java.util.Date;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author wangm
* @date 2021-06-10
**/
public interface PddOrderService {

    JSONObject printOrder(String dataJson) throws Exception;

    void pddPullOrder();

    void pullOrderByHours(Integer hours);

    PddCloudPrintData getMailNo(PddOrder order) throws Exception;

    String getOrderStatus(String orderSn, String shopCode) throws Exception;

    JSONArray getMailNo(String[] orderDatas, String shopId);

    void pullOrderByOrderSn(String[] orderSns, ShopToken shopToken) throws Exception;


    void confirmOrder(CrossBorderOrder crossBorderOrder)throws Exception;

    void confirmOrderByTools(String orderNo) throws Exception;

    void confirmClearStart(String orderId) throws Exception;

    void confirmClearStart(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmClearSuccess(String orderId) throws Exception;

    void confirmClearSuccess(CrossBorderOrder crossBorderOrder) throws Exception;

    void confirmDeliver(String orderId) throws Exception;

    void confirmDeliver(CrossBorderOrder crossBorderOrder) throws Exception;

    void pullOrderByPage(String body) throws Exception;

    void createOrder(String body)throws Exception;

    void getMailNo(CrossBorderOrder order) throws Exception;

    String declare(CrossBorderOrder order) throws Exception;

    String cancelDeclare(CrossBorderOrder order) throws Exception;

    void decryptMask(CrossBorderOrder order) throws Exception;

    void pullOrderByTimeRange(Date startTime, Date endTime, ShopInfo shopInfo);

    long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception;

    PddOrder getOrderByOrderSn(String orderSn,String shopCode) throws Exception;
}
