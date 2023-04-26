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
import me.zhengjie.domain.OrderReturn;
import me.zhengjie.domain.ReturnGather;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.ReturnGatherService;
import me.zhengjie.service.dto.ReturnGatherQueryCriteria;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.enums.CBReturnGatherStatusEnum;
import me.zhengjie.utils.enums.CBReturnOrderStatusEnum;
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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @author luob
* @date 2022-04-06
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "returngather管理")
@RequestMapping("/api/returnGather")
public class ReturnGatherController {

    private final ReturnGatherService returnGatherService;

    @Autowired
    private WmsSupport wmsSupport;


    @GetMapping(value = "/query-wait-gather")
    @Log("查询returngather")
    @ApiOperation("查询returngather")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> queryWaitGather(){
        List<Map<String, Object>> result = returnGatherService.queryWaitGather();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/create-gather")
    @Log("查询returngather")
    @ApiOperation("查询returngather")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> createGather(Long shopId){
        returnGatherService.createGather(shopId);
        return new ResponseEntity<>("汇总成功", HttpStatus.OK);
    }

    @Log("关单")
    @ApiOperation("关单")
    @GetMapping(value = "/close-order")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> closeOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                returnGatherService.closeOrder(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + idArr[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Log("推送wms退货提总单")
    @ApiOperation("推送wms退货提总单")
    @GetMapping(value = "/push-wms-order")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> pushWmsOrder(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                returnGatherService.pushToWms(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("刷新wms状态")
    @ApiOperation("刷新wms状态")
    @GetMapping(value = "/update-wms-status")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> updateWMsStatus(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                returnGatherService.updateWMsStatus(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("对比数据")
    @ApiOperation("对比数据")
    @GetMapping(value = "/compare")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> compare(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                returnGatherService.compare(Long.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('returnGather:list')")
    public void download(HttpServletResponse response, ReturnGatherQueryCriteria criteria) throws IOException {
        returnGatherService.download(returnGatherService.queryAll(criteria), response);
    }

    @Log("导出退货单号")
    @ApiOperation("导出退货单号")
    @GetMapping(value = "/download-return-order")
    @PreAuthorize("@el.check('returnGather:list')")
    public void doExportReturnOrder(HttpServletResponse response, ReturnGatherQueryCriteria criteria) throws IOException {
        returnGatherService.doExportReturnOrder(returnGatherService.queryAll(criteria), response);
    }

    @Log("导出明细")
    @ApiOperation("导出明细")
    @GetMapping(value = "/download-details")
    @PreAuthorize("@el.check('returnGather:list')")
    public void doExportDetails(HttpServletResponse response, ReturnGatherQueryCriteria criteria) throws IOException {
        returnGatherService.doExportDetails(returnGatherService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询returngather")
    @ApiOperation("查询returngather")
    @PreAuthorize("@el.check('returnGather:list')")
    public ResponseEntity<Object> query(ReturnGatherQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(returnGatherService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增returngather")
    @ApiOperation("新增returngather")
    @PreAuthorize("@el.check('returnGather:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody ReturnGather resources){
        return new ResponseEntity<>(returnGatherService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改returngather")
    @ApiOperation("修改returngather")
    @PreAuthorize("@el.check('returnGather:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody ReturnGather resources){
        returnGatherService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除returngather")
    @ApiOperation("删除returngather")
    @PreAuthorize("@el.check('returnGather:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        returnGatherService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}