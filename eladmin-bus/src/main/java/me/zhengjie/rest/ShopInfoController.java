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
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.BooksInfoQueryCriteria;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.service.dto.ShopInfoQueryCriteria;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.FileUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author 王淼
* @date 2020-10-20
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "shopInfo管理")
@RequestMapping("/api/shopInfo")
public class ShopInfoController {

    private final ShopInfoService shopInfoService;

    @ApiOperation("返回当前登录用户的客户")
    @GetMapping(value = "/query-current-user-shop")
    public ResponseEntity<Object> queryCurrentUserShop(Long customerId){
        return new ResponseEntity<>(shopInfoService.queryCurrentUserShop(customerId),HttpStatus.OK);
    }

    @GetMapping(value = "/query-all-id")
    @Log("查询shopInfo")
    @ApiOperation("查询shopInfo")
    @PreAuthorize("@el.check('shopInfo:list')")
    public ResponseEntity<Object> queryAllId(){
        List<ShopInfoDto> shopInfoDtos = shopInfoService.queryAll(new ShopInfoQueryCriteria());
        List<Long> shopIds = new ArrayList<>();
        for (ShopInfoDto shopInfoDto : shopInfoDtos) {
            shopIds.add(shopInfoDto.getId());
        }
        return new ResponseEntity<>(shopIds,HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('shopInfo:list')")
    public void download(HttpServletResponse response, ShopInfoQueryCriteria criteria) throws IOException {
        shopInfoService.download(shopInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询shopInfo")
    @ApiOperation("查询shopInfo")
    @PreAuthorize("@el.check('shopInfo:list')")
    public ResponseEntity<Object> query(ShopInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(shopInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping("query-zhuoZhi")
    @Log("查询shopInfo")
    @ApiOperation("查询shopInfo")
    public ResponseEntity<Object> queryZhuoZhi(){
        return new ResponseEntity<>(shopInfoService.queryZhuoZhi(),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增shopInfo")
    @ApiOperation("新增shopInfo")
    @PreAuthorize("@el.check('shopInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ShopInfo resources){
        return new ResponseEntity<>(shopInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改shopInfo")
    @ApiOperation("修改shopInfo")
    @PreAuthorize("@el.check('shopInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ShopInfo resources){
        shopInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除shopInfo")
    @ApiOperation("删除shopInfo")
    @PreAuthorize("@el.check('shopInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        shopInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导入shopInfo")
    @ApiOperation("导入shopInfo")
    @PreAuthorize("@el.check('shopInfo:import')")
    @PostMapping("/import-shop-info")
    public ResponseEntity<Object> importShopInfo(@RequestParam("file") MultipartFile file) {
        List<Map<String, Object>> list = FileUtils.importMapExcel(file);
        shopInfoService.uploadInsert(list);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
