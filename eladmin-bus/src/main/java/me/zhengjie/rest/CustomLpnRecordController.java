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
import me.zhengjie.domain.CustomLpnRecord;
import me.zhengjie.service.CustomLpnRecordService;
import me.zhengjie.service.dto.CustomLpnRecordQueryCriteria;
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
* @date 2021-06-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "自定义托盘号管理")
@RequestMapping("/api/customLpnRecord")
public class CustomLpnRecordController {

    private final CustomLpnRecordService customLpnRecordService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customLpnRecord:list')")
    public void download(HttpServletResponse response, CustomLpnRecordQueryCriteria criteria) throws IOException {
        customLpnRecordService.download(customLpnRecordService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询自定义托盘号")
    @ApiOperation("查询自定义托盘号")
    @PreAuthorize("@el.check('customLpnRecord:list')")
    public ResponseEntity<Object> query(CustomLpnRecordQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customLpnRecordService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增自定义托盘号")
    @ApiOperation("新增自定义托盘号")
    @PreAuthorize("@el.check('customLpnRecord:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomLpnRecord resources){
        return new ResponseEntity<>(customLpnRecordService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改自定义托盘号")
    @ApiOperation("修改自定义托盘号")
    @PreAuthorize("@el.check('customLpnRecord:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomLpnRecord resources){
        customLpnRecordService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除自定义托盘号")
    @ApiOperation("删除自定义托盘号")
    @PreAuthorize("@el.check('customLpnRecord:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customLpnRecordService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}