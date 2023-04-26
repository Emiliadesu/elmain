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
import me.zhengjie.domain.DoLot;
import me.zhengjie.service.DoLotService;
import me.zhengjie.service.dto.DoLotQueryCriteria;
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
@Api(tags = "出库批次信息管理")
@RequestMapping("/api/doLot")
public class DoLotController {

    private final DoLotService doLotService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('doLot:list')")
    public void download(HttpServletResponse response, DoLotQueryCriteria criteria) throws IOException {
        doLotService.download(doLotService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库批次信息")
    @ApiOperation("查询出库批次信息")
    @PreAuthorize("@el.check('doLot:list')")
    public ResponseEntity<Object> query(DoLotQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(doLotService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库批次信息")
    @ApiOperation("新增出库批次信息")
    @PreAuthorize("@el.check('doLot:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DoLot resources){
        return new ResponseEntity<>(doLotService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库批次信息")
    @ApiOperation("修改出库批次信息")
    @PreAuthorize("@el.check('doLot:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DoLot resources){
        doLotService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库批次信息")
    @ApiOperation("删除出库批次信息")
    @PreAuthorize("@el.check('doLot:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        doLotService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/gen-do-lot")
    @Log("根据富勒理货数据，一键生成理货信息,前置条件是没有任何卓志理货信息")
    @ApiOperation("根据富勒理货数据，一键生成理货信息,前置条件是没有任何卓志理货信息")
    @PreAuthorize("@el.check('doLot:add')")
    public ResponseEntity<Object> genDoLot(String soNo,Long outTallyId,String merchantId,String warehouseId){
        doLotService.genDoLot(soNo,outTallyId,merchantId,warehouseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
