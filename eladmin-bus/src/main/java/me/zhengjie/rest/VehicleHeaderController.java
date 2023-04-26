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
import me.zhengjie.domain.VehicleHeader;
import me.zhengjie.service.VehicleHeaderService;
import me.zhengjie.service.dto.VehicleHeaderQueryCriteria;
import me.zhengjie.utils.StringUtil;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-04-01
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "车辆信息表头管理")
@RequestMapping("/api/vehicleHeader")
public class VehicleHeaderController {

    private final VehicleHeaderService vehicleHeaderService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('vehicleHeader:list')")
    public void download(HttpServletResponse response, VehicleHeaderQueryCriteria criteria) throws IOException {
        vehicleHeaderService.download(vehicleHeaderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询车辆信息表头")
    @ApiOperation("查询车辆信息表头")
    @PreAuthorize("@el.check('vehicleHeader:list')")
    public ResponseEntity<Object> query(VehicleHeaderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(vehicleHeaderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增车辆信息表头")
    @ApiOperation("新增车辆信息表头")
    @PreAuthorize("@el.check('vehicleHeader:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody VehicleHeader resources){
        return new ResponseEntity<>(vehicleHeaderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改车辆信息表头")
    @ApiOperation("修改车辆信息表头")
    @PreAuthorize("@el.check('vehicleHeader:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody VehicleHeader resources){
        vehicleHeaderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除车辆信息表头")
    @ApiOperation("删除车辆信息表头")
    @PreAuthorize("@el.check('vehicleHeader:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        vehicleHeaderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("根据so单号获取装载单号")
    @ApiOperation("根据so单号获取装载单号")
    @PreAuthorize("@el.check('vehicleHeader:list')")
    @PostMapping("/query-by-sono")
    public ResponseEntity<Object> queryBySoNo(String soNo) {
        return new ResponseEntity<>(vehicleHeaderService.querySoNoLike(soNo),HttpStatus.OK);
    }

    @Log("根据so单号获取装载单号")
    @ApiOperation("根据so单号获取装载单号")
    @PreAuthorize("@el.check('vehicleHeader:list')")
    @PostMapping("/push-veh")
    public ResponseEntity<Object> pushVeh(Long id) {
        String msg=vehicleHeaderService.pushVeh(id);
        Map<String,Object>data=new HashMap<>();
        data.put("msg",msg);
        if (StringUtil.contains("成功",msg)){
            data.put("is_success",true);
        }else {
            data.put("is_success",false);
        }
        return new ResponseEntity<>(data,HttpStatus.OK);
    }
}
