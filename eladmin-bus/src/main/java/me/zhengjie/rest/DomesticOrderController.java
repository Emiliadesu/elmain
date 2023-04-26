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
import me.zhengjie.domain.Config;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.DomesticOrder;
import me.zhengjie.domain.Platform;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.ConfigService;
import me.zhengjie.service.DomesticOrderService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.DomesticOrderQueryCriteria;
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
* @date 2022-04-11
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "domesticOrder管理")
@RequestMapping("/api/domesticOrder")
public class DomesticOrderController {

    private final DomesticOrderService domesticOrderService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Autowired
    private ConfigService configService;

    @ApiOperation("出库")
    @GetMapping(value = "/deliver")
    public ResponseEntity<Object> deliver(@Validated String weight, @Validated String mailNo){
        JSONObject result = new JSONObject();
        try {
            domesticOrderService.deliver(weight, mailNo);
            result.putOnce("success", true);
            result.putOnce("msg", "出库成功");
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("msg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传接单")
    @ApiOperation("回传接单")
    @GetMapping(value = "/confirm-order")
    @PreAuthorize("@el.check('domesticOrder:confirmOrder')")
    public ResponseEntity<Object> confirmOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                domesticOrderService.confirmOrder(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("审核通过")
    @ApiOperation("审核通过")
    @GetMapping(value = "/check-pass")
    @PreAuthorize("@el.check('domesticOrder:confirmOrder')")
    public ResponseEntity<Object> checkPass(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                domesticOrderService.checkPass(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("预处理完成")
    @ApiOperation("预处理完成")
    @GetMapping(value = "/pre-handel")
    @PreAuthorize("@el.check('domesticOrder:confirmOrder')")
    public ResponseEntity<Object> preHandel(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                domesticOrderService.preHandle(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("回传出库")
    @ApiOperation("回传出库")
    @GetMapping(value = "/confirm-deliver")
    @PreAuthorize("@el.check('domesticOrder:confirmOrder')")
    public ResponseEntity<Object> confirmDeliver(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                domesticOrderService.confirmDeliver(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("取消订单")
    @ApiOperation("取消订单")
    @GetMapping(value = "/cancel")
    @PreAuthorize("@el.check('domesticOrder:list')")
    public ResponseEntity<Object> cancel(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                domesticOrderService.cancel(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }




    @GetMapping(value = "/query-by-id")
    @Log("查询domesticOrderr")
    @ApiOperation("查询domesticOrderr")
    @PreAuthorize("@el.check('domesticOrder:list')")
    public ResponseEntity<Object> queryById(Long id){
        DomesticOrder order = domesticOrderService.queryByIdWithDetails(id);
        return new ResponseEntity<>(order,HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('domesticOrder:list')")
    public void download(HttpServletResponse response, DomesticOrderQueryCriteria criteria) throws IOException {
        Config config = configService.queryByK("DOWNLOAD_SWITCH");
        if (config != null && StringUtils.equals("0", config.getV())) {
            throw new BadRequestException("导出开关被关");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        domesticOrderService.download(domesticOrderService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询domesticOrder")
    @ApiOperation("查询domesticOrder")
    @PreAuthorize("@el.check('domesticOrder:list')")
    public ResponseEntity<Object> query(DomesticOrderQueryCriteria criteria, Pageable pageable){
        Config config = configService.queryByK("DOWNLOAD_SWITCH");
        if (config != null && StringUtils.equals("0", config.getV())) {
            throw new BadRequestException("导出开关被关");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        return new ResponseEntity<>(domesticOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增domesticOrder")
    @ApiOperation("新增domesticOrder")
    @PreAuthorize("@el.check('domesticOrder:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DomesticOrder resources){
        return new ResponseEntity<>(domesticOrderService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改domesticOrder")
    @ApiOperation("修改domesticOrder")
    @PreAuthorize("@el.check('domesticOrder:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DomesticOrder resources){
        domesticOrderService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除domesticOrder")
    @ApiOperation("删除domesticOrder")
    @PreAuthorize("@el.check('domesticOrder:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        domesticOrderService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("上传国内订单")
    @RequestMapping(value = "upload-order")
    @PreAuthorize("@el.check('domesticOrder:list')")
    @Log("上传国内订单")
    public ResponseEntity<Object> uploadOrder(@RequestParam("file") MultipartFile file, @RequestParam("name") String name){
        List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
        domesticOrderService.uploadOrder(maps, name);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}