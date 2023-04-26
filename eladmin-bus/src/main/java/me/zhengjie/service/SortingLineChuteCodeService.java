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

import me.zhengjie.domain.SortingLineChuteCode;
import me.zhengjie.service.dto.SortingLineChuteCodeDto;
import me.zhengjie.service.dto.SortingLineChuteCodeQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-10-04
**/
public interface SortingLineChuteCodeService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(SortingLineChuteCodeQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<SortingLineChuteCodeDto>
    */
    List<SortingLineChuteCodeDto> queryAll(SortingLineChuteCodeQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return SortingLineChuteCodeDto
     */
    SortingLineChuteCodeDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return SortingLineChuteCodeDto
    */
    SortingLineChuteCodeDto create(SortingLineChuteCode resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(SortingLineChuteCode resources);

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
    void download(List<SortingLineChuteCodeDto> all, HttpServletResponse response) throws IOException;

    List<SortingLineChuteCode> queryByLineId(Long lineId);

    SortingLineChuteCode queryByUserIdAndRuleCode1(String userId, String ruleCode1);

    SortingLineChuteCode queryByUserIdAndRuleCode2(String userId, String cnChuteCode);

    SortingLineChuteCode queryByUserIdAndRuleCode(String userId, String dyCode);

    SortingLineChuteCode queryByUserIdAndChuteCode(String userId, String dyCode);
}