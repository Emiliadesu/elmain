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
import me.zhengjie.domain.TransInfo;
import me.zhengjie.domain.TransLog;
import me.zhengjie.service.dto.TransInfoDto;
import me.zhengjie.service.dto.TransInfoQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-09-04
**/
public interface TransInfoService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(TransInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<TransInfoDto>
    */
    List<TransInfoDto> queryAll(TransInfoQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return TransInfoDto
     */
    TransInfoDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return TransInfoDto
    */
    TransInfoDto create(TransInfo resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(TransInfo resources);

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
    void download(List<TransInfoDto> all, HttpServletResponse response) throws IOException;

    void createTransByClear(ClearInfo clearInfo);

    TransInfo queryById(Long id);

    TransInfo queryByClearNo(String clearNo);

    TransInfo queryByOrderNo(String orderNo);

    void updateOptNode(TransLog log);
}