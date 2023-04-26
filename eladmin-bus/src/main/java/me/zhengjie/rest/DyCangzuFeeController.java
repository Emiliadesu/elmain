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
import me.zhengjie.domain.DyCangzuFee;
import me.zhengjie.service.DyCangzuFeeService;
import me.zhengjie.service.dto.DyCangzuFeeQueryCriteria;
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
* @date 2023-02-07
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "抖音仓租计费通知单管理")
@RequestMapping("/api/dyCangzuFee")
public class DyCangzuFeeController {

    private final DyCangzuFeeService dyCangzuFeeService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('dyCangzuFee:list')")
    public void download(HttpServletResponse response, DyCangzuFeeQueryCriteria criteria) throws IOException {
        dyCangzuFeeService.download(dyCangzuFeeService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询抖音仓租计费通知单")
    @ApiOperation("查询抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:list')")
    public ResponseEntity<Object> query(DyCangzuFeeQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(dyCangzuFeeService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增抖音仓租计费通知单")
    @ApiOperation("新增抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DyCangzuFee resources){
        return new ResponseEntity<>(dyCangzuFeeService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改抖音仓租计费通知单")
    @ApiOperation("修改抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DyCangzuFee resources){
        dyCangzuFeeService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除抖音仓租计费通知单")
    @ApiOperation("删除抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        dyCangzuFeeService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/pushFeeOrder")
    @Log("推送抖音仓租计费通知单")
    @ApiOperation("推送抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:push')")
    public ResponseEntity<Object> pushFeeOrder(Long id){
        dyCangzuFeeService.pushFeeOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/pushFeeOrderByIds")
    @Log("推送抖音仓租计费通知单")
    @ApiOperation("推送抖音仓租计费通知单")
    @PreAuthorize("@el.check('dyCangzuFee:pushByIds')")
    public ResponseEntity<Object> pushFeeOrder(@RequestBody Long[] ids){
        dyCangzuFeeService.pushFeeOrderByIds(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}