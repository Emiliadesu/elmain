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

import cn.hutool.json.JSONObject;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.service.dto.CrossBorderOrderDto;
import me.zhengjie.service.dto.CrossBorderOrderQueryCriteria;
import me.zhengjie.service.dto.HomeDto;
import me.zhengjie.support.oms.OrderMain;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-03-25
**/
public interface CrossBorderOrderService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(CrossBorderOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<CrossBorderOrderDto>
    */
    List<CrossBorderOrderDto> queryAll(CrossBorderOrderQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return CrossBorderOrderDto
     */
    CrossBorderOrderDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return CrossBorderOrderDto
    */
    CrossBorderOrderDto create(CrossBorderOrder resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(CrossBorderOrder resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    /**
    * 导出数据
    * @param response /
    * @throws IOException /
    */
    void download(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException;

    void declare(String orderId) throws Exception;

    void refreshClearStatus(String orderId) throws Exception;

    CrossBorderOrder queryByOrderNo(String orderId);

    CrossBorderOrder queryById(Long id);

    /**
     * 更新申报单状态
     */
    void updateMftStatus();

    /**
     * 根据申报单号查询订单
     * @param mftNo
     * @return
     */
    CrossBorderOrder queryByMftNo(String mftNo);

    /**
     * 根据运单号查询订单
     * @param mftNo
     * @return
     */
    CrossBorderOrder queryByMailNo(String mftNo);

    List<CrossBorderOrder> findByIds(String ids);

    /**
     * 发货
     * @param weight
     * @param mailNo
     * @throws Exception
     */
    String deliver(String weight, String mailNo, String materialCode) throws Exception;

    /**
     * 取消
     * @param id
     */
    void cancel(Long id) throws Exception;

    CrossBorderOrder queryByIdWithDetails(Long aLong);

    /**
     * 保存同时保存明细
     * @param order
     * @return
     */
    CrossBorderOrder createWithDetail(CrossBorderOrder order);

    /**
     * 处理取消订单
     */
    void updateCancelOrder();

    Map<String,Object>  orderTotalCount(String startTime, String endTime);

    List<Map<String, Object>> shopOrderCount(String startTime, String endTime);

    /**
     * 根据条件查询订单
     * @param criteria
     * @param pageable
     * @return
     */
    Map<String,Object> queryOrders(CrossBorderOrderQueryCriteria criteria, Pageable pageable);

    /**
     * 解冻
     * @param order
     */
    void unFreeze(CrossBorderOrder order);

    /**
     * 批量处理解冻
     */
    void unFreezeBatch();

    /**
     * 小时播报数据
     * @return
     */
    Map<String, Map<String, Integer>> reportHourOrder();

    void handelDecErr();

    void cancelDec(Long id) throws Exception;

    void downloadDetails(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException;

    void refreshCancelStatus(String orderBody) throws Exception;

    void updateDecDelStatus(Long id) throws Exception;

    void refreshDecInfo(String orderId) throws Exception;

    void handel200() throws Exception;

    void refreshWmsStatus();

    void refreshWmsStatus(String orderId);

    void refreshWmsStatusAndCancel(String body) throws Exception;

    void refreshPlatformStatus();

    void refreshPlatformStatus2();

    void refreshPlatformStatus(String body) throws Exception;

    void refreshPlatformCancelStatus();

    void declarePdd(CrossBorderOrder order) throws Exception;

    void decrypt(CrossBorderOrder order) throws Exception;

    void encrypt(CrossBorderOrder declareEnc) throws Exception;

    void rePullByNoPayNo();

    void orderDeclare() throws Exception;

    void decryptMask(CrossBorderOrder order) throws Exception ;

    void pullOrderByTimeRange(Date startTime, Date endTime,String shopCode);

    // 计算理论重量
    BigDecimal countTheoryWeight(CrossBorderOrder order);

    Integer getPlatformStatus(CrossBorderOrder order) throws Exception;

    CrossBorderOrder queryShopId(Long shopId);

    Map<String, Object> orderTotalCountAll(Map<String, Object> map);

    List<Map<String, Object>> shopOrderCountAll(Map<String, Object> map);

    void updateWmsOrderTime();

    void updateWmsOrderTime(String id);

    void lockOrder(CrossBorderOrder order) throws Exception;

    void cancelLockOrder(CrossBorderOrder order) throws Exception;

    void refundOpt(Long id, Integer action, String optionReason);

    void pushWmsOrder(String id) throws Exception;

    CrossBorderOrder queryByOrderNoAndDeclareNo(String orderNo, String declareNo);

    CrossBorderOrder queryByCrossBorderNo(String orderNo);

    void updateOrderWmsStatus(String data);

    void upgradeLogisticsInfo();

    void checkCollected(String body) throws Exception;

    void refreshDecInfo();

    void confirmClearDelStart();

    void confirmClearDelSucc();

    void confirmCloseOrder();

    void pushOrder(OrderMain orderMain);

    CrossBorderOrder queryByOrderNoWithDetails(String orderNo);

    void cancelWmsOrder(Long id);

    void deliverWms(Long id);

    CrossBorderOrder queryByCrossBorderNoAndDeclareNo(String crossBorderNo, String externalNo);

    List<CrossBorderOrder> queryByWaveNo(String waveNo);

    CrossBorderOrder queryByCrossBorderNoWithDetails(String crossBorderNo);

    // 校验重量
    void checkTheoryWeight(CrossBorderOrder order);

    JSONObject batchDeliver(String weight, String waveNo, String materialCode);

    void updateDecDelStatus();

    BigDecimal materialCodeChange(String waveNo, String materialCode);

    CrossBorderOrder queryMaterialCodeByMailNo(String mailNo);

    CrossBorderOrder materialCodeChangeByMailNo(String mailNo, String materialCode);

    JSONObject batchDeliverScan(String waveNo);

    void clearDelIsSucc(String day);

    CrossBorderOrder queryByLogisticsNo(String logisticsNo);

    LinkedList<Map<String, Object>> getOrderHour(String startCountTime, String endCountTime, String startOrderCreateTime, String endOrderCreateTime, String preSell);

    void downloadIn(CrossBorderOrderQueryCriteria criteria, HttpServletResponse response) throws IOException;

    CrossBorderOrder queryByLpCode(String cnOrderNo);

    JSONObject orderProcess(String startCreateTime, String createTime, String platformCode);

    List<CrossBorderOrder> queryByCNDeliverOrder();

    void createO2OOrder(String data);
}
