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
import me.zhengjie.domain.ShopToken;
import me.zhengjie.service.DouyinService;
import me.zhengjie.service.ShopTokenService;
import me.zhengjie.service.dto.ShopTokenQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author 王淼
* @date 2020-10-20
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "shopToken管理")
@RequestMapping("/api/shopToken")
public class ShopTokenController {

    private final ShopTokenService shopTokenService;

    @Autowired
    private DouyinService douyinService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('shopToken:list')")
    public void download(HttpServletResponse response, ShopTokenQueryCriteria criteria) throws IOException {
        shopTokenService.download(shopTokenService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询shopToken")
    @ApiOperation("查询shopToken")
    @PreAuthorize("@el.check('shopToken:list')")
    public ResponseEntity<Object> query(ShopTokenQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(shopTokenService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增shopToken")
    @ApiOperation("新增shopToken")
    @PreAuthorize("@el.check('shopToken:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ShopToken resources){
        return new ResponseEntity<>(shopTokenService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改shopToken")
    @ApiOperation("修改shopToken")
    @PreAuthorize("@el.check('shopToken:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ShopToken resources){
        shopTokenService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除shopToken")
    @ApiOperation("删除shopToken")
    @PreAuthorize("@el.check('shopToken:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        shopTokenService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("copy-link")
    @Log("复制授权链接")
    @ApiOperation("复制授权链接")
    public ResponseEntity<Object> copyLink(Long id){
        return new ResponseEntity<>(shopTokenService.copyLink(id),HttpStatus.OK);
    }

    @PostMapping("to-token")
    @Log("去授权")
    @ApiOperation("去授权")
    public ResponseEntity<Object> toToken(Long id){
        shopTokenService.toToken(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("refresh-token")
    @Log("刷新授权")
    @ApiOperation("刷新授权")
    public ResponseEntity<Object> refreshToken(Long id) throws Exception {
        ShopToken shopToken = shopTokenService.queryById(id);
        shopTokenService.testTokenOverdue(shopToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
