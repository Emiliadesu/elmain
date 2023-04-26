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
import me.zhengjie.domain.DyOrderPush;
import me.zhengjie.service.DyOrderPushService;
import me.zhengjie.service.dto.DyOrderPushQueryCriteria;
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
* @date 2021-09-30
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音推单管理")
@RequestMapping("/api/dyOrderPush")
public class DyOrderPushController {

    private final DyOrderPushService dyOrderPushService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyOrderPush:list')")
    public void download(HttpServletResponse response, DyOrderPushQueryCriteria criteria) throws IOException {
        dyOrderPushService.download(dyOrderPushService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音推单")
    @ApiOperation("查询抖音推单")
    @PreAuthorize("@el.check('dyOrderPush:list')")
    public ResponseEntity<Object> query(DyOrderPushQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyOrderPushService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音推单")
    @ApiOperation("新增抖音推单")
    @PreAuthorize("@el.check('dyOrderPush:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyOrderPush resources){
        return new ResponseEntity<>(dyOrderPushService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音推单")
    @ApiOperation("修改抖音推单")
    @PreAuthorize("@el.check('dyOrderPush:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyOrderPush resources){
        dyOrderPushService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音推单")
    @ApiOperation("删除抖音推单")
    @PreAuthorize("@el.check('dyOrderPush:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyOrderPushService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}