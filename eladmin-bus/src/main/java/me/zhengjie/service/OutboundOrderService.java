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

import me.zhengjie.domain.OutboundOrder;
import me.zhengjie.rest.model.douyin.TallyOrderReview;
import me.zhengjie.service.dto.OutboundOrderDto;
import me.zhengjie.service.dto.OutboundOrderQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luob
 * @website https://el-admin.vip
 * @description 服务接口
 * @date 2021-07-13
 **/
public interface OutboundOrderService {

    /**
     * 查询数据分页
     *
     * @param criteria 条件
     * @param pageable 分页参数
     * @return Map<String, Object>
     */
    Map<String, Object> queryAll(OutboundOrderQueryCriteria criteria, Pageable pageable);

    /**
     * 查询所有数据不分页
     *
     * @param criteria 条件参数
     * @return List<OutboundOrderDto>
     */
    List<OutboundOrderDto> queryAll(OutboundOrderQueryCriteria criteria);

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return OutboundOrderDto
     */
    OutboundOrderDto findById(Long id);

    /**
     * 创建
     *
     * @param resources /
     * @return OutboundOrderDto
     */
    OutboundOrderDto create(OutboundOrder resources);

    /**
     * 编辑
     *
     * @param resources /
     */
    void update(OutboundOrder resources);

    /**
     * 多选删除
     *
     * @param ids /
     */
    void deleteAll(Long[] ids);

    /**
     * 导出数据
     *
     * @param all      待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<OutboundOrderDto> all, HttpServletResponse response) throws IOException;

    void refreshStatus(Long id);

    void refreshStatus();

    OutboundOrder queryByIdWithDetails(Long id);

    OutboundOrder queryByOrderNo(String orderNo);

    void verifyPass(Long orderId) throws Exception;

    List<Map<String,Object>> uploadSku(List<Map<String, Object>> maps);

    void dyConfirmCancel(Long id);

    void dyConfirmOrder(Long id);

    void dyConfirmStockTally(Long id);

    void dyConfirmStockedTally(Long id);

    void dyConfirmOut(Long id);

    OutboundOrder queryByOutNo(String outNo);

    void listenFluxOutStatus();

    void tallyReview(TallyOrderReview tallyOrderReview);

    void freezeOrder(Long id,String freezeReason);

    void freezeOrder(OutboundOrder outboundOrder, String freezeReason);

    void doExportDetails(List<OutboundOrderDto> all, HttpServletResponse response) throws IOException;
}
