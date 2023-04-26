package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.KJGService;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author luob
 * @description
 * @date 2022/3/4
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "跨境购接口")
@RequestMapping("/datahubWeb/CRSDataUDFSERVICES")
@Slf4j
public class KJGApiController {

    @Autowired
    private KJGService kjgService;

    @Autowired
    private BusinessLogService businessLogService;

    @AnonymousAccess
    @RequestMapping("/NBHG")
    @Log("跨境购消息下发")
    @ApiOperation("跨境购消息下发")
    public String msgPush(String msgtype, String msg, String userid, String sign) {
        long start = System.currentTimeMillis();
        log.info("收到跨境购推送信息，msgtype：{}", msgtype);
        log.info("收到跨境购推送信息，msg：{}", msg);
        log.info("收到跨境购推送信息，userid：{}", userid);
        log.info("收到跨境购推送信息，sign：{}", sign);
        String result = kjgService.msgPush(msgtype, msg);
        businessLogService.saveLog(BusTypeEnum.KJG_API,  msgtype, "", "",  msg, result, (System.currentTimeMillis() - start));
        log.info("跨境购推送返回，result：{}", result);
        return result;
    }


}
