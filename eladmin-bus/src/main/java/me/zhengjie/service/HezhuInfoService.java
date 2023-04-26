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

import me.zhengjie.domain.ClearInfo;
import me.zhengjie.domain.HezhuInfo;
import me.zhengjie.domain.HezhuLog;
import me.zhengjie.service.dto.HezhuInfoDto;
import me.zhengjie.service.dto.HezhuInfoQueryCriteria;
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
* @date 2021-08-26
**/
public interface HezhuInfoService {

    void createHeZhuByClear(ClearInfo clearInfo);

    HezhuInfo queryByClearNo(String clearNo);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(HezhuInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<HezhuInfoDto>
    */
    List<HezhuInfoDto> queryAll(HezhuInfoQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return HezhuInfoDto
     */
    HezhuInfoDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return HezhuInfoDto
    */
    HezhuInfoDto create(HezhuInfo resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(HezhuInfo resources);

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
    void download(List<HezhuInfoDto> all, HttpServletResponse response) throws IOException;

    HezhuInfo queryByOrderNo(String orderNo);

    void updateOptNode(HezhuLog log);

    HezhuInfo queryById(Long id);

    void spawnInOutBoundOrder(Long[] ids);
}
