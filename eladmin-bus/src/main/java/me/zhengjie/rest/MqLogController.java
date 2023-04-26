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
import me.zhengjie.domain.MqLog;
import me.zhengjie.service.MqLogService;
import me.zhengjie.service.dto.MqLogQueryCriteria;
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
* @date 2021-03-27
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "mqlog管理")
@RequestMapping("/api/MqLog")
public class MqLogController {

    private final MqLogService MqLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('MqLog:list')")
    public void download(HttpServletResponse response, MqLogQueryCriteria criteria) throws IOException {
        MqLogService.download(MqLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询mqlog")
    @ApiOperation("查询mqlog")
    @PreAuthorize("@el.check('MqLog:list')")
    public ResponseEntity<Object> query(MqLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(MqLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增mqlog")
    @ApiOperation("新增mqlog")
    @PreAuthorize("@el.check('MqLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody MqLog resources){
        return new ResponseEntity<>(MqLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改mqlog")
    @ApiOperation("修改mqlog")
    @PreAuthorize("@el.check('MqLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody MqLog resources){
        MqLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除mqlog")
    @ApiOperation("删除mqlog")
    @PreAuthorize("@el.check('MqLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        MqLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("重推消息")
    @ApiOperation("重推消息")
    @PreAuthorize("@el.check('MqLog:repush')")
    @GetMapping("rePush")
    public ResponseEntity<Object> rePush(String ids) {
        MqLogService.rePush(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}