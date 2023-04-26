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
import me.zhengjie.domain.BusStockConvertDetail;
import me.zhengjie.service.BusStockConvertDetailService;
import me.zhengjie.service.dto.BusStockConvertDetailQueryCriteria;
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
* @date 2021-04-18
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "stockConvertItem管理")
@RequestMapping("/api/busStockConvertDetail")
public class BusStockConvertDetailController {

    private final BusStockConvertDetailService busStockConvertDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('busStockConvertDetail:list')")
    public void download(HttpServletResponse response, BusStockConvertDetailQueryCriteria criteria) throws IOException {
        busStockConvertDetailService.download(busStockConvertDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询stockConvertItem")
    @ApiOperation("查询stockConvertItem")
    @PreAuthorize("@el.check('busStockConvertDetail:list')")
    public ResponseEntity<Object> query(BusStockConvertDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(busStockConvertDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增stockConvertItem")
    @ApiOperation("新增stockConvertItem")
    @PreAuthorize("@el.check('busStockConvertDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody BusStockConvertDetail resources){
        return new ResponseEntity<>(busStockConvertDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改stockConvertItem")
    @ApiOperation("修改stockConvertItem")
    @PreAuthorize("@el.check('busStockConvertDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody BusStockConvertDetail resources){
        busStockConvertDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除stockConvertItem")
    @ApiOperation("删除stockConvertItem")
    @PreAuthorize("@el.check('busStockConvertDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        busStockConvertDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}