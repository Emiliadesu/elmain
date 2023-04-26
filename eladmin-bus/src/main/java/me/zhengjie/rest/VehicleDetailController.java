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
import me.zhengjie.domain.VehicleDetail;
import me.zhengjie.service.VehicleDetailService;
import me.zhengjie.service.dto.VehicleDetailQueryCriteria;
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
* @date 2021-04-01
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "出库车辆信息明细表管理")
@RequestMapping("/api/vehicleDetail")
public class VehicleDetailController {

    private final VehicleDetailService vehicleDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('vehicleDetail:list')")
    public void download(HttpServletResponse response, VehicleDetailQueryCriteria criteria) throws IOException {
        vehicleDetailService.download(vehicleDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询出库车辆信息明细表")
    @ApiOperation("查询出库车辆信息明细表")
    @PreAuthorize("@el.check('vehicleDetail:list')")
    public ResponseEntity<Object> query(VehicleDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(vehicleDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增出库车辆信息明细表")
    @ApiOperation("新增出库车辆信息明细表")
    @PreAuthorize("@el.check('vehicleDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody VehicleDetail resources){
        return new ResponseEntity<>(vehicleDetailService.create(resources),HttpStatus.CREATED);
    }
    @PostMapping("/add-veh-detail")
    @Log("新增出库车辆信息明细表(相同车牌自动增加车次)")
    @ApiOperation("新增出库车辆信息明细表")
    @PreAuthorize("@el.check('vehicleDetail:add')")
    public ResponseEntity<Object> addVehDetail(@Validated @RequestBody VehicleDetail resources){
        return new ResponseEntity<>(vehicleDetailService.addDetail(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改出库车辆信息明细表")
    @ApiOperation("修改出库车辆信息明细表")
    @PreAuthorize("@el.check('vehicleDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody VehicleDetail resources){
        vehicleDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除出库车辆信息明细表")
    @ApiOperation("删除出库车辆信息明细表")
    @PreAuthorize("@el.check('vehicleDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        vehicleDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
