package me.zhengjie.service.impl;

import com.taobao.pac.sdk.cp.ReceiveSysParams;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_ERROR_SYNC.CrossborderErrorSyncRequest;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_ERROR_SYNC.CrossborderErrorSyncResponse;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.OrderLog;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.CrossborderErrorSyncService;
import me.zhengjie.service.OrderLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 菜鸟的
 */
@Service
public class CrossborderErrorSyncServiceImpl implements CrossborderErrorSyncService {
    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private OrderLogService orderLogService;

    @Override
    public CrossborderErrorSyncResponse execute(ReceiveSysParams receiveSysParams, CrossborderErrorSyncRequest crossborderErrorSyncRequest) {
        CrossborderErrorSyncResponse response=new CrossborderErrorSyncResponse();
        try {
            CrossBorderOrder order=crossBorderOrderService.queryByLpCode(crossborderErrorSyncRequest.getCnOrderNo());
            if (order==null) throw new BadRequestException("没有找到该LP单号");
            order.setCnStatus("CN_DECLARE_FAIL");
            crossBorderOrderService.update(order);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(false);
            response.setErrorCode("400");
            response.setErrorMsg(e.getMessage());
            return response;
        }
        response.setSuccess(true);
        return response;
    }
}
