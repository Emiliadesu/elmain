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
import me.zhengjie.domain.CustomerKey;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.dto.CustomerKeyQueryCriteria;
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
* @date 2021-04-06
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "客户秘钥信息管理")
@RequestMapping("/api/customerKey")
public class CustomerKeyController {

    private final CustomerKeyService customerKeyService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customerKey:list')")
    public void download(HttpServletResponse response, CustomerKeyQueryCriteria criteria) throws IOException {
        customerKeyService.download(customerKeyService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询客户秘钥信息")
    @ApiOperation("查询客户秘钥信息")
    @PreAuthorize("@el.check('customerKey:list')")
    public ResponseEntity<Object> query(CustomerKeyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customerKeyService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户秘钥信息")
    @ApiOperation("新增客户秘钥信息")
    @PreAuthorize("@el.check('customerKey:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomerKey resources){
        return new ResponseEntity<>(customerKeyService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户秘钥信息")
    @ApiOperation("修改客户秘钥信息")
    @PreAuthorize("@el.check('customerKey:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomerKey resources){
        customerKeyService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除客户秘钥信息")
    @ApiOperation("删除客户秘钥信息")
    @PreAuthorize("@el.check('customerKey:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customerKeyService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}