package me.zhengjie.modules.quartz.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.support.douyin.SorterSupport;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


/**
 * 跨境订单定时任务
 */
@Slf4j
@Component
@Service
@RequiredArgsConstructor
public class CBOrderTask{

    @Autowired
    private JDService jdService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private QSService qsService;

    @Autowired
    private DyOrderPushService dyOrderPushService;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private DailyStockService dailyStockService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private AikucunService aikucunService;

    @Autowired
    private CaiNiaoService caiNiaoService;

    @Autowired
    private DewuDeclarePushService dewuDeclarePushService;

    @Autowired
    private SorterSupport sorterSupport;

    /**
     * 抖音拉单
     */
    public void dyPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            douyinService.pullOrder();
        }
    }
    /**
     * 圈尚拉单(测试)
     */
    public void qsPullOrder() {
//        String ip = StringUtils.getLocalIp();
//        if (StringUtil.equals(ip, "10.0.6.114")) {
            qsService.pullOrder();
//        }
    }

    /**
     * 京东access_token 24小时失效刷新
     */
    public void jdrefreshToken(){
        try {
            jdService.refreshToken();
        } catch (Exception e) {
           throw new BadRequestException("前端定时刷新京东access_token失败:"+e.getMessage());
        }
    }

    //test
//    public void tests(){
//        //任务调度测试下单
//        String orderNo = "110114726320689834";
//        CrossBorderOrder crossBorderOrder = crossBorderOrderService.queryByOrderNo(orderNo);
//        try {
//            logisticsInfoService.getLogistics(crossBorderOrder);
//        } catch (Exception e) {
//           throw new BadRequestException("选择物流失败:"+e.getMessage());
//        }
//    }

    public void dyPullOrderBy24H() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            douyinService.pullOrderDYDetails();
        }
    }

    public void pddPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            pddOrderService.pddPullOrder();
        }
    }

    public void pddPullOrderHours(String hours) {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            pddOrderService.pullOrderByHours(Integer.valueOf(hours));
        }
    }

    public void mgjPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            moGuJieService.moGuJiePullOrder();
        }
    }

    public void mgjPullOrderByHours(String hours) {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            moGuJieService.pullOrderByHours(Integer.valueOf(hours));
        }
    }

    public void ymtPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            ymatouService.ymatouPullOrder();
        }
    }

    public void ymtPullOrderByHours(String hours) {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            ymatouService.pullOrderByHours(Integer.valueOf(hours));
        }
    }

    public void youZanPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            youZanOrderService.youZanPullOrder();
        }
    }
    public void youZanPullOrderByHouse(String hours) {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            youZanOrderService.pullOrderByHours(StringUtil.isBlank(hours)?24:Integer.valueOf(hours));
        }
    }

    /**
     * 更新清关状态
     */
    public void updateClearStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            crossBorderOrderService.updateMftStatus();
        }
    }

    /**
     * 处理取消订单
     */
    public void updateCancelOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.updateCancelOrder();
        }
    }

    /**
     * 解冻订单
     */
    public void unFreeze() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.unFreezeBatch();
        }
    }

    /**
     * 处理清关失败订单
     */
    public void handelDecErr() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.handelDecErr();
        }
    }

    /**
     * 处理卡接单状态订单
     */
    public void handel200() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                crossBorderOrderService.handel200();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新WMS状态
     */
    public void refreshWmsStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                crossBorderOrderService.refreshWmsStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新平台订单状态
     */
    public void refreshPlatformStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                crossBorderOrderService.refreshPlatformStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 刷新平台订单状态(除了抖音)
     */
    public void refreshPlatformStatus2() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                crossBorderOrderService.refreshPlatformStatus2();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 刷新平台订单取消状态
     */
    public void refreshPlatformCancelStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                crossBorderOrderService.refreshPlatformCancelStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    /**
     * 小时播报
     */
    public void reportHourOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            try {
                douyinService.reportHourOrder();
            }catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

        }
    }

    /**
     * 重新拉取无支付单号的冻结单
     */
    public void rePullByNoPayNo(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            crossBorderOrderService.rePullByNoPayNo();
        }
    }

    /**
     * 尝试申报215状态的订单
     */
    public void orderDeclare(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            try {
                crossBorderOrderService.orderDeclare();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 定时重新拉取抖音推动过来的订单号和拉取失败的订单
     */
    public void rePullByOrderPush(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            try {
                dyOrderPushService.rePullByOrderPush();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void refreshToken(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            try {
                douyinService.refreshToken();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void upgradeLogisticsInfo(){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.upgradeLogisticsInfo();
        }
    }

    /**
     * 更新订单海关信息
     */
    public void refreshDecInfo() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.refreshDecInfo();
        }
    }

    /**
     * 运单批量回传
     */
    public void dyGetMail() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            douyinService.getMailNo();
        }
    }

    /**
     * 抖音清关单删单开始 回传
     */
    public void dyDelClearStart() {
//        String ip = StringUtils.getLocalIp();
//        if (StringUtil.equals(ip, "10.0.6.115")) {
//            crossBorderOrderService.confirmClearDelStart();
//        }
    }

    /**
     * 抖音清关单删单成功 回传
     */
    public void dyDelClearSuccess() {
//        String ip = StringUtils.getLocalIp();
//        if (StringUtil.equals(ip, "10.0.6.115")) {
//            crossBorderOrderService.confirmClearDelSucc();
//        }
    }

    /**
     * 抖音清关单取消申报成功 回传
     */
    public void dyCancelClearSuccess() {
//        String ip = StringUtils.getLocalIp();
//        if (StringUtil.equals(ip, "10.0.6.115")) {
//            crossBorderOrderService.confirmCloseOrder();
//        }
    }

    // 拉取库存
    public void pullDailyStock() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            dailyStockService.pullStock("");
        }
    }

    // 刷新海关撤单状态
    public void updateDecDelStatus() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.updateDecDelStatus();
        }
    }

    // 刷新海关撤单状态
    public void updateSkuSize() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            baseSkuService.updateSkuSize();
        }
    }

    //检测已取消的订单是否撤单成功，撤单时间超过4天仍未成功的邮件报警
    public void clearDelIsSucc(String day){
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.115")) {
            crossBorderOrderService.clearDelIsSucc(day);
        }
    }
    //爱库存拉单
    public void aiKuCunPullOrder() {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            aikucunService.aiKuCunPullOrder();
        }
    }

    //监听菜鸟发货
    public void listenCNDeliver() {
        String ip = StringUtils.getLocalIp();
        //caiNiaoService.listenOrderDeliver();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            caiNiaoService.listenOrderDeliver();
        }
    }
    //得物刷新申报状态
    public void refreshDwDeclareStatus() {
        String ip = StringUtils.getLocalIp();
        //caiNiaoService.listenOrderDeliver();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            dewuDeclarePushService.refreshDeclareStatus();
        }
    }

    // 抖音流水线心跳保活
    public void dySorterHeart() throws Exception {
        String ip = StringUtils.getLocalIp();
        if (StringUtil.equals(ip, "10.0.6.114")) {
            String s = sorterSupport.heartBeat();
            log.info(s);
        }
    }
}
