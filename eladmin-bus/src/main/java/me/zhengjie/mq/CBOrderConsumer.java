package me.zhengjie.mq;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.MqLog;
import me.zhengjie.service.*;
import me.zhengjie.utils.RedisUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Slf4j
public class CBOrderConsumer implements Consumer {

    @Value("${spring.profiles.active}")
    private String env;

    private Properties properties;
    private Consumer consumer;
    private String topic;

    @Autowired
    private CrossBorderOrderService orderService;

    @Autowired
    private MqLogService mqLogService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private MqCBOrderCommonService mqCBOrderCommonService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private MeituanService meituanService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private GuoMeiService guoMeiService;

    @Autowired
    private DailyCrossBorderOrderService dailyCrossBorderOrderService;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private AikucunService aikucunService;

    @Autowired
    private PddCloudPrintDataService pddCloudPrintDataService;

    @Autowired
    private PddCloudPrintLogService pddCloudPrintLogService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private WmsOutstockService wmsOutstockService;

    @Autowired
    private DyCangzuFeeService dyCangzuFeeService;

    @Autowired
    private  CaiNiaoService caiNiaoService;

    @Autowired
    private  DewuDeclarePushService dewuDeclarePushService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public void start() {
        if (!StringUtil.equals("dev", env)) {
            if (null == this.properties) {
                throw new ONSClientException("跨境订单消息处理消费者启动失败：properties not set");
            }else {
                this.consumer = ONSFactory.createConsumer(this.properties);

                consumer.subscribe(topic, "*", new MessageListener() { //订阅全部Tag。
                    public Action consume(Message message, ConsumeContext context) {
                        String tag = message.getTag();
                        String key = message.getKey();
                        String body = new String(message.getBody());
                        String msgId = message.getMsgID();
                        log.info("msgId:" + msgId);
                        log.info("TAG:" + tag);
                        log.info("BODY:" + new String(message.getBody()));

                        // 只有接单、接单回传、清关开始才去重
//                        if (StringUtils.equals(tag, MsgType.CB_ORDER_200) ||
//                                StringUtils.equals(tag, MsgType.CB_ORDER_215) ||
//                                StringUtils.equals(tag, MsgType.CB_ORDER_220) ) {
//                            // 根据msgId做消息幂等，防止重复消费
//                            // 1.先查询当先msgId是否存在redis缓存
//                            String redisKey = String.valueOf(redisUtils.get(msgId));
//                            // 2.如果存在就直接消费成功，如果不存在就继续消费下去(这个方案会导致补偿业务异常补偿方案失效，先观察一段时间)
//                            if (StringUtils.isNotBlank(redisKey)
//                                    && !"null".equals(redisKey)) {
//                                log.info("msgId:{},重复消费，直接返回成功", msgId );
//                                return Action.CommitMessage;
//                            }
//                            // 保存redis缓存
//                            redisUtils.set(msgId, msgId, 60);
//                        }
                        try {
                            switch (tag) {
                                case MsgType.CB_ORDER_PULL:
                                    // 分页拉单
                                    log.info("抖音分页拉单");
                                    douyinService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_PULL_PDD:
                                    // 分页拉单
                                    log.info("拼多多分页拉单");
                                    pddOrderService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_PULL_YZ:
                                    // 分页拉单
                                    log.info("有赞分页拉单");
                                    youZanOrderService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_PULL_MGJ:
                                    // 分页拉单
                                    log.info("蘑菇街分页拉单");
                                    moGuJieService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_PULL_YMT:
                                    // 分页拉单
                                    log.info("洋码头分页拉单");
                                    ymatouService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_200:
                                    // 下发订单，开始保存
                                    log.info("抖音下发订单，开始保存");
                                    douyinService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_200_PDD:
                                    // 下发订单，开始保存
                                    log.info("拼多多下发订单，开始保存");
                                    pddOrderService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_200_YZ:
                                    // 下发订单，开始保存
                                    log.info("有赞下发订单，开始保存");
                                    youZanOrderService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_200_MGJ:
                                    // 下发订单，开始保存
                                    log.info("蘑菇街下发订单，开始保存");
                                    moGuJieService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_200_YMT:
                                    // 下发订单，开始保存
                                    log.info("洋码头下发订单，开始保存");
                                    ymatouService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_205:
                                    // 推送支付单
                                    log.info("推送支付单");
                                    mqCBOrderCommonService.pushPayOrder(body);
                                    break;
                                case MsgType.CB_ORDER_INSTER_PRINT:
                                    // 下发订单，开始保存
                                    log.info("保存面单信息");
                                    douyinService.pushPrintInfoToWms(body);
                                    break;
                                case MsgType.CB_ORDER_215:
                                    // 接单回传，此处给有需求的平台回传接单
                                    mqCBOrderCommonService.confirmOrder(body);
                                    log.info("接单回传，此处给有需求的平台回传接单");
                                    break;
                                case MsgType.CB_ORDER_220:
                                    orderService.declare(body);
                                    // 开始操作清关
                                    log.info("开始操作清关");
                                    break;
                                case MsgType.CB_ORDER_REFRESH:
                                    orderService.refreshClearStatus(body);
                                    // 开始操作清关
                                    log.info("开始操作清关");
                                    break;
                                case MsgType.CB_ORDER_225:
                                    // 清关开始回传
                                    mqCBOrderCommonService.confirmClearStart(body);
                                    log.info("清关开始回传");
                                    break;
                                case MsgType.CB_ORDER_235:
                                    // 清关完成回传
                                    mqCBOrderCommonService.confirmClearSuccess(body);
                                    log.info("清关完成回传");
                                    break;
                                case MsgType.CB_ORDER_236:
                                    // 拣货开始回传
                                    mqCBOrderCommonService.confirmPickStart(body);
                                    log.info("拣货回传");
                                    break;
                                case MsgType.CB_ORDER_2361:
                                    // 拣货完成回传
                                    mqCBOrderCommonService.confirmPickEnd(body);
                                    log.info("拣货回传");
                                    break;
                                case MsgType.CB_ORDER_WMS:
                                    // 刷新WMS状态
                                    orderService.refreshWmsStatus(body);
                                    log.info("刷新WMS状态");
                                    break;
                                case MsgType.CB_ORDER_237:
                                    // 称重完成
                                    mqCBOrderCommonService.confirmPack(body);
                                    log.info("称重完成");
                                    break;
                                case MsgType.CB_ORDER_240:
                                    // 称重完成
                                    mqCBOrderCommonService.confirmWeight(body);
                                    log.info("称重完成");
                                    break;
                                case MsgType.CB_ORDER_245:
                                    // 出库
                                    mqCBOrderCommonService.confirmDeliver(body);
                                    log.info("出库，回传平台出库");
                                    break;
                                case MsgType.CB_ORDER_888:
                                    // 刷新取消状态
                                    orderService.refreshCancelStatus(body);
                                    log.info("刷新取消状态");
                                    break;
                                case MsgType.CB_REFRESH_ORDER_STATUS:
                                    // 刷新订单平台状态
                                    orderService.refreshPlatformStatus(body);
                                    log.info("刷新取消状态");
                                    break;
                                case MsgType.CB_ORDER_PULL_ORDERNO_DY:
                                    // 根据订单号拉取订单
                                    douyinService.pullOrderByOrderNo(body);
                                    log.info("根据订单号拉取订单");
                                    break;
                                case MsgType.CB_UPDATE_WMS_TIME:
                                    // 更新WMS订单时间
                                    orderService.updateWmsOrderTime(body);
                                    log.info("更新WMS订单时间");
                                    break;
                                case MsgType.DY_ORDERINTERCEPTION:
                                    // 抖音锁单
                                    douyinService.lockOrder(body);
                                    log.info("抖音回告锁单");
                                    break;
                                case MsgType.DY_ORDERINTERCEPTION_NOTIFY:
                                    // 抖音锁单
                                    douyinService.cancelOrder(body);
                                    log.info("抖音回告锁单");
                                    break;
                                case MsgType.DY_PUSH_MAIL_NO:
                                    // 抖音推送运单
                                    douyinService.getMailNo(body);
                                    log.info("抖音推送运单");
                                    break;
                                case MsgType.MEITUAN_REFUND_REJECT:
                                    meituanService.refundReject(body);
                                    log.info("美团回告拒绝退款");
                                    break;
                                case MsgType.MEITUAN_LOCK:
                                    meituanService.refundOrder(body);
                                    log.info("美团退款通知");
                                    break;
                                case MsgType.MEITUAN_CANCEL:
                                    meituanService.cancelOrder(body);
                                    log.info("美团回告取消订单");
                                    break;
                                case MsgType.MEITUAN_SET_CROSSBORD_INFO:
                                    meituanService.setDeclareInfo(body);
                                    log.info("美团获取清关信息");
                                    break;
                                case MsgType.CB_UPGRADEUNCOLLECTEDORDER:
                                    orderService.checkCollected(body);
                                    log.info("刷新未揽收的订单");
                                    break;
                                case MsgType.CB_REFRESH_ORDER_STATUS_AND_CANCEL:
                                    orderService.refreshWmsStatusAndCancel(body);
                                    log.info("刷新订单平台状态并且取消/撤单 平台已取消 的订单");
                                    break;
                                case MsgType.CB_UPGRAD_DECINFO:
                                    orderService.refreshDecInfo(body);
                                    log.info("刷新清关信息");
                                    break;
                                case MsgType.SKU_PUSH_WMS:
                                    baseSkuService.pushWms(Long.valueOf(body));
                                    log.info("推送SKU到WMS");
                                    break;
                                case MsgType.CB_ORDER_GM_CANCEL:
                                    guoMeiService.orderCancelByMq(body);
                                    break;
                                case MsgType.CB_ORDER_880:
                                    //回传撤单开始
                                    mqCBOrderCommonService.confirmDelClearStart(body);
                                    break;
                                case MsgType.CB_ORDER_884:
                                    //回传撤单成功
                                    mqCBOrderCommonService.confirmDelClearSuccess(body);
                                    break;
                                case MsgType.CB_ORDER_886:
                                    //回传撤单成功
                                    mqCBOrderCommonService.confirmCloseClearSuccess(body);
                                    break;
                                case MsgType.CB_ORDER_DEC_DEL:
                                    //回传撤单成功
                                    orderService.updateDecDelStatus(Long.valueOf(body));
                                    break;
                                case MsgType.CB_ORDER_DOWNLOAD_CACHE:
                                    //出库成功，开始将订单导出信息缓存到DB
                                    dailyCrossBorderOrderService.addOrderCache(Long.valueOf(body));
                                    break;
                                case MsgType.CB_RK_CANCEL:
                                    inboundOrderService.dyConfirmCancel(Long.valueOf(body));
                                    break;
                                case MsgType.CB_CK_CANCEL:
                                    outboundOrderService.dyConfirmCancel(Long.valueOf(body));
                                    break;
                                case MsgType.CB_ORDER_PULL_AIKUCUN:
                                    aikucunService.pullOrderByPage(body);
                                    break;
                                case MsgType.CB_ORDER_200_AIKUCUN:
                                    aikucunService.createOrder(body);
                                    break;
                                case MsgType.CB_ORDER_PDD_SAVE_PRINTDATA:
                                    pddCloudPrintDataService.saveOrUpdateByMq(body);
                                    break;
                                case MsgType.CB_ORDER_PDD_PRINT_LOG_SAVE:
                                    pddCloudPrintLogService.saveLogByMq(body);
                                    break;
                                case MsgType.ZHUOZHI_OUT_SYNCSTATUS:
                                    wmsOutstockService.syncStatus(new Long[]{Long.valueOf(body)});
                                    break;
                                case MsgType.ZHUOZHI_IN_SYNCSTATUS:
                                    wmsInstockService.syncStatus(new Long[]{Long.valueOf(body)});
                                    break;
                                case MsgType.DY_CREATE_WAREHOUSE_FEE_ORDER:
                                    douyinService.createWarehouseFeeOrder(body);
                                    break;
                                case MsgType.CB_ORDER_200_MEITUAN:
                                    meituanService.createOrder(body);
                                    break;
                                case MsgType.DY_NOTIFY_ADJUST_RESULT:
                                    douyinService.notifyAdjustResult(body);
                                    break;
                                case MsgType.DY_PUSH_CANGZU_FEE:
                                    dyCangzuFeeService.pushFeeOrder(Long.valueOf(body));
                                    break;
                                case MsgType.CN_PUSH_TAX:
                                    caiNiaoService.taxAmountCallback(Long.valueOf(body));
                                    break;
                                case MsgType.CN_PUSH_CLEAR_SUCC:
                                    caiNiaoService.declareResultCallBack(Long.valueOf(body));
                                    break;
                                case MsgType.CN_LASTMINE_HOIN:
                                    caiNiaoService.lastmineHoinCallBack(Long.valueOf(body));
                                    break;
                                case MsgType.DW_215:
                                    dewuDeclarePushService.dewuConfirmOrder(Long.valueOf(body));
                                    break;
                                case MsgType.DW_220:
                                    dewuDeclarePushService.dewuDeclare(Long.valueOf(body));
                                    break;
                                case MsgType.DW_225:
                                    dewuDeclarePushService.dewuConfirmDeclareStart(Long.valueOf(body));
                                    break;
                                case MsgType.DW_230:
                                    dewuDeclarePushService.dewuRefreshDeclare(Long.valueOf(body));
                                    break;
                                case MsgType.DW_235:
                                    dewuDeclarePushService.dewuConfirmDeclareSucc(Long.valueOf(body));
                                    break;
                                case MsgType.DW_888:
                                    dewuDeclarePushService.dewuConfirmDelDeclareSucc(Long.valueOf(body));
                                    break;
                                default:
                                    log.error("未定义的TAG:" +tag);
                                    break;
                            }
                            MqLog log = new MqLog(
                                    topic,
                                    tag,
                                    msgId,
                                    key,
                                    body,
                                    BooleanEnum.SUCCESS.getCode(),
                                    BooleanEnum.SUCCESS.getDescription()
                            );
                            mqLogService.create(log);
                            return Action.CommitMessage;
                        }catch (Exception e) {
                            // 只保存异常日志
                            MqLog log = new MqLog(
                                    topic,
                                    tag,
                                    msgId,
                                    key,
                                    body,
                                    BooleanEnum.FAIL.getCode(),
                                    e.getMessage()
                            );
                            mqLogService.create(log);
                            if (StringUtil.contains(e.getMessage(), "sign校验失败")
                                    || StringUtil.contains(e.getMessage(), "请检查")
                                    || StringUtil.contains(e.getMessage(), "502")
                                    || StringUtil.contains(e.getMessage(), "504")
                                    || StringUtil.contains(e.getMessage(), "内部错误")
                                    || StringUtil.contains(e.getMessage(), "单号数量不足")
                                    || StringUtil.contains(e.getMessage(), "下单异常,请联系客服")
                                    || StringUtil.contains(e.getMessage(), "请求太频繁")
                                    || StringUtil.contains(e.getMessage(), "获取接口信息失败，请反馈")
                                    || StringUtil.contains(e.getMessage(), "服务异常")
                                    || StringUtil.contains(e.getMessage(), "timeout")
                                    || StringUtil.contains(e.getMessage(), "鉴权失败")
                                    || StringUtil.contains(e.getMessage(), "接口调用异常")
                                    || StringUtil.contains(e.getMessage(), "系统异常")
                                    || StringUtil.contains(e.getMessage(), "超时")
                                    || StringUtil.contains(e.getMessage(), "NotifyLogisticsInfo err")
                                    || StringUtil.contains(e.getMessage(), "timed out")
                                    || StringUtil.contains(e.getMessage(),"Sign错误")) {
                                // 如果出现以上异常就再次消费
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.DY_PUSH_MAIL_NO) ) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_240) &&
                                    StringUtil.contains(e.getMessage(), " 当前状态操作时间，不能早于上一个状态操作时间")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_235) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【清关完成】")) {
                                return Action.ReconsumeLater;
                            }

                            if (StringUtils.equals(tag, MsgType.CB_ORDER_237) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【订单拣货完成】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_225) &&
                                    StringUtil.contains(e.getMessage(), "服务商未接单成功")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_245) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【打包完成】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_245) &&
                                    StringUtil.contains(e.getMessage(), "请核实订单当前交易状态")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_240) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【订单开始拣货】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_240) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【清关完成】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_240) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【订单拣货完成】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_235) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【服务商拉单成功】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtils.equals(tag, MsgType.CB_ORDER_236) &&
                                    StringUtil.contains(e.getMessage(), "当前节点为【开始清关】")) {
                                return Action.ReconsumeLater;
                            }
                            if (StringUtil.equals(tag,MsgType.CB_ORDER_PDD_SAVE_PRINTDATA)&&StringUtil.contains(e.getMessage(),"SQL"))
                                return Action.ReconsumeLater;
                            if (StringUtil.equals(tag,MsgType.CB_ORDER_PULL_PDD) && StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                                return Action.ReconsumeLater;
                            }
                            if ((StringUtil.equals(tag,MsgType.CN_PUSH_CLEAR_SUCC)||StringUtil.equals(tag,MsgType.CN_PUSH_TAX)||StringUtil.equals(tag,MsgType.CN_LASTMINE_HOIN))
                                    && StringUtil.contains(e.getMessage(),"业务处理异常")){
                                return Action.ReconsumeLater;
                            }
                            if (StringUtil.equals(tag,MsgType.CN_PUSH_CLEAR_SUCC)&& StringUtil.contains(e.getMessage(),"该订单未进行库存预占")){
                                cbOrderProducer.delaySend(
                                        MsgType.CN_PUSH_TAX,
                                        body,
                                        key,
                                        5000L
                                );
                                return Action.CommitMessage;
                            }
                            return Action.CommitMessage;

                        }
                    }
                });
                this.consumer.start();
                log.info("订单消息队列消费者启动成功，环境：" + this.env + "TOPIC:" + this.topic );
            }
        }else {
            log.info("非正式环境不启动MQ...");
        }

    }

    @Override
    public void shutdown() {
        if (this.consumer != null) {
            this.consumer.shutdown();
        }
    }

    @Override
    public void subscribe(String s, String s1, MessageListener messageListener) {
        if (null == this.consumer) {
            throw new ONSClientException("subscribe must be called after OrderConsumerBean started");
        } else {
            this.consumer.subscribe(s, s1, messageListener);
        }
    }

    @Override
    public void subscribe(String s, MessageSelector messageSelector, MessageListener messageListener) {
        if (null == this.consumer) {
            throw new ONSClientException("subscribe must be called after OrderConsumerBean started");
        } else {
            this.consumer.subscribe(s, messageSelector, messageListener);
        }
    }

    @Override
    public void unsubscribe(String s) {
        this.consumer.unsubscribe(s);
    }

    @Override
    public boolean isStarted() {
        return this.consumer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.consumer.isClosed();
    }


    @Override
    public void updateCredential(Properties properties) {
        if (this.consumer != null) {
            this.consumer.updateCredential(properties);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
