package me.zhengjie.mq;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;
import java.util.concurrent.ExecutorService;

@Slf4j
public class DomesticOrderProducer implements Producer{

    @Value("${spring.profiles.active}")
    private String env;

    private Properties properties;
    private Producer producer;
    private String topic;


    /**
     * 订单消息发送
     * @param msgType 消息类型
     * @param msg 消息主体
     * @param orderNo 业务单号
     * @return
     */
    public void send(String msgType, String msg, String orderNo) {
        Message message = new Message(
                this.topic,
                msgType,
                msg.getBytes()
        );
        if (StringUtil.isNotEmpty(orderNo)) {
            message.setKey(orderNo);
        }
        this.producer.sendOneway(message);
    }

    public void delaySend(String msgType, String msg, String orderNo, long delayTime) {
        Message message = new Message(
                this.topic,
                msgType,
                msg.getBytes()
        );
        if (StringUtil.isNotEmpty(orderNo)) {
            message.setKey(orderNo);
        }
        message.setStartDeliverTime(System.currentTimeMillis() + delayTime);
        this.producer.sendOneway(message);
    }



    /**
     * 启动
     */
    @Override
    public void start() {
        if (!StringUtil.equals("dev", env)) {
            if (null == this.properties) {
                throw new ONSClientException("MQ初始化报错：properties not set");
            } else {
                this.producer = ONSFactory.createProducer(this.properties);
                this.producer.start();
                log.info("国内订单消息队列生产者启动成功，环境：" + this.env + "TOPIC:" + this.topic );
            }
        }else {
            log.info("非正式环境不启动MQ...");
        }

    }

    /**
     * 停止
     */
    @Override
    public void shutdown() {
        if (this.producer != null) {
            this.producer.shutdown();
        }
    }


    @Override
    public boolean isStarted() {
        return this.producer.isStarted();
    }

    @Override
    public boolean isClosed() {
        return this.producer.isClosed();
    }


    @Override
    public void updateCredential(Properties properties) {
        if (this.producer != null) {
            this.producer.updateCredential(properties);
        }
    }

    @Override
    public SendResult send(Message message) {
        return this.producer.send(message);
    }

    @Override
    public void sendOneway(Message message) {
        this.producer.sendOneway(message);
    }

    @Override
    public void sendAsync(Message message, SendCallback sendCallback) {
        this.producer.sendAsync(message, sendCallback);
    }

    @Override
    public void setCallbackExecutor(ExecutorService executorService) {
        this.producer.setCallbackExecutor(executorService);
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
