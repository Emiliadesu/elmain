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
import me.zhengjie.domain.SkuMap;
import me.zhengjie.service.SkuMapService;
import me.zhengjie.service.dto.SkuMapQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-04-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "商仓sku映射管理")
@RequestMapping("/api/skuMap")
public class SkuMapController {

    private final SkuMapService skuMapService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('skuMap:list')")
    public void download(HttpServletResponse response, SkuMapQueryCriteria criteria) throws IOException {
        skuMapService.download(skuMapService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询商仓sku映射")
    @ApiOperation("查询商仓sku映射")
    @PreAuthorize("@el.check('skuMap:list')")
    public ResponseEntity<Object> query(SkuMapQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(skuMapService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增商仓sku映射")
    @ApiOperation("新增商仓sku映射")
    @PreAuthorize("@el.check('skuMap:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SkuMap resources){
        return new ResponseEntity<>(skuMapService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改商仓sku映射")
    @ApiOperation("修改商仓sku映射")
    @PreAuthorize("@el.check('skuMap:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SkuMap resources){
        skuMapService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除商仓sku映射")
    @ApiOperation("删除商仓sku映射")
    @PreAuthorize("@el.check('skuMap:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        skuMapService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}