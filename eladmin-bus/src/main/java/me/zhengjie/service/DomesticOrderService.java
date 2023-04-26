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

import me.zhengjie.domain.DomesticOrder;
import me.zhengjie.service.dto.DomesticOrderDto;
import me.zhengjie.service.dto.DomesticOrderQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2022-04-11
**/
public interface DomesticOrderService {

    void refreshWmsStatus(Long orderId);

    DomesticOrder queryByLogisticsNo(String logisticsNo);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(DomesticOrderQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<DomesticOrderDto>
    */
    List<DomesticOrderDto> queryAll(DomesticOrderQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return DomesticOrderDto
     */
    DomesticOrderDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return DomesticOrderDto
    */
    DomesticOrderDto create(DomesticOrder resources);

    @Transactional(rollbackFor = Exception.class)
    void createWithDetails(DomesticOrder order);

    /**
    * 编辑
    * @param resources /
    */
    void update(DomesticOrder resources);

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
    void download(List<DomesticOrderDto> all, HttpServletResponse response) throws IOException;

    void uploadOrder(List<Map<String, Object>> maps, String name);

    DomesticOrder queryByOrderNo(String orderNo);

    DomesticOrder queryByIdWithDetails(Long id);

    void confirmOrder(Long id);

    DomesticOrder queryById(Long id);

    void checkPass(Long id) throws Exception;

    void preHandle(Long id) throws Exception;

    void deliver(String weight, String mailNo) throws Exception;

    void confirmDeliver(Long id);

    void cancel(Long id);
}