package me.zhengjie.mq;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

@Slf4j
@Configuration
public class ProducerClient {

    @Value("${mq.access-key}")
    private String accessKey;
    @Value("${mq.secret-key}")
    private String secretKey;
    @Value("${mq.addr}")
    private  String addr;

    @Value("${mq.order-topic}")
    private String ORDER_TOPIC;

    @Value("${mq.return-topic}")
    private String RETURN_TOPIC;

    @Value("${mq.bus-log-topic}")
    private String BUS_TOPIC;

    @Value("${mq.bus-domestic-order-topic}")
    private String DOMESTIC_ORDER;

    //跨境订单处理生产者
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CBOrderProducer cbOrderProducer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);

        CBOrderProducer cbOrderProducer = new CBOrderProducer();
        cbOrderProducer.setProperties(properties);
        cbOrderProducer.setTopic(ORDER_TOPIC);
        return cbOrderProducer;
    }

    //跨境退货处理生产者
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CBReturnProducer cbReturnProducer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);

        CBReturnProducer cbReturnProducer = new CBReturnProducer();
        cbReturnProducer.setProperties(properties);
        cbReturnProducer.setTopic(RETURN_TOPIC);
        return cbReturnProducer;
    }

    // 业务日志处理生产者
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public BusLogProducer busLogProducer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);

        BusLogProducer busLogProducer = new BusLogProducer();
        busLogProducer.setProperties(properties);
        busLogProducer.setTopic(BUS_TOPIC);
        return busLogProducer;
    }

    // 业务日志处理生产者
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DomesticOrderProducer domesticOrderProducer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);

        DomesticOrderProducer domesticOrderProducer = new DomesticOrderProducer();
        domesticOrderProducer.setProperties(properties);
        domesticOrderProducer.setTopic(DOMESTIC_ORDER);
        return domesticOrderProducer;
    }

}
