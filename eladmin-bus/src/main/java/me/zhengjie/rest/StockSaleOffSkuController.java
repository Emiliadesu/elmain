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
package me.zhengjie.rest;

import me.zhengjie.annotation.Log;
import me.zhengjie.domain.StockSaleOffSku;
import me.zhengjie.service.StockSaleOffSkuService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.BaseSkuQueryCriteria;
import me.zhengjie.service.dto.StockSaleOffSkuQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author leningzhou
* @date 2022-12-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "库存禁售详情管理")
@RequestMapping("/api/stockSaleOffSku")
public class StockSaleOffSkuController {

    private final StockSaleOffSkuService stockSaleOffSkuService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('stockSaleOffSku:list')")
    public void download(HttpServletResponse response, StockSaleOffSkuQueryCriteria criteria) throws IOException {
        stockSaleOffSkuService.download(stockSaleOffSkuService.queryAll(criteria), response);
    }

    @Log("导出详情数据")
    @ApiOperation("导出详情数据")
    @GetMapping(value = "/downloads")
    @PreAuthorize("@el.check('stockSaleOffSku:lists')")
    public void downloads(HttpServletResponse response, StockSaleOffSkuQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        stockSaleOffSkuService.downloads(stockSaleOffSkuService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询库存禁售详情")
    @ApiOperation("查询库存禁售详情")
    @PreAuthorize("@el.check('stockSaleOffSku:list')")
    public ResponseEntity<Object> query(StockSaleOffSkuQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(stockSaleOffSkuService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增库存禁售详情")
    @ApiOperation("新增库存禁售详情")
    @PreAuthorize("@el.check('stockSaleOffSku:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody StockSaleOffSku resources){
        return new ResponseEntity<>(stockSaleOffSkuService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改库存禁售详情")
    @ApiOperation("修改库存禁售详情")
    @PreAuthorize("@el.check('stockSaleOffSku:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody StockSaleOffSku resources){
        stockSaleOffSkuService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除库存禁售详情")
    @ApiOperation("删除库存禁售详情")
    @PreAuthorize("@el.check('stockSaleOffSku:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        stockSaleOffSkuService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}