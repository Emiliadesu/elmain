package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.domain.PackCheck;
import me.zhengjie.domain.PackCheckDetails;
import me.zhengjie.entity.Result;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.PackCheckDto;
import me.zhengjie.service.dto.StockDto;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "APP接口")
@RequestMapping("/api/app")
@Slf4j
public class AppApiController {

    @Autowired
    private AppApiService appApiService;


    @AnonymousAccess
    @RequestMapping("/pack-check-init")
    @Log("APP接口根据运单号获取订单")
    @ApiOperation("APP接口根据运单号获取订单")
    public Result packCheckInit(String mailNo) {
        try {
            PackCheck packCheck = appApiService.packCheckInit(mailNo);
            return ResultUtils.getSuccess(packCheck);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @AnonymousAccess
    @RequestMapping("/pack-check")
    @Log("包裹抽检")
    @ApiOperation("包裹抽检")
    public Result packCheck(@RequestParam(value = "checkId") Long checkId,
                            @RequestParam(value = "barCode") String barCode) {
        try {
            PackCheck packCheck = appApiService.packCheck(checkId, barCode);
            return ResultUtils.getSuccess(packCheck);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @AnonymousAccess
    @RequestMapping("/pack-check-submit")
    @Log("包裹抽检")
    @ApiOperation("包裹抽检")
    public Result packCheckSubmit(@RequestParam(value = "checkId") Long checkId) {
        try {
            appApiService.packCheckSubmit(checkId);
            return ResultUtils.getSuccess();
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }
    }

    @AnonymousAccess
    @RequestMapping("/query-stock")
    @Log("APP接口库存查询")
    @ApiOperation("APP接口库存查询")
    public Result queryStock(@RequestParam(value = "barCode") String barCode) {
        try {
            List<StockDto> stockDtos = appApiService.queryStock(barCode);
            if (CollectionUtils.isEmpty(stockDtos))
                return ResultUtils.getFail("无库存");
            return ResultUtils.getSuccess(stockDtos);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }
    }
}
