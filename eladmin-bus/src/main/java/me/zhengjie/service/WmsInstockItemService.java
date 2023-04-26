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

import me.zhengjie.domain.WmsInstockItem;
import me.zhengjie.service.dto.WmsInstockItemDto;
import me.zhengjie.service.dto.WmsInstockItemQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author 王淼
* @date 2020-12-08
**/
public interface WmsInstockItemService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(WmsInstockItemQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<WmsInstockItemDto>
    */
    List<WmsInstockItemDto> queryAll(WmsInstockItemQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return WmsInstockItemDto
     */
    WmsInstockItemDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return WmsInstockItemDto
    */
    WmsInstockItemDto create(WmsInstockItem resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(WmsInstockItem resources);

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
    void download(List<WmsInstockItemDto> all, HttpServletResponse response) throws IOException;

    List<WmsInstockItemDto> createBatch(List<WmsInstockItem> itemList);

    List<WmsInstockItem> queryAllByInId(Long inId);
}
