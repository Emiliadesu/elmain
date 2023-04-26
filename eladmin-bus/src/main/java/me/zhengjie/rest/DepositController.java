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
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.Deposit;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DepositService;
import me.zhengjie.service.dto.DepositQueryCriteria;
import me.zhengjie.utils.StringUtils;
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
@Api(tags = "deposit管理")
@RequestMapping("/api/deposit")
public class DepositController {

    private final DepositService depositService;

    @RequestMapping(value = "/change")
    @Log("金额变动")
    @ApiOperation("金额变动")
    @PreAuthorize("@el.check('deposit:change')")
    public ResponseEntity<Object> change(@RequestParam(value = "depositId") Long depositId,
                                         @RequestParam(value = "type") String type,
                                         @RequestParam(value = "changeAmount") BigDecimal changeAmount){
        if (StringUtils.equals("2", type))
            throw new BadRequestException("订单扣减不允许手动变动");
        depositService.change(depositId, type, changeAmount, null);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('deposit:list')")
    public void download(HttpServletResponse response, DepositQueryCriteria criteria) throws IOException {
        depositService.download(depositService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询deposit")
    @ApiOperation("查询deposit")
    @PreAuthorize("@el.check('deposit:list')")
    public ResponseEntity<Object> query(DepositQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(depositService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增deposit")
    @ApiOperation("新增deposit")
    @PreAuthorize("@el.check('deposit:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody Deposit resources){
        return new ResponseEntity<>(depositService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改deposit")
    @ApiOperation("修改deposit")
    @PreAuthorize("@el.check('deposit:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody Deposit resources){
        depositService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除deposit")
    @ApiOperation("删除deposit")
    @PreAuthorize("@el.check('deposit:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        depositService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}