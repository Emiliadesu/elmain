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

import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.ClearContainer;
import me.zhengjie.domain.ClearInfo;
import me.zhengjie.domain.ClearOptLog;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.ClearInfoService;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.dto.ClearInfoQueryCriteria;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.utils.*;
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
* @author luob
* @date 2021-03-07
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "clearinfo管理")
@RequestMapping("/api/clearInfo")
public class ClearInfoController {

    private final ClearInfoService clearInfoService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @GetMapping(value = "/query-by-id")
    @Log("查询clearInfo")
    @ApiOperation("查询clearInfo")
    @PreAuthorize("@el.check('clearInfo:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(clearInfoService.queryByIdWithDetails(id),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('clearInfo:list')")
    public void download(HttpServletResponse response, ClearInfoQueryCriteria criteria) throws IOException {
        clearInfoService.download(clearInfoService.queryAll(criteria), response);
    }

    @Log("导出明细")
    @ApiOperation("导出明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('clearInfo:list')")
    public void downloadDetails(HttpServletResponse response, Long id) throws IOException {
        clearInfoService.downloadDetails(id, response);
    }

    @GetMapping
    @Log("查询clearinfo")
    @ApiOperation("查询clearInfo")
    @PreAuthorize("@el.check('clearInfo:list')")
    public ResponseEntity<Object> query(ClearInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(clearInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增clearinfo")
    @ApiOperation("新增clearinfo")
    @PreAuthorize("@el.check('clearInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ClearInfo resources){
        return new ResponseEntity<>(clearInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改clearinfo")
    @ApiOperation("修改clearinfo")
    @PreAuthorize("@el.check('clearInfo:edit')")
    public ResponseEntity<Object> update(@RequestBody ClearInfo resources){
        clearInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除clearinfo")
    @ApiOperation("删除clearinfo")
    @PreAuthorize("@el.check('clearInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        clearInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "update-opt-node")
    @Log("clearinfo节点上传")
    @ApiOperation("clearinfo节点上传")
    @PreAuthorize("@el.check('clearInfo:list')")
    public ResponseEntity<Object> updateOptNode(@RequestBody ClearOptLog log){
        clearInfoService.updateOptNode(log);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "create-hezhu")
    @Log("clearinfo节点上传")
    @ApiOperation("clearinfo节点上传")
    @PreAuthorize("@el.check('clearInfo:createHezhu')")
    public ResponseEntity<Object> createHezhu(Long id){
        clearInfoService.createHezhu(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PostMapping(value = "create-trans")
    @Log("clearinfo节点上传")
    @ApiOperation("clearinfo节点上传")
    @PreAuthorize("@el.check('clearInfo:createTrans')")
    public ResponseEntity<Object> createTrans(Long id){
        clearInfoService.createTrans(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Log("清关单据下发")
    @ApiOperation("清关单据下发")
    @RequestMapping("/add-clear-info")
    @AnonymousAccess
    public Result addClearInfo(@RequestParam("data")String data,
                         @RequestParam("customersCode")String customersCode){
        try {
            CustomerKeyDto customerKeyDto = customerKeyService.findByCustCode(customersCode);
            if (customerKeyDto == null || StringUtil.isEmpty(customerKeyDto.getSignKey())){
                return ResultUtils.getFail("没有分配秘钥，请联系富立技术人员");
            }
            String dec = SecureUtils.decryptDesHex(data,customerKeyDto.getSignKey());
            clearInfoService.addClearInfo(dec, customerKeyDto.getCustomerId());
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            return ResultUtils.getFail("清关单据下发失败："+e.getMessage());
        }
    }

    @ApiOperation("上传清关SKU")
    @RequestMapping(value = "uploadSku")
    @PreAuthorize("@el.check('clearInfo:add')")
    @Log("上传SKU")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        List<Map<String, Object>> result = clearInfoService.uploadSku(maps);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}