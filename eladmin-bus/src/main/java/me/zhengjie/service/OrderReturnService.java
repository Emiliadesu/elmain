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

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.OrderReturn;
import me.zhengjie.domain.OrderReturnDetails;
import me.zhengjie.service.dto.*;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-04-14
**/
public interface OrderReturnService {

    void updateWMsStatus(Long id);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(OrderReturnQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<OrderReturnDto>
    */
    List<OrderReturnDto> queryAll(OrderReturnQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return OrderReturnDto
     */
    OrderReturnDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return OrderReturnDto
    */
    OrderReturnDto create(OrderReturn resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(OrderReturn resources);

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
    void download(List<OrderReturnDto> all, HttpServletResponse response) throws IOException;

    void downloadDetails(Long id, HttpServletResponse response) throws IOException;

    void downloadDetails(OrderReturnQueryCriteria criteria, HttpServletResponse response)throws IOException;

    void confirmDecEnd(Long id) throws UnsupportedEncodingException, Exception;

    OrderReturn returnBook(String mailNo) throws Exception;

    void returnBookBack(OrderReturn orderReturn);

    OrderReturn queryByMailNo(String mailNo);

    void checkBarcode(String mailNo, String barcode);

    void checkBack(OrderReturn orderReturn) throws Exception;

    void createWithDetail(OrderReturn orderReturn);

    OrderReturn queryByLogisNo(String logisticsNo);

    OrderReturn queryByIdWithDetails(Long id);

    void check(String checkTyp, String mailNo, List<OrderReturnDetails> itemList) throws Exception;

    void createReturns(List<OrderReturn> orderReturnList);

    /**
     * 申报
     * @param s
     * @throws Exception
     */
    void declare(String s) throws Exception;

    /**
     * 更新申报状态
     */
    void updateDecStatus();

    /**
     * 上架完成
     * @param s
     * @throws Exception
     */
    void ground(String s) throws Exception;


    /**
     * 创建退货单
     * @param orderId
     * @param isBorder
     */
    void createReturn(Long orderId, String isBorder);

    OrderReturn queryById(Long id);

    OrderReturn queryByOrderNo(String orderNo);

    OrderReturn queryDeclareNo(String declareNo);

    void updateWMsStatus();

    void queryGatherByShop(Long shopId, Long gatherId, String gatherNo);

    List<OrderReturn> queryDecEnd();

    List<Map<String, Object>> queryWaitGather();

    void closeOrder(Long id);

    String genOrderNo();

    Map<String, Object> queryOrders(OrderReturnQueryCriteria criteria, Pageable pageable);

    OrderReturn queryByOrderNoWithDetails(String orderNo);

    void declareBatch();

    void forceDec(Long id) throws Exception;

    void upload4Pl(List<Map<String, Object>> maps);

    List<OrderReturn> querybyGatherNoAndShopId(String gatherNo, Long shopId);
}