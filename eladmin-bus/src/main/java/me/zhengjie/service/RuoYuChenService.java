package me.zhengjie.service;

import me.zhengjie.domain.*;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenMsgPush;
import org.springframework.web.multipart.MultipartFile;

public interface RuoYuChenService {
    void stockWarehouseSend(RuoYuChenMsgPush ruoYuChenMsgPush);

    void rycConfirmUp(InboundOrder inboundOrder, InboundOrderLog log);

    void rycConfirmOrder(InboundOrder inboundOrder, InboundOrderLog log);

    void rycConfirmStockedTally(InboundOrder inboundOrder, InboundOrderLog log);

    void upLoadFile(MultipartFile file,Long id);

    void rycConfirmSku(BaseSku baseSku);

    void rycConfirmOrderDeliver(OutboundOrder outboundOrder, OutboundOrderLog log);
}
