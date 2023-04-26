package me.zhengjie.support;

import cn.hutool.core.date.DatePattern;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zto.zop.ZopClient;
import com.zto.zop.ZopPublicRequest;
import me.zhengjie.domain.DomesticOrder;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

/**
 * @author luob
 * @description 国内中通
 * @date 2022/4/12
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DMZTOSupport {

    @Value("${express.dm-zto-companyid}")
    public String companyId;

    @Value("${express.dm-zto-key}")
    public String key;

    @Value("${express.dm-zto-mail-url}")
    public String submitOrderUrl;

    @Value("${express.dm-zto-mark-url}")
    public String getMarkUrl;

    @Value("${express.dm-zto-partner}")
    public String partner;

    @Value("${express.dm-zto-verify}")
    public String verify;

    @Autowired
    private BusinessLogService businessLogService;


    public void getMailNo(DomesticOrder order) throws IOException {
        long start = System.currentTimeMillis();
        ZopPublicRequest request = new ZopPublicRequest();

        JSONObject data = new JSONObject();
        JSONObject content = new JSONObject();
        content.put("id", getSynId());
        content.put("typeid", "1");

        JSONObject sender = new JSONObject();
        sender.put("name", "富立物流");
        sender.put("phone", "0574-86873070");
        sender.put("city", "浙江,宁波市,北仑区");
        sender.put("address", "保税东区兴业四路二号");
        content.put("sender", sender);

        JSONObject receiver = new JSONObject();
        receiver.put("name", order.getConsigneeName());
        receiver.put("mobile", order.getConsigneeTel());
        receiver.put("city", order.getProvince()+","+order.getCity()+","+order.getDistrict());//四川,成都市,金牛区
        receiver.put("address", order.getConsigneeAddr());
        content.put("receiver", receiver);

        data.put("content", content);
        data.put("partner", partner);
        data.put("datetime", DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT));
        data.put("verify", verify);

        request.addParam("data", data.toJSONString());

        request.setUrl(submitOrderUrl);
        String mailNo;
        try {
            ZopClient client = new ZopClient(companyId, key);
            String result = client.execute(request);
            // 保存日志
            businessLogService.saveLog(BusTypeEnum.MAIL_GZTO, submitOrderUrl, order.getOrderNo(),  JSON.toJSONString(data), result, (System.currentTimeMillis() - start));

            JSONObject object = JSON.parseObject(result);
            if (object.getBoolean("result")) {
                mailNo = object.getJSONObject("data").getString("billCode");
                order.setLogisticsNo(mailNo);
                getMark(order);
            }else {
                String msg = object.getJSONObject("data").getString("message");
                throw new BadRequestException(msg);
            }
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 获取大头笔信息
     * @return
     */
    public void getMark(DomesticOrder order) throws IOException {
        ZopPublicRequest request = new ZopPublicRequest();
        JSONObject data = new JSONObject();
        data.put("unionCode", order.getLogisticsNo());// 用运单号
        data.put("receive_province", order.getProvince());
        data.put("receive_city", order.getCity());
        data.put("receive_district", order.getDistrict());
        data.put("receive_address", order.getConsigneeAddr());

        data.put("send_province", "浙江");
        data.put("send_city", "宁波市");
        data.put("send_district", "北仑区");

        request.addParam("company_id", companyId);
        request.addParam("msg_type", "GETMARK");
        request.addParam("data", data.toJSONString());
        request.setUrl(getMarkUrl);
        String mark;
        try {
            ZopClient client = new ZopClient(companyId, key);
            String result = client.execute(request);
            JSONObject object = JSON.parseObject(result);

            if (object.getBoolean("status")) {
                mark = object.getJSONObject("result").getString("mark");
                order.setAddMark(mark);
            }else {
                throw new BadRequestException(object.getString("message"));
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private static synchronized String getSynId() {
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATETIME_PATTERN);
        String result="";
        Random random=new Random();
        for(int i=0;i<6;i++){
            result+=random.nextInt(10);
        }
        return time + result;
    }
}
