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

import me.zhengjie.domain.AsnDetail;
import me.zhengjie.service.dto.AsnDetailDto;
import me.zhengjie.service.dto.AsnDetailQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author wangm
* @date 2021-03-28
**/
public interface AsnDetailService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(AsnDetailQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<AsnDetailDto>
    */
    List<AsnDetailDto> queryAll(AsnDetailQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return AsnDetailDto
     */
    AsnDetailDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return AsnDetailDto
    */
    AsnDetailDto create(AsnDetail resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(AsnDetail resources);

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
    void download(List<AsnDetailDto> all, HttpServletResponse response) throws IOException;

    void uploadAsnD(List<Map<String, Object>> maps, Long stockId);
}
