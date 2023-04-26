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
import me.zhengjie.domain.DewuDeclarePushItem;
import me.zhengjie.service.DewuDeclarePushItemService;
import me.zhengjie.service.dto.DewuDeclarePushItemQueryCriteria;
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
* @date 2023-03-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "得物申报订单详情管理")
@RequestMapping("/api/dewuDeclarePushItem")
public class DewuDeclarePushItemController {

    private final DewuDeclarePushItemService dewuDeclarePushItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dewuDeclarePushItem:list')")
    public void download(HttpServletResponse response, DewuDeclarePushItemQueryCriteria criteria) throws IOException {
        dewuDeclarePushItemService.download(dewuDeclarePushItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询得物申报订单详情")
    @ApiOperation("查询得物申报订单详情")
    @PreAuthorize("@el.check('dewuDeclarePushItem:list')")
    public ResponseEntity<Object> query(DewuDeclarePushItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dewuDeclarePushItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增得物申报订单详情")
    @ApiOperation("新增得物申报订单详情")
    @PreAuthorize("@el.check('dewuDeclarePushItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DewuDeclarePushItem resources){
        return new ResponseEntity<>(dewuDeclarePushItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改得物申报订单详情")
    @ApiOperation("修改得物申报订单详情")
    @PreAuthorize("@el.check('dewuDeclarePushItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DewuDeclarePushItem resources){
        dewuDeclarePushItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除得物申报订单详情")
    @ApiOperation("删除得物申报订单详情")
    @PreAuthorize("@el.check('dewuDeclarePushItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dewuDeclarePushItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}