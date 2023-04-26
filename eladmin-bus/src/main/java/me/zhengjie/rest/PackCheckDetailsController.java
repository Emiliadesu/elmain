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
import me.zhengjie.domain.PackCheckDetails;
import me.zhengjie.service.PackCheckDetailsService;
import me.zhengjie.service.dto.PackCheckDetailsQueryCriteria;
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
* @date 2021-07-22
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "packCheckDetails管理")
@RequestMapping("/api/packCheckDetails")
public class PackCheckDetailsController {

    private final PackCheckDetailsService packCheckDetailsService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('packCheckDetails:list')")
    public void download(HttpServletResponse response, PackCheckDetailsQueryCriteria criteria) throws IOException {
        packCheckDetailsService.download(packCheckDetailsService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询packCheckDetails")
    @ApiOperation("查询packCheckDetails")
    @PreAuthorize("@el.check('packCheckDetails:list')")
    public ResponseEntity<Object> query(PackCheckDetailsQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(packCheckDetailsService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增packCheckDetails")
    @ApiOperation("新增packCheckDetails")
    @PreAuthorize("@el.check('packCheckDetails:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody PackCheckDetails resources){
        return new ResponseEntity<>(packCheckDetailsService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改packCheckDetails")
    @ApiOperation("修改packCheckDetails")
    @PreAuthorize("@el.check('packCheckDetails:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody PackCheckDetails resources){
        packCheckDetailsService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除packCheckDetails")
    @ApiOperation("删除packCheckDetails")
    @PreAuthorize("@el.check('packCheckDetails:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        packCheckDetailsService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}