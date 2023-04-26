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
public class DomesticOrderConsumer implements Consumer {

    @Value("${spring.profiles.active}")
    private String env;

    private Properties properties;
    private Consumer consumer;
    private String topic;

    @Autowired
    private DomesticOrderService domesticOrderService;

    @Autowired
    private MqLogService mqLogService;



    @Override
    public void start() {
        if (!StringUtil.equals("dev", env)) {
            if (null == this.properties) {
                throw new ONSClientException("国内订单消息处理消费者启动失败：properties not set");
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
                        try {
                            switch (tag) {
                                case MsgType.DM_ORDER_215:
                                    domesticOrderService.confirmOrder(Long.valueOf(body));
                                    log.info("国内订单接单");
                                    break;
                                case MsgType.DM_ORDER_220:
                                    domesticOrderService.checkPass(Long.valueOf(body));
                                    log.info("国内订单审核通过");
                                    break;
                                case MsgType.DM_ORDER_235:
                                    domesticOrderService.preHandle(Long.valueOf(body));
                                    log.info("国内订单预处理完后曾");
                                    break;
                                case MsgType.DM_ORDER_245:
                                    domesticOrderService.confirmDeliver(Long.valueOf(body));
                                    log.info("国内订单出库");
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
                            return Action.CommitMessage;

                        }
                    }
                });
                this.consumer.start();
                log.info("国内订单消息队列消费者启动成功，环境：" + this.env + "TOPIC:" + this.topic );
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
