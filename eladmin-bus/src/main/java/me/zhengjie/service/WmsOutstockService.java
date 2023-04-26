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

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.service.dto.WmsOutstockDto;
import me.zhengjie.service.dto.WmsOutstockQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author 王淼
* @date 2020-12-04
**/
public interface WmsOutstockService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(WmsOutstockQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<WmsOutstockDto>
    */
    List<WmsOutstockDto> queryAll(WmsOutstockQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return WmsOutstockDto
     */
    WmsOutstockDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return WmsOutstockDto
    */
    WmsOutstockDto create(WmsOutstock resources);

    /**
    * 编辑
    * @param resources /
    */
    boolean update(WmsOutstock resources);

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
    void download(List<WmsOutstockDto> all, HttpServletResponse response) throws IOException;

    void outStockPushFlux();

    List<WmsOutstock> queryByStatus(String status);

    boolean addOutBound(String decData, String customersCode);

    WmsOutstock findByOutOrderSn(String outStockSn);

    boolean cancel(String orderSn, String customersCode,String tenantCode, String reason);

    WmsOutstockDto queryByIdDto(Long id);

    WmsOutstock queryById(Long id);

    void pushLpnStock(Long id);

    void pushPreLoad(Long id);

    void syncStatus(Long[] ids);

    List<Map<String,String>> getOrderSns();

    void outStock(long id);

    void getFluxSoNo();

    void getPoNo(JSONObject resp);

    WmsOutstock queryByLoadNo(String loadNo);
}
