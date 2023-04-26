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
import me.zhengjie.domain.ByteDanceItem;
import me.zhengjie.service.ByteDanceItemService;
import me.zhengjie.service.dto.ByteDanceItemQueryCriteria;
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
@Api(tags = "抖店订单详情管理")
@RequestMapping("/api/byteDanceItem")
public class ByteDanceItemController {

    private final ByteDanceItemService byteDanceItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('byteDanceItem:list')")
    public void download(HttpServletResponse response, ByteDanceItemQueryCriteria criteria) throws IOException {
        byteDanceItemService.download(byteDanceItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖店订单详情")
    @ApiOperation("查询抖店订单详情")
    @PreAuthorize("@el.check('byteDanceItem:list')")
    public ResponseEntity<Object> query(ByteDanceItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(byteDanceItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖店订单详情")
    @ApiOperation("新增抖店订单详情")
    @PreAuthorize("@el.check('byteDanceItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ByteDanceItem resources){
        return new ResponseEntity<>(byteDanceItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖店订单详情")
    @ApiOperation("修改抖店订单详情")
    @PreAuthorize("@el.check('byteDanceItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ByteDanceItem resources){
        byteDanceItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖店订单详情")
    @ApiOperation("删除抖店订单详情")
    @PreAuthorize("@el.check('byteDanceItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        byteDanceItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}