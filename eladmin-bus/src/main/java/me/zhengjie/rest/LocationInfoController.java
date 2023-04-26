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
import me.zhengjie.domain.LocationInfo;
import me.zhengjie.service.LocationInfoService;
import me.zhengjie.service.dto.LocationInfoQueryCriteria;
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
* @author 王淼
* @date 2020-10-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "locationInfo管理")
@RequestMapping("/api/locationInfo")
public class LocationInfoController {

    private final LocationInfoService locationInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('locationInfo:list')")
    public void download(HttpServletResponse response, LocationInfoQueryCriteria criteria) throws IOException {
        locationInfoService.download(locationInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询locationInfo")
    @ApiOperation("查询locationInfo")
    @PreAuthorize("@el.check('locationInfo:list')")
    public ResponseEntity<Object> query(LocationInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(locationInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增locationInfo")
    @ApiOperation("新增locationInfo")
    @PreAuthorize("@el.check('locationInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody LocationInfo resources){
        return new ResponseEntity<>(locationInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改locationInfo")
    @ApiOperation("修改locationInfo")
    @PreAuthorize("@el.check('locationInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody LocationInfo resources){
        locationInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除locationInfo")
    @ApiOperation("删除locationInfo")
    @PreAuthorize("@el.check('locationInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        locationInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}