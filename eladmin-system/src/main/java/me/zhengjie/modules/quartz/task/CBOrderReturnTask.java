package me.zhengjie.modules.quartz.task;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.service.OrderReturnService;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CBOrderReturnTask {

    @Autowired
    private OrderReturnService orderReturnService;

    // 退货申报
    public void declare() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            orderReturnService.declareBatch();
        }
    }

    // 刷新申报状态
    public void updateDecStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            orderReturnService.updateDecStatus();
        }
    }

    // 刷新退货单WMS状态
    public void updateWMsStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            orderReturnService.updateWMsStatus();
        }
    }




}
