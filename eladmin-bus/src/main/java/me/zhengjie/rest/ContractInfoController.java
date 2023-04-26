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
import me.zhengjie.domain.ContractInfo;
import me.zhengjie.service.ContractInfoService;
import me.zhengjie.service.dto.ContractInfoQueryCriteria;
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
* @date 2022-03-02
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "合同管理信息管理")
@RequestMapping("/api/contractInfo")
public class ContractInfoController {

    private final ContractInfoService contractInfoService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('contractInfo:list')")
    public void download(HttpServletResponse response, ContractInfoQueryCriteria criteria) throws IOException {
        contractInfoService.download(contractInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询合同管理信息")
    @ApiOperation("查询合同管理信息")
    @PreAuthorize("@el.check('contractInfo:list')")
    public ResponseEntity<Object> query(ContractInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(contractInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增合同管理信息")
    @ApiOperation("新增合同管理信息")
    @PreAuthorize("@el.check('contractInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ContractInfo resources){
        return new ResponseEntity<>(contractInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改合同管理信息")
    @ApiOperation("修改合同管理信息")
    @PreAuthorize("@el.check('contractInfo:add')")
    public ResponseEntity<Object> update(@Validated @RequestBody ContractInfo resources){
        contractInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除合同管理信息")
    @ApiOperation("删除合同管理信息")
    @PreAuthorize("@el.check('contractInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        contractInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}