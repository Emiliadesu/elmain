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

import me.zhengjie.domain.ReturnGather;
import me.zhengjie.service.dto.ReturnGatherDto;
import me.zhengjie.service.dto.ReturnGatherQueryCriteria;
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
* @date 2022-04-06
**/
public interface ReturnGatherService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(ReturnGatherQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<ReturnGatherDto>
    */
    List<ReturnGatherDto> queryAll(ReturnGatherQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return ReturnGatherDto
     */
    ReturnGatherDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return ReturnGatherDto
    */
    ReturnGatherDto create(ReturnGather resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(ReturnGather resources);

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
    void download(List<ReturnGatherDto> all, HttpServletResponse response) throws IOException;

    void createGather(Long shopId);

    ReturnGather queryById(Long id);

    List<Map<String, Object>> queryWaitGather();

    ReturnGather queryByGatherNo(String gatherNo);

    ReturnGather queryByIdWithDetails(Long id);

    void pushToWms(Long id) throws UnsupportedEncodingException;

    void updateWMsStatus(Long id);

    void closeOrder(Long id);

    void doExportDetails(List<ReturnGatherDto> queryAll, HttpServletResponse response) throws IOException;

    void doExportReturnOrder(List<ReturnGatherDto> queryAll, HttpServletResponse response) throws IOException;

    void compare(Long id);
}