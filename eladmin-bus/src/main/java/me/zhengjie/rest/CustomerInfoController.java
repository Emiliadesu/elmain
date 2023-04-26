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
import me.zhengjie.domain.CustomerInfo;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.dto.CustomerInfoQueryCriteria;
import me.zhengjie.utils.SecurityUtils;
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
* @date 2021-02-27
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "客户基本信息管理")
@RequestMapping("/api/customerInfo")
public class CustomerInfoController {

    private final CustomerInfoService customerInfoService;

    @ApiOperation("返回当前登录用户店铺数据")
    @GetMapping(value = "/query-user-cus-shop")
    @PreAuthorize("@el.check('customerInfo:list')")
    public ResponseEntity<Object> queryUserCusShop(){
        return new ResponseEntity<>(customerInfoService.queryUserCusShop(),HttpStatus.OK);
    }

    @ApiOperation("返回客户店铺数据")
    @GetMapping(value = "/customer-shop")
    @PreAuthorize("@el.check('customerInfo:list')")
    public ResponseEntity<Object> queryCustomerShop(Long customerId){
        return new ResponseEntity<>(customerInfoService.queryCustomerShop(customerId),HttpStatus.OK);
    }

    @ApiOperation("返回当前登录用户的客户")
    @GetMapping(value = "/query-current-user-customer")
    public ResponseEntity<Object> queryCurrentUserCustomer(){
        return new ResponseEntity<>(customerInfoService.queryCurrentUserCustomer(),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customerInfo:list')")
    public void download(HttpServletResponse response, CustomerInfoQueryCriteria criteria) throws IOException {
        customerInfoService.download(customerInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询客户基本信息")
    @ApiOperation("查询客户基本信息")
    @PreAuthorize("@el.check('customerInfo:list')")
    public ResponseEntity<Object> query(CustomerInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customerInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户基本信息")
    @ApiOperation("新增客户基本信息")
    @PreAuthorize("@el.check('customerInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomerInfo resources){
        return new ResponseEntity<>(customerInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户基本信息")
    @ApiOperation("修改客户基本信息")
    @PreAuthorize("@el.check('customerInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomerInfo resources){
        customerInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除客户基本信息")
    @ApiOperation("删除客户基本信息")
    @PreAuthorize("@el.check('customerInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customerInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
