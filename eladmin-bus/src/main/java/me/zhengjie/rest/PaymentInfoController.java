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
import me.zhengjie.domain.PaymentInfo;
import me.zhengjie.service.PaymentInfoService;
import me.zhengjie.service.dto.PaymentInfoQueryCriteria;
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
* @author leningzhou
* @date 2022-04-26
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "支付公司信息管理")
@RequestMapping("/api/paymentInfo")
public class PaymentInfoController {

    private final PaymentInfoService paymentInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('paymentInfo:list')")
    public void download(HttpServletResponse response, PaymentInfoQueryCriteria criteria) throws IOException {
        paymentInfoService.download(paymentInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询支付公司信息")
    @ApiOperation("查询支付公司信息")
    @PreAuthorize("@el.check('paymentInfo:list')")
    public ResponseEntity<Object> query(PaymentInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(paymentInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增支付公司信息")
    @ApiOperation("新增支付公司信息")
    @PreAuthorize("@el.check('paymentInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PaymentInfo resources){
        return new ResponseEntity<>(paymentInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改支付公司信息")
    @ApiOperation("修改支付公司信息")
    @PreAuthorize("@el.check('paymentInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PaymentInfo resources){
        paymentInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除支付公司信息")
    @ApiOperation("删除支付公司信息")
    @PreAuthorize("@el.check('paymentInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        paymentInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}