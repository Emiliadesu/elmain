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
import me.zhengjie.domain.ByteDance;
import me.zhengjie.service.ByteDanceService;
import me.zhengjie.service.dto.ByteDanceQueryCriteria;
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
* @date 2021-03-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖店订单管理")
@RequestMapping("/api/byteDance")
public class ByteDanceController {

    private final ByteDanceService byteDanceService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('byteDance:list')")
    public void download(HttpServletResponse response, ByteDanceQueryCriteria criteria) throws IOException {
        byteDanceService.download(byteDanceService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖店订单")
    @ApiOperation("查询抖店订单")
    @PreAuthorize("@el.check('byteDance:list')")
    public ResponseEntity<Object> query(ByteDanceQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(byteDanceService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖店订单")
    @ApiOperation("新增抖店订单")
    @PreAuthorize("@el.check('byteDance:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ByteDance resources){
        return new ResponseEntity<>(byteDanceService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖店订单")
    @ApiOperation("修改抖店订单")
    @PreAuthorize("@el.check('byteDance:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ByteDance resources){
        byteDanceService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖店订单")
    @ApiOperation("删除抖店订单")
    @PreAuthorize("@el.check('byteDance:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        byteDanceService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}