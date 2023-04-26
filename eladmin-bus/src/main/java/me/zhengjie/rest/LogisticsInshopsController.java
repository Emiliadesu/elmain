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
import me.zhengjie.domain.LogisticsInshops;
import me.zhengjie.service.LogisticsInshopsService;
import me.zhengjie.service.dto.LogisticsInshopsQueryCriteria;
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
* @author leningzhou
* @date 2021-12-03
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "商家快递关联管理")
@RequestMapping("/api/logisticsInshops")
public class LogisticsInshopsController {

    private final LogisticsInshopsService logisticsInshopsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('logisticsInshops:list')")
    public void download(HttpServletResponse response, LogisticsInshopsQueryCriteria criteria) throws IOException {
        logisticsInshopsService.download(logisticsInshopsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询商家快递关联")
    @ApiOperation("查询商家快递关联")
    @PreAuthorize("@el.check('logisticsInshops:list')")
    public ResponseEntity<Object> query(LogisticsInshopsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(logisticsInshopsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/queryCodes")
    @Log("查询商家快递关联")
    @ApiOperation("查询商家快递关联")
//    @PreAuthorize("@el.check('logisticsInshops:list')")
    public ResponseEntity<Object> queryCodes(LogisticsInshopsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(logisticsInshopsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增商家快递关联")
    @ApiOperation("新增商家快递关联")
    @PreAuthorize("@el.check('logisticsInshops:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LogisticsInshops resources){
        return new ResponseEntity<>(logisticsInshopsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改商家快递关联")
    @ApiOperation("修改商家快递关联")
    @PreAuthorize("@el.check('logisticsInshops:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LogisticsInshops resources){
        logisticsInshopsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除商家快递关联")
    @ApiOperation("删除商家快递关联")
    @PreAuthorize("@el.check('logisticsInshops:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        logisticsInshopsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}