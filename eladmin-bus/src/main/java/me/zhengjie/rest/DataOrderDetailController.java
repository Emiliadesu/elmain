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
import me.zhengjie.domain.DataOrderDetail;
import me.zhengjie.service.DataOrderDetailService;
import me.zhengjie.service.dto.DataOrderDetailQueryCriteria;
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
* @author luob
* @date 2022-03-30
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "dataorderdetail管理")
@RequestMapping("/api/dataOrderDetail")
public class DataOrderDetailController {

    private final DataOrderDetailService dataOrderDetailService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dataOrderDetail:list')")
    public void download(HttpServletResponse response, DataOrderDetailQueryCriteria criteria) throws IOException {
        dataOrderDetailService.download(dataOrderDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询dataorderdetail")
    @ApiOperation("查询dataorderdetail")
    @PreAuthorize("@el.check('dataOrderDetail:list')")
    public ResponseEntity<Object> query(DataOrderDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dataOrderDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增dataorderdetail")
    @ApiOperation("新增dataorderdetail")
    @PreAuthorize("@el.check('dataOrderDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DataOrderDetail resources){
        return new ResponseEntity<>(dataOrderDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改dataorderdetail")
    @ApiOperation("修改dataorderdetail")
    @PreAuthorize("@el.check('dataOrderDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DataOrderDetail resources){
        dataOrderDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除dataorderdetail")
    @ApiOperation("删除dataorderdetail")
    @PreAuthorize("@el.check('dataOrderDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dataOrderDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}