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
import me.zhengjie.domain.WmsInstockItem;
import me.zhengjie.service.WmsInstockItemService;
import me.zhengjie.service.dto.WmsInstockItemQueryCriteria;
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
* @date 2020-12-08
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "wmsInstockItem管理")
@RequestMapping("/api/wmsInstockItem")
public class WmsInstockItemController {

    private final WmsInstockItemService wmsInstockItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('wmsInstockItem:list')")
    public void download(HttpServletResponse response, WmsInstockItemQueryCriteria criteria) throws IOException {
        wmsInstockItemService.download(wmsInstockItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询wmsInstockItem")
    @ApiOperation("查询wmsInstockItem")
    @PreAuthorize("@el.check('wmsInstockItem:list')")
    public ResponseEntity<Object> query(WmsInstockItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(wmsInstockItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增wmsInstockItem")
    @ApiOperation("新增wmsInstockItem")
    @PreAuthorize("@el.check('wmsInstockItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WmsInstockItem resources){
        return new ResponseEntity<>(wmsInstockItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改wmsInstockItem")
    @ApiOperation("修改wmsInstockItem")
    @PreAuthorize("@el.check('wmsInstockItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WmsInstockItem resources){
        wmsInstockItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除wmsInstockItem")
    @ApiOperation("删除wmsInstockItem")
    @PreAuthorize("@el.check('wmsInstockItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        wmsInstockItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}