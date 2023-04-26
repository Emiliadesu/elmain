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
import me.zhengjie.domain.OutofPlanDetail;
import me.zhengjie.service.OutofPlanDetailService;
import me.zhengjie.service.dto.OutofPlanDetailQueryCriteria;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-03-23
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "入库理货超收信息管理")
@RequestMapping("/api/outofPlanDetail")
public class OutofPlanDetailController {

    private final OutofPlanDetailService outofPlanDetailService;
    @Value("${serverUrl}")
    private String url="http://120.55.9.185";

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('outofPlanDetail:list')")
    public void download(HttpServletResponse response, OutofPlanDetailQueryCriteria criteria) throws IOException {
        outofPlanDetailService.download(outofPlanDetailService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询入库理货超收信息")
    @ApiOperation("查询入库理货超收信息")
    @PreAuthorize("@el.check('outofPlanDetail:list')")
    public ResponseEntity<Object> query(OutofPlanDetailQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(outofPlanDetailService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增入库理货超收信息")
    @ApiOperation("新增入库理货超收信息")
    @PreAuthorize("@el.check('outofPlanDetail:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody OutofPlanDetail resources, HttpServletRequest request){
        if (StringUtil.isEmpty(resources.getPicUrl())){
            resources.setPicUrl("-");
        }
        return new ResponseEntity<>(outofPlanDetailService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改入库理货超收信息")
    @ApiOperation("修改入库理货超收信息")
    @PreAuthorize("@el.check('outofPlanDetail:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody OutofPlanDetail resources){
        outofPlanDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("update-pic-url")
    @Log("修改入库理货超收信息")
    @ApiOperation("修改入库理货超收信息")
    @PreAuthorize("@el.check('outofPlanDetail:edit')")
    public ResponseEntity<Object> updatePicUrl(@Validated @RequestBody OutofPlanDetail resources, HttpServletRequest request){
        resources.setPicUrl(url+resources.getPicUrl());
        outofPlanDetailService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除入库理货超收信息")
    @ApiOperation("删除入库理货超收信息")
    @PreAuthorize("@el.check('outofPlanDetail:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        outofPlanDetailService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
