import com.aliyun.mq.http.MQClient;
import com.aliyun.mq.http.MQProducer;
import com.aliyun.mq.http.model.TopicMessage;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import me.zhengjie.support.dewu.DewuSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Properties;

@RunWith(SpringRunner.class)
public class MqTest {


    @Test
    public void testNum() {
        DewuSupport dewuSupport = new DewuSupport();
        dewuSupport.reqPackInfo("SF1020082244120", new BigDecimal(1050));
    }


    @Test
    public void testSend() {
        MQClient mqClient = new MQClient(
                // 设置HTTP接入域名。
                "http://1788072189104904.mqrest.cn-qingdao-public.aliyuncs.com",
                // AccessKey ID阿里云身份验证，在阿里云服务器管理控制台创建。
                "LTAILrMIaxNYSloH",
                // AccessKey Secret阿里云身份验证，在阿里云服务器管理控制台创建。
                "sMgf2lbtpJjevoyVWMtzXYSb8kbQXY"
        );
        // 所属的Topic。
        final String topic = "ORDER_PROCESS_TEST";
        // Topic所属实例ID，默认实例为空。
        final String instanceId = "MQ_INST_1788072189104904_BXf4MMqC";

        // 获取Topic的生产者。
        MQProducer producer;
        if (instanceId != null && instanceId != "") {
            producer = mqClient.getProducer(instanceId, topic);
        } else {
            producer = mqClient.getProducer(topic);
        }

        try {
            // 循环发送4条消息。
            for (int i = 0; i < 4; i++) {
                TopicMessage pubMsg;
                if (i % 2 == 0) {
                    // 普通消息。
                    pubMsg = new TopicMessage(
                            // 消息内容。
                            "hello mq!".getBytes(),
                            // 消息标签。
                            "A"
                    );
                    // 设置属性。
                    pubMsg.getProperties().put("a", String.valueOf(i));
                    // 设置Key。
                    pubMsg.setMessageKey("MessageKey");
                } else {
                    pubMsg = new TopicMessage(
                            // 消息内容。
                            "hello mq!".getBytes(),
                            // 消息标签。
                            "A"
                    );
                    // 设置属性。
                    pubMsg.getProperties().put("a", String.valueOf(i));
                    // 定时消息，定时时间为10s后。
                    pubMsg.setStartDeliverTime(System.currentTimeMillis() + 10 * 1000);
                }
                // 同步发送消息，只要不抛异常就是成功。
                TopicMessage pubResultMsg = producer.publishMessage(pubMsg);

                // 同步发送消息，只要不抛异常就是成功。
                System.out.println(new Date() + " Send mq message success. Topic is:" + topic + ", msgId is: " + pubResultMsg.getMessageId()
                        + ", bodyMD5 is: " + pubResultMsg.getMessageBodyMD5());
            }
        } catch (Throwable e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理。
            System.out.println(new Date() + " Send mq message failed. Topic is:" + topic);
            e.printStackTrace();
        }

        mqClient.close();
    }

    @Test
    public void testSend1() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, "LTAILrMIaxNYSloH");
        properties.put(PropertyKeyConst.SecretKey, "sMgf2lbtpJjevoyVWMtzXYSb8kbQXY");
        properties.put(PropertyKeyConst.NAMESRV_ADDR,
                "http://MQ_INST_1788072189104904_BXf4MMqC.mq-internet-access.mq-internet.aliyuncs.com:80");
        Producer producer = ONSFactory.createProducer(properties);
        producer.start();

        Message msg = new Message(
                "ORDER_PROCESS_TEST",
                "AB",
                "222".getBytes()
        );
        msg.setKey("33251");
        producer.send(msg);
        producer.shutdown();
    }
}
