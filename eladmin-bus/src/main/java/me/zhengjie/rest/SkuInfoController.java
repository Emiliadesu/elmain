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
import me.zhengjie.domain.SkuInfo;
import me.zhengjie.service.SkuInfoService;
import me.zhengjie.service.dto.SkuInfoQueryCriteria;
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
* @author luob
* @date 2021-04-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "skuInfo管理")
@RequestMapping("/api/skuInfo")
public class SkuInfoController {

    private final SkuInfoService skuInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('skuInfo:list')")
    public void download(HttpServletResponse response, SkuInfoQueryCriteria criteria) throws IOException {
        skuInfoService.download(skuInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询skuInfo")
    @ApiOperation("查询skuInfo")
    @PreAuthorize("@el.check('skuInfo:list')")
    public ResponseEntity<Object> query(SkuInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(skuInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增skuInfo")
    @ApiOperation("新增skuInfo")
    @PreAuthorize("@el.check('skuInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SkuInfo resources){
        return new ResponseEntity<>(skuInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改skuInfo")
    @ApiOperation("修改skuInfo")
    @PreAuthorize("@el.check('skuInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SkuInfo resources){
        skuInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除skuInfo")
    @ApiOperation("删除skuInfo")
    @PreAuthorize("@el.check('skuInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        skuInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}