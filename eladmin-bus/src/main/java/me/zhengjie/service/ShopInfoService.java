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

import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.service.dto.ShopInfoQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author 王淼
* @date 2020-10-20
**/
public interface ShopInfoService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(ShopInfoQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<ShopInfoDto>
    */
    List<ShopInfoDto> queryAll(ShopInfoQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return ShopInfoDto
     */
    ShopInfoDto findByIdDto(Long id);

    ShopInfo findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return ShopInfoDto
    */
    ShopInfoDto create(ShopInfo resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(ShopInfo resources);

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
    void download(List<ShopInfoDto> all, HttpServletResponse response) throws IOException;

    void uploadInsert(List<Map<String, Object>> list);

    List<ShopInfo> queryByPlafCode(String code);

    ShopInfo queryByShopCode(String code);

    ShopInfo queryByName(String shopName);

    ShopInfoDto queryById(Long shopId);

    List<ShopInfo> queryByPlafCodeByWithCusId(String dy, List<Long> shopIds);

    List<ShopInfo> queryByCusId(Long id);

    List<ShopInfo> queryZhuoZhi();

    List<ShopInfo> queryAllZZShopInfo();

    List<ShopInfo> queryCurrentUserShop(Long customerId);

    List<ShopInfo> queryByPlafCodeByWithCusIdAll(List<Long> shopIds,String platformCode);

    List<ShopInfo> queryByCusIdAll(List<Long> shopIds);

    ShopInfo queryByServiceId(Long serviceId);

    ShopInfo queryByKjgCode(String kjgCode);
}
