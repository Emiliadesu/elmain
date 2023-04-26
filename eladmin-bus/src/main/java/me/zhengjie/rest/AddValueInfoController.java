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
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.AddValueInfo;
import me.zhengjie.entity.Result;
import me.zhengjie.service.AddValueInfoService;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.dto.AddValueInfoQueryCriteria;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.utils.ResultUtils;
import me.zhengjie.utils.SecureUtils;
import me.zhengjie.utils.StringUtil;
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
* @author AddValueInfo
* @date 2021-08-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "增值项基础信息管理")
@RequestMapping("/api/addValueInfo")
public class AddValueInfoController {

    private final AddValueInfoService addValueInfoService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('addValueInfo:list')")
    public void download(HttpServletResponse response, AddValueInfoQueryCriteria criteria) throws IOException {
        addValueInfoService.download(addValueInfoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询增值项基础信息")
    @ApiOperation("查询增值项基础信息")
    @PreAuthorize("@el.check('addValueInfo:list')")
    public ResponseEntity<Object> query(AddValueInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(addValueInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增增值项基础信息")
    @ApiOperation("新增增值项基础信息")
    @PreAuthorize("@el.check('addValueInfo:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody AddValueInfo resources){
        return new ResponseEntity<>(addValueInfoService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改增值项基础信息")
    @ApiOperation("修改增值项基础信息")
    @PreAuthorize("@el.check('addValueInfo:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody AddValueInfo resources){
        addValueInfoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除增值项基础信息")
    @ApiOperation("删除增值项基础信息")
    @PreAuthorize("@el.check('addValueInfo:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        addValueInfoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/querys")
    @Log("查询增值项基础信息")
    @ApiOperation("查询增值项基础信息")
//    @PreAuthorize("@el.check('addValueInfo:list')")
    public ResponseEntity<Object> querys(AddValueInfoQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(addValueInfoService.queryAll(criteria,pageable),HttpStatus.OK);
    }

//    @Log("增值服务订单下发")
//    @ApiOperation("增值服务订单下发")
//    @AnonymousPostMapping("/push-addValue")
//    public Result pushAddValue(@RequestParam(value = "data")String data,
//                          @RequestParam(value = "customersCode")String customersCode){
//        if (StringUtil.isEmpty(data))
//            return ResultUtils.getFail("data必填");
//        if (StringUtil.isEmpty(customersCode))
//                return ResultUtils.getFail("customersCode必填");
//        CustomerKeyDto customerKeyDto = customerKeyService.findByCustCode(customersCode);
//        if (customerKeyDto == null)
//            return ResultUtils.getFail("没有分配秘钥，请联系富立技术人员");
//        try {
//            String decData = SecureUtils.decryptDesHex(data,customerKeyDto.getSignKey());
//            addValueInfoService.addValue(decData, customerKeyDto.getCustomerId());
//            return ResultUtils.getSuccess();
//        }catch (Exception e) {
//            return ResultUtils.getFail(e.getMessage());
//        }
//    }


}