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
import me.zhengjie.domain.LogisticsInfo;
import me.zhengjie.service.LogisticsInfoService;
import me.zhengjie.service.dto.LogisticsInfoQueryCriteria;
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
* @date 2021-12-02
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "logisticsinfo管理")
@RequestMapping("/api/logisticsInfo")
public class LogisticsInfoController {

    private final LogisticsInfoService logisticsInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('logisticsInfo:list')")
    public void download(HttpServletResponse response, LogisticsInfoQueryCriteria criteria) throws IOException {
        logisticsInfoService.download(logisticsInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询logisticsinfo")
    @ApiOperation("查询logisticsinfo")
    @PreAuthorize("@el.check('logisticsInfo:list')")
    public ResponseEntity<Object> query(LogisticsInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(logisticsInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/queryCodes")
    @Log("查询logisticsinfo")
    @ApiOperation("查询logisticsinfo")
//    @PreAuthorize("@el.check('logisticsInfo:list')")
    public ResponseEntity<Object> queryCodes(LogisticsInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(logisticsInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增logisticsinfo")
    @ApiOperation("新增logisticsinfo")
    @PreAuthorize("@el.check('logisticsInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LogisticsInfo resources){
        return new ResponseEntity<>(logisticsInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改logisticsinfo")
    @ApiOperation("修改logisticsinfo")
    @PreAuthorize("@el.check('logisticsInfo:edit')")
    public ResponseEntity<Object> update(@RequestBody LogisticsInfo resources){
        logisticsInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除logisticsinfo")
    @ApiOperation("删除logisticsinfo")
    @PreAuthorize("@el.check('logisticsInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        logisticsInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}