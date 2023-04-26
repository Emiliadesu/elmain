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

import me.zhengjie.domain.GiftInfo;
import me.zhengjie.service.dto.GiftInfoDto;
import me.zhengjie.service.dto.GiftInfoQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author leningzhou
* @date 2021-12-27
**/
public interface GiftInfoService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(GiftInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<GiftInfoDto>
    */
    List<GiftInfoDto> queryAll(GiftInfoQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return GiftInfoDto
     */
    GiftInfoDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return GiftInfoDto
    */
    GiftInfoDto create(GiftInfo resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(GiftInfo resources);

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
    void download(List<GiftInfoDto> all, HttpServletResponse response) throws IOException;

    Object queryByIdWithDetails(Long id);

    List<GiftInfo> queryByShopIdAndSkuId(Long shopId, Long goodsId);

    List<GiftInfo> queryByShopId(Long shopId);

    List<GiftInfo>  queryByShopIdAndGiftIdWithOrder(Long shopId, Long giftId);

    List<GiftInfo> queryByShopIdAndGiftIdWithSKU(Long shopId, Long giftId);

    GiftInfo queryByShopIdAndGiftIdAndSkuIdWithSKU(Long shopId, Long giftId, Long skuId);
}