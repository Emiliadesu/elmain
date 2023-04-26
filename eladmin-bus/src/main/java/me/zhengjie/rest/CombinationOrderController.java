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
import me.zhengjie.domain.CombinationOrder;
import me.zhengjie.service.CombinationOrderService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.CombinationOrderQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.FileUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author wangm
* @date 2021-06-21
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "组合包 表头管理")
@RequestMapping("/api/combinationOrder")
public class CombinationOrderController {

    private final CombinationOrderService combinationOrderService;
    @Autowired
    private UserCustomerService userCustomerService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('combinationOrder:list')")
    public void download(HttpServletResponse response, CombinationOrderQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        combinationOrderService.download(combinationOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询组合包 表头")
    @ApiOperation("查询组合包 表头")
    @PreAuthorize("@el.check('combinationOrder:list')")
    public ResponseEntity<Object> query(CombinationOrderQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(combinationOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增组合包 表头")
    @ApiOperation("新增组合包 表头")
    @PreAuthorize("@el.check('combinationOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody CombinationOrder resources){
        return new ResponseEntity<>(combinationOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改组合包 表头")
    @ApiOperation("修改组合包 表头")
    @PreAuthorize("@el.check('combinationOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody CombinationOrder resources){
        combinationOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除组合包 表头")
    @ApiOperation("删除组合包 表头")
    @PreAuthorize("@el.check('combinationOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        combinationOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传组合包")
    @RequestMapping(value = "uploadComb")
    @PreAuthorize("@el.check('combinationOrder:uploadSku')")
    @Log("上传组合包")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        combinationOrderService.uploadComb(maps);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
