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

import cn.hutool.core.date.DateTime;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.PortToWarehouse;
import me.zhengjie.service.PortToWarehouseService;
import me.zhengjie.service.dto.PortToWarehouseQueryCriteria;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author lh
* @date 2021-01-10
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "bus_port_to_warehouse管理")
@RequestMapping("/api/portToWarehouse")
public class PortToWarehouseController {

    private final PortToWarehouseService portToWarehouseService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('portToWarehouse:list')")
    public void download(HttpServletResponse response, PortToWarehouseQueryCriteria criteria) throws IOException {
        portToWarehouseService.download(portToWarehouseService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询bus_port_to_warehouse")
    @ApiOperation("查询bus_port_to_warehouse")
    @PreAuthorize("@el.check('portToWarehouse:list')")
    public ResponseEntity<Object> query(PortToWarehouseQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(portToWarehouseService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增bus_port_to_warehouse")
    @ApiOperation("新增bus_port_to_warehouse")
    @PreAuthorize("@el.check('portToWarehouse:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PortToWarehouse resources){
        return new ResponseEntity<>(portToWarehouseService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改bus_port_to_warehouse")
    @ApiOperation("修改bus_port_to_warehouse")
    @PreAuthorize("@el.check('portToWarehouse:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PortToWarehouse resources){
        portToWarehouseService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除bus_port_to_warehouse")
    @ApiOperation("删除bus_port_to_warehouse")
    @PreAuthorize("@el.check('portToWarehouse:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        portToWarehouseService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/fileupload")
    @Log("文件上传")
    @ApiOperation("文件上传")
    @PreAuthorize("@el.check('portToWarehouse:fileupload')")
    public ResponseEntity<Object> fileupload(@RequestParam("id") Long id ,@RequestParam("file") MultipartFile file){
        try {
            portToWarehouseService.uploadfile(id,file);
        }catch (Exception e){
            e.getMessage();
            return new ResponseEntity<>("上传失败",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/downloadfile")
    @Log("文件下载")
    @ApiOperation("文件下载")
    @PreAuthorize("@el.check('portToWarehouse:download')")
    public void downloadfile(@RequestParam("id")Long id, HttpServletRequest request, HttpServletResponse response){
        portToWarehouseService.dewnload(id,request,response);
    }

    @RequestMapping("/updateparam")
    @Log("参数修改")
    @ApiOperation("参数修改")
    @PreAuthorize("@el.check('portToWarehouse:edit')")
    public void updateparam(@RequestParam("id")Long id,
                            @RequestParam("name")String name,
                            @RequestParam("time")Timestamp time,
                            @RequestParam("status")String status){
        System.out.println(id+name+time+"我是分隔符------ "+status  );
        portToWarehouseService.updateparam(id,status,time,name);
    }

    @RequestMapping("/updatetimeoutreason")
    @Log("添加备注")
    @ApiOperation("添加备注")
    @PreAuthorize("@el.check('portToWarehouse:edit')")
    public void updatetimeoutreason(@RequestParam("id")Long id,
                            @RequestParam("name")String name,
                            @RequestParam("text")String text){
        System.out.println(id+"   "+name+"     "+text);
        portToWarehouseService.updateReason(id,name,text);
    }

}
