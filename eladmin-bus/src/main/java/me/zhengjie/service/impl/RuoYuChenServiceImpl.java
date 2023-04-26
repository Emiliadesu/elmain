package me.zhengjie.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.rest.model.Flux.DocOrderHeader;
import me.zhengjie.rest.model.ruoyuchen.request.*;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.DocAsnHeader;
import me.zhengjie.service.dto.ReceivingTransaction;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.fuliPre.ActAllocationDetails;
import me.zhengjie.support.ruoYuChen.RuoYuChenSupport;
import me.zhengjie.support.ruoYuChen.request.*;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenFileUpload;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenResponse;
import me.zhengjie.utils.DateUtil;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuoYuChenServiceImpl implements RuoYuChenService {
    @Autowired
    private InboundOrderService inboundOrderService;

    @Autowired
    private OutboundOrderService outboundOrderService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private InboundOrderLogService inboundOrderLogService;

    @Autowired
    private OutboundOrderLogService outboundOrderLogService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private RuoYuChenSupport ruoYuChenSupport;
    @Override
    public void stockWarehouseSend(RuoYuChenMsgPush ruoYuChenMsgPush) {
        if (ruoYuChenMsgPush == null)
            throw new BadRequestException("请求体body无任何内容或不是个有效的对象");
        if (ruoYuChenMsgPush.getMethod()==null)
            throw new BadRequestException("method为空");
        switch (ruoYuChenMsgPush.getMethod()) {
            case "Material":
                //商品资料同步
                baseSkuSync(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChenBaseSku.class),ruoYuChenMsgPush.getWarehouseCode());
                break;
            case "EntryorderSend":
                //入库单推送
                inboundOrderPush(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChenInBoundOrder.class));
                break;
            case "StockoutSend":
                //出库单推送
                outboundOrderPush(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChenOutBoundOrder.class));
                break;
            case "OrderCancel":
                //单据取消
                orderCancel(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChenOrderCancel.class));
                break;
            case "TallyConfirm":
                //理货单确认
                tallyConfirm(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChenTallyOrderConfirm.class));
                break;
            case "DeliveryorderSend":
                //销售出库单推送
                //deliverOrderPush(new JSONObject(ruoYuChenMsgPush.getData()).toBean(RuoYuChen))
                break;
            case "ReturnorderSend":
                //销售退货单推送:

                break;
            case "StoreprocessSend":
                //组装单推送

                break;
            case "InventorySend":
                //库存盘点推送

                break;

            default:
                throw new BadRequestException("不支持的接口标识");
        }
    }

    @Override
    public void rycConfirmUp(InboundOrder inboundOrder, InboundOrderLog log) {
        // TODO: 2022/3/31 若羽臣回传收货完成
        RuoYuChenInboundOrderBack orderBack = new RuoYuChenInboundOrderBack();
        orderBack.setEntryOrderCode(inboundOrder.getOutNo());
        orderBack.setEntryOrderId(inboundOrder.getOrderNo());
        if (StringUtils.equals(inboundOrder.getOrderType(),"0")){
            //采购入库单
            orderBack.setPurchaseOrderCode(inboundOrder.getDeclareNo());
        }
        orderBack.setEntryOrderType(inboundOrder.getDefault01());
        orderBack.setOutBizCode(IdUtil.randomUUID());

        orderBack.setOperateTime(DateUtils.now());
        orderBack.setModeType("1");
        List<RuoYuChenInboundOrderBackDetail>details=new ArrayList<>();
        boolean flag=false;
        if (StringUtil.isBlank(inboundOrder.getWmsNo())){
            for (InboundOrderDetails detail : inboundOrder.getDetails()) {
                if (detail.getTakeNum()==null||detail.getTakeNum()==0){
                    flag=true;
                    continue;
                }else if (!detail.getTakeNum().equals(detail.getExpectNum()))
                    flag=true;
                RuoYuChenInboundOrderBackDetail backDetail=new RuoYuChenInboundOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                backDetail.setOrderLineNo(detail.getGoodsLineNo());
                backDetail.setItemCode(detail.getGoodsCode());
                backDetail.setInventoryType(detail.getDefault01());
                backDetail.setActualQty(detail.getTakeNum()+"");//良品
                backDetail.setBatchCode("LT"+String.format("%08d",detail.getId()));
                backDetail.setExpireDate(DateUtils.formatDate(detail.getExpireDate()));
                backDetail.setProductDate(DateUtils.formatDate(DateUtils.subtract(DateUtils.parseDate(backDetail.getExpireDate()),baseSku.getLifecycle(),DateUtils.DAY)));
                List<RuoYuChenOrderBackBatch>batches=new ArrayList<>();
                RuoYuChenOrderBackBatch batch=new RuoYuChenOrderBackBatch();
                batch.setExpireDate(backDetail.getExpireDate());
                batch.setProductDate(backDetail.getProductDate());
                batch.setInventoryType(backDetail.getInventoryType());
                batch.setActualQty(backDetail.getActualQty());
                batch.setBatchCode(backDetail.getBatchCode());
                batches.add(batch);
                backDetail.setBatchs(batches);
                details.add(backDetail);
            }
        }else {
            List<ReceivingTransaction>detailList=wmsSupport.getDocAsnOrderByAsnNo(inboundOrder.getWmsNo());
            for (ReceivingTransaction transaction : detailList) {
                RuoYuChenInboundOrderBackDetail backDetail=new RuoYuChenInboundOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(transaction.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+transaction.getSku()+"找不到");
                backDetail.setOrderLineNo(String.format("%.0f",transaction.getAsnlineno()));
                backDetail.setItemCode(baseSku.getGoodsCode());
                if (StringUtil.equals(transaction.getLotatt08(),"良品"))
                    backDetail.setInventoryType("ZP");
                else
                    backDetail.setInventoryType("CC");
                backDetail.setActualQty(String.format("%.0f",transaction.getReceivedqty()));
                backDetail.setBatchCode(transaction.getLotnum());
                backDetail.setExpireDate(transaction.getLotatt02());
                backDetail.setProductDate(DateUtils.formatDate(DateUtils.subtract(DateUtils.parseDate(backDetail.getExpireDate()),baseSku.getLifecycle(),DateUtils.DAY)));
                List<RuoYuChenOrderBackBatch>batches=new ArrayList<>();
                RuoYuChenOrderBackBatch batch=new RuoYuChenOrderBackBatch();
                batch.setBatchCode(transaction.getLotnum());
                batch.setExpireDate(backDetail.getExpireDate());
                batch.setInventoryType(backDetail.getInventoryType());
                batch.setProductDate(backDetail.getProductDate());
                batch.setActualQty(backDetail.getActualQty());
                batches.add(batch);
                backDetail.setBatchs(batches);
                details.add(backDetail);
            }
        }
        orderBack.setStatus(flag?"PARTFULFILLED":"FULFILLED");
        orderBack.setOrderLines(details);
        RuoYuChenResponse response=ruoYuChenSupport.request(orderBack);
        log.setReqMsg(new JSONObject(orderBack).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess(response.getStatus()==0?"1":"0");
        log.setMsg(response.getMsg()==null?"":response.getMsg());
    }

    @Override
    public void rycConfirmOrder(InboundOrder inboundOrder, InboundOrderLog log) {
        // TODO: 2022/3/30 若羽臣入库接单回传
        log.setSuccess("1");
        /*RuoYuChenInboundOrderBack orderBack = new RuoYuChenInboundOrderBack();
        orderBack.setEntryOrderCode(inboundOrder.getOutNo());
        orderBack.setEntryOrderId(inboundOrder.getOrderNo());
        if (StringUtils.equals(inboundOrder.getOrderType(),"0")){
            //采购入库单
            orderBack.setPurchaseOrderCode(inboundOrder.getDeclareNo());
        }
        orderBack.setEntryOrderType(inboundOrder.getDefault01());
        orderBack.setOutBizCode(IdUtil.randomUUID());
        orderBack.setStatus("ACCEPT");
        orderBack.setOperateTime(DateUtils.now());
        RuoYuChenResponse response=ruoYuChenSupport.request(orderBack);
        log.setReqMsg(new JSONObject(orderBack).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess(response.getStatus()==0?"1":"0");
        log.setMsg(response.getMsg()==null?"":response.getMsg());*/
    }

    @Override
    public void rycConfirmStockedTally(InboundOrder inboundOrder, InboundOrderLog log) {
        // TODO: 2022/3/30 若羽臣入库理货回传
        RuoYuChenTallyOrderBack tallyOrder = new RuoYuChenTallyOrderBack();
        tallyOrder.setEntryOrderCode(inboundOrder.getOutNo());
        tallyOrder.setReceivDate(DateUtils.formatDateTime(inboundOrder.getArriveTime()));
        tallyOrder.setTallyDate(DateUtils.now());
        List<RuoYuChenTallyOrderBackDetail>details=new ArrayList<>();
        if (StringUtil.isBlank(inboundOrder.getWmsNo())){
            for (InboundOrderDetails detail : inboundOrder.getDetails()) {
                RuoYuChenTallyOrderBackDetail backDetail=new RuoYuChenTallyOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                backDetail.setItemCode(detail.getGoodsCode());
                backDetail.setSumQty(detail.getTakeNum()+"");
                backDetail.setGoodsQty(detail.getNormalNum()+"");//良品
                backDetail.setDefectiveQty(detail.getDamagedNum()+"");//残次
                backDetail.setExpireDate(DateUtils.formatDate(detail.getExpireDate()));
                details.add(backDetail);
            }
        }else {
            List<ReceivingTransaction>detailList=wmsSupport.getDocAsnOrderByAsnNo(inboundOrder.getWmsNo());
            List<ReceivingTransaction>huiZongList=new ArrayList<>();
            for (ReceivingTransaction transaction : detailList) {
                if (huiZongList.indexOf(transaction)!=-1){
                    ReceivingTransaction huiZongDetail=huiZongList.get(huiZongList.indexOf(transaction));
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(huiZongDetail.getGoodQty()+transaction.getReceivedqty().intValue());
                    }else {
                        huiZongDetail.setBadQty(huiZongDetail.getBadQty()+transaction.getReceivedqty().intValue());
                    }
                }else {
                    ReceivingTransaction huiZongDetail=new ReceivingTransaction();
                    transaction.copy(huiZongDetail);
                    if (StringUtil.equals(transaction.getLotatt08(),"良品")){
                        huiZongDetail.setGoodQty(transaction.getReceivedqty().intValue());
                        huiZongDetail.setBadQty(0);
                    }else {
                        huiZongDetail.setGoodQty(0);
                        huiZongDetail.setBadQty(transaction.getReceivedqty().intValue());
                    }
                    huiZongList.add(huiZongDetail);
                }
            }
            for (ReceivingTransaction transaction : huiZongList) {
                RuoYuChenTallyOrderBackDetail backDetail=new RuoYuChenTallyOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(transaction.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+transaction.getSku()+"找不到");
                backDetail.setItemCode(baseSku.getGoodsCode());
                backDetail.setSumQty((transaction.getGoodQty()+transaction.getBadQty())+"");
                backDetail.setGoodsQty(transaction.getGoodQty()+"");//良品
                backDetail.setDefectiveQty(transaction.getBadQty()+"");//次品
                backDetail.setExpireDate(StringUtil.isBlank(transaction.getLotatt02())?
                        DateUtils.formatDate(new Date(System.currentTimeMillis()+5*365+24*3600*1000L)):
                        transaction.getLotatt02());//效期,无效期管理的默认当前时间的5年后
            }
        }
        tallyOrder.setDetails(details);
        List<RuoYuChenFileUrl>urlList=new ArrayList<>();
        RuoYuChenFileUrl url=new RuoYuChenFileUrl();
        url.setUrl(inboundOrder.getStockRecordUrl());
        urlList.add(url);
        tallyOrder.setAttDetails(urlList);
        RuoYuChenResponse response=ruoYuChenSupport.request(tallyOrder);
        log.setReqMsg(new JSONObject(tallyOrder).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess(response.getStatus()==0?"1":"0");
        log.setMsg(response.getMsg()==null?"":response.getMsg());
    }

    @Override
    public void upLoadFile(MultipartFile file,Long id) {
        RuoYuChenFileUpload fileUpload=ruoYuChenSupport.uploadFile(file);
        InboundOrder inboundOrder=inboundOrderService.queryById(id);
        inboundOrder.setStockRecordUrl(fileUpload.getPath());
        inboundOrder.setStockRecordName(fileUpload.getName());
        inboundOrderService.update(inboundOrder);
    }

    @Override
    public void rycConfirmSku(BaseSku baseSku) {
        RuoYuChenBaseSkuRecordBack baseSkuRecordBack = new RuoYuChenBaseSkuRecordBack();
        baseSkuRecordBack.setItemCode(baseSku.getGoodsCode());
        baseSkuRecordBack.setCusBarCode(baseSku.getBarCode());
        baseSkuRecordBack.setCusItemCode(baseSku.getGoodsCode());
        baseSkuRecordBack.setCusItemName(baseSku.getGoodsName());
        ShopInfo shopInfo=shopInfoService.findById(baseSku.getShopId());
        baseSkuRecordBack.setCusBooksCode(shopInfo.getBooksNo());
        baseSkuRecordBack.setCusArtCode(baseSku.getGoodsNo());
        baseSkuRecordBack.setCusSerialCode(baseSku.getRecordNo());
        baseSkuRecordBack.setCusHsCode(baseSku.getHsCode());
        baseSkuRecordBack.setCusCountry(baseSku.getMakeContry());
        baseSkuRecordBack.setCusOneQty(baseSku.getLegalNum().stripTrailingZeros());
        baseSkuRecordBack.setCusOneDw(baseSku.getLegalUnit());
        baseSkuRecordBack.setCusTwoDw(baseSku.getSecondUnit());
        baseSkuRecordBack.setCusTwoQty(baseSku.getSecondNum().stripTrailingZeros());
        baseSkuRecordBack.setCusSpecModel(baseSku.getProperty());
        baseSkuRecordBack.setCusBradName(baseSku.getBrand());
        baseSkuRecordBack.setCusPurpose(baseSku.getGuse());
        baseSkuRecordBack.setCusIngredient(baseSku.getGcomposition());
        baseSkuRecordBack.setCusGrossWeight(baseSku.getGrossWeight().stripTrailingZeros());
        baseSkuRecordBack.setCusNetWeight(baseSku.getNetWeight()==null?BigDecimal.ZERO:baseSku.getNetWeight().stripTrailingZeros());
        baseSkuRecordBack.setCusDecElement(baseSku.getDeclareElement());
        RuoYuChenResponse response=ruoYuChenSupport.request(baseSkuRecordBack);
        if (!response.isSuccess())
            throw new BadRequestException(response.getMsg());
    }

    @Override
    public void rycConfirmOrderDeliver(OutboundOrder outboundOrder, OutboundOrderLog log) {
        RuoYuChenOutboundOrderBack back=new RuoYuChenOutboundOrderBack();
        back.setDeliveryOrderCode(outboundOrder.getOutNo());
        back.setDeliveryOrderId(outboundOrder.getOrderNo());
        back.setOrderType(outboundOrder.getDefault01());
        back.setOperateTime(DateUtils.now());
        back.setModeType("1");
        List<RuoYuChenOutboundOrderBackDetail>backDetailList=new ArrayList<>();
        boolean flag=false;
        if (StringUtil.isBlank(outboundOrder.getWmsNo())) {
            for (OutboundOrderDetails detail : outboundOrder.getDetails()) {
                if (detail.getDeliverNum()==null||detail.getDeliverNum()==0) {
                    flag=true;
                    continue;
                }else if (!detail.getDeliverNum().equals(detail.getExpectNum())){
                    flag=true;
                }
                RuoYuChenOutboundOrderBackDetail backDetail=new RuoYuChenOutboundOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getGoodsNo()+"找不到");
                backDetail.setOrderLineNo(detail.getGoodsLineNo());
                backDetail.setItemCode(baseSku.getGoodsCode());
                backDetail.setActualQty(detail.getDeliverNum()+"");
                backDetail.setInventoryType(detail.getDefault01());
                backDetail.setBatchCode(detail.getDefault02());
                backDetail.setExpireDate(DateUtils.formatDate(detail.getExpireDate()));
                backDetail.setProductDate(DateUtils.formatDate(DateUtils.subtract(detail.getExpireDate(),baseSku.getLifecycle(),DateUtils.DAY)));
                List<RuoYuChenOrderBackBatch>batches=new ArrayList<>();
                RuoYuChenOrderBackBatch batch=new RuoYuChenOrderBackBatch();
                batch.setProductDate(backDetail.getProductDate());
                batch.setExpireDate(backDetail.getExpireDate());
                batch.setActualQty(backDetail.getActualQty());
                batch.setBatchCode(backDetail.getBatchCode());
                batch.setInventoryType(backDetail.getInventoryType());
                batches.add(batch);
                backDetail.setBatchs(batches);
                backDetailList.add(backDetail);
            }
        }else {
            List<ActAllocationDetails>detailsList=wmsSupport.getActAllocationBySoNo(outboundOrder.getWmsNo());
            if (CollectionUtil.isEmpty(detailsList))
                throw new BadRequestException("富勒出库单明细为空");
            for (ActAllocationDetails detail : detailsList) {
                RuoYuChenOutboundOrderBackDetail backDetail=new RuoYuChenOutboundOrderBackDetail();
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getSku());
                if (baseSku==null)
                    throw new BadRequestException("货号"+detail.getSku()+"找不到");
                backDetail.setOrderLineNo(detail.getOrderlineno()+"");
                backDetail.setItemCode(baseSku.getGoodsCode());
                backDetail.setActualQty(detail.getQty()+"");
                backDetail.setInventoryType(StringUtil.equals("良品",detail.getLotAtt08())?"ZP":"CC");
                backDetail.setBatchCode(detail.getLotnum());
                backDetail.setExpireDate(detail.getLotAtt02());
                backDetail.setProductDate(DateUtils.formatDate(DateUtils.subtract(DateUtils.parseDate(detail.getLotAtt02()),baseSku.getLifecycle(),DateUtils.DAY)));
                List<RuoYuChenOrderBackBatch>batches=new ArrayList<>();
                RuoYuChenOrderBackBatch batch=new RuoYuChenOrderBackBatch();
                batch.setProductDate(backDetail.getProductDate());
                batch.setExpireDate(backDetail.getExpireDate());
                batch.setActualQty(backDetail.getActualQty());
                batch.setBatchCode(backDetail.getBatchCode());
                batch.setInventoryType(backDetail.getInventoryType());
                batches.add(batch);
                backDetail.setBatchs(batches);
                backDetailList.add(backDetail);
            }
        }
        back.setStatus(flag?"PARTFULFILLED":"FULFILLED");
        back.setOrderLines(backDetailList);
        RuoYuChenResponse response=ruoYuChenSupport.request(back);
        log.setReqMsg(new JSONObject(back).toString());
        log.setResMsg(new JSONObject(response).toString());
        log.setSuccess(response.getStatus()==0?"1":"0");
        log.setMsg(response.getMsg()==null?"":response.getMsg());
    }

    private void tallyConfirm(RuoYuChenTallyOrderConfirm tallyOrderConfirm) {
        if (tallyOrderConfirm==null)
            throw new BadRequestException("请求体body无任何内容或不是个有效的对象");
        InboundOrder inboundOrder=inboundOrderService.queryByOutNo(tallyOrderConfirm.getEntryOrderCode());
        if (inboundOrder==null)
            throw new BadRequestException("入库单据"+tallyOrderConfirm.getEntryOrderCode()+"不存在");
        if (inboundOrder.getStatus()!=646)
            throw new BadRequestException("入库单据不是理货待审核状态");
        if (StringUtils.equals(tallyOrderConfirm.getStatus(),"FULFILLED")){
            inboundOrder.setStatus(645);//理货通过，状态改为理货完成回传
            inboundOrder.setTallyEndBackTime(new Timestamp(System.currentTimeMillis()));
        }else {
            inboundOrder.setStatus(647);//理货驳回
        }
        inboundOrderService.update(inboundOrder);
        inboundOrderLogService.create(
                new InboundOrderLog(
                        inboundOrder.getId(),
                        inboundOrder.getOrderNo(),
                        inboundOrder.getStatus()+"",
                        new JSONObject(tallyOrderConfirm).toString(),
                        null,
                        "1",
                        "成功",
                        "RuoYuChen"
                )
        );
    }

    private void orderCancel(RuoYuChenOrderCancel orderCancel) {
        if (orderCancel==null)
            throw new BadRequestException("请求体body无任何内容或不是个有效的对象");
        switch (orderCancel.getOrderType()){
            case "JYCK"://交易出库
            case "HHCK"://换货出库
            case "BFCK"://补发出库
            case "PTCK"://普通出库
            case "DBCK"://调拨出库
            case "QTCK"://其他出库
            case "B2BCK"://B2B出库
            case "LYCK"://领用出库
            case "PKCK"://盘亏出库
            case "CGTH"://采购退货
                outBoundOrderCancel(orderCancel);
                break;
            case "B2BRK"://B2B入库
            case "CGRK"://采购入库
            case "DBRK"://调拨入库
            case "QTRK"://其它入库
            case "THRK"://退货入库
            case "HHRK"://换货入库
            case "LYRK"://领用入库
            case "PYRK"://盘盈入库
                inboundOrderCancel(orderCancel);
                break;
            case "CNJG"://仓内加工，组装单的
                break;
            case "CKPD"://库存盘点，库存盘点单的
                break;
        }
    }

    private void inboundOrderCancel(RuoYuChenOrderCancel orderCancel) {
        InboundOrder inboundOrder =inboundOrderService.queryByOutNo(orderCancel.getOrderCode());
        if (inboundOrder==null)
            throw new BadRequestException("单据不存在");
        if (inboundOrder.getStatus()==650||inboundOrder.getStatus()==655)
            throw new BadRequestException("单据已完成，无法取消");
        if (StringUtils.isNotBlank(inboundOrder.getWmsNo())){
            DocAsnHeader header= wmsSupport.getAsnOrder(inboundOrder.getWmsNo());
            if (header!=null&&!StringUtils.equals(header.getAsnstatus(),"90")){
                //富勒不是取消状态
                throw new BadRequestException("下游单据未取消，请联系相关业务人员取消下游单据");
            }
        }
        inboundOrder.setStatus(888);
        inboundOrder.setCancelTime(new Timestamp(System.currentTimeMillis()));
        inboundOrderService.update(inboundOrder);
        inboundOrderLogService.create(
                new InboundOrderLog(
                        inboundOrder.getId(),
                        inboundOrder.getOrderNo(),
                        inboundOrder.getStatus()+"",
                        new JSONObject(orderCancel).toString(),
                        null,
                        "1",
                        "成功 "+orderCancel.getCancelReason(),
                        "RuoYuChen"
                )
        );
    }

    /**
     * 出库单取消
     * @param orderCancel
     */
    private void outBoundOrderCancel(RuoYuChenOrderCancel orderCancel) {
        OutboundOrder outboundOrder=outboundOrderService.queryByOutNo(orderCancel.getOrderCode());
        if (outboundOrder==null)
            throw new BadRequestException("单据不存在");
        if (outboundOrder.getStatus()==750||outboundOrder.getStatus()==755)
            throw new BadRequestException("单据已完成，无法取消");
        if (StringUtils.isNotBlank(outboundOrder.getWmsNo())){
            DocOrderHeader header= wmsSupport.getDocOrderHead(outboundOrder.getWmsNo());
            if (header!=null&&!StringUtils.equals(header.getSostatus(),"90")){
                //富勒不是取消状态
                throw new BadRequestException("下游单据未取消，请联系相关业务人员取消下游单据");
            }
        }
        outboundOrder.setStatus(888);
        outboundOrderService.update(outboundOrder);
        outboundOrderLogService.create(
                new OutboundOrderLog(
                        outboundOrder.getId(),
                        outboundOrder.getOrderNo(),
                        outboundOrder.getStatus()+"",
                        new JSONObject(orderCancel).toString(),
                        null,
                        "1",
                        "成功 "+orderCancel.getCancelReason(),
                        "RuoYuChen"
                )
        );
    }

    private void outboundOrderPush(RuoYuChenOutBoundOrder ruoYuChenOutBoundOrder) {
        if (ruoYuChenOutBoundOrder.getDeliveryOrderCode()==null)
            throw new BadRequestException("出库单号为空");
        OutboundOrder outboundOrder = outboundOrderService.queryByOutNo(ruoYuChenOutBoundOrder.getDeliveryOrderCode());
        if (outboundOrder!=null)
            throw new BadRequestException("出库单号已存在");
        outboundOrder=new OutboundOrder();
        outboundOrder.setOutNo(ruoYuChenOutBoundOrder.getDeliveryOrderCode());
        outboundOrder.setDefault01(ruoYuChenOutBoundOrder.getOrderType());
        outboundOrder.setExpectDeliverTime(new Timestamp(System.currentTimeMillis()+128*3600*1000));
        switch (ruoYuChenOutBoundOrder.getOrderType()){
            case "DBCK"://调拨出库
                outboundOrder.setOrderType("0");
                break;
            case "PTCK"://普通出库
            case "B2BCK"://B2B出库
            case "QTCK"://其他出库
            case "CGTH"://采购退货出库单
            case "XNCK"://虚拟出库单
            case "LYCK"://领用出库
            case "JITCK"://唯品出库
            case "PKCK"://盘亏出库
                outboundOrder.setOrderType("11");//其他出库
                break;
        }
        if (CollectionUtil.isEmpty(ruoYuChenOutBoundOrder.getOrderLines()))
            throw new BadRequestException("出库单明细为空");
        List<OutboundOrderDetails>details=new ArrayList<>();
        long shopId=-1;
        long custId=-1;
        for (RuoYuChenOutBoundOrderDetail orderLine : ruoYuChenOutBoundOrder.getOrderLines()) {
            BaseSku baseSku=baseSkuService.queryByCode(orderLine.getItemCode());
            if (baseSku==null)
                throw new BadRequestException("商品编码:\""+orderLine.getItemCode()+"\"不存在");
            OutboundOrderDetails detail=new OutboundOrderDetails();
            detail.setGoodsNo(baseSku.getGoodsNo());
            detail.setGoodsCode(orderLine.getItemCode());
            if (!StringUtils.isNumeric(orderLine.getPlanQty()))
                throw new BadRequestException("入库单明细-计划数量:"+orderLine.getPlanQty()+" 不是一个有效的数字");
            detail.setExpectNum(Integer.valueOf(orderLine.getPlanQty()));
            detail.setGoodsLineNo(orderLine.getOrderLineNo());
            detail.setDefault01(orderLine.getInventoryType());
            detail.setDefault02(orderLine.getBatchCode());
            details.add(detail);
            shopId=baseSku.getShopId();
            custId=baseSku.getCustomersId();
        }
        outboundOrder.setDetails(details);
        outboundOrder.setIsOnline("1");
        outboundOrder.setOnlineSrc("RuoYuChen");
        outboundOrder.setShopId(shopId);
        outboundOrder.setCustomersId(custId);
        outboundOrder.setCreateBy("RuoYuChen");
        outboundOrderService.create(outboundOrder);
    }

    private void inboundOrderPush(RuoYuChenInBoundOrder ruoYuChenInBoundOrder) {
        if (ruoYuChenInBoundOrder.getEntryOrderCode()==null)
            throw new BadRequestException("入库单号必填");
        InboundOrder inboundOrder=inboundOrderService.queryByOutNo(ruoYuChenInBoundOrder.getEntryOrderCode());
        if (inboundOrder!=null)
            throw new BadRequestException("入库单已存在");
        inboundOrder=new InboundOrder();
        inboundOrder.setOutNo(ruoYuChenInBoundOrder.getEntryOrderCode());
        inboundOrder.setExpectArriveTime(new Timestamp(System.currentTimeMillis()+128*3600*1000));
        switch (ruoYuChenInBoundOrder.getOrderType()){
            case "DBRK":
                //调拨入库
                inboundOrder.setOrderType("1");
                break;
            case "CGRK":
                //采购入库
                inboundOrder.setOrderType("0");
                inboundOrder.setDeclareNo(ruoYuChenInBoundOrder.getPurchaseOrderCode());
                break;
            case "CCRK":
                //残次入库
                inboundOrder.setOrderType("2");//销退入库
            case "SCRK"://生产入库
            case "LYRK"://领用入库
            case "QTRK"://其它入库
            case "B2BRK"://B2B入库
            case "XNRK"://虚拟入库
            case "PYRK"://盘盈入库
                break;
            default:
                throw new BadRequestException("不支持的业务");
        }
        if (CollectionUtil.isEmpty(ruoYuChenInBoundOrder.getOrderLines()))
            throw new BadRequestException("入库单明细为空");
        List<InboundOrderDetails>details=new ArrayList<>();
        long shopId=-1;
        long custId=-1;
        inboundOrder.setDefault01(ruoYuChenInBoundOrder.getOrderType());
        for (RuoYuChenInBoundOrderDetail orderLine : ruoYuChenInBoundOrder.getOrderLines()) {
            BaseSku baseSku=baseSkuService.queryByCode(orderLine.getItemCode());
            if (baseSku==null)
                throw new BadRequestException("商品编码\""+orderLine.getItemCode()+"\"不存在");
            if (baseSku.getGoodsNo()==null)
                throw new BadRequestException("商品编码\""+orderLine.getItemCode()+"\"未完成备案");
            InboundOrderDetails detail=new InboundOrderDetails();
            detail.setGoodsLineNo(orderLine.getOrderLineNo());
            detail.setGoodsNo(baseSku.getGoodsNo());
            if (!StringUtils.isNumeric(orderLine.getPlanQty()))
                throw new BadRequestException("入库单明细-计划数量:"+orderLine.getPlanQty()+" 不是一个有效的数字");
            detail.setExpectNum(Integer.valueOf(orderLine.getPlanQty()));
            detail.setGoodsCode(orderLine.getItemCode());
            detail.setDefault01(orderLine.getInventoryType());
            detail.setBarCode(baseSku.getBarCode());
            details.add(detail);
            shopId=baseSku.getShopId();
            custId=baseSku.getCustomersId();
        }
        inboundOrder.setDetails(details);
        inboundOrder.setIsOnline("1");
        inboundOrder.setOnlineSrc("RuoYuChen");
        inboundOrder.setShopId(shopId);
        inboundOrder.setCustomersId(custId);
        inboundOrder.setCreateBy("RuoYuChen");
        inboundOrderService.create(inboundOrder);
    }

    private void baseSkuSync(RuoYuChenBaseSku ruoYuChenBaseSku,String warehouseCode) {
        if (StringUtils.isBlank(ruoYuChenBaseSku.getItemCode()))
            throw new BadRequestException("商品编码 必填");
        ShopInfo shopInfo=shopInfoService.queryByKjgCode(warehouseCode);
        if (shopInfo==null)
            throw new BadRequestException("根据货主编码找不到店铺信息");
        BaseSku baseSku = baseSkuService.queryByBarCodeAndShopId(ruoYuChenBaseSku.getBarCode(),shopInfo.getId());
        if (baseSku == null) {
            baseSku = new BaseSku();
            baseSku.setGoodsCode(ruoYuChenBaseSku.getItemCode());
            baseSku.setStatus(100);
            baseSku.setCreateTime(new Timestamp(System.currentTimeMillis()));
            baseSku.setCreateBy("ruoYuChen");
            baseSku.setShopId(shopInfo.getId());
            baseSku.setCustomersId(shopInfo.getCustId());
            baseSku.setGoodsName(ruoYuChenBaseSku.getItemName());
            baseSku.setGoodsNameC(ruoYuChenBaseSku.getItemName());
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getBarCode()))
                //条码
                baseSku.setBarCode(ruoYuChenBaseSku.getBarCode());
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getStockUnit()))
                //单位
                baseSku.setUnit(ruoYuChenBaseSku.getStockUnit());//申报单位
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getLength())) {
                //长
                baseSku.setSaleL(new BigDecimal(ruoYuChenBaseSku.getLength()));
                baseSku.setPackL(baseSku.getSaleL());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getWidth())) {
                //宽
                baseSku.setSaleW(new BigDecimal(ruoYuChenBaseSku.getWidth()));
                baseSku.setPackW(baseSku.getSaleW());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getHeight())) {
                //高
                baseSku.setSaleH(new BigDecimal(ruoYuChenBaseSku.getHeight()));
                baseSku.setPackH(baseSku.getSaleH());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getVolume())) {
                //体积
                baseSku.setSaleVolume(new BigDecimal(ruoYuChenBaseSku.getVolume()));
                baseSku.setPackVolume(baseSku.getSaleVolume());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getGrossWeight()))
                //毛重
                baseSku.setGrossWeight(new BigDecimal(ruoYuChenBaseSku.getGrossWeight()));
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getNetWeight()))
                //净重
                baseSku.setNetWeight(new BigDecimal(ruoYuChenBaseSku.getNetWeight()));
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getCategoryName()))
                //商品分类名
                baseSku.setGuse(ruoYuChenBaseSku.getCategoryName());//用途
            //if (StringUtils.isNotBlank(ruoYuChenBaseSku.getItemType()))
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getBrandName()))
                //品牌编码
                baseSku.setBrand(ruoYuChenBaseSku.getBrandName());
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getIsShelfLifeMgmt())&&StringUtils.equals(ruoYuChenBaseSku.getIsShelfLifeMgmt(),"Y")){
                //是保质期管理
                if (StringUtils.isNotBlank(ruoYuChenBaseSku.getShelfLife()))
                    //保质期 小时
                    baseSku.setLifecycle(Integer.parseInt(ruoYuChenBaseSku.getShelfLife())/24);//保质期 天
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getIsBatchMgmt())){
                //是否批次管理
                baseSku.setSnControl(StringUtils.equals("Y",ruoYuChenBaseSku.getIsBatchMgmt())?"1":"0");
            }
            if (StringUtil.isNotBlank(ruoYuChenBaseSku.getStockUnit()))
                baseSku.setStockUnit(ruoYuChenBaseSku.getStockUnit());
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getPcs())){
                //箱规
                if (!ruoYuChenBaseSku.getPcs().matches("^\\d{1,10}$"))
                    throw new BadRequestException("箱规的值不是个有效的数字");
                baseSku.setPackNum(Integer.parseInt(ruoYuChenBaseSku.getPcs()));
            }
            baseSku.setUpdatedBy("ruoYuChen");
            baseSku.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            baseSkuService.create(baseSku);
        }else {
            baseSku.setGoodsCode(ruoYuChenBaseSku.getItemCode());
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getWidth())) {
                //宽
                baseSku.setSaleW(new BigDecimal(ruoYuChenBaseSku.getWidth()));
                baseSku.setPackW(baseSku.getSaleW());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getHeight())) {
                //高
                baseSku.setSaleH(new BigDecimal(ruoYuChenBaseSku.getHeight()));
                baseSku.setPackH(baseSku.getSaleH());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getVolume())) {
                //体积
                baseSku.setSaleVolume(new BigDecimal(ruoYuChenBaseSku.getVolume()));
                baseSku.setPackVolume(baseSku.getSaleVolume());
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getIsShelfLifeMgmt())&&StringUtils.equals(ruoYuChenBaseSku.getIsShelfLifeMgmt(),"Y")){
                //是保质期管理
                if (StringUtils.isNotBlank(ruoYuChenBaseSku.getShelfLife()))
                    //保质期 小时
                    baseSku.setLifecycle(Integer.parseInt(ruoYuChenBaseSku.getShelfLife())/24);//保质期 天
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getIsBatchMgmt())){
                //是否批次管理
                baseSku.setSnControl(StringUtils.equals("Y",ruoYuChenBaseSku.getIsBatchMgmt())?"1":"0");
            }
            if (StringUtils.isNotBlank(ruoYuChenBaseSku.getPcs())){
                //箱规
                if (!ruoYuChenBaseSku.getPcs().matches("^\\d{1,10}$"))
                    throw new BadRequestException("箱规的值不是个有效的数字");
                baseSku.setPackNum(Integer.parseInt(ruoYuChenBaseSku.getPcs()));
            }
            baseSkuService.update(baseSku);
        }
    }
}
