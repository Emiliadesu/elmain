package me.zhengjie.utils.constant;

import com.aliyun.openservices.ons.api.Message;

/**
 * 消息类型，也就是消息的TAG
 */
public class MsgType {

    /**
     * 拉单
     */
    public static final String TOPIC_CB_ORDER_PROCESS = "CB_ORDER_PROCESS";

    /**
     * 拉单通知
     */
    public static final String CB_ORDER_PULL = "CB_ORDER_PULL";

    /**
     * 接单通知
     */
    public static final String CB_ORDER_200 = "CB_ORDER_200";

    /**
     * 推送支付单通知
     */
    public static final String CB_ORDER_205 = "CB_ORDER_205";

    /**
     * 新增面单信息通知
     */
    public static final String CB_ORDER_INSTER_PRINT = "CB_ORDER_INSTER_PRINT";

    /**
     * 接单回传通知
     */
    public static final String CB_ORDER_215 = "CB_ORDER_215";

    /**
     * 清关开始通知
     */
    public static final String CB_ORDER_220= "CB_ORDER_220";

    /**
     * 刷新申报状态通知
     */
    public static final String CB_ORDER_REFRESH = "CB_ORDER_REFRESH";

    /**
     * 清关开始回传通知
     */
    public static final String CB_ORDER_225 = "CB_ORDER_225";

    /**
     * 清关完成通知
     */
//    public static final String CB_ORDER_230 = "CB_ORDER_230";// 查询到清关完成直接修改状态，不通过MQ发消息了

    /**
     * 清关完成回传通知
     */
    public static final String CB_ORDER_235 = "CB_ORDER_235";

    /**
     * 拣货开始回传通知
     */
    public static final String CB_ORDER_236 = "CB_ORDER_236";

    /**
     * 拣货完成回传通知
     */
    public static final String CB_ORDER_2361 = "CB_ORDER_2361";

    /**
     * 刷新WMS状态通知
     */
    public static final String CB_ORDER_WMS = "CB_ORDER_WMS";

    /**
     * 打包通知
     */
    public static final String CB_ORDER_237 = "CB_ORDER_237";

    /**
     * 称重完成通知
     */
    public static final String CB_ORDER_240 = "CB_ORDER_240";

    /**
     * 出库通知
     */
    public static final String CB_ORDER_245 = "CB_ORDER_245";

    /*************************退货相关**************************/

    /**
     *接单
     */
    public static final String CB_RETURN_300 = "CB_RETURN_300";

    /**
     *收货回传
     */
    public static final String CB_RETURN_310 = "CB_RETURN_310";

    /**
     *质检通过回传
     */
    public static final String CB_RETURN_317 = "CB_RETURN_317";

    /**
     *理货完成回传
     */
    public static final String CB_RETURN_365 = "CB_RETURN_365";

    /**
     *质检异常回传
     */
    public static final String CB_RETURN_323 = "CB_RETURN_323";

    /**
     *发起退货申报
     */
    public static final String CB_RETURN_325 = "CB_RETURN_325";

    /**
     *申报开始回传
     */
    public static final String CB_RETURN_330 = "CB_RETURN_330";

    /**
     *申报完成回传
     */
    public static final String CB_RETURN_337 = "CB_RETURN_337";

    /**
     *申报异常回传
     */
    public static final String CB_RETURN_345 = "CB_RETURN_345";

    /**
     *保税仓上架回传
     */
    public static final String CB_RETURN_355 = "CB_RETURN_355";

    /**
     *退货仓上架回传
     */
    public static final String CB_RETURN_375 = "CB_RETURN_375";

    /**
     * 拼多多拉单通知
     */
    public static final String CB_ORDER_PULL_PDD = "CB_ORDER_PULL_PDD";

    /**
     * 拼多多创建订单通知
     */
    public static final String CB_ORDER_200_PDD = "CB_ORDER_200_PDD";

    /**
     * 刷新订单取消状态通知
     */
    public static final String CB_ORDER_888 = "CB_ORDER_888";

    /**
     * 回传撤单开始
     */
    public static final String CB_ORDER_880 = "CB_ORDER_880";
    /**
     * 回传撤单成功
     */
    public static final String CB_ORDER_884 = "CB_ORDER_884";
    /**
     * 回传关单成功
     */
    public static final String CB_ORDER_886 = "CB_ORDER_886";

    /**
     * 海关撤单状态刷新
     */
    public static final String CB_ORDER_DEC_DEL = "CB_ORDER_DEC_DEL";
    /**
     * 刷新订单平台状态
     */
    public static final String CB_REFRESH_ORDER_STATUS = "CB_REFRESH_ORDER_STATUS";
    public static final String CB_ORDER_PULL_YZ = "CB_ORDER_PULL_YZ";
    public static final String CB_ORDER_200_YZ = "CB_ORDER_200_YZ";
    public static final String DY_GET_MAIL_NO = "DY_GET_MAIL_NO";
    public static final String CB_ORDER_PULL_ORDERNO_DY = "CB_ORDER_PULL_ORDERNO_DY";

    public static final String CB_ORDER_200_MGJ = "CB_ORDER_200_MGJ";
    public static final String CB_ORDER_200_YMT = "CB_ORDER_200_YMT";

    /**
     * 抖音锁单、拦截、取消锁单的回告
     */
    public static final String DY_ORDERINTERCEPTION = "DY_ORDERINTERCEPTION";

    /**
     * 抖音锁单、拦截、取消锁单的回告
     */
    public static final String DY_ORDERINTERCEPTION_NOTIFY = "DY_ORDERINTERCEPTION_NOTIFY";

