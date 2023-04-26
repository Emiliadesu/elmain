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
import me.zhengjie.domain.CustomerComplainItem;
import me.zhengjie.service.CustomerComplainItemService;
import me.zhengjie.service.dto.CustomerComplainItemQueryCriteria;
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
* @date 2021-12-15
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "custComplainItem管理")
@RequestMapping("/api/customerComplainItem")
public class CustomerComplainItemController {

    private final CustomerComplainItemService customerComplainItemService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customerComplainItem:list')")
    public void download(HttpServletResponse response, CustomerComplainItemQueryCriteria criteria) throws IOException {
        customerComplainItemService.download(customerComplainItemService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询custComplainItem")
    @ApiOperation("查询custComplainItem")
    @PreAuthorize("@el.check('customerComplainItem:list')")
    public ResponseEntity<Object> query(CustomerComplainItemQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customerComplainItemService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增custComplainItem")
    @ApiOperation("新增custComplainItem")
    @PreAuthorize("@el.check('customerComplainItem:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomerComplainItem resources){
        return new ResponseEntity<>(customerComplainItemService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改custComplainItem")
    @ApiOperation("修改custComplainItem")
    @PreAuthorize("@el.check('customerComplainItem:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomerComplainItem resources){
        customerComplainItemService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除custComplainItem")
    @ApiOperation("删除custComplainItem")
    @PreAuthorize("@el.check('customerComplainItem:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customerComplainItemService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}