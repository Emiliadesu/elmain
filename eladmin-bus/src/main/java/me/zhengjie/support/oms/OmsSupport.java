package me.zhengjie.support.oms;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.CustomerKey;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.SecureUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OmsSupport {
    @Autowired
    private CustomerKeyService customerKeyService;

    public static void main(String[] args)throws Exception {
        OmsSupport support = new OmsSupport();
        OrderStatusCheck orderStatusCheck = new OrderStatusCheck();
        orderStatusCheck.setOrderNo("186476752");
        orderStatusCheck.setOperateTime(DateUtils.now());
        orderStatusCheck.setStatus(CBOrderStatusEnum.STATUS_245.getCode());
        orderStatusCheck.setLogisticsNo("771245478");
        orderStatusCheck.setLogisticsName("中通国际");
        support.statusCheck(orderStatusCheck,-1L);
    }

    public String statusCheck(OrderStatusCheck orderStatusCheck,Long custId) throws Exception{
        CustomerKey customerKey = customerKeyService.findByCustId(custId);
        if (customerKey==null || StringUtil.isEmpty(customerKey.getCallbackUrl()))
            return null;
        String reqData = JSONObject.toJSONString(orderStatusCheck);
        log.info("状态回传请求参数:{}", reqData);
        String data = SecureUtils.encryptDexHex(reqData,customerKey.getSignKey());
        String resp = HttpRequest.post(customerKey.getCallbackUrl())
                .contentType(ContentType.FORM_URLENCODED.getValue())
                .form("customersCode",customerKey.getCode())
                .form("data",data).execute().body();
        log.info("状态回传响应:{}", resp);
        JSONObject respJson = JSON.parseObject(resp);
        Boolean isSucc = respJson.getBoolean("success");
        if (!isSucc)
            throw new BadRequestException(respJson.getString("msg"));
        return resp;
    }
}
