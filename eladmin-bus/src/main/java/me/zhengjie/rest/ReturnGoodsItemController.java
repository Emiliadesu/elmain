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
import me.zhengjie.domain.ReturnGoodsItem;
import me.zhengjie.service.ReturnGoodsItemService;
import me.zhengjie.service.dto.ReturnGoodsItemQueryCriteria;
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
* @author lh
* @date 2021-01-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "returnGoodsItem管理")
@RequestMapping("/api/returnGoodsItem")
public class ReturnGoodsItemController {

    private final ReturnGoodsItemService returnGoodsItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('returnGoodsItem:list')")
    public void download(HttpServletResponse response, ReturnGoodsItemQueryCriteria criteria) throws IOException {
        returnGoodsItemService.download(returnGoodsItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询returnGoodsItem")
    @ApiOperation("查询returnGoodsItem")
    @PreAuthorize("@el.check('returnGoodsItem:list')")
    public ResponseEntity<Object> query(ReturnGoodsItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(returnGoodsItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增returnGoodsItem")
    @ApiOperation("新增returnGoodsItem")
    @PreAuthorize("@el.check('returnGoodsItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ReturnGoodsItem resources){
        return new ResponseEntity<>(returnGoodsItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改returnGoodsItem")
    @ApiOperation("修改returnGoodsItem")
    @PreAuthorize("@el.check('returnGoodsItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ReturnGoodsItem resources){
        returnGoodsItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除returnGoodsItem")
    @ApiOperation("删除returnGoodsItem")
    @PreAuthorize("@el.check('returnGoodsItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        returnGoodsItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}