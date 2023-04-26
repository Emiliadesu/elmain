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

import cn.hutool.json.JSONObject;
import me.zhengjie.annotation.Log;
import me.zhengjie.annotation.rest.AnonymousPostMapping;
import me.zhengjie.domain.AddValueOrder;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.AddValueOrderService;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.dto.AddValueOrderQueryCriteria;
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
* @author AddValueOrder
* @date 2021-08-05
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "增值单据管理")
@RequestMapping("/api/addValueOrder")
public class AddValueOrderController {

    private final AddValueOrderService addValueOrderService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Log("增值单据完成")
    @ApiOperation("增值单据完成")
    @GetMapping(value = "/finish")
    @PreAuthorize("@el.check('addValueOrder:finish')")
    public ResponseEntity<Object> finish(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                addValueOrderService.finish(Long.valueOf(ids));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/query-by-id")
    @Log("查询addValueOrder")
    @ApiOperation("查询addValueOrder")
    @PreAuthorize("@el.check('addValueOrder:list')")
    public ResponseEntity<Object> queryById(Long id){
        return new ResponseEntity<>(addValueOrderService.queryByIdWithDetails(id),HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('addValueOrder:list')")
    public void download(HttpServletResponse response, AddValueOrderQueryCriteria criteria) throws IOException {
        addValueOrderService.download(addValueOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询增值单据")
    @ApiOperation("查询增值单据")
    @PreAuthorize("@el.check('addValueOrder:list')")
    public ResponseEntity<Object> query(AddValueOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(addValueOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @GetMapping("/queryVal")
    @Log("查询增值单据")
    @ApiOperation("查询增值单据")
    @PreAuthorize("@el.check('addValueOrder:list')")
    public ResponseEntity<Object> querys(AddValueOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(addValueOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增增值单据")
    @ApiOperation("新增增值单据")
    @PreAuthorize("@el.check('addValueOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody AddValueOrder resources){
        return new ResponseEntity<>(addValueOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改增值单据")
    @ApiOperation("修改增值单据")
    @PreAuthorize("@el.check('addValueOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody AddValueOrder resources){
        addValueOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除增值单据")
    @ApiOperation("删除增值单据")
    @PreAuthorize("@el.check('addValueOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        addValueOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("增值服务订单下发")
    @ApiOperation("增值服务订单下发")
    @AnonymousPostMapping("/add-add-value-order")
    public Result pushAddValue(@RequestParam(value = "data")String data,
                               @RequestParam(value = "customersCode")String customersCode){
        if (StringUtil.isEmpty(data))
            return ResultUtils.getFail("data必填");
        if (StringUtil.isEmpty(customersCode))
            return ResultUtils.getFail("customersCode必填");
        CustomerKeyDto customerKeyDto = customerKeyService.findByCustCode(customersCode);
        if (customerKeyDto == null)
            return ResultUtils.getFail("没有分配秘钥，请联系富立技术人员");
        try {
            String decData = SecureUtils.decryptDesHex(data,customerKeyDto.getSignKey());
            addValueOrderService.addValue(decData, customerKeyDto.getCustomerId());
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            return ResultUtils.getFail("增值单下发失败："+e.getMessage());
        }
    }


}