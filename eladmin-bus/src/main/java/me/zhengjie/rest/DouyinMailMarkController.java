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
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.DouyinMailMark;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DouyinMailMarkService;
import me.zhengjie.service.DouyinService;
import me.zhengjie.service.dto.DouyinMailMarkQueryCriteria;
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
* @author le
* @date 2021-09-28
**/
@RestController
@RequiredArgsConstructor
@Api(tags = "douyinmailmark管理")
@RequestMapping("/api/douyinMailMark")
public class DouyinMailMarkController {

    private final DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private DouyinService douyinService;

    @Log("回传运单")
    @ApiOperation("回传运单")
    @GetMapping(value = "/confirm-mail-no")
    @PreAuthorize("@el.check('crossBorderOrder:confirmClearSucc')")
    public ResponseEntity<Object> confirmMailNo(String ids){
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < idArr.length; i++) {
            try {
                douyinService.getMailNo(String.valueOf(idArr[i]));
                resMsg = resMsg + idArr[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                resMsg = resMsg + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('douyinMailMark:list')")
    public void download(HttpServletResponse response, DouyinMailMarkQueryCriteria criteria) throws IOException {
        douyinMailMarkService.download(douyinMailMarkService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询douyinmailmark")
    @ApiOperation("查询douyinmailmark")
    @PreAuthorize("@el.check('douyinMailMark:list')")
    public ResponseEntity<Object> query(DouyinMailMarkQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(douyinMailMarkService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增douyinmailmark")
    @ApiOperation("新增douyinmailmark")
    @PreAuthorize("@el.check('douyinMailMark:add')")
    public ResponseEntity<Object> create(@Validated @RequestBody DouyinMailMark resources){
        return new ResponseEntity<>(douyinMailMarkService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改douyinmailmark")
    @ApiOperation("修改douyinmailmark")
    @PreAuthorize("@el.check('douyinMailMark:edit')")
    public ResponseEntity<Object> update(@Validated @RequestBody DouyinMailMark resources){
        douyinMailMarkService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除douyinmailmark")
    @ApiOperation("删除douyinmailmark")
    @PreAuthorize("@el.check('douyinMailMark:del')")
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestBody Long[] ids) {
        douyinMailMarkService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}