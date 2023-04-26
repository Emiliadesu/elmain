package me.zhengjie.mq;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.MqLog;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.DouyinService;
import me.zhengjie.service.MqLogService;
import me.zhengjie.service.OrderReturnService;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.BooleanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Slf4j
public class CBReturnConsumer implements Consumer {

    @Value("${spring.profiles.active}")
    private String env;

    private Properties properties;
    private Consumer consumer;
    private String topic;

    @Autowired
    private OrderReturnService orderReturnService;

    @Autowired
    private MqLogService mqLogService;

    @Autowired
    private DouyinService douyinService;


    @Override
    public void start() {
        if (!StringUtil.equals("dev", env)) {
            if (null == this.properties) {
                throw new ONSClientException("退货消息处理消费者启动失败：properties not set");
            }else {
                this.consumer = ONSFactory.createConsumer(this.properties);
                consumer.subscribe(topic, "*", new MessageListener() { //订阅全部Tag。
                    public Action consume(Message message, ConsumeContext context) {
                        String tag = message.getTag();
                        String key = message.getKey();
                        String msgId = message.getMsgID();
                        String body = new String(message.getBody());
                        log.info("msgId:" + msgId);
                        log.info("TAG:" + tag);
                        log.info("BODY:" + new String(message.getBody()));
                        try {
                            switch (tag) {
                                case MsgType.CB_RETURN_300:
                                    log.info("退货接单");
                                    douyinService.recReturnOrder(body);
                                    break;
                                case MsgType.CB_RETURN_310:
                                    log.info("退货收货回传");
                                    douyinService.confirmReturnBook(body);
                                    break;
                                case MsgType.CB_RETURN_317:
                                    // 质检通过
                                    douyinService.confirmReturnCheck(body);
                                    log.info("退货质检通过回传");
                                    break;
                                case MsgType.CB_RETURN_323:
                                    // 质检异常
                                    douyinService.confirmReturnCheckErr(body);
                                    log.info("退货质检通过回传");
                                    break;
                                case MsgType.CB_RETURN_325:
                                    orderReturnService.declare(body);
                                    log.info("开始申报");
                                    break;
                                case MsgType.CB_RETURN_330:
                                    douyinService.confirmReturnDecStart(body);
                                    // 开始操作清关
                                    log.info("退货申报开始回传");
                                    break;
                                case MsgType.CB_RETURN_337:
                                    douyinService.confirmReturnDecEnd(body);
                                    log.info("退货申报完成回传");
                                    break;
                                case MsgType.CB_RETURN_355:
                                    douyinService.confirmReturnGround(body);
                                    log.info("保税仓上架回传");
                                    break;
                                case MsgType.CB_RETURN_365:
                                    // 理货完成
                                    douyinService.confirmTally(body);
                                    log.info("理货完成回传");
                                    break;
                                case MsgType.CB_RETURN_375:
                                    douyinService.confirmOutReturnGround(body);
                                    log.info("退货仓上架回传");
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
                                    || StringUtil.contains(e.getMessage(), "内部错误")) {
                                // 如果出现以上异常就再次消费
                                return Action.ReconsumeLater;
                            }
                            return Action.CommitMessage;
                        }


                    }
                });

                String ip = StringUtils.getLocalIp();
                if (StringUtil.equals(ip, "10.0.6.114")) {
                    // 只启动一台机器来处理退货
                    this.consumer.start();
                    log.info("退货消息队列消费者启动成功，环境：" + this.env + "TOPIC:" + this.topic );
                }
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
