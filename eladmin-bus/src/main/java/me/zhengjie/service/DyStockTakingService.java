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

import me.zhengjie.domain.DyStockTaking;
import me.zhengjie.service.dto.DyStockTakingDto;
import me.zhengjie.service.dto.DyStockTakingQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author wangm
* @date 2021-09-26
**/
public interface DyStockTakingService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(DyStockTakingQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<DyStockTakingDto>
    */
    List<DyStockTakingDto> queryAll(DyStockTakingQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return DyStockTakingDto
     */
    DyStockTakingDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return DyStockTakingDto
    */
    DyStockTakingDto create(DyStockTaking resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(DyStockTaking resources);

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
    void download(List<DyStockTakingDto> all, HttpServletResponse response) throws IOException;

    void push();

    List<Map<String, Object>> uploadSku(List<Map<String, Object>> maps,Long shopId);

    DyStockTaking queryByIdempotentNo(String idempotentNo);

    Map<String,Object> trackInboundOrderNo(String wmsBatch, String location);

    void pushProcess(Long id) throws Exception;

    DyStockTaking queryById(Long id);

    void pushSuccess(Long id) throws Exception;

    void pushCancel(Long id) throws Exception;
}