    /**
     * 抖音推送运单通知
     */
    public static final String DY_PUSH_MAIL_NO = "DY_PUSH_MAIL_NO";

    /**
     * 蘑菇街拉单通知
     */
    public static final String CB_ORDER_PULL_MGJ = "CB_ORDER_PULL_MGJ";

    /**
     * 洋码头拉单通知
     */
    public static final String CB_ORDER_PULL_YMT = "CB_ORDER_PULL_YMT";

    public static final String CB_UPDATE_WMS_TIME = "CB_UPDATE_WMS_TIME";
    /**
     * 美团拒绝退款
     */
    public static final String MEITUAN_REFUND_REJECT = "MEITUAN_REFUND_REJECT";
    /**
     * 美团获取清关材料
     */
    public static final String MEITUAN_SET_CROSSBORD_INFO = "MEITUAN_SET_CROSSBORD_INFO";
    /**
     * 美团同意退款
     */
    public static final String MEITUAN_REFUND_AGREE = "MEITUAN_REFUND_AGREE";

    /**
     * 美团取消订单
     */
    public static final String MEITUAN_CANCEL = "MEITUAN_CANCEL";
    /**
     * 美团锁单
     */
    public static final String MEITUAN_LOCK = "MEITUAN_LOCK";
    /**
     * 刷新未揽收的运单
     */
    public static final String CB_UPGRADEUNCOLLECTEDORDER="CB_UPGRADEUNCOLLECTEDORDER";

    /**
     * 刷新未揽收的运单
     */
    public static final String CB_UPGRAD_DECINFO="CB_UPGRAD_DECINFO";
    /**
     * 刷新订单平台状态并且取消/撤单 平台已取消 的订单
     */
    public static final String CB_REFRESH_ORDER_STATUS_AND_CANCEL = "CB_REFRESH_ORDER_STATUS_AND_CANCEL";

    /**
     * 推送sku到wms
     */
    public static final String SKU_PUSH_WMS = "SKU_PUSH_WMS";





    /*******************************国内订单************************************/
    /**
     * 接单回传通知
     */
    public static final String DM_ORDER_215 = "DM_ORDER_215";

    /**
     *
     */
    public static final String DM_ORDER_220 = "DM_ORDER_220";

    /**
     *
     */
    public static final String DM_ORDER_235 = "DM_ORDER_235";

    /**
     *
     */
    public static final String DM_ORDER_245 = "DM_ORDER_245";
    /**
     * 国美取消订单通知标识
     */
    public static final String CB_ORDER_GM_CANCEL = "CB_ORDER_GM_CANCEL";

    /**
     * 出库后将订单下载信息缓存到数据库
     */
    public static final String CB_ORDER_DOWNLOAD_CACHE = "CB_ORDER_DOWNLOAD_CACHE";
    /**
     * 入库取消
     */
    public static final String CB_RK_CANCEL = "CB_RK_CANCEL";
    /**
     * 出库取消
     */
    public static final String CB_CK_CANCEL = "CB_CK_CANCEL";

    public static final String CB_ORDER_200_AIKUCUN = "CB_ORDER_200_AIKUCUN";
    public static final String CB_ORDER_PULL_AIKUCUN = "CB_ORDER_PULL_AIKUCUN";
    /**
     * 拼多多保存打印数据
     */
    public static final String CB_ORDER_PDD_SAVE_PRINTDATA = "CB_ORDER_PDD_SAVE_PRINTDATA";
    /**
     * 拼多多保存打印log
     */
    public static final String CB_ORDER_PDD_PRINT_LOG_SAVE = "CB_ORDER_PDD_PRINT_LOG_SAVE";
    /**
     * 卓志出库单同步状态
     */
    public static final String ZHUOZHI_OUT_SYNCSTATUS = "ZHUOZHI_OUT_SYNCSTATUS";
    /**
     * 卓志入库单同步状态
     */
    public static final String ZHUOZHI_IN_SYNCSTATUS = "ZHUOZHI_IN_SYNCSTATUS";
    /**
     * 抖音仓储计费通知
     */
    public static final String DY_CREATE_WAREHOUSE_FEE_ORDER = "DY_CREATE_WAREHOUSE_FEE_ORDER";
    /**
     * 抖音库存转移、盘点审核回告
     */
    public static final String DY_NOTIFY_ADJUST_RESULT = "DY_NOTIFY_ADJUST_RESULT";

    /**
     * 美团推送订单
     */
    public static final String CB_ORDER_200_MEITUAN = "CB_ORDER_200_MEITUAN";
    public static final String DY_PUSH_CANGZU_FEE = "DY_PUSH_CANGZU_FEE";
    /**
     * 向菜鸟推送税费
     */
    public static final String CN_PUSH_TAX = "CN_PUSH_TAX";
    /**
     *向菜鸟推送清关放行回执
     */
    public static final String CN_PUSH_CLEAR_SUCC = "CN_PUSH_CLEAR_SUCC";
    /**
     *向菜鸟回传揽收
     */
    public static final String CN_LASTMINE_HOIN = "CN_LASTMINE_HOIN";
    /**
     *得物申报单代推-回传接单
     */
    public static final String DW_215 = "DW_215";

    /**
     *得物申报单代推-清关开始
     */
    public static final String DW_220 = "DW_220";

    /**
     *得物申报单代推-回传清关开始
     */
    public static final String DW_225 = "DW_225";

    /**
     *得物申报单代推-刷新清关状态
     */
    public static final String DW_230 = "DW_230";

    /**
     *得物申报单代推-回传清关完成
     */
    public static final String DW_235 = "DW_235";

    /**
     *得物申报单代推-回传清关完成
     */
    public static final String DW_888 = "DW_888";
}
