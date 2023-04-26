package me.zhengjie.rest;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import com.aliyun.openservices.ons.api.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.Config;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.mq.CBOrderConsumer;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.LogisticsInfoDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.SecurityUtils;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * @website https://el-admin.vip
 * @author 王淼
 * @date 2020-12-21
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timer")
public class TimerController {
    private final WmsInstockService wmsInstockService;

    private final WmsOutstockService wmsOutstockService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CBOrderConsumer cbOrderConsumer;

    @Autowired
    private StackStockRecordService stackStockRecordService;

    @GetMapping("/in-stock-push-flux")
    @ApiOperation("入库单推送富勒")
    public ResponseEntity<Object>inStockPushFlux() {
        wmsInstockService.inStockPushFlux();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/out-stock-push-flux")
    @ApiOperation("出库单推送富勒")
    public ResponseEntity<Object>outStockPushFlux() {
        wmsOutstockService.outStockPushFlux();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-flux-so-no")
    @ApiOperation("出库单获取富勒的SO单号")
    public ResponseEntity<Object>getFluxSoNo() {
        wmsOutstockService.getFluxSoNo();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-flux-asn-no")
    @ApiOperation("入库单获取富勒的ASN单号")
    public ResponseEntity<Object>getFluxASNNo() {
        wmsInstockService.getAsnNo();
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * 抖音按订单创建时间拉取订单
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/dy-pull-order")
    @ApiOperation("抖音拉取订单")
    public ResponseEntity<Object> dyPullOrder(String startTime, String endTime) {
        douyinService.pullOrder();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/dy-pull-order-by-id")
    @ApiOperation("抖音拉取订单")
    public ResponseEntity<Object> dyPullOrderById(String shopId, String orderId) throws Exception {
        douyinService.pullOrderById(shopId, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/update-mft-status")
    @ApiOperation("抖音拉取订单")
    public ResponseEntity<Object> updateMftStatus() throws Exception {
        crossBorderOrderService.updateMftStatus();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/push-print-info-to-wms")
    @ApiOperation("抖音拉取订单")
    public ResponseEntity<Object> pushPrintInfoToWms(String orderId) throws Exception {
        douyinService.pushPrintInfoToWms(orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }



    @AnonymousAccess
    @RequestMapping("/stop-mq")
    @Log("停止MQ")
    @ApiOperation("停止MQ")
    public ResponseEntity<Object> stopMq(){
//        Config config = configService.queryByK("TEST");
        String s = HttpUtil.get("http://192.168.1.207:8000/api/clearInfo/add-clear-info");
        return new ResponseEntity<>(s, HttpStatus.OK);
    }

    @RequestMapping("/stack-stock-record")
    @Log("堆库存快照记录")
    @ApiOperation("堆库存快照记录")
    public ResponseEntity<Object> stackStockRecord(){
        stackStockRecordService.recordZhuoZStackStock();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private LogisticsInfoService logisticsInfoService;
    @AnonymousAccess
    @RequestMapping("/test-cache")
    @Log("testCache")
    @ApiOperation("testCache")
    public ResponseEntity<Object> testCache(String key){
        LogisticsInfoDto byId = logisticsInfoService.findById(Long.valueOf(key));
        return new ResponseEntity<>(byId, HttpStatus.OK);
    }

    @AnonymousAccess
    @RequestMapping("/test-cache1")
    @Log("testCache")
    @ApiOperation("testCache")
    public ResponseEntity<Object> testCache1(String key){
        String redisKey = String.valueOf(redisUtils.get(key));
        // 2.如果存在就直接消费成功，如果不存在就继续消费下去(这个方案会导致补偿业务异常补偿方案失效，先观察一段时间)
        if (StringUtils.isNotEmpty(redisKey)) {
            return new ResponseEntity<>("key存在：" + redisKey, HttpStatus.OK);
        }
        return new ResponseEntity<>("key不存在", HttpStatus.OK);
    }
}
