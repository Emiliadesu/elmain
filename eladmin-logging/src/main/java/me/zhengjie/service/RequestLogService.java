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

import me.zhengjie.domain.RequestLog;
import me.zhengjie.service.dto.RequestLogDto;
import me.zhengjie.service.dto.RequestLogQueryCriteria;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-04-28
**/
public interface RequestLogService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(RequestLogQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<RequestLogDto>
    */
    List<RequestLogDto> queryAll(RequestLogQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return RequestLogDto
     */
    RequestLogDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return RequestLogDto
    */
    RequestLogDto create(RequestLog resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(RequestLog resources);

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
    void download(List<RequestLogDto> all, HttpServletResponse response) throws IOException;

    void save(ProceedingJoinPoint joinPoint, RequestLog log) throws Throwable;
}