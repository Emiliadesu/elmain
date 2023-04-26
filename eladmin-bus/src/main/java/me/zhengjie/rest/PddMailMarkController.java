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
import me.zhengjie.domain.PddMailMark;
import me.zhengjie.service.PddMailMarkService;
import me.zhengjie.service.dto.PddMailMarkQueryCriteria;
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
* @date 2021-06-10
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "拼多多运单号与订单号绑定管理")
@RequestMapping("/api/pddMailMark")
public class PddMailMarkController {

    private final PddMailMarkService pddMailMarkService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('pddMailMark:list')")
    public void download(HttpServletResponse response, PddMailMarkQueryCriteria criteria) throws IOException {
        pddMailMarkService.download(pddMailMarkService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询拼多多运单号与订单号绑定")
    @ApiOperation("查询拼多多运单号与订单号绑定")
    @PreAuthorize("@el.check('pddMailMark:list')")
    public ResponseEntity<Object> query(PddMailMarkQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(pddMailMarkService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增拼多多运单号与订单号绑定")
    @ApiOperation("新增拼多多运单号与订单号绑定")
    @PreAuthorize("@el.check('pddMailMark:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PddMailMark resources){
        return new ResponseEntity<>(pddMailMarkService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改拼多多运单号与订单号绑定")
    @ApiOperation("修改拼多多运单号与订单号绑定")
    @PreAuthorize("@el.check('pddMailMark:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PddMailMark resources){
        pddMailMarkService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除拼多多运单号与订单号绑定")
    @ApiOperation("删除拼多多运单号与订单号绑定")
    @PreAuthorize("@el.check('pddMailMark:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        pddMailMarkService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}