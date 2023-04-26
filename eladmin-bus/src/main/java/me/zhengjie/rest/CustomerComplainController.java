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
import me.zhengjie.domain.CustomerComplain;
import me.zhengjie.service.CustomerComplainService;
import me.zhengjie.service.dto.CustomerComplainQueryCriteria;
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
@Api(tags = "bus_customer_complain管理")
@RequestMapping("/api/customerComplain")
public class CustomerComplainController {

    private final CustomerComplainService customerComplainService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('customerComplain:list')")
    public void download(HttpServletResponse response, CustomerComplainQueryCriteria criteria) throws IOException {
        customerComplainService.download(customerComplainService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询bus_customer_complain")
    @ApiOperation("查询bus_customer_complain")
    @PreAuthorize("@el.check('customerComplain:list')")
    public ResponseEntity<Object> query(CustomerComplainQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(customerComplainService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增bus_customer_complain")
    @ApiOperation("新增bus_customer_complain")
    @PreAuthorize("@el.check('customerComplain:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CustomerComplain resources){
        return new ResponseEntity<>(customerComplainService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改bus_customer_complain")
    @ApiOperation("修改bus_customer_complain")
    @PreAuthorize("@el.check('customerComplain:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CustomerComplain resources){
        customerComplainService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除bus_customer_complain")
    @ApiOperation("删除bus_customer_complain")
    @PreAuthorize("@el.check('customerComplain:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        customerComplainService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-cust-companly-by-id")
    @Log("根据id查询客诉表和明细")
    @ApiOperation("根据id查询客诉表和明细")
    @PreAuthorize("@el.check('customerComplain:list')")
    public ResponseEntity<Object> getCustComplanyById(Long id){
        return new ResponseEntity<>(customerComplainService.getCustComplainByIdDto(id),HttpStatus.OK);
    }
}
