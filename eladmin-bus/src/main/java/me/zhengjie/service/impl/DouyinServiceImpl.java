package me.zhengjie.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.mq.CBReturnProducer;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.rest.model.douyin.*;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.*;
import me.zhengjie.support.*;
import me.zhengjie.support.douyin.*;
import me.zhengjie.support.fuliPre.ActAllocationDetails;
import me.zhengjie.support.fuliPre.BaseSkuPackInfo;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

/**
 * 抖音服务接口类
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DouyinServiceImpl implements DouyinService {

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private ShopTokenService shopTokenService;

    @Autowired
    private DYSupport dySupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private PullOrderLogService pullOrderLogService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private OrderReturnService orderReturnService;

    @Autowired
    private OrderReturnLogService orderReturnLogService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CBReturnProducer cbReturnProducer;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private DouyinGoodsDetailsService douyinGoodsDetailsService;

    @Autowired
    private DyOrderPushService dyOrderPushService;

    @Autowired
    private DepositService depositService;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private MailSupport mailSupport;

    @Autowired
    private PackageInfoService packageInfoService;

    @Autowired
    private DailyStockService dailyStockService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private OrderMaterialService orderMaterialService;

    @Autowired
    private DyStockTakingService dyStockTakingService;

    @Autowired
    private DyStockTransformService dyStockTransformService;

    @Autowired
    private ReturnGatherService returnGatherService;

    @Autowired
    private ReturnGatherDetailService returnGatherDetailService;

    @Autowired
    private DyCangzuFeeService dyCangzuFeeService;

    private static String vendor = "NBFL";


    @Value("${douyin.app-key}")
    public String appKey;
    @Value("${douyin.app-secret}")
    public String appSecret;
    @Value("${douyin.url}")
    private String url;


    @Override
    public void pullOrder() {
        Config config = configService.queryByK("ORDER_PULL");
        if (config != null && StringUtils.equals("0", config.getV())) {
            return;
        }
        // 查询可拉单的商家列表
        List<ShopInfo> shopList = shopInfoService.queryByPlafCode(PlatformConstant.DY);
        if (CollectionUtils.isEmpty(shopList))
            throw new BadRequestException("无可拉单商家，请检查平台商家配置");
        for (ShopInfo shopInfo : shopList) {
            // 获取店铺授权信息
            ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
            if (shopToken == null || !StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }

            PullOrderLog pullTime = pullOrderLogService.getPullTime(shopInfo.getId());
            if (pullTime == null) {
                pullTime = new PullOrderLog();
                pullTime.setPageNo(0);
                pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
                pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
            }

            int pageNo = pullTime.getPageNo();
            int pageSize = 100;
            Date startTime = new Date(pullTime.getStartTime().getTime());
            Date endTime = new Date(pullTime.getEndTime().getTime());

            // 分页拉取订单，
            CBOrderListRequest request = new CBOrderListRequest();
            request.setStartTime(startTime);
            request.setEndTime(endTime);
            request.setOrderBy("create_time");
            request.setVendor(vendor);
            request.setPage(String.valueOf(pageNo));
            request.setSize(String.valueOf(pageSize));

            request.setAccessToken(shopToken.getAccessToken());
            request.setCustId(shopInfo.getCustId());
            request.setShopId(shopInfo.getId());
            dySupport.setAccessToken(shopToken.getAccessToken());
            BigDecimal totalNum;
            try {
                checkToken(shopToken);
                log.info("抖音拉单：请求参数：{}", request);
                // 第一次先查询本次有多少订单
                dySupport.setApiParam(request);
                DYCommonResponse<DYOrderListResponse> response = dySupport.request(DYOrderListResponse.class);
                log.info("抖音拉单：返回：{}", response);
                if (StringUtil.equals(response.getStatusCode(), "0")) {
                    // 拉取成功
                    totalNum = BigDecimal.valueOf(response.getData().getTotal());
//                        Boolean hasNext = response.getData().getHasNext();
                    if (totalNum.compareTo(new BigDecimal(pageSize)) <= 0) {
                        // 没有下一页
                        response.getData().setlShopId(shopInfo.getId());
                        response.getData().setCustId(shopInfo.getCustId());

                        // 记录拉单日志
                        PullOrderLog log = new PullOrderLog(
                                shopInfo.getId(),
                                pullTime.getStartTime(),
                                pullTime.getEndTime(),
                                pageNo,
                                pageSize,
                                "F",
                                totalNum.toString(),
                                "T",
                                "成功"
                        );
                        pullOrderLogService.create(log);
                        handleOrder(response);
                    }else {
                        // 循环分页拉取数据
                        // 计算总页数
                        BigDecimal totalPage = totalNum.divide(BigDecimal.valueOf(pageSize), 0, BigDecimal.ROUND_UP);
                        for (int i = pageNo; i < totalPage.intValue(); i++) {
                            request.setPage(String.valueOf(i));
                            request.setSize(String.valueOf(pageSize));

                            // 发送MQ消息
                            String jsonString = JSON.toJSONString(request);
                            cbOrderProducer.send(
                                    MsgType.CB_ORDER_PULL,
                                    String.valueOf(jsonString),
                                    null
                            );
                        }
                    }
                }else {
                    // 拉取异常抖音给出异常信息，保存到日志表
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "E",
                            "0",
                            "F",
                            response.getMessage()
                    );
                    pullOrderLogService.create(log);
                }
            }catch (Exception e) {
                // catch住异常信息并保存到日志表，继续循环下一个店铺
                PullOrderLog log = new PullOrderLog(
                        shopInfo.getId(),
                        pullTime.getStartTime(),
                        pullTime.getEndTime(),
                        pageNo,
                        pageSize,
                        "E",
                        "0",
                        "F",
                        e.getMessage()
                );
                pullOrderLogService.create(log);
                e.printStackTrace();
            }

        }
    }

    /*
    定时拉单
     */
    @Override
    public void pullOrderDYDetails() {
        // 查询可拉单的商家列表
        List<ShopInfo> shopList = shopInfoService.queryByPlafCode(PlatformConstant.DY);
        if (CollectionUtils.isEmpty(shopList))
            throw new BadRequestException("无可拉单商家，请检查平台商家配置");
        for (ShopInfo shopInfo : shopList) {
            // 获取店铺授权信息
            ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
            if (shopToken == null || !StringUtil.equals("1", shopToken.getPullOrderAble())) {
                // 没开启拉单
                continue;
            }

            PullOrderLog pullTime = new PullOrderLog();
            pullTime.setPageNo(0);
            pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
            pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
            Date end = new Date();
            Date start = new Date(end.getTime() - 3600*1000*24);

            int pageNo = 0;
            int pageSize = 100;
            Date startTime = start;
            Date endTime = end;

            //分页拉取订单，
            CBOrderListRequest request = new CBOrderListRequest();
            request.setStartTime(startTime);
            request.setEndTime(endTime);
            request.setOrderBy("create_time");
            request.setVendor(vendor);
            request.setPage(String.valueOf(pageNo));
            request.setSize(String.valueOf(pageSize));

            request.setAccessToken(shopToken.getAccessToken());
            request.setCustId(shopInfo.getCustId());
            request.setShopId(shopInfo.getId());
            dySupport.setAccessToken(shopToken.getAccessToken());
            BigDecimal totalNum;
            try {
                checkToken(shopToken);
                log.info("抖音拉单：请求参数：{}", request);
                // 第一次先查询本次有多少订单
                dySupport.setApiParam(request);
                DYCommonResponse<DYOrderListResponse> response = dySupport.request(DYOrderListResponse.class);
                log.info("抖音拉单：返回：{}", response);
                if (StringUtil.equals(response.getStatusCode(), "0")) {
                    // 拉取成功
                    totalNum = BigDecimal.valueOf(response.getData().getTotal());
//                        Boolean hasNext = response.getData().getHasNext();
                    if (totalNum.compareTo(new BigDecimal(pageSize)) <= 0) {
                        // 没有下一页
                        response.getData().setlShopId(shopInfo.getId());
                        response.getData().setCustId(shopInfo.getCustId());

                        // 记录拉单日志
                        PullOrderLog log = new PullOrderLog(
                                shopInfo.getId(),
                                pullTime.getStartTime(),
                                pullTime.getEndTime(),
                                pageNo,
                                pageSize,
                                "F",
                                totalNum.toString(),
                                "T",
                                "成功"
                        );
                        pullOrderLogService.create(log);
                        handleOrder(response);
                    }else {
                        // 循环分页拉取数据
                        // 计算总页数
                        BigDecimal totalPage = totalNum.divide(BigDecimal.valueOf(pageSize), 0, BigDecimal.ROUND_UP);
                        for (int i = pageNo; i < totalPage.intValue(); i++) {
                            request.setPage(String.valueOf(i));
                            request.setSize(String.valueOf(pageSize));

                            // 发送MQ消息
                            String jsonString = JSON.toJSONString(request);
                            cbOrderProducer.send(
                                    MsgType.CB_ORDER_PULL,
                                    String.valueOf(jsonString),
                                    null
                            );
                        }
                    }
                }else {
                    // 拉取异常抖音给出异常信息，保存到日志表
                    PullOrderLog log = new PullOrderLog(
                            shopInfo.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "E",
                            "0",
                            "F",
                            response.getMessage()
                    );
                    pullOrderLogService.create(log);
                }
            }catch (Exception e) {
                // catch住异常信息并保存到日志表，继续循环下一个店铺
                PullOrderLog log = new PullOrderLog(
                        shopInfo.getId(),
                        pullTime.getStartTime(),
                        pullTime.getEndTime(),
                        pageNo,
                        pageSize,
                        "E",
                        "0",
                        "F",
                        e.getMessage()
                );
                pullOrderLogService.create(log);
                e.printStackTrace();
            }

        }
    }

    @Override
    public void pullOrderByShop(Long shopId, String start, String end) {
        // 查询可拉单的商家列表
        ShopInfoDto shopInfoDto = shopInfoService.queryById(shopId);
        if (shopInfoDto == null)
            throw new BadRequestException("商家不存在");
        // 获取店铺授权信息
        ShopToken shopToken = shopTokenService.queryByShopId(shopInfoDto.getId());

        PullOrderLog pullTime = new PullOrderLog();
        pullTime.setPageNo(0);
        pullTime.setEndTime(new Timestamp(System.currentTimeMillis()));
        pullTime.setStartTime(new Timestamp(System.currentTimeMillis() - 3600*1000*24));
        Date startTime = DateUtils.parse(start, DatePattern.NORM_DATETIME_PATTERN);
        Date endTime = DateUtils.parse(end, DatePattern.NORM_DATETIME_PATTERN);

        int pageNo = 0;
        int pageSize = 100;

        //分页拉取订单，
        CBOrderListRequest request = new CBOrderListRequest();
        request.setStartTime(startTime);
        request.setEndTime(endTime);
        request.setOrderBy("create_time");
        request.setVendor(vendor);
        request.setPage(String.valueOf(pageNo));
        request.setSize(String.valueOf(pageSize));

        request.setAccessToken(shopToken.getAccessToken());
        request.setCustId(shopInfoDto.getCustId());
        request.setShopId(shopInfoDto.getId());
        dySupport.setAccessToken(shopToken.getAccessToken());
        BigDecimal totalNum;
        try {
            checkToken(shopToken);
            log.info("抖音拉单：请求参数：{}", request);
            // 第一次先查询本次有多少订单
            dySupport.setApiParam(request);
            DYCommonResponse<DYOrderListResponse> response = dySupport.request(DYOrderListResponse.class);
            log.info("抖音拉单：返回：{}", response);
            if (StringUtil.equals(response.getStatusCode(), "0")) {
                // 拉取成功
                totalNum = BigDecimal.valueOf(response.getData().getTotal());
//                        Boolean hasNext = response.getData().getHasNext();
                if (totalNum.compareTo(new BigDecimal(pageSize)) <= 0) {
                    // 没有下一页
                    response.getData().setlShopId(shopInfoDto.getId());
                    response.getData().setCustId(shopInfoDto.getCustId());

                    // 记录拉单日志
                    PullOrderLog log = new PullOrderLog(
                            shopInfoDto.getId(),
                            pullTime.getStartTime(),
                            pullTime.getEndTime(),
                            pageNo,
                            pageSize,
                            "F",
                            totalNum.toString(),
                            "T",
                            "成功"
                    );
                    pullOrderLogService.create(log);
                    handleOrder(response);
                }else {
                    // 循环分页拉取数据
                    // 计算总页数
                    BigDecimal totalPage = totalNum.divide(BigDecimal.valueOf(pageSize), 0, BigDecimal.ROUND_UP);
                    for (int i = pageNo; i < totalPage.intValue(); i++) {
                        request.setPage(String.valueOf(i));
                        request.setSize(String.valueOf(pageSize));

                        // 发送MQ消息
                        String jsonString = JSON.toJSONString(request);
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_PULL,
                                String.valueOf(jsonString),
                                null
                        );
                    }
                }
            }else {
                // 拉取异常抖音给出异常信息，保存到日志表
                PullOrderLog log = new PullOrderLog(
                        shopInfoDto.getId(),
                        pullTime.getStartTime(),
                        pullTime.getEndTime(),
                        pageNo,
                        pageSize,
                        "E",
                        "0",
                        "F",
                        response.getMessage()
                );
                pullOrderLogService.create(log);
            }
        }catch (Exception e) {
            // catch住异常信息并保存到日志表，继续循环下一个店铺
            PullOrderLog log = new PullOrderLog(
                    shopInfoDto.getId(),
                    pullTime.getStartTime(),
                    pullTime.getEndTime(),
                    pageNo,
                    pageSize,
                    "E",
                    "0",
                    "F",
                    e.getMessage()
            );
            pullOrderLogService.create(log);
            e.printStackTrace();
        }
    }


    @Override
    public void pullOrderByPage(String body) throws Exception {
        CBOrderListRequest request = JSON.parseObject(body, CBOrderListRequest.class);
        dySupport.setAccessToken(request.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音拉单：请求参数：{}", request);
        DYCommonResponse<DYOrderListResponse> res = dySupport.request(DYOrderListResponse.class);
        log.info("抖音拉单：返回：{}", res);
        if (StringUtil.equals(res.getStatusCode(), "0")) {
            Boolean resHasNext = res.getData().getHasNext();
            res.getData().setlShopId(request.getShopId());
            res.getData().setCustId(request.getCustId());
            // 将客户、店铺信息插入实体，省得到下一步再去查数据库

            Long totalNum = res.getData().getTotal();
            String nextPage = resHasNext?"T":"F";
            // 记录拉单日志
            PullOrderLog log = new PullOrderLog(
                    request.getShopId(),
                    new Timestamp(request.getStartTime().getTime()),
                    new Timestamp(request.getEndTime().getTime()),
                    Integer.valueOf(request.getPage()),
                    Integer.valueOf(request.getSize()),
                    nextPage,
                    totalNum.toString(),
                    "T",
                    "成功"
            );
            pullOrderLogService.create(log);
            handleOrder(res);
        }else {
            PullOrderLog log = new PullOrderLog(
                    request.getShopId(),
                    new Timestamp(request.getStartTime().getTime()),
                    new Timestamp(request.getEndTime().getTime()),
                    Integer.valueOf(request.getPage()),
                    Integer.valueOf(request.getSize()),
                    "E",
                    "0",
                    "F",
                    res.getMessage()
            );
            pullOrderLogService.create(log);
        }
    }

    @Override
    public Long queryShopTotalOrder(String startTime, String endTime, Long shopId) throws Exception {
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(shopId);
            int pageNo = 0;
            int pageSize = 1;

            CBOrderListRequest request = new CBOrderListRequest();
            request.setStartTime(DateUtils.parse(startTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setEndTime(DateUtils.parse(endTime, DatePattern.NORM_DATETIME_PATTERN));
            request.setOrderBy("create_time");
            request.setVendor(vendor);
            request.setPage(String.valueOf(pageNo));
            request.setSize(String.valueOf(pageSize));
            dySupport.setAccessToken(shopToken.getAccessToken());
            dySupport.setApiParam(request);
            // 第一次先查询本次有多少订单
            DYCommonResponse<DYOrderListResponse> response = dySupport.request(DYOrderListResponse.class);
            if (StringUtil.equals(response.getStatusCode(), "0")) {
                return response.getData().getTotal();
            }else {
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void pullOrderById(String shopId, String orderId) throws Exception {
        ShopToken shopToken = shopTokenService.queryByShopId(Long.valueOf(shopId));
        if (shopToken==null)
            shopToken=shopTokenService.queryByPaltShopId(shopId);
        if (shopToken == null)
            throw new BadRequestException("店铺未配置授权信息，请先配置:" + shopId);
        ShopInfoDto shopInfo = shopInfoService.findByIdDto(shopToken.getShopId());
        try {
            checkToken(shopToken);
            CBOrderListRequest request = new CBOrderListRequest();
            request.setStartTime(new Date(System.currentTimeMillis()-3600*24*1000));
            request.setEndTime(new Date());
            request.setOrderBy("create_time");
            request.setVendor(vendor);
            request.setPage("0");
            request.setSize("30");
            request.setOrderList(new String[]{orderId});
            dySupport.setAccessToken(shopToken.getAccessToken());
            dySupport.setApiParam(request);
            DYCommonResponse<DYOrderListResponse> response = dySupport.request(DYOrderListResponse.class);
            if (StringUtil.equals(response.getStatusCode(), "0")) {
                response.getData().setlShopId(shopInfo.getId());
                response.getData().setCustId(shopInfo.getCustId());
                List<CBOrderListMain> list = response.getData().getList();
                if (CollectionUtils.isNotEmpty(list)) {
                    CBOrderListMain cbOrderListMain = list.get(0);
                    cbOrderListMain.setlShopId(response.getData().getlShopId());
                    cbOrderListMain.setCustId(response.getData().getCustId());
                    String jsonString = JSON.toJSONString(cbOrderListMain);
                    createOrder(jsonString);
                }
            }else {
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Integer getUpStatus(CrossBorderOrder order, ShopToken shopToken) throws Exception {
        if (shopToken == null) {
            shopToken = shopTokenService.queryByShopId(order.getShopId());
        }
        CBOrderListRequest request = new CBOrderListRequest();
        request.setStartTime(new Date(System.currentTimeMillis()-3600*24*1000));
        request.setEndTime(new Date());
        request.setOrderBy("create_time");
        request.setVendor(vendor);
        request.setPage("0");
        request.setSize("30");
        request.setOrderList(new String[]{order.getOrderNo()});
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<DYOrderListResponse> response;
        try {
            response = dySupport.request(DYOrderListResponse.class);
            if (StringUtil.equals(response.getStatusCode(), "0")) {
                List<CBOrderListMain> list = response.getData().getList();
                return list.get(0).getStatus();
            }else {
                throw new BadRequestException(response.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    /**
     * 处理拉到的订单数据
     * @param response
     */
    private  void handleOrder(DYCommonResponse<DYOrderListResponse> response) {
        List<CBOrderListMain> list = response.getData().getList();
        for (CBOrderListMain cbOrderListMain : list) {
            cbOrderListMain.setlShopId(response.getData().getlShopId());
            cbOrderListMain.setCustId(response.getData().getCustId());
            String jsonString = JSON.toJSONString(cbOrderListMain);
            // 保存订单通知
            cbOrderProducer.send(
                    MsgType.CB_ORDER_200,
                    String.valueOf(jsonString),
                    cbOrderListMain.getOrderId()
            );
        }
    }

    // 回传接单
    @Override
    public void confirmOrder(CrossBorderOrder order) throws Exception{
        long start = new Date().getTime();
        String costTimeMsg = "";

        log.info("抖音开始回传接单：{}", order.getOrderNo());
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_200.getCode().intValue()) {
            throw new BadRequestException("当前状态不允许接单回传：" + order.getId());
        }


        if (StringUtil.equals(order.getUpStatus(),"102")
                || StringUtil.equals(order.getUpStatus(),"100")) {
            throw new BadRequestException("100和102状态不能接单回传：" + order.getId());
        }

        if (StringUtils.isEmpty(order.getBuyerName())) {
            throw new BadRequestException("抖音状态正常但是无申报必要信息不能回传接单：" + order.getId());
        }
        // 开始回传接单
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        long costTime1 = new Date().getTime() - start;
        costTimeMsg = costTimeMsg + "查询shopToken花费时间：" + costTime1;

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000)+""):DateUtil.formatDateTime(new Date()));
        request.setStatus("9");
        request.setVendor(vendor);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音回传接单,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        long costTime2 = new Date().getTime() - start;
        costTimeMsg = costTimeMsg + "请求抖音花费时间：" + costTime2 + "<抖音Support：" + response.getMsg() + ">";
        log.info("抖音回传接单,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传接单成功,更改订单状态
            order.setStatus(CBOrderStatusEnum.STATUS_215.getCode());
            order.setReceivedBackTime(new Timestamp(System.currentTimeMillis()));
            crossBorderOrderService.update(order);

            long costTime3 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "更新订单信息花费时间：" + costTime3;

            if (!StringUtils.equals(order.getDefault02(), "2")) {
                // 服务商申报
                // 清关开始
                cbOrderProducer.send(
                        MsgType.CB_ORDER_220,
                        String.valueOf(String.valueOf(order.getId())),
                        order.getOrderNo()
                );
            }
            long costTime4 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "发送MQ4花费时间：" + costTime4;
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLog.setCostTime(costTime4);
            orderLog.setCostTimeMsg(costTimeMsg);
            orderLogService.create(orderLog);

        }else {
            // 回传接单失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_215.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    //工具重推接单，只重推，不改变任何订单数据
    @Override
    public void confirmOrderByTools(CrossBorderOrder order) throws Exception {
        // 开始回传接单
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000)+""):DateUtil.formatDateTime(new Date()));
        request.setStatus("9");
        request.setVendor(vendor);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<EmptyResponse> response = dySupport.request(EmptyResponse.class);
        if (response.isSuccess()) {
            return;
        }else {
            // 回传接单失败
            throw new BadRequestException(response.getMessage());
        }
    }


    @Override
    public void confirmClearStartByTools(CrossBorderOrder order) throws Exception {
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("1");
        // 清关异常手动回传
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传清关开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传清关开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            return;
        }else {
            throw new BadRequestException(response.getMessage());
        }
    }


    @Override
    public void confirmClearStart(CrossBorderOrder order) throws Exception{
        // 处理抖音平台申报的订单，此时订单的状态为接单回传
        if (StringUtils.equals("2", order.getDefault02())) {
            if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_215.getCode().intValue())
                throw new BadRequestException("当前状态不允许回传清关开始：" + order.getId());
            order.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            order.setClearStartBackTime(new Timestamp(System.currentTimeMillis()));
            crossBorderOrderService.update(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLogService.create(orderLog);
            return;
        }
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_220.getCode().intValue()) {
            throw new BadRequestException("当前状态不允许回传清关开始：" + order.getId());
        }
        log.info("抖音开始回传清关开始：{}", order.getOrderNo());
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("1");
        // 清关异常手动回传
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传清关开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传清关开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            if (order.getStatus().intValue() == CBOrderStatusEnum.STATUS_220.getCode().intValue()) {
                // 如果当前状态为清关开始，回传成功后则可以改状态为清关开始回传，其他状态则不能更改状态
                order.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            }
            order.setClearStartBackTime(new Timestamp(System.currentTimeMillis()));
            crossBorderOrderService.update(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_225.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            if (StringUtil.contains(response.getMessage(), "服务商未接单成功")) {
                confirmOrderByTools(order);
            }
            throw new BadRequestException(response.getMessage());
        }
    }

    // 接单异常回传
    @Override
    public void confirmOrderErr(CrossBorderOrder order) throws Exception {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_999.getCode().intValue())
            throw new BadRequestException("此状态不能回传接单异常：" + order.getOrderNo());
        if (StringUtils.equals("1", order.getPreSell()))
            throw new BadRequestException("预售订单不能回传接单异常：" + order.getOrderNo());
        if (StringUtils.isEmpty(order.getFreezeReason()))
            throw new BadRequestException("冻结原因为空不能回传接单异常：" + order.getOrderNo());

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传接单异常,单号：{}", order.getOrderNo());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000)+""):DateUtil.formatDateTime(new Date()));
        request.setStatus("10");
        request.setVendor(vendor);

        ErrorInfo errorInfo =  new ErrorInfo();
        String freezeReason = order.getFreezeReason();
        if (StringUtils.contains(freezeReason, "库存")) {
            errorInfo.setErrorCode("ORDER002");
            errorInfo.setErrorMsg(freezeReason);
        }else if (StringUtils.contains(freezeReason, "编码")) {
            errorInfo.setErrorCode("ORDER004");
            errorInfo.setErrorMsg(freezeReason);
        }else {
            throw new BadRequestException("无可用类型回传接单异常：" + order.getOrderNo());
        }
        request.setError_info(errorInfo);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音回传接单异常,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传接单异常,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            order.setStatus(CBOrderStatusEnum.STATUS_201.getCode());
            crossBorderOrderService.update(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_201.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_201.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }

    }

    // 清关异常回传
    @Override
    public void confirmClearErr(String orderId) throws Exception {
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order == null) {
            throw new BadRequestException("订单不存在：" + orderId);
        }
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_225.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传清关异常：" + order.getOrderNo());
        }

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        log.info("抖音开始回传清关异常,单号：{}", order.getOrderNo());
        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("3");

        ErrorInfo errorInfo =  new ErrorInfo();
        String decMsg = order.getDeclareMsg();
        if (StringUtil.contains(decMsg, "[Code:1371")) {
            errorInfo.setErrorCode("CLEARANCE026");
            errorInfo.setErrorMsg(decMsg);
        }else if (StringUtil.contains(decMsg, "[Code:1313")) {
            //年额度超限
            errorInfo.setErrorCode("CLEARANCE002");
            errorInfo.setErrorMsg(decMsg);
        }else if (StringUtil.contains(decMsg, "人工审核")) {
            //人工审核
            errorInfo.setErrorCode("CLEARANCE013");
            errorInfo.setErrorMsg("[Code:1200;Desc:待人工审核]");
        }else if (StringUtil.contains(decMsg, "担保金额不足")) {
            //担保金额不足
            errorInfo.setErrorCode("CLEARANCE011");
            errorInfo.setErrorMsg("[Code:1311;Desc:担保金额不足,担保金额不足]");
        }else if (StringUtil.contains(decMsg, "系统异常")) {
            //系统异常
            errorInfo.setErrorCode("CLEARANCE025");
            errorInfo.setErrorMsg("[Code:1301;Desc:系统异常，请稍后重试]");
        }else {
            throw new BadRequestException("无对应的可回传异常类型");
        }
        request.setError_info(errorInfo);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音回传清关异常,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传清关异常,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            order.setStatus(CBOrderStatusEnum.STATUS_227.getCode());
            crossBorderOrderService.update(order);
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_227.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }

    }

    @Override
    public void confirmClearSuccessByTools(CrossBorderOrder order) throws Exception {

        if (StringUtils.equals("2", order.getDefault02())) {
            // 平台申报的，不用回传清关完成，直接推送富勒
            order.setClearSuccessBackTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(order);

            wmsSupport.pushOrder(order);
            return;
        }
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传清关完成,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.formatDateTime(order.getClearSuccessTime()));
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("2");

        // 清关异常手动回传
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传清关完成,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传清关完成,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            return;
        }else {
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void confirmClearSuccess(CrossBorderOrder order) throws Exception{
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_230.getCode().intValue()
                && order.getStatus().intValue() != CBOrderStatusEnum.STATUS_777.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传清关完成：" + order.getOrderNo());
        }
        Integer totalNum = 0;
        log.info("数量比对开始：{}，{}, {}", order.getOrderNo(), totalNum);
        List<CrossBorderOrderDetails> itemList = order.getItemList();
        if (CollectionUtils.isEmpty(itemList))
            throw new BadRequestException("明细为空");
        for (CrossBorderOrderDetails details : itemList) {
            log.info("明细数量：{}，{}", order.getOrderNo(), details.getQty());
            totalNum = totalNum + Integer.valueOf(details.getQty());
        }
        Integer orderTotalNum = Integer.valueOf(order.getTotalNum());
        log.info("数量比对开始：{}，{}, {}", order.getOrderNo(), totalNum, orderTotalNum);
        if (totalNum.intValue() != orderTotalNum.intValue()) {
            // 临时逻辑，中通停发的单子，直接冻结
            order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
            order.setFreezeReason("冻结");
            crossBorderOrderService.update(order);
            OrderLog freezeLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    "冻结"
            );
            orderLogService.create(freezeLog);
            return;
        }

        if ( StringUtils.equals("2", order.getDefault02())) {
            if (!StringUtils.equals("1", order.getLogisticsFourPl())) {
                // 非配4PL和抖音申报的单子才做处理
                // 查询运单相关信息
                DouyinMailMark douyinMailMark = douyinMailMarkService.queryByOrderNo(order.getOrderNo());
                if (douyinMailMark == null || StringUtils.isBlank(douyinMailMark.getLogisticsNo()))
                    throw new BadRequestException("抖音订单未获取到运单：" + order.getId());
                order.setSupplierId(Long.valueOf(douyinMailMark.getSupplierId()));
                order.setLogisticsNo(douyinMailMark.getLogisticsNo());
                order.setAddMark(douyinMailMark.getAddMark());
            }
            confirmOrderByTools(order);// 回传一个接单

            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            String declareNo = kjgSupport.getDeclareNoByOrderNo(order.getOrderNo(), order.getOrderForm(), clearCompanyInfo.getKjgUser(), clearCompanyInfo.getKjgKey());
            order.setDeclareNo(declareNo);
            // 平台申报的，不用回传清关完成，直接推送富勒
            order.setClearSuccessTime(new Timestamp(System.currentTimeMillis()));
            order.setClearSuccessBackTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(order);

            wmsSupport.pushOrder(order);
            return;
        }

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传清关完成,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("2");

        // 清关异常手动回传
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传清关完成,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传清关完成,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 抖音订单直接由ERP推到富勒
            wmsSupport.pushOrder(order);

            // 回传成功
            order.setClearSuccessBackTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(order);

            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_235.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            if (StringUtil.contains(response.getMessage(), "当前节点为【服务商拉单成功】")) {
                confirmClearStartByTools(order);
            }
            throw new BadRequestException(response.getMessage());
        }
    }

    // 回传拣货开始
    @Override
    public void confirmPickStart(CrossBorderOrder order) throws Exception {
        log.info("抖音开始回传拣货开始,单号：{}", order.getOrderNo());

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("6");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000-3*60)+""):DateUtil.formatDateTime(new Date(System.currentTimeMillis()-3*60*1000)));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传拣货开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传拣货开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_236.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

            // 3秒后回传拣货完成
            cbOrderProducer.delaySend(
                    MsgType.CB_ORDER_2361,
                    String.valueOf(order.getId()),
                    order.getOrderNo(),
                    180000
            );
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_236.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            if (StringUtil.contains(response.getMessage(), "当前节点为【开始清关】")) {
                confirmClearSuccessByTools(order);
            }
            if (StringUtil.contains(response.getSubMsg(), "当前节点为【推单成功】")) {
                confirmOrderByTools(order);
            }
            throw new BadRequestException(response.getMessage());
        }
    }

    // 回传拣货完成
    @Override
    public  void confirmPickEnd(CrossBorderOrder order) throws Exception {
        log.info("抖音开始回传拣货完成,单号：{}", order.getOrderNo());

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("7");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000)+""):DateUtil.formatDateTime(new Date()));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传拣货完成,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传拣货完成,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            order.setSendPickFlag("1");
            crossBorderOrderService.update(order);

            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_2361.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_2361.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public  void confirmPackByTool(CrossBorderOrder order) throws Exception {
        log.info("抖音开始回传打包,单号：{}", order.getOrderNo());
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("2");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((order.getPackTime().getTime()/1000)+""):DateUtil.formatDateTime(order.getPackTime()));
        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传打包,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传打包,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            return;
        }else {
            throw new BadRequestException(response.getMessage());
        }
    }

    // 回传打包
    @Override
    public  void confirmPack(CrossBorderOrder order) throws Exception {
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传打包：" + order.getOrderNo());
        }

        log.info("抖音开始回传打包,单号：{}", order.getOrderNo());
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("2");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((order.getPackTime().getTime()/1000)+""):DateUtil.formatDateTime(order.getPackTime()));
        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传打包,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传打包,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()
                || StringUtils.contains(response.getMessage(), "当前节点为【出库完成】")
                || StringUtils.contains(response.getMessage(), "当前节点为【国内物流已揽收】")) {
            // 打包完成回传成功
            order.setPackBackTime(new Timestamp(System.currentTimeMillis()));
            crossBorderOrderService.update(order);

            // 500ms后回传出库
            cbOrderProducer.delaySend(
                    MsgType.CB_ORDER_245,
                    String.valueOf(order.getId()),
                    order.getOrderNo(),
                    500
            );

            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_237.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

        }else {
            // 打包回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_237.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            // 出现状态缺失，重推一下缺失的状态
            if (StringUtil.contains(response.getMessage(), "当前节点为【订单开始拣货】")) {
                confirmPickEnd(order);
            }
            if (StringUtil.contains(response.getMessage(), "当前节点为【清关完成】")) {
                confirmPickStart(order);
                confirmPickEnd(order);
            }
            if (StringUtil.contains(response.getMessage(), "当前状态操作时间")) {
                // 时间节点异常
                order.setPackTime(new Timestamp(System.currentTimeMillis()));
                order.setWeighingTime(new Timestamp(System.currentTimeMillis()));
                crossBorderOrderService.update(order);
            }
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void confirmDeliver(CrossBorderOrder order)  throws Exception{
        if (order.getStatus().intValue() != CBOrderStatusEnum.STATUS_240.getCode().intValue()) {
            throw new BadRequestException("此状态不能回传出库：" + order.getOrderNo());
        }

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传出库,单号：{}", order.getOrderNo());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("3");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((order.getWeighingTime().getTime()/1000)+""):DateUtil.formatDateTime(order.getWeighingTime()));
        if (StringUtil.equals(order.getFourPl(),"1")){
            if (StringUtils.isEmpty(order.getMaterialCode())) {
                throw new BadRequestException("4PL订单无包材信息");
            }
            //4PL订单并且包材编码不为空
            CBOrderOperateRequest.Package apackage=new CBOrderOperateRequest.Package();
            List<CBOrderOperateRequest.Package.PackingMaterials>packingMaterials=new ArrayList<>();// 包材列表
            CBOrderOperateRequest.Package.PackingMaterials packingMaterial=new CBOrderOperateRequest.Package.PackingMaterials();
            PackageInfo packageInfo = packageInfoService.queryByPackageCode(order.getMaterialCode());
            packingMaterial.setPackingMaterialCode(packageInfo.getPackageCode());
            packingMaterial.setPackingMaterialName(packageInfo.getPackageName());
            packingMaterial.setPackingMaterialNum(1);
            packingMaterials.add(packingMaterial);

            List<OrderMaterial> orderMaterialList = orderMaterialService.queryByOrderId(order.getId());
            if (CollectionUtils.isNotEmpty(orderMaterialList)) {
                List<CBOrderOperateRequest.Package.ConsumablesMaterials> consumablesMaterials = new ArrayList<>();// 耗材列表
                for (OrderMaterial orderMaterial : orderMaterialList) {
                    CBOrderOperateRequest.Package.ConsumablesMaterials consumablesMaterial =new CBOrderOperateRequest.Package.ConsumablesMaterials();
                    consumablesMaterial.setConsumablesCode(orderMaterial.getMaterialCode());
                    consumablesMaterial.setConsumablesName(orderMaterial.getMaterialName());
                    consumablesMaterial.setConsumablesNum(orderMaterial.getQty());
                    consumablesMaterials.add(consumablesMaterial);
                }
                apackage.setConsumablesMaterials(consumablesMaterials);
            }

            apackage.setPackingMaterials(packingMaterials);
            apackage.setPackageWeight(Long.parseLong(String.format("%.0f",Double.parseDouble(order.getPackWeight())*1000)));
            request.setApackage(apackage);
        }
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        CBOrderOperateRequest.Transportation trans = new CBOrderOperateRequest.Transportation();
        trans.setDomesticCarrier(logisticsInfo == null?"zhongtongguoji":logisticsInfo.getDefault01());
        trans.setDomesticTransNo(order.getLogisticsNo());
        CBOrderListMain.Address address = new CBOrderListMain.Address();
        CBOrderListMain.AddressNameId provinced = new CBOrderListMain.AddressNameId();
        provinced.setName("浙江省");
        CBOrderListMain.AddressNameId city = new CBOrderListMain.AddressNameId();
        city.setName("宁波市");
        CBOrderListMain.AddressNameId town = new CBOrderListMain.AddressNameId();
        town.setName("北仑区");
        address.setProvince(provinced);
        address.setCity(city);
        address.setTown(town);
        address.setDetail("港东大道29号");
        trans.setDomesticShipAddress(JSONObject.toJSONString(address));
        request.setTransportation(trans);
        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传出库,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传出库,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()
                || StringUtils.contains(response.getMessage(), "当前节点为【出库完成】")
                || StringUtils.contains(response.getMessage(), "当前节点为【国内物流已揽收】")) {
            // 出库回传成功
            order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
            order.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
            crossBorderOrderService.update(order);
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

            // 扣除保证金
//            depositService.change(order);

            cbOrderProducer.delaySend(
                    MsgType.CB_ORDER_WMS,
                    String.valueOf(order.getId()),
                    order.getOrderNo(),
                    30000
            );

        }else {
            if (StringUtil.contains(response.getMessage(), "当前节点为【订单拣货完成】")) {
                confirmPack(order);
            }
            // 出库回传失败
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_245.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException("请求抖音出库失败：" + response.getMessage() + "，请一分钟后尝试重新扫描出库");
        }
    }


    // 拦截成功
    @Override
    public void confirmInterceptionSucc(CrossBorderOrder order) throws Exception {
        log.info("抖音开始回传拣货开始,单号：{}", order.getOrderNo());

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("11");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000-3*60)+""):DateUtil.formatDateTime(new Date(System.currentTimeMillis()-3*60*1000)));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传取消拦截成功开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传取消拦截成功开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }
    }

    // 拦截失败
    @Override
    public void confirmInterceptionErr(CrossBorderOrder order) throws Exception {
        log.info("抖音开始回传拣货开始,单号：{}", order.getOrderNo());

        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());

        CBOrderOperateRequest request = new CBOrderOperateRequest();
        request.setOrderId(order.getOrderNo());
        request.setStatus("12");
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?((System.currentTimeMillis()/1000-3*60)+""):DateUtil.formatDateTime(new Date(System.currentTimeMillis()-3*60*1000)));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音回传取消拦截失败开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传取消拦截失败开始,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);

        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }
    }

    // 查询订单状态
    @Override
    public Integer getStatus(CrossBorderOrder order) throws Exception {
        try {
            ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息,请联系技术配置：" + order.getOrderNo());
            }
            if (StringUtils.equals("1", order.getFourPl())) {
                CBOrderGetTradeStatusRequest request = new CBOrderGetTradeStatusRequest();
                request.setOrderId(order.getOrderNo());
                request.setOpenAppKey(shopToken.getClientId());
                request.setVendor(vendor);
                dySupport.setApiParam(request);
                DYCommonResponse <DYGetOrderStatusResponse> response = dySupport.request(DYGetOrderStatusResponse.class);
                return response.getData().getOrderStatus();
            }else {
                CBOrderGetOrderStatusRequest request = new CBOrderGetOrderStatusRequest();
                request.setOrderId(order.getOrderNo());
                request.setVendor(vendor);
                request.setOpenAppKey(shopToken.getClientId());
                request.setOpenShopId(order.getPlatformShopId());
                dySupport.setAccessToken(shopToken.getAccessToken());
                dySupport.setApiParam(request);
                DYCommonResponse <DYGetOrderStatusResponse> response = dySupport.request(DYGetOrderStatusResponse.class);
                return response.getData().getOrderStatus();
            }
        }catch (Exception e) {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(order.getStatus()),
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
            throw e;
        }

    }

    // 将面单寄件人信息推送到WMS
    @Override
    public void pushPrintInfoToWms(String orderId) {
        String customersCode = "el9999";
        String key = "d751a440141c4a65ba2e9bd45f3f6b78";
        CrossBorderOrder order = crossBorderOrderService.queryById(Long.valueOf(orderId));
        if (order == null)
            throw new BadRequestException("订单信息不存在");
        ShopInfoDto shopInfoDto = shopInfoService.findByIdDto(order.getShopId());
        String shopName = shopInfoDto.getName();
        String phone = shopInfoDto.getContactPhone();
        if (StringUtil.isNotBlank(shopName) && StringUtil.isNotBlank(phone)) {
            cn.hutool.json.JSONObject jsonObject = new cn.hutool.json.JSONObject();
            jsonObject.putOnce("orderId", order.getOrderNo());
            jsonObject.putOnce("shopName", shopName);
            jsonObject.putOnce("shopPhone", phone);
            String data = EncryptUtils.encryptDexHex(jsonObject.toString(), key);
            Map<String, Object> params = new HashMap<>();
            params.put("customersCode", customersCode);
            params.put("data", data);
            String post = HttpUtil.post("http://pre.fl56.net/api/el-admin/save-sender-info", params);// 不记录是否成功
            System.out.println(post);
        }
    }

    // 保存订单
    @Override
    public void createOrder(String body) {
        long start = new Date().getTime();
        String costTimeMsg = "";
        try {
            CBOrderListMain orderMain = JSON.parseObject(body, CBOrderListMain.class);
//            if (orderMain.getStatus().intValue() > 102) {
//                // 101支付单申报成功、102支付单申报异常、100支付单申报异常
//                // 100\101\102这些状态才需要保存
//                return;
//            }
            // 查询确认是否已保存过
            CrossBorderOrder exists = crossBorderOrderService.queryByOrderNo(orderMain.getOrderId());
            if (exists != null) {
                // 已保存过
                return;
            }
            long costTime1 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "根据单号查询数据库花费时间：" + costTime1;
            // 将订单保存进数据库
            CrossBorderOrder order = new CrossBorderOrder();

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");
            order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());

            order.setPlatformShopId(orderMain.getShopId());
            order.setUpStatus(orderMain.getStatus()+"");
            order.setIsWave("0");
            order.setIsPrint("0");
            order.setSendPickFlag("0");
            order.setPlatformStatus(2);
            order.setDefault01("0");
            order.setDefault05("1");
            order.setIsLock("0");// 未锁单
            order.setLogisticsCode("73698071-8");
            order.setDefault04("空间变换");
            order.setLogisticsStatus(1);
            order.setDefault02(StringUtils.equals("1", orderMain.getOrderDeclare().toString()) ? "1" : "2"); //1服务商申报、2平台申报

            ShopToken shopToken = shopTokenService.queryByPaltShopId(orderMain.getShopId());
            ClearCompanyInfo clearCompanyInfo = null;
            if (shopToken != null) {
                ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
                if (shopInfoDto != null) {
                    order.setCustomersId(shopInfoDto.getCustId());
                    order.setShopId(shopInfoDto.getId());
                    order.setOrderForm(shopInfoDto.getKjgCode());
                    clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
                }else {
                    order.setCustomersId(orderMain.getCustId());
                    order.setShopId(orderMain.getlShopId());
                }
            }else {
                order.setCustomersId(orderMain.getCustId());
                order.setShopId(orderMain.getlShopId());
            }
            long costTime2 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "根据店铺ID查询花费店铺信息时间：" + costTime2;

            order.setOrderNo(orderMain.getOrderId());
            order.setCrossBorderNo(orderMain.getOrderId());
            order.setOrderCreateTime(new Timestamp(Long.valueOf(orderMain.getCreateTime())));
            order.setEbpCode(orderMain.getEbpCode());
            order.setEbpName(orderMain.getEbpName());

            // 这些数据一期时间紧先写死
            order.setPlatformCode("DY");
            order.setOrderForm("1506");
            order.setDisAmount(orderMain.getDiscount().divide(new BigDecimal(100)).toString());
            order.setPostFee(orderMain.getFreight().divide(new BigDecimal(100)).toString());
            order.setPayment(orderMain.getActuralPaid().divide(new BigDecimal(100)).toString());// 实际支付金额
            order.setBuyerAccount(orderMain.getBuyerRegNo());
            order.setBuyerPhone(orderMain.getBuyerTelephone());
            order.setBuyerIdNum(orderMain.getBuyerIdNumber());
            order.setBuyerName(orderMain.getBuyerName());
            order.setPayTime(new Timestamp(Long.valueOf(orderMain.getPayTime())));
            BigDecimal taxTotal = orderMain.getTaxTotal().divide(new BigDecimal(100));
            order.setTaxAmount(taxTotal.toString());// 总税费
            order.setPaymentNo(orderMain.getPayTransactionId());
            order.setOrderSeqNo(orderMain.getPayTransactionId());
            order.setBooksNo("T3105W000159");// 先写死
            order.setPreSell(orderMain.getPreSaleType());
            order.setExpDeliverTime(new Timestamp(Long.valueOf(orderMain.getExpShipTime())));
            if (StringUtil.isNotEmpty(orderMain.getPayCode())) {
                switch (orderMain.getPayCode()) {
                    case "4403169D3W":
                        order.setPayCode("13");//财付通
                        break;
                    case "31222699S7":
                        order.setPayCode("02");//支付宝
                        break;
                    case "4201960AED":
                        order.setPayCode("66");//支付宝
                        break;
                    default:
                        order.setPayCode(orderMain.getPayCode());
                }
            }
            order.setConsigneeName(orderMain.getConsignee());
            if (orderMain.getAddress() != null) {
                order.setConsigneeAddr(orderMain.getAddress().getProvince().getName() + " " +
                        orderMain.getAddress().getCity().getName() + " " +
                        orderMain.getAddress().getTown().getName() + " " +
                        orderMain.getAddress().getStreet().getName() + " " +
                        orderMain.getAddress().getDetail());
                order.setConsigneeTel(orderMain.getConsigneeTelephone());
                order.setProvince(orderMain.getAddress().getProvince().getName());
                order.setCity(orderMain.getAddress().getCity().getName());
                order.setDistrict(orderMain.getAddress().getTown().getName());
            }

            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";

            // 处理明细数据
            List<CrossBorderOrderDetails> list = new ArrayList<>();
            List<CBOrderListChild> itemList = orderMain.getItemList();
            if (CollectionUtils.isNotEmpty(itemList)) {
                for (CBOrderListChild item : itemList) {
                    if (item.getItemNo()!=null){
                        // 有些商家瞎填货号有空格，直接去除
                        item.setItemNo(StringUtil.removeEscape(item.getItemNo()));
                    }

                    if (StringUtil.equals("P31051510212592301/P31051510212592296", item.getItemNo())) {
                        // 克瑞恩的组合包，现在抖音不支持组合形式，先写死，等后面抖音出方案
                        // 一拆二，难搞
                        CrossBorderOrderDetails details1 = new CrossBorderOrderDetails();
                        BigDecimal tax = taxTotal.divide(new BigDecimal(itemList.size()),2,BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(2));
                        // 一期不做商品映射关系
                        details1.setOrderNo(order.getOrderNo());
                        details1.setFontGoodsName("黛珂牛油果乳液300ML*1");
                        details1.setGoodsCode("P31051510212592296");
                        details1.setGoodsNo("P31051510212592296");
                        details1.setQty(String.valueOf(item.getQty()));
                        details1.setTaxAmount(tax.toString());//
                        details1.setPayment(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).add(tax).toString());// 商品支付总价
                        details1.setDutiableValue(new BigDecimal(item.getPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).toString()); // 不含税(申报)单价
                        details1.setDutiableTotalValue(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).toString());
                        list.add(details1);

                        CrossBorderOrderDetails details2 = new CrossBorderOrderDetails();
                        details2.setOrderNo(order.getOrderNo());
                        details2.setFontGoodsName("黛珂紫苏精华水300ML*1");
                        details2.setGoodsCode("P31051510212592301");
                        details2.setGoodsNo("P31051510212592301");
                        details2.setQty(String.valueOf(item.getQty()));
                        details2.setTaxAmount(tax.toString());//
                        details2.setPayment(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).add(tax).toString());// 商品支付总价
                        details2.setDutiableValue(new BigDecimal(item.getPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).toString()); // 不含税(申报)单价
                        details2.setDutiableTotalValue(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(2)).divide(new BigDecimal(100)).toString());
                        list.add(details2);
                    }else {
                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        BaseSku baseSku = baseSkuService.queryByGoodsNo(item.getItemNo());
                        if (baseSku == null || order.getShopId().intValue() != baseSku.getShopId().intValue()) {
                            BaseSku baseSku1 = baseSkuService.queryByGoodsCodeAndShopId(item.getItemNo(), order.getShopId());
                            if (baseSku1 != null) {
                                details.setGoodsId(baseSku1.getId());
                                details.setGoodsCode(baseSku1.getGoodsCode());
                                details.setBarCode(baseSku1.getBarCode());
                                details.setGoodsNo(baseSku1.getGoodsNo());
                            }else {
                                needFreeze = true;
                                freezeReason = item.getItemNo() + "skuId商品编码错误:" + item.getItemNo();
                            }
                        }else {
                            details.setGoodsId(baseSku.getId());
                            details.setGoodsCode(baseSku.getGoodsCode());
                            details.setBarCode(baseSku.getBarCode());
                        }
                        BigDecimal tax = taxTotal.divide(new BigDecimal(itemList.size()),2,BigDecimal.ROUND_HALF_UP);
                        // 一期不做商品映射关系
                        details.setOrderNo(order.getOrderNo());
                        details.setFontGoodsName(item.getItemName());
                        if (StringUtil.isBlank(details.getGoodsCode())) {
                            details.setGoodsCode(item.getItemNo());
                        }
                        if (StringUtil.isBlank(details.getGoodsNo())) {
                            details.setGoodsNo(item.getItemNo());
                        }
                        details.setQty(String.valueOf(item.getQty()));
                        details.setTaxAmount(tax.toString());
                        details.setPayment(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(100)).add(tax).toString());// 商品支付总价
                        details.setDutiableValue(new BigDecimal(item.getPrice()).divide(new BigDecimal(100)).toString()); // 不含税(申报)单价
                        details.setDutiableTotalValue(new BigDecimal(item.getTotalPrice()).divide(new BigDecimal(100)).toString());
                        list.add(details);
                        // 查询商品库存信息，无库存则冻结订单
                        if (clearCompanyInfo != null) {
                            Integer qty = wmsSupport.queryInventoryBySku(item.getItemNo(), clearCompanyInfo.getCustomsCode());
                            if (qty != null && qty.intValue() == 0 && !needFreeze) {
                                needFreeze = true;
                                freezeReason =  "WMS库存冻结失败商品可用库存不足,[skuId:]" + item.getItemNo() + "购买数量：" + item.getQty()+",可用库存0";
                            }
                        }

                    }
                }
            }
            long costTime3 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "订单明细处理花费时间：" + costTime3;
            order.setItemList(list);
            CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
            long costTime4 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "保存订单信息花费时间花费时间：" + costTime4;

            Config specialAddr = configService.queryByK("SPECIAL_ADDR");
            if (specialAddr != null && order.getConsigneeAddr().contains(specialAddr.getV())) {
                needFreeze = true;// 特殊地址打标冻结
                mailSupport.sendMail("VIP订单", "luobin@fl56.net", "VIP订单：" + order.getOrderNo());
            }
            // 冻结逻辑
            if (needFreeze) {
                order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                order.setFreezeReason(freezeReason);
                crossBorderOrderService.update(order);
                OrderLog freezeLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        freezeReason
                );
                orderLogService.create(freezeLog);
            }else {
                // 抖音不hold预售单
//                if(!StringUtil.equals("1", orderDto.getPreSell())) {
//                    cbOrderProducer.send(
//                            MsgType.CB_ORDER_215,
//                            String.valueOf(orderDto.getId()),
//                            order.getOrderNo()
//                    );
//                }
                cbOrderProducer.send(
                        MsgType.CB_ORDER_215,
                        String.valueOf(orderDto.getId()),
                        order.getOrderNo()
                );
            }


            long costTime5 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "冻结发送MQ消息花费时间：" + costTime5;

            long costTime6 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "完成时间花费时间：" + costTime6;
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setCostTime(costTime6);
            orderLog.setCostTimeMsg(costTimeMsg);
            orderLogService.create(orderLog);
        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    Long.valueOf(0),
                    String.valueOf(0),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    body,
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            e.printStackTrace();
        }

    }

    //刷新TOKEN
    @Override
    public void refreshToken(ShopToken shopToken) throws Exception {
        CommonTokenRefreshRequest request = new CommonTokenRefreshRequest();
        request.setRefreshToken(shopToken.getRefreshToken());
        dySupport.setApiParam(request);

        DYCommonResponse<CommonTokenResponse> response = dySupport.request(CommonTokenResponse.class);
        log.info("刷新抖音token:{}", response);
        if (StringUtil.equals(response.getStatusCode(), "0")) {
            CommonTokenResponse data = response.getData();
            shopToken.setAccessToken(data.getAccessToken());
            shopToken.setRefreshToken(data.getRefreshToken());
            shopToken.setTokenTime(data.getExpireIn() + System.currentTimeMillis() / 1000);
            Date time = new Date(System.currentTimeMillis() + 14L * 24L * 3600L * 1000L);
            shopToken.setRefreshTime(new Timestamp(time.getTime()));
            shopTokenService.update(shopToken);
        }else {
            throw new BadRequestException("刷新token失败" + response.getMessage());
        }
    }

    @Override
    public void getToken(String code) throws Exception {
        CommonTokenCreateRequest request = new CommonTokenCreateRequest();
        request.setCode(code);
        dySupport.setApiParam(request);

        DYCommonResponse<CommonTokenResponse> response = dySupport.request(CommonTokenResponse.class);
        log.info("获取抖音token:{}", response);
        if (StringUtil.equals(response.getStatusCode(), "0")) {
            CommonTokenResponse data = response.getData();
            ShopToken shopToken = shopTokenService.queryByPaltShopId(data.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("shopToken has not init: " + data.getShopId());
            }
            shopToken.setClientId(appKey);
            shopToken.setClientSecret(appSecret);
            shopToken.setCode(code);
            shopToken.setCodeGetTime(new Timestamp(System.currentTimeMillis()));
            shopToken.setAccessToken(data.getAccessToken());
            shopToken.setRefreshToken(data.getRefreshToken());
            shopToken.setShopName(data.getShopName());
            shopToken.setTokenTime(data.getExpireIn() + System.currentTimeMillis() / 1000);
            Date time = new Date(System.currentTimeMillis() + 14L * 24L * 3600L * 1000L);
            shopToken.setRefreshTime(new Timestamp(time.getTime()));
            shopTokenService.update(shopToken);
        }else {
            throw new BadRequestException(response.getMessage());
        }
    }


    public void checkToken(ShopToken shopToken) {
        if (StringUtil.isBlank(shopToken.getAccessToken())) {
            throw new BadRequestException("店铺没授权Token,请联系商家授权：" + shopToken.getId());
        }
        if (shopToken.getTokenTime()-3600<System.currentTimeMillis()/1000) {
            // 一个小时时失效
            if (StringUtil.isBlank(shopToken.getRefreshToken())) {
                throw new BadRequestException("店铺没刷新Token,请联系商家授权：" + shopToken.getId());
            }else if (shopToken.getRefreshTime().getTime()<System.currentTimeMillis()) {
                throw new BadRequestException("刷新Token已失效,请联系商家授权：" + shopToken.getId());
            }
            try {
                refreshToken(shopToken);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void crossborderMsgPush(CrossborderMsgPush crossborderMsgPush) throws Exception {
        if (StringUtil.isBlank(crossborderMsgPush.getMsgType()))
            throw new BadRequestException("msg_type必填");
        switch (crossborderMsgPush.getMsgType()){
            case "orderPush":
                orderPush(crossborderMsgPush.getOrderPush());
                break;
            case "order_info_notify":
                orderInfoNotify(crossborderMsgPush.getOrderInfoNotify());
                break;
            case "inbound_bill_push":
                inboundBillPush(crossborderMsgPush.getInboundBillPush());
                break;
            case "lading_bill_push":
                ladingBillPush(crossborderMsgPush.getLadingBillPush());
                break;
            case "order_interception_push":
                orderInterceptionPush(crossborderMsgPush.getOrderInterceptionPush());
                break;
            case "order_interception_notify":
                orderInterceptionNotify(crossborderMsgPush.getOrderInterceptionNotify());
                break;
            case "take_logistics_info_push":
                takeLogisticsInfoPush(crossborderMsgPush.getTakeLogisticsInfoPush());
                break;
            case "tally_order_review":
                tallyOrderReview(crossborderMsgPush.getTallyOrderReview());
                break;
            case "reverse_inbound_order":
                reverseInboundOrder(crossborderMsgPush.getReverseInboundOrder());
                break;
            case "reverse_outbound_order":
                reverseOutboundOrder(crossborderMsgPush.getReverseOutboundOrder());
                break;
            case "create_warehouse_fee_order":
                createWarehouseFeeOrder(crossborderMsgPush.getCreateWarehouseFeeOrder());
                break;

            case "notify_adjust_result":
                notifyAdjustResult(crossborderMsgPush.getNotifyAdjustResult());
                break;
            case "push_sale_return_order":// 推送销退单
                createSaleReturnOrder(crossborderMsgPush.getSaleReturnOrder());
                break;
            default:
                throw new IllegalStateException("不规范的值: " + crossborderMsgPush.getMsgType());
        }
    }

    private void notifyAdjustResult(NotifyAdjustResult notifyAdjustResult) {
        cbOrderProducer.send(
                MsgType.DY_NOTIFY_ADJUST_RESULT,
                JSONObject.toJSONString(notifyAdjustResult),
                notifyAdjustResult.getInventoryAdjustNo());
    }

    @Override
    public void notifyAdjustResult(String body){
        NotifyAdjustResult notifyAdjustResult = JSONObject.parseObject(body,NotifyAdjustResult.class);
        if (notifyAdjustResult.getAdjustReason()==1){
            DyStockTaking dyStockTaking = dyStockTakingService.queryByIdempotentNo(notifyAdjustResult.getInventoryAdjustNo());
            if (dyStockTaking==null)
                throw new BadRequestException("幂等单号"+notifyAdjustResult.getInventoryAdjustNo()+"不存在");
            if (StringUtil.equals(dyStockTaking.getStatus(),"1")){
                if (notifyAdjustResult.getApproveResult()==1){
                    dyStockTaking.setStatus("2");
                }else {
                    dyStockTaking.setStatus("3");
                    dyStockTaking.setRejectReason(notifyAdjustResult.getRejectReason());
                }
                dyStockTaking.setSucTime(new Timestamp(notifyAdjustResult.getApproveTime()*1000L));
                dyStockTakingService.update(dyStockTaking);
            }
        }else if (notifyAdjustResult.getAdjustReason() == 2){
            DyStockTransform dyStockTransform = dyStockTransformService.queryByIdempotentNo(notifyAdjustResult.getInventoryAdjustNo());
            if (dyStockTransform==null)
                throw new BadRequestException("幂等单号"+notifyAdjustResult.getInventoryAdjustNo()+"不存在");
            if (StringUtil.equals(dyStockTransform.getStatus(),"1")){
                if (notifyAdjustResult.getApproveResult()==1){
                    dyStockTransform.setStatus("2");
                }else {
                    dyStockTransform.setStatus("3");
                    dyStockTransform.setRejectReason(notifyAdjustResult.getRejectReason());
                }
                dyStockTransform.setSucTime(new Timestamp(notifyAdjustResult.getApproveTime()*1000L));
                dyStockTransformService.update(dyStockTransform);
            }
        }
    }

    private void createWarehouseFeeOrder(CreateWarehouseFeeOrder createWarehouseFeeOrder) {
        cbOrderProducer.send(
                MsgType.DY_CREATE_WAREHOUSE_FEE_ORDER,
                JSONObject.toJSONString(createWarehouseFeeOrder),
                createWarehouseFeeOrder.getWsStoreNo());
    }

    @Override
    public void createWarehouseFeeOrder(String body){
        CreateWarehouseFeeOrder order = JSONObject.parseObject(body,CreateWarehouseFeeOrder.class);
        DyCangzuFee dyCangzuFee = dyCangzuFeeService.queryByWsStoreNo(order.getWsStoreNo());
        if (dyCangzuFee == null){
            dyCangzuFee = new DyCangzuFee(order);
            dyCangzuFeeService.create(dyCangzuFee);
        }
        Config config = configService.queryByK("MQ_AUTO_CZ_CALLBACK");
        if ("1".equals(config.getV())){
            createWarehouseFeeOrderPush(dyCangzuFee);
            dyCangzuFee.setIsPush("1");
            dyCangzuFeeService.update(dyCangzuFee);
        }
    }

    @Override
    public void createWarehouseFeeOrderPush(DyCangzuFee dyCangzuFee){
        // TODO: 2023/2/15 抖音仓租计费回传
        NotifyWarehouseFeeOrderRequest request = new NotifyWarehouseFeeOrderRequest();
        request.setWsStoreNo(dyCangzuFee.getWsStoreNo());
        request.setFeeDate(dyCangzuFee.getFeeDate());
        request.setVendor(vendor);
        request.setWarehouseCode(dyCangzuFee.getWarehouseCode());
        List<NotifyWarehouseFeeOrderRequest.Detail>details = new ArrayList<>();
        if (dyCangzuFee.getOwnerId()!=null || dyCangzuFee.getShopId()!=null){
            //获取shopToken拿到shopId，再根据shopId拿到所对应的指定日期结余库存
            ShopToken shopToken = null;
            if (dyCangzuFee.getOwnerId()!=null)
                shopToken = shopTokenService.queryByPaltShopId(dyCangzuFee.getOwnerId()+"");
            if (shopToken==null&&dyCangzuFee.getShopId()!=null)
                shopToken=shopTokenService.queryByPaltShopId(dyCangzuFee.getShopId()+"");
            if (shopToken!=null){
                dySupport.setAccessToken(shopToken.getAccessToken());
                //查询计费日期-1天的结余库存
                List<DailyStock>dailyStockList = dailyStockService.queryByShopIdAndDayTime(shopToken.getShopId(),DateUtils.format(new Date((dyCangzuFee.getFeeDate()-24*3600)*1000L),"yyyyMMdd"));
                addNotifyWarehouseFeeOrderDetail(details,dailyStockList,dyCangzuFee);
            }
        }else {
            //没有提供店铺code，默认回传抖音4PL所有库存的库龄
            List<ShopInfo>shopInfos = shopInfoService.queryByPlafCode("DY");
            if (CollectionUtils.isNotEmpty(shopInfos)){
                for (ShopInfo shopInfo : shopInfos) {
                    List<DailyStock>dailyStockList = dailyStockService.queryByShopIdAndDayTime(shopInfo.getId(),DateUtils.format(new Date(dyCangzuFee.getFeeDate()*1000L),"yyyyMMdd"));
                    addNotifyWarehouseFeeOrderDetail(details,dailyStockList,dyCangzuFee);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(details)){
            request.setNeedCharge(true);
            request.setDetailList(details);
        }else
            request.setNeedCharge(false);
        dySupport.setApiParam(request);
        DYCommonResponse<EmptyResponse>response;
        try {
            response=dySupport.request(EmptyResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        if (!response.isSuccess())
            throw new BadRequestException(response.getMsg()+":"+response.getSubMsg());
    }

    private void addNotifyWarehouseFeeOrderDetail(List<NotifyWarehouseFeeOrderRequest.Detail> details, List<DailyStock> dailyStockList,DyCangzuFee order) {
        if (CollectionUtils.isNotEmpty(dailyStockList)){
            for (DailyStock dailyStock : dailyStockList) {
                if (StringUtil.isBlank(dailyStock.getInTime())||dailyStock.getLocation().indexOf("SORTATION")==0)
                    continue;//没有入库时间或者在拣货库位的
                BaseSku baseSku = baseSkuService.queryByGoodsNo(dailyStock.getGoodsNo());
                if (StringUtil.equals(baseSku.getWarehouseCode(), order.getWarehouseCode())&&!StringUtil.equals(baseSku.getIsGift(),"1")){
                    cn.hutool.json.JSONArray trsLogs = wmsSupport.trackDocNoByLotNumAndLocation(dailyStock.getBatchNo(),dailyStock.getLocation());
                    if (CollectionUtils.isEmpty(trsLogs)){
                      //查不到
                    } else {
                        String docNo;
                        //无法追踪相同批次号相同库位的多个入库单号的现存数量，故采用总数逐渐扣减平均值的办法
                        for (int i = 0; i < trsLogs.size(); i++) {
                            cn.hutool.json.JSONObject trsLog = trsLogs.getJSONObject(i);
                            if (trsLog == null)
                                throw new BadRequestException("批次号:" + dailyStock.getBatchNo() + ",库位:" + dailyStock.getLocation() + "的结果集" + (i + 1) + "处对象为空");
                            docNo = trsLog.getStr("docno");
                            if (docNo.indexOf("THTZ") == 0)
                                fillReturnInDetail(details, trsLog, baseSku, dailyStock,order);//退货提总单包含多个抖音下发的退货入库单，需要单独处理
                            else {
                                NotifyWarehouseFeeOrderRequest.Detail detail = new NotifyWarehouseFeeOrderRequest.Detail();
                                fillReturnDetailPackage(detail, baseSku);//填充箱规和抖音货号
                                if (docNo.indexOf("RK") == 0) {
                                    detail.setOrderType("PURCHASE_IN");
                                    InboundOrder inboundOrder = inboundOrderService.queryByOrderNo(docNo);
                                    if (StringUtil.isBlank(inboundOrder.getOutNo())) {
                                        //detail.setOrderNo(inboundOrder.getOrderNo());
                                    } else
                                        detail.setOrderNo(inboundOrder.getOutNo());
                                } else if (docNo.indexOf("AD") == 0) {
                                    detail.setOrderType("PURCHASE_IN");
                                    if (details.size() > 0) {
                                        detail.setOrderNo(details.get(details.size() - 1).getOrderNo());
                                    } else {
                                        detail.setOrderNo(docNo);
                                    }
                                }
                                int sumStock = dailyStock.getQty();
                                int avgStock = dailyStock.getQty() / trsLogs.size();
                                if (i < trsLogs.size() - 1) {
                                    sumStock -= avgStock;
                                    detail.setStockNum(avgStock);
                                } else {
                                    detail.setStockNum(sumStock);
                                }
                                detail.setStorageAge(DateUtils.differentDays(DateUtils.parseDate(dailyStock.getInTime()), new Date((order.getFeeDate() - 24 * 3600) * 1000)));
                                if (detail.getStorageAge()<=0)
                                    continue;
                                detail.setWarehouseAreaType(1);
                                if (details.size() > 0 && details.get(0).getOrderNo().indexOf("AD") == 0 && !detail.getOrderNo().contains("AD")) {
                                    details.get(0).setOrderNo(detail.getOrderNo());
                                }
                                if (avgStock == 0) {
                                    //当追踪到的入库单数量大于目标结余库存时计算出的平均值为0
                                    detail.setStockNum(sumStock);
                                    details.add(detail);
                                    break;
                                }
                                details.add(detail);
                            }
                        }
                    }
                    fillTransInDetail(details,order, baseSku, dailyStock);//调整单
                }
            }
        }
    }

    private void fillTransInDetail(List<NotifyWarehouseFeeOrderRequest.Detail> details,DyCangzuFee order, BaseSku baseSku, DailyStock dailyStock) {
        DyStockTransform transform = dyStockTransformService.queryByOccurrenceTimeAndLotNumAndLoction(dailyStock.getBatchNo(),dailyStock.getLocation(),dailyStock.getGoodsNo());
        if (transform==null)
            return;
        if (!StringUtil.equals(transform.getStatus(),"5"))
            return;
        for (DyStockTransformDetail transformDetail : transform.getItemList()) {
            if (StringUtil.equals(transformDetail.getGoodsNo(),dailyStock.getGoodsNo())){
                NotifyWarehouseFeeOrderRequest.Detail detail = new NotifyWarehouseFeeOrderRequest.Detail();
                fillReturnDetailPackage(detail, baseSku);//填充箱规和抖音货号
                detail.setOrderType("INVENTORY_ADJUST");
                detail.setOrderNo(transform.getIdempotentNo());
                detail.setStockNum(dailyStock.getQty());
                detail.setWarehouseAreaType(1);
                detail.setStorageAge(DateUtils.differentDays(transform.getSucTime(), new Date((order.getFeeDate() - 24 * 3600) * 1000)));
                if (detail.getStorageAge()<=0)
                    continue;
                details.add(detail);
            }
        }
    }

    private void fillReturnDetailPackage(NotifyWarehouseFeeOrderRequest.Detail detail, BaseSku baseSku) {
        detail.setCargoCode(baseSku.getOuterGoodsNo());
        if (baseSku.getSaleW()==null || BigDecimalUtils.eq(baseSku.getSaleW(),BigDecimal.ZERO)||
                baseSku.getSaleH()==null || BigDecimalUtils.eq(baseSku.getSaleH(),BigDecimal.ZERO)||
                baseSku.getSaleL()==null || BigDecimalUtils.eq(baseSku.getSaleL(),BigDecimal.ZERO)){
            baseSkuService.syncSize(baseSku.getId());
            baseSku = baseSkuService.queryByGoodsNo(baseSku.getGoodsNo());
        }
        try {
            if (BigDecimalUtils.gt(baseSku.getSaleH(),BigDecimal.ZERO)&&BigDecimalUtils.lt(baseSku.getSaleH(),new BigDecimal("0.1"))){
                detail.setHeight(1);
            }else {
                detail.setHeight(baseSku.getSaleH().multiply(BigDecimal.TEN).setScale(0, RoundingMode.HALF_UP).intValue());
            }
            if (BigDecimalUtils.gt(baseSku.getSaleL(),BigDecimal.ZERO)&&BigDecimalUtils.lt(baseSku.getSaleL(),new BigDecimal("0.1"))){
                detail.setLength(1);
            }else {
                detail.setLength(baseSku.getSaleL().multiply(BigDecimal.TEN).setScale(0, RoundingMode.HALF_UP).intValue());
            }
            if (BigDecimalUtils.gt(baseSku.getSaleW(),BigDecimal.ZERO)&&BigDecimalUtils.lt(baseSku.getSaleW(),new BigDecimal("0.1"))){
                detail.setWidth(1);
            }else {
                detail.setWidth(baseSku.getSaleW().multiply(BigDecimal.TEN).setScale(0, RoundingMode.HALF_UP).intValue());
            }
        }catch (NullPointerException e)
        {
            throw new BadRequestException("货号"+baseSku.getGoodsNo()+"没有维护长宽高");
        }
    }

    private void fillReturnInDetail(List<NotifyWarehouseFeeOrderRequest.Detail> details,cn.hutool.json.JSONObject trsLog,BaseSku baseSku,DailyStock dailyStock,DyCangzuFee order) {
        String docNo = trsLog.getStr("docno");
        ReturnGather returnGather = returnGatherService.queryByGatherNo(docNo);
        returnGather.setItemList(
                returnGatherDetailService.queryByGatherId(returnGather.getId())
        );
        NotifyWarehouseFeeOrderRequest.Detail detail = new NotifyWarehouseFeeOrderRequest.Detail();
        detail.setOrderType("SALE_RETURN_IN");
        detail.setOrderNo(docNo);
        fillReturnDetailPackage(detail,baseSku);//填充箱规与抖音货号
        detail.setWarehouseAreaType(1);
        detail.setStorageAge(DateUtils.differentDays(DateUtils.parseDate(dailyStock.getInTime()), new Date((order.getFeeDate() - 24 * 3600) * 1000)));
        if (detail.getStorageAge()<=0)
            return;
        //这个detail其实是个模板
        for (ReturnGatherDetail returnGatherDetail : returnGather.getItemList()) {
            if (StringUtil.equals(baseSku.getGoodsNo(),returnGatherDetail.getGoodsNo())) {
                String[] returnIds = returnGatherDetail.getReturnIds().split(",");
                int sum = dailyStock.getQty();//结余库存
                for (String returnId : returnIds) {
                    if(sum <=0){
                        break;
                    }
                    OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(returnId));
                    for (OrderReturnDetails returnDetails : orderReturn.getItemList()) {
                        if (StringUtil.equals(returnDetails.getGoodsNo(), baseSku.getGoodsNo())) {
                            int qty = Integer.valueOf(returnDetails.getQty());
                            if (sum - qty <= 0){
                                qty = sum;//当结余库存不足以抵扣退货单的上架数量时将结束循环退货入库单(外面的for)
                                sum = 0;
                            }else {
                                sum -= qty;
                            }
                            if (StringUtil.equals("1",orderReturn.getOrderSource())){
                                //退货单来源是抖音下发的
                                detail.setStockNum(qty);
                                detail.setOrderNo(orderReturn.getTradeReturnNo());
                                NotifyWarehouseFeeOrderRequest.Detail copy = new NotifyWarehouseFeeOrderRequest.Detail();
                                BeanUtil.copyProperties(detail, copy);
                                details.add(copy);
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private void reverseOutboundOrder(ReverseOutboundOrder reverseOutboundOrder) {
        OutboundOrder outboundOrder=outboundOrderService.queryByOutNo(reverseOutboundOrder.getOutboundPlanNo());
        if (outboundOrder!=null){
            DocOrderHeader orderHeader;
            if (StringUtil.isNotBlank(outboundOrder.getWmsNo()))
                orderHeader=wmsSupport.getDocOrderHead(outboundOrder.getWmsNo());
            else {
                cn.hutool.json.JSONObject object = wmsSupport.querySo(outboundOrder.getOrderNo(),outboundOrder.getOrderNo());
                orderHeader=object.getBean("header",DocOrderHeader.class);
            }
            if (orderHeader==null||orderHeader.getOrderno()==null&&(outboundOrder.getStatus()==700||outboundOrder.getStatus()==888||outboundOrder.getStatus()==999)
                    ||(!StringUtil.equals(orderHeader.getSostatus(),"99")&&outboundOrder.getDeliverBackTime()==null)){
                cbOrderProducer.send(
                        MsgType.CB_CK_CANCEL,
                        outboundOrder.getId()+"",
                        outboundOrder.getOrderNo());
            }else if (StringUtil.equals(orderHeader.getSostatus(),"99")){
                throw new BadRequestException("下游出库作业已完成，拒绝取消");
            }
        }
    }

    private void reverseInboundOrder(ReverseInboundOrder reverseInboundOrder) {
        InboundOrder inboundOrder=inboundOrderService.queryByOutNo(reverseInboundOrder.getInboundPlanNo());
        if (inboundOrder!=null){
            DocAsnHeader asnHeader;
            if (StringUtil.isNotBlank(inboundOrder.getWmsNo()))
                asnHeader=wmsSupport.getAsnOrder(inboundOrder.getWmsNo());
            else {
                cn.hutool.json.JSONObject object = wmsSupport.querySo(inboundOrder.getOrderNo(),inboundOrder.getOrderNo());
                asnHeader=object.getBean("header",DocAsnHeader.class);
            }
            if (asnHeader==null||asnHeader.getAsnno()==null&&(inboundOrder.getStatus()==700||inboundOrder.getStatus()==888||inboundOrder.getStatus()==999)
                    ||(!StringUtil.equals(asnHeader.getAsnstatus(),"99")&&inboundOrder.getTakeBackTime()==null)){
                cbOrderProducer.send(
                        MsgType.CB_RK_CANCEL,
                        inboundOrder.getId()+"",
                        inboundOrder.getOrderNo());
            }else if (StringUtil.equals(asnHeader.getAsnstatus(),"99")){
                throw new BadRequestException("下游入库作业已完成，拒绝取消");
            }
        }
    }

    private void tallyOrderReview(TallyOrderReview tallyOrderReview) {
        try {
            if (tallyOrderReview.getTallyOrderId().indexOf("A")==0){
                //入库单
                inboundOrderService.tallyReview(tallyOrderReview);
            }else {
                //出库单
                outboundOrderService.tallyReview(tallyOrderReview);
            }
        }catch (Exception e){
            e.printStackTrace();
            log.error("抖音理货审核回告异常,理货单号为"+tallyOrderReview.getTallyOrderId(),e);
        }
    }

    private void orderInfoNotify(OrderInfoNotify orderInfoNotify) throws Exception {
//        if (StringUtil.isBlank(orderInfoNotify.getOrderId()))
//            throw new BadRequestException("交易单号为空");
//        if (orderInfoNotify.getShopId()==null)
//            throw new BadRequestException("店铺id为空");
//        if (orderInfoNotify.getCreateTime()==null)
//            throw new BadRequestException("订单创建时间为空");
//        if (StringUtil.isBlank(orderInfoNotify.getIeFlag()))
//            throw new BadRequestException("进口标志为空");
//        if (orderInfoNotify.getCustomsClearType()==null)
//            throw new BadRequestException("通关模式为空");
//        if (StringUtil.isBlank(orderInfoNotify.getCustomsCode()))
//            throw new BadRequestException("申报海关代码为空");
//        if (StringUtil.isBlank(orderInfoNotify.getPortCode()))
//            throw new BadRequestException("口岸海关代码为空");
//        if (StringUtil.isBlank(orderInfoNotify.getWarehouseCode()))
//            throw new BadRequestException("仓库编码为空");
//        if (StringUtil.isBlank(orderInfoNotify.getScspWarehouseCode()))
//            throw new BadRequestException("服务商仓库编码为空");
//        if (orderInfoNotify.getWhType()==null)
//            throw new BadRequestException("wh_type为空");
//        if (orderInfoNotify.getGoodsValue()==null)
//            throw new BadRequestException("商品实际成交价为空");
//        if (orderInfoNotify.getFreight()==null)
//            throw new BadRequestException("运杂费为空");
//        if (orderInfoNotify.getDiscount()==null)
//            throw new BadRequestException("非现金抵扣额为空");
//        if (orderInfoNotify.getTaxTotal()==null)
//            throw new BadRequestException("代扣税款为空");
//        if (orderInfoNotify.getActualPaid()==null)
//            throw new BadRequestException("实际支付金额为空");
//        if (orderInfoNotify.getInsuredFee()==null)
//            throw new BadRequestException("物流保费为空");
//        if (StringUtil.isBlank(orderInfoNotify.getCurrency()))
//            throw new BadRequestException("币种为空");
//        if (StringUtil.isBlank(orderInfoNotify.getBuyerName()))
//            throw new BadRequestException("订购人姓名为空");
//        if (StringUtil.isBlank(orderInfoNotify.getBuyerTelephone()))
//            throw new BadRequestException("订购人电话号码为空");
//        if (orderInfoNotify.getBuyerIdType()==null)
//            throw new BadRequestException("订购人证件类型为空");
//        if (StringUtil.isBlank(orderInfoNotify.getBuyerIdNumber()))
//            throw new BadRequestException("订购人证件号码为空");
//        if (StringUtil.isBlank(orderInfoNotify.getConsignee()))
//            throw new BadRequestException("收件人姓名为空");
//        if (StringUtil.isBlank(orderInfoNotify.getConsigneeTelephone()))
//            throw new BadRequestException("收件人电话为空");
//        if (StringUtil.isBlank(orderInfoNotify.getConsigneeAddress()))
//            throw new BadRequestException("收件人地址为空");
//        if (orderInfoNotify.getCustomsClearType()==1||orderInfoNotify.getCustomsClearType()==2){
//            if (StringUtil.isBlank(orderInfoNotify.getPayCode()))
//                throw new BadRequestException("支付企业编码为空");
//        }
//        if (CollectionUtils.isEmpty(orderInfoNotify.getOrderDetailList()))
//            throw new BadRequestException("商品明细为空");
//        if (orderInfoNotify.getPreSaleType()==null)
//            throw new BadRequestException("预售类型为空");
//        if (orderInfoNotify.getExpShipTime()==null)
//            throw new BadRequestException("预计发货时间为空");
//        if (StringUtil.isBlank(orderInfoNotify.getEbpCode()))
//            throw new BadRequestException("电商平台代码为空");
//        if (StringUtil.isBlank(orderInfoNotify.getEbpName()))
//            throw new BadRequestException("电商平台名称为空");
        long start = new Date().getTime();
        String costTimeMsg = "";
        try {
            // 查询确认是否已保存过
            CrossBorderOrder order = crossBorderOrderService.queryByOrderNoWithDetails(orderInfoNotify.getOrderId());
            if (order != null) {
                return;
            }else {
                order = new CrossBorderOrder();
            }

            long costTime1 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "根据单号查询数据库花费时间：" + costTime1;
            // 将订单保存进数据库

            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setCreateBy("SYSTEM");

            order.setPlatformShopId(orderInfoNotify.getShopId()+"");
            order.setIsWave("0");
            order.setIsPrint("0");
            order.setSendPickFlag("0");
            order.setPlatformStatus(2);
            order.setDefault01("0");
            order.setDefault05("1");
            order.setIsLock("0");// 未锁单
            order.setDefault04("空间变换");
            order.setLogisticsStatus(1);



            //4PL订单和包材
            OrderInfoNotify.Extend extend=JSONObject.parseObject(orderInfoNotify.getExtend(),OrderInfoNotify.Extend.class);
            order.setVasPack(StringUtil.equals(extend.getVasPack(),"T")?"1":"0");
            order.setFourPl(StringUtil.equals(extend.getFourPL(),"T")?"1":"0");

            order.setPreSell(orderInfoNotify.getPreSaleType()+"");
            //订单申报方
            order.setDefault02(StringUtil.equals(extend.getOrderDeclare(),"T") ? "2" : "1");//1服务商申报， 2平台申报
            if (StringUtils.equals("1", order.getPreSell())) {
                order.setStatus(CBOrderStatusEnum.STATUS_777.getCode());
            }else {
                if (StringUtils.equals("2", order.getDefault02())) {
                    order.setStatus(CBOrderStatusEnum.STATUS_230.getCode());// 抖音申报的直接清关完成
                }else {
                    order.setStatus(CBOrderStatusEnum.STATUS_200.getCode());
                }
            }

            // 快递情况，是否4PL配
            order.setLogisticsFourPl(StringUtil.equals(extend.getTmsSeperate(),"T")?"1":"0");
            if (StringUtils.equals("1", order.getLogisticsFourPl())) {
                // 4PL配，直接保存运单相关信息
                LogisticsInfo logisticsInfo = logisticsInfoService.queryByDefault01(orderInfoNotify.getCarrierCode());
                if (logisticsInfo == null)
                    throw new BadRequestException("该快递编码未配置：" + orderInfoNotify.getCarrierCode());
                order.setSupplierId(logisticsInfo.getId());
//                if (StringUtil.isBlank(orderInfoNotify.getTransNo()))
//                    throw new BadRequestException("运单号为空");
//                if (StringUtil.isBlank(orderInfoNotify.getSortCode()))
//                    throw new BadRequestException("三段码为空");
                order.setLogisticsNo(orderInfoNotify.getTransNo());
                order.setAddMark(StringUtils.isBlank(orderInfoNotify.getShortAddressCode())?orderInfoNotify.getSortCode():orderInfoNotify.getShortAddressCode());
                order.setGrossWeight(orderInfoNotify.getGrossWeight());
                order.setNetWeight(orderInfoNotify.getNetWeight());
            }

            ShopToken shopToken = shopTokenService.queryByPaltShopId(orderInfoNotify.getShopId()+"");
            ClearCompanyInfo clearCompanyInfo = null;
            if (shopToken != null) {
                ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
                if (shopInfoDto != null) {
                    order.setCustomersId(shopInfoDto.getCustId());
                    order.setShopId(shopInfoDto.getId());
                    order.setOrderForm(shopInfoDto.getKjgCode());
                    clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
                }else {
                    order.setCustomersId(0L);
                    order.setShopId(shopToken.getShopId());
                }
            }else {
                order.setCustomersId(0L);
                order.setShopId(0L);
            }
            long costTime2 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "根据店铺ID查询花费店铺信息时间：" + costTime2;

            order.setOrderNo(orderInfoNotify.getOrderId());
            order.setCrossBorderNo(orderInfoNotify.getOrderId());
            order.setOrderCreateTime(new Timestamp(new BigDecimal(orderInfoNotify.getCreateTime()).multiply(new BigDecimal(1000)).longValue()));
            order.setEbpCode(orderInfoNotify.getEbpCode());
            order.setEbpName(orderInfoNotify.getEbpName());

            // 这些数据一期时间紧先写死
            order.setPlatformCode("DY");
            if (StringUtils.equals("11089697EJ", order.getEbpCode())) {
                order.setOrderForm("1506");
            }else {
                order.setOrderForm("1781");
            }

            order.setDisAmount(new BigDecimal(orderInfoNotify.getDiscount()+"").divide(new BigDecimal(100)).toString());
            order.setPostFee(new BigDecimal(orderInfoNotify.getFreight()+"").divide(new BigDecimal(100)).toString());
            order.setPayment(new BigDecimal(orderInfoNotify.getActualPaid()).divide(new BigDecimal(100)).toString());// 实际支付金额
            order.setBuyerAccount(orderInfoNotify.getBuyerRegNo());
            order.setBuyerPhone(orderInfoNotify.getBuyerTelephone());
            order.setBuyerIdNum(orderInfoNotify.getBuyerIdNumber());
            order.setBuyerName(orderInfoNotify.getBuyerName());
            order.setPayTime(new Timestamp(new BigDecimal(orderInfoNotify.getPayTime()).multiply(new BigDecimal(1000)).longValue()));
            BigDecimal taxTotal = new BigDecimal(orderInfoNotify.getTaxTotal()).divide(new BigDecimal(100));
            order.setTaxAmount(taxTotal.toString());// 总税费
            order.setPaymentNo(orderInfoNotify.getPayTransactionId());
            order.setOrderSeqNo(orderInfoNotify.getPayTransactionId());
            order.setBooksNo("T3105W000185");// 先写死

            order.setExpDeliverTime(new Timestamp(new BigDecimal(orderInfoNotify.getExpShipTime()).multiply(new BigDecimal(1000)).longValue()));
            if (StringUtil.isNotEmpty(orderInfoNotify.getPayCode())) {
                switch (orderInfoNotify.getPayCode()) {
                    case "4403169D3W":
                        order.setPayCode("13");//财付通
                        break;
                    case "31222699S7":
                        order.setPayCode("02");//支付宝
                        break;
                    case "4201960AED":
                        order.setPayCode("66");//
                        break;
                    default:
                        order.setPayCode(orderInfoNotify.getPayCode());
                }
            }
            order.setConsigneeName(orderInfoNotify.getConsignee());
            if (orderInfoNotify.getConsigneeAddress() != null) {
                CBOrderListMain.Address address=JSONObject.parseObject(orderInfoNotify.getConsigneeAddress(),CBOrderListMain.Address.class);
                order.setConsigneeAddr(address.getProvince().getName() + " " +
                        address.getCity().getName() + " " +
                        address.getTown().getName() + " " +
                        address.getStreet().getName() + " " +
                        address.getDetail());
                order.setConsigneeTel(orderInfoNotify.getConsigneeTelephone());
                order.setProvince(address.getProvince().getName());
                order.setCity(address.getCity().getName());
                order.setDistrict(address.getTown().getName());
            }
            // 是否需要冻结标记
            boolean needFreeze = false;
            String freezeReason = "";

            HashSet<String> skuNumSet = new HashSet<>();
            BigDecimal totalNum = BigDecimal.ZERO;
            // 处理明细数据
            if (CollectionUtils.isEmpty(order.getItemList())) {
                List<CrossBorderOrderDetails> list = new ArrayList<>();
                List<OrderDetail> itemList = orderInfoNotify.getOrderDetailList();
                if (CollectionUtils.isNotEmpty(itemList)) {
                    for (OrderDetail item : itemList) {
//                        if (StringUtil.isBlank(item.getItemNo()))
//                            throw new BadRequestException("货品编码为空");
//                        if (item.getTotalPrice()==null)
//                            throw new BadRequestException("总价为空");
//                        if (item.getQty()==null)
//                            throw new BadRequestException("商品数量为空");
//                        if (item.getPrice()==null)
//                            throw new BadRequestException("单价为空");
                        if (item.getItemNo()!=null){
                            // 有些商家瞎填货号有空格，直接去除
                            item.setItemNo(StringUtil.removeEscape(item.getItemNo()));
                        }
                        skuNumSet.add(item.getItemNo());
                        totalNum = totalNum.add(new BigDecimal(item.getQty()));

                        CrossBorderOrderDetails details = new CrossBorderOrderDetails();
                        // 1.根据货号+仓库编码查询
//                        BaseSku baseSku = baseSkuService.queryByGoodsNoAndWarehouseCode(item.getItemNo(), orderInfoNotify.getScspWarehouseCode());
                        BaseSku baseSku = baseSkuService.queryByOutGoodsNoAndWarehouseCode(item.getItemId(), orderInfoNotify.getScspWarehouseCode());
                        if (baseSku == null || order.getShopId().intValue() != baseSku.getShopId().intValue()) {
                            needFreeze = true;
                            freezeReason = item.getItemNo() + "skuId商品编码错误:" + item.getItemNo();
                        }else {
                            if (!StringUtils.equals(baseSku.getGoodsNo(), item.getRecordPartNo())) {
                                needFreeze = true;
                                freezeReason = item.getItemNo() + "备案料号不一致:" + item.getRecordPartNo()+"," + baseSku.getGoodsNo();
                            }else {
                                details.setGoodsId(baseSku.getId());
                                details.setGoodsCode(baseSku.getGoodsCode());
                                details.setBarCode(baseSku.getBarCode());
                                details.setGoodsNo(baseSku.getGoodsNo());
                            }
                        }

                        BigDecimal tax = taxTotal.divide(new BigDecimal(itemList.size()),2,BigDecimal.ROUND_HALF_UP);
                        // 一期不做商品映射关系
                        details.setOrderNo(order.getOrderNo());
                        details.setFontGoodsName(item.getItemName());
                        if (StringUtil.isBlank(details.getGoodsCode())) {
                            details.setGoodsCode(item.getItemNo());
                        }
                        if (StringUtil.isBlank(details.getGoodsNo())) {
                            details.setGoodsNo(item.getItemNo());
                        }
                        details.setQty(String.valueOf(item.getQty()));
                        details.setTaxAmount(tax.toString());
                        details.setPayment(new BigDecimal(item.getTotalPrice()+"").divide(new BigDecimal(100)).add(tax).toString());// 商品支付总价
                        details.setDutiableValue(new BigDecimal(item.getPrice()+"").divide(new BigDecimal(100)).toString()); // 不含税(申报)单价
                        details.setDutiableTotalValue(new BigDecimal(item.getTotalPrice()+"").divide(new BigDecimal(100)).toString());
                        list.add(details);
                        // 查询商品库存信息，无库存则冻结订单
                        if (clearCompanyInfo != null) {
                            Integer qty = wmsSupport.queryInventoryBySku(details.getGoodsNo(), clearCompanyInfo.getCustomsCode());
                            if (qty != null && qty.intValue() == 0 && !needFreeze) {
                                needFreeze = true;
                                freezeReason =  "WMS库存冻结失败商品可用库存不足,[skuId:]" + item.getItemNo() + "购买数量：" + item.getQty()+",可用库存0";
                            }
                        }
                    }
                }
                order.setItemList(list);
            }
            order.setSkuNum(String.valueOf(skuNumSet.size()));
            order.setTotalNum(totalNum.toString());

            long costTime3 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "订单明细处理花费时间：" + costTime3;

            CrossBorderOrder orderDto = crossBorderOrderService.createWithDetail(order);
            long costTime4 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "保存订单信息花费时间花费时间：" + costTime4;

            // 冻结逻辑
            if (needFreeze) {
                order.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
                order.setFreezeReason(freezeReason);
                crossBorderOrderService.update(order);
                OrderLog freezeLog = new OrderLog(
                        order.getId(),
                        order.getOrderNo(),
                        String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
                        BooleanEnum.SUCCESS.getCode(),
                        freezeReason
                );
                orderLogService.create(freezeLog);
            }else {
                // 如果是预售的单子，状态直接就是预售等待，不再往下走
                if (!StringUtils.equals("1", order.getPreSell())) {
                    if (StringUtils.equals("2", order.getDefault02())) {
                        cbOrderProducer.delaySend(
                                MsgType.CB_ORDER_235,
                                String.valueOf(order.getId()),
                                order.getOrderNo(),
                                3000
                        );
                    }else {
//                    if (!StringUtil.equals("1", orderDto.getPreSell())) {
//                        cbOrderProducer.send(
//                                MsgType.CB_ORDER_215,
//                                String.valueOf(orderDto.getId()),
//                                order.getOrderNo()
//                        );
//                    }
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_215,
                                String.valueOf(orderDto.getId()),
                                order.getOrderNo()
                        );
                    }
                }

            }


            long costTime5 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "冻结发送MQ消息花费时间：" + costTime5;

            long costTime6 = new Date().getTime() - start;
            costTimeMsg = costTimeMsg + "完成时间花费时间：" + costTime6;
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setCostTime(costTime6);
            orderLog.setCostTimeMsg(costTimeMsg);
            orderLogService.create(orderLog);
        }catch (Exception e) {
            e.printStackTrace();
            // 记录异常日志，继续循环
            OrderLog orderLog = new OrderLog(
                    0L,
                    String.valueOf(0),
                    String.valueOf(CBOrderStatusEnum.STATUS_200.getCode()),
                    orderInfoNotify.toString(),
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            throw e;
        }

    }



    @Override
    public void getMailNo() {
        List<DouyinMailMark>mailMarkList=douyinMailMarkService.queryByNonSucc();
        if (CollectionUtil.isEmpty(mailMarkList))
            return;
        for (DouyinMailMark douyinMailMark : mailMarkList) {
            try {
                getMailNo(String.valueOf(douyinMailMark.getId()));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void pushStockTransform(DyStockTransform dyStockTransform) throws Exception{
        StockTransformRequest request=new StockTransformRequest();
        request.setOccurrenceTime(dyStockTransform.getOccurrenceTime());
        request.setRemark(dyStockTransform.getRemark());
        ShopToken shopToken=shopTokenService.queryByShopId(dyStockTransform.getShopId());
        request.setShopId(Long.parseLong(shopToken.getPlatformShopId()));
        request.setTransformType(Short.parseShort(dyStockTransform.getTransformType()+""));
        request.setAdjustBizType(dyStockTransform.getAdjustBizType());
        request.setInboundPlanNo(dyStockTransform.getInboundOrderNo());
        if (StringUtil.equals(dyStockTransform.getStatus(),"0"))
            //创建-待审核
            request.setStatus(2);
        else if (StringUtil.equals(dyStockTransform.getStatus(),"2"))
            //审核通过-完成调整
            request.setStatus(1);
        else if (StringUtil.equals(dyStockTransform.getStatus(),"8"))
            //取消
            request.setStatus(5);
        request.setIdempotentNo(dyStockTransform.getIdempotentNo());
        request.setVendor(vendor);

        List<StockTransformDetail>detailList=new ArrayList<>();
        for (DyStockTransformDetail item : dyStockTransform.getItemList()) {
            StockTransformDetail detail=new StockTransformDetail();
            BaseSku baseSku=baseSkuService.queryByGoodsNo(item.getGoodsNo());
            detail.setCargoCode(baseSku.getOuterGoodsNo());
            detail.setFromGrade(item.getFromGrade());
            detail.setToGrade(item.getToGrade());
            detail.setQuantity(item.getQuantity());
            detail.setReasonCode(item.getReasonCode());
            detail.setReasonMsg(item.getReasonMsg());
            detail.setExtend(new HashMap<>());
            detail.setDuty(item.getDuty());
            detail.setRemark(item.getRemark());
            detail.setEvidence(JSONArray.parseArray(item.getEvidence(),String.class));
            detailList.add(detail);
            request.setWarehouseCode(baseSku.getWarehouseCode());
        }
        request.setStockTransformDetails(detailList);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音库存变动推送,id：{},请求参数：{}", dyStockTransform.getId(), request);
        DYCommonResponse<EmptyResponse>response=dySupport.request(EmptyResponse.class);
        log.info("抖音库存变动推送,id：{},返回：{}", dyStockTransform.getId(), response);
        if (response.isSuccess()){
            dyStockTransform.setIsSuccess("1");
        }else
            throw new BadRequestException(response.getMsg()+":"+response.getSubMsg());
    }

    @Override
    public void pushStockTaking(DyStockTaking dyStockTaking) throws Exception {
        // TODO: 2022/1/21 抖音盘点
        ShopToken shopToken=shopTokenService.queryByShopId(dyStockTaking.getShopId());
        StockTakingRequest request=new StockTakingRequest();
        request.setOccurrenceTime(dyStockTaking.getOccurrenceTime());
        request.setRemark(dyStockTaking.getRemark());
        request.setShopId(Long.parseLong(shopToken.getPlatformShopId()));
        request.setTakingType(dyStockTaking.getTakingType());
        request.setInboundPlanNo(dyStockTaking.getInboundOrderNo());
        request.setAdjustBizType(dyStockTaking.getAdjustBizType());
        request.setIdempotentNo(dyStockTaking.getIdempotentNo());
        if (StringUtil.equals(dyStockTaking.getStatus(),"0"))
            //创建-待审核
            request.setStatus(2);
        else if (StringUtil.equals(dyStockTaking.getStatus(),"2"))
            //审核通过-完成盘点
            request.setStatus(1);
        else if (StringUtil.equals(dyStockTaking.getStatus(),"8"))
            //取消
            request.setStatus(5);
        request.setVendor(vendor);

        List<StockTakingDetail>detailList=new ArrayList<>();
        for (DyStockTakingDetail item : dyStockTaking.getItemList()) {
            StockTakingDetail detail=new StockTakingDetail();
            BaseSku baseSku=baseSkuService.queryByGoodsNo(item.getGoodsNo());
            detail.setCargoCode(baseSku.getOuterGoodsNo());
            detail.setQualityGrade(item.getQualityGrade()+"");
            detail.setReasonCode(item.getReasonCode()+"");
            detail.setReasonMsg(item.getReasonMsg());
            detail.setQuantity(item.getQuantity());
            detail.setExtend(new HashMap<>());
            detail.setDuty(item.getDuty());
            detail.setRemark(item.getRemark());
            detail.setEvidence(JSONArray.parseArray(item.getEvidence(),String.class));
            detailList.add(detail);
            request.setWarehouseCode(baseSku.getWarehouseCode());
        }
        request.setStockTakingDetails(detailList);
        dySupport.setApiParam(request);
        dySupport.setAccessToken(shopToken.getAccessToken());
        log.info("抖音库存盘点报告推送,id：{},请求参数：{}", dyStockTaking.getId(), request);
        DYCommonResponse<EmptyResponse>response=dySupport.request(EmptyResponse.class);
        log.info("抖音库存盘点报告推送,id：{},返回：{}", dyStockTaking.getId(), response);
        if (!response.isSuccess()){
            throw new BadRequestException(response.getMsg()+":"+response.getSubMsg());
        }
    }

    @Override
    public void pullOrderByOrderNo(String body) throws Exception {
        JSONObject jsonObject=JSONObject.parseObject(body);
        String orderNo=jsonObject.getString("orderNo");
        String shopCode=jsonObject.getString("shopCode");
        pullOrderById(shopCode,orderNo);
    }

    private void takeLogisticsInfoPush(TakeLogisticsInfoPush takeLogisticsInfoPush) {
        if (takeLogisticsInfoPush == null)
            return;
        DouyinMailMark mailMark = douyinMailMarkService.queryByOrderNo(takeLogisticsInfoPush.getOrderId());
        boolean flag = true;
        if (mailMark == null) {
            flag = false;
            mailMark = new DouyinMailMark();
        }
        //1.takeLogisticsInfoPush转成DouyinMailMark保存到DB
        String orderNo = takeLogisticsInfoPush.getOrderId();
        if (StringUtils.isEmpty(orderNo))
            throw new BadRequestException("缺少orderId");
        mailMark.setOrderNo(orderNo);
        String shopId = takeLogisticsInfoPush.getShopId().toString();
        if (StringUtils.isEmpty(shopId))
            throw new BadRequestException("shopId必填");
        mailMark.setShopId(shopId);
        String consignee = takeLogisticsInfoPush.getConsignee();
        if (StringUtils.isEmpty(consignee))
            throw new BadRequestException("consignee必填");
        mailMark.setConsignee(StringUtil.filterEmoji(consignee));
        String consigneeTelephone = takeLogisticsInfoPush.getConsigneeTelephone();
        if (StringUtils.isEmpty(consigneeTelephone))
            throw new BadRequestException("consigneeTelephone必填");
        mailMark.setConsigneeTelephone(consigneeTelephone);
        String buyerIdType = takeLogisticsInfoPush.getBuyerIdType();
        if (StringUtils.isEmpty(buyerIdType))
            throw new BadRequestException("buyerIdType必填");
        mailMark.setBuyerIdType(buyerIdType);
        String buyerIdNumber = takeLogisticsInfoPush.getBuyerIdNumber();
        if (StringUtils.isEmpty(buyerIdNumber))
            throw new BadRequestException("buyerIdNumber必填");
        mailMark.setBuyerIdNumber(buyerIdNumber);
        String ieFlag = takeLogisticsInfoPush.getIeFlag();
        if (StringUtils.isEmpty(ieFlag))
            throw new BadRequestException("ieFlag必填");
        mailMark.setIeFlag(ieFlag);
        Integer whType = takeLogisticsInfoPush.getWhType();
        if (whType == null)
            throw new BadRequestException("whType必填");
        mailMark.setWhType(whType+"");
        String ebpCode = takeLogisticsInfoPush.getEbpCode();
        if (StringUtils.isEmpty(ebpCode))
            throw new BadRequestException("ebpCode必填");
        mailMark.setEbpCode(ebpCode);
        String ebpName = takeLogisticsInfoPush.getEbpName();
        if (StringUtils.isEmpty(ebpName))
            throw new BadRequestException("ebpName必填");
        mailMark.setEbpName(ebpName);
        String portCode = takeLogisticsInfoPush.getPortCode();
        if (StringUtils.isEmpty(portCode))
            throw new BadRequestException("portCode必填");
        mailMark.setPortCode(portCode);
        String scspWarehouseCode = takeLogisticsInfoPush.getScspWarehouseCode();
        if (StringUtils.isEmpty(scspWarehouseCode))
            throw new BadRequestException("scspWarehouseCode必填");
        mailMark.setScspWarehouseCode(scspWarehouseCode);
        String consigneeAddressStr = takeLogisticsInfoPush.getConsigneeAddress();
        if (StringUtils.isEmpty(consigneeAddressStr))
            throw new BadRequestException("consigneeAddress必填");
        TakeLogisticsInfoPush.Extend extend = JSONObject.parseObject(takeLogisticsInfoPush.getExtend(),TakeLogisticsInfoPush.Extend.class);
        if (extend != null && StringUtils.equals(extend.getFourPL(), "T")) {
            mailMark.setFourPl("1");
            // 4PL订单指定了快递
            if (StringUtils.isBlank(takeLogisticsInfoPush.getCarrierCode()))
                throw new BadRequestException("4PL订单未指定快递");
            LogisticsInfo logisticsInfo = logisticsInfoService.queryByDefault01(takeLogisticsInfoPush.getCarrierCode());
            if (logisticsInfo == null)
                throw new BadRequestException("仓库未签约此快递Code:" + takeLogisticsInfoPush.getCarrierCode());
            mailMark.setSupplierId(String.valueOf(logisticsInfo.getId()));
        }else {
            mailMark.setFourPl("0");
        }
        mailMark.setCarrierCode(takeLogisticsInfoPush.getCarrierCode());
        mailMark.setConsigneeAddress(StringUtil.filterEmoji(consigneeAddressStr));

        mailMark.setConsigneeAddr(takeLogisticsInfoPush.getAddress().getProvince().getName() + " " +
                takeLogisticsInfoPush.getAddress().getCity().getName() + " " +
                takeLogisticsInfoPush.getAddress().getTown().getName() + " " +
                takeLogisticsInfoPush.getAddress().getStreet().getName() + " " +
                takeLogisticsInfoPush.getAddress().getDetail());
        mailMark.setProvince(takeLogisticsInfoPush.getAddress().getProvince().getName());
        mailMark.setCity(takeLogisticsInfoPush.getAddress().getCity().getName());
        mailMark.setDistrict(takeLogisticsInfoPush.getAddress().getTown().getName());
        mailMark.setCreateTime(new Timestamp(System.currentTimeMillis()));
        mailMark.setIsSuccess("0");

        List<TakeLogisticsInfoPush.GoodsDetails> details = takeLogisticsInfoPush.getDetails();
        douyinMailMarkService.create(mailMark);
        if (!flag) {
            List<DouyinGoodsDetails>detailsList=new ArrayList<>();
            for (TakeLogisticsInfoPush.GoodsDetails detail : details) {
                DouyinGoodsDetails douyinGoodsDetails = new DouyinGoodsDetails();
                douyinGoodsDetails.setMarkId(mailMark.getId());
                douyinGoodsDetails.setItemNo(detail.getItemNo());
                douyinGoodsDetails.setBarCode(detail.getBarCode());
                douyinGoodsDetails.setItemName(detail.getItemName());
                douyinGoodsDetails.setPrice(detail.getPrice());
                douyinGoodsDetails.setQty(detail.getQty().longValue());
                douyinGoodsDetails.setCurrency(detail.getCurrency());
                douyinGoodsDetails.setWeight(detail.getWeight());
                douyinGoodsDetails.setNetWeightQty(detail.getNetWeightQty());
                douyinGoodsDetails.setRecordName(detail.getRecordName());
                douyinGoodsDetailsService.create(douyinGoodsDetails);
                detailsList.add(douyinGoodsDetails);
            }
            mailMark.setDetailList(detailsList);
        }
        cbOrderProducer.send(
                MsgType.DY_PUSH_MAIL_NO,
                String.valueOf(mailMark.getId()),
                mailMark.getOrderNo()
        );
    }

    @Override
    public void dyConfirmOrder(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(200);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmArrive(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(300);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmStockTally(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(400);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmStockedTally(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        // TODO: 2021/11/29  抖音回传入库理货结果
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(420);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        List<InboundCargoInfo>inboundCargoInfos=new ArrayList<>();
        TallyReport tallyReport = null;
        List<TallyReportCarGoDetail>carGoDetailList=null;
        if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
            if (StringUtil.isBlank(inboundOrder.getWmsNo())){
                cn.hutool.json.JSONObject wmsOrder = wmsSupport.queryAsn(inboundOrder.getOrderNo());
                if (wmsOrder == null)
                    throw new BadRequestException("获取WMS单号失败:" + inboundOrder.getOrderNo());
                inboundOrder.setWmsNo(wmsOrder.getJSONObject("header").getStr("orderno"));
            }
            tallyReport = new TallyReport();
            inboundOrder.setTallyCount(1+(inboundOrder.getTallyCount()==null?0:inboundOrder.getTallyCount()));
            tallyReport.setTallyOrderId(inboundOrder.getWmsNo()+"-"+inboundOrder.getTallyCount());
            tallyReport.setTallyCompletedTime(System.currentTimeMillis()/1000);
            carGoDetailList = new ArrayList<>();
        }
        if (StringUtil.isBlank(inboundOrder.getWmsNo())||inboundOrder.getWmsNo().indexOf("A-Test-")==0){
            if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
                tallyReport.setTallyTotalQty(0);//理货总件数
            }
            for (InboundOrderDetails detail : inboundOrder.getDetails()) {
                InboundCargoInfo cargoInfo=new InboundCargoInfo();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                cargoInfo.setCargoCode(StringUtil.isBlank(baseSku.getOuterGoodsNo())?detail.getGoodsNo():baseSku.getOuterGoodsNo());
                cargoInfo.setActualStackCount(detail.getTakeNum());
                cargoInfo.setGoodCargoCount(detail.getNormalNum());//良品
                cargoInfo.setDefectiveCargoCount(detail.getDamagedNum());//次品
                cargoInfo.setExpiryDate(DateUtils.formatDateTime(detail.getExpireDate()));//效期
                inboundCargoInfos.add(cargoInfo);
                if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
                    TallyReportCarGoDetail carGoDetail = new TallyReportCarGoDetail();
                    carGoDetail.setCargoCode(baseSku.getOuterGoodsNo());
                    carGoDetail.setOriginCountry(baseSku.getMakeContryCode());
                    carGoDetail.setCargoName(baseSku.getGoodsNameC());
                    BaseSkuPackInfo baseSkuPackInfo=null;
                    try {
                        baseSkuPackInfo=wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    int length;
                    int width;
                    int height;
                    int weight;
                    if (baseSkuPackInfo==null){
                        if (BigDecimalUtils.eq(baseSku.getSaleW(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleL(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleH(),BigDecimal.ZERO))
                            throw new BadRequestException("货号"+detail.getGoodsNo()+"请先去维护富勒的长宽高");
                        length=new Integer(String.format("%.0f",baseSku.getSaleL().multiply(BigDecimal.TEN)));
                        width=new Integer(String.format("%.0f",baseSku.getSaleW().multiply(BigDecimal.TEN)));
                        height=new Integer(String.format("%.0f",baseSku.getSaleH().multiply(BigDecimal.TEN)));
                        weight=new Integer(String.format("%.0f",baseSku.getNetWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                    }else {
                        if (BigDecimalUtils.eq(baseSkuPackInfo.getLength(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getWidth(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getHeight(),BigDecimal.ZERO))
                            throw new BadRequestException("货号"+detail.getGoodsNo()+"请先去维护富勒的长宽高");
                        length=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getLength().multiply(BigDecimal.TEN)));
                        width=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWidth().multiply(BigDecimal.TEN)));
                        height=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getHeight().multiply(BigDecimal.TEN)));
                        weight=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                    }
                    carGoDetail.setLength(length==0?1:length);
                    carGoDetail.setWidth(width==0?1:width);
                    carGoDetail.setHeight(height==0?1:height);
                    carGoDetail.setVolume(carGoDetail.getLength()*carGoDetail.getWidth()*carGoDetail.getHeight());
                    carGoDetail.setWeight(weight==0?1:weight);
                    carGoDetail.setBarcode(baseSku.getBarCode());
                    carGoDetail.setGoodQty(cargoInfo.getGoodCargoCount());
                    carGoDetail.setDefectiveQty(cargoInfo.getDefectiveCargoCount());
                    carGoDetail.setReceiveQty(cargoInfo.getActualStackCount());
                    //生产日期
                    carGoDetail.setExpirationDate(detail.getExpireDate().getTime()/1000);
                    carGoDetail.setProductDate(detail.getExpireDate().getTime()/1000-baseSku.getLifecycle()*24*3600);
                    carGoDetail.setDifference("-");
                    carGoDetailList.add(carGoDetail);
                    tallyReport.setTallyTotalQty(detail.getTakeNum()+tallyReport.getTallyTotalQty());
                }
            }
        }else {
            List<ReceivingTransaction>detailList=wmsSupport.getDocAsnOrderByAsnNo(inboundOrder.getWmsNo());
            List<ReceivingTransaction>huiZongList=new ArrayList<>();
            for (ReceivingTransaction transaction : detailList) {
                if (huiZongList.indexOf(transaction)!=-1){
                    ReceivingTransaction huiZongDetail=huiZongList.get(huiZongList.indexOf(transaction));
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(huiZongDetail.getGoodQty()+transaction.getReceivedqty().intValue());
                    }else {
                        huiZongDetail.setBadQty(huiZongDetail.getBadQty()+transaction.getReceivedqty().intValue());
                    }
                }else {
                    ReceivingTransaction huiZongDetail=new ReceivingTransaction();
                    transaction.copy(huiZongDetail);
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(transaction.getReceivedqty().intValue());
                        huiZongDetail.setBadQty(0);
                    }else {
                        huiZongDetail.setGoodQty(0);
                        huiZongDetail.setBadQty(transaction.getReceivedqty().intValue());
                    }
                    huiZongList.add(huiZongDetail);
                }
            }
            if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
                tallyReport.setTallyTotalQty(0);
            }
            for (ReceivingTransaction transaction : huiZongList) {
                InboundCargoInfo cargoInfo=new InboundCargoInfo();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(transaction.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+transaction.getSku()+"找不到");
                cargoInfo.setCargoCode(baseSku.getOuterGoodsNo());
                cargoInfo.setActualStackCount(transaction.getGoodQty()+transaction.getBadQty());
                cargoInfo.setGoodCargoCount(transaction.getGoodQty());//良品
                cargoInfo.setDefectiveCargoCount(transaction.getBadQty());//次品
                cargoInfo.setExpiryDate(StringUtil.isBlank(transaction.getLotatt02())?
                        DateUtils.formatDate(new Date(System.currentTimeMillis()+5*365+24*3600*1000L)):
                        transaction.getLotatt02());//效期,无效期管理的默认当前时间的5年后
                if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
                    TallyReportCarGoDetail carGoDetail = new TallyReportCarGoDetail();
                    carGoDetail.setCargoCode(baseSku.getOuterGoodsNo());
                    carGoDetail.setOriginCountry(baseSku.getMakeContryCode());
                    carGoDetail.setCargoName(baseSku.getGoodsNameC());
                    BaseSkuPackInfo baseSkuPackInfo=null;
                    try {
                        baseSkuPackInfo=wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    int length;
                    int width;
                    int height;
                    int weight;
                    if (baseSkuPackInfo==null){
                        if (BigDecimalUtils.eq(baseSku.getSaleW(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleL(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleH(),BigDecimal.ZERO))
                            throw new BadRequestException("货号"+transaction.getSku()+"请先去维护富勒的长宽高");
                        length=new Integer(String.format("%.0f",baseSku.getSaleL().multiply(BigDecimal.TEN)));
                        width=new Integer(String.format("%.0f",baseSku.getSaleW().multiply(BigDecimal.TEN)));
                        height=new Integer(String.format("%.0f",baseSku.getSaleH().multiply(BigDecimal.TEN)));
                        weight=new Integer(String.format("%.0f",baseSku.getNetWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                    }else {
                        if (BigDecimalUtils.eq(baseSkuPackInfo.getLength(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getWidth(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getHeight(),BigDecimal.ZERO))
                            throw new BadRequestException("货号"+transaction.getSku()+"请先去维护富勒的长宽高");
                        length=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getLength().multiply(BigDecimal.TEN)));
                        width=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWidth().multiply(BigDecimal.TEN)));
                        height=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getHeight().multiply(BigDecimal.TEN)));
                        weight=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                    }
                    carGoDetail.setLength(length==0?1:length);
                    carGoDetail.setWidth(width==0?1:width);
                    carGoDetail.setHeight(height==0?1:height);
                    carGoDetail.setVolume(carGoDetail.getLength()*carGoDetail.getWidth()*carGoDetail.getHeight());
                    carGoDetail.setWeight(weight==0?1:weight);
                    carGoDetail.setBarcode(baseSku.getBarCode());
                    carGoDetail.setGoodQty(cargoInfo.getGoodCargoCount());
                    carGoDetail.setDefectiveQty(cargoInfo.getDefectiveCargoCount());
                    carGoDetail.setReceiveQty(cargoInfo.getActualStackCount());
                    //生产日期
                    int lifecycle;
                    if (baseSku.getLifecycle()==null||baseSku.getLifecycle()==0)
                        lifecycle=5*365;
                    else
                        lifecycle=baseSku.getLifecycle();
                    if (transaction.getLotatt02()!=null)
                        carGoDetail.setExpirationDate(DateUtils.parseDate(transaction.getLotatt02()).getTime()/1000);
                    carGoDetail.setProductDate(DateUtils.parseDate(cargoInfo.getExpiryDate()).getTime()/1000-lifecycle*24*3600);//效期,无效期管理的默认当前时间的5年后
                    carGoDetail.setDifference("-");
                    carGoDetailList.add(carGoDetail);
                }
                inboundCargoInfos.add(cargoInfo);
                if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
                    tallyReport.setTallyTotalQty(tallyReport.getTallyTotalQty()+cargoInfo.getActualStackCount());
                }
            }
        }
        if (StringUtil.equals("1",inboundOrder.getIsFourPl())){
            tallyReport.setTallyReportCargoDetails(carGoDetailList);
            request.setTallyReport(tallyReport);
        }
        List<TallyReportUrl>tallyReportUrlList=new ArrayList<>();
        TallyReportUrl tallyReportUrl=new TallyReportUrl();
        tallyReportUrl.setName(inboundOrder.getStockRecordName());
        tallyReportUrl.setUrl(inboundOrder.getStockRecordUrl());
        tallyReportUrlList.add(tallyReportUrl);
        request.setTallyReportUrlList(tallyReportUrlList);
        request.setInboundCargoInfos(inboundCargoInfos);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmUp(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        if (StringUtil.equals("1",inboundOrder.getIsFourPl())&&inboundOrder.getStatus()!=647)
            //throw new BadRequestException("不处于审核通过状态");
            return;
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(500);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        List<InboundCargoInfo>inboundCargoInfos=new ArrayList<>();
        if (StringUtil.isBlank(inboundOrder.getWmsNo())||StringUtil.contains(inboundOrder.getWmsNo(),"A-Test-")){
            for (InboundOrderDetails detail : inboundOrder.getDetails()) {
                InboundCargoInfo cargoInfo=new InboundCargoInfo();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                cargoInfo.setCargoCode(StringUtil.isBlank(baseSku.getOuterGoodsNo())?detail.getGoodsNo():baseSku.getOuterGoodsNo());
                cargoInfo.setActualStackCount(detail.getTakeNum());
                cargoInfo.setGoodCargoCount(detail.getNormalNum());//良品
                cargoInfo.setDefectiveCargoCount(detail.getDamagedNum());//次品
                cargoInfo.setExpiryDate("2024-08-24 00:00:00");//效期
                inboundCargoInfos.add(cargoInfo);
            }
        }else {
            List<ReceivingTransaction>detailList=wmsSupport.getDocAsnOrderByAsnNo(inboundOrder.getWmsNo());
            List<ReceivingTransaction>huiZongList=new ArrayList<>();
            for (ReceivingTransaction transaction : detailList) {
                if (huiZongList.indexOf(transaction)!=-1){
                    ReceivingTransaction huiZongDetail=huiZongList.get(huiZongList.indexOf(transaction));
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(huiZongDetail.getGoodQty()+transaction.getReceivedqty().intValue());
                    }else {
                        huiZongDetail.setBadQty(huiZongDetail.getBadQty()+transaction.getReceivedqty().intValue());
                    }
                }else {
                    ReceivingTransaction huiZongDetail=new ReceivingTransaction();
                    transaction.copy(huiZongDetail);
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(transaction.getReceivedqty().intValue());
                        huiZongDetail.setBadQty(0);
                    }else {
                        huiZongDetail.setGoodQty(0);
                        huiZongDetail.setBadQty(transaction.getReceivedqty().intValue());
                    }
                    huiZongList.add(huiZongDetail);
                }
            }
            for (ReceivingTransaction transaction : huiZongList) {
                InboundCargoInfo cargoInfo=new InboundCargoInfo();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(transaction.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+transaction.getSku()+"找不到");
                cargoInfo.setCargoCode(baseSku.getOuterGoodsNo());
                cargoInfo.setActualStackCount(transaction.getGoodQty()+transaction.getBadQty());
                cargoInfo.setGoodCargoCount(transaction.getGoodQty());//良品
                cargoInfo.setDefectiveCargoCount(transaction.getBadQty());//次品
                cargoInfo.setExpiryDate(StringUtil.isBlank(transaction.getLotatt02())?
                        DateUtils.formatDate(new Date(System.currentTimeMillis()+5*365+24*3600*1000L)):
                        transaction.getLotatt02());//效期,无效期管理的默认当前时间的5年后
                inboundCargoInfos.add(cargoInfo);
            }
        }
        request.setInboundCargoInfos(inboundCargoInfos);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmCancel(InboundOrder inboundOrder,InboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(600);//单据状态
        request.setSourceOrderNo(inboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(inboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(inboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmCancel(OutboundOrder outboundOrder,OutboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(1400);//单据状态
        request.setSourceOrderNo(outboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(outboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(outboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmOut(OutboundOrder outboundOrder,OutboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(1300);//单据状态
        request.setSourceOrderNo(outboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(outboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(outboundOrder.getOrderNo());
        List<OutboundCargoInfo>outboundCargoInfos=new ArrayList<>();
        if (StringUtil.isBlank(outboundOrder.getWmsNo())||StringUtil.contains(outboundOrder.getWmsNo(),"S-Test-")){
            for (OutboundOrderDetails detail : outboundOrder.getDetails()) {
                OutboundCargoInfo outboundCargoInfo=new OutboundCargoInfo();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                outboundCargoInfo.setCargoCode(baseSku.getOuterGoodsNo());
                outboundCargoInfo.setQualityGrade(Short.parseShort(StringUtil.equals(detail.getDefault01(),"ZP")?"1":"2"));
                outboundCargoInfo.setQuantity(detail.getDeliverNum());
                outboundCargoInfos.add(outboundCargoInfo);
            }
        }else {
            List<ActAllocationDetails>actDetails=wmsSupport.getActAllocationBySoNo(outboundOrder.getWmsNo());
            if (CollectionUtil.isEmpty(actDetails))
                throw new BadRequestException(outboundOrder.getWmsNo()+"找不到出库单分配明细");
            for (ActAllocationDetails actDetail : actDetails) {
                OutboundCargoInfo outboundCargoInfo=new OutboundCargoInfo();
                OutboundOrderDetails details=new OutboundOrderDetails();
                details.setGoodsLineNo(actDetail.getOrderlineno()+"");
                int index=outboundOrder.getDetails().indexOf(details);
                if (index!=-1)
                    details=outboundOrder.getDetails().get(index);
                else {
                    //优先使用goodsLineNo
                    details.setGoodsNo(actDetail.getSku());
                    details.setGoodsLineNo(null);
                    details=outboundOrder.getDetails().get(outboundOrder.getDetails().indexOf(details));
                }
                if (StringUtil.equals(actDetail.getLotAtt08(),"良品")){
                    outboundCargoInfo.setQualityGrade(Short.valueOf("1"));
                }else {
                    outboundCargoInfo.setQualityGrade(Short.valueOf("2"));
                }
                if (details!=null){
                    if (StringUtil.isNotBlank(details.getDefault01())){
                        //防止旧单没有库存属性
                        short qualityGrade=(short)(StringUtil.equals(details.getDefault01(),"ZP")?1:2);
                        outboundCargoInfo.setQualityGrade(qualityGrade);
                    }
                }
                BaseSku baseSku=baseSkuService.queryByGoodsNo(actDetail.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+actDetail.getSku()+"找不到");
                outboundCargoInfo.setCargoCode(baseSku.getOuterGoodsNo());
                outboundCargoInfo.setQuantity(Integer.parseInt(String.format("%.0f",actDetail.getQty())));
                outboundCargoInfos.add(outboundCargoInfo);
            }
        }
        request.setOutboundCargoInfos(outboundCargoInfos);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMessage());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmOrder(OutboundOrder outboundOrder, OutboundOrderLog log) throws Exception{
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(1200);//单据状态
        request.setSourceOrderNo(outboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(outboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(outboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmStockedTally(OutboundOrder outboundOrder, OutboundOrderLog log)  throws Exception{
        // TODO: 2022/1/23 抖音回传 出库理货完成
        if (!StringUtil.equals(outboundOrder.getIsFourPl(),"1")){
            throw new BadRequestException("非4pl订单不可回传 抖音出库理货完成");
        }
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(1270);//单据状态
        request.setSourceOrderNo(outboundOrder.getOutNo());//来源单据号

        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(outboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(outboundOrder.getOrderNo());
        TallyReport tallyReport = new TallyReport();
        outboundOrder.setTallyCount(1+(outboundOrder.getTallyCount()==null?0:outboundOrder.getTallyCount()));
        /*if (StringUtil.isBlank(outboundOrder.getWmsNo()))
            outboundOrder.setWmsNo("S-Test-"+outboundOrder.getId());*/
        if (StringUtil.isBlank(outboundOrder.getWmsNo())){
            cn.hutool.json.JSONObject wmsOrder = wmsSupport.querySo(outboundOrder.getOrderNo(),outboundOrder.getOrderNo());
            if (wmsOrder == null)
                throw new BadRequestException("获取WMS单号失败:" + outboundOrder.getOrderNo());
            outboundOrder.setWmsNo(wmsOrder.getJSONObject("header").getStr("orderno"));
        }
        tallyReport.setTallyOrderId(outboundOrder.getWmsNo()+"-"+outboundOrder.getTallyCount());
        tallyReport.setTallyCompletedTime(System.currentTimeMillis()/1000);
        List<TallyReportCarGoDetail>carGoDetailList = new ArrayList<>();
        tallyReport.setTallyTotalQty(0);
        if (StringUtil.isBlank(outboundOrder.getWmsNo())||outboundOrder.getWmsNo().indexOf("S-Test-")==0){
            for (OutboundOrderDetails actDetail : outboundOrder.getDetails()) {
                TallyReportCarGoDetail carGoDetail = new TallyReportCarGoDetail();
                BaseSku baseSku = baseSkuService.queryByGoodsNo(actDetail.getGoodsNo());
                carGoDetail.setCargoCode(baseSku.getOuterGoodsNo());
                carGoDetail.setOriginCountry(baseSku.getMakeContryCode());
                carGoDetail.setCargoName(baseSku.getGoodsNameC());
                BaseSkuPackInfo baseSkuPackInfo=null;
                try {
                    baseSkuPackInfo=wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
                }catch (Exception e){
                    e.printStackTrace();
                }
                int length;
                int width;
                int height;
                int weight;
                if (baseSkuPackInfo==null){
                    if (BigDecimalUtils.eq(baseSku.getSaleW(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleL(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleH(),BigDecimal.ZERO))
                        throw new BadRequestException("货号"+actDetail.getGoodsNo()+"请先去维护富勒的长宽高");
                    length=new Integer(String.format("%.0f",baseSku.getSaleL().multiply(BigDecimal.TEN)));
                    width=new Integer(String.format("%.0f",baseSku.getSaleW().multiply(BigDecimal.TEN)));
                    height=new Integer(String.format("%.0f",baseSku.getSaleH().multiply(BigDecimal.TEN)));
                    weight=new Integer(String.format("%.0f",baseSku.getNetWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                }else {
                    if (BigDecimalUtils.eq(baseSkuPackInfo.getLength(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getWidth(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getHeight(),BigDecimal.ZERO))
                        throw new BadRequestException("货号"+actDetail.getGoodsNo()+"请先去维护富勒的长宽高");
                    length=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getLength().multiply(BigDecimal.TEN)));
                    width=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWidth().multiply(BigDecimal.TEN)));
                    height=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getHeight().multiply(BigDecimal.TEN)));
                    weight=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                }
                carGoDetail.setLength(length==0?1:length);
                carGoDetail.setWidth(width==0?1:width);
                carGoDetail.setHeight(height==0?1:height);
                carGoDetail.setVolume(carGoDetail.getLength()*carGoDetail.getWidth()*carGoDetail.getHeight());
                carGoDetail.setWeight(weight==0?1:weight);
                carGoDetail.setBarcode(baseSku.getBarCode());
                carGoDetail.setGoodQty(actDetail.getNormalNum());
                carGoDetail.setDefectiveQty(actDetail.getDamagedNum());
                carGoDetail.setReceiveQty(actDetail.getDeliverNum());
                //生产日期
                carGoDetail.setExpirationDate(actDetail.getExpireDate().getTime()/1000);
                carGoDetail.setProductDate(actDetail.getExpireDate().getTime()/1000-baseSku.getLifecycle()*24*3600);
                carGoDetail.setDifference("-");
                carGoDetailList.add(carGoDetail);
                tallyReport.setTallyTotalQty(tallyReport.getTallyTotalQty()+actDetail.getDeliverNum());
            }
        }else {
            List<ActAllocationDetails>actDetails=wmsSupport.getActAllocationBySoNo(outboundOrder.getWmsNo());
            if (CollectionUtil.isEmpty(actDetails))
                throw new BadRequestException(outboundOrder.getWmsNo()+"找不到出库单分配明细");
            for (ActAllocationDetails actDetail : actDetails) {
                TallyReportCarGoDetail carGoDetail = new TallyReportCarGoDetail();
                BaseSku baseSku = baseSkuService.queryByGoodsNo(actDetail.getSku());
                carGoDetail.setCargoCode(baseSku.getOuterGoodsNo());
                carGoDetail.setOriginCountry(baseSku.getMakeContryCode());
                carGoDetail.setCargoName(baseSku.getGoodsNameC());
                BaseSkuPackInfo baseSkuPackInfo=null;
                try {
                    baseSkuPackInfo=wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
                }catch (Exception e){
                    e.printStackTrace();
                }
                int length;
                int width;
                int height;
                int weight;
                if (baseSkuPackInfo==null){
                    if (BigDecimalUtils.eq(baseSku.getSaleW(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleL(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSku.getSaleH(),BigDecimal.ZERO))
                        throw new BadRequestException("货号"+actDetail.getSku()+"请先去维护富勒的长宽高");
                    length=new Integer(String.format("%.0f",baseSku.getSaleL().multiply(BigDecimal.TEN)));
                    width=new Integer(String.format("%.0f",baseSku.getSaleW().multiply(BigDecimal.TEN)));
                    height=new Integer(String.format("%.0f",baseSku.getSaleH().multiply(BigDecimal.TEN)));
                    weight=new Integer(String.format("%.0f",baseSku.getNetWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                }else {
                    if (BigDecimalUtils.eq(baseSkuPackInfo.getLength(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getWidth(),BigDecimal.ZERO)||BigDecimalUtils.eq(baseSkuPackInfo.getHeight(),BigDecimal.ZERO))
                        throw new BadRequestException("货号"+actDetail.getSku()+"请先去维护富勒的长宽高");
                    length=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getLength().multiply(BigDecimal.TEN)));
                    width=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWidth().multiply(BigDecimal.TEN)));
                    height=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getHeight().multiply(BigDecimal.TEN)));
                    weight=Integer.valueOf(String.format("%.0f",baseSkuPackInfo.getWeight().multiply(BigDecimalUtils.ONETHOUSAND)));
                }
                carGoDetail.setLength(length==0?1:length);
                carGoDetail.setWidth(width==0?1:width);
                carGoDetail.setHeight(height==0?1:height);
                carGoDetail.setVolume(carGoDetail.getLength()*carGoDetail.getWidth()*carGoDetail.getHeight());
                carGoDetail.setWeight(weight==0?1:weight);
                carGoDetail.setBarcode(baseSku.getBarCode());
                int qty = Integer.parseInt(String.format("%.0f",actDetail.getQty()));
                if (StringUtil.equals(actDetail.getLotAtt08(),"良品")){
                    carGoDetail.setGoodQty(qty);
                    carGoDetail.setDefectiveQty(0);
                }else {
                    carGoDetail.setGoodQty(0);
                    carGoDetail.setDefectiveQty(qty);
                }
                carGoDetail.setReceiveQty(Integer.parseInt(String.format("%.0f",actDetail.getQty())));
                //生产日期
                int lifecycle;
                if(baseSku.getLifecycle()==null||baseSku.getLifecycle()==0)
                    lifecycle=5*365;//无效期默认5年
                else
                    lifecycle=baseSku.getLifecycle();
                if (actDetail.getLotAtt02()==null){
                    actDetail.setLotAtt02(DateUtils.formatDateTime(new Date(System.currentTimeMillis()+lifecycle*24*3600*1000)));
                }else {
                    carGoDetail.setExpirationDate(DateUtils.parseDate(actDetail.getLotAtt02()).getTime()/1000);
                }
                carGoDetail.setProductDate(DateUtils.parseDate(actDetail.getLotAtt02()).getTime()/1000-lifecycle*24*3600);
                carGoDetail.setDifference("-");
                carGoDetailList.add(carGoDetail);
                tallyReport.setTallyTotalQty(tallyReport.getTallyTotalQty()+qty);
            }
        }
        tallyReport.setTallyReportCargoDetails(carGoDetailList);
        request.setTallyReport(tallyReport);
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void dyConfirmStockTally(OutboundOrder outboundOrder, OutboundOrderLog log)  throws Exception{
        // TODO: 2022/1/23 抖音回传 出库开始理货
        if (!StringUtil.equals(outboundOrder.getIsFourPl(),"1")){
            throw new BadRequestException("非4pl订单不可回传 抖音出库理货完成");
        }
        WarehouseInOutboundEventRequest request=new WarehouseInOutboundEventRequest();
        request.setVendor(vendor);
        request.setInOutboundEventType(1250);//单据状态
        request.setSourceOrderNo(outboundOrder.getOutNo());//来源单据号
        Config config=configService.queryByK("DY_CALLBACK_TIMESTAMP");
        Config config2=configService.queryByK("DY_TEST_TOKEN");
        ShopToken shopToken=shopTokenService.queryByShopId(outboundOrder.getShopId());
        request.setOccurrenceTime(((config!=null&&StringUtil.equals("1",config.getV()))||(config2!=null&&StringUtil.equals(config2.getV(),shopToken.getAccessToken())))?(System.currentTimeMillis()/1000):System.currentTimeMillis());
        request.setSpOrderNo(outboundOrder.getOrderNo());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        DYCommonResponse<WarehouseInOutboundEventResponse>response=dySupport.request(WarehouseInOutboundEventResponse.class);
        log.setReqMsg(JSON.toJSONString(request));
        log.setResMsg(JSON.toJSONString(response));
        if (response.isSuccess()){
            log.setSuccess(BooleanEnum.SUCCESS.getCode());
            log.setMsg(BooleanEnum.SUCCESS.getDescription());
        }else {
            log.setSuccess(BooleanEnum.FAIL.getCode());
            log.setMsg(BooleanEnum.FAIL.getDescription()+","+response.getMsg());
            throw new BadRequestException(response.getMessage());
        }
    }

    private void orderInterceptionNotify(OrderInterceptionNotify orderInterceptionNotify) throws Exception{
        if (StringUtil.isBlank(orderInterceptionNotify.getOrderId()))
            throw new BadRequestException("order_id必填");
        if (StringUtil.isBlank(orderInterceptionNotify.getOccurrenceTime()))
            throw new BadRequestException("occurrence_time必填");
        if (orderInterceptionNotify.getType()==null)
            throw new BadRequestException("type必填");
        CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(orderInterceptionNotify.getOrderId());
        if (order != null) {
            switch (orderInterceptionNotify.getType()){
                case 1:{
                    //拦截
                    cbOrderProducer.delaySend(
                            MsgType.DY_ORDERINTERCEPTION_NOTIFY,
                            String.valueOf(order.getId()),
                            order.getOrderNo(),
                            5000
                    );
                    break;
                }
                case 2:{
                    //取消拦截
                    break;
                }
            }
        }
    }

    private void orderInterceptionPush(OrderInterceptionPush orderInterceptionPush) throws Exception{
        if (StringUtil.isBlank(orderInterceptionPush.getOrderId()))
            throw new BadRequestException("order_id必填");
        if (StringUtil.isBlank(orderInterceptionPush.getOccurrenceTime()))
            throw new BadRequestException("occurrence_time必填");
        if (orderInterceptionPush.getType()==null)
            throw new BadRequestException("type必填");
        CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(orderInterceptionPush.getOrderId());
        if (order != null) {
            switch (orderInterceptionPush.getType()){
                case 1:{
                    //锁单(冻结)
                    cbOrderProducer.delaySend(
                            MsgType.DY_ORDERINTERCEPTION,
                            String.valueOf(order.getId()),
                            order.getOrderNo(),
                            5000
                    );
                    break;
                }
                case 2:{
                    //拦截
                    crossBorderOrderService.cancel(order.getId());
                    break;
                }
                case 3:{
                    //取消锁单
                    crossBorderOrderService.cancelLockOrder(order);
                    break;
                }
            }
        }
    }

    /**
     * 取消订单
     * @param body
     * @throws Exception
     */
    @Override
    public synchronized void cancelOrder(String body) throws Exception{
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.parseLong(body));
        try{
            crossBorderOrderService.cancel(order.getId());
            // 拦截成功
            confirmInterceptionSucc(order);
        }catch (Exception e) {
            // 拦截失败
            confirmInterceptionSucc(order);
        }


    }

    /**
     * 锁单
     * @param order
     * @throws Exception
     */
    @Override
    public synchronized void lockOrder(CrossBorderOrder order) throws Exception{
        crossBorderOrderService.lockOrder(order);
        OrderInterceptionRequest request=new OrderInterceptionRequest();
        request.setStatus(4);
        request.setVendor(vendor);
        request.setOrderId(order.getOrderNo());
        request.setOccurrenceTime(DateUtils.now());
        orderInterception(JSONObject.toJSONString(request));
    }

    /**
     * 刷新店铺token
     */
    @Override
    public void refreshToken() {
        List<ShopInfo> shopList = shopInfoService.queryByPlafCode(PlatformConstant.DY);
        for (ShopInfo shopInfo : shopList) {
            ShopToken shopToken = shopTokenService.queryByShopId(shopInfo.getId());
            if (shopToken != null) {
                try {
                    checkToken(shopToken);
                }catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void orderInterception(String body) throws Exception{
        OrderInterceptionRequest request=JSONObject.parseObject(body,OrderInterceptionRequest.class);
        CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(request.getOrderId());
        ShopToken shopToken=shopTokenService.queryByShopId(order.getShopId());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音回告锁单结果 请求："+body);
        DYCommonResponse<OrderInterceptionResponse>response=dySupport.request(OrderInterceptionResponse.class);
        log.info("抖音回告锁单结果 响应："+JSONObject.toJSONString(response));
        OrderLog orderLog = new OrderLog(
                order.getId(),
                order.getOrderNo(),
                "lock",
                BooleanEnum.FAIL.getCode(),
                BooleanEnum.FAIL.getDescription()
        );
        orderLog.setReqMsg(JSON.toJSONString(request));
        orderLog.setResMsg(JSON.toJSONString(response));
        if (!StringUtil.equals(response.getMessage(),"success")||response.getData().getCode()!=0){
            orderLogService.create(orderLog);
            throw new BadRequestException((StringUtil.equals(response.getMessage(),"success")?response.getData().getMsg():response.getMessage()));
        }
        orderLog.setSuccess(BooleanEnum.SUCCESS.getCode());
        orderLog.setMsg(BooleanEnum.SUCCESS.getDescription());
        orderLogService.create(orderLog);
    }

    @Override
    public void getMailNo(String body) throws Exception{
        DouyinMailMark mailMark = douyinMailMarkService.queryById(Long.valueOf(body));
        CrossBorderOrder order = douyinMailMarkService.toOrder(mailMark);
        Integer status = getStatus(order);
        if (status==16||status==17||status==21||status==5){
            //取消订单
            mailMark.setIsSuccess("1");
            douyinMailMarkService.update(mailMark);
            return;
        }
        if (StringUtils.isEmpty(order.getLogisticsNo())) {
            if (StringUtils.equals("1", mailMark.getFourPl())) {
                //4PL单指定了快递
                LogisticsInfo logisticsInfo = logisticsInfoService.queryByDefault01(mailMark.getCarrierCode());
                if (logisticsInfo == null)
                    throw new BadRequestException("4PL单未指定快递");
                logisticsInfoService.getLogisticsByLogis(order, logisticsInfo.getId());
            }else {
                logisticsInfoService.getLogistics(order);
            }
            mailMark.setSupplierId(String.valueOf(order.getSupplierId()));
            mailMark.setAddMark(order.getAddMark());
            mailMark.setLogisticsNo(order.getLogisticsNo());
            douyinMailMarkService.update(mailMark);
        }
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        //回传抖音接口
        OrderTransportRequest request = new OrderTransportRequest();
        request.setVendor(vendor);
        request.setOrderId(mailMark.getOrderNo());
        request.setDomesticCarrier(logisticsInfo.getDefault01());//物流公司code
        request.setDomesticTransNo(order.getLogisticsNo());    //物流单号
        request.setExpressCode(logisticsInfo.getKjgName());//物流企业代码,四字中文编码 (宁波必填)
        request.setExpressCopCode(logisticsInfo.getCustomsCode()); //物流企业海关编码
        request.setExpressCopName(logisticsInfo.getCustomsName());//物流企业名称
        request.setSortCode(order.getAddMark());//三段码
        ShopToken shopToken = shopTokenService.queryByPaltShopId(mailMark.getShopId()+"");

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音运单推送,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音运单推送响应,单号：{},返回：{}", order.getOrderNo(), response);
        mailMark.setLogisticsNo(order.getLogisticsNo());
        mailMark.setAddMark(order.getAddMark());
        mailMark.setCreateTime(new Timestamp(System.currentTimeMillis()));
        if (response.isSuccess()) {
            // 保存订单日志
            OrderLog orderLog= new OrderLog(
                    order.getId()==null?0L:order.getId(),
                    order.getOrderNo(),
                    order.getId()==null?(order.getStatus()+""):(CBOrderStatusEnum.STATUS_200.getCode()+""),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            mailMark.setIsSuccess("1");
            orderLog.setReqMsg(JSON.toJSONString(request));
            orderLog.setResMsg(JSON.toJSONString(response));
            orderLogService.create(orderLog);
            douyinMailMarkService.update(mailMark);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId()==null?0L:order.getId(),
                    order.getOrderNo(),
                    "pushMailNo",
                    BooleanEnum.FAIL.getCode(),
                    BooleanEnum.FAIL.getDescription()
            );
            orderLog.setReqMsg(JSON.toJSONString(request));
            orderLog.setResMsg(JSON.toJSONString(response));
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }

    }


    @Override
    public void lockOrder(String body) throws Exception{
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.parseLong(body));
        lockOrder(order);
    }

    private void ladingBillPush(LadingBillPush ladingBillPush) throws Exception{
        if (StringUtil.isBlank(ladingBillPush.getOutboundPlanNo()))
            throw new BadRequestException("outbound_plan_no必填");
        if (StringUtil.isBlank(ladingBillPush.getWarehouseNo()))
            throw new BadRequestException("warehouse_no必填");
        if (ladingBillPush.getShopId()==null)
            throw new BadRequestException("shop_id必填");
        if (ladingBillPush.getOutboundType()==null)
            throw new BadRequestException("outbound_type");
        if (ladingBillPush.getOutboundDate()==null)
            throw new BadRequestException("outbound_date必填");
        if (CollectionUtil.isEmpty(ladingBillPush.getCargoLadingList()))
            throw new BadRequestException("cargo_lading_list必填");
        int i=1;
        /*ClearInfo clearInfo=new ClearInfo();
        clearInfo.setClearNo(ladingBillPush.getOutboundPlanNo());
        ClearInfo exist=clearInfoService.queryByClearNo(clearInfo.getClearNo());
        if (exist!=null)
            return;
        clearInfo.setStatus(ClearInfoStatusEnum.STATUS_CREATE.getCode());
        ShopToken shopToken=shopTokenService.queryByPaltShopId(ladingBillPush.getShopId()+"");
        if (shopToken==null)
            throw new Exception("店铺未登记系统");
        ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
        clearInfo.setCustomersId(shopInfo.getCustId());
        clearInfo.setClearCompanyId(shopInfo.getServiceId());
        clearInfo.setShopId(shopInfo.getId());
        //提货出库类型(业务类型)
        // 1.销毁 2.退供 3.转仓 4.其他
        switch (ladingBillPush.getOutboundType()){
            case 1:
                //销毁
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_07.getCode());
                break;
            case 2:
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_06.getCode());
                break;
            case 3:
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_10.getCode());
            case 4:
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_04.getCode());
            default:
                throw new BadRequestException("outbound_type非法的值:"+ladingBillPush.getOutboundType());
        }
        clearInfo.setExpectArrivalTime(new Timestamp(ladingBillPush.getOutboundDate().getTime()));
        clearInfo.setRemark(ladingBillPush.getRemark());
        List<ClearDetails>clearDetailsList=new ArrayList<>();
        for (CargoLading cargoLading : ladingBillPush.getCargoLadingList()) {
            if (StringUtil.isBlank(cargoLading.getCargoCode()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的cargo_code为空");
            if (StringUtil.isBlank(cargoLading.getCargoName()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的cargo_name为空");
            if (StringUtil.isBlank(cargoLading.getExternalCode()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的external_code为空");
            if (cargoLading.getQualityGrade()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
            if (cargoLading.getQuantity()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
            if (cargoLading.getBarCode()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
            BaseSku baseSku = baseSkuService.queryByGoodsNo(cargoLading.getExternalCode());
            if (baseSku==null){
                throw new Exception("仓库货号:"+cargoLading.getExternalCode()+"未备案在系统");
            }
            ClearDetails clearDetails=new ClearDetails();
            clearDetails.setGrossWeight(baseSku.getGrossWeight()==null?"0.000":baseSku.getGrossWeight()+"");
            clearDetails.setNetWeight(baseSku.getNetWeight()==null?"0.000":baseSku.getNetWeight()+"");
            clearDetails.setPrice("0");
            clearDetails.setClearNo(ladingBillPush.getOutboundPlanNo());
            clearDetails.setGoodsNo(cargoLading.getExternalCode());
            clearDetails.setOuterGoodsNo(cargoLading.getCargoCode());
            clearDetails.setQty(cargoLading.getQuantity()+"");
            clearDetailsList.add(clearDetails);
        }
        clearInfo.setDetails(clearDetailsList);
        clearInfoService.createClearInfo(clearInfo);*/
        OutboundOrder outboundOrder=outboundOrderService.queryByOutNo(ladingBillPush.getOutboundPlanNo());
        if (outboundOrder!=null)
            return;
        boolean freezeFlag=false;
        StringBuilder freezeReason=new StringBuilder();
        outboundOrder = new OutboundOrder();
        ShopToken shopToken=shopTokenService.queryByPaltShopId(ladingBillPush.getShopId()+"");
        if (shopToken==null){
            freezeFlag=true;
            freezeReason.append("店铺编码").append(outboundOrder.getShopId()).append("未进行授权,\r\n");
        }else {
            ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
            outboundOrder.setCustomersId(shopInfo.getCustId());
            outboundOrder.setShopId(shopInfo.getId());
            outboundOrder.setCreateBy(shopInfo.getName());
            outboundOrder.setPlatformCode(shopInfo.getPlatformCode());
        }
        outboundOrder.setOutNo(ladingBillPush.getOutboundPlanNo());
        outboundOrder.setOrderType("0");
        outboundOrder.setTallyWay("0");
        outboundOrder.setIsOnline("1");
        outboundOrder.setOnlineSrc("DY");
        outboundOrder.setExpectDeliverTime(new Timestamp(ladingBillPush.getOutboundDate().getTime()));
        List<OutboundOrderDetails>details=new ArrayList<>();
        for (CargoLading cargoLading : ladingBillPush.getCargoLadingList()) {
            if (StringUtil.isBlank(cargoLading.getCargoCode()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的cargo_code为空");
            if (StringUtil.isBlank(cargoLading.getCargoName()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的cargo_name为空");
            if (StringUtil.isBlank(cargoLading.getExternalCode()))
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的external_code为空");
            if (cargoLading.getQualityGrade()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
            if (cargoLading.getQuantity()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
            if (cargoLading.getBarCode()==null)
                throw new BadRequestException("cargo_lading_list第"+i+"条数据的quality_grade为空");
//            BaseSku baseSku = baseSkuService.queryByOutGoodsNo(StringUtil.removeEscape(cargoLading.getCargoCode()));
            BaseSku baseSku = baseSkuService.queryByOutGoodsNoAndWarehouseCode(StringUtil.removeEscape(cargoLading.getCargoCode()), ladingBillPush.getWarehouseNo());
            if (baseSku==null){
                freezeFlag=true;
                freezeReason.append("仓库外部货号:").append(StringUtil.removeEscape(cargoLading.getExternalCode())).append("未备案在系统");
                OutboundOrderDetails detail=new OutboundOrderDetails();
                detail.setGoodsLineNo(i+"");
                detail.setGoodsNo(cargoLading.getExternalCode());
                detail.setExpectNum(cargoLading.getQuantity());
                detail.setDefault01(cargoLading.getQualityGrade()==1?"ZP":"CC");
                detail.setGoodsCode(cargoLading.getCargoCode());
                detail.setGoodsName(cargoLading.getCargoName());
                detail.setBarCode(cargoLading.getBarCode());
                details.add(detail);
                continue;
            }
            OutboundOrderDetails detail=new OutboundOrderDetails();
            detail.setGoodsLineNo(i+"");
            detail.setGoodsNo(baseSku.getGoodsNo());
            detail.setExpectNum(cargoLading.getQuantity());
            detail.setDefault01(cargoLading.getQualityGrade()==1?"ZP":"CC");
            details.add(detail);
            i++;
        }
        outboundOrder.setDetails(details);

        OrderInfoNotify.Extend extend=JSONObject.parseObject(ladingBillPush.getExtend(), OrderInfoNotify.Extend.class);
        if (extend != null){
            //4PL单
            outboundOrder.setDyExtReceiverName(extend.getReceiverName());
            outboundOrder.setDyExtReceiverPhone(extend.getReceiverPhone());
            outboundOrder.setDyExtTargetWarehouseAddr(extend.getTargetWarehouseAddr());
            outboundOrder.setDyExtTmsType(extend.getTmsType());
            if (StringUtil.equals("T",extend.getFourPL()))
                outboundOrder.setIsFourPl("1");
            else
                outboundOrder.setIsFourPl("0");
        }
        outboundOrderService.create(outboundOrder);
        if (freezeFlag)
            outboundOrderService.freezeOrder(outboundOrder,freezeReason.toString());
    }

    private void inboundBillPush(InboundBillPush inboundBillPush) throws Exception{
        if (StringUtil.isBlank(inboundBillPush.getInboundPlanNo()))
            throw new BadRequestException("inbound_plan_no必填");
        if (StringUtil.isBlank(inboundBillPush.getWarehouseNo()))
            throw new BadRequestException("warehouse_no必填");
        if (inboundBillPush.getShopId()==null)
            throw new BadRequestException("shop_id必填");
        if (inboundBillPush.getInboundFromType()==null)
            throw new BadRequestException("inbound_from_type必填");
        if (StringUtil.isBlank(inboundBillPush.getShippedFrom()))
            throw new BadRequestException("shipped_from必填");
        if (inboundBillPush.getShipmentMode()==null)
            throw new BadRequestException("shipment_mode必填");
        if (inboundBillPush.getShipmentDate()==null)
            throw new BadRequestException("shipment_date必填");
        if (inboundBillPush.getArrivalDate()==null)
            throw new BadRequestException("arrival_date必填");
        if (StringUtil.isBlank(inboundBillPush.getShipperName()))
            throw new BadRequestException("shipper_name必填");
        if (StringUtil.isBlank(inboundBillPush.getShipperPhone()))
            throw new BadRequestException("shipper_phone必填");
        if (StringUtil.isBlank(inboundBillPush.getShipperEmail()))
            throw new BadRequestException("shipper_email必填");
        if (CollectionUtil.isEmpty(inboundBillPush.getCargoInboundList()))
            throw new BadRequestException("cargo_inbound_list必填");
        if (CollectionUtil.isEmpty(inboundBillPush.getDeclareDetailList()))
            throw new BadRequestException("declare_detail_list必填");
        /*ClearInfo clearInfo=new ClearInfo();
        clearInfo.setClearNo(inboundBillPush.getInboundPlanNo());
        ClearInfo exist=clearInfoService.queryByClearNo(inboundBillPush.getInboundPlanNo());
        if (exist!=null)
            return;
        clearInfo.setStatus(ClearInfoStatusEnum.STATUS_CREATE.getCode());
        ShopToken shopToken=shopTokenService.queryByPaltShopId(inboundBillPush.getShopId()+"");// 后面加"" 是将int类型转String
        if (shopToken==null)
            throw new Exception("店铺未登记系统");
        ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
        clearInfo.setCustomersId(shopInfo.getCustId());
        clearInfo.setClearCompanyId(shopInfo.getServiceId());
        clearInfo.setShopId(shopInfo.getId());
        //预约类型(业务类型)
        switch (inboundBillPush.getInboundFromType()){
            case 1:
                //跨境一线进境
                clearInfo.setBusType(ClearBusTypeEnum.TYPE_01.getCode());
                break;
            case 2:
                if (StringUtil.isBlank(inboundBillPush.getOutAreaName()))
                    throw new BadRequestException("inbound_from_type为2时out_area_name必填");
                if (StringUtil.isBlank(inboundBillPush.getOutAreaCode()))
                    throw new BadRequestException("inbound_from_type为2时out_area_code必填");
                if (StringUtil.isBlank(inboundBillPush.getInboundBook()))
                    throw new BadRequestException("inbound_from_type为2时inbound_book必填");
                if (StringUtil.equals(inboundBillPush.getOutAreaCode(),"3105")){
                    //区内流转
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_03.getCode());
                }else {
                    //区间流转
                    clearInfo.setBusType(ClearBusTypeEnum.TYPE_02.getCode());
                }
                break;
            default:
                throw new BadRequestException("inbound_from_type非法的值:"+inboundBillPush.getInboundFromType());
        }
        //运输方式
        switch (inboundBillPush.getShipmentMode()){
            case 1:
                clearInfo.setTransWay("5");
                break;
            case 2:
                clearInfo.setTransWay("2");
                break;
            case 3:
                clearInfo.setTransWay("3");
                break;
            default:
                throw new BadRequestException("shipment_mode非法的值:"+inboundBillPush.getShipmentMode());
        }
        clearInfo.setShipCountry(inboundBillPush.getShippedFrom());
        clearInfo.setInWarehose(inboundBillPush.getWarehouseNo());
        clearInfo.setBooksNo(inboundBillPush.getInboundBook());
        StringBuilder urlBuild=new StringBuilder();
        int i=1;
        for (DeclareDetail declareDetail : inboundBillPush.getDeclareDetailList()) {
            urlBuild.append(declareDetail.getUrl());
            if (i==inboundBillPush.getDeclareDetailList().size()){
                urlBuild.append(",");
            }
            i++;
        }
        clearInfo.setClearDataLink(urlBuild.toString());
        clearInfo.setRemark(inboundBillPush.getRemark());
        List<ClearDetails>clearDetailsList=new ArrayList<>();
        for (CargoInbound cargoInbound : inboundBillPush.getCargoInboundList()) {
            BaseSku baseSku = baseSkuService.queryByGoodsNo(cargoInbound.getExternalCode());
            if (baseSku==null){
                throw new Exception("仓库货号:"+cargoInbound.getExternalCode()+"未备案在系统");
            }
            ClearDetails clearDetails=new ClearDetails();
            clearDetails.setGrossWeight(baseSku.getGrossWeight()==null?"0.000":baseSku.getGrossWeight()+"");
            clearDetails.setNetWeight(baseSku.getNetWeight()==null?"0.000":baseSku.getNetWeight()+"");
            clearDetails.setPrice("0");
            clearDetails.setClearNo(inboundBillPush.getInboundPlanNo());
            clearDetails.setGoodsNo(cargoInbound.getExternalCode());
            clearDetails.setOuterGoodsNo(cargoInbound.getCargoCode());
            clearDetails.setQty(cargoInbound.getQuantity()+"");

            clearDetailsList.add(clearDetails);
        }
        clearInfo.setDetails(clearDetailsList);
        clearInfo.setExpectArrivalTime(new Timestamp(inboundBillPush.getArrivalDate().getTime()));
        clearInfoService.createClearInfo(clearInfo);*/

        InboundOrder inboundOrder=new InboundOrder();
        inboundOrder.setOutNo(inboundBillPush.getInboundPlanNo());
        InboundOrder exist=inboundOrderService.queryByOutNo(inboundOrder.getOutNo());
        if (exist!=null)
            return;
        boolean freezeFlag=false;
        StringBuilder freezeReason= new StringBuilder();
        ShopToken shopToken=shopTokenService.queryByPaltShopId(inboundBillPush.getShopId()+"");// 后面加"" 是将int类型转String
        if (shopToken==null){
            freezeFlag=true;
            freezeReason.append("店铺编码").append(inboundBillPush.getShopId()).append("未进行授权,\r\n");
        }
        ShopInfo shopInfo=shopInfoService.findById(shopToken.getShopId());
        inboundOrder.setPlatformCode(shopInfo.getPlatformCode());
        inboundOrder.setCustomersId(shopInfo.getCustId());
        inboundOrder.setShopId(shopInfo.getId());
        inboundOrder.setOrderType("0");
        inboundOrder.setExpectArriveTime(new Timestamp(inboundBillPush.getArrivalDate().getTime()));
        inboundOrder.setTallyWay("0");
        inboundOrder.setIsOnline("1");
        inboundOrder.setOnlineSrc("DY");
        List<InboundOrderDetails>details=new ArrayList<>();
        for (CargoInbound cargoInbound : inboundBillPush.getCargoInboundList()) {
            BaseSku baseSku = baseSkuService.queryByOutGoodsNoAndWarehouseCode(StringUtil.removeEscape(cargoInbound.getCargoCode()),inboundBillPush.getWarehouseNo());
            if (baseSku==null){
                baseSku = baseSkuService.queryByGoodsNoAndWarehouseCode(cargoInbound.getExternalCode(), inboundBillPush.getWarehouseNo());
                if (baseSku==null){
                    freezeReason.append("仓库货号:").append(cargoInbound.getExternalCode()).append("未备案在系统,\r\n");
                    freezeFlag=true;
                    InboundOrderDetails detail=new InboundOrderDetails();
                    detail.setGoodsNo(cargoInbound.getExternalCode());
                    detail.setExpectNum(cargoInbound.getQuantity());
                    detail.setBarCode(cargoInbound.getBarCode());
                    detail.setGoodsCode(cargoInbound.getCargoCode());
                    detail.setGoodsName(cargoInbound.getCargoName());
                    details.add(detail);
                    continue;
                }else {
                    baseSku.setOuterGoodsNo(cargoInbound.getCargoCode());
                    baseSkuService.update(baseSku);
                }
            }
            InboundOrderDetails detail=new InboundOrderDetails();
            detail.setGoodsNo(baseSku.getGoodsNo());
            detail.setExpectNum(cargoInbound.getQuantity());
            details.add(detail);
            if (!StringUtil.equals(baseSku.getOuterGoodsNo(),cargoInbound.getCargoCode())){
                baseSku.setOuterGoodsNo(cargoInbound.getCargoCode());
                baseSkuService.update(baseSku);
            }
        }
        inboundOrder.setDeclareDetailList(JSONObject.toJSONString(inboundBillPush.getDeclareDetailList()));
        inboundOrder.setDetails(details);
        inboundOrder.setCreateBy(shopInfo.getName());
        OrderInfoNotify.Extend extend=JSONObject.parseObject(inboundBillPush.getExtend(), OrderInfoNotify.Extend.class);
        if (extend != null){
            //4PL单
            inboundOrder.setDyExtPortCustomType(extend.getPortCustomType());
            inboundOrder.setDyExtSendWarehouseAddr(extend.getSendWarehouseAddr());
            inboundOrder.setDyExtSubLadingBillNo(extend.getSubLadingBillNo());
            inboundOrder.setDyExtTmsType(extend.getTmsType());
            inboundOrder.setDyExtTransactionMethod(extend.getTransactionMethod());
            if (StringUtil.equals("T",extend.getFourPL()))
                inboundOrder.setIsFourPl("1");
            else
                inboundOrder.setIsFourPl("0");
        }
        inboundOrderService.create(inboundOrder);
        if (freezeFlag)
            inboundOrderService.freezeOrder(inboundOrder,freezeReason.toString());
    }

    private void orderPush(OrderPush orderPush) throws Exception{
        if (StringUtil.isBlank(orderPush.getOrderId()))
            throw new BadRequestException("order_id必填");
        if (orderPush.getShopId()==null)
            throw new BadRequestException("shop_id必填");
        ShopToken shopToken=shopTokenService.queryByPaltShopId(orderPush.getShopId()+"");
        if (shopToken==null)
            throw new Exception("店铺id:"+orderPush.getShopId()+"未登记系统");
        DyOrderPush dyOrderPush=dyOrderPushService.queryByOrderNo(orderPush.getOrderId());
        if (dyOrderPush==null){
            dyOrderPush=new DyOrderPush();
            dyOrderPush.setOrderNo(orderPush.getOrderId());
            dyOrderPush.setPlatformShopId(orderPush.getShopId()+"");
            dyOrderPush.setShopId(shopToken.getShopId());
            dyOrderPush.setCreateTime(new Timestamp(System.currentTimeMillis()));
            dyOrderPush.setIsSuccess("0");
            dyOrderPush.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            dyOrderPushService.create(dyOrderPush);
        }
        String json="{\"orderNo\":\""+dyOrderPush.getOrderNo()+"\",\"shopCode\":\""+dyOrderPush.getPlatformShopId()+"\"}";
        cbOrderProducer.send(MsgType.CB_ORDER_PULL_ORDERNO_DY,json,dyOrderPush.getOrderNo());
    }


    // 创建退货单
    @Override
    public void recReturnOrder(String data) {
        JSONObject obj = JSON.parseObject(data);
        CBOrderReturnMain returnMain = JSON.parseObject(data, CBOrderReturnMain.class);
        List<CBOrderReturnChild> itemList = JSONArray.parseArray(obj.getString("order_details"), CBOrderReturnChild.class);
        OrderReturn exist = orderReturnService.queryByLogisNo(returnMain.getLogisticsNo());
        if (exist != null)
            return;
        OrderReturn orderReturn = new OrderReturn();
        orderReturn.setOrderSource("1");// 抖音下发
        ShopToken shopToken = shopTokenService.queryByPaltShopId(returnMain.getShopId());
        if (shopToken != null) {
            orderReturn.setShopId(shopToken.getShopId());
            ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
            orderReturn.setCustomersId(shopInfoDto.getCustId());
        }
        orderReturn.setIsWave("0");
        orderReturn.setIsOverTime("1");
        orderReturn.setTradeReturnNo(returnMain.getTradeReturnNo());
        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_300.getCode());
        orderReturn.setLogisticsNo(returnMain.getLogisticsNo());
        orderReturn.setOrderNo(returnMain.getOrderId());
        orderReturn.setIsBorder(String.valueOf(returnMain.getIsBorder()));
        orderReturn.setAfterSalesType(String.valueOf(returnMain.getAfterSaleType()));
        orderReturn.setAfterSalesNo(returnMain.getAfterSaleNo());
        orderReturn.setSalesCustomsTime(new Timestamp(Long.valueOf(returnMain.getSaleCustomsTime())));
        orderReturn.setSExpressNo(returnMain.getsExpressNo());
        orderReturn.setSExpressName(returnMain.getsExpressName());
        orderReturn.setRExpressNo(returnMain.getrExpressNo());
        orderReturn.setRExpressName(returnMain.getrExpressName());
        orderReturn.setReturnType(String.valueOf(returnMain.getReturnType()));
        orderReturn.setCreateBy("SYSTEM");
        orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));

        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderReturn.getOrderNo());
        if (order != null) {
            orderReturn.setSalesCustomsTime(order.getClearSuccessTime());
            orderReturn.setSalesDeliverTime(order.getClearSuccessTime());
            orderReturn.setInvtNo(order.getInvtNo());
            orderReturn.setPlatformCode(order.getPlatformCode());
            orderReturn.setFourPl(order.getFourPl());
        }
        List<OrderReturnDetails> detailsList = new ArrayList<>();
        for (CBOrderReturnChild child : itemList) {
            if (child.getItemNo()!=null){
                // 有些商家瞎填货号有空格，直接去除
                child.setItemNo(StringUtil.removeEscape(child.getItemNo()));
            }
            OrderReturnDetails details = new OrderReturnDetails();
            details.setLogisticsNo(orderReturn.getOrderNo());
            details.setGoodsNo(child.getItemNo());
            details.setFontGoodsName(child.getItemName());
            details.setQty(String.valueOf(child.getQty()));
            // 查询商品基本信息
            BaseSku baseSku = baseSkuService.queryByGoodsNoAndShop(child.getItemNo(), orderReturn.getShopId());
            if (baseSku != null) {
                details.setGoodsId(baseSku.getId());
                details.setGoodsName(baseSku.getGoodsName());
                details.setGoodsCode(baseSku.getGoodsCode());
                details.setHsCode(baseSku.getHsCode());
                details.setBarCode(StringUtil.removeEscape(baseSku.getBarCode()));
            }
            detailsList.add(details);
        }
        orderReturn.setItemList(detailsList);
        orderReturnService.createWithDetail(orderReturn);

        OrderReturnLog log = new OrderReturnLog(
                orderReturn.getId(),
                orderReturn.getTradeReturnNo(),
                String.valueOf(orderReturn.getStatus()),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderReturnLogService.create(log);
    }

    // 抖音创建退货单
    @Override
    public void createSaleReturnOrder(SaleReturnOrder saleReturnOrder) {
        OrderReturn exist = orderReturnService.queryByOrderNo(saleReturnOrder.getTradeSaleNo());
        if (exist != null)
            return;// 退货单已存在
        OrderReturn orderReturn = new OrderReturn();
        orderReturn.setOrderSource("1");// 抖音下发
        ShopToken shopToken = shopTokenService.queryByPaltShopId(String.valueOf(saleReturnOrder.getShopId()));
        if (shopToken != null) {
            orderReturn.setShopId(shopToken.getShopId());
            ShopInfoDto shopInfoDto = shopInfoService.queryById(shopToken.getShopId());
            orderReturn.setCustomersId(shopInfoDto.getCustId());
        }
        orderReturn.setIsWave("0");
        orderReturn.setIsOverTime("0");
        orderReturn.setPlatformCode("DY");
        orderReturn.setTradeReturnNo(saleReturnOrder.getTradeReturnNo());
        orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_337.getCode());
        orderReturn.setLogisticsNo(saleReturnOrder.getLogisticsNo());
        orderReturn.setOrderNo(saleReturnOrder.getTradeSaleNo());
        orderReturn.setIsBorder(String.valueOf(saleReturnOrder.getIsBorder()));
        orderReturn.setAfterSalesType(String.valueOf(saleReturnOrder.getAfterSaleType()));
        orderReturn.setAfterSalesNo(saleReturnOrder.getAfterSaleNo());
        orderReturn.setSalesCustomsTime(new Timestamp(saleReturnOrder.getSaleCustomsTime()));
        orderReturn.setSExpressNo(saleReturnOrder.getsExpressNo());
        orderReturn.setSExpressName(saleReturnOrder.getsExpressName());
        orderReturn.setRExpressNo(saleReturnOrder.getrExpressNo());
        orderReturn.setRExpressName(saleReturnOrder.getrExpressName());
        orderReturn.setReturnType(String.valueOf(saleReturnOrder.getReturnType()));
        orderReturn.setCreateBy("SYSTEM");
        orderReturn.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orderReturn.setLogisticsFulfilNo(saleReturnOrder.getLogisticsFulfilNo());


        CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(orderReturn.getOrderNo());
        if (order != null) {
            orderReturn.setSalesCustomsTime(order.getClearSuccessTime());
            orderReturn.setSalesDeliverTime(order.getClearSuccessTime());
            orderReturn.setInvtNo(order.getInvtNo());
            orderReturn.setPlatformCode(order.getPlatformCode());
            orderReturn.setFourPl(order.getFourPl());
        }
        List<OrderReturnDetails> detailsList = new ArrayList<>();
        List<SaleReturnOrderDetail> itemList = saleReturnOrder.getOrderDetails();
        for (SaleReturnOrderDetail child : itemList) {
            OrderReturnDetails details = new OrderReturnDetails();
            BaseSku baseSku = baseSkuService.queryByOutGoodsNoAndWarehouseCode(child.getItemId(), saleReturnOrder.getrWarehouseCode());
            if (baseSku == null)
                throw new BadRequestException("货品ID不存在：" + child.getItemId());
            details.setLogisticsNo(orderReturn.getOrderNo());
            details.setGoodsNo(baseSku.getGoodsNo());
            details.setFontGoodsName(child.getItemName());
            details.setQty(String.valueOf(child.getQuantity()));
            // 查询商品基本信息

            details.setGoodsId(baseSku.getId());
            details.setGoodsName(baseSku.getGoodsName());
            details.setGoodsCode(baseSku.getGoodsCode());
            details.setHsCode(baseSku.getHsCode());
            details.setBarCode(StringUtil.removeEscape(baseSku.getBarCode()));
            detailsList.add(details);
        }
        orderReturn.setItemList(detailsList);
        orderReturnService.createWithDetail(orderReturn);

        OrderReturnLog log = new OrderReturnLog(
                orderReturn.getId(),
                orderReturn.getTradeReturnNo(),
                String.valueOf(orderReturn.getStatus()),
                "",
                "",
                BooleanEnum.SUCCESS.getCode(),
                BooleanEnum.SUCCESS.getDescription()
        );
        orderReturnLogService.create(log);
    }

    @Override
    public void confirmReturnByTools(OrderReturn orderReturn, String status) throws Exception {
        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus(status);
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));
        request.setVendor(vendor);
        request.setWarehouseCode("4PLFLBBC01");
        request.setLogisticsFulfilNo(orderReturn.getLogisticsFulfilNo());
        if ("3".equals(status)) {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setErrorCode("QC002");
            errorInfo.setErrorMsg("其他");
            request.setReason(JSON.toJSONString(errorInfo));
        }

        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货工具回传,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货工具回传,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_310.getCode()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public ReconciliationRespData inventoryReconciliation(Reconciliation reconciliation) {
        // TODO: 2021/12/21 跨境库存对账
        ReconciliationRespData data=new ReconciliationRespData();
        List<CargoInventory>list=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reconciliation.getCargoList())){
            CustomerKeyDto keyDto=customerKeyService.findByCustCode("3302461510");
            for (Cargo cargo : reconciliation.getCargoList()) {
                BaseSku baseSku=baseSkuService.queryByOutGoodsNoAndWarehouseCode(cargo.getCargoCode(),reconciliation.getWarehouseCode());
                CargoInventory cargoInventory=new CargoInventory();
                cargoInventory.setCargoCode(cargo.getCargoCode());
                cargoInventory.setExternalCode(cargo.getExternalCode());
                cargoInventory.setActualReconciliationTime(System.currentTimeMillis()/1000);
                cargoInventory.setShopId(cargo.getShopId());
                if (baseSku==null){
                    cargoInventory.setGoodInventoryQty(0);
                    cargoInventory.setDefectiveGoodQty(0);
                    list.add(cargoInventory);
                }else {
                    int[] qtys=getSumStock(baseSku.getGoodsNo(),keyDto);
                    cargoInventory.setGoodInventoryQty(qtys[0]);
                    cargoInventory.setDefectiveGoodQty(qtys[1]);
                    list.add(cargoInventory);
                }
            }
        }
        data.setCargoInventoryList(list);
        return data;
    }

    /**
     * 撤单成功，在单证放行之后撤单成功时回传
     * @param order
     */
    @Override
    public void confirmDelClearSuccess(CrossBorderOrder order) throws Exception{
        // TODO: 2022/1/11  抖音撤单完成
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传撤单成功,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("6");

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音开始回传撤单成功开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传'撤单成功'成功,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_884.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            order.setClearDelSuccessBackTime(new Timestamp(System.currentTimeMillis()));
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_884.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    /**
     * 抖音撤单开始回传，单证放行后的
     * @param order
     * @throws Exception
     */
    @Override
    public void confirmDelClearStart(CrossBorderOrder order) throws Exception{
        // TODO: 2022/1/11  抖音撤单开始
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传撤单开始,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("4");

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音开始回传撤单开始,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传撤单开始成功,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            // 保存订单日志
            order.setClearDelStartBackTime(new Timestamp(System.currentTimeMillis()));
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_880.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_880.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    /**
     * 关单成功，在单证放行之前取消申报成功时回传
     * @param order
     */
    @Override
    public void confirmCloseClearSuccess(CrossBorderOrder order) throws Exception {
        // TODO: 2022/1/11  抖音回传取消申报成功(关单)
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传关单成功,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("1000");

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音开始回传关单成功,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传关单成功,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_886.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            order.setCancelTimeBack(new Timestamp(System.currentTimeMillis()));
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_886.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    /**
     * 抖音回传撤单失败
     * @param order
     * @throws Exception
     */
    @Override
    public void confirmDelClearFail(CrossBorderOrder order) throws Exception {
        // TODO: 2022/1/11  抖音回传撤单失败
        ShopToken shopToken = shopTokenService.queryByShopId(order.getShopId());
        log.info("抖音开始回传撤单失败,单号：{}", order.getOrderNo());

        CBOrderCustomClearanceRequest request = new CBOrderCustomClearanceRequest();
        request.setOrderId(order.getOrderNo());
        request.setVendor(vendor);
        request.setOccurrenceTime(DateUtil.now());
        request.setCustomsCode("3105");
        request.setCustomsName("甬保税区");
        request.setStatus("5");

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);

        log.info("抖音开始回传撤单失败,单号：{},请求参数：{}", order.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音回传撤单失败 成功,单号：{},返回：{}", order.getOrderNo(), response);
        if (response.isSuccess()) {
            // 回传成功
            // 保存订单日志
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_882.getCode()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
        }else {
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    String.valueOf(CBOrderStatusEnum.STATUS_882.getCode()),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderLog.setReqMsg(request.toString());
            orderLog.setResMsg(response.toString());
            orderLog.setKeyWord(response.getLogId());
            orderLogService.create(orderLog);
            throw new BadRequestException(response.getMessage());
        }
    }

    @Override
    public void syncInventorySnapshot() {
        // TODO: 2022/8/21 抖音库存快照同步
        List<ShopInfo>shopInfoList=shopInfoService.queryByPlafCode("DY");
        if (shopInfoList.isEmpty())
            return;
        String dayTime=DateUtils.format(new Date(System.currentTimeMillis()-48*3600*1000),"yyyyMMdd");//前天23点50分记录的，可视为昨天0点记录的库存
        for (ShopInfo shopInfo : shopInfoList) {
            List<DailyStock>stockList=dailyStockService.queryByShopIdAndDayTime(shopInfo.getId(),dayTime);
            ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
            if (CollectionUtils.isEmpty(stockList))
                continue;
            YuncSyncInventorySnapshot snapshot = new YuncSyncInventorySnapshot();
            String idempotentNo=Md5Utils.md5Hex(System.currentTimeMillis()+"");
            snapshot.setIdempotentNo(idempotentNo);
            snapshot.setVendorNo(vendor);
            snapshot.setShopIdOut(shopToken.getPlatformShopId());
            snapshot.setCurrentPage(1);
            snapshot.setOccurTime(System.currentTimeMillis()/1000L-24*3600);
            snapshot.setWarehouseCode("4PLFLBBC01");
            snapshot.setOwnerCode("0");
            snapshot.setApiTime(System.currentTimeMillis()/1000);
            //List<YuncSyncInventorySnapshot.Detail>_3plStock=new ArrayList<>();
            List<YuncSyncInventorySnapshot.Detail>_4plDetail=new ArrayList<>();
            for (DailyStock dailyStock : stockList) {
                BaseSku baseSku=baseSkuService.queryByGoodsNoAndShop(dailyStock.getGoodsNo(),shopInfo.getId());
                if (StringUtils.isBlank(baseSku.getOuterGoodsNo()))
                    continue;
                YuncSyncInventorySnapshot.Detail detail=new YuncSyncInventorySnapshot.Detail();
                detail.setCargoCode(baseSku.getOuterGoodsNo());
                detail.setCargoType(1);
                detail.setInventoryType(StringUtil.equals("良品",dailyStock.getStockStatus())?1:2);
                detail.setInventoryStatus(1);
                detail.setScmInboundOrder("");
                detail.setSupplierId("0");
                if (StringUtil.isBlank(dailyStock.getProdTime())&&baseSku.getLifecycle()!=null&&baseSku.getLifecycle()!=0){
                    detail.setProductDate(DateUtils.parseDate(dailyStock.getExpTime()).getTime()/1000-baseSku.getLifecycle()*24*3600);
                }else if (StringUtil.isBlank(dailyStock.getProdTime())&&(baseSku.getLifecycle()==null||baseSku.getLifecycle()==0)){
                    detail.setProductDate(0L);
                }else {
                    detail.setProductDate(DateUtils.parseDate(dailyStock.getProdTime()).getTime()/1000L);
                }
                if (StringUtil.isBlank(dailyStock.getExpTime())){
                    if (StringUtil.isNotBlank(dailyStock.getProdTime())){
                        if (baseSku.getLifecycle()!=null&&baseSku.getLifecycle()!=0){
                            detail.setExpireDate(DateUtils.parseDate(dailyStock.getProdTime()).getTime()/1000+baseSku.getLifecycle()*24*3600);
                        }else {
                            detail.setExpireDate(0L);
                        }
                    }else {
                        detail.setExpireDate(0L);
                    }
                }else {
                    detail.setExpireDate(DateUtils.parseDate(dailyStock.getExpTime()).getTime()/1000);
                }
                detail.setReceiptDate(StringUtil.isBlank(dailyStock.getInTime())?0L:DateUtils.parseDate(dailyStock.getInTime()).getTime()/1000);
                detail.setBatchNumber(StringUtil.isBlank(dailyStock.getBatchNo())?"":dailyStock.getBatchNo());
                detail.setOpeningInventory(0.0);
                detail.setEndingInventory(Double.valueOf(dailyStock.getQty()+".0"));
                if (StringUtil.equals(baseSku.getWarehouseCode(),"FLBBC01")){
                    //_3plStock.add(detail);
                }else {
                    _4plDetail.add(detail);
                }

            }
            if (_4plDetail.size()>0){
                if (_4plDetail.size()>50){
                    //分页回传
                    List<YuncSyncInventorySnapshot.Detail>copyDetail = new ArrayList<>();
                    int page=0;
                    for (int i = 0; i < _4plDetail.size();) {
                        if (copyDetail.size() == 50 || i == _4plDetail.size()-1){
                            page++;
                            if (i == _4plDetail.size()-1){
                                copyDetail.add(_4plDetail.get(i));
                                i++;
                            }
                            snapshot.setIdempotentNo(idempotentNo+"-"+page);
                            snapshot.setDetails(copyDetail);
                            snapshot.setTotalLines(_4plDetail.size());
                            snapshot.setPageSize(copyDetail.size());
                            snapshot.setCurrentPage(page);
                            dySupport.setApiParam(snapshot);
                            dySupport.setAccessToken(shopToken.getAccessToken());
                            DYCommonResponse<BaseDataResponse>resp;
                            try {
                                resp=dySupport.request(BaseDataResponse.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            if (!resp.isSuccess()){
                                log.error("抖音回传库存快照错误"+JSONObject.toJSONString(resp));
                            }
                            copyDetail.clear();
                        }else {
                            copyDetail.add(_4plDetail.get(i));
                            i++;
                        }
                    }
                }else {
                    snapshot.setDetails(_4plDetail);
                    snapshot.setTotalLines(_4plDetail.size());
                    snapshot.setPageSize(snapshot.getTotalLines());
                    dySupport.setApiParam(snapshot);
                    dySupport.setAccessToken(shopToken.getAccessToken());
                    DYCommonResponse<BaseDataResponse>resp;
                    try {
                        resp=dySupport.request(BaseDataResponse.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (!resp.isSuccess()){
                        log.error("抖音回传库存快照错误"+JSONObject.toJSONString(resp));
                    }
                }
            }
            /*if (_3plStock.size()>0){
                snapshot.setIdempotentNo(Md5Utils.md5Hex((System.currentTimeMillis()/1000)+""));
                snapshot.setOccurTime(System.currentTimeMillis()/1000);
                snapshot.setApiTime(snapshot.getOccurTime());
                snapshot.setTotalLines(_3plStock.size());
                snapshot.setPageSize(snapshot.getTotalLines());
                snapshot.setWarehouseCode("FLBBC01");
                snapshot.setDetails(_3plStock);
                dySupport.setApiParam(snapshot);
                DYCommonResponse<BaseDataResponse>resp;
                try {
                    resp=dySupport.request(BaseDataResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if (!(StringUtil.equals("10000",resp.getStatusCode())&&StringUtil.equals(resp.getData().getCode(),"0"))){
                    log.error("抖音回传库存快照错误"+JSONObject.toJSONString(resp));
                }
            }*/
        }


    }

    @Override
    public void syncInventoryLogFlow() {
        // TODO: 2022/8/21 抖音库存流水同步
        List<ShopInfo>shopInfoList=shopInfoService.queryByPlafCode("DY");
        if (shopInfoList.isEmpty())
            return;
        String endTime=DateUtils.format(new Date(System.currentTimeMillis()-24*3600*1000L),"yyyy-MM-dd")+" 23:59:59";
        String startTime=DateUtils.format(new Date(System.currentTimeMillis()-24*3600*1000L),"yyyy-MM-dd")+" 00:00:00";
        String snapShortTime = DateUtils.formatDate(new Date(System.currentTimeMillis()-24*3600*1000));
        for (ShopInfo shopInfo : shopInfoList) {
            ShopToken shopToken=shopTokenService.queryByShopId(shopInfo.getId());
            List<String>goodsNo=baseSkuService.queryByShopIdOnlyGoodsNo(shopInfo.getId());
            if (CollectionUtils.isEmpty(goodsNo))
                continue;
            List<ActTransactionLogSoAsnInvLotAtt>list=wmsSupport.queryTranscationLogByDyApi(goodsNo,startTime,endTime);//库存流水记录
            if (CollectionUtils.isEmpty(list))
                continue;
            YuncSyncInventoryLogFlow request = new YuncSyncInventoryLogFlow();
            String idempotentNo=list.get(0).getTransactionid();
            request.setIdempotentNo(idempotentNo);
            request.setVendorNo(vendor);
            request.setShopIdOut(shopToken.getPlatformShopId());
            request.setCurrentPage(1);
            request.setWarehouseCode("4PLFLBBC01");
            request.setOwnerCode("0");
            request.setSnapshotTime(snapShortTime);
            request.setApiTime(System.currentTimeMillis()/1000);
            //List<YuncSyncInventoryLogFlow.Detail>_3plDetail=new ArrayList<>();
            List<YuncSyncInventoryLogFlow.Detail>_4plDetail=new ArrayList<>();
            BaseSku baseSku=null;
            for (ActTransactionLogSoAsnInvLotAtt invLotAtt : list) {
                if (baseSku==null||!StringUtil.equals(baseSku.getGoodsNo(),invLotAtt.getFmsku())){
                    baseSku=baseSkuService.queryByGoodsNoAndShop(invLotAtt.getFmsku(),shopInfo.getId());//来自pre的数据已经过FMSKU排序
                }
                if (StringUtils.isBlank(baseSku.getOuterGoodsNo()))
                    continue;
                YuncSyncInventoryLogFlow.Detail detail=new YuncSyncInventoryLogFlow.Detail();
                detail.setOccurTime(invLotAtt.getTransactiontime().getTime()/1000L);
                detail.setCargoCode(baseSku.getOuterGoodsNo());
                detail.setCargoType(1);
                detail.setInventoryType(StringUtil.equals("良品",invLotAtt.getLotatt08())?1:2);
                detail.setInventoryStatus(1);
                detail.setLotNo(invLotAtt.getFmlotnum());
                if (StringUtil.isBlank(invLotAtt.getLotatt01())&&StringUtil.isBlank(invLotAtt.getLotatt02())){
                    //无效期
                    detail.setProductDate(0L);
                    detail.setExpireDate(0L);
                }else if (StringUtil.isBlank(invLotAtt.getLotatt01())){
                    detail.setExpireDate(DateUtils.parseDate(invLotAtt.getLotatt02()).getTime()/1000);
                    if (baseSku.getLifecycle()==null||baseSku.getLifecycle()==0){
                        detail.setProductDate(0L);
                    }else {
                        detail.setProductDate(detail.getExpireDate()-baseSku.getLifecycle()*24*3600);
                    }
                }else {
                    detail.setProductDate(DateUtils.parseDate(invLotAtt.getLotatt01()).getTime()/1000);
                    if (baseSku.getLifecycle()==null||baseSku.getLifecycle()==0){
                        detail.setExpireDate(0L);
                    }else {
                        detail.setExpireDate(detail.getProductDate()+baseSku.getLifecycle()*24*3600);
                    }
                }
                detail.setReceiptDate(DateUtils.parseDate(invLotAtt.getLotatt03()).getTime()/1000);
                detail.setSupplierId(0L);
                detail.setScmInboundOrder("");
                if (StringUtil.equals("SO",invLotAtt.getTransactiontype())){
                    if (StringUtil.contains(invLotAtt.getSoreference2(),"CK")){
                        //2B
                        detail.setOrderType("TRANSFER_OUT");//调拨出库
                        OutboundOrder outboundOrder = outboundOrderService.queryByOrderNo(invLotAtt.getSoreference2());
                        if (outboundOrder==null)
                            detail.setWmsOrderNo(invLotAtt.getSoreference2());
                        else
                            detail.setWmsOrderNo(outboundOrder.getOutNo());
                    }else {
                        //2C
                        detail.setOrderType("SALE_OUT");//调拨出库
                        detail.setWmsOrderNo(invLotAtt.getSoreference2());
                    }
                    detail.setErpOrderNo(invLotAtt.getOrderno());
                }else {
                    if (StringUtil.contains(invLotAtt.getAsnreference1(),"TH")){
                        //2B
                        detail.setOrderType("SALE_RETURN_IN");//销退入库
                        List<OrderReturn>orderReturnList = orderReturnService.querybyGatherNoAndShopId(invLotAtt.getAsnreference1(),shopInfo.getId());
                        if (CollectionUtils.isEmpty(orderReturnList))
                            detail.setWmsOrderNo(invLotAtt.getAsnreference1());
                        else {
                            for (OrderReturn orderReturn : orderReturnList) {
                                if (CollectionUtils.isNotEmpty(orderReturn.getItemList())){
                                    for (OrderReturnDetails returnDetails : orderReturn.getItemList()) {
                                        if (invLotAtt.getFmsku().equals(returnDetails.getGoodsNo())){
                                            if (StringUtils.equals(orderReturn.getOrderSource(),"1"))
                                                detail.setWmsOrderNo(orderReturn.getTradeReturnNo());
                                            else if (StringUtils.equals(orderReturn.getOrderSource(),"2")){
                                                detail.setWmsOrderNo(orderReturn.getOrderNo());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }else {
                        //2C
                        detail.setOrderType("PURCHASE_IN");//采购入库
                        InboundOrder inboundOrder = inboundOrderService.queryByOrderNo(invLotAtt.getAsnreference1());
                        if (inboundOrder==null)
                            detail.setWmsOrderNo(invLotAtt.getAsnreference1());
                        else
                            detail.setWmsOrderNo(inboundOrder.getOutNo());
                    }
                    detail.setErpOrderNo(invLotAtt.getAsnno());
                }
                detail.setChangeQty(invLotAtt.getFmqty());
                detail.setUnit(StringUtil.isBlank(baseSku.getUnit())?"件":baseSku.getUnit());
                if (StringUtil.equals(baseSku.getWarehouseCode(),"FLBBC01")){
                    //_3plDetail.add(detail);
                }else {
                    _4plDetail.add(detail);
                }
            }
            if (_4plDetail.size()>0){
                if (_4plDetail.size()>50){
                    //分页回传
                    List<YuncSyncInventoryLogFlow.Detail>copyDetail = new ArrayList<>();
                    int page=0;
                    for (int i = 0; i < _4plDetail.size();) {
                        if (copyDetail.size() == 50 || i == _4plDetail.size()-1){
                            page++;
                            if (i == _4plDetail.size()-1){
                                copyDetail.add(_4plDetail.get(i));
                                i++;
                            }
                            request.setIdempotentNo(idempotentNo+"-"+page);
                            request.setDetails(copyDetail);
                            request.setTotalLines(_4plDetail.size());
                            request.setPageSize(copyDetail.size());
                            request.setCurrentPage(page);
                            dySupport.setApiParam(request);
                            dySupport.setAccessToken(shopToken.getAccessToken());
                            DYCommonResponse<BaseDataResponse>resp;
                            try {
                                resp=dySupport.request(BaseDataResponse.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            if (!resp.isSuccess()){
                                log.error("抖音回传库存流水错误"+JSONObject.toJSONString(resp));
                            }
                            copyDetail.clear();
                        }else {
                            copyDetail.add(_4plDetail.get(i));
                            i++;
                        }
                    }
                }else {
                    request.setDetails(_4plDetail);
                    request.setTotalLines(_4plDetail.size());
                    request.setPageSize(request.getTotalLines());
                    dySupport.setApiParam(request);
                    dySupport.setAccessToken(shopToken.getAccessToken());
                    DYCommonResponse<BaseDataResponse>resp;
                    try {
                        resp=dySupport.request(BaseDataResponse.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (!resp.isSuccess()){
                        log.error("抖音回传库存流水错误"+JSONObject.toJSONString(resp));
                    }
                }
            }
            /*if (_3plDetail.size()>0){
                request.setIdempotentNo(Md5Utils.md5Hex((System.currentTimeMillis()/1000)+""));
                request.setApiTime(System.currentTimeMillis()/1000);
                request.setTotalLines(_3plDetail.size());
                request.setPageSize(request.getTotalLines());
                request.setWarehouseCode("FLBBC01");
                request.setDetails(_3plDetail);
                dySupport.setApiParam(request);
                DYCommonResponse<BaseDataResponse>resp;
                try {
                    resp=dySupport.request(BaseDataResponse.class);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                if (!(StringUtil.equals("10000",resp.getStatusCode())&&StringUtil.equals(resp.getData().getCode(),"0"))){
                    log.error("抖音回传库存流水错误"+JSONObject.toJSONString(resp));
                }
            }*/
        }
    }

    @Override
    public YuncWmsInventoryQueryResponse yuncWmsInventoryQuery(YuncWmsInventoryQueryRequest queryRequest) {
        YuncWmsInventoryQueryResponse data=new YuncWmsInventoryQueryResponse();
        data.setPage(queryRequest.getPage());
        data.setPageSize(queryRequest.getPageSize());
        List<YuncWmsInventoryQueryResponse.InventoryData>list=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(queryRequest.getCargoList())){
            for (String cargo : queryRequest.getCargoList()) {
                BaseSku baseSku=baseSkuService.queryByOutGoodsNoAndWarehouseCode(cargo,queryRequest.getWarehouseCode());
                YuncWmsInventoryQueryResponse.InventoryData inventory=new YuncWmsInventoryQueryResponse.InventoryData();
                inventory.setWarehouseCode(queryRequest.getWarehouseCode());
                inventory.setOwnerCode("0");
                inventory.setCargoCode(cargo);
                inventory.setApiTime(System.currentTimeMillis()/1000);
                if (baseSku==null){
                    inventory.setGoodQty(0);
                    inventory.setDefectiveQty(0);
                    inventory.setDetails(new ArrayList<>());
                    list.add(inventory);
                }else {
                    List<YuncWmsInventoryQueryResponse.Detail>details=new ArrayList<>();
                    int[] qtys=getSumStock(baseSku.getGoodsNo(),details,baseSku,queryRequest.getQueryType());
                    inventory.setGoodQty(qtys[0]);
                    inventory.setDefectiveQty(qtys[1]);
                    inventory.setDetails(details);
                    list.add(inventory);
                }
            }
        }
        data.setList(list);
        data.setTotal((long) list.size());
        return data;
    }

    private static int[] getSumStock(String goodsNo, CustomerKeyDto customerKeyDto) {
        List<String>list=new ArrayList<>();
        list.add(goodsNo);
        cn.hutool.json.JSONObject obj=FluxUtils.queryStock(1,100,list,customerKeyDto);
        List<InvLotLocIdAtt>records=obj.getJSONArray("records").toList(InvLotLocIdAtt.class);
        if (CollectionUtil.isEmpty(records))
            return new int[]{0,0};
        int[] stock=new int[2];
        for (InvLotLocIdAtt record : records) {
            if (StringUtil.equals(record.getLotAtt08(),"良品")){
                stock[0]+=record.getQty();
            }else {
                stock[1]+=record.getQty();
            }
        }
        return stock;
    }

    /**
     *
     * @param goodsNo 查询库存的货号
     * @param details 要返回的库存明细，由调用层提供，参数级传递，只要不是重新实例化，调用层不会丢失引用
     * @param baseSku 商品基本数据，由调用层提供
     * @param queryType 抖音查询选项，1按效期维度，2仅良次品的最低维度
     * @return
     */
    private int[] getSumStock(String goodsNo,List<YuncWmsInventoryQueryResponse.Detail>details,BaseSku baseSku,int queryType) {
        List<StockDto>stockDtos=baseSkuService.queryDetailStock(goodsNo);
        if (CollectionUtil.isEmpty(stockDtos))
            return new int[]{0,0};
        int[] stock=new int[2];
        for (StockDto stockDto : stockDtos) {
            YuncWmsInventoryQueryResponse.Detail detail=new YuncWmsInventoryQueryResponse.Detail();
            detail.setQty(stockDto.getAvaQty());
            detail.setInventoryType(StringUtil.equals(stockDto.getAvaOrDef(), "良品") ? 1 : 2);
            detail.setInventoryStatus(1);
            detail.setBatchNumber(stockDto.getWmsBatchNo());
            if (StringUtil.isBlank(stockDto.getProdDate())&&baseSku.getLifecycle()!=null&&baseSku.getLifecycle()!=0){
                if (StringUtil.isBlank(stockDto.getExpireDate()))
                    detail.setProductDate(0L);
                else
                    detail.setProductDate(DateUtils.parseDate(stockDto.getExpireDate()).getTime()/1000-baseSku.getLifecycle()*24*3600);
            }else if (StringUtil.isBlank(stockDto.getProdDate())&&(baseSku.getLifecycle()==null||baseSku.getLifecycle()==0)){
                detail.setProductDate(0L);
            }else {
                detail.setProductDate(DateUtils.parseDate(stockDto.getProdDate()).getTime()/1000L);
            }
            if (StringUtil.isBlank(stockDto.getExpireDate())){
                if (StringUtil.isNotBlank(stockDto.getProdDate())){
                    if (baseSku.getLifecycle()!=null&&baseSku.getLifecycle()!=0){
                        detail.setExpireDate(DateUtils.parseDate(stockDto.getProdDate()).getTime()/1000+baseSku.getLifecycle()*24*3600);
                    }else {
                        detail.setExpireDate(0L);
                    }
                }else {
                    detail.setExpireDate(0L);
                }
            }else {
                detail.setExpireDate(DateUtils.parseDate(stockDto.getExpireDate()).getTime()/1000);
            }
            if (StringUtil.isBlank(stockDto.getInStockDate()))
                detail.setReceiptDate(0L);
            else
                detail.setReceiptDate(DateUtils.parseDate(stockDto.getInStockDate()).getTime()/1000);
            detail.setSupplierId("");
            detail.setScmInboundOrder("");
            if (queryType==2){
                detail.setBatchNumber("");
                detail.setProductDate(0L);
                detail.setExpireDate(0L);
                detail.setReceiptDate(0L);
                if (details.indexOf(detail)!=-1){
                    YuncWmsInventoryQueryResponse.Detail old=details.get(details.indexOf(detail));
                    old.setQty(old.getQty()+detail.getQty());
                }else
                    details.add(detail);
            }else{
                details.add(detail);
            }
            if (StringUtil.equals(stockDto.getAvaOrDef(),"良品")){
                stock[0]+=stockDto.getAvaQty();
            }else {
                stock[1]+=stockDto.getAvaQty();
            }
        }
        return stock;
    }

    // 回传退货收货
    @Override
    public synchronized void confirmReturnBook(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryById(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_305.getCode().intValue())
            throw new BadRequestException("该状态不能回传收货：" + orderReturn.getStatus());
        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }

        // 如果decFlag未0，那么就暂时不回传收货
        if (StringUtils.equals("0", orderReturn.getDecFlag())) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_310.getCode());
            orderReturn.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
            return;
        }

        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("1");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));
        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传收货,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传收货,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_310.getCode());
            orderReturn.setTakeBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_310.getCode()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }


    }

    // 回传退货质检通过
    @Override
    public synchronized void confirmReturnCheck(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_315.getCode().intValue())
            throw new BadRequestException("该状态不能回传质检通过：" + orderReturn.getStatus());

        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }

        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("2");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传质检通过,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传质检通过,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_317.getCode());
            orderReturn.setCheckBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);

            // 质检通过开始申报
            cbReturnProducer.send(
                    MsgType.CB_RETURN_325,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo()
            );
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_317.getCode()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }
    }

    // 回传退货质检异常
    @Override
    public void confirmReturnCheckErr(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_315.getCode().intValue()
                && orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_310.getCode().intValue())
            throw new BadRequestException("该状态不能回传质检异常：" + orderReturn.getStatus());
        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }
        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("3");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

        request.setVendor(vendor);

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode("QC002");
        errorInfo.setErrorMsg("其他");
        request.setReason(JSON.toJSONString(errorInfo));

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传质检异常,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传质检异常,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setCheckBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);

            cbReturnProducer.delaySend(
                    MsgType.CB_RETURN_375,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo(),
                    2000
            );
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
        }

    }

    // 回传退货申报开始
    @Override
    public synchronized void confirmReturnDecStart(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryById(Long.valueOf(id));
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_325.getCode().intValue())
            throw new BadRequestException("该状态不能申报开始：" + orderReturn.getStatus());

        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }

        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("4");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传申报开始,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传申报开始,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_330.getCode());
            orderReturn.setDeclareStartBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_330.getCode()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
        }
    }

    @Override
    public void confirmReturnDecErr(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));
        try {
            if (orderReturn == null)
                throw new BadRequestException("订单不存在");
            if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_340.getCode().intValue())
                throw new BadRequestException("该状态不能申报异常：" + orderReturn.getStatus());

            ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
            }

            // 回传抖音
            CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
            request.setLogisticsNo(orderReturn.getLogisticsNo());
            request.setStatus("6");
            request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

            request.setVendor(vendor);

            dySupport.setAccessToken(shopToken.getAccessToken());
            dySupport.setApiParam(request);
            log.info("抖音退货回传申报异常,单号：{},请求参数：{}", orderReturn.getLogisticsNo(), dySupport.getApiParam());
            DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
            log.info("抖音退货回传申报异常,单号：{},返回：{}", orderReturn.getLogisticsNo(), response);
            if (response.isSuccess()) {
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_345.getCode());
                orderReturnService.update(orderReturn);
                OrderReturnLog log = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(orderReturn.getStatus()),
                        request.toString(),
                        response.toString(),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderReturnLogService.create(log);

                cbReturnProducer.delaySend(
                        MsgType.CB_RETURN_375,
                        String.valueOf(orderReturn.getId()),
                        orderReturn.getOrderNo(),
                        2000
                );
            }else {
                OrderReturnLog log = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(CBReturnOrderStatusEnum.STATUS_345.getCode()),
                        request.toString(),
                        response.toString(),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderReturnLogService.create(log);
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getTradeReturnNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_345.getCode()),
                    "",
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderReturnLogService.create(log);
            e.printStackTrace();
            throw e;
        }
    }


    // 回传退货申报完成
    @Override
    public synchronized void confirmReturnDecEnd(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));
        try {
            if (orderReturn == null)
                throw new BadRequestException("订单不存在");
            if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_335.getCode().intValue())
                throw new BadRequestException("该状态不能回传申报完后：" + orderReturn.getStatus());

            ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
            if (shopToken == null) {
                throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
            }

            // 回传抖音
            CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
            request.setLogisticsNo(orderReturn.getLogisticsNo());
            request.setStatus("5");
            request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

            request.setVendor(vendor);

            dySupport.setAccessToken(shopToken.getAccessToken());
            dySupport.setApiParam(request);
            log.info("抖音退货回传申报完成,单号：{},请求参数：{}", orderReturn.getLogisticsNo(), dySupport.getApiParam());
            DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
            log.info("抖音退货回传申报完成,单号：{},返回：{}", orderReturn.getLogisticsNo(), response);
            if (response.isSuccess()) {
                orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_337.getCode());
                orderReturn.setDeclareEndBackTime(new Timestamp(System.currentTimeMillis()));
                orderReturnService.update(orderReturn);
                OrderReturnLog log = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(orderReturn.getStatus()),
                        request.toString(),
                        response.toString(),
                        BooleanEnum.SUCCESS.getCode(),
                        BooleanEnum.SUCCESS.getDescription()
                );
                orderReturnLogService.create(log);
//                if (StringUtils.equals("1", orderReturn.getIsWave())) {
//                    // 推送wms退货入库单
//                    wmsSupport.pushResturn(orderReturn);
//                }
            }else {
                OrderReturnLog log = new OrderReturnLog(
                        orderReturn.getId(),
                        orderReturn.getOrderNo(),
                        String.valueOf(CBReturnOrderStatusEnum.STATUS_337.getCode()),
                        request.toString(),
                        response.toString(),
                        BooleanEnum.FAIL.getCode(),
                        response.getMessage()
                );
                orderReturnLogService.create(log);
                throw new BadRequestException(response.getMessage());
            }
        }catch (Exception e) {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn==null?0L:orderReturn.getId(),
                    orderReturn==null?"0":orderReturn.getOrderNo(),
                    String.valueOf(CBReturnOrderStatusEnum.STATUS_337.getCode()),
                    "",
                    "",
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderReturnLogService.create(log);
            e.printStackTrace();
            throw e;
        }

    }

    // 回传保税仓上架
    @Override
    public synchronized void confirmReturnGround(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));
        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_355.getCode().intValue())
            throw new BadRequestException("该状态不能回传保税仓上架：" + orderReturn.getStatus());

        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }

        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("7");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));
        request.setWarehouseCode("4PLFLBBC01");
        request.setLogisticsFulfilNo(orderReturn.getLogisticsFulfilNo());

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传保税仓上架,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传保税仓上架,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());// 关单
            orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);

        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }
    }


    // 退货回传理货完成
    @Override
    public void confirmTally(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));

        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_310.getCode().intValue())
            throw new BadRequestException("该状态不能回传理货完成：" + orderReturn.getStatus());
        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }
        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("8");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传理货完成,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传理货完成,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setTallyBackTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);

            cbReturnProducer.delaySend(
                    MsgType.CB_RETURN_375,
                    String.valueOf(orderReturn.getId()),
                    orderReturn.getOrderNo(),
                    2000
            );
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }
    }

    // 退货回传退货仓上架
    @Override
    public void confirmOutReturnGround(String id) throws Exception {
        OrderReturn orderReturn = orderReturnService.queryByIdWithDetails(Long.valueOf(id));

        if (orderReturn == null)
            throw new BadRequestException("订单不存在");
        if (orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_310.getCode().intValue()
                && orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_315.getCode().intValue()
                && orderReturn.getStatus().intValue() != CBReturnOrderStatusEnum.STATUS_345.getCode().intValue())
            throw new BadRequestException("该状态不能回传质检通过：" + orderReturn.getStatus());

        ShopToken shopToken = shopTokenService.queryByShopId(orderReturn.getShopId());
        if (shopToken == null) {
            throw new BadRequestException("店铺未配置授权信息：" + orderReturn.getLogisticsNo());
        }
        // 回传抖音
        CBReturnOrderStatusRequest request = new CBReturnOrderStatusRequest();
        request.setLogisticsNo(orderReturn.getLogisticsNo());
        request.setStatus("9");
        request.setOccurrenceTime(String.valueOf(System.currentTimeMillis()/1000));

        request.setVendor(vendor);

        dySupport.setAccessToken(shopToken.getAccessToken());
        dySupport.setApiParam(request);
        log.info("抖音退货回传退货仓上架,单号：{},请求参数：{}", orderReturn.getOrderNo(), dySupport.getApiParam());
        DYCommonResponse <EmptyResponse> response = dySupport.request(EmptyResponse.class);
        log.info("抖音退货回传退货仓上架,单号：{},返回：{}", orderReturn.getOrderNo(), response);
        if (response.isSuccess()) {
            orderReturn.setStatus(CBReturnOrderStatusEnum.STATUS_900.getCode());// 关单
            orderReturn.setCloseTime(new Timestamp(System.currentTimeMillis()));
            orderReturnService.update(orderReturn);
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.SUCCESS.getCode(),
                    BooleanEnum.SUCCESS.getDescription()
            );
            orderReturnLogService.create(log);
        }else {
            OrderReturnLog log = new OrderReturnLog(
                    orderReturn.getId(),
                    orderReturn.getOrderNo(),
                    String.valueOf(orderReturn.getStatus()),
                    request.toString(),
                    response.toString(),
                    BooleanEnum.FAIL.getCode(),
                    response.getMessage()
            );
            orderReturnLogService.create(log);
            throw new BadRequestException(response.getMessage());
        }

    }

    // 自动小时播报
    @Override
    public void reportHourOrder() {
        Map<String, Map<String, Integer>> result = crossBorderOrderService.reportHourOrder();

        StringBuilder str = new StringBuilder();
        str.append("小时").append("&nbsp;")
                .append("接单").append("&nbsp;")
                .append("已接单").append("&nbsp;")
                .append("出库").append("&nbsp;")
                .append("已出库").append("&nbsp;")
                .append("未出");
        for (Map.Entry entry : result.entrySet()) {
            str.append("&#10;");
            String key = String.valueOf(entry.getKey());
            Map<String, Integer> value = (Map<String, Integer>) entry.getValue();
            str.append(key).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")
                    .append(value.get("hourIn")).append("&nbsp;&nbsp;&nbsp;&nbsp;")
                    .append(value.get("allIn")).append("&nbsp;&nbsp;&nbsp;&nbsp;")
                    .append(value.get("hourOut")).append("&nbsp;&nbsp;&nbsp;&nbsp;")
                    .append(value.get("allOut")).append("&nbsp;&nbsp;&nbsp;&nbsp;")
                    .append(value.get("allWait"));
        }

        String url = "https://open.feishu.cn/open-apis/bot/v2/hook/28920757-5134-48b7-b823-650744545277";
        JSONObject body = new JSONObject();
        body.put("msg_type", "post");
        JSONObject content = new JSONObject();
        JSONObject post = new JSONObject();
        JSONObject zh_cn = new JSONObject();
        zh_cn.put("title", "自动小时播报");
        JSONArray contentArrVar = new JSONArray();
        JSONArray contentArr = new JSONArray();
        JSONObject contentItem1 = new JSONObject();
        contentItem1.put("tag", "text");
        contentItem1.put("un_escape", true);
        contentItem1.put("text", str.toString());
        contentArr.add(contentItem1);
        contentArrVar.add(contentArr);
        zh_cn.put("content", contentArrVar);
        post.put("zh_cn", zh_cn);
        content.put("post", post);
        body.put("content", content);
        System.out.println(body.toString());
        String res = HttpRequest.post(url).body(body.toString()).execute().body();
        System.out.println(res);
    }



    public static void main(String[] args) {
        CustomerKeyDto dto=new CustomerKeyDto();
        dto.setCode("3302461510");
        dto.setSignKey("8cbcbce121f94415a97398b0c408bb40");
        getSumStock("12465881",dto);
    }


}
