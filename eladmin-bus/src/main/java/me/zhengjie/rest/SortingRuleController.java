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
import me.zhengjie.domain.SortingRule;
import me.zhengjie.service.SortingRuleService;
import me.zhengjie.service.dto.SortingRuleQueryCriteria;
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
* @author luob
* @date 2021-10-04
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "sortingRule管理")
@RequestMapping("/api/sortingRule")
public class SortingRuleController {

    private final SortingRuleService sortingRuleService;

    @GetMapping(value = "/query-all")
    @Log("查询sortingRule")
    @ApiOperation("查询sortingRule")
    @PreAuthorize("@el.check('sortingRule:list')")
    public ResponseEntity<Object> queryAll(){
        return new ResponseEntity<>(sortingRuleService.queryList(),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('sortingRule:list')")
    public void download(HttpServletResponse response, SortingRuleQueryCriteria criteria) throws IOException {
        sortingRuleService.download(sortingRuleService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询sortingRule")
    @ApiOperation("查询sortingRule")
    @PreAuthorize("@el.check('sortingRule:list')")
    public ResponseEntity<Object> query(SortingRuleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(sortingRuleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增sortingRule")
    @ApiOperation("新增sortingRule")
    @PreAuthorize("@el.check('sortingRule:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody SortingRule resources){
        return new ResponseEntity<>(sortingRuleService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改sortingRule")
    @ApiOperation("修改sortingRule")
    @PreAuthorize("@el.check('sortingRule:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody SortingRule resources){
        sortingRuleService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除sortingRule")
    @ApiOperation("删除sortingRule")
    @PreAuthorize("@el.check('sortingRule:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        sortingRuleService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}