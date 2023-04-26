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
import me.zhengjie.domain.WorkloadAct;
import me.zhengjie.service.WorkloadActService;
import me.zhengjie.service.dto.WorkloadActQueryCriteria;
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
* @author KC
* @date 2021-01-13
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "workload管理")
@RequestMapping("/api/workloadAct")
public class WorkloadActController {

    private final WorkloadActService workloadActService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('workloadAct:list')")
    public void download(HttpServletResponse response, WorkloadActQueryCriteria criteria) throws IOException {
        workloadActService.download(workloadActService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询workload")
    @ApiOperation("查询workload")
    @PreAuthorize("@el.check('workloadAct:list')")
    public ResponseEntity<Object> query(WorkloadActQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(workloadActService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增workload")
    @ApiOperation("新增workload")
    @PreAuthorize("@el.check('workloadAct:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody WorkloadAct resources){
        return new ResponseEntity<>(workloadActService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改workload")
    @ApiOperation("修改workload")
    @PreAuthorize("@el.check('workloadAct:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody WorkloadAct resources){
        workloadActService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除workload")
    @ApiOperation("删除workload")
    @PreAuthorize("@el.check('workloadAct:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        workloadActService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}