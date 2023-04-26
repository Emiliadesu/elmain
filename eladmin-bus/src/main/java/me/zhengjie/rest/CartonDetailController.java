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
import me.zhengjie.domain.CartonDetail;
import me.zhengjie.service.CartonDetailService;
import me.zhengjie.service.dto.CartonDetailQueryCriteria;
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
* @date 2021-03-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "出库包裹明细管理")
@RequestMapping("/api/cartonDetail")
public class CartonDetailController {

    private final CartonDetailService cartonDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('cartonDetail:list')")
    public void download(HttpServletResponse response, CartonDetailQueryCriteria criteria) throws IOException {
        cartonDetailService.download(cartonDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库包裹明细")
    @ApiOperation("查询出库包裹明细")
    @PreAuthorize("@el.check('cartonDetail:list')")
    public ResponseEntity<Object> query(CartonDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(cartonDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库包裹明细")
    @ApiOperation("新增出库包裹明细")
    @PreAuthorize("@el.check('cartonDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CartonDetail resources){
        return new ResponseEntity<>(cartonDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库包裹明细")
    @ApiOperation("修改出库包裹明细")
    @PreAuthorize("@el.check('cartonDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CartonDetail resources){
        cartonDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库包裹明细")
    @ApiOperation("删除出库包裹明细")
    @PreAuthorize("@el.check('cartonDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        cartonDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}