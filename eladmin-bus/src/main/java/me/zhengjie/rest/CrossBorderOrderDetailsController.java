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
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.service.CrossBorderOrderDetailsService;
import me.zhengjie.service.dto.CrossBorderOrderDetailsQueryCriteria;
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
* @date 2021-03-25
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "crossBorderOrderDetails管理")
@RequestMapping("/api/crossBorderOrderDetails")
public class CrossBorderOrderDetailsController {

    private final CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('crossBorderOrderDetails:list')")
    public void download(HttpServletResponse response, CrossBorderOrderDetailsQueryCriteria criteria) throws IOException {
        crossBorderOrderDetailsService.download(crossBorderOrderDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询crossBorderOrderDetails")
    @ApiOperation("查询crossBorderOrderDetails")
    @PreAuthorize("@el.check('crossBorderOrderDetails:list')")
    public ResponseEntity<Object> query(CrossBorderOrderDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(crossBorderOrderDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增crossBorderOrderDetails")
    @ApiOperation("新增crossBorderOrderDetails")
    @PreAuthorize("@el.check('crossBorderOrderDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CrossBorderOrderDetails resources){
        return new ResponseEntity<>(crossBorderOrderDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改crossBorderOrderDetails")
    @ApiOperation("修改crossBorderOrderDetails")
    @PreAuthorize("@el.check('crossBorderOrderDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CrossBorderOrderDetails resources){
        crossBorderOrderDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除crossBorderOrderDetails")
    @ApiOperation("删除crossBorderOrderDetails")
    @PreAuthorize("@el.check('crossBorderOrderDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        crossBorderOrderDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}