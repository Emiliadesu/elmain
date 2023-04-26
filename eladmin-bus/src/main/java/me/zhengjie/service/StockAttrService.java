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

import me.zhengjie.domain.StockAttr;
import me.zhengjie.service.dto.StockAttrDto;
import me.zhengjie.service.dto.StockAttrQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author wangm
* @date 2021-06-08
**/
public interface StockAttrService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(StockAttrQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<StockAttrDto>
    */
    List<StockAttrDto> queryAll(StockAttrQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return StockAttrDto
     */
    StockAttrDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return StockAttrDto
    */
    StockAttrDto create(StockAttr resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(StockAttr resources);

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
    void download(List<StockAttrDto> all, HttpServletResponse response) throws IOException;

    StockAttr queryByWmsBatch(String wmsBatch);

    List<StockAttr> findByCustomerBatchNoAndSubType(String customerBatchNo,String subType);

    boolean exists(StockAttr stockAttr);

    void recordNewLotNum();
}
