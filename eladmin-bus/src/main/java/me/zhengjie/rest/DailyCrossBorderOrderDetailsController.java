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
import me.zhengjie.domain.DailyCrossBorderOrderDetails;
import me.zhengjie.service.DailyCrossBorderOrderDetailsService;
import me.zhengjie.service.dto.DailyCrossBorderOrderDetailsQueryCriteria;
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
* @date 2022-07-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "dailyCrossBorderOrderDetails管理")
@RequestMapping("/api/dailyCrossBorderOrderDetails")
public class DailyCrossBorderOrderDetailsController {

    private final DailyCrossBorderOrderDetailsService dailyCrossBorderOrderDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dailyCrossBorderOrderDetails:list')")
    public void download(HttpServletResponse response, DailyCrossBorderOrderDetailsQueryCriteria criteria) throws IOException {
        dailyCrossBorderOrderDetailsService.download(dailyCrossBorderOrderDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询dailyCrossBorderOrderDetails")
    @ApiOperation("查询dailyCrossBorderOrderDetails")
    @PreAuthorize("@el.check('dailyCrossBorderOrderDetails:list')")
    public ResponseEntity<Object> query(DailyCrossBorderOrderDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dailyCrossBorderOrderDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dailyCrossBorderOrderDetails")
    @ApiOperation("新增dailyCrossBorderOrderDetails")
    @PreAuthorize("@el.check('dailyCrossBorderOrderDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DailyCrossBorderOrderDetails resources){
        return new ResponseEntity<>(dailyCrossBorderOrderDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dailyCrossBorderOrderDetails")
    @ApiOperation("修改dailyCrossBorderOrderDetails")
    @PreAuthorize("@el.check('dailyCrossBorderOrderDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DailyCrossBorderOrderDetails resources){
        dailyCrossBorderOrderDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dailyCrossBorderOrderDetails")
    @ApiOperation("删除dailyCrossBorderOrderDetails")
    @PreAuthorize("@el.check('dailyCrossBorderOrderDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dailyCrossBorderOrderDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}