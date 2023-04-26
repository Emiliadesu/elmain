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
import me.zhengjie.domain.ReturnGoods;
import me.zhengjie.service.ReturnGoodsService;
import me.zhengjie.service.dto.ReturnGoodsQueryCriteria;
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
@Api(tags = "bus_return_goods管理")
@RequestMapping("/api/returnGoods")
public class ReturnGoodsController {

    private final ReturnGoodsService returnGoodsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('returnGoods:list')")
    public void download(HttpServletResponse response, ReturnGoodsQueryCriteria criteria) throws IOException {
        returnGoodsService.download(returnGoodsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询bus_return_goods")
    @ApiOperation("查询bus_return_goods")
    @PreAuthorize("@el.check('returnGoods:list')")
    public ResponseEntity<Object> query(ReturnGoodsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(returnGoodsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增bus_return_goods")
    @ApiOperation("新增bus_return_goods")
    @PreAuthorize("@el.check('returnGoods:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ReturnGoods resources){
        System.out.println("s");
        return new ResponseEntity<>(returnGoodsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改bus_return_goods")
    @ApiOperation("修改bus_return_goods")
    @PreAuthorize("@el.check('returnGoods:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ReturnGoods resources){
        returnGoodsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除bus_return_goods")
    @ApiOperation("删除bus_return_goods")
    @PreAuthorize("@el.check('returnGoods:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        returnGoodsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
