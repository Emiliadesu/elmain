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

import me.zhengjie.domain.DewuDeclarePush;
import me.zhengjie.service.dto.DewuDeclarePushDto;
import me.zhengjie.service.dto.DewuDeclarePushQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author wangm
* @date 2023-03-21
**/
public interface DewuDeclarePushService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(DewuDeclarePushQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<DewuDeclarePushDto>
    */
    List<DewuDeclarePushDto> queryAll(DewuDeclarePushQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return DewuDeclarePushDto
     */
    DewuDeclarePushDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return DewuDeclarePushDto
    */
    DewuDeclarePushDto create(DewuDeclarePush resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(DewuDeclarePush resources);

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
    void download(List<DewuDeclarePushDto> all, HttpServletResponse response) throws IOException;

    void createOrder(DewuDeclarePush dewuDeclarePush);

    DewuDeclarePush queryById(Long id);

    DewuDeclarePush queryByIdWithDetail(Long id);

    void dewuConfirmOrder(Long id) throws Exception;

    void dewuConfirmOrder(DewuDeclarePush dewuDeclarePush) throws Exception;

    void dewuConfirmDeclareStart(Long id) throws Exception;

    void dewuConfirmDeclareStart(DewuDeclarePush dewuDeclarePush) throws Exception;

    void dewuDeclare(Long id) throws Exception;

    void dewuDeclare(DewuDeclarePush dewuDeclarePush) throws Exception;

    void dewuRefreshDeclare(Long id) throws Exception;

    void dewuRefreshDeclare(DewuDeclarePush dewuDeclarePush) throws Exception;

    void dewuConfirmDeclareSucc(Long id);

    void dewuConfirmDeclareSucc(DewuDeclarePush dewuDeclarePush);

    void dewuConfirmDelDeclareSucc(Long id);

    void dewuConfirmDelDeclareSucc(DewuDeclarePush dewuDeclarePush);

    void cancelOrder(String orderNo);

    void refreshDeclareStatus();
}