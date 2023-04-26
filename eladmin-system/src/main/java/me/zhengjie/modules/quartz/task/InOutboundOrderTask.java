package me.zhengjie.modules.quartz.task;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.service.*;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 2B出入库单定时任务
 */
@Slf4j
@Component
public class InOutboundOrderTask {

    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private WmsOutstockService wmsOutstockService;

    @Autowired
    private StackStockRecordService stackStockRecordService;

    @Autowired
    private StockAttrService stockAttrService;

    @Autowired
    private M3ScreensService m3ScreensService;

    @Autowired
    private DouyinService douyinService;

    /**
     * 入库单WMS状态查询
     */
    public void refreshInStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            inboundOrderService.refreshStatus();
        }
    }

    /**
     * 除单WMS状态查询
     */
    public void refreshOutStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            outboundOrderService.refreshStatus();
        }
    }

    /**
     * 入库单推送富勒
     */
    public void inStockPushFlux() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsInstockService.inStockPushFlux();
        }
    }
    /**
     * 出库单推送富勒
     */
    public void outStockPushFlux() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsOutstockService.outStockPushFlux();
        }
    }
    /**
     * 出库单获取富勒的SO单号
     */
    public void getFluxSoNo() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsOutstockService.getFluxSoNo();
        }
    }
    /**
     * 入库单获取富勒的ASN单号
     */
    public void getFluxASNNo() {
        System.err.println("???");
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsInstockService.getAsnNo();
        }
    }


    /**
     * 卓志
     * 获取卓志在富勒的最新批次号
     */
    public void getZhuozhiLotNum() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            stockAttrService.recordNewLotNum();
        }
    }
    /**
     * 卓志
     * 堆库存快照记录
     */
    public void recordZhuoZStackStock() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            stackStockRecordService.recordZhuoZStackStock();
        }
    }

    /**
     * 卓志
     * 推送入库理货单
     */
    public void pushInTally() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsInstockService.inTallyPush();
        }
    }

    /**
     * 卓志
     * 推送验收入库单
     */
    public void checkInTally() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsInstockService.checkInTally();
        }
    }

    /**
     * 卓志
     * 推送入库通知
     */
    public void inStock() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            wmsInstockService.inStock();
        }
    }

    /**
     * 入库单监听富勒状态，回传状态给渠道
     */
    public void listenFluxInStatus(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            inboundOrderService.listenFluxInStatus();
        }
    }

    /**
     * 出库单监听富勒状态，回传状态给渠道
     */
    public void listenFluxOutStatus(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            //outboundOrderService.listenFluxOutStatus();
        }
    }

    /**
     * 定时生成库存快照并上传到指定的FTP服务器
     */
    public void generatorInventorySnapshotByFTP(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            m3ScreensService.generatInvrptFile();
        }
    }

    /**
     * 定时生成库存移动记录并上传到指定的FTP服务器
     */
    public void generatorInventoryMovByFTP(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            m3ScreensService.generatHandingMovementFile();
        }
    }

    /**
     * 定时生成库存快照并回传到抖音
     */
    public void generatorInventorySnapshotByDy(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            douyinService.syncInventorySnapshot();
        }
    }

    /**
     * 定时回传库存流水到抖音
     */
    public void syncInventoryTransLogByDy(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            douyinService.syncInventoryLogFlow();
        }
    }
}
