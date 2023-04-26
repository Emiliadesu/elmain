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
import me.zhengjie.domain.DepositLog;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DepositLogService;
import me.zhengjie.service.dto.DepositLogQueryCriteria;
import me.zhengjie.utils.StringUtils;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-11-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "depositLog管理")
@RequestMapping("/api/depositLog")
public class DepositLogController {

    private final DepositLogService depositLogService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('depositLog:list')")
    public void download(HttpServletResponse response, DepositLogQueryCriteria criteria) throws IOException {
        if (criteria.getDepositId() == null)
            return;
        depositLogService.download(depositLogService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询depositLog")
    @ApiOperation("查询depositLog")
    @PreAuthorize("@el.check('depositLog:list')")
    public ResponseEntity<Object> query(DepositLogQueryCriteria criteria, Pageable pageable){
        if (criteria.getDepositId() == null)
            return new ResponseEntity<>(null,HttpStatus.OK);
        return new ResponseEntity<>(depositLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增depositLog")
    @ApiOperation("新增depositLog")
    @PreAuthorize("@el.check('depositLog:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DepositLog resources){
        return new ResponseEntity<>(depositLogService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改depositLog")
    @ApiOperation("修改depositLog")
    @PreAuthorize("@el.check('depositLog:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DepositLog resources){
        depositLogService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除depositLog")
    @ApiOperation("删除depositLog")
    @PreAuthorize("@el.check('depositLog:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        depositLogService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}