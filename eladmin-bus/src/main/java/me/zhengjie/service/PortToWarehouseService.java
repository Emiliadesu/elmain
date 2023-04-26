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

import me.zhengjie.domain.PortToWarehouse;
import me.zhengjie.service.dto.PortToWarehouseDto;
import me.zhengjie.service.dto.PortToWarehouseQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author lh
* @date 2021-01-10
**/
public interface PortToWarehouseService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(PortToWarehouseQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<PortToWarehouseDto>
    */
    List<PortToWarehouseDto> queryAll(PortToWarehouseQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return PortToWarehouseDto
     */
    PortToWarehouseDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return PortToWarehouseDto
    */
    PortToWarehouseDto create(PortToWarehouse resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(PortToWarehouse resources);

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
    void download(List<PortToWarehouseDto> all, HttpServletResponse response) throws IOException;

    /**
     * 上传文件
     * @param id
     * @param file
     */
    void uploadfile(Long id, MultipartFile file);
    /**
     * 下载文件
     */
    void dewnload(Long id, HttpServletRequest request,HttpServletResponse response);

    /**
     * 修改参数
     * @param id
     * @param status
     * @param time
     * @param statusname
     */
    void updateparam(Long id, String status, Timestamp time,String statusname);

    void updateReason(Long id,String name,String text);
}
