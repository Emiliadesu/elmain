package me.zhengjie.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.cainiao.CaiNiaoSupport;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqCBOrderCommonServiceImpl implements MqCBOrderCommonService {
    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private QSService qsService;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private MeituanService meituanService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private OmsService omsService;

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private LogisticsUnarrivePlaceService logisticsUnarrivePlaceService;

    @Autowired
    private GuoMeiService guoMeiService;

    @Autowired
    private JackYunService jackYunService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private AikucunService aikucunService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CaiNiaoService caiNiaoService;

    @Autowired
    private  PddCloudPrintDataService pddCloudPrintDataService;

    @Override
    public void pullOrderByPage(String body) {

    }

    @Override
    public void createOrder(String body) {

    }

    @Override
    public void pushPrintInfoToWms(String body) {

    }

    /**
     * 回传接单
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmOrder(String orderId) throws Exception {
        Config config = configService.queryByK("ORDER_PULL");
        if (config != null && StringUtils.equals("0", config.getV())) {
            return;
        }
        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByIdWithDetails(Long.valueOf(orderId));
        if (StringUtil.equals(crossBorderOrder.getPlatformCode(), "MeiTuan") && StringUtil.isBlank(crossBorderOrder.getPaymentNo())) {
            //等待美团推送清关信息
            return;
        }

        // 获取清关信息
        kjgSupport.getDecinfo(crossBorderOrder);
        crossBorderOrderDetailsService.updateBatch(crossBorderOrder.getItemList());

        try {
            crossBorderOrderService.decrypt(crossBorderOrder);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("订单解密失败:" + e.getMessage());
        }
        if (!StringUtils.equals(PlatformConstant.DY, crossBorderOrder.getPlatformCode())) {
            // 抖音已经提前获取了运单
            // 获取运单
            if (!StringUtil.equals(PlatformConstant.PDD,crossBorderOrder.getPlatformCode())) {
                try {
                    logisticsInfoService.getLogistics(crossBorderOrder);
                }catch (Exception e){
                    e.printStackTrace();
                    crossBorderOrder.setStatus(999);
                    crossBorderOrder.setFreezeReason(e.getMessage());
                    crossBorderOrderService.update(crossBorderOrder);
                    return;
                }
            }
        }else {
            if (!StringUtils.equals("1", crossBorderOrder.getLogisticsFourPl())) {
                // 非配4PL的单子才做处理
                DouyinMailMark douyinMailMark = douyinMailMarkService.queryByOrderNo(crossBorderOrder.getOrderNo());
                if (douyinMailMark == null || StringUtils.isBlank(douyinMailMark.getLogisticsNo()))
                    throw new BadRequestException("抖音订单未获取到运单：" + crossBorderOrder.getId());
                crossBorderOrder.setSupplierId(Long.valueOf(douyinMailMark.getSupplierId()));
                crossBorderOrder.setLogisticsNo(douyinMailMark.getLogisticsNo());
                crossBorderOrder.setAddMark(douyinMailMark.getAddMark());
//                List<LogisticsUnarrivePlace> unarrivePlaces =  logisticsUnarrivePlaceService.queryByCityAndDistrictAndLogisticsId(crossBorderOrder.getCity(), crossBorderOrder.getDistrict(), crossBorderOrder.getSupplierId());

//                if (StringUtil.contains(douyinMailMark.getAddMark(), "停发") || CollectionUtils.isNotEmpty(unarrivePlaces)) {
//                    // 临时逻辑，中通停发的单子，直接冻结
//                    crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_999.getCode());
//                    crossBorderOrder.setFreezeReason("疫情区域停发");
//                    crossBorderOrderService.update(crossBorderOrder);
//                    OrderLog freezeLog = new OrderLog(
//                            crossBorderOrder.getId(),
//                            crossBorderOrder.getOrderNo(),
//                            String.valueOf(CBOrderStatusEnum.STATUS_999.getCode()),
//                            BooleanEnum.SUCCESS.getCode(),
//                            "疫情区域停发"
//                    );
//                    orderLogService.create(freezeLog);
//                    return;
//                }
            }

        }
        crossBorderOrderService.update(crossBorderOrder);
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("PDD",platformCode)){
            pddOrderService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("YZ",platformCode)){
            youZanOrderService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("Ymatou",platformCode)){
            ymatouService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("MGJ",platformCode)){
            moGuJieService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("MeiTuan",platformCode)){
            meituanService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmOrder(crossBorderOrder);
        }else if (StringUtil.equals("AiKuCun",platformCode)){
            aikucunService.confirmOrder(crossBorderOrder);
        }else {
            omsService.confirmOrder(crossBorderOrder);
        }
    }

    /**
     * 回传清关开始
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmClearStart(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryById(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmClearStart(crossBorderOrder);
        }else if (StringUtil.equals("PDD",platformCode)){
            pddOrderService.confirmClearStart(crossBorderOrder);
        }else if (StringUtil.equals("YZ",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("Ymatou",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("MGJ",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("MeiTuan",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmClearStart(crossBorderOrder);
        }else if (StringUtil.equals("AiKuCun",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_225.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else {
            omsService.confirmClearStart(crossBorderOrder);
        }
    }

    /**
     * 回传清关完成
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmClearSuccess(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryByIdWithDetails(Long.parseLong(orderId));

        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmClearSuccess(crossBorderOrder);
        }else if (StringUtil.equals("PDD",platformCode)){
            pddOrderService.confirmClearSuccess(crossBorderOrder);
        }else if (StringUtil.equals("YZ",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("Ymatou",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("MGJ",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("MeiTuan",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmClearSuccess(crossBorderOrder);
        }else if (StringUtil.equals("AiKuCun",platformCode)){
            crossBorderOrder.setStatus(CBOrderStatusEnum.STATUS_235.getCode());
            crossBorderOrderService.update(crossBorderOrder);
        }else {
            omsService.confirmClearSuccess(crossBorderOrder);
        }
        if (!StringUtil.equals("DY",platformCode)) {
            ShopInfo shopInfo = shopInfoService.findById(crossBorderOrder.getShopId());
            if (shopInfo!=null){
                if (StringUtil.equals(shopInfo.getPushTo(),"1")){
                    //菜鸟关税回传、清关回执回传
                    if (StringUtil.isBlank(crossBorderOrder.getLpCode())){
                        PddCloudPrintData printData = pddCloudPrintDataService.findByOrderNo(crossBorderOrder.getOrderNo());
                        String lpCode = caiNiaoService.sendOrderToWmsAsPdd(crossBorderOrder,printData);
                        crossBorderOrder.setLpCode(lpCode);
                        crossBorderOrderService.update(crossBorderOrder);
                    }
                    if (StringUtil.isBlank(crossBorderOrder.getInvtNo())){
                        crossBorderOrderService.refreshDecInfo(crossBorderOrder.getId()+"");
                    }
                    cbOrderProducer.delaySend(
                            MsgType.CN_PUSH_CLEAR_SUCC,
                            crossBorderOrder.getId()+"",
                            crossBorderOrder.getOrderNo(),
                            60000L
                    );
                    cbOrderProducer.delaySend(
                            MsgType.CN_PUSH_TAX,
                            crossBorderOrder.getId()+"",
                            crossBorderOrder.getOrderNo(),
                            70000L
                    );
//                    caiNiaoService.taxAmountCallback(crossBorderOrder);
//                    caiNiaoService.declareResultCallBack(crossBorderOrder);
                }
                else if (StringUtils.isNotBlank(crossBorderOrder.getOrderMsg())){
                    wmsSupport.pushOrder(crossBorderOrder);
                }
            }else if (StringUtils.isNotBlank(crossBorderOrder.getOrderMsg()))
                wmsSupport.pushOrder(crossBorderOrder);
        }
    }

    /**
     * 回传 开始撤单
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmDelClearStart(String orderId) throws Exception {
        CrossBorderOrder order=crossBorderOrderService.queryByIdWithDetails(Long.parseLong(orderId));
        douyinService.confirmDelClearStart(order);
    }

    /**
     * 回传 撤单完成
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmDelClearSuccess(String orderId) throws Exception {
        CrossBorderOrder order=crossBorderOrderService.queryByIdWithDetails(Long.parseLong(orderId));
        douyinService.confirmDelClearSuccess(order);
    }

    /**
     * 回传 撤单失败
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmDelClearFail(String orderId) throws Exception {

    }

    /**
     * 回传 关单完成
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmCloseClearSuccess(String orderId) throws Exception {
        CrossBorderOrder order=crossBorderOrderService.queryByIdWithDetails(Long.parseLong(orderId));
        douyinService.confirmCloseClearSuccess(order);
    }

    /**
     * 回传拣货开始
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmPickStart(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryById(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmPickStart(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmPickStart(crossBorderOrder);
        }
    }

    /**
     * 回传拣货完后
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmPickEnd(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryById(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmPickEnd(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmPickEnd(crossBorderOrder);
        }
    }

    /**
     * 回传称重完成
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmWeight(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryById(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmPack(crossBorderOrder);
        }else if (StringUtil.equals("PDD",platformCode)){
            for (int i = 0; i < 50; i++) {
                try {
                    pddOrderService.confirmDeliver(crossBorderOrder);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage(),e);
                    if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                        throw new BadRequestException(e.getMessage());
                    }else if (i > 48)
                        throw new BadRequestException(e.getMessage());
                }
            }
        }else if (StringUtil.equals("YZ",platformCode)){
            youZanOrderService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("QS",platformCode)){
            qsService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("Ymatou",platformCode)){
            ymatouService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("MGJ",platformCode)){
            moGuJieService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("MeiTuan",platformCode)){
            meituanService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmPack(crossBorderOrder);
        }else if (StringUtil.contains(platformCode,"-JCY")) {
            List<CrossBorderOrderDetails>detailsList= crossBorderOrderDetailsService.queryByOrderId(crossBorderOrder.getId());
            crossBorderOrder.setItemList(detailsList);
            jackYunService.deliver(crossBorderOrder);
        }else if (StringUtil.equals("AiKuCun",platformCode)){
            aikucunService.confirmPack(crossBorderOrder);
        }
        else {
            omsService.confirmDeliver(crossBorderOrder);
        }
        cbOrderProducer.delaySend(
                MsgType.CB_ORDER_DOWNLOAD_CACHE,
                orderId,
                crossBorderOrder.getOrderNo(),
                10000);
    }

    @Override
    public void pushPayOrder(String body) throws Exception{
        CrossBorderOrder order=crossBorderOrderService.queryById(Long.parseLong(body));
        try {
            if (StringUtil.equals(order.getPlatformCode(),"YZ")){
                youZanOrderService.pushPayOrder(order);
            }else if (StringUtil.equals(order.getPlatformCode(),"Ymatou")){
                ymatouService.pushPayOrder(order);
            }
        }catch (Exception e){
            order.setFreezeReason(e.getMessage());
            crossBorderOrderService.update(order);
            throw e;
        }
    }

    /**
     * 回传打包
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmPack(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryById(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmPack(crossBorderOrder);
        }
    }

    /**
     * 回传出库
     * @param orderId
     * @throws Exception
     */
    @Override
    public void confirmDeliver(String orderId) throws Exception {
        CrossBorderOrder crossBorderOrder=crossBorderOrderService.queryByIdWithDetails(Long.parseLong(orderId));
        String platformCode=crossBorderOrder.getPlatformCode();
        if (StringUtil.equals("DY",platformCode)){
            douyinService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("PDD",platformCode)){
            for (int i = 0; i < 50; i++) {
                try {
                    pddOrderService.confirmDeliver(crossBorderOrder);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(e.getMessage(),e);
                    if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                        throw new BadRequestException(e.getMessage());
                    }else if (i > 48)
                        throw new BadRequestException(e.getMessage());
                }
            }
        }else if (StringUtil.equals("QS",platformCode)){
            qsService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("Ymatou",platformCode)){
            ymatouService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("MGJ",platformCode)){
            moGuJieService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("MeiTuan",platformCode)){
            meituanService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("GM",platformCode)){
            guoMeiService.confirmDeliver(crossBorderOrder);
        }else if (StringUtil.equals("AiKuCun",platformCode)){
            aikucunService.confirmDeliver(crossBorderOrder);
        }else {
            omsService.confirmDeliver(crossBorderOrder);
        }
    }


}
