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

import me.zhengjie.domain.InboundOrder;
import me.zhengjie.rest.model.douyin.TallyOrderReview;
import me.zhengjie.service.dto.InboundOrderDto;
import me.zhengjie.service.dto.InboundOrderQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-05-13
**/
public interface InboundOrderService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(InboundOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<InboundOrderDto>
    */
    List<InboundOrderDto> queryAll(InboundOrderQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return InboundOrderDto
     */
    InboundOrderDto findById(Long id);

    InboundOrder queryById(Long id);

    /**
    * 创建
    * @param resources /
    * @return InboundOrderDto
    */
    InboundOrderDto create(InboundOrder resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(InboundOrder resources);

    /**
    * 多选删除
    * @param ids /
    */
    void deleteAll(Long[] ids);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<InboundOrderDto> all, HttpServletResponse response) throws IOException;

    InboundOrder queryByOrderNo(String orderNo);

    InboundOrder queryByIdWithDetails(Long id);

    void arriveSign(Long orderId, String carNumber, Timestamp arriveTime);

    void verifyPass(Long orderId) throws Exception;

    void startTally(Long orderId);

    InboundOrder queryTallyById(Long id);

    void refreshStatus();

    void refreshStatus(Long aLong);

    List<Map<String, Object>> uploadSku(List<Map<String, Object>> maps);

    void dyConfirmOrder(Long id);

    void dyConfirmArrive(Long id);

    void dyConfirmStockTally(Long id);

    void dyConfirmStockedTally(Long id);

    void dyConfirmUp(Long id);

    void dyConfirmCancel(Long id);

    InboundOrder queryByOutNo(String outNo);

    void listenFluxInStatus();

    void tallyReview(TallyOrderReview tallyOrderReview);

    void cancel(Long id);

    void freezeOrder(Long id,String freezeReason);

    void freezeOrder(InboundOrder order,String freezeReason);

    void doExportDetails(List<InboundOrderDto> queryAll, HttpServletResponse response) throws IOException;
}
