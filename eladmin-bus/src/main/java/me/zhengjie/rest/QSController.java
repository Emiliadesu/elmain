package me.zhengjie.rest;


import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.OrderLog;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.OrderLogService;
import me.zhengjie.service.QSService;
import me.zhengjie.support.EmptyResponse;
import me.zhengjie.support.queenshop.QSCommonResponse;
import me.zhengjie.support.queenshop.QSOrderDeliveryRequest;
import me.zhengjie.support.queenshop.QSSupport;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@RequiredArgsConstructor
@Api(tags = "圈尚接口")
@RequestMapping("/api/qs")
@Slf4j
public class QSController {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private QSSupport qsSupport;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private QSService qsService;


    @AnonymousAccess
    @RequestMapping("/qs.notify.pop.trade.cancel")
    @Log("圈尚订单取消通知")
    @ApiOperation("圈尚订单取消通知")
    public JSONObject notifyCancel(@RequestParam(value = "orderNo",required = false) String orderNo,
                                   @RequestParam(value = "reason") String reason,
                                   @RequestParam(value = "sign") String sign) {
        JSONObject result = new JSONObject();
        try {
            log.info("收到圈尚订单取消通知");
            log.info("orderNo:{}", orderNo);
            log.info("reason:{}", reason);
            log.info("sign:{}", sign);
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderNo);
            if (order == null)
                throw new BadRequestException("单号不存在:" + orderNo);
            crossBorderOrderService.cancel(order.getId());
            result.putOnce("success", true);
            result.putOnce("code", 200);
            result.putOnce("message", "取消成功");
            result.putOnce("nowTime", System.currentTimeMillis() / 1000);
            return result;
        }catch (Exception e) {
            result.putOnce("success", false);
            result.putOnce("code", 400);
            result.putOnce("message", e.getMessage());
            result.putOnce("nowTime", System.currentTimeMillis() / 1000);
            return result;
        }
    }

    @AnonymousAccess
    @RequestMapping("/qs.notify.pop.trade.delivery")
    @Log("圈尚运单号")
    @ApiOperation("圈尚运单号")
    public void confirmDeliver()  throws Exception{
        String orderNo="TS2109010024003111";
        CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(orderNo);
        order.setLogisticsNo("777123456");
        order.setStatus(240);
        qsService.confirmDeliver(order);
    }

    @AnonymousAccess
    @RequestMapping("/qs.pop.goods.stock.update")
    @Log("圈尚更新")
    @ApiOperation("圈尚库存更新")
    public void qsStockUpdate()  throws Exception{
        String orderNo="TS2109010002003909";
        CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(orderNo);
        qsService.QSStockUpdate(order);
    }


}
