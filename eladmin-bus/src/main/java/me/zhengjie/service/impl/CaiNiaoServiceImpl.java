package me.zhengjie.service.impl;

import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.Node;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.PackageStatus;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.TransitDetail;
import me.zhengjie.domain.CainiaoShopInfo;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.PddCloudPrintData;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.support.cainiao.CaiNiaoSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.MsgType;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaiNiaoServiceImpl implements CaiNiaoService {
    @Autowired
    private CainiaoShopInfoService cainiaoShopInfoService;

    @Autowired
    private CaiNiaoSupport caiNiaoSupport;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public String sendOrderToWmsAsPdd(CrossBorderOrder order, PddCloudPrintData printData) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoService.queryByShopId(order.getShopId());
        ShopInfo shopInfo = shopInfoService.findById(order.getShopId());
        return caiNiaoSupport.sendOrderToWms(order,shopInfo,cainiaoShopInfo,printData);
    }

    @Override
    public void declareResultCallBack(Long orderId) throws Exception{
        caiNiaoSupport.decalreResultCallBackGA(crossBorderOrderService.queryById(orderId));
    }

    @Override
    public void declareResultCallBack(CrossBorderOrder order) throws Exception{
        caiNiaoSupport.decalreResultCallBackGA(order);
    }

    @Override
    public boolean cancelDeclare(CrossBorderOrder order) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoService.queryByShopId(order.getShopId());
        return caiNiaoSupport.cancelDeclare(order,cainiaoShopInfo);
    }

    @Override
    public void collectPackage(CrossBorderOrder order) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoService.queryByShopId(order.getShopId());
        caiNiaoSupport.lastmineHoinCallback(order,cainiaoShopInfo);
    }

    @Override
    public PackageStatus queryWmsStatus(String lpCode,Long shopId) {
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoService.queryByShopId(shopId);
        Node node = caiNiaoSupport.queryWmsStatus(lpCode,cainiaoShopInfo);
        return node.getPackageStatus();
    }
    @Override
    public void taxAmountCallback(Long orderId) {
        caiNiaoSupport.gaCustomsTaxCallback(crossBorderOrderService.queryByIdWithDetails(orderId));
    }

    @Override
    public void taxAmountCallback(CrossBorderOrder order) {
        caiNiaoSupport.gaCustomsTaxCallback(order);
    }

    @Override
    public void listenOrderDeliver(){
        List<CrossBorderOrder> orderList = crossBorderOrderService.queryByCNDeliverOrder();
        if (CollectionUtils.isEmpty(orderList))
            return;
        CainiaoShopInfo cainiaoShopInfo = null;
        for (CrossBorderOrder order : orderList) {
            try {
                if (cainiaoShopInfo == null || cainiaoShopInfo.getShopId().equals(order.getShopId()))
                    cainiaoShopInfo=cainiaoShopInfoService.queryByShopId(order.getShopId());
                Node node = caiNiaoSupport.queryWmsStatus(order.getLpCode(),cainiaoShopInfo);
                boolean enableDeliver = false;
                if (CollectionUtils.isNotEmpty(node.getTransitList())){
                    for (TransitDetail detail : node.getTransitList()) {
                        if (StringUtils.equals(detail.getAction(),"GWMS_OUTBOUND")||StringUtils.equals(detail.getAction(),"WMS_CONFIRMED")){
                            enableDeliver=true;
                            break;
                        }
                    }
                }
                if (enableDeliver){
                    //菜鸟发货
                    //先发货平台的
                    if ("PDD".equals(order.getPlatformCode())){
                        pddOrderService.confirmDeliver(order);
                        cbOrderProducer.send(
                                MsgType.CB_ORDER_DOWNLOAD_CACHE,
                                order.getId()+"",
                                order.getOrderNo()
                        );
                    }
                    //再调用菜鸟揽收
                    //caiNiaoSupport.lastmineHoinCallback(order,cainiaoShopInfo);
                    cbOrderProducer.delaySend(
                            MsgType.CN_LASTMINE_HOIN,
                            order.getId()+"",
                            order.getOrderNo(),
                            5000L
                    );
                }else {
                    if (!StringUtils.equals(node.getPackageStatus().getLogisticStatus(),order.getCnStatus())){
                        order.setCnStatus(node.getPackageStatus().getLogisticStatus());
                        order.setWmsStatus(node.getPackageStatus().getLogisticStatusDesc());
                        crossBorderOrderService.update(order);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void lastmineHoinCallBack(Long id) {
        CrossBorderOrder order = crossBorderOrderService.queryById(id);
        lastmineHoinCallBack(order);
    }

    @Override
    public void lastmineHoinCallBack(CrossBorderOrder order){
        CainiaoShopInfo cainiaoShopInfo = cainiaoShopInfoService.queryByShopId(order.getShopId());
        caiNiaoSupport.lastmineHoinCallback(order,cainiaoShopInfo);
    }
}
