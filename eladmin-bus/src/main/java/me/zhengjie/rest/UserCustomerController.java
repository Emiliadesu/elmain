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

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.UserCustomer;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.UserCustomerQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2021-04-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "usercustomer管理")
@RequestMapping("/api/userCustomer")
public class UserCustomerController {

    private final UserCustomerService userCustomerService;

    @Log("保存usercustomer")
    @ApiOperation("保存usercustomer")
    @RequestMapping(value = "/save")
    @PreAuthorize("@el.check('userCustomer:add')")
    public ResponseEntity<Object> save(@RequestBody JSONObject data){
        Long userId = data.getLong("userId");
        JSONArray shopIds = data.getJSONArray("shopIds");
        userCustomerService.save(shopIds, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("查询usercustomer")
    @ApiOperation("查询usercustomer")
    @RequestMapping(value = "/query-shops")
    @PreAuthorize("@el.check('userCustomer:list')")
    public List<Long> queryShops(Long userId){
        List<Long> ids = userCustomerService.queryShops(userId);
        return ids;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('userCustomer:list')")
    public void download(HttpServletResponse response, UserCustomerQueryCriteria criteria) throws IOException {
        userCustomerService.download(userCustomerService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询usercustomer")
    @ApiOperation("查询usercustomer")
    @PreAuthorize("@el.check('userCustomer:list')")
    public ResponseEntity<Object> query(UserCustomerQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(userCustomerService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增usercustomer")
    @ApiOperation("新增usercustomer")
    @PreAuthorize("@el.check('userCustomer:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody UserCustomer resources){
        return new ResponseEntity<>(userCustomerService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改usercustomer")
    @ApiOperation("修改usercustomer")
    @PreAuthorize("@el.check('userCustomer:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody UserCustomer resources){
        userCustomerService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除usercustomer")
    @ApiOperation("删除usercustomer")
    @PreAuthorize("@el.check('userCustomer:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        userCustomerService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}