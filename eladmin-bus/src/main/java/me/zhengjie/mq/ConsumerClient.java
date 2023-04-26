package me.zhengjie.mq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Properties;

@Slf4j
@Configuration
public class ConsumerClient {

    @Value("${mq.cross-border-order-gid}")
    private String ORDER_GROUP_ID;

    @Value("${mq.cross-border-return-gid}")
    private String RETURN_GROUP_ID;

    @Value("${mq.bus-log-gid}")
    private String BUS_LOG_ID;

    @Value("${mq.bus-domestic-order-gid}")
    private String GID_DOMESTIC_ORDER;

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
    private String BUS_LOG_TOPIC;

    @Value("${mq.bus-domestic-order-topic}")
    private String DOMESTIC_ORDER;


    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CBOrderConsumer cbOrderConsumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);
        properties.put(PropertyKeyConst.GROUP_ID, ORDER_GROUP_ID);

        CBOrderConsumer cbOrderConsumer = new CBOrderConsumer();
        cbOrderConsumer.setProperties(properties);
        cbOrderConsumer.setTopic(ORDER_TOPIC);
        return cbOrderConsumer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public CBReturnConsumer cbReturnConsumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);
        properties.put(PropertyKeyConst.GROUP_ID, RETURN_GROUP_ID);

        CBReturnConsumer cbReturnConsumer = new CBReturnConsumer();
        cbReturnConsumer.setProperties(properties);
        cbReturnConsumer.setTopic(RETURN_TOPIC);
        return cbReturnConsumer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public BusLogConsumer busLogConsumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);
        properties.put(PropertyKeyConst.GROUP_ID, BUS_LOG_ID);

        BusLogConsumer busLogConsumer = new BusLogConsumer();
        busLogConsumer.setProperties(properties);
        busLogConsumer.setTopic(BUS_LOG_TOPIC);
        return busLogConsumer;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DomesticOrderConsumer domesticOrderConsumer() {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, accessKey);
        properties.put(PropertyKeyConst.SecretKey, secretKey);
        properties.put(PropertyKeyConst.NAMESRV_ADDR, addr);
        properties.put(PropertyKeyConst.GROUP_ID, GID_DOMESTIC_ORDER);

        DomesticOrderConsumer domesticOrderConsumer = new DomesticOrderConsumer();
        domesticOrderConsumer.setProperties(properties);
        domesticOrderConsumer.setTopic(DOMESTIC_ORDER);
        return domesticOrderConsumer;
    }
}
