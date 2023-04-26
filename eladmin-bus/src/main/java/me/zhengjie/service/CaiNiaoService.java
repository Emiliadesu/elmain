package me.zhengjie.service;

import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.PackageStatus;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.PddCloudPrintData;

public interface CaiNiaoService {
    /**
     * 发货给菜鸟
     * @param order 订单数据
     * @param printData 拼多多面单打印数据，不传则菜鸟申报订单
     * @return
     */
    String sendOrderToWmsAsPdd(CrossBorderOrder order, PddCloudPrintData printData);

    /**
     * 清关申报回执回传
     * @param orderId 订单id
     */
    void declareResultCallBack(Long orderId) throws Exception;
    void declareResultCallBack(CrossBorderOrder order) throws Exception;

    /**
     * 菜鸟取消订单
     * @param order 订单数据，只用到LP单号和订单平台编码
     * @return
     */
    boolean cancelDeclare(CrossBorderOrder order);

    /**
     * 包裹揽收，菜鸟发货后才能调用
     * @param order 订单数据
     */
    void collectPackage(CrossBorderOrder order);

    /**
     * 查询菜鸟订单状态
     * @param lpCode LP单号
     * @return
     */
    PackageStatus queryWmsStatus(String lpCode,Long shopId);

    /**
     * 总署版关税回传
     * @param orderId
     */
    void taxAmountCallback(Long orderId);
    void taxAmountCallback(CrossBorderOrder order);

    /**
     * 监听菜鸟订单发货状态
     */
    void listenOrderDeliver();

    void lastmineHoinCallBack(Long orderId);

    void lastmineHoinCallBack(CrossBorderOrder order);
}
