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

import me.zhengjie.domain.BaseSku;
import me.zhengjie.service.dto.BaseSkuDto;
import me.zhengjie.service.dto.BaseSkuQueryCriteria;
import me.zhengjie.service.dto.StockDto;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务接口
* @author luob
* @date 2021-04-15
**/
public interface BaseSkuService {

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(BaseSkuQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<BaseSkuDto>
    */
    List<BaseSkuDto> queryAll(BaseSkuQueryCriteria criteria);

    List<StockDto> queryAllStock(BaseSkuQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return BaseSkuDto
     */
    BaseSkuDto findById(Long id);

    /**
    * 创建
    * @param resources /
    * @return BaseSkuDto
    */
    BaseSkuDto create(BaseSku resources);

    /**
    * 编辑
    * @param resources /
    */
    void update(BaseSku resources);

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
    void download(List<BaseSkuDto> all, HttpServletResponse response) throws IOException;

    void downloads(List<BaseSkuDto> all, HttpServletResponse response) throws IOException;

    void downloadStock(List<StockDto> all, HttpServletResponse response) throws IOException;

    BaseSku queryByGoodsNo(String goodsNo);

    List<BaseSku> queryByShopId(Long shopId);

    /**
     * 上传货品信息
     * @param list
     */
    void uploadSku(List<Map<String, Object>> list);

    BaseSku queryByCode(String goodsCode);

    BaseSku queryByBarcode(String barCode);

    BaseSku queryByGoodsNoAndShop(String goodsNo, Long shopId);

    BaseSku getProductIdByBarcode(BaseSku baseSku,String shopCode);

    BaseSku queryByOutGoodsNo(String outGoodsNo);

    Map<String,Object> querySumStock(BaseSkuQueryCriteria criteria, Pageable pageable);

    List<StockDto> queryDetailStock(String goodsNo);

    void pushWms(Long id) throws Exception;

    List<BaseSku> queryByCustomerId(long customerId);

    BaseSku addSku(String decData, Long customersId);

    BaseSku queryByBarCodeAndShopId(String barCode, Long shopId);

    void uploadSize(List<Map<String, Object>> maps);

    void auditPass(Long valueOf) throws Exception;

    BaseSku queryById(Long id);

    List<BaseSku> queryListByBarcode(String barCode);

    List<StockDto> queryDetailStockByLocation(String location);

    void uploadGoodsNo(List<Map<String, Object>> maps);

    void uploadCNGoodsNo(List<Map<String, Object>> maps);

    void uploadGift(List<Map<String, Object>> maps);

    Map<String,List<BaseSku>> querySkuAndGiftSku(Long shopId);

    BaseSku queryByGoodsName(String goodsName);

    List<BaseSku> queryByTypeAndBarCodeLike(String type, String barCode, Long shopId);

    void uploadDYSku(List<Map<String, Object>> maps);

    BaseSku queryByGoodsCodeAndShopId(String goodsCode, Long id);

    void uploadRYCSku(List<Map<String, Object>> maps);

    void banOrUnbanSku(String ids, int opt);

    BaseSku queryByOutGoodsNoAndWarehouseCode(String removeEscape, String warehouseNo);

    BaseSku queryByGoodsNoAndWarehouseCode(String goodsNo, String warehouseNo);

    BaseSku queryByGoodsCodeAndWarehouseCode(String itemNo, String scspWarehouseCode);

    void syncSize(Long id);

    void updateSkuSize();

    List<String> queryByShopIdOnlyGoodsNo(Long shopId);
}
