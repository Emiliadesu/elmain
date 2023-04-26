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
import me.zhengjie.domain.OrderTaxAccount;
import me.zhengjie.service.OrderTaxAccountService;
import me.zhengjie.service.dto.OrderTaxAccountQueryCriteria;
import me.zhengjie.utils.FileUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-12-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "orderTaxAccount管理")
@RequestMapping("/api/orderTaxAccount")
public class OrderTaxAccountController {

    private final OrderTaxAccountService orderTaxAccountService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('orderTaxAccount:list')")
    public void download(HttpServletResponse response, OrderTaxAccountQueryCriteria criteria) throws IOException {
        orderTaxAccountService.download(orderTaxAccountService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询orderTaxAccount")
    @ApiOperation("查询orderTaxAccount")
    @PreAuthorize("@el.check('orderTaxAccount:list')")
    public ResponseEntity<Object> query(OrderTaxAccountQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderTaxAccountService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增orderTaxAccount")
    @ApiOperation("新增orderTaxAccount")
    @PreAuthorize("@el.check('orderTaxAccount:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OrderTaxAccount resources){
        return new ResponseEntity<>(orderTaxAccountService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改orderTaxAccount")
    @ApiOperation("修改orderTaxAccount")
    @PreAuthorize("@el.check('orderTaxAccount:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OrderTaxAccount resources){
        orderTaxAccountService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除orderTaxAccount")
    @ApiOperation("删除orderTaxAccount")
    @PreAuthorize("@el.check('orderTaxAccount:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        orderTaxAccountService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传")
    @RequestMapping(value = "upload")
    @PreAuthorize("@el.check('orderTaxAccount:upload')")
    @Log("上传")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        if(StringUtils.equals("lp", name)) {
            List<OrderTaxAccount> orderTaxAccounts = FileUtils.importExcel(file, 0, 1, OrderTaxAccount.class);
            orderTaxAccountService.createTaxs(orderTaxAccounts);
        }else if(StringUtils.equals("kj", name)){
            List<OrderTaxAccount> orderTaxAccounts = FileUtils.importExcel(file, 0, 1, OrderTaxAccount.class);
            orderTaxAccountService.updateInvtNo(orderTaxAccounts);
        }else if(StringUtils.equals("zs", name)){
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}